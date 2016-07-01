/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonguitasks;

import calculators.Calculator;
import calculators.FileCollection;
import java.io.File;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import nodes.DatapointCollection;

/**
 *
 * @author Arne
 */
public class ThreadSeparater extends Service<DatapointCollection> {
    /**
     * The DatapointCollection to fill.
     */
    private DatapointCollection dotCol;
    /**
     * The parser that parses the uniqueness file.
     */
    private final PeptideUniquenessParser pup = new PeptideUniquenessParser();
    /**
     * The FileCollection contains all files and other options that have been selected
     * in the IVPlotter and have to be used for creation of all tests.
     */
    private final FileCollection files;
    /**
     * Constructor of the class.
     * @param files the container that collects and contains all files and selection options
     */
    public ThreadSeparater(final FileCollection files) {
        this.files = files;
    }
    /**
     * Creates a task that is sent to a different thread than the one of the UI.
     * @return a DatapointCollection that is filled
     */
    @Override
    protected Task createTask() {
        return new Task<DatapointCollection>() {
            @Override
            protected DatapointCollection call() throws Exception { // overides the call method to be able to use it.
                Calculator calc = new Calculator(files.getControl_group(), files.getTarget_group(), files.getGroup_file(), files.getIntensity_file());
                pup.getPeptideUniqueness(files.getPeptide_file(), files.getUniqueness_file());
                DataframeReader dfr = new DataframeReader(calc.getCalculationResults(), pup.getMpidToProtein());
                dotCol = dfr.readDataFrame();
                return dotCol;
            }
        };
    }
}
