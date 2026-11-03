package cl.duoc.fitpower.model;

/**
 * Clase semipersonal que ofrece el centro.
 * Costo de la clase: base $18.000; si la cantidad de participantes supera 3, recargo del 10%.
 */
public class ClaseSemipersonal extends Clase {

    private static final double COSTO_BASE = 18000;
    private static final double RECARGO_PARTICIPANTES = 0.10;
    private static final int LIMITE_PARTICIPANTES = 3;

    private int participantes;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public ClaseSemipersonal() {
    }

    public ClaseSemipersonal(String nombre, int cupoMaximo, int duracionMinutos, int participantes) {
        super(nombre, cupoMaximo, duracionMinutos);
        setParticipantes(participantes);
    }

    public int getParticipantes() {
        return participantes;
    }

    public void setParticipantes(int participantes) {
        this.participantes = participantes;
    }

    @Override
    public double calcularCosto() {
        if (participantes > LIMITE_PARTICIPANTES) {
            return COSTO_BASE + COSTO_BASE * RECARGO_PARTICIPANTES;
        }
        return COSTO_BASE;
    }

    @Override
    public String getTipo() {
        return "Clase Semipersonal";
    }
}
