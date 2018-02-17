package visualization.command;

import model.graph.AGraphView3D;
import model.graph.ANode;
import model.graph.ANodeView3D;
import visualization.model.ASelectionModel;

public class IncreaseSizeCommand extends ACommand {

    public IncreaseSizeCommand(AGraphView3D graphView, ASelectionModel<ANode> nodeASelectionModel) {
        super(graphView, nodeASelectionModel);
    }

    @Override
    public void execute() throws Exception {
        increase();
    }

    private void increase() {
        for (ANode node : selectedItems) {
            ANodeView3D nodeView = graphView.getNodeViewBy(node.getId());
            nodeView.setScaleX(nodeView.getScaleX() * 110 / 100);
            nodeView.setScaleY(nodeView.getScaleY() * 110 / 100);
            nodeView.setScaleZ(nodeView.getScaleZ() * 110 / 100);
        }
    }

    @Override
    public void undo() throws Exception {
        for (ANode node : selectedItems) {
            ANodeView3D nodeView = graphView.getNodeViewBy(node.getId());
            nodeView.setScaleX(nodeView.getScaleX() * 90 / 100);
            nodeView.setScaleY(nodeView.getScaleY() * 90 / 100);
            nodeView.setScaleZ(nodeView.getScaleZ() * 90 / 100);
        }
    }

    @Override
    public void redo() throws Exception {
        increase();
    }
}
