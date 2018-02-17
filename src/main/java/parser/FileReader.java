package parser;

import model.AminoAcid;
import model.Atom;
import model.Peptide;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class FileReader {

    public static List<Peptide> readPeptidesFromFile(String filePath) {
        List<Peptide> peptides = new ArrayList<Peptide>();

        if (filePath == null || filePath.isEmpty()) {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("File not found!");
            }
        }

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            stream.forEach(x -> {
                if (x.startsWith("SEQRES")) {
                    addAminoAcidToPeptideSequence(peptides, x);
                }

            });
        } catch (IOException e) {
            System.out.println("File not found!");
        }

        return peptides;
    }

    public static void readAtomsFromFile(String filePath, List<Peptide> peptides) {
        if (filePath == null || filePath.isEmpty()) {
            try {
                throw new Exception();
            } catch (Exception e) {
                System.out.println("File not found!");
            }
        }

        try (Stream<String> stream = Files.lines(Paths.get(filePath))) {

            stream.forEach(x -> {
                if (x.startsWith("ATOM")) {
                    addAtomToPeptide(peptides, x);
                }

            });
        } catch (IOException e) {
            System.out.println("File not found!");
        }
    }

    private static void addAtomToPeptide(List<Peptide> peptides, String x) {
        String symbol = x.substring(12, 16).trim();


        if (!(symbol.equals("C") || symbol.equals("CA") || symbol.equals("CB") || symbol.equals("O") || symbol.equals("N"))) {
            return;
        }

        Peptide peptide = null;
        for (Peptide pep : peptides) {
            if (pep.getChain() == x.charAt(21)) {
                peptide = pep;
            }
        }

        if (peptide.getRealSize() < Integer.valueOf(x.substring(22, 26).trim())) {
            return;
        }

        AminoAcid aminoAcid = peptide.getAminoAcidBy(Integer.valueOf(x.substring(22, 26).trim()) - 1);

        String xCor = x.substring(30, 38).trim();
        String yCor = x.substring(38, 46).trim();
        String zCor = x.substring(46, 55).trim();


        Atom atom = new Atom(xCor, yCor, zCor, symbol);
        aminoAcid.addAtom(atom);
    }

    private static void addAminoAcidToPeptideSequence(List<Peptide> peptides, String x) {
        Peptide peptide = null;
        for (Peptide pep : peptides) {
            if (pep.getChain() == x.charAt(11)) {
                peptide = pep;
            }
        }

        if (peptide == null) {
            peptide = new Peptide();
            peptide.setChain(x.charAt(11));
            peptide.setRealSize(Integer.valueOf(x.substring(13, 17).trim()));
            peptides.add(peptide);
        }

        String[] aminoAcidStr = x.substring(19).split("\\s+");
        for (int i = 0; i < aminoAcidStr.length; i++) {
            peptide.addAminoAcid(aminoAcidStr[i]);
        }
    }


}
