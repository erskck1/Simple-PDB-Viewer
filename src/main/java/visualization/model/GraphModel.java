package visualization.model;

import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableBooleanValue;
import javafx.beans.value.ObservableValue;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javafx.stage.Stage;
import model.Peptide;
import model.graph.AGraph;
import model.graph.AGraphView3D;
import model.graph.ANode;
import model.graph.ANodeView3D;
import visualization.command.CommandManager;
import visualization.plugin.SecondaryViewPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ThreadLocalRandom;

public class GraphModel {

    private final ObjectProperty<List<Peptide>> peptidesProperty = new SimpleObjectProperty<>(this, "peptides", null);
    private final ObjectProperty<Color> selectionColorProperty = new SimpleObjectProperty<>(this, "selectionColor", null);
    private final ObjectProperty<double[][]> coordinatesProperty = new SimpleObjectProperty<>(this, "coordinates", null);
    private final ObjectProperty<Rectangle> coordinatesBounds = new SimpleObjectProperty<>(this, "coordinatesBounds", new Rectangle());
    private final ObjectProperty<AminoAcidSequence> AASequenceProperty = new SimpleObjectProperty<>(this, "sequence", null);
    private final ObjectProperty<File> fileObjectProperty = new SimpleObjectProperty<>(this, "file", null);
    private final IntegerProperty selectMode = new SimpleIntegerProperty(this, "selectionMode", -1);
    private final DoubleProperty scaleProperty = new SimpleDoubleProperty(this, "scale", 17);
    private RadiusModel radiusModel;
    private ASelectionModel<ANode> nodeASelectionModel = new ASelectionModel<>();
    private final ObservableBooleanValue noSelectionMadeBinding = new BooleanBinding() {
        {
            bind(nodeASelectionModel.getSelectedIndices());

        }

        @Override
        protected boolean computeValue() {
            return nodeASelectionModel.getSelectedIndices().isEmpty();
        }
    };
    private AGraphView3D graphView3D;
    private AGraph aGraph;
    private StackPane stackPane;
    private CommandManager commandManager = new CommandManager();
    private BoundingBoxes2D boundingBoxes2D;
    private Property<Transform> graphViewTransformProperty;
    private Stage primaryStage;
    private StringProperty logger = new SimpleStringProperty();
    private Set<Integer> selectedAminoAcids = new HashSet<>();
    private int helixUp;
    private int helixDown;
    private int sheetUp;
    private int sheetDown;

    public GraphModel() {
    }

    public IntegerProperty selectMode() {
        return selectMode;
    }

    public DoubleProperty scaleProperty() {
        return scaleProperty;
    }

    public boolean isHelix(int i) {
        if (helixDown < i && i < helixUp) {
            return true;
        }
        return false;
    }

    public boolean isSheet(int i) {
        if (sheetDown < i && i < sheetUp) {
            return true;
        }
        return false;
    }

    public ObservableValue<double[][]> getCoordinatesObservable() {
        return coordinatesProperty;
    }

    public ObservableValue<Rectangle> getCoordinatesBoundsObservable() {
        return coordinatesBounds;
    }

