package pt.isec.pa.chess.ui;

import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.FileChooser;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.ChessGameManager;
import java.io.File;

public class RootPane extends BorderPane {
    private final ChessGameManager data;
    private BoardCanvas boardCanvas;
    private Pane center;

    private Label lbStatus; //status like learning mode, normal mode, white's&black's turn, etc.
    private MenuItem mnNew, mnOpen, mnSave, mnImport, mnExport, mnQuit,
    mnNormal, mnLearning, mnShowMoves, mnUndo, mnRedo, mnEnglish, mnPortuguese, mnToggle;
    private Menu mnMode, mnAcessibility;

    private boolean gameStarted = false;
    private boolean showMoves = false;

    public RootPane(ChessGameManager data) {
        this.data = data;

        createViews();
        registerHandlers();
        update();
        updateStatus("Welcome to Chess Game");
    }

    private void createViews() {
        setTop(createMenu());

        lbStatus = new Label();
        setBottom(lbStatus);
        BorderPane.setMargin(lbStatus, new Insets(10));

        LearningFeatures(true);
        SoundFeatures(true);

        center = new Pane();
        setCenter(center);
        boardCanvas = new BoardCanvas(data, center, this);
        center.getChildren().add(boardCanvas);

    }

    private MenuBar createMenu() {
        MenuBar mb = new MenuBar();

        // Game menu
        Menu mnGame = new Menu("Game");
        mnNew = new MenuItem("New");
        mnOpen = new MenuItem("Open");
        mnSave = new MenuItem("Save");
        mnImport = new MenuItem("Import");
        mnExport = new MenuItem("Export");
        mnQuit = new MenuItem("Quit");
        mnGame.getItems().addAll(mnNew, mnOpen, mnSave, mnImport, mnExport,
                new SeparatorMenuItem(), mnQuit);

        // Mode menu
        mnMode = new Menu("Mode");
        mnNormal = new MenuItem("Normal");
        mnLearning = new MenuItem("Learning");
        mnShowMoves = new MenuItem("Show Possible Moves");
        mnUndo = new MenuItem("Undo");
        mnRedo = new MenuItem("Redo");

        mnMode.getItems().addAll(mnNormal, mnLearning, new SeparatorMenuItem(), mnShowMoves, mnUndo, mnRedo);

        // Acessiblility menu
        mnAcessibility = new Menu("Acessiblility");
        mnToggle = new MenuItem("Enable Sounds");
        mnEnglish = new MenuItem("English");
        mnPortuguese = new MenuItem("Portuguese");

        mnAcessibility.getItems().addAll(mnToggle, new SeparatorMenuItem(), mnEnglish,mnPortuguese);

        mb.getMenus().addAll(mnGame, mnMode, mnAcessibility);

        return mb;
    }

