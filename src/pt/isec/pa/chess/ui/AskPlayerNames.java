package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;

public class AskPlayerNames extends Stage {
    ChessGameManager data;
    RootPane rootPane;

    TextField tfWhitePlayer, tfBlackPlayer;
    Button btnConfirm, btnCancel;

    public AskPlayerNames(ChessGameManager data, RootPane rp) {
        this.data=data;
        this.rootPane = rp;

        createViews();
        registerHandlers();
        update();
    }

    private void createViews() {
        // White player
        Label lbWhite = new Label("White Player:");
        lbWhite.setMinWidth(80);
        tfWhitePlayer = new TextField();
        tfWhitePlayer.setPrefWidth(200);
        tfWhitePlayer.setPromptText("Enter white player name");
        HBox whiteBox = new HBox(lbWhite, tfWhitePlayer);
        whiteBox.setAlignment(Pos.BASELINE_LEFT);
        whiteBox.setSpacing(10);

        // Black player
        Label lbBlack = new Label("Black Player:");
        lbBlack.setMinWidth(80);
        tfBlackPlayer = new TextField();
        tfBlackPlayer.setPrefWidth(200);
        tfBlackPlayer.setPromptText("Enter black player name");
        HBox blackBox = new HBox(lbBlack, tfBlackPlayer);
        blackBox.setAlignment(Pos.BASELINE_LEFT);
        blackBox.setSpacing(10);

        btnConfirm = new Button("Confirm");
        btnConfirm.setPrefWidth(9999);
        btnCancel  = new Button("Cancel");
        btnCancel.setPrefWidth(9999);
        HBox btns = new HBox(btnCancel,btnConfirm);
        btns.setSpacing(20);
        btns.setAlignment(Pos.CENTER_RIGHT);

        VBox root = new VBox(whiteBox, blackBox,btns);
        root.setSpacing(10);
        root.setPadding(new Insets(16));

        Scene scene = new Scene(root,250,120);
        this.setScene(scene);
        this.setResizable(false);
    }

    private void registerHandlers() {
        //Confirm name
        btnConfirm.setOnAction(actionEvent -> {
            String whiteName = tfWhitePlayer.getText().trim();
            String blackName = tfBlackPlayer.getText().trim();

            // Default names if empty
            if (whiteName.isEmpty()) {
                whiteName = "White Player";
            }
            if (blackName.isEmpty()) {
                blackName = "Black Player";
            }

            try {
                data.startNewGame(whiteName, blackName);
                rootPane.setGameStarted(true);
                this.close();
            } catch (IllegalArgumentException e) {
                System.err.println("Error setting player names: " + e.getMessage());
            }
        });
        //Cancel names
        btnCancel.setOnAction(actionEvent -> this.close());
    }

    private void update() {
    }
}
