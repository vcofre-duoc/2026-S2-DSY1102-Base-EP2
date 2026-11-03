package cl.duoc.fitpower.model;

/**
 * Representa a cualquier clase deportiva que ofrece el Centro de Entrenamiento FitPower.
 * Define los datos comunes y delega en cada tipo de clase el cálculo del
 * costo de la clase.
 */
public abstract class Clase {

    private String nombre;
    private int cupoMaximo;
    private int duracionMinutos;

    /**
     * Constructor sin parámetros, requerido para reconstruir objetos.
     */
    protected Clase() {
    }

    public Clase(String nombre, int cupoMaximo, int duracionMinutos) {
        setNombre(nombre);
        setCupoMaximo(cupoMaximo);
        setDuracionMinutos(duracionMinutos);
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        if (nombre == null || nombre.trim().isEmpty()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacío.");
        }
        this.nombre = nombre.trim();
    }

    public int getCupoMaximo() {
        return cupoMaximo;
    }

    public void setCupoMaximo(int cupoMaximo) {
        if (cupoMaximo < 1 || cupoMaximo > 30) {
            throw new IllegalArgumentException("El cupo máximo debe estar entre 1 y 30.");
        }
        this.cupoMaximo = cupoMaximo;
    }

    public int getDuracionMinutos() {
        return duracionMinutos;
    }

    public void setDuracionMinutos(int duracionMinutos) {
        if (duracionMinutos <= 0) {
            throw new IllegalArgumentException("La duración debe ser mayor que cero.");
        }
        this.duracionMinutos = duracionMinutos;
    }

    /**
     * Calcula el costo de la clase según las reglas propias de cada tipo de clase.
     *
     * @return costo de la clase en pesos
     */
    public abstract double calcularCosto();

    /**
     * Retorna el nombre del tipo de clase (por ejemplo, "Clase Personal" o "Clase Semipersonal").
     *
     * @return tipo de clase
     */
    public abstract String getTipo();

    @Override
    public String toString() {
        return "Nombre: " + nombre + " | Cupo: " + cupoMaximo;
    }
}
