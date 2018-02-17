package model.graph;

public class AEdge {

    private String label = "";
    private double weight;
    private ANode source;
    private ANode target;

    public ANode getSource() {
        return source;
    }

    public void setSource(ANode source) {
        this.source = source;
    }

    public ANode getTarget() {
        return target;
    }

    public void setTarget(ANode target) {
        this.target = target;
    }

    public String getId() {
        return String.format("%s%s", source.getId(), target.getId());
    }

    @Override
    public boolean equals(Object obj) {
        AEdge aEdge = (AEdge) obj;

        if (this == aEdge) {
            return true;
        }

        if (this.getSource() == aEdge.getSource() && this.getTarget() == aEdge.getTarget()) {
            return true;
        }

        if (this.getSource() == aEdge.getTarget() && this.getTarget() == aEdge.getSource()) {
            return true;
        }

        return false;
    }
}
