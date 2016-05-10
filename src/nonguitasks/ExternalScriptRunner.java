/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nonguitasks;

import java.io.File;
import javafx.concurrent.Service;
import javafx.concurrent.Task;
import nodes.DatapointCollection;

/**
 *
 * @author Arne
 */
public class ExternalScriptRunner extends Service<DatapointCollection> {
    /**
     * The command to run the rscript with.
     */
    private String command = "";
    /**
     * The DatapointCollection to fill.
     */
    private DatapointCollection dotCol;
    /**
     * The filename as input.
     */
    private final String fileIn;
    /**
     * The file that contains all peptides in it.
     */
    private final File peptideFile;
    /**
     * Contains the file that has the uniqueness per peptide in it.
     */
    private final File uniquenessFile;
    /**
     * The parser that parses the uniqueness file.
     */
    private final PeptideUniquenessParser pup = new PeptideUniquenessParser();
    /**
     * Constructor of the class.
     * @param newCommand the command to run.
     * @param fileIn the file to process.
     * @param peptideFile the second file to process.
     * @param uniqueness the uniqueness file to use.
     */
    public ExternalScriptRunner(final String newCommand, final String fileIn, final File peptideFile, final File uniqueness) {
        this.command = newCommand;
        this.fileIn = fileIn;
        this.peptideFile = peptideFile;
        this.uniquenessFile = uniqueness;
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
                if (!new File(fileIn).isFile()) {
                    Process process = Runtime.getRuntime().exec(command);
                    process.waitFor();
                }
                pup.getPeptideUniqueness(peptideFile, uniquenessFile);
                DataframeReader dfr = new DataframeReader(fileIn, pup.getMpidToProtein());
                dotCol = dfr.readDataFrame();
                return dotCol;
            }
        };
    }
}
