package cl.duoc.fitpower.persistence;

import cl.duoc.fitpower.model.Clase;
import java.io.IOException;
import java.util.List;

/** Contrato de archivos sin referencias a JavaFX ni al controlador. */
public interface ClaseDao {
    List<Clase> leer() throws IOException;
    // Cada operación modifica un solo subtipo: una sustitución de archivo es suficiente.
    void guardarTipo(List<Clase> clases, Class<? extends Clase> tipo) throws IOException;
}
