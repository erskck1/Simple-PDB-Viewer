package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;
import model.Peptide;
import model.graph.AEdgeView3D;
import model.graph.ANode;
import model.graph.ANodeView3D;
import parser.FileReader;
import visualization.model.BoundingBoxes2D;
import visualization.model.GraphModel;
import visualization.presenter.Presenter;

import java.util.List;
import java.util.Random;

public class ToolBarController implements Controller {

    private static int seconds = 0;
    private GraphModel graphModel;
    private Presenter presenter;
    @FXML
    private ComboBox<String> modeSelect;
    @FXML
    private RadioButton radioAtom;
    /**
     * The radio aminoacid.
     */
    @FXML
    private RadioButton radioAminoacid;
    /**
     * The radio chain.
     */
    @FXML
    private RadioButton radioChain;
    private ToggleGroup scopeRadioGroup;
    private Random random = new Random();

    public void start3D() {
        graphModel.clearAll();
        radioAtom.setSelected(true);
        setAtomColor();
        setEdgeColor();
        presenter = new Presenter(graphModel);
        presenter.readGraph(graphModel.getFile().getPath());
        List<Peptide> peptides = FileReader.readPeptidesFromFile(graphModel.getFile().getPath());
        graphModel.peptidesProperty().setValue(peptides);

        BoundingBoxes2D boundingBoxes2D = new BoundingBoxes2D(graphModel.getBottom(), graphModel.getGraphView3D(), graphModel.getNodeASelectionModel(), graphModel.getGraphViewTransformProperty());
        graphModel.getTop().getChildren().addAll(boundingBoxes2D.getRectangles());

        graphModel.getNodeASelectionModel().setItems(graphModel.getaGraph().getNodesAsArray());
        graphModel.setBoundingBoxes2D(boundingBoxes2D);
        graphModel.writeLog(".... view showed");
    }

    private void showOnlySticks() {
        for (Node node : graphModel.getGraphView3D().getNodeViews()) {
            node.setVisible(false);
        }
        for (Node node : graphModel.getGraphView3D().getEdgeViews()) {
            node.setVisible(true);
        }
    }

    @Override
    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        modeSelect.disableProperty().bind(graphModel.fileObjectProperty().isNull());
        radioAtom.disableProperty().bind(graphModel.fileObjectProperty().isNull());
        radioAtom.disableProperty().bind(graphModel.selectMode().isNotEqualTo(0).and(graphModel.selectMode().isNotEqualTo(1)));
        radioAminoacid.disableProperty().bind(graphModel.fileObjectProperty().isNull());
        radioChain.disableProperty().bind(graphModel.fileObjectProperty().isNull());
        graphModel.selectMode().addListener(((observable, oldValue, newValue) -> {
            if (newValue.intValue() == 2 || newValue.intValue() == 3) {
                radioAminoacid.setSelected(true);
                graphModel.getGraphView3D().prepareColor();
            }
        }));
        modeSelect.getItems().addAll("Spheres", "Ball and Stick", "Sticks",
                "Backbone");
        modeSelect.setOnAction((event) -> {
            int selection = modeSelect.getSelectionModel().getSelectedIndex();

            switch (selection) {
                case 0:
                    graphModel.selectMode().set(selection);
                    spheres();
                    graphModel.scale();
                    break;

                case 1:
                    graphModel.selectMode().set(selection);
                    ballAndSticks();
                    graphModel.scale();
                    break;

                case 2:
                    graphModel.selectMode().set(selection);
                    showOnlySticks();
                    break;

                case 3:
                    graphModel.selectMode().set(selection);
                    backbone();
                    break;
                case 4:
                    break;
                default:
                    graphModel.selectMode().setValue(-1);
                    break;
            }

        });

        this.graphModel.fileObjectProperty().addListener((observable, oldValue, newValue) -> {
            if (oldValue != newValue) {
                graphModel.clearAll();
                start3D();
                modeSelect.getSelectionModel().select(1);
            }
        });

        scopeRadioGroup = new ToggleGroup();
        radioAtom.setToggleGroup(scopeRadioGroup);
        radioAtom.setSelected(true);
        radioAminoacid.setToggleGroup(scopeRadioGroup);
        radioChain.setToggleGroup(scopeRadioGroup);


        radioAtom.setOnAction((event) -> {
            setAtomColor();
            setEdgeColor();
        });

