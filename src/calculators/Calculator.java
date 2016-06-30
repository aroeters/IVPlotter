/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package calculators;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import org.apache.commons.math3.analysis.function.Log;
import org.apache.commons.math3.analysis.function.Log10;
import org.apache.commons.math3.stat.inference.TTest;

/**
 *
 * @author Asus
 */
public class Calculator {

    /**
     * The hashmap that contains all results.
     */
    private final HashMap<String, LinkedList<Double>> results;
    /**
     * the column means.
     */
    private LinkedList<Double> columnMeans = new LinkedList<>();
    /**
     * The mean of all column means.
     */
    private Double totalMean = 0.0;
    /**
     * The HashMap that states to which group a sample belongs.
     */
    private LinkedHashMap<String, String> groupMap = new LinkedHashMap<>();
    /**
     * The size of the control group.
     */
    private Integer controlSize;
    /**
     * The size of the target group.
     */
    private Integer targetSize;
    /**
     * The number of columns in the whole file that contain intensities.
     */
    private Integer length;

    /**
     * The constructor of the class.
     *
     * @param control_group the group that should be used as an control group
     * @param target_group the group to check against the control group
     * @param group_file the file that tells the program which sample belongs to
     * which group
     * @param intensity_file the intensity file to use
     * @throws IOException
     */
    public Calculator(final String control_group, final String target_group,
            final File group_file, final File intensity_file) throws IOException {
        results = new HashMap<>();
        calculateMeans(intensity_file);
        createGroupMap(group_file, control_group, target_group);
        BufferedReader br = new BufferedReader(new FileReader(intensity_file.getAbsolutePath()));
        br.readLine();
        String line;
        String[] splitLine;
        int i  = 0;
        while ((line = br.readLine()) != null) {
            splitLine = line.split("\\s+");
            results.put(splitLine[0], calculateValues(splitLine, control_group, target_group));
        }
    }

    /**
     * Creates the HashMap that states which sample belongs to which sample
     * group. It can be done on index because the order of the samples in the
     * group file is the same as the order of the samples in the intensity file.
     * The group file does not contain the sample names so this is also the only
     * way to do it
     *
     * @param group_file the group file
     * @param control the control group
     * @param target the target group
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void createGroupMap(final File group_file, final String control, final String target) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(group_file.getAbsolutePath()));
        String line;
        LinkedList<String> temporaryMap = new LinkedList<>();
        while ((line = br.readLine()) != null) {
            temporaryMap.add(line.split("\\s+")[1]);
        }
        Integer index = 0;
        for (String key : groupMap.keySet()) {
            groupMap.replace(key, temporaryMap.get(index));
            index++;
        }
        controlSize = Collections.frequency(new ArrayList<>(groupMap.values()), control);
        targetSize = Collections.frequency(new ArrayList<>(groupMap.values()), target);
    }

    private LinkedList<Double> calculateValues(final String[] splitLine, final String control_group, final String target_group) {
        double controlTotal = 0.0;
        double targetTotal = 0.0;
        double[] targetList = new double[targetSize];
        double[] controlList = new double[controlSize];
        Double tempValue;
        int index = 0;
        int controlIndex = 0;
        int targetIndex = 0;
        for (String group : groupMap.values()) {
            tempValue = (((Double.parseDouble(splitLine[index + 14]) / columnMeans.get(index))) * totalMean);
            if (group.equals(control_group)) {
                controlTotal += tempValue;
                controlList[controlIndex] = tempValue;
                controlIndex++;
            } else {
                targetTotal += tempValue;
                targetList[targetIndex] =  tempValue;
                targetIndex++;
            }
            index++;
        }
        LinkedList<Double> list = new LinkedList<>();
        list.add(calculateLogFC(controlTotal/controlSize, targetTotal/controlSize));
        list.add(calculatePvalue(controlList, targetList));
        return list;
    }

    /**
     * Calculates the logFC.
     *
     * @param meanControl the mean of the control group
     * @param meanTarget the mean of the target group
     * @return the logFC
     */
    private Double calculateLogFC(final Double meanControl, final Double meanTarget) {
        return (Math.log(meanTarget/meanControl)/Math.log(2));
    }

    /**
     * Calculates the pvalue.
     *
     * @param control the control group intensities
     * @param target the target group intensities
     * @return
     */
    private Double calculatePvalue(final double[] control, final double[] target) {
        TTest t_test = new TTest();
        Log10 logTen = new Log10();
        return -logTen.value(t_test.tTest(target, control));
    }

    /**
     * Calculates the means of every column and the total mean.
     *
     * @param intensity_file the file that contains the intensities
     * @throws FileNotFoundException
     * @throws IOException
     */
    private void calculateMeans(final File intensity_file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(intensity_file.getAbsolutePath()));
        String[] splitLine = br.readLine().split("\\s+");
        length = splitLine.length;
        for (int i = 14; i < length; i++) {
            groupMap.put(splitLine[i], "");
        }
        String line;
        Integer totalRows = 0;
        Double temporaryNumber;
        while ((line = br.readLine()) != null) {
            splitLine = line.split("\\s+");
            // skip the first 14 elements because it is not intensity data
            if (totalRows != 0) {
                for (int i = 14; i < length; i++) {
                    temporaryNumber = columnMeans.get(i - 14);
                    columnMeans.set(i - 14, temporaryNumber + Double.parseDouble(splitLine[i]));
                }
            } else {
                for (int i = 14; i < length; i++) {
                    columnMeans.add(Double.parseDouble(splitLine[i]));
                }
            }
            totalRows++;
        }
        Double tempNumber;
        for (int i = 14; i < length; i++) {
            tempNumber = columnMeans.get(i-14) / totalRows;
            columnMeans.set(i - 14, tempNumber);
            totalMean += tempNumber;
        }
        totalMean = totalMean / (length - 14);
    }

    /**
     * Returns the results of the calculations.
     *
     * @return a HashMap with mpid as key and another HashMap with logFC and pva
     */
    public final HashMap<String, LinkedList<Double>> getCalculationResults() {
        return results;
    }
}