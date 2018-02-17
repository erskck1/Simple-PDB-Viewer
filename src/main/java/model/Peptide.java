package model;

import visualization.model.secondary.Helix;

import java.util.ArrayList;
import java.util.List;

public class Peptide {

    private List<AminoAcid> sequence;
    private char chain;
    private int realSize;
    private List<Helix> helices = new ArrayList<>();

    public Peptide() {
        sequence = new ArrayList<>();
    }

    public void addAminoAcid(AminoAcidSymbols aminoAcidSymbol) {
        AminoAcid aminoAcid = new AminoAcid();
        aminoAcid.setSymbol(aminoAcidSymbol);
        sequence.add(aminoAcid);
    }

    public void addAminoAcid(String threeCharCode) {
        AminoAcid aminoAcid = new AminoAcid();
        aminoAcid.setSymbol(AminoAcidSymbols.getValueBy(threeCharCode));
        sequence.add(aminoAcid);
        aminoAcid.setAminoAcidNumber(sequence.size());
    }

    public List<AminoAcid> getSequence() {
        return sequence;
    }

    public char getChain() {
        return chain;
    }

    public void setChain(char chain) {
        this.chain = chain;
    }

    public AminoAcid getAminoAcidBy(int index) {
        return sequence.get(index);
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        for (AminoAcid aminoAcid : sequence) {
            builder.append("\n--->");
            builder.append(aminoAcid.getSymbol().getThreeCharCode());
            for (Atom atom : aminoAcid.getAtoms()) {
                builder.append("\n#");
                builder.append(atom.getSymbol());
            }
            builder.append(" ");
        }
        return builder.toString();
    }

    public int size() {
        return sequence.size();
    }

    public int getRealSize() {
        return realSize;
    }

    public void setRealSize(int realSize) {
        this.realSize = realSize;
    }
}
