package mx.net.asp.asp_pago_payments.ws.asp;

import com.google.gson.Gson;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_payments.dao.SolicitanteDAO;
import mx.net.asp.asp_pago_payments.dto.RespuestaDTO;
import mx.net.asp.asp_pago_payments.model.SolicitanteOBJ;
import mx.net.asp.asp_pago_payments.service.JwtTokenManager;
import mx.net.asp.asp_pago_payments.utilerias.InvokeRestServiceUtil;
import mx.net.asp.asp_pago_payments.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_payments.utilerias.errores.ErroresPSpeiSimple;
import mx.net.asp.asp_pago_payments.ws.asp.request.*;
import mx.net.asp.asp_pago_payments.ws.asp.response.FraudeCheckResponse;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@Service
@Log4j2
@RequiredArgsConstructor
public class WSFradudeCheck {

    @Value("${ws.fraude.check.host}")
    private String wsFraudeCheckHost;
    @Value("${ws.fraude.check.service}")
    private String fraudeCheckService;

    @Value("${valida.fraude.trx}")
    private Boolean validaFraude;

    @Value("${moneda.trx}")
    private String monedaTrx;
    @Value("${event.fraude}")
    private String eventoFraude;
    @Value("${nick.name.node}")
    private String nickNameNode;
    @Value("${nick.name.module}")
    private String nickNameModule;
    @Value("${pblu.id}")
    private String pbluId;
    @Value("${monitor.plus.id}")
    private String monitorPlusId;
    @Value("${type.fraude.check}")
    private String typeFraudeCheck;
    @Value("${user.fraude.check}")
    private String userFraudeCheck;

    private final SolicitanteDAO solicitanteDAO;

    private final JwtTokenManager jwtTokenManager;
    private final InvokeRestServiceUtil invokeRestServiceUtil;
    private final Gson gson;

    public RespuestaDTO validaTrxFraudulenta(FraudeCheckReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();
        // Construir el cuerpo de la solicitud
        if (!Boolean.TRUE.equals(validaFraude)) {
            log.info("*** Validacion transaccion fraudulenta deshabilitada ***");
            return RespuestaUtils.respuestaExito(respuesta);
        }

        respuesta = ejecutarFraudeCheck(req);
        if (respuesta.getCodigo() == 401) {
            //Renovacion de token
            jwtTokenManager.forceRefresh();
            respuesta = ejecutarFraudeCheck(req);
        }

        return respuesta;
    }

    public RespuestaDTO ejecutarFraudeCheck(FraudeCheckReq req) {
        RespuestaDTO respuesta = new RespuestaDTO();

        String url = InvokeRestServiceUtil.getURLRest(wsFraudeCheckHost, "", fraudeCheckService);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(jwtTokenManager.getToken());

        // Configurar la solicitud con los encabezados
        HttpEntity<FraudeCheckReq> requestEntity = new HttpEntity<>(req, headers);
        log.info("request:: {}", gson.toJson(req));
        respuesta = invokeRestServiceUtil.enviarSolicitudApiEiYu(url, requestEntity, HttpMethod.POST, null);
        if (respuesta.getCodigo() == 0) {
            //deserealizamos la respuesta del servicio guardada en el campo data
            FraudeCheckResponse response = gson.fromJson(respuesta.getData(), FraudeCheckResponse.class);
            //valida el success
            if (!response.isSuccess()) {
                //imprimir mensaje original
                log.warn("*** Transaccion fraudulenta por motivo: {}", response.getMessage());
            }

            return RespuestaUtils.respuestaExito(respuesta);
        } else if (respuesta.getCodigo() <= -500 && respuesta.getCodigo() >= -503) {
            log.warn("Error {}, Mensaje {}", respuesta.getCodigo(), respuesta.getMensaje());
            log.warn("Se dejara continuar con la transaccion ya que es un error no controlado");
            RespuestaUtils.respuestaExito(respuesta);
        }
        return respuesta;
    }

