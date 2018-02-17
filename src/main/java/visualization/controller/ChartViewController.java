package visualization.controller;

import javafx.fxml.FXML;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import model.AminoAcid;
import model.AminoAcidSymbols;
import visualization.model.AminoAcidSequence;
import visualization.model.GraphModel;

import java.util.HashMap;
import java.util.Map;

public class ChartViewController implements Controller {

    @FXML
    private NumberAxis xAxis;
    @FXML
    private CategoryAxis yAxis;

    @FXML
    private BarChart<String, Number> bc;

    private GraphModel graphModel;

    @Override
    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        bc.setLegendVisible(false);
        this.graphModel.AASequenceProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null && newValue1 != oldValue1) {
                bc.getData().clear();
                xAxis.setTickLabelRotation(90);
                AminoAcidSequence aminoAcids = graphModel.AASequenceProperty().get();

                Map<AminoAcidSymbols, Integer> aminoAcidMap = new HashMap<>();
                for (AminoAcid aminoAcid : aminoAcids) {
                    Integer integer = aminoAcidMap.get(aminoAcid.getSymbol());
                    if (integer == null) {
                        integer = new Integer(1);
                        aminoAcidMap.put(aminoAcid.getSymbol(), integer);
                    } else {
                        integer = integer + 1;
                        aminoAcidMap.put(aminoAcid.getSymbol(), integer);
                    }
                }

                for (Map.Entry<AminoAcidSymbols, Integer> entry : aminoAcidMap.entrySet()) {
                    AminoAcidSymbols key = entry.getKey();
                    Integer value = entry.getValue();
                    XYChart.Series<String, Number> series1 = new XYChart.Series();
                    XYChart.Data<String, Number> e = new XYChart.Data(key.toString(), value);
                    series1.getData().add(e);
                    bc.getData().add(series1);
                }
            }

        });
    }


}
