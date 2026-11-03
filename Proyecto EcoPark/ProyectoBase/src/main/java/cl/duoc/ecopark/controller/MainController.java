package cl.duoc.ecopark.controller;

import cl.duoc.ecopark.dao.JsonEstadiaDAO;
import cl.duoc.ecopark.model.Estadia;
import cl.duoc.ecopark.model.EstadiaAuto;
import cl.duoc.ecopark.model.EstadiaMoto;
import cl.duoc.ecopark.repository.EstadiaRepository;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import java.nio.file.Path;

/**
 * CONTROLLER - A COMPLETAR POR EL ESTUDIANTE.
 * Recibe los eventos del FXML y coordina el Repository con la tabla.
 *
 * IMPORTANTE: el FXML base todavía NO tiene fx:controller, fx:id ni onAction.
 * Esto es intencional para que los estudiantes realicen la vinculación.
 */
public class MainController {

    // Colección que luego se conectará con TableView.
    private final ObservableList<Estadia> filas =
            FXCollections.observableArrayList();

    // Repository recibe un DAO encargado de la persistencia JSON.
    private final EstadiaRepository repository =
            new EstadiaRepository(
                    new JsonEstadiaDAO(Path.of("datos", "estadias.json"))
            );

    /*
     * TODO A: declarar atributos @FXML con los mismos nombres que
     * se agreguen como fx:id en la vista FXML.
     * Ejemplos de nombres sugeridos en GUIA_DESARROLLO.md.
     */

    /** TODO B: cargar tipos, configurar columnas y cargar registros JSON. */
    @FXML
    private void initialize() {
        // TODO: iniciar formulario y TableView.
    }

    /** TODO C: leer, validar, crear subtipo, registrar y actualizar tabla. */
    @FXML
    private void registrar() {
        // TODO: implementar acción del botón Registrar.
    }

    /** TODO D: eliminar fila seleccionada y actualizar tabla. */
    @FXML
    private void eliminar() {
        // TODO: implementar acción del botón Eliminar.
    }
}
