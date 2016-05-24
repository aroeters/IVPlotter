/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import java.util.ArrayList;
import java.util.HashMap;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Callback;
import nodes.Datapoint;
import nodes.DatapointCollection;

/**
 *
 * @author Arne
 */
public class VolcanoCanvasFiller {

    /**
     * All shapes in the plot.
     */
    private HashMap<String, Shape> shapes = new HashMap<>();
    /**
     * All used colors stored of the current layout.
     */
    private HashMap<String, String> highlightColors;
    /**
     * Everything that has to be unHighlighted in the plot.
     */
    ArrayList<String> unHighlight = new ArrayList<>();
    /**
     * The height of the canvas to draw on.
     */
    double canvasHeight;
    /**
     * The width of the canvas to draw on.
     */
    double canvasWidth;
    /**
     * The log fc scaled to the canvas.
     */
    double logFCScaled;
    /**
     * The pvalue scaled to the canvas.
     */
    double pvalScaled;
    /**
     * The minimal LogFC converted to a absolute value.
     */
    double absoluteMinLogFC;
    /**
     * The maximum pvalue.
     */
    double maxPval;
    /**
     * The difference between the minimal and maximal logfc.
     */
    double difference;
    /**
     * The point on the x axis.
     */
    double zeroPoint;
    /**
     * The maximal logfc rounded up.
     */
    Integer ceiledMaxLogFC;
    /**
     * The minimal logfc rounded down.
     */
    Integer flooredMinLogFC;
    /**
     * The previous protein name selected.
     */
    private String previousName;
    /**
     * The size of the circles.
     */
    private double circleSize;
    /**
     * The size of the circles.
     */
    private double rectSize;
    /**
     * True if unidentified are present on the canvas.
     */
    private Boolean isVisible = true;

    /**
     * clears the plot canvas.
     *
     * @param graphPane the Pane to add the shapes to
     */
    public final void clearCanvas(Pane graphPane) {
        graphPane.getChildren().clear();
        graphPane.setStyle("-fx-background-color: #FFFFFF;");
    }

