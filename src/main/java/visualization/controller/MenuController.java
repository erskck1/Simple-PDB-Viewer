package visualization.controller;

import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.stage.FileChooser;
import visualization.command.DecreaseEdgeCommand;
import visualization.command.DecreaseSizeCommand;
import visualization.command.IncreaseEdgeCommand;
import visualization.command.IncreaseSizeCommand;
import visualization.model.GraphModel;

import java.io.File;

public class MenuController implements Controller {

    private final FileChooser fileChooser = new FileChooser();
    private GraphModel graphModel;
    @FXML
    private MenuItem undo;

    @FXML
    private MenuItem redo;

    @FXML
    private MenuBar menuBar;

    @FXML
    private MenuItem edgePlus;

    @FXML
    private MenuItem edgeMinus;

    @FXML
    private MenuItem nodePlus;

    @FXML
    private MenuItem nodeMinus;

    @Override
    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        undo.disableProperty().bind(graphModel.getCommandManager().canUndoProperty().not());
        redo.disableProperty().bind(graphModel.getCommandManager().canRedoProperty().not());
        edgePlus.disableProperty().bind(graphModel.AASequenceProperty().isNull().or(graphModel.selectMode().isEqualTo(0)));
        edgeMinus.disableProperty().bind(graphModel.AASequenceProperty().isNull().or(graphModel.selectMode().isEqualTo(0)));
        nodePlus.disableProperty().bind(Bindings.isEmpty(graphModel.getNodeASelectionModel().getSelectedItems()).or(graphModel.selectMode().isNotEqualTo(0).and(graphModel.selectMode().isNotEqualTo(1))));
        nodeMinus.disableProperty().bind(Bindings.isEmpty(graphModel.getNodeASelectionModel().getSelectedItems()).or(graphModel.selectMode().isNotEqualTo(0).and(graphModel.selectMode().isNotEqualTo(1))));
        configureFileChooser(fileChooser);
    }

    @FXML
    public void close(ActionEvent event) {
        Platform.exit();
    }

    @FXML
    public void undo(ActionEvent event) {
        try {
            graphModel.getCommandManager().undo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void redo(ActionEvent event) {
        try {
            graphModel.getCommandManager().redo();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void selectAll(ActionEvent event) {
        graphModel.getNodeASelectionModel().selectAll();
    }

    @FXML
    public void selectNone(ActionEvent event) {
        graphModel.getNodeASelectionModel().clearSelection();
    }

    @FXML
    public void selectLocalFile(ActionEvent event) {
        File file = fileChooser.showOpenDialog(graphModel.getPrimaryStage());
        if (file != null) {
            graphModel.setFile(file);
        }
    }

    public void bindWith(ReadOnlyDoubleProperty readOnlyDoubleProperty) {
        menuBar.prefWidthProperty().bind(readOnlyDoubleProperty);
    }

    @FXML
    public void edgePlus(ActionEvent event) {
        IncreaseEdgeCommand increaseEdgeCommand = new IncreaseEdgeCommand(graphModel.getGraphView3D(), graphModel.getNodeASelectionModel());
        try {
            graphModel.getCommandManager().executeAndAdd(increaseEdgeCommand);
        } catch (Exception e) {

        }
    }

    @FXML
    public void edgeMinus(ActionEvent event) {
        if (graphModel.getGraphView3D().isMinRadius()) {
            return;
        }
        DecreaseEdgeCommand decreaseEdgeCommand = new DecreaseEdgeCommand(graphModel.getGraphView3D(), graphModel.getNodeASelectionModel());
        try {
            graphModel.getCommandManager().executeAndAdd(decreaseEdgeCommand);
        } catch (Exception e) {

        }
    }

    @FXML
    public void nodePlus(ActionEvent event) {
        IncreaseSizeCommand increaseSizeCommand = new IncreaseSizeCommand(graphModel.getGraphView3D(), graphModel.getNodeASelectionModel());
        try {
            graphModel.getCommandManager().executeAndAdd(increaseSizeCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void nodeMinus(ActionEvent event) {
        DecreaseSizeCommand decreaseSizeCommand = new DecreaseSizeCommand(graphModel.getGraphView3D(), graphModel.getNodeASelectionModel());
        try {
            graphModel.getCommandManager().executeAndAdd(decreaseSizeCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void configureFileChooser(
            final FileChooser fileChooser) {
        fileChooser.setTitle("Select a PDB file");
        fileChooser.setInitialDirectory(
                new File(System.getProperty("user.home"))
        );
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDB", "*.pdb")
        );
    }
}
