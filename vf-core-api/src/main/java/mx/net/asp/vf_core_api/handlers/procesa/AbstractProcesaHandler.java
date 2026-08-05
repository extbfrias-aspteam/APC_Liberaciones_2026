package mx.net.asp.vf_core_api.handlers.procesa;

public abstract class AbstractProcesaHandler implements ProcesaHandler {

    @Override
    public boolean canHandle(String proceso) {
        return getCodigo().equals(proceso);
    }

    protected abstract String getCodigo();
}
