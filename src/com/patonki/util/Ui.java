package com.patonki.util;

import javafx.application.Application;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.ArrayList;

/**
 * Ei vielä mitään hyödyllistä...
 */
public class Ui extends Application {
    public static ArrayList<String> printable = new ArrayList<>();
    private static int index = 0;
    public static boolean ready = false;
    public static Stage stage;

    public static void startTheApplication() {
        launch(Ui.class);
    }
    public static String selectedPath;
    public static void selectBeloScriptFile() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("BeloScript files", "*.bel","*.belo")
        );
        File f = fileChooser.showOpenDialog(Ui.stage);
        if (f == null) selectedPath = "";
        else selectedPath = f.getPath();
    }

    @Override
    public void start(Stage stage) throws Exception {
        Ui.stage = stage;
        VBox root = new VBox(); // Elementti, johon kaikki muut laitetaan
        ready = true;
    }
}
