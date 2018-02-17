package visualization.controller;

import javafx.concurrent.Service;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.BorderPane;
import visualization.model.GraphModel;

import java.io.IOException;

public class LoadingView extends BorderPane implements Controller {

    @FXML
    ProgressBar progressBar;
    @FXML
    Label messageLabel;
    @FXML
    Button cancelButton;

    private GraphModel graphModel;

    public LoadingView() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/LoadingView.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void attachService(Service<?> service) {
        progressBar.progressProperty().bind(service.progressProperty());
        messageLabel.textProperty().bind(service.messageProperty());
        cancelButton.setOnAction(event -> service.cancel());
    }

    @Override
    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
    }
}
