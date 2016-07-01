/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interactivevolcano;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.WorkerStateEvent;
import javafx.embed.swing.SwingFXUtils;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.control.ListView;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javax.imageio.ImageIO;
import modules.AutoCompleteTextField;
import modules.VolcanoCanvasFiller;
import modules.Listeners;
import modules.NodeGenerator;
import nodes.Datapoint;
import nodes.DatapointCollection;
import nonguitasks.ThreadSeparater;
import calculators.FileCollection;

/**
 * The class that regulates all functionality of the application.
 *
 * @author Arne
 */
public class VolcanoController implements Initializable {
    @FXML
    private MenuBar menuBar;
    /**
     * The main pane that holds all other elements.
     */
    @FXML
    private AnchorPane anchor;
    /**
     * The intensity file selection option.
     */
    @FXML
    private MenuItem intensity_option;
    /**
     * The group selection file option.
     */
    @FXML
    private MenuItem group_option;
    /**
     * The button to make the volcano plot.
     */
    @FXML
    private Button plot_button;
    /**
     * The progress indicator.
     */
    @FXML
    private ProgressIndicator progress_indicator;
    /**
     * The pane that is the container of the stackpane and graphPane for the
     * zoom functionality.
     */
    @FXML
    private ScrollPane scrollPane;
    /**
     * The pane that hold the graphPane.
     */
    @FXML
    private StackPane stackPane;
    /**
     * The pane to draw on.
     */
    @FXML
    private Pane graphPane;
    /**
     * The peptide sequence file option.
     */
    @FXML
    private MenuItem peptide_sequence_option;
    /**
     * the uniqueness file selection option.
     */
    @FXML
    private MenuItem uniqueness_option;
    /**
     * The save image menu item.
     */
    @FXML
    private MenuItem saveImage;
    /**
     * The hide and show toggle button.
     */
    @FXML
    ToggleButton toggle_button;
    /**
     * The help menu item.
     */
    @FXML
    private MenuItem help;

    /**
     * The list view element in the scene.
     */
    private ListView<String> protein_list_view;
    /**
     * The list view element in the scene.
     */
    private ListView<String> peptide_list_view;
    /**
     * The resource file with protein intensities.
     */
    private File resource_file;
    /**
     * The file that contains all groups.
     */
    private File group_file;
    /**
     * The file that contains all peptide uniqueness.
     */
    private File uniqueness_file;
    /**
     * The file that contains all peptides per MPID.
     */
    private File peptide_file;
    /**
     * The autocomplete search field to search for gene or protein names and
     * highlight them.
     */
    private AutoCompleteTextField search_field;
    /**
     * The choice box for which group is the control sample.
     */
    private ComboBox<String> control_choice_box;
    /**
     * The choice box for which group is the non control group.
     */
    private ComboBox<String> target_choice_box;
    /**
     * The checker that does the checks if all necessary elements are present to
     * create the interactive volcano plot.
     */
    private final FileCollection sr = new FileCollection();
    /**
     * The location where the R script is temporarily stored.
     */
    private String rscript;
    /**
     * The directory that is used to store the result files in.
     */
    private String rscriptDir;
    /**
     * The place where the fileChooser is opened.
     */
    private File initialDir = null;
    /**
     * The collection of all dots.
     */
    DatapointCollection dotCol;
    /**
     * All listeners for the view.
     */
    Listeners listeners;
    /**
     * The class that creates the graphs.
     */
    VolcanoCanvasFiller cf;
    /**
     * The text above the control group box.
     */
    Text textControl;
    /**
     * The text above the check group box.
     */
    Text textCheck;
    /**
     * ToggleText.
     */
    Text toggleText;
    /**
     * the width of the graphPane.
     */
    double graphPaneWidth;
    /**
     * the height of the graphPane.
     */
    double graphPaneHeight;
    /**
     * Linked list with all previous X coordinates.
     */
    LinkedList<Double> Xcoordinates = new LinkedList<>();
    /**
     * Linked list with all previous Y coordinates.
     */
    LinkedList<Double> Ycoordinates = new LinkedList<>();
    /**
     * Text on the X-axis for scale of the logFC.
     */
    Text xAxisText;
    /**
     * Text on the Y-axis for scale of the logFC.
     */
    Text yAxisText;