    private void registerHandlers() {
        data.addPropertyChangeListener(ChessGameManager.PROP_UPDATE_BOARD, evt -> { update(); });

        data.addPropertyChangeListener(ChessGameManager.PROP_GAME_STATE, evt -> {

            switch (data.getGameState()) {
                case ChessGame.GameState.CHECKMATEc -> {
                    String winner;
                    String title;
                    title = "Checkmate!";
                    winner = "\nVencedor:" + (data.getCurrentPlayer().equals("White") ? data.getWhitePlayerName() : data.getBlackPlayerName());
                    Platform.runLater(() -> Utils.showAlert(Alert.AlertType.INFORMATION, title, winner));
                }
                case ChessGame.GameState.CHECKMATEe -> {
                    String winner;
                    String title;
                    title = "Checkmate!";
                    winner = "\nVencedor: " + (data.getCurrentPlayer().equals("White") ? data.getBlackPlayerName() : data.getWhitePlayerName());
                    Platform.runLater(() -> Utils.showAlert(Alert.AlertType.INFORMATION, title, winner));
                }
                case ChessGame.GameState.STALEMATE -> {
                    String winner;
                    String title;
                    title = "Stalemate!";
                    winner = "\nEmpate!";
                    System.out.println(title + winner);
                    Platform.runLater(() -> Utils.showAlert(Alert.AlertType.INFORMATION, title, winner));
                }
            }
        });


        //New game
        mnNew.setOnAction(e -> {
            AskPlayerNames askName = new AskPlayerNames(data, this);
            askName.showAndWait();


            // Only proceed if both player names are set
            if (data.getWhitePlayerName() != null && data.getBlackPlayerName() != null) {
                updateStatus("Game started!");
            }
            else {
                updateStatus("New game canceled");
            }
        });

        // Open game
        mnOpen.setOnAction(e -> {
            FileChooser fileChooser = Utils.createFileChooser(
                    "Open Chess Game",
                    new FileChooser.ExtensionFilter("PA Files (*.paf)", "*.paf")
            );
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (file == null) return;

            try {
                data.loadGame(file);
                Utils.showAlert(Alert.AlertType.INFORMATION,
                        "Game Loaded", "Game was successfully loaded");
            } catch (Exception ex) {
                Utils.showAlert(Alert.AlertType.ERROR,
                        "Load Error", "Failed to load game: " + ex.getMessage());
            }
        });

        // Save game
        mnSave.setOnAction(e -> {
            FileChooser fileChooser = Utils.createFileChooser(
                    "Save Chess Game",
                    new FileChooser.ExtensionFilter("PA Files (*.paf)", "*.paf")
            );
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (file == null) return;

            try {
                data.saveGame(file);
                Utils.showAlert(Alert.AlertType.INFORMATION,
                        "Game Saved", "Game was successfully saved");
            } catch (Exception ex) {
                Utils.showAlert(Alert.AlertType.ERROR,
                        "Save Error", "Failed to save game: " + ex.getMessage());
            }
        });

        // Import game
        mnImport.setOnAction(e -> {
            FileChooser fileChooser = Utils.createFileChooser(
                    "Import Chess Game",
                    new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                    new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv")
            );
            File file = fileChooser.showOpenDialog(this.getScene().getWindow());
            if (file == null) return;
            AskPlayerNames askName = new AskPlayerNames(data, this);
            askName.showAndWait();

            // Only proceed if both player names are set
            if (data.getWhitePlayerName() != null && data.getBlackPlayerName() != null) {
                updateStatus("Game started!");
            }
            else {
                updateStatus("Import game canceled");
            }

            try {
                data.importGame(file);
                Utils.showAlert(Alert.AlertType.INFORMATION,
                        "Game Imported", "Game state was successfully imported");
            } catch (Exception ex) {
                Utils.showAlert(Alert.AlertType.ERROR,
                        "Import Error", "Failed to import game: " + ex.getMessage());
            }

        });

        // Export game
        mnExport.setOnAction(e -> {
            FileChooser fileChooser = Utils.createFileChooser(
                    "Export Chess Game",
                    new FileChooser.ExtensionFilter("Text Files (*.txt)", "*.txt"),
                    new FileChooser.ExtensionFilter("CSV Files (*.csv)", "*.csv")
            );
            File file = fileChooser.showSaveDialog(this.getScene().getWindow());
            if (file == null) return;

            try {
                data.exportGame(file);
                Utils.showAlert(Alert.AlertType.INFORMATION,
                        "Game Exported", "Game state was successfully exported");
            } catch (Exception ex) {
                Utils.showAlert(Alert.AlertType.ERROR,
                        "Export Error", "Failed to export game: " + ex.getMessage());
            }
        });

        // Quit
        mnQuit.setOnAction(e -> Platform.exit());

        mnMode.setOnShowing(e -> updateModeStyles());

        mnShowMoves.setOnAction(e -> {
            setShowMoves(!isShowMoves());
            mnShowMoves.getStyleableNode().setStyle(isShowMoves() ? "-fx-background-color: #b3e5fc;" : "");
        });


        mnNormal.setOnAction(e -> {
            setShowMoves(false);
            mnShowMoves.getStyleableNode().setStyle("");
            updateStatus("Normal mode activated");
            LearningFeatures(true);
            updateModeStyles();
        });

        mnLearning.setOnAction(e -> {
            updateStatus("Learning mode activated");
            LearningFeatures(false);
            updateModeStyles();
        });

        mnUndo.setOnAction(e -> {
            // Se estiver em modo de aprendizagem
            if (!mnShowMoves.isDisable()) {
                if (boardCanvas.hasSelection()) {
                    boardCanvas.clearSelection();
                    updateStatus("Seleção desfeita. Escolha outra peça.");
                    return;
                }
            }

            // Caso contrário, desfaz jogada
            if (data.undo()) {
                updateStatus("Undo successful - " + data.getCurrentNamePlayer() + "'s turn");
            } else {
                updateStatus("Nothing to undo");
            }
        });

        mnRedo.setOnAction(e -> {
            if (data.redo()) {
                updateStatus("Redo successful - " + data.getCurrentNamePlayer() + "'s turn");
            } else {
                updateStatus("Nothing to redo");
            }
        });

        mnAcessibility.setOnShowing(e -> updateSoundMenuStyles());

        mnEnglish.setOnAction(e -> {
            data.setSoundLanguage(0);
            updateStatus("Sound language set to English");
            updateSoundMenuStyles();
        });

        mnPortuguese.setOnAction(e -> {
            data.setSoundLanguage(1);
            updateStatus("Sound language set to Portuguese");
            updateSoundMenuStyles();
        });

        mnToggle.setOnAction(e -> {
            if(mnPortuguese.isDisable()) {
                data.setSoundLanguage(0);
                updateStatus("Sound language set to English");
                SoundFeatures(false);
                mnToggle.setText("Disable Sounds");
            } else {
                SoundFeatures(true);
                updateStatus("Sound disabled");
                mnToggle.setText("Enable Sounds");
                data.setSoundLanguage(-1);
            }
            updateSoundMenuStyles();
        });
    }

