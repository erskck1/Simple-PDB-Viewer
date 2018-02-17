package visualization.controller;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.PerspectiveCamera;
import javafx.scene.SceneAntialiasing;
import javafx.scene.SubScene;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import visualization.model.GraphModel;

public class VisualizationController implements Controller {

    private final double VIEWPORT_SIZE = 500;
    @FXML
    private StackPane stackPane;
    private Property<Transform> graphViewTransformProperty = new SimpleObjectProperty<>(new Rotate());
    private double mouseOldX, mouseOldY = 0;
    private Rotate rotateX = new Rotate(0, Rotate.X_AXIS);
    private Rotate rotateY = new Rotate(0, Rotate.Y_AXIS);
    private Rotate rotateZ = new Rotate(0, Rotate.Z_AXIS);
    private Group group = new Group();
    private Pane topPane;
    private Pane bottomPane;

    @Override
    public void setUp(GraphModel graphModel) {
        topPane = new Pane();
        bottomPane = new Pane();
        stackPane.getChildren().addAll(topPane, bottomPane);
        graphModel.setStackPane(stackPane);

        setPerspectiveCamera();

        group.setTranslateX(VIEWPORT_SIZE / 2);
        group.setTranslateY(VIEWPORT_SIZE / 2);
        group.setTranslateZ(VIEWPORT_SIZE / 2);
        group.scaleXProperty().bind(graphModel.scaleProperty());
        group.scaleYProperty().bind(graphModel.scaleProperty());
        group.scaleZProperty().bind(graphModel.scaleProperty());

        this.group.getChildren().add(graphModel.getGraphView3D());
        this.group.setRotationAxis(Rotate.X_AXIS);
        this.group.setRotate(200);

        stackPane.setOnScroll(event -> {
            if (graphModel.AASequenceProperty().isNotNull().get()) {
                double zoomFactor = -0.3;
                double deltaY = event.getDeltaY();
                if (deltaY < 0) {
                    zoomFactor = 0.3;
                }

                graphModel.scaleProperty().setValue(graphModel.scaleProperty().get() + zoomFactor);
            }
        });
        stackPane.setOnZoomStarted(event -> System.out.println("xxxx"));
        graphModel.setGraphViewTransformProperty(graphViewTransformProperty);
    }

    public void setPerspectiveCamera() {
        SubScene subs = new SubScene(group, 0, 0, true, SceneAntialiasing.BALANCED);
        PerspectiveCamera camera = new PerspectiveCamera(false);
        camera.setTranslateX(100);
        camera.setTranslateY(0);
        camera.setTranslateZ(100);
        camera.getTransforms().addAll(rotateX, rotateY, new Translate(0, 0, 0));
        camera.setFarClip(Double.MAX_VALUE);
        camera.setNearClip(0.1);
        subs.setCamera(camera);
        subs.setFill(Color.DARKGREY);

        bottomPane.getChildren().add(subs);
        subs.widthProperty().bind(stackPane.widthProperty());
        subs.heightProperty().bind(stackPane.heightProperty());

        rotateX.setPivotX(VIEWPORT_SIZE / 2);
        rotateX.setPivotY(VIEWPORT_SIZE / 2);
        rotateX.setPivotZ(VIEWPORT_SIZE / 2);

        rotateY.setPivotX(VIEWPORT_SIZE / 2);
        rotateY.setPivotY(VIEWPORT_SIZE / 2);
        rotateY.setPivotZ(VIEWPORT_SIZE / 2);

        rotateZ.setPivotX(VIEWPORT_SIZE / 2);
        rotateZ.setPivotY(VIEWPORT_SIZE / 2);
        rotateZ.setPivotZ(VIEWPORT_SIZE / 2);

        bottomPane.setOnMousePressed(event -> {
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
        });

        bottomPane.setOnMouseDragged(event -> {
            rotateX.setAngle(rotateX.getAngle() - (event.getSceneY() - mouseOldY));
            rotateY.setAngle(rotateY.getAngle() + (event.getSceneX() - mouseOldX));
            mouseOldX = event.getSceneX();
            mouseOldY = event.getSceneY();
            graphViewTransformProperty.setValue(
                    rotateX.createConcatenation(graphViewTransformProperty.getValue())
            );
        });
    }
}
