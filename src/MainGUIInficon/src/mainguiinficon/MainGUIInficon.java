/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
//package mainguiinficon;
//
//import javafx.application.Application;
//import javafx.geometry.Insets;
//import javafx.geometry.Pos;
//import javafx.scene.Scene;
//import javafx.scene.control.*;
//import javafx.scene.layout.*;
//import javafx.scene.paint.Color;
//import javafx.scene.text.Font;
//import javafx.scene.text.FontWeight;
//import javafx.stage.Stage;
//
//public class MainGUIInficon extends Application {
//
//    @Override
//    public void start(Stage primaryStage) {
//        primaryStage.setTitle("INFICON Data Manager");
//
//        BorderPane root = new BorderPane();
//        root.setStyle("-fx-background-color: #0f1117;");
//
//        root.setTop(buildHeader());
//        root.setLeft(buildLeftPanel());
//        root.setCenter(buildCenterPanel());
//        root.setBottom(buildStatusBar());
//
//        Scene scene = new Scene(root);
//        primaryStage.setScene(scene);
//        primaryStage.setMaximized(true);
//        primaryStage.show();
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    //  HEADER
//    // ─────────────────────────────────────────────────────────────
//    private HBox buildHeader() {
//        Label appTitle = new Label("INFICON  DATA  MANAGER");
//        appTitle.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
//        appTitle.setTextFill(Color.web("#00e5ff"));
//
//        Label dateLabel = new Label("2026-04-10   12:00:00");
//        dateLabel.setFont(Font.font("Courier New", 12));
//        dateLabel.setTextFill(Color.web("#888888"));
//
//        Region spacer = new Region();
//        HBox.setHgrow(spacer, Priority.ALWAYS);
//
//        HBox header = new HBox(appTitle, spacer, dateLabel);
//        header.setAlignment(Pos.CENTER_LEFT);
//        header.setPadding(new Insets(14, 24, 14, 24));
//        header.setStyle(
//            "-fx-background-color: #090b10;" +
//            "-fx-border-color: #00e5ff;" +
//            "-fx-border-width: 0 0 2 0;"
//        );
//        return header;
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    //  LEFT PANEL  — Import + Output Container
//    // ─────────────────────────────────────────────────────────────
//    private VBox buildLeftPanel() {
//        VBox panel = new VBox(14);
//        panel.setPadding(new Insets(20));
//        panel.setPrefWidth(270);
//        panel.setStyle(
//            "-fx-background-color: #090b10;" +
//            "-fx-border-color: #1e2130;" +
//            "-fx-border-width: 0 1 0 0;"
//        );
//
//        // ── Import section ────────────────────────────────────────
//        Label importHeading = sectionLabel("▶  IMPORT");
//
//        Label inputFilename = new Label("No file selected");
//        inputFilename.setTextFill(Color.web("#555555"));
//        inputFilename.setFont(Font.font("Courier New", 11));
//
//        Button chooseFileBtn = makeButton("  Choose Input File", "#1e2130", "#00e5ff");
//        // TODO: hook up FileChooser in your backend class
//
//        Label recordsInfo = new Label("Records loaded:  —");
//        recordsInfo.setTextFill(Color.web("#555555"));
//        recordsInfo.setFont(Font.font("Courier New", 11));
//
//        Button importBtn = makeButton("⬇   Import  (up to 10 000)", "#002a33", "#00e5ff");
//        // TODO: call your import logic here
//
//        Separator sep1 = styledSeparator();
//
//        // ── Output Container section ──────────────────────────────
//        Label outHeading = sectionLabel("▶  OUTPUT CONTAINER");
//
//        ListView<String> fileList = new ListView<>();
//        fileList.setPrefHeight(170);
//        fileList.setStyle(
//            "-fx-background-color: #0f1117;" +
//            "-fx-border-color: #1e2130;" +
//            "-fx-font-family: 'Courier New';" +
//            "-fx-font-size: 12px;"
//        );
//        fileList.getItems().add("(no files added yet)");
//
//        Button openContainerBtn = makeButton("⊞  Open Container",   "#1e2130", "#00e5ff");
//        Button openPDFBtn       = makeButton("⊟  Open PDF / File",  "#1e2130", "#4fc3f7");
//        Button removeFileBtn    = makeButton("✕  Remove File",       "#1e2130", "#ef5350");
//        // TODO: wire up file operations in your backend class
//
//        panel.getChildren().addAll(
//            importHeading,
//            chooseFileBtn,
//            inputFilename,
//            importBtn,
//            recordsInfo,
//            sep1,
//            outHeading,
//            fileList,
//            openContainerBtn,
//            openPDFBtn,
//            removeFileBtn
//        );
//
//        return panel;
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    //  CENTER PANEL  — Search + Table
//    // ─────────────────────────────────────────────────────────────
//    private VBox buildCenterPanel() {
//        VBox panel = new VBox(12);
//        panel.setPadding(new Insets(20));
//        panel.setStyle("-fx-background-color: #0f1117;");
//
//        // ── Search row ────────────────────────────────────────────
//        Label searchHeading = sectionLabel("▶  KEYWORDS  /  SPECIFIC SEARCH");
//
//        TextField searchField = new TextField();
//        searchField.setPromptText("Search by keyword, value, date…");
//        searchField.setStyle(
//            "-fx-background-color: #090b10;" +
//            "-fx-text-fill: #e0e0e0;" +
//            "-fx-prompt-text-fill: #444444;" +
//            "-fx-border-color: #1e2130;" +
//            "-fx-border-radius: 4;" +
//            "-fx-background-radius: 4;" +
//            "-fx-font-family: 'Courier New';" +
//            "-fx-font-size: 13px;" +
//            "-fx-padding: 8;"
//        );
//        // TODO: add listener that calls your filter/search logic
//
//        Button searchBtn = makeButton("Search", "#002a33", "#00e5ff");
//        Button clearBtn  = makeButton("Clear",  "#1e2130", "#888888");
//
//        HBox searchRow = new HBox(10, searchField, searchBtn, clearBtn);
//        HBox.setHgrow(searchField, Priority.ALWAYS);
//        searchRow.setAlignment(Pos.CENTER_LEFT);
//
//        Separator sep2 = styledSeparator();
//
//        // ── Data table ────────────────────────────────────────────
//        Label tableHeading = sectionLabel("▶  DATA TABLE  —  LOOK-UP");
//
//        TableView<String> table = new TableView<>();
//        table.setStyle(
//            "-fx-background-color: #090b10;" +
//            "-fx-border-color: #1e2130;" +
//            "-fx-table-cell-border-color: #1e2130;"
//        );
//        table.setPlaceholder(buildTablePlaceholder());
//        VBox.setVgrow(table, Priority.ALWAYS);
//
//        TableColumn<String, String> colID   = styledColumn("ID",      80);
//        TableColumn<String, String> colKw   = styledColumn("Keyword", 160);
//        TableColumn<String, String> colVal  = styledColumn("Value",   260);
//        TableColumn<String, String> colDate = styledColumn("Date",    140);
//        TableColumn<String, String> colSrc  = styledColumn("Source",  140);
//
//        //noinspection unchecked
//        table.getColumns().addAll(colID, colKw, colVal, colDate, colSrc);
//        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
//        // TODO: bind table.setItems(...) to your ObservableList in the backend
//
//        panel.getChildren().addAll(
//            searchHeading,
//            searchRow,
//            sep2,
//            tableHeading,
//            table
//        );
//
//        return panel;
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    //  STATUS BAR
//    // ─────────────────────────────────────────────────────────────
//    private HBox buildStatusBar() {
//        Label status = new Label("Ready  —  select a file and press Import to begin.");
//        status.setTextFill(Color.web("#555555"));
//        status.setFont(Font.font("Courier New", 11));
//
//        HBox bar = new HBox(status);
//        bar.setPadding(new Insets(6, 18, 6, 18));
//        bar.setStyle(
//            "-fx-background-color: #090b10;" +
//            "-fx-border-color: #1e2130;" +
//            "-fx-border-width: 1 0 0 0;"
//        );
//        return bar;
//    }
//
//    // ─────────────────────────────────────────────────────────────
//    //  HELPERS
//    // ─────────────────────────────────────────────────────────────
//
//    private Label sectionLabel(String text) {
//        Label l = new Label(text);
//        l.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
//        l.setTextFill(Color.web("#00e5ff"));
//        return l;
//    }
//
//    private Button makeButton(String text, String bg, String fg) {
//        Button b = new Button(text);
//        b.setMaxWidth(Double.MAX_VALUE);
//        b.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
//        String normal = String.format(
//            "-fx-background-color:%s;-fx-text-fill:%s;" +
//            "-fx-border-color:%s;-fx-border-radius:4;" +
//            "-fx-background-radius:4;-fx-padding:7 12;", bg, fg, fg);
//        String hover = String.format(
//            "-fx-background-color:%s;-fx-text-fill:#ffffff;" +
//            "-fx-border-color:%s;-fx-border-radius:4;" +
//            "-fx-background-radius:4;-fx-padding:7 12;", fg, fg);
//        b.setStyle(normal);
//        b.setOnMouseEntered(e -> b.setStyle(hover));
//        b.setOnMouseExited(e  -> b.setStyle(normal));
//        return b;
//    }
//
//    private TableColumn<String, String> styledColumn(String title, double minWidth) {
//        TableColumn<String, String> col = new TableColumn<>(title);
//        col.setMinWidth(minWidth);
//        col.setStyle("-fx-alignment: CENTER-LEFT; -fx-font-family: 'Courier New';");
//        return col;
//    }
//
//    private Separator styledSeparator() {
//        Separator s = new Separator();
//        s.setStyle("-fx-background-color: #1e2130;");
//        return s;
//    }
//
//    private Label buildTablePlaceholder() {
//        Label l = new Label("No data loaded — import a file to populate the table.");
//        l.setFont(Font.font("Courier New", 13));
//        l.setTextFill(Color.web("#333333"));
//        return l;
//    }
//
//    public static void main(String[] args) {
//        launch(args);
//    }
//}