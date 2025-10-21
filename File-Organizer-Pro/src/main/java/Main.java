import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.stage.Modality;
import javafx.stage.StageStyle;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class Main extends Application {

    private Stage primaryStage;
    private VBox mainContainer;
    private String selectedPath = "";
    private Stage dragDropStage;

    private final String PRIMARY_COLOR = "#BA7878"; // Rose gold for buttons
    private final String BACKGROUND_COLOR = "#C6AA96"; // Warm beige background
    private final String CARD_COLOR = "#F5F1EB"; // Cream white for cards
    private final String TEXT_COLOR = "#5D4E37"; // Dark brown text
    private final String MUTED_COLOR = "#8B7355"; // Muted brown
    private final String HOVER_COLOR = "#A66B6B"; // Darker rose for hover
    private final String ACCENT_COLOR = "#D4AF37"; // Gold accent
    private final String DRAG_AREA_COLOR = "#F0E6D6"; // Light cream for drag areas
    private final String DRAG_HOVER_COLOR = "#E8D5C4"; // Slightly darker cream for drag hover

    @Override
    public void start(Stage stage) {
        this.primaryStage = stage;

        // Main container
        mainContainer = new VBox(20);
        mainContainer.setPadding(new Insets(40));
        mainContainer.setAlignment(Pos.CENTER);
        mainContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        showInitialScreen();

        Scene scene = new Scene(mainContainer, 900, 700);
        stage.setTitle("File Organizer Pro");
        stage.setScene(scene);
        stage.show();
    }

    private void showInitialScreen() {
        mainContainer.getChildren().clear();

        // Header
        Label title = new Label("File Organizer Pro");
        title.setFont(Font.font("System", FontWeight.BOLD, 36));
        title.setTextFill(Color.web(TEXT_COLOR));

        Label subtitle = new Label("Organize your files with elegant efficiency");
        subtitle.setFont(Font.font("System", FontWeight.LIGHT, 18));
        subtitle.setTextFill(Color.web(MUTED_COLOR));

        // Card container
        VBox card = createCard();

        // Main buttons
        Button organizeBtn = createPrimaryButton("Organize Files", 220, 55);
        organizeBtn.setOnAction(e -> showFilePathInput());

        Button undoBtn = createSecondaryButton("Undo Last Organize", 220, 55);
        undoBtn.setOnAction(e -> handleUndo());

        VBox buttonContainer = new VBox(20);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.getChildren().addAll(organizeBtn, undoBtn);

        card.getChildren().addAll(
                title,
                subtitle,
                new Region() {{ setPrefHeight(30); }}, // Spacer
                buttonContainer
        );

        mainContainer.getChildren().add(card);
    }

    private void showFilePathInput() {
        mainContainer.getChildren().clear();

        VBox card = createCard();

        // Header
        Label title = new Label("Select Directory");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(TEXT_COLOR));

        Label subtitle = new Label("Choose the folder you want to organize");
        subtitle.setFont(Font.font("System", FontWeight.LIGHT, 16));
        subtitle.setTextFill(Color.web(MUTED_COLOR));

        // Drag and drop area
        VBox dragDropArea = createDragDropArea();

        // Path input section
        HBox pathContainer = new HBox(15);
        pathContainer.setAlignment(Pos.CENTER);

        TextField pathField = new TextField();
        pathField.setPromptText("Enter folder path or browse...");

        pathField.setPrefWidth(450);

        pathField.setPrefHeight(45);

        pathField.setStyle(
                "-fx-font-size: 14px; " +
                        "-fx-padding: 12px; " +
                        "-fx-background-color: " + CARD_COLOR + "; " +
                        "-fx-border-color: " + MUTED_COLOR + "; " +
                        "-fx-border-radius: 8; " +
                        "-fx-background-radius: 8;"
        );

        Button browseBtn = createSecondaryButton("Browse", 110, 45);
        browseBtn.setOnAction(e -> {
            DirectoryChooser directoryChooser = new DirectoryChooser();
            directoryChooser.setTitle("Select Directory to Organize");
            File selectedDirectory = directoryChooser.showDialog(primaryStage);
            if (selectedDirectory != null) {
                pathField.setText(selectedDirectory.getAbsolutePath());
            }
        });

        pathContainer.getChildren().addAll(pathField, browseBtn);

        // Drag and drop button
        Button dragDropBtn = createPrimaryButton("Open Drag & Drop Window", 250, 50);
        dragDropBtn.setOnAction(e -> showDragDropDialog(pathField));

        // Action buttons
        HBox buttonContainer = new HBox(20);
        buttonContainer.setAlignment(Pos.CENTER);

        Button continueBtn = createPrimaryButton("Continue", 130, 45);
        continueBtn.setOnAction(e -> {
            String path = pathField.getText().trim();
            if (validatePath(path)) {
                selectedPath = path;
                showSortOptions();
            } else {
                showAlert("Invalid Path", "Please enter a valid directory path.");
            }
        });

        Button backBtn = createSecondaryButton("Back", 130, 45);
        backBtn.setOnAction(e -> showInitialScreen());

        buttonContainer.getChildren().addAll(backBtn, continueBtn);

        card.getChildren().addAll(
                title,
                subtitle,
                new Region() {{ setPrefHeight(25); }},
                dragDropArea,
                new Region() {{ setPrefHeight(20); }},
                dragDropBtn,
                new Region() {{ setPrefHeight(20); }},
                new Label("OR") {{
                    setFont(Font.font("System", FontWeight.BOLD, 14));
                    setTextFill(Color.web(MUTED_COLOR));
                }},
                new Region() {{ setPrefHeight(20); }},
                pathContainer,
                new Region() {{ setPrefHeight(25); }},
                buttonContainer
        );

        mainContainer.getChildren().add(card);
    }

    private VBox createDragDropArea() {
        VBox dragArea = new VBox(12);
        dragArea.setAlignment(Pos.CENTER);
        dragArea.setPrefHeight(140);
        dragArea.setPrefWidth(550);

        // Store original style for proper reset
        String originalStyle =
                "-fx-background-color: " + DRAG_AREA_COLOR + ";" +
                        "-fx-border-color: " + PRIMARY_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-border-style: dashed;" +
                        "-fx-border-radius: 15;" +
                        "-fx-background-radius: 15;";


        dragArea.setOnDragExited(event -> {
            // Always reset to original style when drag exits
            dragArea.setStyle(originalStyle);
            event.consume();
        });

        dragArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            // Immediately reset visual state
            dragArea.setStyle(originalStyle);

            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                if (!files.isEmpty()) {
                    File droppedFile = files.get(0);
                    if (droppedFile.isDirectory()) {
                        selectedPath = droppedFile.getAbsolutePath();
                        // Simple success feedback without persistent dialog
                        success = true;
                    } else {
                        showAlert("Invalid Selection", "Please drop a folder, not a file.");
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        return dragArea;
    }

    private void showDragDropDialog(TextField pathField) {
        if (dragDropStage != null) {
            dragDropStage.close();
        }

        dragDropStage = new Stage();
        dragDropStage.initModality(Modality.APPLICATION_MODAL);
        dragDropStage.initOwner(primaryStage);
        dragDropStage.initStyle(StageStyle.DECORATED);
        dragDropStage.setTitle("Drag & Drop Folder");
        dragDropStage.setAlwaysOnTop(true);
        dragDropStage.setResizable(false);

        VBox dialogContainer = new VBox(25);
        dialogContainer.setPadding(new Insets(35));
        dialogContainer.setAlignment(Pos.CENTER);
        dialogContainer.setStyle("-fx-background-color: " + BACKGROUND_COLOR + ";");

        // Header
        Label title = new Label("Drag & Drop Your Folder");
        title.setFont(Font.font("System", FontWeight.BOLD, 22));
        title.setTextFill(Color.web(TEXT_COLOR));

        // Large drag and drop area
        VBox largeDragArea = new VBox(18);
        largeDragArea.setAlignment(Pos.CENTER);
        largeDragArea.setPrefHeight(220);
        largeDragArea.setPrefWidth(450);

        String originalLargeStyle =
                "-fx-background-color: " + DRAG_AREA_COLOR + ";" +
                        "-fx-border-color: " + PRIMARY_COLOR + ";" +
                        "-fx-border-width: 3;" +
                        "-fx-border-style: dashed;" +
                        "-fx-border-radius: 18;" +
                        "-fx-background-radius: 18;";

        largeDragArea.setStyle(originalLargeStyle);

        Label dragIcon = new Label("📁");
        dragIcon.setFont(Font.font("System", 52));

        Label dragLabel = new Label("Drop Your Folder Here");
        dragLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
        dragLabel.setTextFill(Color.web(PRIMARY_COLOR));

        Label dragSubLabel = new Label("This window stays on top for easy access");
        dragSubLabel.setFont(Font.font("System", FontWeight.LIGHT, 13));
        dragSubLabel.setTextFill(Color.web(MUTED_COLOR));

        largeDragArea.getChildren().addAll(dragIcon, dragLabel, dragSubLabel);

        // Add drag and drop handlers with proper cleanup and no persistent dialogs
        largeDragArea.setOnDragOver(event -> {
            if (event.getGestureSource() != largeDragArea && event.getDragboard().hasFiles()) {
                event.acceptTransferModes(TransferMode.COPY_OR_MOVE);
            }
            event.consume();
        });

        largeDragArea.setOnDragEntered(event -> {
            if (event.getGestureSource() != largeDragArea && event.getDragboard().hasFiles()) {
                largeDragArea.setStyle(
                        "-fx-background-color: " + DRAG_HOVER_COLOR + ";" +
                                "-fx-border-color: " + ACCENT_COLOR + ";" +
                                "-fx-border-width: 4;" +
                                "-fx-border-style: solid;" +
                                "-fx-border-radius: 18;" +
                                "-fx-background-radius: 18;"
                );
            }
            event.consume();
        });

        largeDragArea.setOnDragExited(event -> {
            // Always reset to original style when drag exits
            largeDragArea.setStyle(originalLargeStyle);
            event.consume();
        });

        largeDragArea.setOnDragDropped(event -> {
            Dragboard db = event.getDragboard();
            boolean success = false;

            // Immediately reset visual state
            largeDragArea.setStyle(originalLargeStyle);

            if (db.hasFiles()) {
                List<File> files = db.getFiles();
                if (!files.isEmpty()) {
                    File droppedFile = files.get(0);
                    if (droppedFile.isDirectory()) {
                        selectedPath = droppedFile.getAbsolutePath();
                        pathField.setText(selectedPath);

                        // Show success feedback in the drag area itself
                        dragIcon.setText("✅");
                        dragLabel.setText("Folder Selected!");
                        dragLabel.setTextFill(Color.web(ACCENT_COLOR));
                        dragSubLabel.setText("Closing in 2 seconds...");

                        // Auto-close after 2 seconds
                        Timeline timeline = new Timeline(
                                new javafx.animation.KeyFrame(Duration.seconds(2), e -> {
                                    dragDropStage.close();
                                })
                        );
                        timeline.play();

                        success = true;
                    } else {
                        // Show error feedback in the drag area
                        dragIcon.setText("❌");
                        dragLabel.setText("Invalid Selection");
                        dragLabel.setTextFill(Color.web("#D32F2F"));
                        dragSubLabel.setText("Please drop a folder, not a file");

                        // Reset after 3 seconds
                        Timeline timeline = new Timeline(
                                new javafx.animation.KeyFrame(Duration.seconds(3), e -> {
                                    dragIcon.setText("📁");
                                    dragLabel.setText("Drop Your Folder Here");
                                    dragLabel.setTextFill(Color.web(PRIMARY_COLOR));
                                    dragSubLabel.setText("This window stays on top for easy access");
                                })
                        );
                        timeline.play();
                    }
                }
            }

            event.setDropCompleted(success);
            event.consume();
        });

        Button closeBtn = createSecondaryButton("Close", 110, 40);
        closeBtn.setOnAction(e -> dragDropStage.close());

        dialogContainer.getChildren().addAll(title, largeDragArea, closeBtn);

        Scene dialogScene = new Scene(dialogContainer, 550, 400);
        dragDropStage.setScene(dialogScene);
        dragDropStage.show();
    }

    private void showSortOptions() {
        mainContainer.getChildren().clear();

        VBox card = createCard();

        // Header
        Label title = new Label("Choose Sorting Method");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(TEXT_COLOR));

        Label subtitle = new Label("Selected folder: " + selectedPath);
        subtitle.setFont(Font.font("System", FontWeight.LIGHT, 14));
        subtitle.setTextFill(Color.web(MUTED_COLOR));

        Label filesLabel = new Label("Files in this folder:");
        filesLabel.setFont(Font.font("System", FontWeight.BOLD, 16));
        filesLabel.setTextFill(Color.web(TEXT_COLOR));

        ScrollPane fileListPane = createFileListPane();

        // Sort option buttons
        VBox optionContainer = new VBox(18);
        optionContainer.setAlignment(Pos.CENTER);

        Button extensionBtn = createPrimaryButton("Sort by Extension", 280, 55);
        extensionBtn.setOnAction(e -> handleSortByExtension());

        Button dateBtn = createPrimaryButton("Sort by Date", 280, 55);
        dateBtn.setOnAction(e -> showDateOptions());

        Button backBtn = createSecondaryButton("Back", 130, 45);
        backBtn.setOnAction(e -> showFilePathInput());

        optionContainer.getChildren().addAll(extensionBtn, dateBtn);

        card.getChildren().addAll(
                title,
                subtitle,
                new Region() {{ setPrefHeight(20); }},
                filesLabel,
                fileListPane,
                new Region() {{ setPrefHeight(25); }},
                optionContainer,
                new Region() {{ setPrefHeight(25); }},
                backBtn
        );

        mainContainer.getChildren().add(card);
    }

    private ScrollPane createFileListPane() {
        VBox fileList = new VBox(8);
        fileList.setPadding(new Insets(15));
        fileList.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 10;"
        );

        try {
            File folder = new File(selectedPath);
            File[] files = folder.listFiles();

            if (files != null && files.length > 0) {
                int fileCount = 0;
                int folderCount = 0;

                for (File file : files) {
                    if (fileCount + folderCount >= 10) { // Limit display to first 10 items
                        Label moreLabel = new Label("... and " + (files.length - 10) + " more items");
                        moreLabel.setFont(Font.font("System", FontWeight.LIGHT, 12));
                        moreLabel.setTextFill(Color.web(MUTED_COLOR));
                        fileList.getChildren().add(moreLabel);
                        break;
                    }

                    HBox fileItem = new HBox(10);
                    fileItem.setAlignment(Pos.CENTER_LEFT);

                    String icon = file.isDirectory() ? "📁" : "📄";
                    Label iconLabel = new Label(icon);
                    iconLabel.setFont(Font.font("System", 14));

                    Label nameLabel = new Label(file.getName());
                    nameLabel.setFont(Font.font("System", 13));
                    nameLabel.setTextFill(Color.web(TEXT_COLOR));

                    fileItem.getChildren().addAll(iconLabel, nameLabel);
                    fileList.getChildren().add(fileItem);

                    if (file.isDirectory()) folderCount++;
                    else fileCount++;
                }

                // Add summary
                Label summaryLabel = new Label("Total: " + fileCount + " files, " + folderCount + " folders");
                summaryLabel.setFont(Font.font("System", FontWeight.BOLD, 12));
                summaryLabel.setTextFill(Color.web(ACCENT_COLOR));
                fileList.getChildren().add(new Region() {{ setPrefHeight(5); }});
                fileList.getChildren().add(summaryLabel);

            } else {
                Label emptyLabel = new Label("No files found in this folder");
                emptyLabel.setFont(Font.font("System", FontWeight.LIGHT, 14));
                emptyLabel.setTextFill(Color.web(MUTED_COLOR));
                fileList.getChildren().add(emptyLabel);
            }

        } catch (Exception e) {
            Label errorLabel = new Label("Error reading folder contents");
            errorLabel.setFont(Font.font("System", FontWeight.LIGHT, 14));
            errorLabel.setTextFill(Color.web("#D32F2F"));
            fileList.getChildren().add(errorLabel);
        }

        ScrollPane scrollPane = new ScrollPane(fileList);
        scrollPane.setPrefHeight(150);
        scrollPane.setPrefWidth(500);
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle(
                "-fx-background: " + DRAG_AREA_COLOR + ";" +
                        "-fx-background-color: " + DRAG_AREA_COLOR + ";" +
                        "-fx-border-color: " + MUTED_COLOR + ";" +
                        "-fx-border-radius: 10;" +
                        "-fx-background-radius: 10;"
        );

        return scrollPane;
    }

    private void showDateOptions() {
        mainContainer.getChildren().clear();

        VBox card = createCard();

        // Header
        Label title = new Label("Date Sorting Options");
        title.setFont(Font.font("System", FontWeight.BOLD, 28));
        title.setTextFill(Color.web(TEXT_COLOR));

        Label subtitle = new Label("Choose how to organize by date");
        subtitle.setFont(Font.font("System", FontWeight.LIGHT, 16));
        subtitle.setTextFill(Color.web(MUTED_COLOR));

        // Date option buttons
        VBox optionContainer = new VBox(18);
        optionContainer.setAlignment(Pos.CENTER);

        Button byDateBtn = createPrimaryButton("By Date", 220, 50);
        byDateBtn.setOnAction(e -> handleSortByDate("date"));

        Button byMonthBtn = createPrimaryButton("By Month", 220, 50);
        byMonthBtn.setOnAction(e -> handleSortByDate("month"));

        Button byYearBtn = createPrimaryButton("By Year", 220, 50);
        byYearBtn.setOnAction(e -> handleSortByDate("year"));

        Button backBtn = createSecondaryButton("Back", 130, 45);
        backBtn.setOnAction(e -> showSortOptions());

        optionContainer.getChildren().addAll(byDateBtn, byMonthBtn, byYearBtn);

        card.getChildren().addAll(
                title,
                subtitle,
                new Region() {{ setPrefHeight(25); }},
                optionContainer,
                new Region() {{ setPrefHeight(25); }},
                backBtn
        );

        mainContainer.getChildren().add(card);
    }

    private VBox createCard() {
        VBox card = new VBox(25);
        card.setAlignment(Pos.CENTER);
        card.setPadding(new Insets(45));
        card.setMaxWidth(700);
        card.setStyle(
                "-fx-background-color: " + CARD_COLOR + ";" +
                        "-fx-background-radius: 15;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.15), 12, 0, 0, 3);"
        );
        return card;
    }

    private Button createPrimaryButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setPrefSize(width, height);
        button.setFont(Font.font("System", FontWeight.BOLD, 15));
        button.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + HOVER_COLOR + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 3);"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: " + PRIMARY_COLOR + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-background-radius: 10;" +
                        "-fx-cursor: hand;" +
                        "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 6, 0, 0, 2);"
        ));

        return button;
    }

    private Button createSecondaryButton(String text, double width, double height) {
        Button button = new Button(text);
        button.setPrefSize(width, height);
        button.setFont(Font.font("System", FontWeight.NORMAL, 15));
        button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + MUTED_COLOR + ";" +
                        "-fx-border-color: " + MUTED_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        );

        button.setOnMouseEntered(e -> button.setStyle(
                "-fx-background-color: " + MUTED_COLOR + ";" +
                        "-fx-text-fill: white;" +
                        "-fx-border-color: " + MUTED_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        button.setOnMouseExited(e -> button.setStyle(
                "-fx-background-color: transparent;" +
                        "-fx-text-fill: " + MUTED_COLOR + ";" +
                        "-fx-border-color: " + MUTED_COLOR + ";" +
                        "-fx-border-width: 2;" +
                        "-fx-background-radius: 10;" +
                        "-fx-border-radius: 10;" +
                        "-fx-cursor: hand;"
        ));

        return button;
    }

    private boolean validatePath(String path) {
        return path != null && !path.trim().isEmpty() && Files.exists(Paths.get(path)) && Files.isDirectory(Paths.get(path));
    }

    private void handleSortByExtension() {
        try {
            // This is where you would call your sortext.java class
            System.out.println("Calling sortext.java with path: " + selectedPath);

            sortbyext sbe = new sortbyext(selectedPath);

            // Example of how you might call your Java class:
            // ProcessBuilder pb = new ProcessBuilder("java", "sortext", selectedPath);
            // Process process = pb.start();
            // process.waitFor();

            showAlert("Success", "Files organized by extension successfully!\nPath: " + selectedPath);
            showInitialScreen();
        } catch (Exception e) {
            showAlert("Error", "Failed to organize files: " + e.getMessage());
        }
    }

    private void handleSortByDate(String dateType) {
        try {
            System.out.println("Sorting by " + dateType + " with path: " + selectedPath);

            // Here you would call your Java backend with the specific date sorting type
            // You might have different classes or pass parameters to handle date, month, year sorting

            sortbydate sbd = new sortbydate(selectedPath, dateType);

            showAlert("Success", "Files organized by " + dateType + " successfully!\nPath: " + selectedPath);
            showInitialScreen();
        } catch (Exception e) {
            showAlert("Error", "Failed to organize files: " + e.getMessage());
        }
    }

    private void handleUndo() {
        try {
            System.out.println("Undoing last organize operation");

            Undo undo = new Undo();
            String[] result = undo.undoLastSort();
            String message = result[0];
            String mainFolder = result[1];

            String alertTitle;
            String alertMessage;

            if (message.startsWith("No operation")) {
                alertTitle = "Nothing to Undo";
                alertMessage = "There is no organize operation available to undo.";
            } else if (message.startsWith("Undo completed!")) {
                alertTitle = "Undo Successful";
                alertMessage = message + (mainFolder.isEmpty() ? "" : "\nOrganized folder: " + mainFolder);
            } else if (message.startsWith("Undo finished with some errors")) {
                alertTitle = "Undo Partially Successful";
                alertMessage = message + (mainFolder.isEmpty() ? "" : "\nOrganized folder: " + mainFolder);
            } else {
                alertTitle = "Undo Status";
                alertMessage = message + (mainFolder.isEmpty() ? "" : "\nOrganized folder: " + mainFolder);
            }

            showAlert(alertTitle, alertMessage);

            showInitialScreen();
        } catch (Exception e) {
            showAlert("Error", "Failed to undo operation: " + e.getMessage());
        }
    }




    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}