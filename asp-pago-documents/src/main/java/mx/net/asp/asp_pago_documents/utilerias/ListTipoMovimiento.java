package mx.net.asp.asp_pago_documents.utilerias;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;

@Component
public class ListTipoMovimiento {
    @Value("${tipo.mov.procrea.retspei}")
    private String tipoMovProcreaRetspei;

    @Value("${tipo.mov.procrea.retspeicodi}")
    private String tipoMovProcreaRetspeiCodi;

    @Value("${tipo.mov.procrea.depspei}")
    private String tipoMovProcreaDepspei;

    @Value("${tipo.mov.procrea.depspeicodi}")
    private String tipoMovProcreaDepspeiCodi;

    @Value("${tipo.mov.procrea.devspei}")
    private String tipoMovProcreaDevspei;

    @Value("${tipo.mov.procrea.retcodi}")
    private String tipoMovProcreaRetcodi;

    @Value("${tipo.mov.procrea.depcodi}")
    private String tipoMovProcreaDepcodi;

    @Value("${tipo.mov.cero.depspei}")
    private String tipoMovCeroDepspei;

    @Value("${tipo.mov.cero.depspei.eiyu}")
    private String tipoMovCeroDepspeiEiyu;

    @Value("${tipo.mov.cero.retspei}")
    private String tipoMovCeroRetspei;

    @Value("${tipo.mov.cero.retspei.eiyu}")
    private String tipoMovCeroRetspeiEiyu;

    @Value("${tipo.mov.cero.devspei}")
    private String tipoMovCeroDevspei;

    @Value("${tipo.mov.cero.devspei.eiyu}")
    private String tipoMovCeroDevspeiEiyu;

    @Value("${tipo.mov.cero.retspeicodi}")
    private String tipoMovCeroRetspeiCodi;

    @Value("${tipo.mov.cero.depspeicodi}")
    private String tipoMovCeroDepspeiCodi;

    @Value("${tipo.mov.cero.retcodi}")
    private String tipoMovCeroRetcodi;

    @Value("${tipo.mov.cero.depcodi}")
    private String tipoMovCeroDepcodi;

    @Value("${tipo.mov.cero.devcodi.abono}")
    private String tipoMovCeroDevcodiAbono;

    @Value("${tipo.mov.cero.devcodi.cargo}")
    private String tipoMovCeroDevcodiCargo;

    @Value("${tipo.mov.procrea.rettraspaso}")
    private String tipoMovProcreaRetTraspaso;

    @Value("${tipo.mov.procrea.deptraspaso}")
    private String tipoMovProcreaDepTraspaso;

    @Value("${tipo.mov.cero.rettraspaso}")
    private String tipoMovCeroRetTraspaso;

    @Value("${tipo.mov.cero.deptraspaso}")
    private String tipoMovCeroDepTraspaso;

    @Value("${tipo.mov.cero.devtraspaso}")
    private String tipoMovCeroDevTraspaso;

    @Value("${tipo.mov.procrea.retpagoservicio}")
    private String tipoMovProcreaRetPagoServicio;

    @Value("${tipo.mov.procrea.retrecargatel}")
    private String tipoMovProcreaRetRecargaTel;

    @Value("${tipo.mov.cero.retpagoservicio}")
    private String tipoMovCeroRetPagoServicio;

    @Value("${tipo.mov.cero.retrecargatel}")
    private String tipoMovCeroRetRecargaTel;

    @Value("${tipo.mov.procrea.retcredito}")
    private String tipoMovProcreaRetCredito;

    @Value("${tipo.mov.cero.retcredito}")
    private String tipoMovCeroRetCredito;

    @Value("${tipo.mov.cero.devcredito}")
    private String tipoMovCeroDevCredito;

    @Value("${tipo.mov.procrea.devrecargatel}")
    private String tipoMovProcreaDevRecargaTel;

    @Value("${tipo.mov.procrea.devpagoservicio}")
    private String tipoMovProcreaDevPagoServicio;

    @Value("${tipo.mov.cero.devrecargatel}")
    private String tipoMovCeroDevRecargaTel;

    @Value("${tipo.mov.cero.devpagoservicio}")
    private String tipoMovCeroDevPagoServicio;

    @Value("${tipo.mov.cero.compra.pos}")
    private String tipoMovCeroCompraPos;

    @Value("${tipo.mov.cero.compra.anulacion.pos}")
    private String tipoMovCeroCompraAnulacionPos;

    @Value("${tipo.mov.cero.compra.ecommerce}")
    private String tipoMovCeroCompraEcommerce;

    @Value("${tipo.mov.cero.compra.anulacion.ecommerce}")
    private String tipoMovCeroCompraAnulacionEcommerce;

    @Value("${tipo.mov.cero.compra.compra.cashback}")
    private String tipoMovCeroCompraCompraCashback;

    @Value("${tipo.mov.cero.compra.anulacion.cashback}")
    private String tipoMovCeroCompraAnulacionCashback;

    @Value("${tipo.mov.cero.compra.contactless}")
    private String tipoMovCeroCompraContactless;

