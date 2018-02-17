package model;


import javafx.scene.paint.Color;

public enum AminoAcidSymbols {

    I("ILE", Color.BLACK),
    L("LEU", Color.BLUE),
    V("VAL", Color.GREEN),
    F("PHE", Color.MAGENTA),
    M("MET", Color.RED),
    C("CYS", Color.YELLOW),
    A("ALA", Color.BROWN),
    G("GLY", Color.ORANGE),
    P("PRO", Color.NAVY),
    T("THR", Color.PALEGOLDENROD),
    S("SER", Color.CORAL),
    Y("TYR", Color.BURLYWOOD),
    W("TRP", Color.DARKMAGENTA),
    Q("GLN", Color.GAINSBORO),
    N("ASN", Color.LIGHTSTEELBLUE),
    H("HIS", Color.SKYBLUE),
    E("GLU", Color.SIENNA),
    D("ASP", Color.GAINSBORO),
    K("LYS", Color.HOTPINK),
    R("ARG", Color.TOMATO),
    B("ASX", Color.DARKVIOLET);

    private String threeCharCode;
    private Color color;

    AminoAcidSymbols(String threeCharCode, Color color) {
        this.threeCharCode = threeCharCode;
        this.color = color;
    }

    public static AminoAcidSymbols getValueBy(String threeCharCode) {
        for (AminoAcidSymbols a : AminoAcidSymbols.values()) {
            if (a.threeCharCode.toUpperCase().equals(threeCharCode.toUpperCase())) {
                return a;
            }
        }
        return null;
    }

    public char getChar() {
        return this.name().charAt(0);
    }

    public String getThreeCharCode() {
        return threeCharCode;
    }

    public Color getColor() {
        return color;
    }
}
