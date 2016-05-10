/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonguitasks;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Arne
 */
public class PeptideUniquenessParser {

    /**
     * The mpid to protein hashmap.
     */
    private final HashMap<String, ArrayList<String>> mpidToProtein = new HashMap<>();

    /**
     * Gets the peptide uniqueness per protein and fills the mpid to protein
     * hashmap.
     *
     * @param peptideFile the file with peptide sequences
     * @param proteinFile the peptide to protein uniqueness file
     * @throws FileNotFoundException
     * @throws IOException
     */
    public final void getPeptideUniqueness(final File peptideFile, final File proteinFile) throws FileNotFoundException, IOException {
        HashMap<String, ArrayList<String>> peptideToProtein = getProteinMap(proteinFile);
        BufferedReader br = new BufferedReader(new FileReader(peptideFile));
        Boolean first;
        String[] splitElement;
        String[] splitLine;
        String line;
        String mpid = "";
        br.readLine(); // skip the header
        ArrayList<String> temp;
        HashMap<String, Integer> peptideCount = new HashMap<>();
        String maxEntry = null;
        while ((line = br.readLine()) != null) {
            first = true; // the first element is the mpid so should not be included among the peptides
            splitLine = line.split("\\s+");
            for (String element : splitLine) { // for every element in the peptide list
                if (first) {
                    mpid = element;
                    first = false;
                } else if (line.replaceAll("-", "").replaceAll("}", "").trim().equals(mpid)) {
                    peptideCount.put("-", 1);
                    break;
                } else if (!element.equals("-")) { // if the peptide != empty
                    if (element.contains("}")) { // if there are multiple peptides for a single sample
                        splitElement = element.split("}");
                        for (String secondElement : splitElement) { // for every possible peptide in the sample
                            if (!secondElement.equals("-")) { // if the peptide != empty
                                if (peptideCount.containsKey(secondElement)) {
                                    peptideCount.put(secondElement, peptideCount.get(secondElement) + 1);
                                } else {
                                    peptideCount.put(secondElement, 1);
                                }
                            }
                        }
                    } else if (peptideCount.containsKey(element)) {
                        peptideCount.put(element, peptideCount.get(element) + 1);
                    } else {
                        peptideCount.put(element, 1);
                    }
                }
            }

            for (String key : peptideCount.keySet()) { // for every peptide found and counted
                if (maxEntry == null || peptideCount.get(key) > peptideCount.get(maxEntry)) { // if the count is greater than the previous count
                    maxEntry = key;
                } else if (peptideCount.get(key) == peptideCount.get(maxEntry)) { // if there are multiple peptides with the same count
                    maxEntry = null;
                    break;
                }
            }
            if (maxEntry != null && peptideToProtein.containsKey(maxEntry)) {
                temp = new ArrayList<>();
                temp.add(maxEntry);
                temp.addAll(peptideToProtein.get(maxEntry));
                mpidToProtein.put(mpid, temp);
            } else {
                temp = new ArrayList<>();
                temp.add("-");
                temp.add("Unknown");
                mpidToProtein.put(mpid, temp);
            }
            maxEntry = null;
            peptideCount.clear();
        }
    }

    /**
     * Gets the protein hashmap and returns it.
     *
     * @param fileIn the file that contains the uniqueness per peptide.
     * @return a hashmap with as key the peptide and the value the protein
     * @throws FileNotFoundException
     * @throws IOException
     */
    private HashMap<String, ArrayList<String>> getProteinMap(final File fileIn) throws FileNotFoundException, IOException {
        HashMap<String, ArrayList<String>> peptideToProtein = new HashMap<>();
        BufferedReader br = new BufferedReader(new FileReader(fileIn));
        String line;
        String[] splitLine;
        ArrayList<String> temp;
        while ((line = br.readLine()) != null) {
            splitLine = line.split(",");
            temp = new ArrayList<>();
            for (int i = 1; i < splitLine.length; i++) {
                temp.add(splitLine[i]);
            }
            peptideToProtein.put(splitLine[0], temp);
        }
        return peptideToProtein;
    }

    /**
     * The getter of the mpidToProtein hashmap.
     *
     * @return hashmap, key = mpid, value = protein
     */
    public final HashMap<String, ArrayList<String>> getMpidToProtein() {
        return this.mpidToProtein;
    }
}
