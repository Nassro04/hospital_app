package com.example.hospital_system;

import com.example.hospital_system.Controllers.FactureDetailsController;
import com.example.hospital_system.Controllers.FactureFormController;
import com.example.hospital_system.Controllers.FactureListController;
import com.example.hospital_system.Models.Client;
import com.example.hospital_system.Models.Invoice;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;
import java.io.IOException;
import java.util.function.Consumer;

public class SceneManager {

    private static Stage mainStage;

    public static void setStage(Stage stage) {
        mainStage = stage;
    }

    // Method to change scene
    public static void changeScene(String fxmlName) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlName));
        Parent root = loader.load();
        if (mainStage.getScene() == null) {
            mainStage.setScene(new Scene(root));
        } else {
            mainStage.getScene().setRoot(root);
        }
        mainStage.show();
    }

    // Simple modal method
    public static void openModal(String fxmlPath, String title) throws IOException {
        openModal(fxmlPath, title, false, null, mainStage);
    }

    // Methode b 5-parameters
    public static <T> void openModal(String fxmlPath, String title,
            Consumer<T> controllerConfig, Window owner) throws IOException {
        openModal(fxmlPath, title, false, controllerConfig, owner);
    }

    // Methode b 5-parameters
    public static <T> void openModal(String fxmlPath, String title, boolean resizable,
            Consumer<T> controllerConfig, Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        // Configure controller if provided
        if (controllerConfig != null) {
            T controller = loader.getController();
            controllerConfig.accept(controller);
        }

        Stage modalStage = new Stage();
        modalStage.setTitle(title);

        // Modal behavior
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(owner != null ? owner : mainStage);

        modalStage.setScene(new Scene(root));
        modalStage.setResizable(resizable);

        // Block until closed
        modalStage.showAndWait();
    }

    public static <T> void openModal(String fxmlPath, String title,
            Consumer<T> controllerConfig) throws IOException {
        openModal(fxmlPath, title, false, controllerConfig, mainStage);
    }

    public static void openModal(String fxmlPath, String title,
            Window owner) throws IOException {
        openModal(fxmlPath, title, false, null, owner);
    }

    // Method bel hieght w width
    public static <T> void openModal(String fxmlPath, String title, double width, double height,
            boolean resizable, Consumer<T> controllerConfig,
            Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        if (controllerConfig != null) {
            T controller = loader.getController();
            controllerConfig.accept(controller);
        }

        Stage modalStage = new Stage();
        modalStage.setTitle(title);
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(owner != null ? owner : mainStage);

        Scene scene = new Scene(root, width, height);
        modalStage.setScene(scene);
        modalStage.setResizable(resizable);
        modalStage.showAndWait();
    }

    public static <T> T openModalWithController(String fxmlPath, String title,
            Consumer<T> controllerConfig,
            Window owner) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        T controller = loader.getController();

        if (controllerConfig != null) {
            controllerConfig.accept(controller);
        }

        Stage modalStage = new Stage();
        modalStage.setTitle(title);
        modalStage.initModality(Modality.WINDOW_MODAL);
        modalStage.initOwner(owner != null ? owner : mainStage);
        modalStage.setScene(new Scene(root));
        modalStage.showAndWait(); // Dialog blocks here until closed

        return controller; // Return controller AFTER dialog closes
    }

    public static void changeScene(String fxmlPath, Object data, Node anyNodeInScene) throws IOException {
        FXMLLoader loader = new FXMLLoader(SceneManager.class.getResource(fxmlPath));
        Parent root = loader.load();

        // Get controller
        Object controller = loader.getController();

        // Inject data if controller supports it
        if (controller instanceof FactureDetailsController && data instanceof Invoice) {
            ((FactureDetailsController) controller).setInvoice((Invoice) data);
        } else if (controller instanceof FactureListController && data instanceof Client) {
            ((FactureListController) controller).setClient((Client) data);
        } else if (controller instanceof FactureFormController && data instanceof Client) {
            ((FactureFormController) controller).setClient((Client) data);
        } else if (controller instanceof FactureFormController && data instanceof Invoice) {
            ((FactureFormController) controller).setInvoice((Invoice) data);
        }
        // Add more else-if blocks here for other controllers that need data

        Stage stage = (Stage) anyNodeInScene.getScene().getWindow();
        stage.getScene().setRoot(root);
        // stage.show(); // Not strictly needed if already showing, but harmless.
    }

}