package mx.net.asp.asp_pago_management.service;

import java.util.Calendar;
import java.util.Date;

import mx.net.asp.asp_pago_management.dto.RespuestaDTO;
import mx.net.asp.asp_pago_management.utilerias.RespuestaUtils;
import mx.net.asp.asp_pago_management.utilerias.errores.ErroresGenerales;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.dao.AhorroContratoDAO;
import mx.net.asp.asp_pago_management.dao.AhorroGarantiasDAO;
import mx.net.asp.asp_pago_management.dao.AhorroRendimientoDAO;
import mx.net.asp.asp_pago_management.dao.AhorroRendimientoVigenteDAO;
import mx.net.asp.asp_pago_management.dao.AhorroRenovacionesDAO;
import mx.net.asp.asp_pago_management.dao.AhorroRetencionesDAO;
import mx.net.asp.asp_pago_management.dao.AhorroSaldosDAO;

import mx.net.asp.asp_pago_management.model.AhorroContrato;
import mx.net.asp.asp_pago_management.model.AhorroRendimientoVigentesOBJ;
import mx.net.asp.asp_pago_management.model.AhorroSaldosOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import mx.net.asp.asp_pago_management.utilerias.Utilerias;

@Log4j2
@Service
public class SaldoAhorroService {

    private final AhorroContratoDAO adao;
    private final AhorroRendimientoDAO ardao;
    private final AhorroRenovacionesDAO aredao;
    private final AhorroRendimientoVigenteDAO vdao;
    private final AhorroSaldosDAO asdao;
    private final AhorroGarantiasDAO agdao;
    private final AhorroRetencionesDAO aretdao;
    private final Utilerias util;
    private final ErrorHandler errorHandler;

    public SaldoAhorroService(AhorroContratoDAO adao,
                              AhorroRendimientoDAO ardao,
                              AhorroRenovacionesDAO aredao,
                              AhorroRendimientoVigenteDAO vdao,
                              AhorroSaldosDAO asdao,
                              AhorroGarantiasDAO agdao,
                              AhorroRetencionesDAO aretdao,
                              Utilerias util,
                              ErrorHandler errorHandler) {
        this.adao = adao;
        this.ardao = ardao;
        this.aredao = aredao;
        this.vdao = vdao;
        this.asdao = asdao;
        this.agdao = agdao;
        this.aretdao = aretdao;
        this.util = util;
        this.errorHandler = errorHandler;
    }

