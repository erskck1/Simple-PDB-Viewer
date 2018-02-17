package model.graph;

import javafx.beans.InvalidationListener;
import javafx.beans.property.DoubleProperty;
import javafx.geometry.Point3D;
import javafx.scene.Group;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Cylinder;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Translate;

public class MyLine3D extends Group {

    private DoubleProperty startXProperty;
    private DoubleProperty startYProperty;
    private DoubleProperty startZProperty;
    private DoubleProperty endXProperty;
    private DoubleProperty endYProperty;
    private DoubleProperty endZProperty;
    private Cylinder line;
    private Color color;
    private InvalidationListener invalidationListener = (x) -> {

    };

    public MyLine3D(DoubleProperty startXProperty,
                    DoubleProperty startYProperty,
                    DoubleProperty startZProperty,
                    DoubleProperty endXProperty,
                    DoubleProperty endYProperty,
                    DoubleProperty endZProperty,
                    Color color) {
        this.startXProperty = startXProperty;
        this.startYProperty = startYProperty;
        this.startZProperty = startZProperty;
        this.endXProperty = endXProperty;
        this.endYProperty = endYProperty;
        this.endZProperty = endZProperty;
        this.color = color;

        addListeners();
        create3DLine();
        setLineColor(color);
        getChildren().add(line);
        setDefaultRadius();
    }

    public void setDefaultRadius() {
        line.setRadius(0.2);
    }

    public DoubleProperty getRadiusProperty() {
        return line.radiusProperty();
    }

    public final DoubleProperty startXProperty() {
        return startXProperty;
    }

    public final DoubleProperty startYProperty() {
        return startYProperty;
    }

    public final DoubleProperty endXProperty() {
        return endXProperty;
    }

    public final DoubleProperty endYProperty() {
        return endYProperty;
    }

    private void create3DLine() {
        Point3D origin = new Point3D(startXProperty.get(), startYProperty.get(), startZProperty.get());
        Point3D target = new Point3D(endXProperty.get(), endYProperty.get(), endZProperty.get());

        Point3D yAxis = new Point3D(0, 1, 0);

        Point3D diff = target.subtract(origin);
        double height = diff.magnitude();

        Point3D mid = target.midpoint(origin);
        Translate moveToMidpoint = new Translate(mid.getX(), mid.getY(), mid.getZ());

        Point3D axisOfRotation = diff.crossProduct(yAxis);
        double angle = Math.acos(diff.normalize().dotProduct(yAxis));
        Rotate rotateAroundCenter = new Rotate(-Math.toDegrees(angle), axisOfRotation);

        line = new Cylinder(1, height);
        line.getTransforms().addAll(moveToMidpoint, rotateAroundCenter);
    }

    public void setLineColor(Color color) {
        PhongMaterial greyMaterial = new PhongMaterial();
        greyMaterial.setDiffuseColor(color.darker());
        greyMaterial.setSpecularColor(color.brighter());
        line.setMaterial(greyMaterial);
    }

    private void addListeners() {
        startXProperty.addListener(invalidationListener);
        startYProperty.addListener(invalidationListener);
        startZProperty.addListener(invalidationListener);
        endXProperty.addListener(invalidationListener);
        endYProperty.addListener(invalidationListener);
        endZProperty.addListener(invalidationListener);
    }
}
