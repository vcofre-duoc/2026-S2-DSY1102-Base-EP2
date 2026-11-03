package cl.duoc.ecopark.model;

/**
 * Herencia: EstadiaAuto ES UNA Estadia.
 * Regla: $2.000 por cada hora registrada.
 */
public class EstadiaAuto extends Estadia {

    private static final int TARIFA_HORA = 2000;

    public EstadiaAuto(String patente, int horas) {
        super(patente, horas);
    }

    @Override
    public int calcularCosto() {
        return getHoras() * TARIFA_HORA;
    }

    @Override
    public String getTipo() {
        return "Auto";
    }
}