        radioAminoacid.setOnAction((event) -> {
            graphModel.getGraphView3D().prepareColor();
        });

        radioChain.setOnAction(event -> {
            int size = graphModel.getGraphView3D().getNodeViews().size();

            for (int i = 0; i < size; i++) {
                ANodeView3D nodeView3D = (ANodeView3D) graphModel.getGraphView3D().getNodeViews().get(i);
                if (graphModel.isHelix(i)) {
                    nodeView3D.changeColor(Color.YELLOW);
                } else if (graphModel.isSheet(i)) {
                    nodeView3D.changeColor(Color.RED);
                } else {
                    nodeView3D.changeColor(Color.BLUE);
                }
            }

            for (int i = 0; i < graphModel.getGraphView3D().getEdgeViews().size(); i++) {
                AEdgeView3D edgeView3D = (AEdgeView3D) graphModel.getGraphView3D().getEdgeViews().get(i);
                int i1 = graphModel.getGraphView3D().getNodeViews().indexOf(edgeView3D.getSource());
                int i2 = graphModel.getGraphView3D().getNodeViews().indexOf(edgeView3D.getTarget());

                if (graphModel.isHelix(i1) || graphModel.isHelix(i2)) {
                    edgeView3D.changeColor(Color.YELLOW);
                } else if (graphModel.isSheet(i1) || graphModel.isSheet(i2)) {
                    edgeView3D.changeColor(Color.RED);
                } else {
                    edgeView3D.changeColor(Color.BLUE);
                }
            }
        });

    }

    private void setAtomColor() {
        for (ANode node : graphModel.getaGraph().getNodes()) {
            ANodeView3D nodeViewBy = graphModel.getGraphView3D().getNodeViewBy(node.getId());
            if (node.getSymbol().equals("C") || node.getSymbol().equals("CA") || node.getSymbol().equals("CB")) {
                nodeViewBy.changeColor(Color.GREY);
            } else if (node.getSymbol().equals("O")) {
                nodeViewBy.changeColor(Color.RED);
            } else {
                nodeViewBy.changeColor(Color.BLUE);
            }
        }

    }

    private void setEdgeColor() {
        for (int i = 0; i < graphModel.getGraphView3D().getEdgeViews().size(); i++) {
            AEdgeView3D edgeView3D = (AEdgeView3D) graphModel.getGraphView3D().getEdgeViews().get(i);
            edgeView3D.changeColor(Color.GOLD);
        }
    }

    private void backbone() {
        for (Node node : graphModel.getGraphView3D().getNodeViews()) {
            node.setVisible(false);
        }

        for (ANode node : graphModel.getaGraph().getNodes()) {
            if (node.getSymbol().equals("CB") || node.getSymbol().equals("O")) {
                ANodeView3D nodeViewBy = graphModel.getGraphView3D().getNodeViewBy(node.getId());
                AEdgeView3D edgeViewBy = graphModel.getGraphView3D().getEdgeViewBy(nodeViewBy);
                edgeViewBy.setVisible(false);
            }
        }
    }

    private void ballAndSticks() {
        for (ANode node : graphModel.getaGraph().getNodes()) {
            ANodeView3D nodeViewBy = graphModel.getGraphView3D().getNodeViewBy(node.getId());
            if (node.getSymbol().equals("C") || node.getSymbol().equals("CA") || node.getSymbol().equals("CB")) {
                nodeViewBy.setRadius(0.44f);
            } else if (node.getSymbol().equals("O")) {
                nodeViewBy.setRadius(0.7f);
            } else {
                nodeViewBy.setRadius(0.55f);
            }
            nodeViewBy.setVisible(true);
        }
        for (Node node : graphModel.getGraphView3D().getEdgeViews()) {
            node.setVisible(true);
        }
    }

    private void spheres() {
        for (ANode node : graphModel.getaGraph().getNodes()) {
            ANodeView3D nodeViewBy = graphModel.getGraphView3D().getNodeViewBy(node.getId());
            if (node.getSymbol().equals("C") || node.getSymbol().equals("CA") || node.getSymbol().equals("CB")) {
                nodeViewBy.setRadius(1.1f);
            } else if (node.getSymbol().equals("O")) {
                nodeViewBy.setRadius(1.5f);
            } else {
                nodeViewBy.setRadius(1.3f);
            }
            nodeViewBy.setVisible(true);
        }

    }
}
