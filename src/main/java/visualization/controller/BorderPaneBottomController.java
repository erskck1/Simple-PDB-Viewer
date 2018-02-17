package visualization.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.SplitPane;
import visualization.command.DecreaseSizeCommand;
import visualization.command.IncreaseSizeCommand;
import visualization.model.GraphModel;

public class BorderPaneBottomController implements Controller {

    @FXML
    private ToolBarController toolBarController;

    @FXML
    private ChartViewController chartViewController;

    @FXML
    private PdbViewController pdbViewController;

    @FXML
    private SplitPane splitPane;

    private GraphModel graphModel;

    @FXML
    public void increaseEdgeScale(ActionEvent event) {
        graphModel.getRadiusModel().setCylinderRadius(graphModel.getRadiusModel().getCylinderRadius() + 0.1);
    }

    @FXML
    public void decreaseEdgeScale(ActionEvent event) {
        if (graphModel.getRadiusModel().getCylinderRadius() - 0.1 >= 0.1)
            graphModel.getRadiusModel().setCylinderRadius(graphModel.getRadiusModel().getCylinderRadius() - 0.1);
    }

    @FXML
    public void increaseNodeScale(ActionEvent event) {
        IncreaseSizeCommand increaseSizeCommand = new IncreaseSizeCommand(graphModel.getGraphView3D(), graphModel.getNodeASelectionModel());
        try {
            graphModel.getCommandManager().executeAndAdd(increaseSizeCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void decreaseNodeScale(ActionEvent event) {
        DecreaseSizeCommand decreaseSizeCommand = new DecreaseSizeCommand(graphModel.getGraphView3D(), graphModel.getNodeASelectionModel());
        try {
            graphModel.getCommandManager().executeAndAdd(decreaseSizeCommand);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        toolBarController.setUp(graphModel);
        chartViewController.setUp(graphModel);
        pdbViewController.setUp(graphModel);
    }
}

