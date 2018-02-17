package model.graph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AminoAcidSymbols;

public class ANode {

    private String id;
    private String label = "";
    private double weight;
    private ObservableList<AEdge> leaving = FXCollections.observableArrayList();
    private ObservableList<AEdge> entering = FXCollections.observableArrayList();
    private double x;
    private double y;
    private double z;
    private String symbol;
    private int seqId;
    private AminoAcidSymbols aminoAcidSymbols;

    public ANode(double x, double y, double z, int seqId, String symbol, AminoAcidSymbols aminoAcidSymbols) {
        setId(symbol + "#" + seqId + "#" + x);
        this.seqId = seqId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.symbol = symbol;
        this.aminoAcidSymbols = aminoAcidSymbols;
    }

    public AminoAcidSymbols getAminoAcidSymbols() {
        return aminoAcidSymbols;
    }


    public int getSeqId() {
        return seqId;
    }

    public String getSymbol() {
        return symbol.trim();
    }

    public void addLeavingEdge(AEdge aEdge) {
        leaving.add(aEdge);
    }

    public AEdge getLeavingEdgeBy(int index) {
        return leaving.get(index);
    }

    public void addEnteringEdge(AEdge aEdge) {
        entering.add(aEdge);
    }

    public AEdge getEnteringEdgeBy(int index) {
        return entering.get(index);
    }

    public boolean isLeavingEdgeExist(AEdge edge) {
        return leaving.contains(edge);
    }

    public boolean isEnteringEdgeExist(AEdge edge) {
        return entering.contains(edge);
    }

    public void removeEdgeFromLeavingEdges(AEdge aEdge) {
        leaving.remove(aEdge);
    }

    public void removeEdgeFromEnteringEdges(AEdge aEdge) {
        entering.remove(aEdge);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getEnteringEdgeSize() {
        return entering.size();
    }

    public int getLeavingEdgeSize() {
        return leaving.size();
    }

    @Override
    public boolean equals(Object obj) {
        ANode node = (ANode) obj;

        if (this.getId().equals(node.getId())) {
            return true;
        }
        return super.equals(obj);
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

}

