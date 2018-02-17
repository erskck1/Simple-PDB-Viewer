package visualization.main;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import model.graph.AGraph;
import model.graph.AGraphView3D;
import visualization.command.CommandManager;
import visualization.controller.BorderPaneController;
import visualization.model.GraphModel;
import visualization.model.RadiusModel;

public class Main extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/BorderPaneView.fxml"));
        BorderPane root = loader.load();
        BorderPaneController borderPaneController = loader.getController();
        Scene scene = new Scene(root, 700, 700, true);

        scene.widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
                borderPaneController.scale();
            }
        });

        scene.heightProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
                borderPaneController.scale();
            }
        });

        borderPaneController.getMenuController().bindWith(primaryStage.widthProperty());
        prepareGraphModel(borderPaneController, primaryStage);

        primaryStage.setMaximized(true);
        primaryStage.setTitle("PDB Viewer");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void prepareGraphModel(BorderPaneController borderPaneController, final Stage primaryStage) {

        GraphModel graphModel = new GraphModel();
        graphModel.setaGraph(new AGraph());
        graphModel.setCommandManager(new CommandManager());
        graphModel.setGraphView3D(new AGraphView3D());
        graphModel.setRadiusModel(new RadiusModel());
        graphModel.setPrimaryStage(primaryStage);

        borderPaneController.setUp(graphModel);
    }

}
