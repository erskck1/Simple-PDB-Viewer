package model.graph;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.AminoAcid;
import model.Atom;
import model.Peptide;
import parser.FileReader;
import visualization.exception.ParallelEdgeException;
import visualization.exception.SelfLoopException;

import java.util.List;

public class AGraph {

    private ObservableList<ANode> nodes = FXCollections.observableArrayList();
    private ObservableList<AEdge> edges = FXCollections.observableArrayList();

    public void connect(ANode source, ANode target, String label) throws SelfLoopException, ParallelEdgeException { // connect target to source

        if (source.getId().equals(target.getId())) {
            throw new SelfLoopException();
        }

        AEdge edge = new AEdge();
        edge.setSource(source);
        edge.setTarget(target);

        if (source.isLeavingEdgeExist(edge)) {
            throw new ParallelEdgeException();
        }

        if (source.isEnteringEdgeExist(edge)) {
            throw new ParallelEdgeException();
        }

        source.addLeavingEdge(edge);
        target.addEnteringEdge(edge);
        edges.add(edge);
    }

    public void disconnect(ANode source, ANode target) { // disconnect target from source
        AEdge edge = new AEdge();
        edge.setSource(source);
        edge.setTarget(target);

        source.removeEdgeFromLeavingEdges(edge);
        target.removeEdgeFromEnteringEdges(edge);
        edges.remove(edge);
    }

    public void removeNode(ANode node) {
        for (int i = 0; i < node.getLeavingEdgeSize(); ) {
            AEdge edge = node.getLeavingEdgeBy(i);
            disconnect(edge.getSource(), edge.getTarget());
        }

        for (int i = 0; i < node.getEnteringEdgeSize(); ) {
            AEdge edge = node.getEnteringEdgeBy(i);
            disconnect(edge.getSource(), edge.getTarget());
        }

        nodes.remove(node);
    }

    public void connectExceptionHandled(ANode node1, ANode node2) {
        try {
            connect(node1, node2, "");
        } catch (SelfLoopException e) {
        } catch (ParallelEdgeException e) {
        }
    }

    public Peptide read(String filePath) {
        List<Peptide> peptides = FileReader.readPeptidesFromFile(filePath);
        FileReader.readAtomsFromFile(filePath, peptides);

        for (Peptide peptide : peptides) {
            for (AminoAcid aminoAcid : peptide.getSequence()) {
                aminoAcid.calculateCoordinates();
            }
        }

        int biggest = -1;
        Peptide biggestPeptide = null;
        for (Peptide peptide : peptides) {

            if (biggest < peptide.size()) {
                biggestPeptide = peptide;
            }
        }

        if (biggestPeptide == null) {
            return null;
        }

        int count = 0;

        for (AminoAcid aminoAcid : biggestPeptide.getSequence()) {
            count++;
            for (Atom atom : aminoAcid.getAtoms()) {
                ANode currentNode = new ANode(atom.getX(), atom.getY(), atom.getZ(), count, atom.getSymbol(), aminoAcid.getSymbol());
                nodes.add(currentNode);
            }
        }

        for (int i = 0; i < nodes.size(); i++) {

            ANode currentNode = nodes.get(i);
            if (currentNode.getSymbol().equals("N") || currentNode.getSymbol().equals("CA")) {
                connectExceptionHandled(currentNode, nodes.get(i + 1));
                continue;
            }

            if (currentNode.getSymbol().equals("C")) {
                if (!(i + 2 >= nodes.size())) {
                    ANode nextNode = nodes.get(i + 2);
                    if (nextNode.getSymbol().equals("CB")) {
                        if (!(i + 3 >= nodes.size())) {
                            connectExceptionHandled(currentNode, nodes.get(i + 3));
                        }
                    } else {
                        connectExceptionHandled(currentNode, nodes.get(i + 2));
                    }
                }
            }

            if (currentNode.getSymbol().equals("O")) {
                connectExceptionHandled(currentNode, nodes.get(i - 1));
                continue;
            }

            if (currentNode.getSymbol().equals("CB")) {
                connectExceptionHandled(currentNode, nodes.get(i - 3));
                continue;
            }
        }


        return biggestPeptide;
    }

    public ANode getNodeById(String id) {
        for (ANode node : nodes) {
            if (node.getId().equals(id)) {
                return node;
            }
        }
        return null;
    }

    public ObservableList<AEdge> getEdges() {
        return FXCollections.unmodifiableObservableList(edges);
    }

    public ObservableList<ANode> getNodes() {
        return FXCollections.unmodifiableObservableList(nodes);
    }

    public ANode[] getNodesAsArray() {
        ANode[] aNodeArray = new ANode[nodes.size()];
        for (int i = 0; i < nodes.size(); i++) {
            aNodeArray[i] = nodes.get(i);
        }
        return aNodeArray;
    }

    public void reset() {
        nodes.clear();
        edges.clear();
    }
}
