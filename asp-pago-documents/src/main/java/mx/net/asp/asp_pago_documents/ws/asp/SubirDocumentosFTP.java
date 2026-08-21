package mx.net.asp.asp_pago_documents.ws.asp;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.google.gson.Gson;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.ArchivoDAO;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.model.AttachmentOBJ;
import mx.net.asp.asp_pago_documents.model.CustomMultipartFile;
import mx.net.asp.asp_pago_documents.model.ExpedienteOBJ;
import mx.net.asp.asp_pago_documents.model.HeaderWS;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import mx.net.asp.asp_pago_documents.ws.asp.request.ExpedienteReq;

@Service
@Log4j2
@RequiredArgsConstructor
public class SubirDocumentosFTP {

    @Value("${sftp.files.host}")
    private String SFTP_HOST;
    @Value("${sftp.files.port}")
    private Integer SFTP_PORT;
    @Value("${sftp.files.user}")
    private String SFTP_USER;
    @Value("${sftp.files.pass}")
    private String SFTP_PASS;
    @Value("${session.connect.timeout}")
    private Integer sessionConnectTimeout;
    @Value("${session.timeout}")
    private Integer sessionTimeout;
    @Value("${channel.connect.timeout}")
    private Integer channelConnectTimeout;

    private final ArchivoDAO archivoDAO;

    private final Gson gson;
    private final ErrorHandler errorHandler;

