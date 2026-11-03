package cl.duoc.ecopark.controller;

import cl.duoc.ecopark.dao.JsonEstadiaDAO;
import cl.duoc.ecopark.model.Estadia;
import cl.duoc.ecopark.model.EstadiaAuto;
import cl.duoc.ecopark.model.EstadiaMoto;
import cl.duoc.ecopark.repository.EstadiaRepository;

import javafx.beans.property.ReadOnlyIntegerWrapper;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;

import java.io.IOException;
import java.nio.file.Path;
import java.text.NumberFormat;
import java.util.Locale;

/**
 * CONTROLLER: recibe los eventos desde FXML y coordina el formulario,
 * la tabla y el Repository. No lee ni escribe el JSON directamente.
 */
public class MainController {

    // FXML: cada atributo coincide con el fx:id declarado en main-view.fxml.
    @FXML private ComboBox<String> tipoCombo;
    @FXML private TextField patenteField;
    @FXML private TextField horasField;
    @FXML private CheckBox convenioCheck;
    @FXML private TableView<Estadia> estadiasTable;
    @FXML private TableColumn<Estadia, String> tipoCol;
    @FXML private TableColumn<Estadia, String> patenteCol;
    @FXML private TableColumn<Estadia, Number> horasCol;
    @FXML private TableColumn<Estadia, String> costoCol;
    @FXML private Label mensajeLabel;

    // ObservableList: el TableView observa cambios en esta colección.
    private final ObservableList<Estadia> filas =
            FXCollections.observableArrayList();

    // Repository administra registros; DAO se encarga del archivo JSON.
    private final EstadiaRepository repository =
            new EstadiaRepository(
                    new JsonEstadiaDAO(Path.of("datos", "estadias.json"))
            );

    // Formato de pesos chilenos usado al mostrar costos.
    private final NumberFormat formatoPesos =
            NumberFormat.getIntegerInstance(Locale.forLanguageTag("es-CL"));

    /**
     * INITIALIZE: JavaFX ejecuta este método después de leer el FXML.
     * Configuramos los controles y recuperamos los registros guardados.
     */
    @FXML
    private void initialize() {
        // Carga las opciones del selector de tipo.
        tipoCombo.getItems().addAll("Auto", "Moto");
        tipoCombo.getSelectionModel().selectFirst();

        // El convenio solo existe para motocicletas.
        tipoCombo.valueProperty().addListener((obs, anterior, actual) ->
                actualizarConvenio());
        actualizarConvenio();

        // Cada columna muestra un atributo o comportamiento de Estadia.
        // No preguntamos si el objeto es Auto o Moto para mostrarlo.
        tipoCol.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(dato.getValue().getTipo()));
        patenteCol.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(dato.getValue().getPatente()));
        horasCol.setCellValueFactory(dato ->
                new ReadOnlyIntegerWrapper(dato.getValue().getHoras()));
        costoCol.setCellValueFactory(dato ->
                new ReadOnlyStringWrapper(formatearCosto(dato.getValue().calcularCosto())));

        // Entregamos la lista observable a la tabla una sola vez.
        estadiasTable.setItems(filas);

        try {
            // Al iniciar recuperamos las estadías desde el JSON.
            repository.cargar();
            actualizarTabla();
            mostrarMensaje("Estadías cargadas: " + filas.size() + ".", false);
        } catch (IOException | RuntimeException e) {
            // No cerramos la ventana si el archivo tiene un problema.
            mostrarMensaje("No se pudieron cargar los datos: " + e.getMessage(), true);
        }
    }

    /** Activa/desactiva el descuento según el tipo elegido. */
    private void actualizarConvenio() {
        boolean esMoto = "Moto".equals(tipoCombo.getValue());
        convenioCheck.setDisable(!esMoto);
        if (!esMoto) {
            convenioCheck.setSelected(false);
        }
    }

    /**
     * REGISTRAR: lee los controles, valida los datos, crea el objeto
     * y solicita al Repository que lo agregue y persista.
     */
    @FXML
    private void registrar() {
        try {
            String tipo = tipoCombo.getValue();
            String patente = patenteField.getText().trim();

            if (tipo == null) {
                throw new IllegalArgumentException("Seleccione Auto o Moto.");
            }
            if (patente.isEmpty()) {
                throw new IllegalArgumentException("La patente es obligatoria.");
            }

            // Si escriben letras en horas, capturamos NumberFormatException.
            int horas = Integer.parseInt(horasField.getText().trim());

            // Polimorfismo: referencia Estadia con el subtipo correspondiente.
            Estadia nueva;
            if ("Auto".equals(tipo)) {
                nueva = new EstadiaAuto(patente, horas);
            } else if ("Moto".equals(tipo)) {
                nueva = new EstadiaMoto(patente, horas, convenioCheck.isSelected());
            } else {
                throw new IllegalArgumentException("Tipo de vehículo desconocido.");
            }

            // Repository valida patentes duplicadas y guarda mediante DAO.
            repository.registrar(nueva);
            actualizarTabla();
            limpiarFormulario();
            mostrarMensaje(nueva.getTipo() + " " + nueva.getPatente()
                    + " registrada. Costo: " + formatearCosto(nueva.calcularCosto()), false);

        } catch (NumberFormatException e) {
            mostrarMensaje("Las horas deben ser un número entero entre 1 y 24.", true);
        } catch (IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (IOException e) {
            mostrarMensaje("No se pudo guardar el archivo JSON: " + e.getMessage(), true);
        }
    }

    /**
     * ELIMINAR: utiliza la fila seleccionada y delega el borrado
     * al Repository, que además actualiza el JSON.
     */
    @FXML
    private void eliminar() {
        Estadia seleccionada = estadiasTable.getSelectionModel().getSelectedItem();
        if (seleccionada == null) {
            mostrarMensaje("Seleccione una estadía de la tabla para eliminar.", true);
            return;
        }

        try {
            repository.eliminar(seleccionada);
            actualizarTabla();
            mostrarMensaje("Estadía " + seleccionada.getPatente() + " eliminada.", false);
        } catch (IllegalArgumentException e) {
            mostrarMensaje(e.getMessage(), true);
        } catch (IOException e) {
            mostrarMensaje("No se pudo actualizar el JSON: " + e.getMessage(), true);
        }
    }

    /** Sincroniza la ObservableList con los datos del Repository. */
    private void actualizarTabla() {
        filas.setAll(repository.listar());
    }

    /** Deja el formulario preparado para otro registro. */
    private void limpiarFormulario() {
        patenteField.clear();
        horasField.clear();
        convenioCheck.setSelected(false);
        tipoCombo.getSelectionModel().selectFirst();
        patenteField.requestFocus();
    }

    /** Muestra mensajes de éxito o error con ayuda de una clase CSS. */
    private void mostrarMensaje(String mensaje, boolean error) {
        mensajeLabel.setText(mensaje);
        mensajeLabel.getStyleClass().removeAll("mensaje-error", "mensaje-exito");
        mensajeLabel.getStyleClass().add(error ? "mensaje-error" : "mensaje-exito");
    }

    /** Formato visual: 4000 -> $4.000. */
    private String formatearCosto(int costo) {
        return "$" + formatoPesos.format(costo);
    }
}
