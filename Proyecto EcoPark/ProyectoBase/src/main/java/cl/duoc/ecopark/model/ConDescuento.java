package cl.duoc.ecopark.model;

/**
 * Interfaz: contrato para estadías que pueden calcular un descuento.
 * En este ejercicio solo las motocicletas implementan este contrato.
 */
public interface ConDescuento {

    // Retorna el monto descontado en pesos chilenos.
    int calcularDescuento();
}
