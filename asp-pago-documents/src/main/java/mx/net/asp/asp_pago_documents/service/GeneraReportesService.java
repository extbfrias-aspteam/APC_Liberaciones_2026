package mx.net.asp.asp_pago_documents.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_documents.model.*;
import mx.net.asp.asp_pago_documents.utilerias.ErrorHandler;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.sql.DataSource;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiFunction;

@Service
@Log4j2
@RequiredArgsConstructor
public class GeneraReportesService {

    private final ErrorHandler errorHandler;

    @Autowired
    @Qualifier("procreaDataSource")
    private DataSource dataSourcePr;
    @Autowired
    @Qualifier("ceroDataSource")
    private DataSource dataSourceCero;

    @Value("${ruta.jaspers}")
    private String rutaJaspers;
    @Value("${ruta.img}")
    private String rutaImg;

    @Value("${ruta.doc.cta.simplificada}")
    private String rutaDocCtaSimplificada;
    @Value("${ruta.contrato}")
    private String rutaContrato;
    @Value("${ruta.tarjeta.ahorro}")
    private String rutaTarjetaAhorro;
    @Value("${ruta.con.serv.elec}")
    private String rutaConServElec;
    @Value("${ruta.expediente}")
    private String rutaExpediente;
    @Value("${ruta.anexo.a.disposiciones.legales}")
    private String rutaDispLegales;
    @Value("${ruta.caratula}")
    private String rutaCaratula;
    @Value("${ruta.anexoA}")
    private String rutaAnexoA;

    @Value("${ruta.doc.edo.movimientos}")
    private String rutaReporteEdoMovimientos;

    @Value("${ruta.doc.cta.inversion}")
    private String rutaDocCtaInversion;

    public byte[] generaTarjetaAhorro(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();
            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("P_CUENTA", datosContratoCtaFacilObj.getCuenta());
            params.put("cuenta", datosContratoCtaFacilObj.getCuenta());
            params.put("P_CLABE_INT", datosContratoCtaFacilObj.getCuentaClabe());
            params.put("P_REFERENCIA", datosContratoCtaFacilObj.getReferencia());
            params.put("P_SOLICITANTE", datosContratoCtaFacilObj.getNombreSolicitante());
            params.put("P_REGION", datosContratoCtaFacilObj.getSucursalId());

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaTarjetaAhorro +
                        "TarjetaAhorroSimplificada.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaTarjetaAhorro::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaContratoCuentaSimplificada(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_CUENTA", datosContratoCtaFacilObj.getCuenta());
            params.put("P_NUM_SOLICITANTE", datosContratoCtaFacilObj.getPersonaId());
            params.put("P_EMPRESA_ID", datosContratoCtaFacilObj.getEmpresa()); // No está
            params.put("P_PRODUCTO_ID", datosContratoCtaFacilObj.getProductoId()); // No está
            params.put("P_AGENTE", "SUCVIRTUAL");
            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("SUBREPORT_DIR", rutaJaspers + rutaDocCtaSimplificada + rutaContrato);
            params.put("P_COLONIA", datosContratoCtaFacilObj.getColonia()); // No está
            params.put("P_CP", datosContratoCtaFacilObj.getCp()); // No está
            params.put("P_LOCALIDAD", datosContratoCtaFacilObj.getLocalidad()); // No está
            params.put("P_MUNICIPIO", datosContratoCtaFacilObj.getMunicipio()); // No está
            params.put("P_ESTADO", datosContratoCtaFacilObj.getEstado()); // No está
            params.put("NOMBRE_B", datosContratoCtaFacilObj.getNombreBeneficiario());
            params.put("PORCENTAJE_B", datosContratoCtaFacilObj.getPorcentajeBeneficiario());
            params.put("PARENTESCO_B", datosContratoCtaFacilObj.getParentescoBeneficiario());

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaContrato +
                        "contrato.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaContratoCuentaSimplificada::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaContratoServElecSF(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("P_SOLICITANTE", datosContratoCtaFacilObj.getPersonaId());
            params.put("P_SUCURSAL_ID", datosContratoCtaFacilObj.getSucursalId());
            params.put("SUBREPORT_DIR", rutaJaspers + rutaDocCtaSimplificada + rutaContrato);
            params.put("P_EMPRESA_ID", datosContratoCtaFacilObj.getEmpresa());
            params.put("P_AGENTE", "SUCVIRTUAL");

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaConServElec +
                        "con_serv_elec.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaContratoServElecSF::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaRegistroContrato(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("P_NUM_SOLICITANTE", datosContratoCtaFacilObj.getPersonaId());
            params.put("P_CUENTA", datosContratoCtaFacilObj.getCuenta());

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaExpediente +
                        "car_exp.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaRegistroContrato::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaDisposicionesLegales(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("P_SOLICITANTE", datosContratoCtaFacilObj.getNombreSolicitante());
            params.put("P_CUENTA", datosContratoCtaFacilObj.getCuenta());
            params.put("SUBREPORT_DIR", rutaJaspers + rutaDocCtaSimplificada + rutaDispLegales);

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaDispLegales +
                        "anexo_a.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaDisposicionesLegales::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaCaratula(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("P_SOLICITANTE", datosContratoCtaFacilObj.getNombreSolicitante());
            params.put("P_CUENTA", datosContratoCtaFacilObj.getCuenta());
            params.put("P_SUCURSAL", datosContratoCtaFacilObj.getSucursalId());
            params.put("SUBREPORT_DIR", rutaJaspers + rutaDocCtaSimplificada + rutaCaratula);

