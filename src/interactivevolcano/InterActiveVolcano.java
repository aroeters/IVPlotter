/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package interactivevolcano;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;


/**
 *
 * @author Arne
 */
public class InterActiveVolcano extends Application {

    /**
     * starts the program by creating the root with the FXML file and adding it
     * to the scene. Then the scene gets added to the stage and is shown.
     *
     * @param stage a top lvl javafx container created by the platform.
     * @throws Exception an exception.
     */
    @Override
    public final void start(final Stage stage) throws Exception {
        Parent root = FXMLLoader.load(getClass()
                .getResource("volcano.fxml"));
        Scene scene = new Scene(root);
        stage.setScene(scene);
        stage.setTitle("IVPlotter");
        stage.show();
        stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });
    }

    /**
     * @param args the command line arguments
     */
    public static void main(final String[] args) {
        launch(args);
    }
}