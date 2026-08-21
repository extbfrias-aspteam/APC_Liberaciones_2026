package mx.net.asp.asp_pago_onb.utilerias;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class ListConceptosCuenta {
    @Value("${clave.codigo.autorizacion}")
    private String ASPP_CA;
    @Value("${clave.biometrico}")
    private String ASPP_HD;
    @Value("${clave.acepta.terminos}")
    private String ASPP_AT;
    @Value("${clave.acepta.uso.biometricos}")
    private String ASPP_AB;
    @Value("${clave.actuo.cuenta.propia}")
    private String ACTUO_CUENTA_PROPIA;
    @Value("${clave.ret.trasp.dock.carnet}")
    private String CON_RET_TRASP_DOCK_CARNET;
    @Value("${clave.sol.nombre.cliente}")
    private String SOL_NOM_CLIE;
    @Value("${concepto.nivel.cuenta}")
    private String AHO_NIVEL_CUENTA;

    private static final List<String> listConceptosProcrea = new ArrayList<>();
    private static final List<String> listConceptosCero = new ArrayList<>();

    @PostConstruct
    private void inicializarListas() {
        listConceptosProcrea.add(ASPP_CA);
        listConceptosProcrea.add(ASPP_HD);
        listConceptosProcrea.add(ASPP_AT);
        listConceptosProcrea.add(ASPP_AB);

        listConceptosCero.add(ASPP_CA);
        listConceptosCero.add(ASPP_HD);
        listConceptosCero.add(ASPP_AT);
        listConceptosCero.add(ASPP_AB);
        listConceptosCero.add(ACTUO_CUENTA_PROPIA);
        listConceptosCero.add(CON_RET_TRASP_DOCK_CARNET);
        listConceptosCero.add(SOL_NOM_CLIE);
        listConceptosCero.add(AHO_NIVEL_CUENTA);
    }

    public static List<String> getListConceptosProcrea() {
        return listConceptosProcrea;
    }
    public static List<String> getListConceptosCero() {
        return listConceptosCero;
    }
}
