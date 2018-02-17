package visualization.command;



import model.graph.AGraphView3D;
import model.graph.ANode;
import visualization.model.ASelectionModel;


public class ShowFillCommand extends ACommand {

    public ShowFillCommand(AGraphView3D graphView, ASelectionModel<ANode> nodeASelectionModel) {
        super(graphView, nodeASelectionModel);
    }

    @Override
    public void execute() throws Exception {

    }

    @Override
    public void undo() throws Exception {

    }

    @Override
    public void redo() throws Exception {

    }
}
