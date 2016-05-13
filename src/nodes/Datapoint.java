/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 *
 * @author Arne
 */
public class Datapoint {
    /**
     * The LogFC of the dot.
     */
    private final float mLogFC;
    /**
     * The pvalue of the dot.
     */
    private final float mPvalue;
    /**
     * The ID of the dot.
     */
    private final String mpid;
    /**
     * The proteinName of the dot.
     */
    private ArrayList<String> proteinNames;
    /**
     * The Sequence of the String.
     */
    private final String sequence;
    /**
     * True if the peptide is not identified.
     */
    private final Boolean isIdentified;
    /**
     * Constructor of the dot class
     * @param mpid the new id
     * @param logFC the new logfc
     * @param pvalue the new pvalue
     * @param sequence the sequence of the dot
     * @param proteinNames the new proteinName(s) the dot belongs to
     */
    public Datapoint(String mpid, Float logFC, Float pvalue, String sequence, List<String> proteinNames) {
        this.mLogFC = logFC;
        this.mPvalue = pvalue;
        this.mpid = mpid;
        this.proteinNames = new ArrayList<>();
        this.proteinNames.addAll(proteinNames);
        this.sequence = sequence;
        this.isIdentified = !this.sequence.equals("-");
    }
    /**
     * Getter of the logFC.
     * @return  the logfc
     */
    public final float getLogFC() {
        return mLogFC;
    }
    /**
     * Getter of the sequence that belongs to the data point.
     * @return String Amino Acid sequence
     */
    public final String getSequence() {
        return this.sequence;
    }
    /**
     * Getter of the pvalue of the dot.
     * @return the pvalue
     */
    public final float getPvalue() {
        return mPvalue;
    }
    /**
     * Sets the protein name of the dot.
     * @param proteinNames the protein/gene names the dot belongs to.
     */
    public final void setProteinNames(final ArrayList<String> proteinNames) {
        this.proteinNames = proteinNames;
    }
    /**
     * Returns the protein name.
     * @return String protein name
     */
    public final ArrayList<String> getProteinNames() {
        return proteinNames;
    }
    /**
     * Getter of the ID if the dot.
     * @return String with the id in it.
     */
    public final String getMPID() {
        return mpid;
    }
    /**
     * Returns the boolean of the peptide that specifies if the mpid has an identified peptide.
     * @return True if the there is an AA sequence present
     */
    public final Boolean isIdentified() {
        return isIdentified;
    }
}
