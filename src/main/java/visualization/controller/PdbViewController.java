package visualization.controller;

import javafx.beans.binding.Bindings;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.stage.FileChooser;
import visualization.command.DecreaseEdgeCommand;
import visualization.command.DecreaseSizeCommand;
import visualization.command.IncreaseEdgeCommand;
import visualization.command.IncreaseSizeCommand;
import visualization.model.GraphModel;
import visualization.utils.PDBDownloader;

import java.io.File;
import java.io.IOException;

public class PdbViewController implements Controller {

    private final FileChooser fileChooser = new FileChooser();
    @FXML
    private Button oK;
    @FXML
    private TextField pdbId;
    @FXML
    private Button selectAll;
    @FXML
    private Button selectNone;
    @FXML
    private Button edgePlus;
    @FXML
    private Button edgeMinus;
    @FXML
    private Button nodePlus;
    @FXML
    private Button nodeMinus;
    @FXML
    private Button undo;
    @FXML
    private Button redo;
    @FXML
    private Slider slider;
    @FXML
    private Label zoom;
    private GraphModel graphModel;
    private PDBDownloader downloader;

    @Override
    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        oK.setText("Download");
        configureFileChooser(fileChooser);
        selectAll.disableProperty().bind(graphModel.AASequenceProperty().isNull());
        selectNone.disableProperty().bind(graphModel.AASequenceProperty().isNull());
        edgePlus.disableProperty().bind(graphModel.AASequenceProperty().isNull().or(graphModel.selectMode().isEqualTo(0)));
        edgeMinus.disableProperty().bind(graphModel.AASequenceProperty().isNull().or(graphModel.selectMode().isEqualTo(0)));
        nodePlus.disableProperty().bind(Bindings.isEmpty(graphModel.getNodeASelectionModel().getSelectedItems()).or(graphModel.selectMode().isNotEqualTo(0).and(graphModel.selectMode().isNotEqualTo(1))));
        nodeMinus.disableProperty().bind(Bindings.isEmpty(graphModel.getNodeASelectionModel().getSelectedItems()).or(graphModel.selectMode().isNotEqualTo(0).and(graphModel.selectMode().isNotEqualTo(1))));
        undo.disableProperty().bind(graphModel.getCommandManager().canUndoProperty().not());
        redo.disableProperty().bind(graphModel.getCommandManager().canRedoProperty().not());
        slider.disableProperty().bind(graphModel.AASequenceProperty().isNull());
        zoom.disableProperty().bind(graphModel.AASequenceProperty().isNull());
        slider.setValue(graphModel.scaleProperty().getValue());
        slider.valueProperty().addListener((observable, oldValue, newValue) -> {
            graphModel.scaleProperty().setValue(newValue);
        });
        graphModel.scaleProperty().addListener((observable, oldValue, newValue) -> {
            slider.valueProperty().setValue(newValue);
            graphModel.scale();
        });
    }

    @FXML
    public void find(ActionEvent event) {
        if (pdbId.getText() != null || pdbId.getText() != "") {
            retrieveProtein(pdbId.getText());
        }
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
    public void showHelp(ActionEvent event) {
        BorderPaneController.showPopUp("PDB Viewer version 1.0 @Author : Ersoy Kocak\n" +
                "Advanced Java for Bioinformatics\n\n" +
                "View modes (bottom) : \n" +
                "Spheres : Spacing filling model,this shows how much space each atom actually occupies.\n" +
                "Ball and Stick : Show the main atoms in all of the protein’s amino acids.\n" +
                "Sticks : Show the edges.\n" +
                "Backbone :The backbone is represented in a stick drawing.\n\n" +
                "Radio buttons : \n" +
                "Atom : Show the atoms of an amino acid. Red : oxygen, Blue : nitrogen, Grey : carbon\n" +
                "Amin Acid : Show amino acids with their color.\n" +
                "Chain : Show helix/sheet amino acids. yellows : helix, reds: sheet, blues : neither helix nor sheet.\n\n" +
                "View modes and radio buttons are combined.\n\n" +
                "Other features :\n" +
                "You can also zoom using scrool down/up in 3D View.\n" +
                "You can download a PDB file with its id or select from computer.\n" +
                "You can see a progress bar on the left view before loading secondary structure.\n" +
                "In the sequence view, you can see amino acids with their color.\n" +
                "If you select one of them, the same amino acid will be alsa selected in 2D and 3D views, vice versa.\n" +
                "In the chart, you can see,what types of amino acids are there and how many.\n" +
                "You can find also sample files in project folder.\n");
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
        graphModel.selectAll();
    }

    @FXML
    public void selectNone(ActionEvent event) {
        graphModel.selectNone();
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

    @FXML
    public void loadFile(ActionEvent event) {
        File file = fileChooser.showOpenDialog(graphModel.getPrimaryStage());
        if (file != null) {
            graphModel.setFile(file);
        }
    }

    private void retrieveProtein(String id) {
        id = id.toUpperCase();

        downloader = new PDBDownloader(id);

        try {
            if (!(new File(id + ".pdb").exists())) {
                downloader.download(id);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        File file = new File(id + ".pdb");
        if (file.exists()) {
            graphModel.setFile(file);
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
