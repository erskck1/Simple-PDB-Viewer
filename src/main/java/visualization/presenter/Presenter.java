package visualization.presenter;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import model.Peptide;
import model.graph.*;
import visualization.model.ASelectionModel;
import visualization.model.AminoAcidSequence;
import visualization.model.GraphModel;

import java.util.ArrayList;
import java.util.List;

public class Presenter {
    private AGraph graph;
    private AGraphView3D graphView;
    private ASelectionModel<ANode> nodeSelectionModel;
    private GraphModel graphModel;

    public Presenter(GraphModel graphModel) {
        this.graph = graphModel.getaGraph();
        this.graphView = graphModel.getGraphView3D();
        this.nodeSelectionModel = graphModel.getNodeASelectionModel();
        this.graphModel = graphModel;
    }

    public void readGraph(String filePath) {
        Peptide pep = graph.read(filePath);
        AminoAcidSequence aminoAcidSequence = new AminoAcidSequence(pep.getSequence());
        graphModel.AASequenceProperty().setValue(aminoAcidSequence);
        addNodes();
        addEdges();
        graphModel.prepareHelixSheet();
    }

    private void addEdges() {
        for (AEdge edge : graphModel.getaGraph().getEdges()) {
            ANodeView3D source = graphModel.getGraphView3D().getNodeViewBy(edge.getSource().getId());
            ANodeView3D target = graphModel.getGraphView3D().getNodeViewBy(edge.getTarget().getId());
            AEdgeView3D edgeView = new AEdgeView3D(edge.getId(), source, target);
            graphModel.getGraphView3D().addEdgeView(edgeView);
        }
    }

    private void addNodes() {
        for (ANode node : graphModel.getaGraph().getNodes()) {
            ANodeView3D nodeView;
            Color color;
            double radius;
            if (node.getSymbol().equals("C") || node.getSymbol().equals("CA") || node.getSymbol().equals("CB")) {
                color = Color.GREY;
                radius = 0.44;
            } else if (node.getSymbol().equals("O")) {
                color = Color.RED;
                radius = 0.7;
            } else {
                color = Color.BLUE;
                radius = 0.55;
            }
            nodeView = new ANodeView3D(node.getId(), node.getX(), node.getY(), node.getZ(), color, node.getSeqId(), node.getSymbol(), node.getAminoAcidSymbols());
            nodeView.setRadius(radius);
            nodeView.setOnMouseClicked(event -> {
                graphModel.handleClick(node.getSeqId(), event);
            });
            graphView.addNodeView(nodeView);
        }
    }

    private void selectAssociated(ANode node) {
        List<ANode> associatedNodes = new ArrayList<>();
        nodeSelectionModel.select(node);

        for (Node nodeV : graphView.getNodeViews()) {
            int seqId = Integer.valueOf(nodeV.getId().split("#")[1]);
            if (seqId == node.getSeqId()) {
                associatedNodes.add(graph.getNodeById(nodeV.getId()));
            }
        }

        for (ANode node1 : associatedNodes) {
            nodeSelectionModel.select(node1);
        }
    }

}
