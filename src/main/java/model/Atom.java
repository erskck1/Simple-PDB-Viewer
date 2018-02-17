package model;

import javafx.geometry.Point3D;

public class Atom {

    private double x, y, z;
    private String symbol;
    private Point3D position;

    public Atom(String x, String y, String z, String symbol) {
        this.x = Double.valueOf(x);
        this.y = Double.valueOf(y);
        this.z = Double.valueOf(z);
        this.symbol = symbol;
        position = new Point3D(this.x, this.y, this.z);
    }

    public Point3D getPosition() {
        return position;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public String getSymbol() {
        return symbol;
    }

}




