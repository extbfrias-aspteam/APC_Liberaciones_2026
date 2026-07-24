package mx.net.asp.asp_pago_management.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import mx.net.asp.asp_pago_management.config.ApiException;
import mx.net.asp.asp_pago_management.dao.*;
import mx.net.asp.asp_pago_management.dto.CuentaDTO;
import mx.net.asp.asp_pago_management.model.CuentaOBJ;
import mx.net.asp.asp_pago_management.utilerias.ErrorHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@Log4j2
@RequiredArgsConstructor
public class CuentasService {

    private final ProcreaDAO procreaDAO;
    private final AhorroDAO ahorroDAO;

    private final CosultaSaldoAhorroService cosultaSaldoAhorroService;
    private final ErrorHandler errorHandler;

    @Value("${producto.id.cuenta.facil}")
    private Integer prodcutoIdCtaFacil;
    @Value("${producto.id.mi.ahorro}")
    private Integer prodcutoIdMiAhorro;

    public List<CuentaDTO> obtenerCuentasCliente(String personaId) {
        List<CuentaOBJ> cuentas = new ArrayList<>();
        try {
            // 1. Obtener todas las cuentas
            cuentas.addAll(obtenerCuentasProcrea(personaId));
            cuentas.addAll(obtenerCuentasCero(personaId));

            if (cuentas.isEmpty()) {
                throw new ApiException(
                        -100,
                        "No se encontraron cuentas vigentes para el cliente " + personaId
                );
            }

            // 2. Asignar saldo a cada cuenta
            for (CuentaOBJ cuenta : cuentas) {
                Double saldo = cosultaSaldoAhorroService.obtenerSaldoAhorroCuenta(cuenta.getCuenta());
                cuenta.setSaldo(saldo);
            }

            // 3. Ordenar por prioridad
            ordenarCuentas(cuentas);

            log.info("Cuentas ordenadas:: {}", cuentas);

            //mapear
            List<CuentaDTO> responseDTO = toResponseDTOList(cuentas);
            return responseDTO;
        } catch (Exception e) {
            errorHandler.handleException(e);
            throw e;
        }
    }

    public List<CuentaOBJ> obtenerCuentasCero(String personaId) {
        List<CuentaOBJ> resultado = new ArrayList<>();
        try {
            List<CuentaOBJ> cuentasList = ahorroDAO.obtenerListadoCuentasVigentesCeroByPersonaId(personaId, prodcutoIdCtaFacil);
            if (cuentasList != null && !cuentasList.isEmpty()) {
                log.info("Se encontraron {} cuentas cero con producto {}", cuentasList.size(), prodcutoIdCtaFacil);
                resultado.addAll(cuentasList);
            } else {
                log.info("No se encontraron cuentas cero con producto {}", prodcutoIdCtaFacil);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
        return resultado.isEmpty() ? Collections.emptyList() : resultado;
    }

    public List<CuentaOBJ> obtenerCuentasProcrea(String personaId) {
        List<CuentaOBJ> resultado = new ArrayList<>();
        try {
            List<CuentaOBJ> cuentasList = procreaDAO.obtenerListadoCuentasVigentesProcreaByPersonaId(personaId, prodcutoIdMiAhorro);
            if (cuentasList != null && !cuentasList.isEmpty()) {
                log.info("Se encontraron {} cuentas procrea con producto {}", cuentasList.size(), prodcutoIdMiAhorro);
                resultado.addAll(cuentasList);
            } else {
                log.info("No se encontraron cuentas procrea con producto {}", prodcutoIdMiAhorro);
            }
        } catch (Exception e) {
            errorHandler.handleException(e);
            return Collections.emptyList();
        }
        return resultado.isEmpty() ? Collections.emptyList() : resultado;
    }

    private void ordenarCuentas(List<CuentaOBJ> cuentas) {

        cuentas.sort((c1, c2) -> {

            // 1. Mayor saldo primero
            int comparacionSaldo = c2.getSaldo().compareTo(c1.getSaldo());
            if (comparacionSaldo != 0) {
                return comparacionSaldo;
            }

            // 2. En empate → AHORRO primero
            boolean c1EsAhorro = esAhorro(c1);
            boolean c2EsAhorro = esAhorro(c2);

            if (c1EsAhorro && !c2EsAhorro) return -1;
            if (!c1EsAhorro && c2EsAhorro) return 1;

            return 0;
        });
    }

    private boolean esAhorro(CuentaOBJ cuenta) {
        return "AHORRO".equalsIgnoreCase(cuenta.getTipoCuenta());
    }

    private static CuentaDTO toResponseDTO(CuentaOBJ obj) {
        CuentaDTO dto = new CuentaDTO();
        dto.setCuenta(obj.getCuenta());
        dto.setClabe(obj.getClabe());
        dto.setProductoId(obj.getProductoId());
        return dto;
    }

    private static List<CuentaDTO> toResponseDTOList(List<CuentaOBJ> list) {
        return list.stream()
                .map(CuentasService::toResponseDTO)
                .toList();
    }
}