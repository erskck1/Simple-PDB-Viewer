package visualization.model;

import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.Property;
import javafx.collections.ListChangeListener;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import model.graph.AGraphView3D;
import model.graph.ANode;
import model.graph.ANodeView3D;

import java.util.HashMap;
import java.util.Map;

/**
 * maintains 2D bounding boxes for 3D node views
 * Daniel Huson, 11.2017
 */
public class BoundingBoxes2D {
    private final Group rectangles = new Group();
    private final Map<ANode, Rectangle> node2rectangle = new HashMap<>();

    /**
     * constructor
     *
     * @param graphView
     * @param properties
     */
    public BoundingBoxes2D(Pane bottomPane, AGraphView3D graphView, ASelectionModel<ANode> nodeSelectionModel, Property... properties) {
        nodeSelectionModel.getSelectedItems().addListener((ListChangeListener<ANode>) c -> {
            while (c.next()) {
                for (ANode node : c.getRemoved()) {
                    rectangles.getChildren().remove(node2rectangle.get(node));
                    node2rectangle.remove(node);
                }
                for (ANode node : c.getAddedSubList()) {
                    final ANodeView3D nodeView = graphView.getView(node.getId());
                    final Rectangle rect = createBoundingBoxWithBinding(bottomPane, nodeView, properties);
                    node2rectangle.put(node, rect);
                    rectangles.getChildren().add(rect);
                }
            }
        });
    }

    /**
     * create a bounding box that is bound to user determined transformations
     */
    private static Rectangle createBoundingBoxWithBinding(Pane pane, Node node, final Property... properties) {
        final Rectangle boundingBox = new Rectangle();
        boundingBox.setStroke(Color.GOLDENROD);
        boundingBox.setStrokeWidth(2);
        boundingBox.setFill(Color.TRANSPARENT);
        boundingBox.setMouseTransparent(true);
        boundingBox.setVisible(true);

        final ObjectBinding<Rectangle> binding = new ObjectBinding<Rectangle>() {
            {
                bind(properties);
                bind(node.translateXProperty());
                bind(node.translateYProperty());
                bind(node.translateZProperty());
                bind(node.scaleXProperty());
            }

            @Override
            protected Rectangle computeValue() {
                return computeRectangle(pane, node);
            }
        };

        binding.addListener((c, o, n) -> {
            boundingBox.setX(n.getX());
            boundingBox.setY(n.getY());
            boundingBox.setWidth(n.getWidth());
            boundingBox.setHeight(n.getHeight());
        });
        boundingBox.setUserData(binding);

        binding.invalidate();
        return boundingBox;
    }

    private static Rectangle computeRectangle(Pane pane, Node node) {
        try {
            final Bounds boundsOnScreen = node.localToScreen(node.getBoundsInLocal());
            final Bounds paneBoundsOnScreen = pane.localToScreen(pane.getBoundsInLocal());
            final double xInScene = boundsOnScreen.getMinX() - paneBoundsOnScreen.getMinX();
            final double yInScene = boundsOnScreen.getMinY() - paneBoundsOnScreen.getMinY();
            return new Rectangle(xInScene, yInScene, boundsOnScreen.getWidth(), boundsOnScreen.getHeight());
        } catch (NullPointerException e) {
            return new Rectangle(0, 0, 0, 0);
        }
    }

    public Group getRectangles() {
        return rectangles;
    }
}
