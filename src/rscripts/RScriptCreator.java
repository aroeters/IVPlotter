/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rscripts;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 *
 * @author Arne
 */
public class RScriptCreator {
    /**
     * The directory to temporary store the R script in.
     */
    private String dir = "";
    /**
     * Creates the temporary R script.
     * @return The string with the path to the script and scriptname in it.
     * @throws IOException
     */
    public final String createTempRScript() throws IOException {
        dir = System.getProperty("user.dir");
        File rscript = File.createTempFile(dir + "/calculations", ".R");
        ClassLoader classLoader = getClass().getClassLoader();
        InputStream is = getClass().getResourceAsStream("/rscripts/calculations.R");
        BufferedReader br = new BufferedReader(new InputStreamReader(is));
        FileWriter bw = new FileWriter(rscript, true);
        String line;
        while ((line = br.readLine()) != null) {
            bw.write(line + "\n");
        }
        bw.close();
        return rscript.getPath();
    }
    /**
     * Gett he directory where the script is stored in.
     * @return the directory with path to it in a string.
     */
    public final String getDir() {
        return dir;
    }
}
