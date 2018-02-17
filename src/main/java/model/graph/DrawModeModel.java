package model.graph;

import javafx.scene.shape.DrawMode;

import java.io.Serializable;

public class DrawModeModel implements Serializable {

    private DrawMode drawMode;

    public DrawModeModel(DrawMode drawMode) {
        this.drawMode = drawMode;
    }

}
