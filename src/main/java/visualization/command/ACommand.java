package visualization.command;


import model.graph.AGraphView3D;
import model.graph.ANode;
import visualization.model.ASelectionModel;

import java.util.ArrayList;
import java.util.List;

public abstract class ACommand {

    protected List<ANode> selectedItems;
    protected AGraphView3D graphView;
    private String name;
    private String description;

    public ACommand(AGraphView3D graphView, ASelectionModel<ANode> nodeASelectionModel) {
        this.selectedItems = new ArrayList<>(nodeASelectionModel.getSelectedItems());
        this.graphView = graphView;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    abstract public void execute() throws Exception;

    abstract public void undo() throws Exception;

    abstract public void redo() throws Exception;

    public boolean isExecutable() {
        return true;
    }

    public boolean isUndoable() {
        return true;
    }

    public boolean isRedoable() {
        return true;
    }
}
