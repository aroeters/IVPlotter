/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.util.Timer;
import java.util.TimerTask;
import javafx.application.Platform;
import rscripts.RScriptRequirementChecker;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import nodes.DatapointCollection;

/**
 *
 * @author Arne
 */
public class Listeners {

    /**
     * The selected item in the listView.
     */
    private String listViewChoice = "";
    /**
     * The collection with all points in it.
     */
    DatapointCollection dotCol;
    /**
     * The canvasfiller that handles the drawing.
     */
    CanvasFiller cf;
    /**
     * Boolean if the volcano plot is made.
     */
    private Boolean isPlotted = false;

    /**
     * Constructor of the class.
     *
     * @param cf the canvas filler.
     */
    public Listeners(CanvasFiller cf) {
        this.cf = cf;
    }

    /**
     * The listener for the listView.
     *
     * @param lv the listview to listen to.
     * @param graphPane the pane to draw on.
     */
    public void listViewListener(ListView<String> lv, Pane graphPane) {
        lv.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                if (newValue != null) {
                    cf.handleHighlight(newValue, dotCol, graphPane);
                }
            }
        });
    }

    /**
     * Sets or updates the DatapointCollection.
     *
     * @param dotCol the collection with all points.
     */
    public final void setDotCol(DatapointCollection dotCol) {
        this.dotCol = dotCol;
    }

    /**
     * Setter of the isPlotted boolean.
     *
     * @param isPlotted true if the canvas shows the volcano plot
     */
    public final void setIsPlotted(Boolean isPlotted) {
        this.isPlotted = isPlotted;
    }

    /**
     * Listens to the control combo box.
     *
     * @param cb the combobox to listen to
     * @param requiChecker checks the requirements for the r script
     * @param plotButton the button to active the externalscript runner with
     * @param anchor the anchorpane on which the button is attached
     */
    public final void controlComboBoxListener(ComboBox<String> cb, RScriptRequirementChecker requiChecker, Button plotButton, AnchorPane anchor) {
        cb.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                requiChecker.setControlGroup(newValue);
                // check if all requirements are met, if not disable the plot button else activate it.
                plotButton.setDisable(!requiChecker.checkRunnable());
                anchor.getChildren().remove(plotButton);
                anchor.getChildren().add(plotButton);
            }
        });
    }

    /**
     * Listens to the Check combo box.
     *
     * @param cb the combobox to listen to
     * @param requiChecker checks the requirements for the r script
     * @param plotButton the button to active the externalscript runner with
     * @param anchor the anchorpane on which the button is attached
     */
    public final void checkComboBoxListener(ComboBox<String> cb, RScriptRequirementChecker requiChecker, Button plotButton, AnchorPane anchor) {
        cb.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue ov, String oldValue, String newValue) {
                requiChecker.setCheckGroup(newValue);
                // check if all requirements are met, if not disable the plot button else activate it.
                plotButton.setDisable(!requiChecker.checkRunnable());
                anchor.getChildren().remove(plotButton);
                anchor.getChildren().add(plotButton);
            }
        });
    }

    public final void windowResizeListener(AnchorPane anchor, Pane graphPane, CanvasFiller cf) {
        final ChangeListener<Number> listener = new ChangeListener<Number>() {
            final Timer timer = new Timer(); // Use a timer to execute a new command
            TimerTask task = null; //task to execute after the delay
            final long delayTime = 200; // delay for operation to be done

            @Override
            public void changed(ObservableValue<? extends Number> observable, Number oldValue, final Number newValue) {
                if (task != null) {
                    task.cancel(); // cancel a task that had been running.
                }
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        Platform.runLater(new Runnable() {
                            public void run() {
                                if (isPlotted) {
                                    cf.createVolcanoPlot(graphPane, anchor, dotCol);
                                    cf.handleHighlight(listViewChoice, dotCol, graphPane);
                                }
                            }
                        });
                    }
                }, delayTime);
            }
        };
        graphPane.widthProperty().addListener(listener);
    }
}