    public void addSelectionChangeListenerFor(SecondaryViewPlugin plugin) {
        getSelectedIndicesObservableList().addListener((ListChangeListener<? super Integer>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    selectedAminoAcids.forEach(plugin::select);
                } else if (change.wasRemoved()) {
                    if (selectedAminoAcids.size() == 0) {
                        plugin.deselect(-1);
                    } else {
                        selectedAminoAcids.forEach(plugin::deselect);
                    }
                }
            }
        });
    }

    public ObjectProperty<List<Peptide>> peptidesProperty() {
        return peptidesProperty;
    }

    public RadiusModel getRadiusModel() {
        return radiusModel;
    }

    public void setRadiusModel(RadiusModel radiusModel) {
        this.radiusModel = radiusModel;
    }

    public ASelectionModel<ANode> getNodeASelectionModel() {
        return nodeASelectionModel;
    }

    public void setNodeASelectionModel(ASelectionModel<ANode> nodeASelectionModel) {
        this.nodeASelectionModel = nodeASelectionModel;
    }

    public AGraphView3D getGraphView3D() {
        return graphView3D;
    }

    public void setGraphView3D(AGraphView3D graphView3D) {
        this.graphView3D = graphView3D;
    }

    public AGraph getaGraph() {
        return aGraph;
    }

    public void setaGraph(AGraph aGraph) {
        this.aGraph = aGraph;
    }

    public StackPane getStackPane() {
        return stackPane;
    }

    public void setStackPane(StackPane stackPane) {
        this.stackPane = stackPane;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public void setCommandManager(CommandManager commandManager) {
        this.commandManager = commandManager;
    }

    public void setBoundingBoxes2D(BoundingBoxes2D boundingBoxes2D) {
        this.boundingBoxes2D = boundingBoxes2D;
    }

    public Property<Transform> getGraphViewTransformProperty() {
        return graphViewTransformProperty;
    }

    public void setGraphViewTransformProperty(Property<Transform> graphViewTransformProperty) {
        this.graphViewTransformProperty = graphViewTransformProperty;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public File getFile() {
        return fileObjectProperty.getValue();
    }

    public void setFile(File file) {
        if (file != null) {
            fileObjectProperty.setValue(file);
        }
        writeLog("File selected : " + file.getName());
    }

    public Pane getTop() {
        ObservableList<Node> childs = this.stackPane.getChildren();
        Pane pane = (Pane) childs.get(childs.size() - 1);
        return pane;
    }

    public Pane getBottom() {
        ObservableList<Node> childs = this.stackPane.getChildren();
        Pane pane = (Pane) childs.get(0);
        return pane;
    }

    public void writeLog(String message) {
        if (logger.getValue() == null) {
            logger.setValue(message);
        } else {
            logger.setValue(logger.getValue() + "\n" + message);
        }
    }

    public StringProperty getLoggerProperty() {
        return logger;
    }

    public void clearAll() {
        getBottom().getChildren().clear();
        selectedAminoAcids.clear();
        radiusModel.reset();
        if (nodeASelectionModel != null)
            nodeASelectionModel.clearSelection();
        graphView3D.reset();
        aGraph.reset();
        if (boundingBoxes2D != null)
            boundingBoxes2D.getRectangles().getChildren().clear();
        commandManager.clear();
    }

    public void selectNone() {
        selectedAminoAcids.clear();
        nodeASelectionModel.clearSelection();
    }

    public void selectAll() {
        nodeASelectionModel.selectAll();
    }

    public void handleClick(int index, MouseEvent mouseEvent) {
        // click outside of anything
        if ((index < 0) && !mouseEvent.isShiftDown()) {
            selectedAminoAcids.clear();
            nodeASelectionModel.clearSelection();
        } else {
            // click on something
            boolean isSelected = isSelected(index);
            if (mouseEvent.isShiftDown()) {
                if (isSelected) {
                    deselectAssociated(index);
                } else {
                    selectAssociated(index);
                }
            } else {
                selectedAminoAcids.clear();
                nodeASelectionModel.clearSelection();
                selectAssociated(index);
            }
        }
    }

    private void selectAssociated(int selectedSeqId) {
        List<ANode> associatedNodes = new ArrayList<>();

        for (Node nodeV : graphView3D.getNodeViews()) {
            int seqId = Integer.valueOf(nodeV.getId().split("#")[1]);
            if (seqId == selectedSeqId) {
                selectedAminoAcids.add(selectedSeqId);
                associatedNodes.add(aGraph.getNodeById(nodeV.getId()));
            }
        }

        for (ANode node1 : associatedNodes) {
            nodeASelectionModel.select(node1);
        }
    }

    private void deselectAssociated(int selectedSeqId) {
        List<ANode> associatedNodes = new ArrayList<>();

        for (Node nodeV : graphView3D.getNodeViews()) {
            int seqId = Integer.valueOf(nodeV.getId().split("#")[1]);
            if (seqId == selectedSeqId) {
                selectedAminoAcids.remove(selectedSeqId);
                associatedNodes.add(aGraph.getNodeById(nodeV.getId()));
            }
        }

        for (ANode node1 : associatedNodes) {
            nodeASelectionModel.clearSelection(node1);
        }
    }

    public boolean isSelected(int selectedSeqId) {
        if (selectedAminoAcids.contains(selectedSeqId)) {
            return true;
        }
        return false;
    }

    public ObservableBooleanValue noSelectionMadeObservable() {
        return noSelectionMadeBinding;
    }

    public ObjectProperty<Color> selectionColorProperty() {
        return selectionColorProperty;
    }

    public ObservableList<Integer> getSelectedIndicesObservableList() {
        return nodeASelectionModel.getSelectedIndices();
    }

    public void setCoordinates(double[][] coordinates) {
        if (coordinates == null) {
            coordinatesProperty.setValue(null);
            coordinatesBounds.setValue(new Rectangle());
            return;
        }
        double minX = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (double[] point : coordinates) {
            double x = point[0];
            double y = point[1];
            minX = minX < x ? minX : x;
            maxX = maxX > x ? maxX : x;
            minY = minY < y ? minY : y;
            maxY = maxY > y ? maxY : y;
        }
        double centerX = (maxX + minX) / 2.0;
        double centerY = (maxY + minY) / 2.0;
        for (double[] point : coordinates) {
            point[0] -= centerX;
            point[1] -= centerY;
        }
        coordinatesBounds.setValue(new Rectangle(minX - centerX, minY - centerY, maxX - minX, maxY - minY));
        coordinatesProperty.setValue(coordinates);
    }

    public ObjectProperty<AminoAcidSequence> AASequenceProperty() {
        return AASequenceProperty;
    }

    public ObjectProperty<File> fileObjectProperty() {
        return fileObjectProperty;
    }

    public void scale() {
        if (graphView3D == null || nodeASelectionModel == null) {
            return;
        }

        for (ANode node : nodeASelectionModel.getSelectedItems()) {
            ANodeView3D nodeView = graphView3D.getNodeViewBy(node.getId());
            double minScale = 0.000000000000001;
            nodeView.setScaleX(nodeView.getScaleX() + minScale);
            nodeView.setScaleX(nodeView.getScaleX() - minScale);
        }
    }

    public void prepareHelixSheet() {
        int size = graphView3D.getNodeViews().size();
        helixUp = ThreadLocalRandom.current().nextInt(size * 4 / 10, size * 5 / 10);
        helixDown = ThreadLocalRandom.current().nextInt(size * 2 / 10, size * 4 / 10);
        sheetUp = ThreadLocalRandom.current().nextInt(size * 8 / 10, size);
        sheetDown = ThreadLocalRandom.current().nextInt(size * 6 / 10, size * 8 / 10);
        scaleProperty.setValue(17);
    }
}
