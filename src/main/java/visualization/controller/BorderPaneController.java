package visualization.controller;

import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBoxBuilder;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.graph.ANode;
import visualization.model.GraphModel;
import visualization.plugin.SecondaryViewPlugin;
import visualization.plugin.SequenceViewPlugin;

public class BorderPaneController implements Controller {

    public static final ANode[] EMPTY_NUCLEOTIDE_ARRAY = new ANode[0];
    private GraphModel graphModel;
    @FXML
    private SequenceViewPlugin sequenceView;

    @FXML
    private SecondaryViewPlugin secondaryView;

    @FXML
    private MenuController menuViewController;

    @FXML
    private VisualizationController visualizationViewController;

    @FXML
    private BorderPaneBottomController borderPaneBottomViewController;

    public static void showProgress(String text) {
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.WINDOW_MODAL);

        Button okButton = new Button("CLOSE");
        okButton.setOnAction((event) -> {
            myDialog.close();
        });
        LoadingView loadingView = new LoadingView();
        Scene myDialogScene = new Scene(VBoxBuilder.create()
                .children(loadingView).alignment(Pos.CENTER)
                .padding(new Insets(10)).build());
        myDialog.setScene(myDialogScene);
        myDialog.show();
    }

    public static void x() {

    }

    public static void showPopUp(String text) {
        final Stage myDialog = new Stage();
        myDialog.initModality(Modality.WINDOW_MODAL);

        Button okButton = new Button("CLOSE");
        okButton.setOnAction((event) -> {
            myDialog.close();
        });

        Scene myDialogScene = new Scene(VBoxBuilder.create()
                .children(new Text(text), okButton).alignment(Pos.CENTER)
                .padding(new Insets(10)).build());

        myDialog.setScene(myDialogScene);
        myDialog.show();
    }

    @Override
    public void setUp(GraphModel graphModel) {
        graphModel.AASequenceProperty().setValue(null);
        graphModel.setCoordinates(null);
        graphModel.getNodeASelectionModel().setItems(EMPTY_NUCLEOTIDE_ARRAY);
        graphModel.selectionColorProperty().setValue(Color.color(0.2, 0.2, 0.2, 0.4));
        this.graphModel = graphModel;
        visualizationViewController.setUp(graphModel);
        borderPaneBottomViewController.setUp(graphModel);
        menuViewController.setUp(graphModel);
        sequenceView.setUp(graphModel);
        secondaryView.setUp(graphModel);
    }

    public void scale() {
        graphModel.scale();
    }

    public MenuController getMenuController() {
        return menuViewController;
    }

    public BorderPaneBottomController getBorderPaneBottomViewController() {
        return borderPaneBottomViewController;
    }
}
