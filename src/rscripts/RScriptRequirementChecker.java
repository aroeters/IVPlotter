/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rscripts;

import java.io.File;

/**
 *
 * @author Arne
 */
public class RScriptRequirementChecker {
    /**
     * The resource file that contains the protein intensities.
     */
    private File resource_file = null;
    /**
     * The file that contains the groups.
     */
    private File group_file = null;
    /**
     * The group to use as a control group.
     */
    private String control_group = null;
    /**
     * The group to check against.
     */
    private String check_group = null;
    /**
     * The file that contains the peptide per MPID.
     */
    private File peptide_file = null;
    /**
     * The file that contains the uniqueness per protein/gene.
     */
    private File uniqueness_file = null;
    /**
     * Setter of the resource file.
     * @param file a new file
     */
    public final void setResourceFile(final File file) {
        resource_file = file;
    }
    /**
     * Getter of the resource file.
     * @return a File object that represents the file.
     */
    public File getResource_file() {
        return resource_file;
    }
    /**
     * Getter of the group file.
     * @return a File object with the group file in it
     */
    public File getGroup_file() {
        return group_file;
    }
    /**
     * Getter of the control group.
     * @return the control group
     */
    public String getControl_group() {
        return control_group;
    }
    /**
     * Getter of the group to check with.
     * @return getter of the group
     */
    public String getCheck_group() {
        return check_group;
    }
    /**
     * Getter of the peptide file.
     * @return a File object with the peptide file
     */
    public File getPeptide_file() {
        return peptide_file;
    }
    /**
     * Getter of the uniqueness file.
     * @return a File object with the file object
     */
    public File getUniqueness_file() {
        return uniqueness_file;
    }
    /**
     * Setter of the group file.
     * @param file the file object with the group file in it.
     */
    public final void setGroupFile(final File file) {
        group_file = file;
    }
    /**
     * Sets the control group.
     * @param group the group to set as a control group
     */
    public final void setControlGroup(final String group) {
        control_group = group;
    }
    /**
     * Setter of the group to check with.
     * @param group the group to set as a check group
     */
    public final void setCheckGroup(final String group) {
        check_group = group;
    }
    /**
     * Sets the uniqueness file.
     * @param file a File object that represents the uniqueness file
     */
    public final void setUniquenessFile(final File file) {
        uniqueness_file = file;
    }
    /**
     * Sets the peptide file.
     * @param file a File object that represents the peptide file.
     */
    public final void setPeptideFile(final File file) {
        peptide_file = file;
    }
    /**
     * Does the check if all the things that are necessary are available.
     * @return true if everything is present
     */
    public final boolean checkRunnable() {
        return resource_file != null && group_file != null
                && control_group != null && check_group != null 
                && uniqueness_file != null && peptide_file != null;
    }
}
