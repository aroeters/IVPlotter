/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nodes;

import java.util.ArrayList;
import java.util.HashMap;

/**
 *
 * @author Arne
 */
public class DatapointCollection {

    /**
     * The hashmap that contains as a key the protein/gene name and as a value a
     * list with all dots that belong to it.
     */
    HashMap<String, ArrayList<Datapoint>> dots = new HashMap<>();
    /**
     * The minimal pvalue.
     */
    private double minPval = Double.MAX_VALUE; // to always be below this
    /**
     * The maximal pvalue.
     */
    private double maxPval = Double.MIN_VALUE; // to always be above this
    /**
     * The minimal logfc.
     */
    private double minLogFC = Double.MAX_VALUE; // to always be below this
    /**
     * The maximal logFC.
     */
    private double maxLogFC = Double.MIN_VALUE; // to always be above this

    /**
     * Adds a dot to the collection.
     *
     * @param datapoint the dot object to add
     */
    public final void addPoint(Datapoint datapoint) {
        for (String proteinName : datapoint.getProteinNames()) {
            if (!dots.containsKey(proteinName)) {
                ArrayList<Datapoint> proteinDatapoints = new ArrayList<>();
                proteinDatapoints.add(datapoint);
                dots.put(proteinName, proteinDatapoints);
            } else {
                ArrayList<Datapoint> proteinDots = dots.get(proteinName);
                proteinDots.add(datapoint);
                dots.put(proteinName, proteinDots);
            }
        }
    }

    /**
     * Returns the total collection.
     *
     * @return HashMap<String, ArrayList<Dot>>
     */
    public final HashMap<String, ArrayList<Datapoint>> getDatapoints() {
        return dots;
    }

    /**
     * Getter of the minimal pvalue.
     *
     * @return the minimal pvalue in a double.
     */
    public final double getMinPval() {
        return minPval;
    }

    /**
     * Getter of the maximal pvalue.
     *
     * @return the maximal pvalue in a double.
     */
    public final double getMaxPval() {
        return maxPval;
    }

    /**
     * Getter of the minimal logfc.
     *
     * @return the minimal logfc in a double.
     */
    public final double getMinLogFC() {
        return minLogFC;
    }

    /**
     * Getter of the maximal logfc.
     *
     * @return the maximal logfc in a double.
     */
    public final double getMaxLogFC() {
        return maxLogFC;
    }

    /**
     * Sets the minimal pvalue.
     *
     * @param value the value to set as minimal
     */
    public final void setMinPval(final Double value) {
        if (value < minPval) {
            minPval = value;
        }
    }

    /**
     * Sets the maximal pvalue.
     *
     * @param value the value to set as max
     */
    public final void setMaxPval(final Double value) {
        if (value > maxPval) {
            maxPval = value;
        }
    }

    /**
     * Sets the minimal logfc.
     *
     * @param value the value to set as minimal
     */
    public final void setMinLogFC(final Double value) {
        if (value < minLogFC) {
            minLogFC = value;
        }
    }

    /**
     * Sets the maximal logfc.
     *
     * @param value the value to set as minimal
     */
    public final void setMaxLogFC(final Double value) {
        if (value > maxLogFC) {
            maxLogFC = value;
        }
    }
}
