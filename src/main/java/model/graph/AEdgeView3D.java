package model.graph;

import javafx.beans.property.DoubleProperty;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.shape.TriangleMesh;

public class AEdgeView3D extends Group {

    private MyLine3D myLine3D;
    private ANodeView3D target;
    private ANodeView3D source;
    private TriangleMesh triangleMesh;

    public AEdgeView3D(String id, ANodeView3D source, ANodeView3D target) {
        setId(id);
        myLine3D = new MyLine3D(source.translateXProperty(),
                source.translateYProperty(),
                source.translateZProperty(),
                target.translateXProperty(),
                target.translateYProperty(),
                target.translateZProperty(),
                Color.GOLD);

        this.source = source;
        this.target = target;
        getChildren().add(myLine3D);
    }

    public void changeColor(Color color) {
        myLine3D.setLineColor(color);
    }

    public DoubleProperty cylinderRadius() {
        return myLine3D.getRadiusProperty();
    }

    public ANodeView3D getTarget() {
        return target;
    }

    public ANodeView3D getSource() {
        return source;
    }

    public void setDefaultRadius() {
        myLine3D.setDefaultRadius();
    }
}
