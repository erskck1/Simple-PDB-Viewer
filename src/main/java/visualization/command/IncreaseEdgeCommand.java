package visualization.command;

import javafx.scene.Node;
import model.graph.AEdgeView3D;
import model.graph.AGraphView3D;
import model.graph.ANode;
import visualization.model.ASelectionModel;

public class IncreaseEdgeCommand extends ACommand {

    public IncreaseEdgeCommand(AGraphView3D graphView, ASelectionModel<ANode> nodeASelectionModel) {
        super(graphView, nodeASelectionModel);
    }

    @Override
    public void execute() throws Exception {
        increase();
    }

    private void increase() {
        for (Node node : graphView.getEdgeViews()) {
            AEdgeView3D edgeView3D = (AEdgeView3D) node;
            edgeView3D.cylinderRadius().setValue(edgeView3D.cylinderRadius().getValue() + 0.1);
        }
    }

    @Override
    public void undo() throws Exception {
        if (graphView.isMinRadius()) {
            return;
        }
        for (Node node : graphView.getEdgeViews()) {
            AEdgeView3D edgeView3D = (AEdgeView3D) node;
            edgeView3D.cylinderRadius().setValue(edgeView3D.cylinderRadius().getValue() - 0.1);
        }
    }

    @Override
    public void redo() throws Exception {
        increase();
    }
}
