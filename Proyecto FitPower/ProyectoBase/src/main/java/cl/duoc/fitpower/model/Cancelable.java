package cl.duoc.fitpower.model;

/**
 * Contrato que cumplen las clases que participan en la política de
 * cancelación del centro.
 */
public interface Cancelable {

    /**
     * Indica si la clase tiene una cancelación activa.
     *
     * @return true si la cancelación está activa
     */
    boolean tieneCancelacionActiva();

    /**
     * Registra que la clase activa su cancelación.
     */
    void activarCancelacion();
}
