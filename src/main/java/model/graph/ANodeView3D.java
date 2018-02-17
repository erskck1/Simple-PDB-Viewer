package model.graph;

import javafx.scene.Group;
import javafx.scene.control.Tooltip;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Sphere;
import model.AminoAcidSymbols;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Random;
import java.util.Stack;

public class ANodeView3D extends Group {

    PhongMaterial material = new PhongMaterial();
    private Sphere sphere;
    private Deque<DrawModeModel> drawModeModelStack = new LinkedList<>();
    private Stack<DrawModeModel> drawModeModelStackRedo = new Stack<>();
    private AminoAcidSymbols aminoAcidSymbols;
    private int seqId;
    private String symbol;

    public ANodeView3D(String id, double x, double y, double z, Color color, int seqId, String symbol, AminoAcidSymbols aminoAcidSymbols) {
        setId(id);
        setX(x);
        setY(y);
        setZ(z);
        this.aminoAcidSymbols = aminoAcidSymbols;
        changeColor(color);
        this.symbol = symbol;
        this.seqId = seqId;
        sphere = new Sphere();
        sphere.setMaterial(material);

        Tooltip t = new Tooltip(seqId + ":" + symbol);

        Tooltip.install(sphere, t);

        sphere.translateXProperty().add(translateXProperty().get());
        sphere.translateYProperty().add(translateYProperty().get());
        sphere.translateZProperty().add(translateZProperty().get());

        getChildren().add(sphere);

    }

    public ANodeView3D(String id, double x, double y, double z) {
        setId(id);
        setX(x);
        setY(y);
        setZ(z);
        Random random = new Random();
        PhongMaterial material = new PhongMaterial();
        Color color = Color.rgb(random.nextInt(255), random.nextInt(255), random.nextInt(255));
        material.setDiffuseColor(color.darker());
        material.setSpecularColor(color.brighter());

        sphere = new Sphere(5);
        sphere.setMaterial(material);

        sphere.translateXProperty().add(translateXProperty().get());
        sphere.translateYProperty().add(translateYProperty().get());
        sphere.translateZProperty().add(translateZProperty().get());

        getChildren().add(sphere);
    }

    public AminoAcidSymbols getAminoAcidSymbols() {
        return aminoAcidSymbols;
    }


    public void changeColor(Color color) {
        material.setDiffuseColor(color.darker());
        material.setSpecularColor(color.brighter());
    }

    public void setRadius(double radius) {
        sphere.setRadius(radius);
    }

    public double getX() {
        return translateXProperty().get();
    }

    public void setX(double x) {
        translateXProperty().setValue(x);
    }

    public double getY() {
        return translateYProperty().get();
    }

    public void setY(double y) {
        translateYProperty().setValue(y);
    }

    public void setZ(double z) {
        translateZProperty().setValue(z);
    }

}
