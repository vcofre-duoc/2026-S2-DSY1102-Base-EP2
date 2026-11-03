package cl.duoc.fitpower.model;

/**
 * Clase personal que ofrece el centro. Participa en la política de cancelación.
 * Costo de la clase: base $35.000; si el cliente no cuenta con evaluación física previa, recargo del 20%.
 */
public class ClasePersonal extends Clase implements Cancelable {

    private static final double COSTO_BASE = 35000;
    private static final double RECARGO_SIN_EVALUACION = 0.20;

    private String instructor;
    private boolean evaluacionPrevia;
    private boolean cancelacionActiva;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    public ClasePersonal() {
    }

    public ClasePersonal(String nombre, int cupoMaximo, int duracionMinutos, String instructor, boolean evaluacionPrevia) {
        super(nombre, cupoMaximo, duracionMinutos);
        setInstructor(instructor);
        setEvaluacionPrevia(evaluacionPrevia);
        this.cancelacionActiva = false;
    }

    public String getInstructor() {
        return instructor;
    }

    public void setInstructor(String instructor) {
        this.instructor = instructor;
    }

    public boolean isEvaluacionPrevia() {
        return evaluacionPrevia;
    }

    public void setEvaluacionPrevia(boolean evaluacionPrevia) {
        this.evaluacionPrevia = evaluacionPrevia;
    }

    @Override
    public boolean tieneCancelacionActiva() {
        return cancelacionActiva;
    }

    @Override
    public void activarCancelacion() {
        this.cancelacionActiva = true;
    }

    @Override
    public double calcularCosto() {
        if (evaluacionPrevia) {
            return COSTO_BASE;
        }
        return COSTO_BASE + COSTO_BASE * RECARGO_SIN_EVALUACION;
    }

    @Override
    public String getTipo() {
        return "Clase Personal";
    }
}
