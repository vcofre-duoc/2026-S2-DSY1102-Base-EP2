package cl.duoc.ecopark.model;

/**
 * Herencia e interfaz: EstadiaMoto ES UNA Estadia
 * y PUEDE tener un descuento si posee convenio.
 * Regla: $1.000 por hora, con 20% de descuento si hay convenio.
 */
public class EstadiaMoto extends Estadia implements ConDescuento {

    private static final int TARIFA_HORA = 1000;
    private boolean convenio;

    public EstadiaMoto(String patente, int horas, boolean convenio) {
        super(patente, horas);
        this.convenio = convenio;
    }

    public boolean isConvenio() {
        return convenio;
    }

    public void setConvenio(boolean convenio) {
        this.convenio = convenio;
    }

    @Override
    public int calcularDescuento() {
        int subtotal = getHoras() * TARIFA_HORA;
        return convenio ? subtotal * 20 / 100 : 0;
    }

    @Override
    public int calcularCosto() {
        return getHoras() * TARIFA_HORA - calcularDescuento();
    }

    @Override
    public String getTipo() {
        return "Moto";
    }
}