    @Value("${tipo.mov.cero.compra.anulacion.contactless}")
    private String tipoMovCeroCompraAnulacionContactless;

    // Listas para agrupar los valores
    private static final List<String> listMovimientosCoDiSpei = new ArrayList<>();
    private static final List<String> listMovimientosPagoServicios = new ArrayList<>();
    private static final List<String> listMovimientosDevPagoServicios = new ArrayList<>();
    private static final List<String> listMovimientosTraspasos = new ArrayList<>();
    private static final List<String> listMovimientosCredito = new ArrayList<>();
    private static final List<String> listMovimientosTarjeta = new ArrayList<>();

    @PostConstruct
    private void inicializarListas() {
        // Lista de movimientos CoDi Spei
        listMovimientosCoDiSpei.add(tipoMovProcreaRetspei);
        listMovimientosCoDiSpei.add(tipoMovProcreaRetspeiCodi);
        listMovimientosCoDiSpei.add(tipoMovProcreaDepspei);
        listMovimientosCoDiSpei.add(tipoMovProcreaDepspeiCodi);
        listMovimientosCoDiSpei.add(tipoMovProcreaDevspei);
        listMovimientosCoDiSpei.add(tipoMovProcreaRetcodi);

        listMovimientosCoDiSpei.add(tipoMovCeroDepspei);
        listMovimientosCoDiSpei.add(tipoMovCeroDepspeiEiyu);
        listMovimientosCoDiSpei.add(tipoMovCeroRetspei);
        listMovimientosCoDiSpei.add(tipoMovCeroRetspeiEiyu);
        listMovimientosCoDiSpei.add(tipoMovCeroDevspei);
        listMovimientosCoDiSpei.add(tipoMovCeroDevspeiEiyu);
        listMovimientosCoDiSpei.add(tipoMovCeroRetspeiCodi);
        listMovimientosCoDiSpei.add(tipoMovCeroDepspeiCodi);
        listMovimientosCoDiSpei.add(tipoMovCeroRetcodi);
        listMovimientosCoDiSpei.add(tipoMovCeroDepcodi);
        listMovimientosCoDiSpei.add(tipoMovCeroDevcodiAbono);
        listMovimientosCoDiSpei.add(tipoMovCeroDevcodiCargo);

        // Lista de movimientos Pago Servicios
        listMovimientosPagoServicios.add(tipoMovProcreaRetPagoServicio);
        listMovimientosPagoServicios.add(tipoMovProcreaRetRecargaTel);

        listMovimientosPagoServicios.add(tipoMovCeroRetPagoServicio);
        listMovimientosPagoServicios.add(tipoMovCeroRetRecargaTel);

        // Lista de movimientos Dev Pago Servicios
        listMovimientosDevPagoServicios.add(tipoMovProcreaDevRecargaTel);
        listMovimientosDevPagoServicios.add(tipoMovProcreaDevPagoServicio);

        listMovimientosDevPagoServicios.add(tipoMovCeroDevRecargaTel);
        listMovimientosDevPagoServicios.add(tipoMovCeroDevPagoServicio);

        // Lista de movimientos Traspasos
        listMovimientosTraspasos.add(tipoMovProcreaRetTraspaso);
        listMovimientosTraspasos.add(tipoMovProcreaDepTraspaso);

        listMovimientosTraspasos.add(tipoMovCeroRetTraspaso);
        listMovimientosTraspasos.add(tipoMovCeroDepTraspaso);
        listMovimientosTraspasos.add(tipoMovCeroDevTraspaso);

        // Lista de movimientos Credito
        listMovimientosCredito.add(tipoMovProcreaRetCredito);

        listMovimientosCredito.add(tipoMovCeroRetCredito);
        listMovimientosCredito.add(tipoMovCeroDevCredito);

        // Lista de movimientos Tarjeta
        listMovimientosTarjeta.add(tipoMovCeroCompraPos);
        listMovimientosTarjeta.add(tipoMovCeroCompraAnulacionPos);
        listMovimientosTarjeta.add(tipoMovCeroCompraEcommerce);
        listMovimientosTarjeta.add(tipoMovCeroCompraAnulacionEcommerce);
        listMovimientosTarjeta.add(tipoMovCeroCompraCompraCashback);
        listMovimientosTarjeta.add(tipoMovCeroCompraAnulacionCashback);
        listMovimientosTarjeta.add(tipoMovCeroCompraContactless);
        listMovimientosTarjeta.add(tipoMovCeroCompraAnulacionContactless);
    }

    public static List<String> getMovimientosCoDiSpei() {
        return listMovimientosCoDiSpei;
    }

    public static List<String> getMovimientosPagoServicios() {
        return listMovimientosPagoServicios;
    }

    public static List<String> getMovimientosDevPagoServicios() {
        return listMovimientosDevPagoServicios;
    }

    public static List<String> getMovimientosTraspasos() {
        return listMovimientosTraspasos;
    }

    public static List<String> getMovimientosCredito() {
        return listMovimientosCredito;
    }

    public static List<String> getMovimientosTarjeta() {
        return listMovimientosTarjeta;
    }

}
