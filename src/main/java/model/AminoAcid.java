package model;

import javafx.scene.paint.Color;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class AminoAcid {
    public static final Comparator<AminoAcid> SEQUENCE_NUMBER_COMPARATOR = Comparator.comparing(AminoAcid::getAminoAcidNumber);
    private AminoAcidSymbols symbol;
    private List<Atom> atoms;
    private int aminoAcidNumber;
    private AminoAcid next;
    private AminoAcid prev;
    private double x;
    private double y;
    private double z;
    private Color aminoAcidColor;

    public AminoAcid() {
        atoms = new ArrayList<Atom>();

    }

    public int getAminoAcidNumber() {
        return aminoAcidNumber;
    }

    public void setAminoAcidNumber(int aminoAcidNumber) {
        this.aminoAcidNumber = aminoAcidNumber;
    }

    public AminoAcidSymbols getSymbol() {
        return symbol;
    }

    public void setSymbol(AminoAcidSymbols symbol) {
        this.symbol = symbol;
    }

    public List<Atom> getAtoms() {
        return atoms;
    }

    public void addAtom(Atom atom) {
        atoms.add(atom);
    }

    public void setNext(AminoAcid next) {
        this.next = next;
    }

    public void setPrev(AminoAcid prev) {
        this.prev = prev;
    }

    public void calculateCoordinates() {
        double xSum = 0;
        double ySum = 0;
        double zSum = 0;
        if (atoms.isEmpty()) {
            x = 0;
            y = 0;
            z = 0;
        } else {
            for (Atom atom : atoms) {

                xSum += atom.getX();
                ySum += atom.getY();
                zSum += atom.getZ();
            }
            x = xSum / atoms.size();
            y = ySum / atoms.size();
            z = zSum / atoms.size();
        }
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

    public Atom getAtomBy(String atomName) {
        for (Atom atom : atoms) {
            if (atom.getSymbol().equals(atomName)) {
                return atom;
            }
        }
        return null;
    }
}
