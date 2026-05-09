package mainguiinficon;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.Desktop;
import java.io.File;
import java.util.List;

public class MainGUIInficon extends Application {

    // ── Table row model ───────────────────────────────────────────
    public static class ResultRow {
        private final SimpleStringProperty questionFile;
        private final SimpleStringProperty question;
        private final SimpleStringProperty detectedSection;
        private final SimpleStringProperty bestAnswer;

        public ResultRow(String questionFile, String question,
                         String detectedSection, String bestAnswer) {
            this.questionFile    = new SimpleStringProperty(questionFile);
            this.question        = new SimpleStringProperty(question);
            this.detectedSection = new SimpleStringProperty(detectedSection);
            this.bestAnswer      = new SimpleStringProperty(bestAnswer);
        }

        public String getQuestionFile()    { return questionFile.get(); }
        public String getQuestion()        { return question.get(); }
        public String getDetectedSection() { return detectedSection.get(); }
        public String getBestAnswer()      { return bestAnswer.get(); }
    }

    // ── Instance fields ───────────────────────────────────────────
    private File   selectedFile = null;
    private Stage  primaryStage;
    private Label  inputFilename;
    private Label  recordsInfo;
    private Label  statusLabel;
    private TextField searchField;

    private TableView<ResultRow>      table;
    private ObservableList<ResultRow> masterData   = FXCollections.observableArrayList();
    private FilteredList<ResultRow>   filteredData = new FilteredList<>(masterData, r -> true);

    private ObservableList<File> containerFiles = FXCollections.observableArrayList();
    private ListView<File>       fileListView;

    private final AnswerMatching answerMatching = new AnswerMatching();

    // ── Theme ─────────────────────────────────────────────────────
    private boolean    isNightMode = true;
    private BorderPane root;
    private HBox       headerBox, statusBar;
    private VBox       leftPanel, centerPanel;
    private Button     themeToggleBtn;
    private Label      appTitleLabel;

    private static final String N_ROOT     = "#0f1117";
    private static final String N_PANEL    = "#090b10";
    private static final String N_BORDER   = "#1e2130";
    private static final String N_ACCENT   = "#00e5ff";
    private static final String N_TEXT     = "#e0e0e0";
    private static final String N_SUBTEXT  = "#555555";
    private static final String N_FIELD_BG = "#090b10";
    private static final String N_BTN_BG   = "#1e2130";

    private static final String D_ROOT     = "#e8f4f8";
    private static final String D_PANEL    = "#ffffff";
    private static final String D_BORDER   = "#b0cfe0";
    private static final String D_ACCENT   = "#1a6fa3";
    private static final String D_TEXT     = "#0d2233";
    private static final String D_SUBTEXT  = "#5a7a8a";
    private static final String D_FIELD_BG = "#f0f8ff";
    private static final String D_BTN_BG   = "#d0e8f5";

    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;
        primaryStage.setTitle("INFICON Data Manager");

        root = new BorderPane();

        statusBar   = buildStatusBar();
        headerBox   = buildHeader();
        centerPanel = buildCenterPanel();
        leftPanel   = buildLeftPanel();

        root.setTop(headerBox);
        root.setLeft(leftPanel);
        root.setCenter(centerPanel);
        root.setBottom(statusBar);