    public FraudeCheckReq buildFraudeCheckTransaction(
            String idPersona,
            String cuentaCliente,
            String usuarioApp,
            String monto,
            String referencia,
            String bancoDestino,
            String cuentaDestino,
            String nombreDestino,
            String sessionId,
            String tPersona,
            String nombreOrdenante,
            String cveRastreo
    ) {

        String fechaActual = LocalDate.now()
                .format(DateTimeFormatter.ofPattern("ddMMyyyy"));

        String horaActual = LocalTime.now()
                .format(DateTimeFormatter.ofPattern("HHmm"));

        // HEADER
        FraudeCheckHeader header = new FraudeCheckHeader();
        header.setDate(fechaActual);
        header.setTime(horaActual);
        header.setEvent(eventoFraude);
        header.setNicknamenode(nickNameNode);
        header.setNicknamemodule(nickNameModule);
        header.setUser(userFraudeCheck);
        header.setRtind("S");

        // FIELDS
        FraudeCheckFields fields = new FraudeCheckFields();

        // Datos cliente
        fields.setCodigoEntidad("0001");
        fields.setIdCliente(safeAlpha(idPersona));
        fields.setUsuarioBancaDigital(safeAlpha(usuarioApp));
        fields.setCodigoClienteTitular(safeNumeric(idPersona));

        if (StringUtils.isNotBlank(idPersona)) {

            SolicitanteOBJ solicitanteOBJ =
                    solicitanteDAO.obtenerDatosSolicitanteTrxById(idPersona);

            fields.setTipoPersona(safeAlpha(solicitanteOBJ.getTPersona()));
            fields.setTelefonoCelularCuentaTitular(
                    safeNumeric(solicitanteOBJ.getTelefonoCoDi()));
            fields.setTelefonoResidenciaCuentaTitular(
                    safeNumeric(solicitanteOBJ.getTelefonoCoDi()));
            fields.setNombreCompletoClienteCuentaTitular(
                    safeAlpha(solicitanteOBJ.getNombre()));

        } else {

            fields.setTipoPersona(safeAlpha(tPersona));
            fields.setNombreCompletoClienteCuentaTitular(
                    safeAlpha(nombreOrdenante));

            fields.setTelefonoCelularCuentaTitular("1");
            fields.setTelefonoResidenciaCuentaTitular("1");
        }

        // Datos transacción
        fields.setOrigenDeTransaccion("APP");
        fields.setAccionDentroSesion("TRANSFER");
        fields.setCodigoTransaccion("010001");
        fields.setCodigoRespuestaHost("00");

        fields.setFechaTrx(fechaActual);
        fields.setHoraTrx(horaActual);

        fields.setMonedaTrx("MXN");

        fields.setMontoTotalTrxLocal(formatMonto(monto));
        fields.setMontoTotalTrxDolares("0");
        fields.setTipodeCambio("0");

        fields.setLocalInternacional("N");
        fields.setTipoTransaccion("D");

        fields.setNumeroReferencia(safeAlpha(referencia));
        fields.setClaveTransferInterbancaria("1");
        fields.setIDRastreoTransferencias("1");

        fields.setReversa("1");

        // MFA
        fields.setDatosRetoMFA("1");
        fields.setEnviaPreguntaReto("1");
        fields.setRespuestasCorrectas("1");
        fields.setMFARetoSolicitado("0");
        fields.setMFATipoReto("1");
        fields.setMFAAprobado("1");
        fields.setMFAPrioridad("1");

        // Cuenta titular
        fields.setCodigoProductoTitular("1");
        fields.setCodigoSubproductoTitular("1");
        fields.setNumerodeCuentaTitular(safeNumeric(cuentaCliente));
        fields.setFechaAperturaCtaTitular("0");
        fields.setTipoVIPCuentaTitular("1");
        fields.setFechaVinculacionCliente("0");
        fields.setEstatusCuentaProductoTitular("A");

        fields.setSignoValorSaldoCuentaTitular("1");
        fields.setSaldoDisponibleCuentaTitular("0");
        fields.setEmpleadoCuentaTitular("1");
        fields.setSucursalAperturaCuentaTitular("");

        fields.setTelefonoLaboralCuentaTitular("1");
        fields.setSegmentoClienteCuentaTitular("1");
        fields.setCorreoElectronicoCuentaTitular("1");
        fields.setEjecutivoCuentaTitular("1");
        fields.setDominioEMailCuentaTitular("1");

        // Cuenta destino
        fields.setCodigoClienteDestino("1");
        fields.setFechaAperturaCuentaDestino("0");
        fields.setEjecutivoCuentaDestino("1");
        fields.setSucursalAperturaCuentaDestino("1");

        fields.setTelefonoCuentaDestino("1");
        fields.setEmailCuentaDestino("1");
        fields.setDominioEmailDestino("1");

        fields.setCodigoPaisDestino("1");
        fields.setCodClienteDestino("1");

        fields.setEmpleadoCtaDestino("1");

        fields.setBancoDestino(safeAlpha(bancoDestino));
        fields.setTipoProductoDestino("1");

        fields.setNumeroCuentaDestino(safeNumeric(cuentaDestino));
        fields.setNombreClienteTitularCtaDestino(
                safeAlpha(nombreDestino));

        // Servicios
        fields.setProveedorServicio("1");
        fields.setCategoriaPagoServicio("1");
        fields.setReferenciaPagoServicio1("1");
        fields.setReferenciaPagoServicio2("1");
        fields.setReferenciaPagoServicio3("1");

        // Fraude
        fields.setOrigendealerta("347");
        fields.setIdentTrxFraude("0");
        fields.setIndicadordeFraude("0");
        fields.setIndicadorRealTime("1");

        // Sesión
        fields.setIPConexion("");
        fields.setCorrelativo("0");
        fields.setSessionID(safeAlpha(sessionId));

        // FingerPrint
        FingerPrint fingerPrint = getFingerPrint();
        fields.setFingerPrint(fingerPrint);

        FraudeCheckTransaction transaction = new FraudeCheckTransaction();
        transaction.setHeader(header);
        transaction.setFields(fields);

        return new FraudeCheckReq(
                "MOBILE",
                "PBL-998877",
                cveRastreo,
                "1347",
                transaction
        );
    }