    private void updateModeStyles() {
        Platform.runLater(() -> {
            boolean isNormalMode = mnShowMoves.isDisable();
            mnNormal.getStyleableNode().setStyle(isNormalMode ? "-fx-background-color: #b3e5fc;" : "");
            mnLearning.getStyleableNode().setStyle(isNormalMode ? "" : "-fx-background-color: #b3e5fc;");
        });
    }

    private void updateSoundMenuStyles() {
        Platform.runLater(() -> {
            int lang = data.getSoundLanguage();

            // Clear all active styles first
            mnEnglish.getStyleableNode().setStyle("");
            mnPortuguese.getStyleableNode().setStyle("");
            mnToggle.getStyleableNode().setStyle("");

            if(lang > -1)
                mnToggle.getStyleableNode().setStyle("-fx-background-color: #b3e5fc;");

            // Add active style to the selected item
            switch (lang) {
                case 0 -> mnEnglish.getStyleableNode().setStyle("-fx-background-color: #b3e5fc;");
                case 1 -> mnPortuguese.getStyleableNode().setStyle("-fx-background-color: #b3e5fc;");
                case -1 -> mnToggle.getStyleableNode().setStyle("");
            }
        });
    }

    private void SoundFeatures(boolean disable) {
        mnEnglish.setDisable(disable);
        mnPortuguese.setDisable(disable);
    }

    private void LearningFeatures(boolean disable) {
        mnShowMoves.setDisable(disable);
        if (!disable) {
            mnUndo.setDisable( !(boardCanvas.hasSelection() || data.canUndo()) );
            mnRedo.setDisable(!data.canRedo());
        } else {
            mnUndo.setDisable(disable);
            mnRedo.setDisable(disable);
        }
    }

    public boolean getGameStarted() {
        return gameStarted;
    }

    public void setGameStarted(boolean gameStarted) {
        this.gameStarted = gameStarted;
    }

    public boolean isShowMoves() { return showMoves; }

    public void setShowMoves(boolean showMoves) {
        boolean old = this.showMoves;
        this.showMoves = showMoves;
        data.fireBoardUpdate();
    }

    private void updateStatus(String message) {
        lbStatus.setText(message);
    }

    private void update() {
        if(!mnShowMoves.isDisable())
        {
            mnUndo.setDisable( !(boardCanvas.hasSelection() || data.canUndo()) );
            mnRedo.setDisable(!data.canRedo());
        }
    }
}