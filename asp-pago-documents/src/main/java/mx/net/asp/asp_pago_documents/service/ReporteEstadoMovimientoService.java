package mx.net.asp.asp_pago_documents.service;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import lombok.Getter;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresEstadoMov;
import mx.net.asp.asp_pago_documents.utilerias.errores.ErroresGenerales;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import io.micrometer.common.util.StringUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.dao.AhorroDAO;
import mx.net.asp.asp_pago_documents.dao.CoDiDAO;
import mx.net.asp.asp_pago_documents.dao.NucleoCentralDAO;
import mx.net.asp.asp_pago_documents.dao.ProcreaDAO;
import mx.net.asp.asp_pago_documents.dao.PublicCeroDAO;
import mx.net.asp.asp_pago_documents.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_documents.dto.RespuestaDTO;
import mx.net.asp.asp_pago_documents.request.ReporteEstadoMovimientoReq;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_documents.utilerias.ListTipoMovimiento;
import mx.net.asp.asp_pago_documents.ws.asp.WSRteConcentradoMovimientos;

import java.lang.reflect.Type;

@Service
@Log4j2
@RequiredArgsConstructor
public class ReporteEstadoMovimientoService {

    private final CoDiDAO codiDAO;
    private final NucleoCentralDAO nucleoCentralDAO;
    private final SolicitanteDAO solicitanteDAO;
    private final PublicCeroDAO publicCeroDAO;
    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;
    private final GeneraReportesService generaReportesService;
    private final ErrorHandler errorHandler;
    private final WSRteConcentradoMovimientos wsRteConcentradoMovimientos;
    private final Gson gson;
    private final Environment env;

    @Value("${version.app}")
    private String versionApp;

    private static final List<String> movimientosCoDiSpei = ListTipoMovimiento.getMovimientosCoDiSpei();
    private static final List<String> movimientosPagoServicios = ListTipoMovimiento.getMovimientosPagoServicios();
    private static final List<String> movimientosDevPagoServicios = ListTipoMovimiento.getMovimientosDevPagoServicios();
    private static final List<String> movimientosTraspasos = ListTipoMovimiento.getMovimientosTraspasos();
    private static final List<String> movimientosCredito = ListTipoMovimiento.getMovimientosCredito();
    private static final List<String> movimientosTarjeta = ListTipoMovimiento.getMovimientosTarjeta();

    @Getter
    private static String formatoFechaPeriodo = "";

    public RespuestaDTO generaReporteEstadoMovimiento(ReporteEstadoMovimientoReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            PosGlobalCuentaOBJ posGlobCuentaObj = nucleoCentralDAO.obtenerCuentaPosicionGlobalByCuentaAh(req.getCuenta());
            if (posGlobCuentaObj == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.POSICION_GLOBAL_NOT_FOUND,
                        ErroresEstadoMov.getMensajeError(ErroresEstadoMov.POSICION_GLOBAL_NOT_FOUND, req.getCuenta()));
            }

            RegistroCoDiOBJ regCoDiOBJ = codiDAO.consultarRegistroCoDiCliente(posGlobCuentaObj.getPersonaId());
            respuesta = validarRegistroCoDi(regCoDiOBJ);
            if (respuesta.getCodigo() != 0) {
                return respuesta;
            }

