package cl.duoc.fitpower.controller;

import cl.duoc.fitpower.model.*;
import cl.duoc.fitpower.persistence.JsonClaseDao;
import cl.duoc.fitpower.repository.ClaseRepository;
import javafx.beans.property.*;
import javafx.collections.*;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;

/** Coordina controles y eventos; los cálculos pertenecen al modelo y los archivos al DAO. */
public final class MainController {
    @FXML private ComboBox<String> tipoCombo;
    @FXML private TextField nombreField, cupoField, duracionField, instructorField, participantesField;
    @FXML private CheckBox evaluacionCheck, cancelacionCheck;
    @FXML private TableView<Clase> clasesTable;
    @FXML private TableColumn<Clase, String> tipoCol, nombreCol, costoCol;
    @FXML private TableColumn<Clase, Number> cupoCol, duracionCol;
    @FXML private Label mensajeLabel;

    // La misma ObservableList vive durante toda la ejecución; nunca se reasigna a la tabla.
    private final ObservableList<Clase> filas = FXCollections.observableArrayList();
    private final ClaseRepository repository = new ClaseRepository(new JsonClaseDao(Path.of(".")));

    @FXML private void initialize() {
        tipoCombo.getItems().setAll("Clase Personal", "Clase Semipersonal");
        tipoCombo.valueProperty().addListener((obs, anterior, actual) -> actualizarTipo());
        actualizarTipo();
        tipoCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getTipo()));
        nombreCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(c.getValue().getNombre()));
        cupoCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getCupoMaximo()));
        duracionCol.setCellValueFactory(c -> new ReadOnlyIntegerWrapper(c.getValue().getDuracionMinutos()));
        NumberFormat moneda = NumberFormat.getCurrencyInstance(Locale.forLanguageTag("es-CL"));
        moneda.setMaximumFractionDigits(0);
        costoCol.setCellValueFactory(c -> new ReadOnlyStringWrapper(moneda.format(c.getValue().calcularCosto())));
        clasesTable.setItems(filas);
        clasesTable.setPlaceholder(new Label("No hay clases registradas."));
        try {
            repository.cargar();
            filas.setAll(repository.listar());
            informar("Datos cargados. Registre una clase para comenzar.", false);
        } catch (IOException ex) { informar(ex.getMessage(), true); }
    }

    private void actualizarTipo() {
        boolean personal = "Clase Personal".equals(tipoCombo.getValue());
        instructorField.setDisable(!personal);
        evaluacionCheck.setDisable(!personal);
        cancelacionCheck.setDisable(!personal);
        participantesField.setDisable(!"Clase Semipersonal".equals(tipoCombo.getValue()));
    }

    @FXML private void registrar() {
        try {
            Clase clase = leerFormulario();
            repository.registrar(clase); // Primero disco; si falla, formulario y tabla quedan intactos.
            filas.setAll(repository.listar());
            limpiarFormulario();
            informar(clase.getNombre() + " (" + clase.getTipo() + ") registrada correctamente.", false);
        } catch (IllegalArgumentException | IOException ex) { informar(ex.getMessage(), true); }
    }

    private Clase leerFormulario() {
        String tipo = tipoCombo.getValue();
        if (tipo == null) throw new IllegalArgumentException("Tipo: seleccione una clase.");
        String nombre = requerido(nombreField, "Nombre");
        int cupo = entero(cupoField, "Cupo máximo", 1, 30);
        int duracion = entero(duracionField, "Duración", 1, Integer.MAX_VALUE);
        if ("Clase Personal".equals(tipo)) {
            ClasePersonal c = new ClasePersonal(nombre, cupo, duracion,
                requerido(instructorField, "Instructor"), evaluacionCheck.isSelected());
            if (cancelacionCheck.isSelected()) c.activarCancelacion();
            return c;
        }
        // No se leen ni se validan los controles del otro subtipo.
        return new ClaseSemipersonal(nombre, cupo, duracion,
            entero(participantesField, "Participantes", 1, Integer.MAX_VALUE));
    }
    private String requerido(TextField campo, String nombre) {
        if (campo.getText() == null || campo.getText().isBlank()) {
            campo.requestFocus();
            throw new IllegalArgumentException(nombre + ": no puede estar vacío ni contener solo espacios.");
        }
        return campo.getText().trim();
    }
    private int entero(TextField campo, String nombre, int minimo, int maximo) {
        String texto = requerido(campo, nombre);
        try {
            int valor = Integer.parseInt(texto);
            if (valor < minimo || valor > maximo) throw new NumberFormatException();
            return valor;
        } catch (NumberFormatException ex) {
            campo.requestFocus();
            throw new IllegalArgumentException(nombre + (maximo == Integer.MAX_VALUE
                ? ": debe ser un entero mayor que cero y no superar 2147483647."
                : ": debe ser un número entero entre " + minimo + " y " + maximo + "."));
        }
    }
    @FXML private void eliminar() {
        Clase seleccionada = clasesTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) { informar("Seleccione una clase para eliminar.", true); return; }
        try {
            repository.eliminar(seleccionada);
            filas.setAll(repository.listar());
            informar(seleccionada.getNombre() + " (" + seleccionada.getTipo() + ") eliminada correctamente.", false);
        } catch (IOException | IllegalArgumentException ex) { informar(ex.getMessage(), true); }
    }
    private void limpiarFormulario() {
        nombreField.clear(); cupoField.clear(); duracionField.clear();
        instructorField.clear(); participantesField.clear();
        evaluacionCheck.setSelected(false); cancelacionCheck.setSelected(false);
        tipoCombo.getSelectionModel().clearSelection();
        actualizarTipo(); nombreField.requestFocus();
    }
    private void informar(String texto, boolean error) {
        mensajeLabel.setText(texto);
        mensajeLabel.setStyle(error ? "-fx-text-fill: #a51c30;" : "-fx-text-fill: #17643a;");
    }
}
