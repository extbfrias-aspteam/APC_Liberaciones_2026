package mx.net.asp.asp_pago_payments.service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.IzelStiDAO;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.spei.RespuestaSVC;
import mx.net.asp.asp_pago_payments.model.spei.UsuariosAutorizadosOBJ;
import mx.net.asp.asp_pago_payments.utilerias.DateUtil;
import mx.net.asp.asp_pago_payments.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresSTI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.Date;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class ValidarAutorizacionUsuarioService {

    private final IzelStiDAO izelStiDAO;
    private final Gson gson;
    private final ErrorHandler errorHandler;

    @Value("${usuario.baja.id}")
    private String bajaId;

    public RespuestaSVC procesar(Date fecha, Long usuarioId, Long aplicacionId, BigDecimal monto, Long empresaId) {
        RespuestaSVC respuesta = new RespuestaSVC();
        new RespuestaDTO();
        RespuestaDTO respuestaDTO;
        try {
            if (!validarParametros(fecha, usuarioId, aplicacionId, empresaId, respuesta)) {
                return respuesta;
            }

            respuestaDTO = izelStiDAO.obtenerUsuariosAutorizados(usuarioId, aplicacionId, empresaId);

            if(respuestaDTO.getCodigo() != 0){
                return agregarError(respuesta, ErroresSTI.ERR_CONFIG_USUARIO);
            }
            Type usuariosListType = new TypeToken<List<UsuariosAutorizadosOBJ>>() {
            }.getType();
            Gson gsonDate = new GsonBuilder()
                    .setDateFormat("MMM d, yyyy")
                    .create();
            List<UsuariosAutorizadosOBJ> usuariosAutorizados = gsonDate.fromJson(respuestaDTO.getData(), usuariosListType);

            for (UsuariosAutorizadosOBJ usuario : usuariosAutorizados) {
                if (usuario == null) {
                    return agregarError(respuesta, ErroresSTI.ERR_USUARIO_MODULO_NO_EXISTE);
                }

                if (bajaId.equals(usuario.getEstatusId())) {
                    return agregarError(respuesta, ErroresSTI.ERR_SPEI_BAJA);
                }

                if (!validarFecha(fecha, usuario, respuesta)) {
                    return respuesta;
                }

                if (!validarMonto(monto, usuario, respuesta)) {
                    respuesta.getBody().addValor("codigo", "REQUERIDO");
                    return respuesta;
                }

                return procesarAutorizacion(usuario, respuesta);
            }

            return agregarError(respuesta, ErroresSTI.ERR_MONTO_INVALIDO); // No se encontró un usuario con monto válido

        } catch (Exception e) {
            log.error("Error en ValidaAutorizacionUsuarioSpei");
            errorHandler.handleException(e);
            return agregarError(respuesta, ErroresSTI.ERR_AUTORIZACION_USUARIO);
        }
    }

    private boolean validarParametros(Date fecha, Long usuarioId, Long aplicacionId, Long empresaId, RespuestaSVC respuesta) {
        boolean esValido = true;
        if (fecha == null) {
            agregarError(respuesta, ErroresSTI.CAMPOS_REQUERIDOS, "fecha captura");
            esValido = false;
        }
        if (usuarioId == null || usuarioId <= 0) {
            agregarError(respuesta, ErroresSTI.CAMPOS_REQUERIDOS, "usuario Id");
            esValido = false;
        }
        if (aplicacionId == null || aplicacionId <= 0) {
            agregarError(respuesta, ErroresSTI.CAMPOS_REQUERIDOS, "Identificador de cuenta");
            esValido = false;
        }
        if (empresaId == null || empresaId <= 0) {
            agregarError(respuesta, ErroresSTI.CAMPOS_REQUERIDOS, "empresa Id");
            esValido = false;
        }
        return esValido;
    }

    private boolean validarMonto(BigDecimal monto, UsuariosAutorizadosOBJ usuario, RespuestaSVC respuesta) {
        BigDecimal montoInferior = usuario.getMontoInferior();
        BigDecimal montoSuperior = usuario.getMontoSuperior();

        if (monto.compareTo(montoInferior) < 0 || monto.compareTo(montoSuperior) > 0) {
            return false;
        }
        return true;
    }

    private boolean validarFecha(Date fecha, UsuariosAutorizadosOBJ usuario, RespuestaSVC respuesta) throws ParseException {
        if (DateUtil.fechaAntes(fecha, usuario.getFechaIni()) || DateUtil.fechaDespues(fecha, usuario.getFechaFin())) {
            agregarError(respuesta, ErroresSTI.ERR_FECHA_FUERA_RANGO);
            return false;
        }
        return true;
    }

    private RespuestaSVC procesarAutorizacion(UsuariosAutorizadosOBJ usuario, RespuestaSVC respuesta) {
        String autorizado = usuario.getAutorizado();
        if ("0".equals(autorizado)) {
            respuesta.getBody().addValor("codigo", "REQUERIDO");
        } else if ("1".equals(autorizado)) {
            respuesta.getBody().addValor("codigo", "AUTORIZADO");
        }
        return respuesta;
    }

    private RespuestaSVC agregarError(RespuestaSVC respuesta, Long codigoError, String detalles) {
        respuesta.getErrores().addCodigo("STI", codigoError, detalles);
        return respuesta;
    }

    private RespuestaSVC agregarError(RespuestaSVC respuesta, Long codigoError) {
        respuesta.getErrores().addCodigo("STI", codigoError);
        return respuesta;
    }
}