package model.graph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public class AGraphView3D extends Group {

    private Group nodeViewGroup;
    private Group edgeViewGroup;


    public AGraphView3D() {
        edgeViewGroup = new Group();
        nodeViewGroup = new Group();
        getChildren().addAll(edgeViewGroup);
        getChildren().addAll(nodeViewGroup);

    }

    public void prepareColor() {
        for (Node node : nodeViewGroup.getChildren()) {
            ANodeView3D aNodeView3D = (ANodeView3D) node;
            Color color = aNodeView3D.getAminoAcidSymbols().getColor();
            aNodeView3D.changeColor(color);
        }

        for (Node node : edgeViewGroup.getChildren()) {
            AEdgeView3D aEdgeView3D = (AEdgeView3D) node;
            Color color = aEdgeView3D.getSource().getAminoAcidSymbols().getColor();
            aEdgeView3D.changeColor(color);
        }
    }

    public void addNodeView(ANodeView3D nodeView) {
        nodeViewGroup.getChildren().add(nodeView);
    }

    public void addEdgeView(AEdgeView3D edgeView) {
        edgeViewGroup.getChildren().add(edgeView);
    }

    public ANodeView3D getNodeViewBy(String id) {
        for (Node node : nodeViewGroup.getChildren()) {
            if (node.getId().equals(id)) {
                return (ANodeView3D) node;
            }
        }
        return null;
    }

    public AEdgeView3D getEdgeViewBy(ANodeView3D nodeView3D) {
        for (Node node : edgeViewGroup.getChildren()) {
            AEdgeView3D edgeView3D = (AEdgeView3D) node;
            if (edgeView3D.getTarget() == nodeView3D || edgeView3D.getSource() == nodeView3D) {
                return (AEdgeView3D) node;
            }
        }
        return null;
    }

    public ObservableList<Node> getEdgeViews() {
        return FXCollections.unmodifiableObservableList(edgeViewGroup.getChildren());
    }

    public boolean isMinRadius() {
        if (edgeViewGroup != null && edgeViewGroup.getChildren().size() != 0) {
            AEdgeView3D aEdgeView3D = (AEdgeView3D) edgeViewGroup.getChildren().get(0);
            return aEdgeView3D.cylinderRadius().getValue().doubleValue() <= 0.25;
        }
        return false;
    }

    public ObservableList<Node> getNodeViews() {
        return FXCollections.unmodifiableObservableList(nodeViewGroup.getChildren());
    }

    public ANodeView3D getView(String id) {
        return getNodeViewBy(id);
    }

    public void reset() {
        nodeViewGroup.getChildren().clear();
        edgeViewGroup.getChildren().clear();
    }
}