    /**
     * Creates a volcanoplot on the given pane.
     *
     * @param graphPane the pane to draw on.
     * @param anchor the anchor pane where the pane is a child of.
     * @param datapointCol the collection with all points in it.
     * @param scrollWidth the width of the scroll pane.
     * @param scrollHeight the height of the scroll pane.
     */
    public final void createVolcanoPlot(Pane graphPane, AnchorPane anchor, DatapointCollection datapointCol, double scrollWidth, double scrollHeight) {
        graphPane.setPrefSize(scrollWidth, scrollHeight);
        clearCanvas(graphPane);
        canvasHeight = graphPane.getHeight() - (graphPane.getHeight() / 10.5);
        canvasWidth = graphPane.getWidth() - (graphPane.getWidth() / 12.5);
        circleSize = canvasHeight / 300;
        rectSize = canvasHeight / 150;
        absoluteMinLogFC = getAbsoluteMinLogFC(datapointCol.getMinLogFC());
        maxPval = Math.ceil(datapointCol.getMaxPval());
        flooredMinLogFC = (int) Math.floor(datapointCol.getMinLogFC());
        ceiledMaxLogFC = (int) Math.ceil(datapointCol.getMaxLogFC());
        difference = calculateDifference(flooredMinLogFC, ceiledMaxLogFC);
        zeroPoint = (absoluteMinLogFC / difference) * canvasWidth;
        drawTicks(graphPane);
        HashMap<String, ArrayList<Datapoint>> datapointMap = datapointCol.getDatapoints();
        for (String key : datapointMap.keySet()) {
            for (Datapoint datapoint : datapointMap.get(key)) {
                Shape shape;
                if (datapoint.getProteinNames().size() == 1) {
                    shape = new Circle(circleSize);
                } else {
                    shape = new Rectangle(rectSize, rectSize);
                }
                logFCScaled = (((canvasWidth * datapoint.getLogFC()) / difference)) + zeroPoint + (15.5 * (graphPane.getWidth() / graphPane.getPrefWidth()));
                pvalScaled = (canvasHeight - ((datapoint.getPvalue() * canvasHeight) / maxPval)) + (13.5 * (graphPane.getHeight() / graphPane.getPrefHeight()));
                shape.relocate(logFCScaled, pvalScaled);
                if (datapoint.isIdentified()) {
                    shape.setFill(Color.DARKGREY);
                } else {
                    shape.setFill(Color.DARKGREY);
                    shape.setOpacity(0.5);
                }
                // To retrieve the data later in a fast way, setting it as the id.
                shape.setId("MPID : " + datapoint.getMPID() + "\nLogFC : " + datapoint.getLogFC() + "\np-value : " + datapoint.getPvalue() + "\nAA sequence : " + datapoint.getSequence() + "\nProtein: " + datapoint.getProteinNames());
                shape.setOnMouseEntered(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        // install a tooltip on a mouse entered
                        Tooltip tooltip = new Tooltip(shape.getId());
                        tooltip.install(shape, tooltip);
                        shape.setFill(Color.BLACK);
                    }
                });
                // Set the onMouseExited event with everything that has to be done.
                shape.setOnMouseExited(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        shape.setFill(Color.DARKGREY);
                    }
                });
                shapes.put(datapoint.getMPID(), shape);
                graphPane.getChildren().add(shape);
            }
        }
    }

    /**
     * Draws the ticks along the x and y axis for the volcano plot.
     *
     * @param graphPane the pane to draw on
     */
    private void drawTicks(Pane graphPane) {
        double xAxisHeight = canvasHeight + (canvasHeight / 11);
        double canvasHeightPart = canvasHeight / maxPval;
        double canvasWidthPart = canvasWidth / difference;
        double xAxisPlacement;
        double yAxisPlacement;
        Text text;
        Line line;
        double start = 0;
        double end = 0;
        for (Integer pval = 0; pval <= maxPval; pval++) {
            yAxisPlacement = (canvasHeightPart * (maxPval - pval));
            if (pval == maxPval) {
                end = yAxisPlacement + (15.5 * (xAxisHeight / graphPane.getPrefHeight()));
            } else if (pval == 0) {
                start = yAxisPlacement + (17.5 * (xAxisHeight / graphPane.getPrefHeight()));
            }
            text = new Text(0, (20 * (xAxisHeight / graphPane.getPrefHeight())) + yAxisPlacement, pval.toString());
            line = new Line(10, yAxisPlacement + (15.5 * (xAxisHeight / graphPane.getPrefHeight())), 15.5, yAxisPlacement + (15.5 * (xAxisHeight / graphPane.getPrefHeight())));
            graphPane.getChildren().addAll(text, line);
        }

        Integer counter = flooredMinLogFC;
        for (Integer widthPart = 0; widthPart <= difference; widthPart++) {
            if (widthPart % 2 == 0) {
                xAxisPlacement = canvasWidthPart * widthPart;
                counter = flooredMinLogFC + widthPart;
                if (counter < 0) {
                    text = new Text(xAxisPlacement + 10.5, xAxisHeight, counter.toString());
                } else {
                    text = new Text(xAxisPlacement + 13.5, xAxisHeight, counter.toString());
                }
                line = new Line(xAxisPlacement + 17.5, canvasHeight + (25 * (xAxisHeight / graphPane.getPrefHeight())), xAxisPlacement + 17.5, canvasHeight + (20 * (xAxisHeight / graphPane.getPrefHeight())));
                graphPane.getChildren().addAll(text, line);
            }
        }
        Line yLine = new Line(15.5, start, 15.5, end);
        Line xLine = new Line(15.5, start, graphPane.getWidth() - (15.5 * (graphPane.getWidth() / graphPane.getPrefWidth())), start);
        graphPane.getChildren().addAll(yLine, xLine);
    }

    /**
     * Handles the highlighting of the points that belong to the chosen
     * protein/gene name.
     *
     * @param proteinName the protein or genename that is selected
     * @param datapointCol all points in the graph
     * @param graphPane the pane to draw on
     */
    public final void handleHighlight(final String proteinName, final DatapointCollection datapointCol, Pane graphPane) {
        if (previousName != null && datapointCol.getDatapoints().containsKey(previousName) && !unHighlight.isEmpty()) {
            // set all dots that were from the previous selection to unhighlighted points.
            for (String MPID : unHighlight) {
                Shape shape = shapes.get(MPID);
                shape.setFill(Color.DARKGREY);
                shape.setOnMouseEntered(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        shape.setFill(Color.BLACK);
                    }
                });
                shape.setOnMouseExited(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        Tooltip tooltip = new Tooltip(shape.getId());
                        tooltip.install(shape, tooltip);
                        shape.setFill(Color.DARKGREY);
                    }
                });
                graphPane.getChildren().remove(shape);
                graphPane.getChildren().add(shape);
            }
            unHighlight.clear();
        }
        highlightColors = new HashMap<>();
        if (proteinName != null && datapointCol.getDatapoints().containsKey(proteinName)) {
            Integer r, g, b;
            String[] splitRGB;
            // highlight all dots from the given protein
            for (Datapoint datapoint : datapointCol.getDatapoints().get(proteinName)) {
                Shape shape = shapes.get(datapoint.getMPID());
                if (highlightColors.containsKey(datapoint.getSequence())) {
                    splitRGB = highlightColors.get(datapoint.getSequence()).split("\\.");
                    r = Integer.parseInt(splitRGB[0]);
                    g = Integer.parseInt(splitRGB[1]);
                    b = Integer.parseInt(splitRGB[2]);
                } else {
                    r = (int) Math.ceil(Math.random() * 255);
                    g = (int) Math.ceil(Math.random() * 255);
                    b = (int) Math.ceil((Math.random() * 255));
                    highlightColors.put(datapoint.getSequence(), (r + "." + g + "." + b));
                }
                shape.setFill(Color.rgb(r, g, b));
                shape.setOnMouseEntered(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        // install a tooltip in a mouse entered event
                        Tooltip tooltip = new Tooltip(shape.getId());
                        tooltip.install(shape, tooltip);
                        shape.setFill(Color.DARKRED);
                    }
                });
                shape.setOnMouseExited(new EventHandler<Event>() {
                    String[] splitRGB;
                    Integer r, g, b;

                    @Override
                    public void handle(Event event) {
                        String[] splitRGB = highlightColors.get(datapoint.getSequence()).split("\\.");
                        r = Integer.parseInt(splitRGB[0]);
                        g = Integer.parseInt(splitRGB[1]);
                        b = Integer.parseInt(splitRGB[2]);
                        shape.setFill(Color.rgb(r, g, b));
                    }
                });
                unHighlight.add(datapoint.getMPID());
                graphPane.getChildren().remove(shape);
                graphPane.getChildren().add(shape);
            }
            this.previousName = proteinName;
        }
    }

    /**
     * When the previous canvas is toggled between fullscreen and windowed
     * everything has to be redrawn to scale. This process removes the highlight
     * and therefore it has to be redrawn in the previous colors to keep the
     * same layout.
     *
     * @param datapointCol all datapoints
     * @param graphPane the pane to draw on
     */
    public final void handlePreviousHighlight(DatapointCollection datapointCol, Pane graphPane) {
        if (this.previousName != null && datapointCol.getDatapoints().containsKey(this.previousName)) {
            Integer r, g, b;
            String[] splitRGB;
            // highlight all dots from the given protein
            for (Datapoint datapoint : datapointCol.getDatapoints().get(this.previousName)) {
                Shape shape = shapes.get(datapoint.getMPID());
                splitRGB = highlightColors.get(datapoint.getSequence()).split("\\.");
                r = Integer.parseInt(splitRGB[0]);
                g = Integer.parseInt(splitRGB[1]);
                b = Integer.parseInt(splitRGB[2]);
                shape.setFill(Color.rgb(r, g, b));
                shape.setOnMouseEntered(new EventHandler<Event>() {
                    @Override
                    public void handle(Event event) {
                        // install a tooltip in a mouse entered event
                        Tooltip tooltip = new Tooltip(shape.getId());
                        tooltip.install(shape, tooltip);
                        shape.setFill(Color.DARKRED);
                    }
                });
                shape.setOnMouseExited(new EventHandler<Event>() {
                    String[] splitRGB;
                    Integer r, g, b;

                    @Override
                    public void handle(Event event) {
                        String[] splitRGB = highlightColors.get(datapoint.getSequence()).split("\\.");
                        r = Integer.parseInt(splitRGB[0]);
                        g = Integer.parseInt(splitRGB[1]);
                        b = Integer.parseInt(splitRGB[2]);
                        shape.setFill(Color.rgb(r, g, b));
                    }
                });
                unHighlight.add(datapoint.getMPID());
                graphPane.getChildren().remove(shape);
                graphPane.getChildren().add(shape);
            }
        }
    }

    /**
     * Toggles the unidentified to visible and invisible.
     *
     * @param datapointCol The dotCollection to het the datapoints from.
     * @param toggle_button true if the peptides should show.
     */
    public final void setVisible(DatapointCollection datapointCol, ToggleButton toggle_button) {
        if (!isVisible) {
            isVisible = true;
            toggle_button.setText("Show");
            toggle_button.setTextFill(Color.GREEN);
        } else {
            isVisible = false;
            toggle_button.setText("Hide");
            toggle_button.setTextFill(Color.RED);
        }
        for (Datapoint datapoint : datapointCol.getDatapoints().get("Unknown")) {
            shapes.get(datapoint.getMPID()).setVisible(isVisible);
        }
    }
    
    /**
     * Handles the filling of the peptide list with the custom listView cells.
     * @param peptide_list_view the listView to add the custom cells to
     * @param datapointCol all datapoints in the canvas
     * @param graphPane the pane to draw on
     */
    public final void fillPeptidesList(ListView<String> peptide_list_view, DatapointCollection datapointCol, Pane graphPane) {
        peptide_list_view.setCellFactory(new Callback<ListView<String>, ListCell<String>>() {
            @Override
            public ListCell<String> call(ListView<String> p) {
                ListCell<String> cell = new ListCell<String>() {
                    @Override
                    protected void updateItem(String t, boolean bln) {
                        super.updateItem(t, bln);
                        if (t != null) {
                            String[] rgb = highlightColors.get(t).split("\\.");
                            Integer r = Integer.parseInt(rgb[0]);
                            Integer g = Integer.parseInt(rgb[1]);
                            Integer b = Integer.parseInt(rgb[2]);
                            ColorPicker colorPicker = new ColorPicker();
                            colorPicker.setValue(Color.rgb(r, g, b));
                            colorPicker.setLayoutY(10);

                            colorPicker.setOnAction((ActionEvent a) -> {
                                String secondRGB = (int) (colorPicker.getValue().getRed() * 255) + "." + (int) (colorPicker.getValue().getGreen() * 255) + "." + (int) (colorPicker.getValue().getBlue() * 255);
                                highlightColors.put(peptide_list_view.getSelectionModel().getSelectedItem(), secondRGB);
                                setTextFill(Color.rgb((int) (colorPicker.getValue().getRed() * 255), (int) (colorPicker.getValue().getGreen() * 255), (int) (colorPicker.getValue().getBlue() * 255)));
                                handlePreviousHighlight(datapointCol, graphPane);
                            });
                            setOnMouseEntered(new EventHandler<MouseEvent>() {
                                @Override
                                public void handle(MouseEvent event) {
                                    if (!getChildren().contains(colorPicker)) {
                                        setText("\t" + t);
                                        getChildren().add(colorPicker);
                                    }
                                    peptide_list_view.getSelectionModel().select(getIndex());
                                }
                            });
                            setText(t);
                            setTextFill(Color.rgb(r, g, b));
                        }
                    }
                };
                return cell;
            }
        });
        peptide_list_view.setItems(FXCollections.observableArrayList(highlightColors.keySet()));
    }

    /**
     * Calculates the difference between the minimal and maximal logFC
     *
     * @param minLogFC minimal logfc of the dot collection
     * @param maxLogFC maximal logfc of the dot collection
     * @return a double with that represents the difference between the to given
     * values
     */
    private double calculateDifference(double minLogFC, double maxLogFC) {
        if (minLogFC < 0 && maxLogFC < 0) {
            return -1 * (minLogFC - maxLogFC);
        } else if (minLogFC < 0) {
            return (-1 * minLogFC) + maxLogFC;
        } else {
            return maxLogFC - minLogFC;
        }
    }

    /**
     * Returns the absolute minimal logfc value
     *
     * @param minLogFC the minimal logfc
     * @return the absolute minimal logfc
     */
    private double getAbsoluteMinLogFC(double minLogFC) {
        if (minLogFC < 0) {
            return Math.ceil(-1 * minLogFC);
        } else {
            return Math.ceil(minLogFC);
        }
    }
}
