/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package modules;

import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import nodes.DatapointCollection;

/**
 *
 * @author Arne
 */
public class NodeGenerator {
    /**
     * Generates a listView in the upper right corner of the pane.
     * @return a listview node
     */
    public final ListView<String> generateUpperRightListView() {
        ListView<String> list_view = new ListView<>();
        list_view.setLayoutX(540.0);
        list_view.setLayoutY(75.0);
        list_view.setPrefHeight(150.0);
        list_view.setPrefWidth(150.0);
        return list_view;
    }
    /**
     * Generates a autocomplete textfield.
     * @param lv a listview with all elements in it
     * @param cf the canvas filler to handle the drawing
     * @param pane the pane to draw on
     * @param dotCol the collection with all dots in it
     * @return the autocomplete textfield
     */
    public final AutoCompleteTextField generateAutoCompleteField(ListView<String> lv,
            CanvasFiller cf, Pane pane, DatapointCollection dotCol) {
        AutoCompleteTextField search_field = new AutoCompleteTextField(lv, cf, pane, dotCol);
        search_field.setLayoutX(540.0);
        search_field.setLayoutY(40.0);
        search_field.setPrefHeight(30.0);
        search_field.setPrefWidth(150.0);
        search_field.setPromptText("Protein name");
        return search_field;
    }
    /**
     * Generates text.
     * @param layoutX the x position of the text
     * @param layoutY the y position of the text
     * @param text the text to display
     * @return a text node
     */
    public final Text generateText(double layoutX, double layoutY, String text) {
        Text newText = new Text();
        newText.setLayoutX(layoutX);
        newText.setLayoutY(layoutY);
        newText.setText(text);
        return newText;
    }
    /**
     * Generates a combobox.
     * @param prefWidth the preffered width of the combo box
     * @param layoutX the position on the x axis
     * @param layoutY the position on the y axis
     * @return a combobox node with string items in it
     */
    public final ComboBox<String> generateComboBox(double prefWidth, double layoutX, double layoutY) {
        ComboBox<String> cb = new ComboBox<>();
        cb.setPrefWidth(prefWidth);
        cb.setVisibleRowCount(5);
        cb.setLayoutX(layoutX);
        cb.setLayoutY(layoutY);
        return cb;
    }
    public final ToggleButton generateToggleButton(double prefWidth, double layoutX, double layoutY) {
        ToggleButton togglebutton = new ToggleButton();
        togglebutton.setPrefWidth(prefWidth);
        togglebutton.setLayoutX(layoutX);
        togglebutton.setLayoutX(layoutY);
        return togglebutton;
    }
}