    public RespuestaDTO consultaSaldoAhorro(String cuentaAhorro) {
        RespuestaDTO respuesta = new RespuestaDTO();
        Double vSaldo = (double) 0;
        try {
            AhorroContrato ahorroContrato;
            ahorroContrato = adao.buscarByCuenta(cuentaAhorro);

            String tipoRendimiento = ahorroTipocuenta(ahorroContrato.getRendimientoId());
            if (!tipoRendimiento.equals("S")) {
                return RespuestaUtils.respuestaExito(respuesta, String.valueOf(vSaldo));
            }

            Boolean vVence = ahorroVence(ahorroContrato, new Date());
            if (Boolean.FALSE.equals(vVence)) {
                return RespuestaUtils.respuestaExito(respuesta, String.valueOf(vSaldo));
            } else {
                vSaldo = ahorroSaldodisponible(ahorroContrato);
                return RespuestaUtils.respuestaExito(respuesta, String.valueOf(vSaldo));
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return RespuestaUtils.asignarError(respuesta,
                    ErroresGenerales.ERROR_INTERNO,
                    ErroresGenerales.descError.get(ErroresGenerales.ERROR_INTERNO));
        }
    }

    private String ahorroTipocuenta(Integer rendimientoId) {
        String tipoRendimiento;
        try {
            tipoRendimiento = ardao.ahorroTipocuenta(rendimientoId);
        } catch (Exception e) {
            errorHandler.handleException(e);
            return "";
        }
        return tipoRendimiento;
    }

    private Boolean ahorroVence(AhorroContrato ahorroContrato, Date pFecha) {
        boolean vBan = false;
        try {
            Date fechaApertura = ahorroContrato.getFechaApertura();
            if (fechaApertura == null) {
                return vBan;  // Si no tiene fecha de apertura, retorna false inmediatamente
            }

            Date fechaRenovacion = getFechaRenovacion(ahorroContrato);
            AhorroRendimientoVigentesOBJ rendimiento = obtenerRendimientoVigente(ahorroContrato);

            // Si el tipo de ahorro es 2 o no tiene plazo, se considera vencido
            if (Boolean.TRUE.equals(esAhorroVencido(rendimiento, ahorroContrato.getTipoAhorroId()))) {
                return true;
            }

            // Calcular la nueva fecha según el tipo de corte
            Date fechaLimite = calcularFechaLimite(fechaRenovacion != null ? fechaRenovacion : fechaApertura, rendimiento, pFecha);

            // Verificar si la fecha de corte es válida
            if (Boolean.TRUE.equals(isFechaDentroDelRango(pFecha, fechaLimite, rendimiento))) {
                vBan = true;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
        }
        return vBan;
    }

    // Método para obtener la última fecha de renovación
    private Date getFechaRenovacion(AhorroContrato ahorroContrato) {
        return aredao.ultimaFechaRenovacion(ahorroContrato.getCuenta());
    }

    // Método para obtener el rendimiento vigente de un ahorro
    private AhorroRendimientoVigentesOBJ obtenerRendimientoVigente(AhorroContrato ahorroContrato) {
        return vdao.buscarByCuenta(ahorroContrato.getCuenta());
    }

    // Método que determina si el ahorro está vencido según el tipo de ahorro
    private Boolean esAhorroVencido(AhorroRendimientoVigentesOBJ rendimiento, Integer tipoAhorro) {
        return rendimiento.getPlazo() == null || rendimiento.getPlazo() == 0 || tipoAhorro == 2;
    }

    // Método que calcula la fecha límite de vencimiento
    private Date calcularFechaLimite(Date fechaInicio, AhorroRendimientoVigentesOBJ rendimiento, Date pFecha) {
        Calendar calendar = Calendar.getInstance();
        Date fechaCalculada = fechaInicio;

        if ("V".equals(rendimiento.getTipoCorte())) {
            while (fechaCalculada.before(pFecha)) {
                calendar.setTime(fechaCalculada);
                calendar.add(Calendar.DATE, rendimiento.getPlazo());
                fechaCalculada = calendar.getTime();
            }
        } else {
            calendar.setTime(fechaCalculada);
            calendar.add(Calendar.MONTH, rendimiento.getPlazo() / 30);
            fechaCalculada = calendar.getTime();
        }

        calendar.setTime(fechaCalculada);
        calendar.add(Calendar.DATE, rendimiento.getDiasGracia());
        return calendar.getTime();
    }

    // Método para verificar si la fecha está dentro del rango de gracia
    private Boolean isFechaDentroDelRango(Date pFecha, Date fechaLimite, AhorroRendimientoVigentesOBJ rendimiento) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fechaLimite);
        calendar.add(Calendar.DATE, rendimiento.getDiasGracia());
        return pFecha.after(fechaLimite) && pFecha.before(calendar.getTime());
    }


    private Double ahorroSaldodisponible(AhorroContrato ahorroContrato) {
        Double vSaldo;
        Date vFecha;
        new AhorroSaldosOBJ();
        AhorroSaldosOBJ ahorroSaldos;
        try {
            ahorroSaldos = asdao.buscarByCuenta(ahorroContrato.getCuenta());
            vFecha = ahorroSaldos.getFechaCorte();

            if (vFecha == null) {
                vSaldo = ahorroContrato.getSaldo();
            } else {
                vSaldo = ahorroSaldos.getSaldoDisponible();
            }

            Double vRetiros = (double) 0;
            Double vGarantias;
            vGarantias = agdao.montoGarantia(ahorroContrato.getCuenta());
            if (vGarantias == null) {
                vGarantias = (double) 0;
            }

            Double vRetenciones;
            vRetenciones = aretdao.montoRetenciones(ahorroContrato.getCuenta());

            vSaldo = vSaldo - vRetiros - vGarantias - vRetenciones;
            if (vSaldo < 0) {
                vSaldo = (double) 0;
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return (double) 0;
        }
        return util.redondeaCentavos(vSaldo, 2);
    }

}
