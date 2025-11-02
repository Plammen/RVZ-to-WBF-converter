package com.example.rvzconverter;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;


import java.io.*;

public class RVZConverterFX extends Application {

    private TextArea outputArea = new TextArea();
    private Button convertButton = new Button("Convert RVZ → WBFS");
    private File selectedFile;
    private ProgressBar progressBar = new ProgressBar(0);


    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage stage) {
        stage.setTitle("RVZ → WBFS Converter");

        // Button to select RVZ file
        Button selectButton = new Button("Select RVZ File");
        selectButton.setOnAction(e -> chooseFile(stage));

        // Convert button disabled until a file is selected
        convertButton.setDisable(true);
        convertButton.setOnAction(e -> new Thread(this::convertFile).start());

        outputArea.setEditable(false);
        outputArea.setPrefHeight(300);

        progressBar.setPrefWidth(500);
        progressBar.setProgress(0);
        progressBar.setMinHeight(20);


        VBox root = new VBox(10, selectButton, convertButton, progressBar, new Label("Output:"), outputArea);
        root.setStyle("-fx-padding: 20;");
        stage.setScene(new Scene(root, 600, 400));
        stage.show();
    }

    private void chooseFile(Stage stage) {
        FileChooser chooser = new FileChooser();
        chooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("RVZ files", "*.rvz"));
        selectedFile = chooser.showOpenDialog(stage);

        if (selectedFile != null) {
            appendOutput("Selected: " + selectedFile.getAbsolutePath() + "\n");
            convertButton.setDisable(false);
        }
    }

    private void convertFile() {
        if (selectedFile == null) return;

        Platform.runLater(() -> {
            progressBar.setProgress(-1); // indeterminate
            convertButton.setDisable(true);
        });

        try {
            String rvzPath = selectedFile.getAbsolutePath();
            String isoPath = rvzPath.replace(".rvz", ".iso");
            String wbfsPath = rvzPath.replace(".rvz", ".wbfs");

            appendOutput("Step 1: Converting RVZ → ISO\n");
            runCommand(new String[]{"DolphinTool.exe", "convert", "--input", rvzPath, "--output", isoPath, "--format", "iso"});

            appendOutput("Step 2: Converting ISO → WBFS\n");
            runCommand(new String[]{"wit.exe", "copy", isoPath, wbfsPath});

            // Delete temporary ISO
            new File(isoPath).delete();

            appendOutput(" Conversion complete: " + wbfsPath + "\n");
        } catch (Exception e) {
            appendOutput("Error: " + e.getMessage() + "\n");
            e.printStackTrace();
        } finally {
            Platform.runLater(() -> {
                progressBar.setProgress(0); // reset
                convertButton.setDisable(false);
            });
            Platform.runLater(() -> progressBar.setProgress(0));
        }
    }



    private void runCommand(String[] command) throws IOException, InterruptedException {
        ProcessBuilder pb = new ProcessBuilder(command);
        pb.redirectErrorStream(true);
        Process process = pb.start();

        try (BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()))) {
            String line;
            while ((line = reader.readLine()) != null) {
                appendOutput(line + "\n");
            }
        }

        int exitCode = process.waitFor();
        if (exitCode != 0) {
            throw new IOException("Command failed: " + String.join(" ", command));
        }
    }

    private void appendOutput(String text) {
        Platform.runLater(() -> outputArea.appendText(text));
    }
}
