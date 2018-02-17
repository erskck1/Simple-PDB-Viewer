package visualization.plugin;

import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.AminoAcid;
import model.Peptide;
import model.graph.ANode;
import visualization.model.GraphModel;

import java.io.IOException;
import java.util.List;

public class SequenceViewPlugin extends HBox {
    private static final double FONT_SIZE = 16.0;

    @FXML
    private FlowPane sequenceBox;
    // used for selection
    private Label[] baseLabels;
    private GraphModel graphModel;
    private final ChangeListener<List<Peptide>> peptideChangeListener = (observable, oldValue, newValue) -> showSequence(newValue);

    public SequenceViewPlugin() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SequenceViewPlugin.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        setOnMouseClicked(event -> {
            this.graphModel.handleClick(-1, event);
            refreshView();
        });
        this.graphModel.peptidesProperty().addListener(peptideChangeListener);
        this.graphModel.getNodeASelectionModel().getSelectedItems().addListener(new ListChangeListener<ANode>() {
            @Override
            public void onChanged(Change<? extends ANode> c) {
                refreshView();
            }
        });
        this.graphModel.selectionColorProperty().addListener((observable, oldValue, newValue) -> refreshView());
    }

    public void select(int index) {
        Label label = baseLabels[index];
        Background background = new Background(new BackgroundFill(Color.RED, null, null));
        label.setBackground(background);
    }

    public void deselect(int index) {
        Label label = baseLabels[index];
        label.setBackground(new Background(new BackgroundFill(null, null, null)));
    }

    private void showSequence(List<Peptide> peptide) {
        sequenceBox.getChildren().clear();
        if (peptide != null) {
            int total = 0;
            for (Peptide peptide1 : peptide) {
                total = total + peptide1.getRealSize();
            }
            baseLabels = new Label[total + 1];
            int index = 1;
            for (Peptide peptide1 : peptide) {
                for (AminoAcid aminoAcid : peptide1.getSequence()) {
                    Label baseLabel = getBaseLabel(aminoAcid);
                    sequenceBox.getChildren().add(baseLabel);
                    baseLabels[index] = baseLabel;
                    ++index;
                }
            }
        }
    }

    private Label getBaseLabel(AminoAcid aminoAcid) {
        Label label = new Label();
        label.setBackground(new Background(new BackgroundFill(null, null, null)));
        label.setTextFill(aminoAcid.getSymbol().getColor());
        label.setFont(Font.font("Verdana", FontWeight.BOLD, FONT_SIZE));
        label.setOnMouseClicked(event -> {

            graphModel.handleClick(aminoAcid.getAminoAcidNumber(), event);
            refreshView();
            event.consume();
        });
        label.setText(Character.toString(aminoAcid.getSymbol().getChar()));
        return label;
    }

    private void refreshView() {
        if (baseLabels != null) {
            for (int i = 1; i < baseLabels.length; ++i) {
                if (graphModel.isSelected(i)) {
                    select(i);
                } else {
                    deselect(i);
                }
            }
        }
    }
}
