package visualization.plugin;

import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Group;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.transform.Translate;
import model.AminoAcid;
import visualization.controller.LoadingView;
import visualization.model.AminoAcidSequence;
import visualization.model.GraphModel;
import visualization.service.ProgressBarService;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.stream.Collectors;

public class SecondaryViewPlugin extends BorderPane {

    private static final double AA_RADIUS = 7;

    // to move the graph to the center of the view
    private final Translate translation = new Translate(0, 0);
    private final ProgressBarService progressBarService = new ProgressBarService();
    private final Map<AminoAcid, AminoAcidIcon> aaIconHashMap = new HashMap<>();

    @FXML
    private Pane objectsPane;

    @FXML
    private Pane mainView;

    @FXML
    private Group objectsGroup;

    @FXML
    private Pane nothingLoadedPane;

    @FXML
    private LoadingView loadingView;

    private ObservableValue<double[][]> coordinatesObservable;
    private DoubleBinding scaleBinding;
    private GraphModel graphModel;

    public SecondaryViewPlugin() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("/view/SecondaryViewPlugin.fxml"));
        fxmlLoader.setRoot(this);
        fxmlLoader.setController(this);
        try {
            fxmlLoader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
        loadingView.setVisible(false);
        objectsGroup.getTransforms().addAll(translation);

        loadingView.attachService(progressBarService);
        progressBarService.setOnScheduled(event -> {
            loadingView.setVisible(true);
            setCenter(loadingView);

        });
        progressBarService.setOnSucceeded(event -> {
            graphModel.setCoordinates(progressBarService.getValue());
            setCenter(mainView);
            loadingView.setVisible(false);
        });
        progressBarService.setOnCancelled(event -> {
            graphModel.setCoordinates(null);
            setCenter(nothingLoadedPane);
            loadingView.setVisible(false);
        });
    }

    public void setUp(GraphModel graphModel) {
        this.graphModel = graphModel;
        registerModel();
    }

    public void select(int index) {
        if (progressBarService.getState() != Worker.State.SUCCEEDED) {
            return;
        }
        aaIconHashMap.get(graphModel.AASequenceProperty().get().getByIndex(index - 1)).select();
    }

    public void deselect(int index) {
        if (progressBarService.getState() != Worker.State.SUCCEEDED) {
            return;
        }
        if (index == -1) {
            Iterator it = aaIconHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                aaIconHashMap.get(pair.getKey()).deselect();
            }
        } else {
            AminoAcid byIndex = graphModel.AASequenceProperty().get().getByIndex(index);
            Iterator it = aaIconHashMap.entrySet().iterator();
            while (it.hasNext()) {
                Map.Entry pair = (Map.Entry) it.next();
                if (pair.getKey() != byIndex) {
                    aaIconHashMap.get(pair.getKey()).deselect();
                }
            }
        }

    }

    public void registerModel() {
        this.graphModel.addSelectionChangeListenerFor(this);
        coordinatesObservable = graphModel.getCoordinatesObservable();
        coordinatesObservable.addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                drawStructure(graphModel.AASequenceProperty().get());
            }
        });
        this.graphModel.AASequenceProperty().addListener((observable1, oldValue1, newValue1) -> {
            if (newValue1 != null && newValue1 != oldValue1) {
                progressBarService.setEdges(newValue1.getBonds());
                progressBarService.setNumberOfNodes(newValue1.length());
                progressBarService.setInitialCoordinates(newValue1.getCoordinates());
                progressBarService.setIterations(300);
                load2dStructure();
            }
        });
        scaleBinding = new DoubleBinding() {
            {
                bind(graphModel.getCoordinatesBoundsObservable());
                bind(objectsPane.widthProperty());
                bind(objectsPane.heightProperty());
            }

            @Override
            protected double computeValue() {
                double heightScaling = objectsPane.getHeight() / graphModel.getCoordinatesBoundsObservable().getValue().getHeight();
                double widthScaling = objectsPane.getWidth() / graphModel.getCoordinatesBoundsObservable().getValue().getWidth();
                return Math.min(heightScaling, widthScaling);
            }
        };
        // move to center
        translation.xProperty().bind(new BoundsAttributeBinding() {
            @Override
            protected double computeValue() {
                Rectangle bounds = graphModel.getCoordinatesBoundsObservable().getValue();
                return objectsPane.getWidth() / 2.0 - (bounds.getX() + bounds.getWidth() / 2.0);
            }
        });
        // move to center
        translation.yProperty().bind(new BoundsAttributeBinding() {
            @Override
            protected double computeValue() {
                Rectangle bounds = graphModel.getCoordinatesBoundsObservable().getValue();
                return objectsPane.getHeight() / 2.0 - (bounds.getY() + bounds.getHeight() / 2.0);
            }
        });
    }

    private DoubleBinding createPositionBinding(double referencePosition) {
        return new DoubleBinding() {
            {
                bind(scaleBinding);
            }

            @Override
            protected double computeValue() {
                return referencePosition * scaleBinding.get();
            }
        };
    }

    @FXML
    private void load2dStructure() {
        progressBarService.reset();
        progressBarService.start();

    }

    private void drawStructure(AminoAcidSequence sequence) {
        objectsGroup.getChildren().clear();
        aaIconHashMap.clear();
        double[][] coordinates = coordinatesObservable.getValue();
        if (sequence == null || coordinates == null) {
            return;
        }

        double edgeVisibilityDistance = Math.max(1.0, sequence.length() * 0.1);
        for (AminoAcid aminoacid : sequence) {
            int index = sequence.getIndex(aminoacid.getAminoAcidNumber());
            Circle icon = new Circle(AA_RADIUS);
            icon.setFill(aminoacid.getSymbol().getColor());
            // bind the value, so that the entire structure scales with the canvas, but the circles stay the same size
            icon.centerXProperty().bind(createPositionBinding(coordinates[index][0]));
            icon.centerYProperty().bind(createPositionBinding(coordinates[index][1]));

            icon.setOnMouseClicked(event -> {
                if (event.getButton() == MouseButton.PRIMARY) {
                    graphModel.handleClick(aminoacid.getAminoAcidNumber(), event);
                    event.consume();
                }
                if (event.getButton() == MouseButton.SECONDARY) {
                    graphModel.handleClick(aminoacid.getAminoAcidNumber(), event);
                    event.consume();
                }
            });
            icon.getStyleClass().add("aminoacid-circle");
            aaIconHashMap.put(aminoacid, new AminoAcidIcon(icon, aminoacid, new SimpleObjectProperty<>(this, "Color", aminoacid.getSymbol().getColor())));
        }
        // draw all bonds
        for (int bondIndex = 0; bondIndex < sequence.getNumberOfBonds(); ++bondIndex) {
            int start = sequence.getBond(bondIndex)[0];
            int end = sequence.getBond(bondIndex)[1];
            AminoAcidIcon startIcon = aaIconHashMap.get(sequence.getByIndex(start));
            AminoAcidIcon endIcon = aaIconHashMap.get(sequence.getByIndex(end));
            Line edge = new Line();
            edge.setStrokeLineCap(StrokeLineCap.ROUND);
            edge.getStyleClass().add("bond");
            if (Math.abs(start - end) == 1) {
                // binding for the bond visibility
                edge.strokeProperty().bind(new ObjectBinding<Paint>() {
                    {
                        bind(startIcon.distanceToSelectedProperty());
                        bind(endIcon.distanceToSelectedProperty());
                    }

                    @Override
                    protected Paint computeValue() {
                        return Color.RED;
                    }
                });
            }
            edge.startXProperty().bind(startIcon.getIcon().centerXProperty());
            edge.startYProperty().bind(startIcon.getIcon().centerYProperty());
            edge.endXProperty().bind(endIcon.getIcon().centerXProperty());
            edge.endYProperty().bind(endIcon.getIcon().centerYProperty());
            objectsGroup.getChildren().add(edge);
        }
        objectsGroup.getChildren().addAll(aaIconHashMap.values().stream().map(AminoAcidIcon::getIcon).collect(Collectors.toList()));
    }

    @FXML
    private void mouseClicked(MouseEvent event) {
        graphModel.handleClick(-1, event);
    }

    private final class AminoAcidIcon {
        private final Circle icon;
        @SuppressWarnings("ThisEscapedInObjectConstruction")
        private final BooleanProperty selectedProperty = new SimpleBooleanProperty(this, "selected", false);
        // this property is needed to calculate the opacity of adjacent edges
        @SuppressWarnings("ThisEscapedInObjectConstruction")
        private final IntegerProperty distanceToSelectedProperty = new SimpleIntegerProperty(this, "distanceToSelected", 0);

        private AminoAcidIcon(Circle shape, AminoAcid aminoAcid, ObservableValue<Color> normalColor) {
            icon = shape;

            icon.fillProperty().bind(new ObjectBinding<Color>() {
                {
                    bind(graphModel.noSelectionMadeObservable());
                    bind(selectedProperty);
                    bind(graphModel.selectionColorProperty());
                    bind(normalColor);
                }

                @Override
                protected Color computeValue() {
                    // if this shape is not selected, but another one is
                    if (!isSelected() && !graphModel.noSelectionMadeObservable().get()) {
                        return graphModel.selectionColorProperty().get();
                    }
                    return normalColor.getValue();
                }
            });
            // the stroke changes depending on selection state
            icon.strokeProperty().bind(new ObjectBinding<Paint>() {
                {
                    bind(graphModel.noSelectionMadeObservable());
                    bind(selectedProperty);
                    bind(graphModel.selectionColorProperty());
                }

                @Override
                protected Paint computeValue() {
                    // if this shape is not selected, but another one is
                    if (!isSelected() && !graphModel.noSelectionMadeObservable().get()) {
                        return graphModel.selectionColorProperty().get();
                    }
                    return Color.BLACK;
                }
            });

            distanceToSelectedProperty.bind(new IntegerBinding() {
                {
                    bind(graphModel.getSelectedIndicesObservableList());
                }

                @Override
                protected int computeValue() {
                    int distance = Integer.MAX_VALUE;
                    if (graphModel.getSelectedIndicesObservableList().isEmpty()) {
                        return 0;
                    }
                    for (int selectedIndex : graphModel.getSelectedIndicesObservableList()) {
                        distance = Math.min(distance, Math.abs(aminoAcid.getAminoAcidNumber() - graphModel.AASequenceProperty().get().getSequenceNumber(selectedIndex)));
                    }
                    return distance;
                }
            });
        }

        public int getDistanceToSelectedProperty() {
            return distanceToSelectedProperty.get();
        }

        public IntegerProperty distanceToSelectedProperty() {
            return distanceToSelectedProperty;
        }

        public void select() {
            selectedProperty.set(true);
        }

        public void deselect() {
            selectedProperty.set(false);
        }

        @SuppressWarnings("BooleanMethodIsAlwaysInverted")
        public boolean isSelected() {
            return selectedProperty.get();
        }

        public Circle getIcon() {
            return icon;
        }
    }

    private abstract class BoundsAttributeBinding extends DoubleBinding {
        {
            bind(graphModel.getCoordinatesBoundsObservable());
            bind(objectsPane.widthProperty());
            bind(objectsPane.heightProperty());
        }
    }
}