    @NotNull
    private static FingerPrint getFingerPrint() {

        FingerPrint fingerPrint = new FingerPrint();

        /* SIM */
        Sim sim = new Sim();
        sim.setIsAirPlanModeOn("1");
        sim.setLineNumber("1");
        sim.setSimStatus("1");
        fingerPrint.setSim(sim);

        /* GENERAL */
        General general = new General();
        general.setCertifiedID("1");
        general.setVoiceMailerNumber("*80");
        general.setPhoneNumber("1");
        general.setAppAction("Startup");
        general.setProcessorCount("1");
        general.setTypeAuthentication("1");
        general.setFingerprintVersion("3.1.6");
        general.setGeolocation("1");
        general.setTimezoneName("1");
        general.setPackageName("com.bancrecer.crecermovil");
        general.setScreenResolution("1290.0 * 2796.0");
        general.setTimezoneID("America/Caracas");
        general.setLanguageCountry("español");
        general.setHardwareSerial("1");
        general.setAppVersion("3.9.6");
        general.setLanguage("es-US");
        general.setAppName("Crecermovil");
        fingerPrint.setGeneral(general);

        /* SENSORS */
        Sensors sensors = new Sensors();
        sensors.setBluetooth("0");
        sensors.setGyroscope("1");
        sensors.setDeviceOrientation("Another");
        sensors.setAccelerometer("1");
        sensors.setDevicePosition("Another");
        sensors.setDevicePressure("1");
        sensors.setStepCounter("1");
        sensors.setDevicePressureByPoint("1");
        sensors.setDeviceMotion("0");
        sensors.setNfc("1");
        fingerPrint.setSensors(sensors);

        /* DEVICE */
        Device device = new Device();
        device.setDeviceId("FBEA7393-7DE5-4DD8-83D3-673D62A03E08");
        device.setImei("1");
        device.setAndroidId("1");
        device.setHostName("iPhone");
        device.setImsi("1");
        device.setIsDeviceRoot("0");
        device.setIsEmulator("0");
        device.setKernel("1");
        device.setDeviceModel("iPhone");
        device.setSerialNumber("1");
        device.setOperativeSystem("1");
        device.setDeviceOs("1");
        device.setDeviceBrand("1");
        fingerPrint.setDevice(device);

        /* GEOIP */
        Geoip geoip = new Geoip();
        geoip.setAs("AS8048 CANTV Servicios, Venezuela");
        geoip.setCurrency("VEF");
        geoip.setIsp("CANTV Servicios, Venezuela");
        geoip.setCountry("Venezuela");
        geoip.setRegion("A");
        geoip.setLon("-66.8738");
        geoip.setMobile("0");
        geoip.setProxy("0");
        geoip.setOrg("CANTV Servicios, Venezuela");
        geoip.setDistrict("1");
        geoip.setContinent("South America");
        geoip.setRegionName("Distrito Federal");
        geoip.setCountryCode3("VEN");
        geoip.setReverse("190-72-90-98.lms-01-p68.cantv.net");
        geoip.setAsname("CANTV Servicios, Venezuela");
        geoip.setOffset("-14400");
        geoip.setTimezone("America/Caracas");
        geoip.setCity("Caracas");
        geoip.setStatus("success");
        geoip.setContinentCode("SA");
        geoip.setQuery("190.72.90.98");
        geoip.setHosting("0");
        geoip.setCurrentTime("2025-11-13T13:54:14-04:00");
        fingerPrint.setGeoip(geoip);

        /* IDENTIFIERS */
        Identifiers identifiers = new Identifiers();
        identifiers.setHash("7B69759630F869F2.7A61B53701BEFDAE.59");
        identifiers.setAnonymity1("356148dfa632e2452ffaf65780b0d82356af6eecf47836aa2948c44c87cb95d7");
        identifiers.setAnonymity2("eb2bea4d9d90f3e4bb98fe7e2f94329f3db101b2e7d45eb2df7093f61c08468b");
        identifiers.setAnonymity3("ee0e5199127360cd2f1a01ff51e81147d8e77ae9c3fdc01291de1650d0bd336f");
        identifiers.setAnonymity5("1");
        fingerPrint.setIdentifiers(identifiers);

        /* ANTI DEBUG */
        AntiDebug antiDebug = new AntiDebug();
        antiDebug.setUnderDebug("0");
        antiDebug.setIsDeveloperModeEnabled("0");
        antiDebug.setPlaystore("1");
        fingerPrint.setAntiDebug(antiDebug);

        /* GEOLOCATION */
        Geolocation geolocation = new Geolocation();
        geolocation.setZipCode("1");
        geolocation.setLongitude("1");
        geolocation.setPostalCode("1");
        geolocation.setCountry("US");
        geolocation.setIsp("--");
        geolocation.setCity("1");
        geolocation.setRegion("1");
        geolocation.setLatitude("1");
        fingerPrint.setGeolocation(geolocation);

        /* NETWORK */
        Network network = new Network();
        network.setPrivateIp("1");
        network.setDetectNetwork("mobile");
        network.setCelId("1");
        network.setIsoCountryCode("--");
        network.setMacAddress("1");
        network.setMcc("1");
        network.setCarrierName("--");
        network.setAllowIp("1");
        network.setNetworkCode("65535");
        network.setCountryCode("65535");
        network.setMnc("1");
        fingerPrint.setNetwork(network);

        /* REVERSE */
        Reverse reverse = new Reverse();
        reverse.setAntiDetect("0");
        reverse.setSslkiller("0");
        reverse.setXposed("0");
        reverse.setJustrustme("0");
        reverse.setScrenAttack("0");
        reverse.setFriday("0");
        reverse.setSuperUser("0");
        reverse.setDroidmon("0");
        fingerPrint.setReverse(reverse);

        return fingerPrint;
    }

    private String safeAlpha(String value) {
        return value == null ? "" : value;
    }

    private String safeNumeric(String value) {

        if (value == null || value.isBlank()) {
            return "0";
        }

        return value.replaceAll("[^0-9]", "");
    }

    private String formatMonto(String monto) {

        if (monto == null || monto.isBlank()) {
            return "0";
        }

        BigDecimal amount = new BigDecimal(monto)
                .setScale(2, RoundingMode.HALF_UP);

        return amount
                .movePointRight(2)
                .toBigInteger()
                .toString();
    }
}

