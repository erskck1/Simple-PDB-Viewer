package visualization.model;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import model.AminoAcid;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class AminoAcidSequence implements Iterable<AminoAcid> {

    private final ObservableList<AminoAcid> sequence = FXCollections.observableArrayList();
    private final SortedList<AminoAcid> sortedSequence = new SortedList<>(sequence);
    private final int startSequenceNumber;
    private final int[][] bonds;
    private final double[][] coordinates;

    public AminoAcidSequence(List<AminoAcid> aminoacids) {
        aminoacids.sort(AminoAcid.SEQUENCE_NUMBER_COMPARATOR);
        {
            startSequenceNumber = aminoacids.get(0).getAminoAcidNumber();
            for (int i = 0; i < aminoacids.size(); i++) {
                sequence.add(aminoacids.get(i));
            }
        }
        coordinates = new double[sequence.size()][];
        for (int i = 0; i < sequence.size(); i++) {
            AminoAcid current = sortedSequence.get(i);
            if (i < sequence.size() - 2) {
                AminoAcid next = sortedSequence.get(i + 1);
                if (next.getAminoAcidNumber() == current.getAminoAcidNumber() + 1) {
                    current.setNext(next);
                    next.setPrev(current);
                }
            }

            coordinates[i] = new double[2];
            coordinates[i][0] = current.getX();
            coordinates[i][1] = current.getY();
        }
        List<int[]> bondList = new ArrayList<>();
        Iterator<AminoAcid> it = aminoacids.iterator();
        while (it.hasNext()) {
            AminoAcid aminoAcid = it.next();
            // spine bond
            if (it.hasNext()) {
                int[] bond = new int[2];
                bond[0] = getIndex(aminoAcid.getAminoAcidNumber());
                bond[1] = bond[0] + 1;
                bondList.add(bond);
            }
        }
        bonds = new int[bondList.size()][];
        for (int i = 0; i < bondList.size(); ++i) {
            bonds[i] = bondList.get(i);
        }
    }

    public double[][] getCoordinates() {
        return coordinates;
    }

    @Override
    public Iterator<AminoAcid> iterator() {
        return new AAIterator(sortedSequence);
    }

    public AminoAcid[] toArray() {
        AminoAcid[] ret = new AminoAcid[length()];
        for (int i = 0; i < length(); ++i) {
            ret[i] = sortedSequence.get(i);
        }
        return ret;
    }

    public int length() {
        return sequence.size();
    }

    public AminoAcid get(int sequenceNumber) {
        return getByIndex(getIndex(sequenceNumber));
    }

    public AminoAcid getByIndex(int index) {
        return sortedSequence.get(index);
    }

    public final int getIndex(int sequenceNumber) {
        return sequenceNumber - startSequenceNumber;
    }

    public int getSequenceNumber(int index) {
        return index + startSequenceNumber;
    }

    public int[][] getBonds() {
        return bonds;
    }

    public int getNumberOfBonds() {
        return bonds.length;
    }

    public int[] getBond(int index) {
        return bonds[index];
    }

    public static final class AAIterator implements Iterator<AminoAcid> {

        private final Iterator<AminoAcid> it;

        private AAIterator(Iterable<AminoAcid> aminoAcids) {
            it = aminoAcids.iterator();
        }

        @Override
        public boolean hasNext() {
            return it.hasNext();
        }

        @Override
        public AminoAcid next() {
            return it.next();
        }
    }
}
