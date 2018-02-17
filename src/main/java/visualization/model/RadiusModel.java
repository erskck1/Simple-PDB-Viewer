package visualization.model;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class RadiusModel {

    private DoubleProperty sphereRadius = new SimpleDoubleProperty();
    private DoubleProperty cylinderRadius = new SimpleDoubleProperty();

    public double getSphereRadius() {
        return sphereRadius.get();
    }

    public void setSphereRadius(double sphereRadius) {
        this.sphereRadius.set(sphereRadius);
    }

    public DoubleProperty sphereRadiusProperty() {
        return sphereRadius;
    }

    public double getCylinderRadius() {
        return cylinderRadius.get();
    }

    public void setCylinderRadius(double cylinderRadius) {
        this.cylinderRadius.set(cylinderRadius);
    }

    public DoubleProperty cylinderRadiusProperty() {
        return cylinderRadius;
    }

    public void reset() {
        cylinderRadius.setValue(0.1);
        sphereRadius.setValue(0);
    }
}
