package pt.isec.pa.chess.ui;

import javafx.scene.control.*;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;

import java.util.List;

public class ListView extends javafx.scene.control.ListView<String> {
    private final ChessGameManager game;
    private MenuItem deleteLog, clearLogs;

    public ListView(ChessGameManager game) {
        this.game = game;
        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        deleteLog = new MenuItem("Delete Selected Log");
        clearLogs = new MenuItem("Clear All Logs");

        ContextMenu contextMenu = new ContextMenu(deleteLog, clearLogs);
        this.setContextMenu(contextMenu);
        this.setPlaceholder(new Label("No log entries yet"));
    }

    private void registerHandlers() {
        // Update logs when they change
        game.addPropertyChangeListener(ChessGameManager.PROP_LOGS,evt -> {
            List<String> newLogs = (List<String>) evt.getNewValue();
            this.getItems().setAll(newLogs);
        });

        // Clear all logs
        clearLogs.setOnAction(e -> {
            ModelLog.getInstance().clear();
            update();
        });

        // Delete single selected log
        deleteLog.setOnAction(e -> {
            int selectedIndex = this.getSelectionModel().getSelectedIndex();
            if (selectedIndex >= 0) {
                ModelLog.getInstance().removeLog(selectedIndex);
                update();
            }
        });
    }

    private void update() {
        this.getItems().clear();
        this.getItems().addAll(ModelLog.getInstance().getList());
    }
}