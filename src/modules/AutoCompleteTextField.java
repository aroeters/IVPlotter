package modules;

import java.util.LinkedList;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Side;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.CustomMenuItem;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.Pane;
import nodes.DatapointCollection;

/**
 *
 * @author Arne
 */
public class AutoCompleteTextField extends TextField {

    /**
     * The existing autocomplete entries.
     */
    private final SortedSet<String> entries;
    /**
     * The popup used to select an entry.
     */
    private ContextMenu entriesPopup;
    /**
     * The list_view the field is linked to.
     */
    private ListView<String> list_view;
    /**
     * The CanvasFiller to draw everything on the canvas
     */
    private CanvasFiller cf;
    /**
     * The pane to draw on.
     */
    private Pane graphPane;
    /**
     * The dotCollection.
     */
    private DatapointCollection datapointCol;

    /**
     * Construct a new autocomplete text field.
     *
     * @param protein_list_view the list with all elements to autocomplete on.
     * @param cf the class that fills the canvas.
     * @param pane the pane to draw on.
     * @param dotCollection the collection with all dots.
     */
    public AutoCompleteTextField(ListView<String> protein_list_view, CanvasFiller cf, Pane pane, DatapointCollection dotCollection, ListView<String> peptide_list_view) {
        super(); // calls the constructor from the TextField class.
        list_view = protein_list_view;
        entries = new TreeSet<>();
        entriesPopup = new ContextMenu();
        this.cf = cf;
        graphPane = pane;
        datapointCol = dotCollection;
        // add a listener to the text field and shows a popup with all elements.
        textProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if (getText().length() == 0) {
                    entriesPopup.hide();
                } else {
                    LinkedList<String> searchResult = new LinkedList<>();
                    searchResult.addAll(entries.subSet(getText(), getText() + Character.MAX_VALUE));
                    if (entries.size() > 0) {
                        populatePopup(searchResult);
                        if (!entriesPopup.isShowing()) {
                            entriesPopup.show(AutoCompleteTextField.this, Side.BOTTOM, 0, 0);
                        }
                    } else {
                        entriesPopup.hide();
                    }
                }
                if (datapointCol != null) {
                    if (datapointCol.getDatapoints().containsKey(newValue)) {
                        cf.handleHighlight(newValue, datapointCol, graphPane);
                        cf.fillPeptidesList(peptide_list_view, datapointCol, graphPane);
                    }
                }
            }
        });
        // checks if the node is still focussed and if not makes the popup hide.
        focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean aBoolean2) {
                entriesPopup.hide();
            }
        });
    }

    /**
     * Get the existing set of autocomplete entries.
     *
     * @return The existing autocomplete entries.
     */
    public SortedSet<String> getEntries() {
        return entries;
    }

    public final void updateDotCollection(DatapointCollection dotCol) {
        this.datapointCol = dotCol;
    }

    /**
     * Populate the entry set with the given search results. Display is limited
     * to 10 entries, for performance.
     *
     * @param searchResult The set of matching strings.
     */
    private void populatePopup(List<String> searchResult) {
        List<CustomMenuItem> menuItems = new LinkedList<>();
        // If you'd like more entries, modify this line.
        int maxEntries = 10;
        int count = Math.min(searchResult.size(), maxEntries);
        for (int i = 0; i < count; i++) {
            final String result = searchResult.get(i);
            Label entryLabel = new Label(result);
            CustomMenuItem item = new CustomMenuItem(entryLabel, true);
            item.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
                    setText(result);
                    entriesPopup.hide();
                }
            });
            menuItems.add(item);
        }
        entriesPopup.getItems().clear();
        entriesPopup.getItems().addAll(menuItems);

    }
}
