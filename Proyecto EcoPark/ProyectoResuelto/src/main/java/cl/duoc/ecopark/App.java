package cl.duoc.ecopark;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

/**
 * Carga la vista principal de JavaFX y abre la ventana.
 * Esta clase ya está preparada; el estudiante no necesita modificarla.
 */
public class App extends Application {

    private static final String VISTA = "/cl/duoc/ecopark/view/main-view.fxml";
    private static final String CSS = "/cl/duoc/ecopark/view/ecopark.css";

    @Override
    public void start(Stage stage) throws IOException {
        // FXMLLoader transforma el FXML en los controles gráficos.
        URL urlVista = App.class.getResource(VISTA);
        if (urlVista == null) {
            throw new IOException("No se encontró la vista FXML: " + VISTA);
        }
        Parent raiz = FXMLLoader.load(urlVista);
        Scene escena = new Scene(raiz);

        // Cargamos estilos separados del FXML.
        URL urlCss = App.class.getResource(CSS);
        if (urlCss != null) {
            escena.getStylesheets().add(urlCss.toExternalForm());
        }

        stage.setTitle("EcoPark - Control de estadías");
        stage.setScene(escena);
        stage.show();
    }

    public static void iniciar(String[] args) {
        launch(args);
    }
}