            try (Connection connection = dataSourceCero.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaCaratula +
                        "caratula.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaCaratula::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaAnexo(DocumentacionCtaFacilOBJ datosContratoCtaFacilObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("reca_facil", datosContratoCtaFacilObj.getRecaFacil());
            params.put("nombre", datosContratoCtaFacilObj.getNombreSolicitante());
            params.put("apellido_p", datosContratoCtaFacilObj.getApellidoPaterno());
            params.put("apellido_m", datosContratoCtaFacilObj.getApellidoMaterno());
            params.put("correo", datosContratoCtaFacilObj.getCorreo());
            params.put("telefono", datosContratoCtaFacilObj.getTelefono());
            params.put("rfc", datosContratoCtaFacilObj.getRfc());
            params.put("curp", datosContratoCtaFacilObj.getCurp());
            params.put("fecha_nacimiento", datosContratoCtaFacilObj.getFechaNaciemiento());
            params.put("calle", datosContratoCtaFacilObj.getCalle());
            params.put("numero_ext", datosContratoCtaFacilObj.getNoExterior());
            params.put("numeri_int", datosContratoCtaFacilObj.getNoInterior());
            params.put("colonia", datosContratoCtaFacilObj.getColonia());
            params.put("cp", datosContratoCtaFacilObj.getCp());
            params.put("localidad", datosContratoCtaFacilObj.getLocalidad());
            params.put("municipio", datosContratoCtaFacilObj.getMunicipio());
            params.put("entidad_federativa", datosContratoCtaFacilObj.getEstado());
            params.put("porcentaje", datosContratoCtaFacilObj.getPorcentajeBeneficiario() + "%");

            List<String> nombres = new ArrayList<String>();
            List<String> porcentajes = new ArrayList<String>();

            nombres.add(datosContratoCtaFacilObj.getNombreBeneficiario());
            porcentajes.add(datosContratoCtaFacilObj.getPorcentajeBeneficiario());

            params.put("Nombres", nombres);
            params.put("Porcentajes", porcentajes);

            try {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaAnexoA +
                        "Anexo_a.jasper", params, new net.sf.jasperreports.engine.JREmptyDataSource());

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaAnexo::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaEstadoMovimientos(DatosEstadoMovimientoOBJ datosEstadoMovimientoObj, List<MovimientoOBJ> movimientoObj) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("titulos", datosEstadoMovimientoObj.getTitulos());
            params.put("clabe", datosEstadoMovimientoObj.getClabe());
            params.put("tipoCuenta", datosEstadoMovimientoObj.getTipoCuenta());
            params.put("colonia", datosEstadoMovimientoObj.getColonia());
            params.put("ciudad", datosEstadoMovimientoObj.getCiudad());
            params.put("cuenta", datosEstadoMovimientoObj.getCuenta());
            params.put("rfc", datosEstadoMovimientoObj.getRfc());
            params.put("nombre", datosEstadoMovimientoObj.getNombre());
            params.put("calle", datosEstadoMovimientoObj.getCalle());
            params.put("fechaInicio", datosEstadoMovimientoObj.getFechaInicio());
            params.put("fechaFin", datosEstadoMovimientoObj.getFechaFin());
            params.put("fechaEmision", datosEstadoMovimientoObj.getFechaEmision());

            JRBeanCollectionDataSource dsEdoMov = new JRBeanCollectionDataSource(movimientoObj);

            try {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaReporteEdoMovimientos +
                        "estado_de_movimientos.jasper", params, dsEdoMov);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaEstadoMovimientos:: ");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaAnexoDetalleBeneficiario(AnexoDetalleBeneficiarioOBJ anexoDetalleBeneficiarioOBJ) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            /* Función para remplazar los null por cadenas vacías */
            BiFunction<String, String, String> safeGet = (value, defaultValue) -> value != null ? value : defaultValue;

            /* Datos del solicitante */
            params.put("nombre", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNombre(), ""));
            params.put("apellido_p", safeGet.apply(anexoDetalleBeneficiarioOBJ.getApellidoPaterno(), ""));
            params.put("apellido_m", safeGet.apply(anexoDetalleBeneficiarioOBJ.getApellidoMaterno(), ""));
            params.put("fecha_nacimiento", safeGet.apply(anexoDetalleBeneficiarioOBJ.getFechaNacimiento(), ""));
            params.put("porcentaje", safeGet.apply(anexoDetalleBeneficiarioOBJ.getPorcentaje(), ""));
            params.put("rfc", safeGet.apply(anexoDetalleBeneficiarioOBJ.getRfc(), ""));
            params.put("curp", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCurp(), ""));
            params.put("calle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCalle(), ""));
            params.put("numero_ext", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNoExterior(), ""));
            params.put("num_interior", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNoInterior(), ""));
            params.put("colonia", safeGet.apply(anexoDetalleBeneficiarioOBJ.getColonia(), ""));
            params.put("municipio", safeGet.apply(anexoDetalleBeneficiarioOBJ.getMunicipio(), ""));
            params.put("entidad_federativa", safeGet.apply(anexoDetalleBeneficiarioOBJ.getEntidadFederativa(), ""));
            params.put("cp", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCp(), ""));
            params.put("telefono", safeGet.apply(anexoDetalleBeneficiarioOBJ.getTelefono(), ""));
            params.put("correo", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCorreo(), ""));

            /* Datos del solicitante secundario */
            params.put("nombre_s", "");
            params.put("apellido_p_s", "");
            params.put("apellido_m_s", "");
            params.put("fecha_nacimiento_s", "");
            params.put("porcentaje_s", "");
            params.put("rfc_s", "");
            params.put("curp_s", "");
            params.put("calle_s", "");
            params.put("numero_ext_s", "");
            params.put("num_interior_s", "");
            params.put("colonia_s", "");
            params.put("municipio_s", "");
            params.put("entidad_federativa_s", "");
            params.put("cp_s", "");
            params.put("telefono_s", "");
            params.put("correo_s", "");

            /* Listas y otros datos generales */
            params.put("Nombres", anexoDetalleBeneficiarioOBJ.getNombres() != null ? anexoDetalleBeneficiarioOBJ.getNombres() : new ArrayList<>());
            params.put("Porcentajes", anexoDetalleBeneficiarioOBJ.getPorcentajes() != null ? anexoDetalleBeneficiarioOBJ.getPorcentajes() : new ArrayList<>());
            params.put("Nombre_ben", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNombreBeneficiario(), ""));
            params.put("Porcentaje_ben", safeGet.apply(anexoDetalleBeneficiarioOBJ.getPorcentajeBeneficiario(), ""));
            params.put("reca_facil", safeGet.apply(anexoDetalleBeneficiarioOBJ.getRecaFacil(), ""));
            params.put("P_RUTAIMAGEN", rutaImg);
            params.put("domicilioCompleto", "");
            params.put("localidad", "");
            params.put("localidad_s", "");

            /* Datos del beneficiario */
            params.put("nombre_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNombreBeneficiarioDetalle(), ""));
            params.put("curp_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCurpBeneficiarioDetalle(), ""));
            params.put("rfc_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getRfcBeneficiarioDetalle(), ""));
            params.put("cp_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCpBeneficiarioDetalle(), ""));
            params.put("colonia_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getColoniaBeneficiarioDetalle(), ""));
            params.put("ciudad_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCiudadBeneficiarioDetalle(), ""));
            params.put("municipio_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getMunicipioBeneficiarioDetalle(), ""));
            params.put("estado_b_detalle", "");
            params.put("calle_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getCalleBeneficiarioDetalle(), ""));
            params.put("noExt_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNoExtBeneficiarioDetalle(), ""));
            params.put("noInt_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getNoIntBeneficiarioDetalle(), ""));
            params.put("telefono_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getTelefonoBeneficiarioDetalle(), ""));
            params.put("correo_b_detalle", "");
            params.put("fechanac_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getFechaNacimientoBeneficiarioDetalle(), ""));
            params.put("porcentaje_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getPorcentajeBeneficiarioDetalle(), ""));
            params.put("parentesco_b_detalle", safeGet.apply(anexoDetalleBeneficiarioOBJ.getParentescoBeneficiarioDetalle(), ""));

            try {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaSimplificada + rutaAnexoA +
                        "Anexo_a_detalle_beneficiario.jasper", params, new net.sf.jasperreports.engine.JREmptyDataSource());

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaAnexoDetalleBeneficiario:: ");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaConstanciaCtaInversion(String cuentaInversion) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("num_cuenta", cuentaInversion);

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaInversion +
                        "constancia.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaConstanciaCtaInversion::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaCaratulaCtaInversion(ReportesParamOBJ reportesParamOBJ) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("llave", reportesParamOBJ.getLlave());
            params.put("parametro1", reportesParamOBJ.getParametro1());
            params.put("parametro2", reportesParamOBJ.getParametro2());
            params.put("parametro3", reportesParamOBJ.getParametro3());
            params.put("parametro4", reportesParamOBJ.getParametro4());
            params.put("parametro5", reportesParamOBJ.getParametro5());
            params.put("parametro6", reportesParamOBJ.getParametro6());
            params.put("parametro7", reportesParamOBJ.getParametro7());
            params.put("parametro8", reportesParamOBJ.getParametro8());
            params.put("parametro9", reportesParamOBJ.getParametro9());
            params.put("parametro10", reportesParamOBJ.getParametro10());
            params.put("parametro11", reportesParamOBJ.getParametro11());
            params.put("parametro12", reportesParamOBJ.getParametro12());
            params.put("parametro52", reportesParamOBJ.getParametro52());
            params.put("parametro53", reportesParamOBJ.getParametro53());

            try {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaInversion +
                        "caratula.jasper", params, new net.sf.jasperreports.engine.JREmptyDataSource());

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaCaratulaCtaInversion::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }

    public byte[] generaEdoCuentaCtaInversion(String cuentaInversion, Integer anio, Integer mes) {
        byte[] pdfBytes;
        try {
            Map<String, Object> params = new HashMap<>();

            params.put("anio", anio);
            params.put("numMes", mes);
            params.put("no_cuenta", cuentaInversion);

            try (Connection connection = dataSourcePr.getConnection()) {
                JasperPrint print = JasperFillManager.fillReport(rutaJaspers + rutaDocCtaInversion +
                        "nuevoDoc.jasper", params, connection);

                pdfBytes = JasperExportManager.exportReportToPdf(print);
            } catch (Exception e) {
                log.error("Error en durante la generacion del jasper:: ", e);
                pdfBytes = null;
            }
        } catch (Exception e) {
            log.error("Error en generaEdoCuentaCtaInversion::");
            errorHandler.handleException(e);
            pdfBytes = null;
        }
        return pdfBytes;
    }
}