        applyTheme();
        loadAllConfluenceEntries();

        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setMaximized(true);
        primaryStage.show();
    }

    private void loadAllConfluenceEntries() {
        masterData.clear();
        List<ConfluencePage.Entry> all = answerMatching.getAllEntries();
        for (ConfluencePage.Entry e : all) {
            masterData.add(new ResultRow("Confluence", e.question, e.section, e.answer));
        }
        recordsInfo.setText("Records loaded:  " + masterData.size());
        statusLabel.setText("✔  " + masterData.size() +
            " entries loaded — import a question file (.txt or .pdf) to match answers.");
    }

    // ─────────────────────────────────────────────────────────────
    //  THEME
    // ─────────────────────────────────────────────────────────────
    private void applyTheme() {
        String rootBg  = isNightMode ? N_ROOT    : D_ROOT;
        String panelBg = isNightMode ? N_PANEL   : D_PANEL;
        String border  = isNightMode ? N_BORDER  : D_BORDER;
        String accent  = isNightMode ? N_ACCENT  : D_ACCENT;
        String subtext = isNightMode ? N_SUBTEXT : D_SUBTEXT;
        String fieldBg = isNightMode ? N_FIELD_BG : D_FIELD_BG;

        root.setStyle("-fx-background-color:" + rootBg + ";");
        headerBox.setStyle("-fx-background-color:" + panelBg + ";-fx-border-color:" + accent + ";-fx-border-width:0 0 2 0;");
        leftPanel.setStyle("-fx-background-color:" + panelBg + ";-fx-border-color:" + border + ";-fx-border-width:0 1 0 0;");
        centerPanel.setStyle("-fx-background-color:" + rootBg + ";");
        statusBar.setStyle("-fx-background-color:" + panelBg + ";-fx-border-color:" + border + ";-fx-border-width:1 0 0 0;");

        if (appTitleLabel != null) appTitleLabel.setTextFill(Color.web(accent));
        if (statusLabel   != null) statusLabel.setTextFill(Color.web(subtext));
        if (table         != null) table.setStyle("-fx-background-color:" + fieldBg + ";-fx-border-color:" + border + ";-fx-table-cell-border-color:" + border + ";");
        if (fileListView  != null) fileListView.setStyle("-fx-background-color:" + fieldBg + ";-fx-border-color:" + border + ";-fx-font-family:'Courier New';-fx-font-size:12px;");
        if (searchField   != null) searchField.setStyle(
            "-fx-background-color:" + fieldBg + ";-fx-text-fill:" + (isNightMode ? N_TEXT : D_TEXT) + ";" +
            "-fx-prompt-text-fill:#888888;-fx-border-color:" + border + ";-fx-border-radius:4;" +
            "-fx-background-radius:4;-fx-font-family:'Courier New';-fx-font-size:13px;-fx-padding:8;");

        if (themeToggleBtn != null) {
            String btnBg = isNightMode ? N_BTN_BG : D_BTN_BG;
            themeToggleBtn.setStyle("-fx-background-color:" + btnBg + ";-fx-text-fill:" + accent +
                ";-fx-border-color:" + accent + ";-fx-border-radius:4;-fx-background-radius:4;" +
                "-fx-font-family:'Courier New';-fx-font-weight:bold;-fx-font-size:12px;-fx-padding:6 14;");
            themeToggleBtn.setText(isNightMode ? "☀  Day Mode" : "☾  Night Mode");
        }
    }

    // ─────────────────────────────────────────────────────────────
    //  HEADER
    // ─────────────────────────────────────────────────────────────
    private HBox buildHeader() {
        ImageView logo = null;
        try {
            Image img = new Image(
                getClass().getResourceAsStream("/mainguiinficon/inficon_logo.png"),
                140, 50, true, true);
            logo = new ImageView(img);
        } catch (Exception ex) { /* text fallback */ }

        appTitleLabel = new Label("DATA  MANAGER");
        appTitleLabel.setFont(Font.font("Courier New", FontWeight.BOLD, 20));
        appTitleLabel.setTextFill(Color.web(N_ACCENT));

        HBox titleBox = new HBox(10);
        titleBox.setAlignment(Pos.CENTER_LEFT);
        if (logo != null) titleBox.getChildren().add(logo);
        titleBox.getChildren().add(appTitleLabel);

        themeToggleBtn = new Button("☀  Day Mode");
        themeToggleBtn.setOnAction(e -> { isNightMode = !isNightMode; applyTheme(); });

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        HBox header = new HBox(titleBox, spacer, themeToggleBtn);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(10, 24, 10, 24));
        return header;
    }

    // ─────────────────────────────────────────────────────────────
    //  LEFT PANEL
    // ─────────────────────────────────────────────────────────────
    private VBox buildLeftPanel() {
        VBox panel = new VBox(14);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(270);

        Label importHeading = sectionLabel("▶  IMPORT QUESTION FILE");

        inputFilename = new Label("No file selected");
        inputFilename.setFont(Font.font("Courier New", 11));
        inputFilename.setTextFill(Color.web(N_SUBTEXT));

        Button chooseFileBtn = makeButton("  Choose Question File", N_BTN_BG, N_ACCENT);
        chooseFileBtn.setOnAction(e -> {
            FileChooser chooser = new FileChooser();
            chooser.setTitle("Select Question File");
            // ── Accept both TXT and PDF ───────────────────────────
            chooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Question Files (*.txt, *.pdf)", "*.txt", "*.pdf"),
                new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                new FileChooser.ExtensionFilter("PDF Files (*.pdf)", "*.pdf")
            );
            File file = chooser.showOpenDialog(primaryStage);
            if (file == null) return;
            selectedFile = file;
            inputFilename.setText(file.getName());
            inputFilename.setTextFill(Color.web(isNightMode ? N_ACCENT : D_ACCENT));
            statusLabel.setText("File selected: " + file.getName() + "  —  press Match Answers.");
        });

        recordsInfo = new Label("Records loaded:  —");
        recordsInfo.setFont(Font.font("Courier New", 11));
        recordsInfo.setTextFill(Color.web(N_SUBTEXT));

        Button importBtn = makeButton("⬇   Match Answers", "#002a33", N_ACCENT);
        importBtn.setOnAction(e -> {
            if (selectedFile == null) {
                statusLabel.setText("⚠  No file selected — choose a question file first.");
                statusLabel.setTextFill(Color.web("#ef5350"));
                return;
            }

            statusLabel.setText("Matching  \"" + selectedFile.getName() + "\"  …");

            // ── Works for both .txt and .pdf automatically ────────
            List<AnswerMatching.MatchResult> results =
                answerMatching.matchFile(selectedFile);

            if (results.isEmpty()) {
                statusLabel.setText("⚠  No questions found in \"" + selectedFile.getName() +
                    "\". Make sure it contains question text.");
                statusLabel.setTextFill(Color.web("#ef5350"));
                return;
            }

            masterData.clear();
            if (searchField != null) searchField.clear();

            for (AnswerMatching.MatchResult r : results) {
                masterData.add(new ResultRow(
                    r.questionFile,
                    r.question,
                    r.detectedSection,
                    r.bestAnswer
                ));
            }

            if (!containerFiles.contains(selectedFile)) {
                containerFiles.add(selectedFile);
            }

            recordsInfo.setText("Records loaded:  " + masterData.size());
            recordsInfo.setTextFill(Color.web(isNightMode ? N_ACCENT : D_ACCENT));
            statusLabel.setText("✔  Matched " + masterData.size() +
                " question(s) from \"" + selectedFile.getName() + "\"");
            statusLabel.setTextFill(Color.web(isNightMode ? N_ACCENT : D_ACCENT));
        });

        Button showAllBtn = makeButton("⊞  Show All Confluence Data", N_BTN_BG, N_ACCENT);
        showAllBtn.setOnAction(e -> {
            loadAllConfluenceEntries();
            if (searchField != null) searchField.clear();
        });

        Separator sep1 = styledSeparator();

        Label outHeading = sectionLabel("▶  OUTPUT CONTAINER");

        fileListView = new ListView<>(containerFiles);
        fileListView.setPrefHeight(150);
        fileListView.setCellFactory(lv -> new ListCell<File>() {
            @Override
            protected void updateItem(File file, boolean empty) {
                super.updateItem(file, empty);
                if (empty || file == null) { setText(null); }
                else {
                    setText(file.getName());
                    setTextFill(Color.web(isNightMode ? N_TEXT : D_TEXT));
                    setFont(Font.font("Courier New", 11));
                    setStyle("-fx-background-color:transparent;");
                }
            }
        });

        Button openContainerBtn = makeButton("⊞  Open Container",  N_BTN_BG, N_ACCENT);
        openContainerBtn.setOnAction(e -> {
            File sel = fileListView.getSelectionModel().getSelectedItem();
            if (sel == null) { statusLabel.setText("⚠  Select a file in the list first."); statusLabel.setTextFill(Color.web("#ef5350")); return; }
            try { Desktop.getDesktop().open(sel.getParentFile()); statusLabel.setText("📁  Opened folder: " + sel.getParentFile().getName()); }
            catch (Exception ex) { statusLabel.setText("⚠  " + ex.getMessage()); statusLabel.setTextFill(Color.web("#ef5350")); }
        });

        Button openPDFBtn = makeButton("⊟  Open PDF / File", N_BTN_BG, "#4fc3f7");
        openPDFBtn.setOnAction(e -> {
            File sel = fileListView.getSelectionModel().getSelectedItem();
            if (sel == null) { statusLabel.setText("⚠  Select a file in the list first."); statusLabel.setTextFill(Color.web("#ef5350")); return; }
            try { Desktop.getDesktop().open(sel); statusLabel.setText("📄  Opened: " + sel.getName()); }
            catch (Exception ex) { statusLabel.setText("⚠  " + ex.getMessage()); statusLabel.setTextFill(Color.web("#ef5350")); }
        });

        Button removeFileBtn = makeButton("✕  Remove File", N_BTN_BG, "#ef5350");
        removeFileBtn.setOnAction(e -> {
            File sel = fileListView.getSelectionModel().getSelectedItem();
            if (sel == null) { statusLabel.setText("⚠  Select a file in the list to remove."); statusLabel.setTextFill(Color.web("#ef5350")); return; }
            containerFiles.remove(sel);
            statusLabel.setText("🗑  Removed: " + sel.getName());
        });

        panel.getChildren().addAll(
            importHeading, chooseFileBtn, inputFilename,
            importBtn, showAllBtn, recordsInfo, sep1,
            outHeading, fileListView,
            openContainerBtn, openPDFBtn, removeFileBtn
        );

        return panel;
    }

    // ─────────────────────────────────────────────────────────────
    //  CENTER PANEL
    // ─────────────────────────────────────────────────────────────
    private VBox buildCenterPanel() {
        VBox panel = new VBox(12);
        panel.setPadding(new Insets(20));

        Label searchHeading = sectionLabel("▶  KEYWORDS  /  SPECIFIC SEARCH");

        searchField = new TextField();
        searchField.setPromptText("Search by question, section, or answer…");

        searchField.textProperty().addListener((obs, oldVal, newVal) -> {
            String query = newVal == null ? "" : newVal.toLowerCase().trim();
            filteredData.setPredicate(row -> {
                if (query.isEmpty()) return true;
                return row.getQuestion().toLowerCase().contains(query)
                    || row.getDetectedSection().toLowerCase().contains(query)
                    || row.getBestAnswer().toLowerCase().contains(query)
                    || row.getQuestionFile().toLowerCase().contains(query);
            });
            statusLabel.setText("🔍  Showing " + filteredData.size() + " of " +
                masterData.size() + " records  —  \"" + newVal + "\"");
            statusLabel.setTextFill(Color.web(isNightMode ? N_ACCENT : D_ACCENT));
        });

        Button searchBtn = makeButton("Search", "#002a33", N_ACCENT);
        searchBtn.setOnAction(e -> searchField.textProperty().setValue(searchField.getText()));

        Button clearBtn = makeButton("Clear", N_BTN_BG, "#888888");
        clearBtn.setOnAction(e -> {
            searchField.clear();
            filteredData.setPredicate(r -> true);
            statusLabel.setText("Search cleared  —  showing all " + masterData.size() + " records.");
        });

        HBox searchRow = new HBox(10, searchField, searchBtn, clearBtn);
        HBox.setHgrow(searchField, Priority.ALWAYS);
        searchRow.setAlignment(Pos.CENTER_LEFT);

        Separator sep2 = styledSeparator();

        Label tableHeading = sectionLabel("▶  DATA TABLE  —  LOOK-UP");

        table = new TableView<>(filteredData);
        table.setPlaceholder(buildTablePlaceholder());
        VBox.setVgrow(table, Priority.ALWAYS);

        TableColumn<ResultRow, String> colFile = new TableColumn<>("Question File");
        colFile.setMinWidth(120);
        colFile.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQuestionFile()));
        styleCol(colFile);

        TableColumn<ResultRow, String> colQ = new TableColumn<>("Question");
        colQ.setMinWidth(260);
        colQ.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getQuestion()));
        styleCol(colQ);

        TableColumn<ResultRow, String> colSec = new TableColumn<>("Detected Section");
        colSec.setMinWidth(220);
        colSec.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getDetectedSection()));
        styleCol(colSec);

        TableColumn<ResultRow, String> colAns = new TableColumn<>("Best Answer");
        colAns.setMinWidth(300);
        colAns.setCellValueFactory(d -> new SimpleStringProperty(d.getValue().getBestAnswer()));
        styleCol(colAns);

        //noinspection unchecked
        table.getColumns().addAll(colFile, colQ, colSec, colAns);
        table.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);

        panel.getChildren().addAll(searchHeading, searchRow, sep2, tableHeading, table);
        return panel;
    }

    // ─────────────────────────────────────────────────────────────
    //  STATUS BAR
    // ─────────────────────────────────────────────────────────────
    private HBox buildStatusBar() {
        statusLabel = new Label("Ready  —  select a question file (.txt or .pdf) and press Match Answers.");
        statusLabel.setFont(Font.font("Courier New", 11));
        statusLabel.setTextFill(Color.web(N_SUBTEXT));
        HBox bar = new HBox(statusLabel);
        bar.setPadding(new Insets(6, 18, 6, 18));
        return bar;
    }

    // ─────────────────────────────────────────────────────────────
    //  HELPERS
    // ─────────────────────────────────────────────────────────────
    private Label sectionLabel(String text) {
        Label l = new Label(text);
        l.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        l.setTextFill(Color.web(isNightMode ? N_ACCENT : D_ACCENT));
        return l;
    }

    private Button makeButton(String text, String bg, String fg) {
        Button b = new Button(text);
        b.setMaxWidth(Double.MAX_VALUE);
        b.setFont(Font.font("Courier New", FontWeight.BOLD, 12));
        String normal = String.format(
            "-fx-background-color:%s;-fx-text-fill:%s;-fx-border-color:%s;" +
            "-fx-border-radius:4;-fx-background-radius:4;-fx-padding:7 12;", bg, fg, fg);
        String hover = String.format(
            "-fx-background-color:%s;-fx-text-fill:#ffffff;-fx-border-color:%s;" +
            "-fx-border-radius:4;-fx-background-radius:4;-fx-padding:7 12;", fg, fg);
        b.setStyle(normal);
        b.setOnMouseEntered(e -> b.setStyle(hover));
        b.setOnMouseExited(e  -> b.setStyle(normal));
        return b;
    }

    private void styleCol(TableColumn<ResultRow, String> col) {
        col.setStyle("-fx-alignment:CENTER-LEFT;-fx-font-family:'Courier New';");
        col.setCellFactory(tc -> new TableCell<ResultRow, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item);
                    setFont(Font.font("Courier New", 12));
                    setTextFill(Color.web(isNightMode ? N_TEXT : D_TEXT));
                    setWrapText(true);
                    setStyle("-fx-background-color:" +
                        (getIndex() % 2 == 0
                            ? (isNightMode ? "#12141f" : "#ddeef7")
                            : (isNightMode ? N_FIELD_BG : D_FIELD_BG)) + ";");
                }
            }
        });
    }

    private Separator styledSeparator() {
        Separator s = new Separator();
        s.setStyle("-fx-background-color:" + N_BORDER + ";");
        return s;
    }

    private Label buildTablePlaceholder() {
        Label l = new Label("No data — choose a question file and press Match Answers.");
        l.setFont(Font.font("Courier New", 13));
        l.setTextFill(Color.web("#333333"));
        return l;
    }

    public static void main(String[] args) { launch(args); }
}