    @Async
    public CompletableFuture<RespuestaDTO> subirDocumentosFTP(String cuenta, String solicitanteId, List<AttachmentOBJ> attachmentsList, List<Map<String, Object>> archivosNombres, HeaderWS header) {
        RespuestaDTO respuesta = new RespuestaDTO();
        log.info("================================================================================================================================");
        log.info("Iniciando carga de documentos vía SFTP...");

        ExpedienteOBJ ex = null;
        String[] expediente = new String[archivosNombres.size()];
        MultipartFile[] archivosMultipart = new MultipartFile[attachmentsList.size()];
        int ii = 0;

        for (AttachmentOBJ adjunto : attachmentsList) {

            byte[] archivo = adjunto.getTheAttachmentBytes();

            ex = new ExpedienteOBJ();
            MultipartFile customMultipartFile = new CustomMultipartFile(archivo, String.valueOf(archivosNombres.get(ii).get("nombre")));
            archivosMultipart[ii] = customMultipartFile;
            ex.setExtension(String.valueOf(archivosNombres.get(ii).get("nombre")).split("[.]")[1]);
            ex.setNombre(String.valueOf(archivosNombres.get(ii).get("nombre")).split("[.]")[0]);
            ex.setIdentificador_documento((int) archivosNombres.get(ii).get("id"));
            ex.setDocumento("");
            expediente[ii] = gson.toJson(ex);
            log.info("expediente[i]:" + expediente[ii]);
            ii++;
        }

        String modulo = "AHORRO";
        String ruta = solicitanteId + "/" + modulo + "/" + cuenta;

        log.info("CUENTA:" + cuenta);
        log.info("SOLICITANTE ID:" + solicitanteId);
        log.info("RUTA: " + ruta);
        boolean urlCorrecta = ruta.matches("^[0-9]+[\\//][A-Za-z]+[\\//][0-9]+$");

        if (!urlCorrecta) {
            log.error("Formato de ruta incorrecto: {}", ruta);
            log.info("FINALIZA CARGA DE ARCHIVOS");
            log.info("================================================================================================================================");
            respuesta.setCodigo(500);
            respuesta.setMensaje("Formato de ruta incorrecto");
            return CompletableFuture.completedFuture(respuesta);
        }

        String[] directorios = ruta.split("[/]");
        if (!directorios[1].equals(modulo)) {
            log.error("Inconsistencia entre la ruta y el módulo enviado");
            respuesta.setCodigo(501);
            respuesta.setMensaje("Inconsistencia entre la ruta y el módulo");
            return CompletableFuture.completedFuture(respuesta);
        }

        Session session = null;
        Channel channel = null;
        ChannelSftp channelSftp = null;

        ExpedienteReq obj = new ExpedienteReq();
        obj.setCuenta(cuenta);
        obj.setRuta(ruta);
        obj.setUsuario_creacion("9");
        obj.setModulo(modulo);

        try {
            // Conectar al servidor SFTP
            JSch jsch = new JSch();
            session = jsch.getSession(SFTP_USER, SFTP_HOST, SFTP_PORT);
            session.setPassword(SFTP_PASS);

            java.util.Properties config = new java.util.Properties();
            config.put("StrictHostKeyChecking", "no");
            session.setConfig(config);

            session.connect(sessionConnectTimeout);
            session.setTimeout(sessionTimeout);
            log.info("-> Host conectado.");

            channel = session.openChannel("sftp");
            channel.connect(channelConnectTimeout);
            log.info("-> Canal SFTP abierto y conectado.");

            channelSftp = (ChannelSftp) channel;
            log.info("Directorio actual: " + channelSftp.pwd());

            channelSftp.cd("expedientes");
            log.info("-> Cambiado a directorio expedientes: " + channelSftp.pwd());

            // Verificar y/o crear estructura de carpetas
            for (String dir : directorios) {
                try {
                    channelSftp.stat(dir); // Verifica si existe
                } catch (SftpException e) {
                    // Si no existe, lo crea
                    if (e.id == ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                        channelSftp.mkdir(dir);
                        log.info("Directorio creado: " + dir);
                    } else {
                        throw e; // Otro error desconocido
                    }
                }
                channelSftp.cd(dir); // Entra al directorio
                log.info("Carpeta actual: " + channelSftp.pwd());
            }

            // Lógica para subir archivos
            for (int i = 0; i < archivosMultipart.length; i++) {
                ExpedienteOBJ expedienteObj = gson.fromJson(expediente[i], ExpedienteOBJ.class);
                String[] nombrePartes = expedienteObj.getNombre().split("[.]");
                expedienteObj.setNombre(nombrePartes[0]);

                String nombreFinal = expedienteObj.getNombre() + "." + expedienteObj.getExtension();

                try {
                    // ¿Existe ya el archivo?
                    String archivo = channelSftp.ls(nombreFinal).toString();

                    if (archivo.contains(nombreFinal)) {
                        // Ya existe: renombrar y marcar como versión 2
                        log.info("Archivo ya existe, se sube como versión 2");

                        expedienteObj.setNombre(expedienteObj.getNombre() + " (2)");
                        nombreFinal = expedienteObj.getNombre() + "." + expedienteObj.getExtension();
                    }
                } catch (SftpException exx) {
                    if (exx.id != ChannelSftp.SSH_FX_NO_SUCH_FILE) {
                        log.error("Error inesperado al verificar existencia del archivo: " + exx.getMessage(), exx);
                        continue; // Pasa al siguiente archivo
                    }
                    // Si no existe, seguimos con nombre original
                }

                // Registrar en BD
                boolean seRegistraronTodosArch = false;
                try {
                    obj.setExpediente(expedienteObj);
                    seRegistraronTodosArch = archivoDAO.insertarInfoArchivo(obj);
                } catch (Exception exc) {
                    log.error("Error al registrar archivo en BD: " + exc.getMessage(), exc);
                    continue;
                }

                // Subir archivo al servidor
                if (seRegistraronTodosArch) {
                    try {
                        byte[] mydata = archivosMultipart[i].getBytes();
                        InputStream stream = new ByteArrayInputStream(mydata);
                        channelSftp.put(stream, nombreFinal);
                        log.info("Archivo transferido con exito: " + nombreFinal);
                        RespuestaUtils.respuestaExito(respuesta, 0, "Archivo transferido con éxito");
                    } catch (Exception exc) {
                        log.error("Error al subir archivo: " + exc.getMessage(), exc);
                        RespuestaUtils.asignarError(respuesta,
                                ErroresGenerales.ERROR_INTERNO,
                                ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
                    }
                } else {
                    log.info("No se guardó en BD: " + nombreFinal);
                }
            }

        } catch (Exception e) {
            log.error("Error general en el proceso de carga de archivos:");
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        } finally {
            try {
                log.info("Entrando a finally...");

                try {
                    if (channelSftp != null && channelSftp.isConnected()) {
                        channelSftp.disconnect();
                    }
                } catch (Exception e) {
                    log.warn("Error cerrando channelSftp", e);
                }

                try {
                    if (channel != null && channel.isConnected()) {
                        channel.disconnect();
                    }
                } catch (Exception e) {
                    log.warn("Error cerrando channel", e);
                }

                try {
                    if (session != null && session.isConnected()) {
                        session.disconnect();
                    }
                } catch (Exception e) {
                    log.warn("Error cerrando session", e);
                }
                log.info("Conexión SFTP cerrada correctamente");
            } catch (Exception exx) {
                log.warn("Error al cerrar la conexión SFTP: " + exx.getMessage(), exx);
                RespuestaUtils.asignarError(respuesta,
                        ErroresGenerales.ERROR_INTERNO,
                        ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
            }
        }
        return CompletableFuture.completedFuture(respuesta);
    }
}

