package visualization.command;

import javafx.scene.Node;
import model.graph.AEdgeView3D;
import model.graph.AGraphView3D;
import model.graph.ANode;
import visualization.model.ASelectionModel;

public class DecreaseEdgeCommand extends ACommand {

    public DecreaseEdgeCommand(AGraphView3D graphView, ASelectionModel<ANode> nodeASelectionModel) {
        super(graphView, nodeASelectionModel);
    }

    @Override
    public void execute() throws Exception {
        decrease();
    }

    private void decrease() {
        if (graphView.isMinRadius()) {
            return;
        }
        for (Node node : graphView.getEdgeViews()) {
            AEdgeView3D edgeView3D = (AEdgeView3D) node;
            edgeView3D.cylinderRadius().setValue(edgeView3D.cylinderRadius().getValue() - 0.1);
        }
    }

    @Override
    public void undo() throws Exception {
        for (Node node : graphView.getEdgeViews()) {
            AEdgeView3D edgeView3D = (AEdgeView3D) node;
            edgeView3D.cylinderRadius().setValue(edgeView3D.cylinderRadius().getValue() + 0.1);
        }
    }

    @Override
    public void redo() throws Exception {
        decrease();
    }
}