    /**
     * Initializes the controller class.
     *
     * @param url
     * @param rb
     */
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        plot_button.setDisable(true);
        progress_indicator.setVisible(false);
        cf = new VolcanoCanvasFiller();
        listeners = new Listeners(cf);
        generateNodes();
        listeners.proteinListViewListener(protein_list_view, graphPane, peptide_list_view);
        listeners.controlComboBoxListener(control_choice_box, sr, plot_button, anchor);
        listeners.checkComboBoxListener(target_choice_box, sr, plot_button, anchor);
        listeners.windowResizeListener(anchor, scrollPane, graphPane, cf, toggle_button, xAxisText, yAxisText);
//        RScriptCreator scriptCreator = new RScriptCreator();
//        try {
//            rscript = scriptCreator.createTempRScript();
//            rscriptDir = scriptCreator.getDir();
//        } catch (IOException ex) {
//            Logger.getLogger(VolcanoController.class.getName()).log(Level.SEVERE, null, ex);
//        }
        search_field.setDisable(true);
        toggle_button.setDisable(true);
        setAnchors();

    }

    /**
     * Generates all initial nodes for the first view.
     */
    private void generateNodes() {
        graphPane.setStyle("-fx-background-color: #FFFFFF;");
        graphPaneWidth = graphPane.getPrefWidth();
        NodeGenerator ng = new NodeGenerator();
        graphPaneHeight = graphPane.getPrefHeight();
        protein_list_view = ng.generateListView();
        peptide_list_view = ng.generateListView();
        peptide_list_view.setPrefHeight(80.0);
        search_field = ng.generateAutoCompleteField(protein_list_view, cf, graphPane, dotCol, peptide_list_view);
        textControl = ng.generateText(540.0, 285.0, "Control group:");
        control_choice_box = ng.generateComboBox(150.0, 540.0, 290.0);
        control_choice_box.setDisable(true);
        textCheck = ng.generateText(540.0, 330.0, "Other group:");
        target_choice_box = ng.generateComboBox(150.0, 540.0, 335.0);
        toggleText = ng.generateText(0, 0, "unidentified\npeptides");
        target_choice_box.setDisable(true);
        toggle_button.setText("Show");
        toggle_button.setTextFill(Color.GREEN);
        xAxisText = new Text("Log2");
        yAxisText = new Text("-Log10");
        yAxisText.setRotate(270.0);
        anchor.getChildren().addAll(protein_list_view, search_field, control_choice_box, target_choice_box,
                textControl, textCheck, toggleText, peptide_list_view, xAxisText, yAxisText);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.NEVER);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        stackPane.setOnScroll(new EventHandler<ScrollEvent>() {
            @Override
            public void handle(ScrollEvent event) {
                if (event.getDeltaY() > 0) {
                    double x = event.getX();
                    double y = event.getY();
                    zoom(graphPane, x, y, 1.1);
                    Xcoordinates.add(x);
                    Ycoordinates.add(y);
                    event.consume();
                } else if (event.getDeltaY() < 0) {
                    if (!Xcoordinates.isEmpty()) {
                        zoom(graphPane, Xcoordinates.getLast(), Ycoordinates.getLast(), (1 / 1.1));
                        Xcoordinates.removeLast();
                        Ycoordinates.removeLast();
                    } else {
                        zoom(graphPane, 0, 0, (1 / 1.1));
                    }
                    event.consume();
                }
            }
        });
        // reset button for the plot
        anchor.setStyle("-fx-background-color: #E6E6E6;");
    }

    /**
     * The zoom function for the pane
     *
     * @param node the node to zoom on
     * @param centerX X coordinate of the center
     * @param centerY Y coordinate of the center
     * @param factor the factor to zoom with
     */
    private void zoom(Node node, double centerX, double centerY, double factor) {
        node.setScaleX(node.getScaleX() * factor);
        node.setScaleY(node.getScaleY() * factor);
        final Point2D center = node.localToParent(centerX, centerY);
        final Bounds bounds = node.getBoundsInParent();
        final double boundWidth = bounds.getWidth();
        final double boundHeight = bounds.getHeight();
        final double scaledBoundWidth = boundWidth * (factor - 1);
        final double scaledBoundHeight = boundHeight * (factor - 1);
        final double xr = 2 * (boundWidth / 2 - (center.getX() - bounds.getMinX())) / boundWidth;
        final double yr = 2 * (boundHeight / 2 - (center.getY() - bounds.getMinY())) / boundHeight;
        if (factor > 1 || !Ycoordinates.isEmpty()) {
            node.setTranslateX(node.getTranslateX() + xr * scaledBoundWidth / 2);
            node.setTranslateY(node.getTranslateY() + yr * scaledBoundHeight / 2);
            graphPaneWidth = graphPaneWidth * factor;
            graphPaneHeight = graphPaneHeight * factor;
        } else {
            node.setScaleX(1);
            node.setScaleY(1);
            node.setTranslateX(0);
            node.setTranslateY(0);
        }
    }

    /**
     * Loads the resource file using a fileChooser.
     *
     * @param event triggered when the menu item is clicked.
     */
    @FXML
    private void loadResourceFile(Event event) {
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Select protein intensity file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select protein intensity file");
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }
        FileChooser.ExtensionFilter fileExtensions
                = new FileChooser.ExtensionFilter(
                        "Intensity file", "*.mpks");

        fileChooser.getExtensionFilters().add(fileExtensions);
        resource_file = fileChooser.showOpenDialog(stage);
        if (resource_file != null && resource_file.getPath().endsWith("mpks")) {
            sr.setResourceFile(resource_file);
            initialDir = new File(resource_file.getParent());
            intensity_option.setText("Protein intensity file:\t" + resource_file.getName());
        }
        checkRunnable();

    }

    /**
     * Loads the group file which is chosen in the fileChooser.
     *
     * @param event triggered when the menu button is clicked
     * @throws FileNotFoundException
     * @throws IOException
     */
    @FXML
    private void loadGroupFile(ActionEvent event) throws FileNotFoundException, IOException {
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Select sample group file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select sample group file");
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }
        FileChooser.ExtensionFilter fileExtensions
                = new FileChooser.ExtensionFilter(
                        "group file", "*.txt", "*.csv");

        fileChooser.getExtensionFilters().add(fileExtensions);
        group_file = fileChooser.showOpenDialog(stage);
        if (group_file != null) {
            if (checkGroupFile(group_file)) {
                sr.setGroupFile(group_file);
                initialDir = new File(group_file.getParent());
                BufferedReader br = new BufferedReader(new FileReader(group_file.getPath()));
                String line;
                ArrayList<String> fxcol = new ArrayList<>();
                while ((line = br.readLine()) != null) {
                    if (!fxcol.contains(line.split("\\s")[1])) {
                        fxcol.add(line.split("\\s")[1]);
                    }
                }
                target_choice_box.setItems(FXCollections.observableArrayList(fxcol));
                target_choice_box.setDisable(false);
                control_choice_box.setItems(FXCollections.observableArrayList(fxcol));
                control_choice_box.setDisable(false);
                group_option.setText("Sample group file:\t\t" + group_file.getName());
                checkRunnable();
            }
        }
    }

    /**
     * Loads the sequence file with a fileChooser.
     *
     * @param event triggered when the menu button is clicked
     */
    @FXML
    private void loadSequenceFile(ActionEvent event) {
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Select sample peptide sequence file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select sample peptide sequence file");
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }
        FileChooser.ExtensionFilter fileExtensions
                = new FileChooser.ExtensionFilter(
                        "sequence file", "*.txt");

        fileChooser.getExtensionFilters().add(fileExtensions);
        peptide_file = fileChooser.showOpenDialog(stage);
        if (peptide_file != null) {
            sr.setPeptideFile(peptide_file);
            initialDir = new File(peptide_file.getParent());
            peptide_sequence_option.setText("Peptide sequence file:\t" + peptide_file.getName());
        }
        checkRunnable();
    }

    /**
     * Loads the peptide uniqueness file which is created by ProQu.
     *
     * @param event triggered when the menu button is selected
     */
    @FXML
    private void loadUniqueness(ActionEvent event) {
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Select peptide uniqueness file");
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Peptide uniqueness file selected");
        if (initialDir != null) {
            fileChooser.setInitialDirectory(initialDir);
        }
        FileChooser.ExtensionFilter fileExtensions
                = new FileChooser.ExtensionFilter(
                        "uniqueness file", "*.csv");

        fileChooser.getExtensionFilters().add(fileExtensions);
        uniqueness_file = fileChooser.showOpenDialog(stage);
        if (uniqueness_file != null) {
            sr.setUniquenessFile(uniqueness_file);
            initialDir = new File(uniqueness_file.getParent());
            uniqueness_option.setText("Peptide sequence file:\t" + uniqueness_file.getName());
        }
        checkRunnable();
    }

    /**
     * Creates the plot when the button is clicked.
     *
     * @param event triggered when the make plot button is clicked
     * @throws IOException
     * @throws InterruptedException
     */
    @FXML
    private void makePlot(ActionEvent event) throws IOException, InterruptedException {
        // Pass all elements that are used to another class that uses a different thread then the UI thread
        // to prevent freezing of the UI.
        ThreadSeparater esc = new ThreadSeparater(sr);
        // binds the progress of the external process to the progress indicator to keep it running
        // while the other thread is still executing the R script
        progress_indicator.visibleProperty().bind(esc.runningProperty());
        // when everything is done correctly this fires.
        esc.setOnSucceeded(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                dotCol = esc.getValue(); // get the return value of the service
                cf.createVolcanoPlot(graphPane, anchor, dotCol, scrollPane.getWidth(), scrollPane.getHeight());
                HashMap<String, ArrayList<Datapoint>> protein_list = (HashMap<String, ArrayList<Datapoint>>) dotCol.getDatapoints().clone();
                protein_list.remove("Unknown");
                ObservableList<String> data = FXCollections.observableArrayList(protein_list.keySet());
                FXCollections.sort(data);
                protein_list.clear(); // to save space
                protein_list_view.setItems(data);
                listeners.setDatapointCol(dotCol);
                search_field.getEntries().addAll(data);
                search_field.updateDotCollection(dotCol);
                search_field.setDisable(false);
                listeners.setIsPlotted(true);
                toggle_button.setDisable(false);
                toggle_button.fire();
                toggle_button.fire(); // to reset the state of the toggle button to its pre plotted state.
            }
        });
        // when the other thread failed to execute this fires.
        esc.setOnFailed(new EventHandler<WorkerStateEvent>() {
            @Override
            public void handle(WorkerStateEvent workerStateEvent) {
                Text contentNode = new Text(35, 30, "Please make sure that R is in the classpath\nof your system and you have selected all the correct files.");
                contentNode.setFont(Font.font("Verdana", 12));
                Stage stage = new Stage();
                Pane paneTwo = new Pane(contentNode);
                Scene scene = new Scene(paneTwo, 250, 75);
                stage.setScene(scene);
                stage.setTitle("Rscript error");
                stage.show();
            }
        });
        esc.restart(); //start service

    }

    /**
     * Checks if the R script can get all its necessary elements.
     */
    private void checkRunnable() {
        if (sr.checkRunnable()) {
            plot_button.setDisable(false);
        } else {
            plot_button.setDisable(true);
        }
        anchor.getChildren().remove(plot_button);
        anchor.getChildren().add(plot_button);
    }

    /**
     * Checks if the given file
     *
     * @param file the file to check
     * @return true if the file only contains two columns
     * @throws FileNotFoundException
     * @throws IOException
     */
    private Boolean checkGroupFile(File file) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(file));
        if (br.readLine().trim().split("\\s").length != 2) {
            Text contentNode = new Text(35, 30, "Please select a correct group\n"
                    + "        file and try again.");
            contentNode.setFont(Font.font("Verdana", 12));
            Stage stage = new Stage();
            Pane paneTwo = new Pane(contentNode);
            Scene scene = new Scene(paneTwo, 250, 75);
            stage.setScene(scene);
            stage.setTitle("Wrong file");
            stage.show();
            return false;
        }
        return true;
    }

    @FXML
    private void activateHelp(ActionEvent event) {
        Text contentNode = new Text(10, 20, "This is the manual on how to use the Interactive volcano plot tool:\n\n"
                + "You have to upload multiple files to use this tool.\n"
                + "Click the File menu where you found this help as well to see what\n"
                + "has to be uploaded.\n"
                + "For the peptide uniqueness file the ProQu programm has to be used\n"
                + "to get the uniqueness per per protein.\n"
                + "When the file is selected that specifies the group for each sample,\n"
                + "the control sample and the sample to check with have to be selected\n"
                + "in the choice boxes.\n"
                + "If everything is selected the make plot button will be clickable.\n"
                + "Click on this button to create a volcano plot.\n"
                + "This may take some minutes due to the calculations.\n"
                + "When the plot is showing one can use the search field and in the\n"
                + "scrollable list to select a gene/protein that you are interested in\n"
                + "to see which peptides belong to the selected gene/protein.");
        contentNode.setFont(Font.font("Verdana", 12));
        Stage stage = new Stage();
        Pane paneTwo = new Pane(contentNode);
        Scene scene = new Scene(paneTwo, 435, 260);
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void saveImage(ActionEvent event) {
        WritableImage image = graphPane.snapshot(new SnapshotParameters(), null);
        Stage stage = new Stage();
        stage.setAlwaysOnTop(true);
        stage.setTitle("Select folder to save the image in");
        DirectoryChooser dirChooser = new DirectoryChooser();
        dirChooser.setTitle("Select folder to save the image in");
        File dir = dirChooser.showDialog(stage);
        if (dir != null) {
            File imageFile = new File(dir.getAbsolutePath() + "/volcano.png");
            try {
                ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", imageFile);
            } catch (IOException e) {
                Logger.getLogger(VolcanoController.class.getName()).log(Level.SEVERE, null, e);
            }
        }
    }

    /**
     * Sets all anchors on the anchor pane.
     */
    private void setAnchors() {
        AnchorPane.setBottomAnchor(plot_button, 15.0);
        AnchorPane.setRightAnchor(plot_button, 15.0);
        AnchorPane.setBottomAnchor(progress_indicator, 15.0);
        AnchorPane.setRightAnchor(progress_indicator, 95.0);
        AnchorPane.setTopAnchor(search_field, 45.0);
        AnchorPane.setRightAnchor(search_field, 15.0);
        AnchorPane.setTopAnchor(protein_list_view, 90.0);
        AnchorPane.setRightAnchor(protein_list_view, 15.0);
        AnchorPane.setTopAnchor(textControl, 200.0);
        AnchorPane.setRightAnchor(textControl, 82.0);
        AnchorPane.setTopAnchor(control_choice_box, 220.0);
        AnchorPane.setRightAnchor(control_choice_box, 15.0);
        AnchorPane.setTopAnchor(textCheck, 250.0);
        AnchorPane.setRightAnchor(textCheck, 90.0);
        AnchorPane.setTopAnchor(target_choice_box, 270.0);
        AnchorPane.setRightAnchor(target_choice_box, 15.0);
        AnchorPane.setTopAnchor(graphPane, 45.0);
        AnchorPane.setBottomAnchor(graphPane, 15.0);
        AnchorPane.setLeftAnchor(graphPane, 15.0);
        AnchorPane.setRightAnchor(graphPane, 185.0);
        graphPane.setPrefSize(graphPane.getWidth(), graphPane.getHeight());
        AnchorPane.setTopAnchor(scrollPane, 45.0);
        AnchorPane.setBottomAnchor(scrollPane, 15.0);
        AnchorPane.setLeftAnchor(scrollPane, 15.0);
        AnchorPane.setRightAnchor(scrollPane, 185.0);
        AnchorPane.setTopAnchor(stackPane, 45.0);
        AnchorPane.setBottomAnchor(stackPane, 15.0);
        AnchorPane.setLeftAnchor(stackPane, 15.0);
        AnchorPane.setRightAnchor(stackPane, 185.0);
        AnchorPane.setRightAnchor(menuBar, 0.0);
        AnchorPane.setLeftAnchor(menuBar, 0.0);
        AnchorPane.setRightAnchor(toggle_button, 90.0);
        AnchorPane.setTopAnchor(toggle_button, 310.0);
        AnchorPane.setRightAnchor(toggleText, 15.0);
        AnchorPane.setTopAnchor(toggleText, 305.0);
        AnchorPane.setTopAnchor(peptide_list_view, 355.0);
        AnchorPane.setRightAnchor(peptide_list_view, 15.0);
        AnchorPane.setTopAnchor(yAxisText, ((scrollPane.getPrefHeight()/2.0)+45));
        AnchorPane.setLeftAnchor(yAxisText, -12.0);
        AnchorPane.setTopAnchor(xAxisText, (57.0+scrollPane.getPrefHeight()));
        AnchorPane.setLeftAnchor(xAxisText, scrollPane.getPrefWidth()/2 + 10.0);
    }

    @FXML
    private void toggleAction(ActionEvent event) {
        cf.setVisible(dotCol, toggle_button);
    }

}