            SolicitanteOBJ solicitanteOBJ = solicitanteDAO.obtenerInfoGeneralSolicitanteById(regCoDiOBJ.getSolicitanteId());
            if (solicitanteOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.SOLICITANTE_NOT_FOUND,
                        ErroresEstadoMov.getMensajeError(ErroresEstadoMov.SOLICITANTE_NOT_FOUND, regCoDiOBJ.getSolicitanteId()));
            }

            if (req.getFechaInicial() > req.getFechaFinal()) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.RANGO_FECHAS_NO_VALIDO,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.RANGO_FECHAS_NO_VALIDO));
            }

            // Obtener la fecha en milisegundos
            long fechaInicialEnMilisegundos = req.getFechaInicial() * 1000;
            long fechaFinalEnMilisegundos = req.getFechaFinal() * 1000;

            // Convertir a java.sql.Date correctamente
            java.sql.Date fechaInicialSql = new java.sql.Date(fechaInicialEnMilisegundos);
            java.sql.Date fechaFinalSql = new java.sql.Date(fechaFinalEnMilisegundos);

            TransaccionCuentaOBJ transaccionCuentaOBJ = new TransaccionCuentaOBJ();
            transaccionCuentaOBJ.setCuenta(req.getCuenta());
            transaccionCuentaOBJ.setFechaInicio(fechaInicialSql);
            transaccionCuentaOBJ.setFechaFin(fechaFinalSql);

            respuesta = wsRteConcentradoMovimientos.obtenerEdoMovimientosFromAdminPlasticos(transaccionCuentaOBJ);
            if (respuesta.getCodigo() == 0) {

                Type listMovimientos = new TypeToken<List<ReporteMovimientosAspPagoOBJ>>() {
                }.getType();

                List<ReporteMovimientosAspPagoOBJ> movimientosAspPago = gson.fromJson(respuesta.getData(), listMovimientos);
                if (movimientosAspPago != null && !movimientosAspPago.isEmpty()) {

                    EdoMovtosOBJ edoMovtosOBJ = new EdoMovtosOBJ();
                    edoMovtosOBJ.setListaReportesEdoMovtos(movimientosAspPago);
                    edoMovtosOBJ.setNombre_cliente(solicitanteOBJ.getNombre());
                    edoMovtosOBJ.setNumero_cuenta(transaccionCuentaOBJ.getCuenta());

                    edoMovtosOBJ.setFecha_inicio(_FechaPeriodo(transaccionCuentaOBJ.getFechaInicio()));
                    edoMovtosOBJ.setFecha_final(_FechaPeriodo(transaccionCuentaOBJ.getFechaFin()));
                    edoMovtosOBJ.setPeriodo(_FechaPeriodo(transaccionCuentaOBJ.getFechaInicio()) + " al " + _FechaPeriodo(transaccionCuentaOBJ.getFechaFin()));
                    edoMovtosOBJ.setMailTo(solicitanteOBJ.getCorreo());
                    edoMovtosOBJ.setMailFrom("asp_pago@aspintegraopciones.com");
                    edoMovtosOBJ.setSubject("Estado de movimientos solicitado");

                    respuesta = llenarDatosReporteEstadoMovimiento(transaccionCuentaOBJ, posGlobCuentaObj.getTipoCuenta(), solicitanteOBJ);
                    if (respuesta.getCodigo() == 0) {
                        DatosEstadoMovimientoOBJ datosEstadoMovimientoObj = gson.fromJson(respuesta.getData(), DatosEstadoMovimientoOBJ.class);
                        respuesta = generaListaMovimientos(edoMovtosOBJ);
                        if (respuesta.getCodigo() == 0) {
                            Type listType = new TypeToken<List<MovimientoOBJ>>() {
                            }.getType();
                            List<MovimientoOBJ> movs = gson.fromJson(respuesta.getData(), listType);

                            AttachmentOBJ attachmentsObj = generaDocumentacionReporteEstadoMovimientos(datosEstadoMovimientoObj, movs);
                            if (attachmentsObj != null) {
                                RespuestaUtils.respuestaExito(respuesta, gson.toJson(attachmentsObj));
                            } else {
                                return RespuestaUtils.asignarError(respuesta,
                                        ErroresEstadoMov.GENERA_DOC,
                                        ErroresEstadoMov.descError.get(ErroresEstadoMov.GENERA_DOC));
                            }
                        }
                    } else {
                        return RespuestaUtils.asignarError(respuesta,
                                ErroresEstadoMov.DATOS_DOC,
                                ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
                    }
                } else {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresEstadoMov.MOVIMIENTOS_NOT_FOUND,
                            ErroresEstadoMov.descError.get(ErroresEstadoMov.MOVIMIENTOS_NOT_FOUND));
                }
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private RespuestaDTO validarRegistroCoDi(RegistroCoDiOBJ regCoDiOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            if (regCoDiOBJ == null) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.REGISTRO_CODI_NOT_FOUND,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.REGISTRO_CODI_NOT_FOUND));
            }

            //Validamos que la cuente este presente en la nueva versión de la app
            if (!versionApp.equals(regCoDiOBJ.getRegistroNuevaApp())) {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.VERSION_APP,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.VERSION_APP));
            }

            if (regCoDiOBJ.getBloqueado() == 1) {
                log.info("*** Registro Bloqueado");
                return RespuestaUtils.asignarError(new RespuestaDTO(),
                        ErroresEstadoMov.REG_CODI_BLOQUEADO,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.REG_CODI_BLOQUEADO));
            } else if (regCoDiOBJ.getBloqueosTemporales() > 0) {
                log.info("*** Registro CoDi Bloqueado temporalmente");
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.REG_CODI_BLOQUEADO_TEMP,
                        ErroresEstadoMov.getMensajeError(ErroresEstadoMov.REG_CODI_BLOQUEADO_TEMP, regCoDiOBJ.getCuenta()));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return RespuestaUtils.respuestaExito(respuesta);
    }

    public RespuestaDTO llenarDatosReporteEstadoMovimiento(TransaccionCuentaOBJ transaccionCuentaOBJ, String tipoCuenta, SolicitanteOBJ solicitanteOBJ) {
        RespuestaDTO respuesta = new RespuestaDTO();
        LocalDate fechaActual = LocalDate.now();
        DatosEstadoMovimientoOBJ datosEstadoMovimientoObj = new DatosEstadoMovimientoOBJ();

        try {
            List<ConfiguracionEstadoMovimientoOBJ> configuracionEstadoMovimientoList = publicCeroDAO.obtenerConfiguracionReporteEdoMov();
            if (configuracionEstadoMovimientoList != null && !configuracionEstadoMovimientoList.isEmpty()) {
                String cadena = "";
                for (ConfiguracionEstadoMovimientoOBJ row : configuracionEstadoMovimientoList) {
                    if (row.getTitulo().isEmpty())
                        cadena += row.getValor() + "\n";
                    else
                        cadena += row.getTitulo() + " " + row.getValor() + "\n";
                }
                datosEstadoMovimientoObj.setTitulos(cadena);
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.DATOS_DOC,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
            }

            if (tipoCuenta.equals("AHORRO")) {
                InfoCuentaProcreaOBJ infoCuentaProcreaOBJ = procreaDAO.obtenerDatosCuentaReporteEdoMovPROCREA(transaccionCuentaOBJ.getCuenta());
                if (infoCuentaProcreaOBJ == null) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresEstadoMov.DATOS_DOC,
                            ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
                }
                datosEstadoMovimientoObj.setClabe(infoCuentaProcreaOBJ.getCuentaClabe());
                datosEstadoMovimientoObj.setTipoCuenta(infoCuentaProcreaOBJ.getDescripcion());
            } else {
                InfoCuentaCeroOBJ infoCuentaCeroOBJs = ahorroDAO.obtenerDatosCuentaReporteEdoMovCERO(transaccionCuentaOBJ.getCuenta());
                if (infoCuentaCeroOBJs == null) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresEstadoMov.DATOS_DOC,
                            ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
                }
                datosEstadoMovimientoObj.setClabe(infoCuentaCeroOBJs.getCuentaClabe());
                datosEstadoMovimientoObj.setTipoCuenta(infoCuentaCeroOBJs.getDescripcion());
            }

            respuesta = obtenerDatosDomicilio(solicitanteOBJ.getNumero());
            if (respuesta.getCodigo() == 0) {

                InfoDomicilioOBJ infoDomicilio = gson.fromJson(respuesta.getData(), InfoDomicilioOBJ.class);
                if (infoDomicilio != null) {
                    datosEstadoMovimientoObj.setColonia(infoDomicilio.getColonia());
                    datosEstadoMovimientoObj.setCiudad(infoDomicilio.getLocalidad());
                }

            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.DATOS_DOC,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
            }

            datosEstadoMovimientoObj.setCuenta(transaccionCuentaOBJ.getCuenta());
            datosEstadoMovimientoObj.setRfc(solicitanteOBJ.getRfc());
            datosEstadoMovimientoObj.setNombre(solicitanteOBJ.getNombre());
            datosEstadoMovimientoObj.setCalle(solicitanteOBJ.getDescripcionDomicilio1());

            String fechaIni = String.valueOf(transaccionCuentaOBJ.getFechaInicio());
            String fechaFin = String.valueOf(transaccionCuentaOBJ.getFechaFin());

            LocalDate fechaIniDate = LocalDate.parse(fechaIni);
            LocalDate fechaFinDate = LocalDate.parse(fechaFin);

            datosEstadoMovimientoObj.setFechaInicio(fechaIniDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            datosEstadoMovimientoObj.setFechaFin(fechaFinDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));
            datosEstadoMovimientoObj.setFechaEmision(fechaActual.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(datosEstadoMovimientoObj));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public RespuestaDTO obtenerDatosDomicilio(String solicitante) {
        RespuestaDTO respuesta = new RespuestaDTO();
        try {
            respuesta = procreaDAO.obtenerConceptoColonia(solicitante);
            if (respuesta.getCodigo() == 0) {
                String idColonia = respuesta.getData();

                InfoDomicilioOBJ infoDomicilio = publicCeroDAO.obtenerInformacionDomicilioByColoniaId(Long.parseLong(idColonia.trim()));
                if (infoDomicilio == null) {
                    return RespuestaUtils.asignarError(respuesta,
                            ErroresEstadoMov.DATOS_DOC,
                            ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
                }
                RespuestaUtils.respuestaExito(respuesta, gson.toJson(infoDomicilio));
            } else {
                return RespuestaUtils.asignarError(respuesta,
                        ErroresEstadoMov.DATOS_DOC,
                        ErroresEstadoMov.descError.get(ErroresEstadoMov.DATOS_DOC));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    public AttachmentOBJ generaDocumentacionReporteEstadoMovimientos(DatosEstadoMovimientoOBJ datosEstadoMovimientoObj, List<MovimientoOBJ> movimientoOBJ) {
        AttachmentOBJ attachment;
        byte[] pdfBytes;
        try {
            pdfBytes = generaReportesService.generaEstadoMovimientos(datosEstadoMovimientoObj, movimientoOBJ);
            attachment = new AttachmentOBJ();
            attachment.setTheAttachmentBytes(pdfBytes);
            attachment.setTheFilename("EstadoMovimientos" + ".pdf");
            attachment.setTheContentType(MediaType.APPLICATION_PDF_VALUE);
            attachment.setSend(true);

        } catch (Exception e) {
            errorHandler.handleException(e);
            attachment = null;
        }
        return attachment;
    }

    public RespuestaDTO generaListaMovimientos(EdoMovtosOBJ edoMovtosOBJ) {

        RespuestaDTO respuesta = new RespuestaDTO();
        List<MovimientoOBJ> movs = new ArrayList<MovimientoOBJ>();

        try {

            for (ReporteMovimientosAspPagoOBJ item : edoMovtosOBJ.getListaReportesEdoMovtos()) {
                //Se declara objeto Movimiento cada que se recorre el arreglo de fechas con sus respectivos movimientos
                MovimientoOBJ mov = new MovimientoOBJ();

                String fechaOriginal = item.getFecha();

                // Formateador para parsear la fecha original
                DateTimeFormatter formatoOriginal = DateTimeFormatter.ofPattern("yyyy-MM-dd");

                // Parsear la fecha original a un objeto LocalDate
                LocalDate fecha = LocalDate.parse(fechaOriginal, formatoOriginal);

                // Formatear la fecha en el nuevo formato "dd de <Nombre del Mes> de yyyy"
                DateTimeFormatter formatoNuevo = DateTimeFormatter.ofPattern("dd 'de' MMMM yyyy", Locale.of("es", "ES"));

                // Convertir la fecha al nuevo formato
                String fechaFormateada = fecha.format(formatoNuevo);

                mov.setFecha(fechaFormateada.toUpperCase());
                boolean first = true;

                for (ObtenerMovimientosAspPagoOBJ item2 : item.getMovimientosAspPago()) {

                    if (!first) {
                        mov = new MovimientoOBJ();
                        mov.setFecha("");
                    } else
                        first = false;

                    if (item2.getTipo().equals("+")) {
                        mov.setAbono(String.valueOf(item2.getMonto()));
                        mov.setCargo("");
                    } else {
                        mov.setCargo(String.valueOf(item2.getMonto()));
                        mov.setAbono("");
                    }
                    String descMov;

                    if (StringUtils.isBlank(item2.getTitulo()))
                        descMov = item2.getDescripcion();
                    else
                        descMov = item2.getTitulo();
                    if (item2.getDetalleMovimiento() != null) {
                        if (movimientosCoDiSpei.contains(item2.getCveMovimiento()) || movimientosTraspasos.contains(item2.getCveMovimiento())) {
                            if (item2.getTipo().equals("-"))
                                descMov += "\nCuenta beneficiaria: " + item2.getDetalleMovimiento().getDestino();
                            else if (item2.getTipo().equals("+"))
                                descMov += "\nCuenta Receptora: " + item2.getDetalleMovimiento().getDestino();
                            String concepto = "";
                            String referencia = "";
                            String claveRastreo = "";
                            String numAut = "";
                            if (item2.getDetalleMovimiento().getDetalle() != null) {
                                for (DetalleMovOBJ item3 : item2.getDetalleMovimiento().getDetalle()) {
                                    String nombre = item3.getNombre();
                                    String valor = item3.getValor();
                                    if (nombre.equals("Concepto")) {
                                        concepto = "/Concepto: " + valor;
                                    } else if (nombre.equals("Referencia")) {
                                        referencia = "\nReferencia: " + valor;
                                    } else if (nombre.contains("rastreo")) {
                                        claveRastreo = "/Clave de rastreo: " + valor;
                                    }
                                }
                                descMov += concepto + referencia + claveRastreo + numAut;
                            }
                            if (item2.getTipo().equals("-"))
                                descMov += "\nBeneficiario: " + item2.getDetalleMovimiento().getBeneficiario();
                            else if (item2.getTipo().equals("+"))
                                descMov += "\nEmisor: " + item2.getDetalleMovimiento().getEmisor();
                        } else if (movimientosPagoServicios.contains(item2.getCveMovimiento())) {
                            String compania = "";
                            String referencia = "";
                            if (item2.getDetalleMovimiento().getDetalle() != null) {
                                for (DetalleMovOBJ item3 : item2.getDetalleMovimiento().getDetalle()) {
                                    String nombre = item3.getNombre();
                                    String valor = item3.getValor();
                                    if (nombre.equals("Compañía")) {
                                        compania = valor;
                                    } else if (nombre.equals("Referencia")) {
                                        referencia = "\nReferencia: " + valor;
                                    }
                                }
                                descMov += " " + compania + referencia;
                            }
                        } else if (movimientosCredito.contains(item2.getCveMovimiento())) {
                            String concepto = "";
                            Pattern patron = null;
                            if (item2.getTipo().equals("-"))
                                patron = Pattern.compile("Pago al contrato de credito (.+)");
                            else if (item2.getTipo().equals("+"))
                                patron = Pattern.compile("Devolucion intento de pago de credito (.+)");
                            if (item2.getDetalleMovimiento().getDetalle() != null) {
                                for (DetalleMovOBJ item3 : item2.getDetalleMovimiento().getDetalle()) {
                                    String nombre = item3.getNombre();
                                    String valor = item3.getValor();
                                    if (nombre.equals("Concepto")) {
                                        Matcher matcher1 = patron.matcher(valor);
                                        if (matcher1.find())
                                            concepto = "\n" + matcher1.group(1);
                                    }
                                }
                                descMov += concepto;
                            }
                        } else if (movimientosDevPagoServicios.contains(item2.getCveMovimiento())) {

                        } else if (movimientosTarjeta.contains(item2.getCveMovimiento())) {
                            String numAut = "";
                            if (item2.getDetalleMovimiento().getDetalle() != null) {
                                for (DetalleMovOBJ item3 : item2.getDetalleMovimiento().getDetalle()) {
                                    String nombre = item3.getNombre();
                                    String valor = item3.getValor();
                                    if (nombre.equals("Número de autorización")) {
                                        numAut = "\nNumero de autorización: " + valor;
                                    }
                                }
                                descMov += " " + numAut;
                            }
                        } else {
                            String concepto = "";
                            if (item2.getDetalleMovimiento().getDetalle() != null) {
                                for (DetalleMovOBJ item3 : item2.getDetalleMovimiento().getDetalle()) {
                                    String nombre = item3.getNombre();
                                    String valor = item3.getValor();
                                    if (nombre.equals("Concepto")) {
                                        concepto = "\n" + valor;
                                    }
                                }
                                descMov += concepto;
                            }
                        }
                    }
                    mov.setSaldo(String.valueOf(item2.getSaldo()));
                    mov.setDetalle(descMov);
                    movs.add(mov);
                }
            }

            RespuestaUtils.respuestaExito(respuesta, gson.toJson(movs));
        } catch (Exception e) {
            errorHandler.handleException(e);
            RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
        return respuesta;
    }

    private String _FechaPeriodo(Date strFecha) {
        Locale LOCALE_MX = Locale.of("es", "MX");
        SimpleDateFormat formatFecha = new SimpleDateFormat(getFormatoFechaPeriodo(), LOCALE_MX);
        String fechaFormato = formatFecha.format(strFecha);
        return fechaFormato;
    }

    public static void setFormatoFechaPeriodo(String formatoFechaPeriodo) {
        ReporteEstadoMovimientoService.formatoFechaPeriodo = formatoFechaPeriodo;
    }
}
