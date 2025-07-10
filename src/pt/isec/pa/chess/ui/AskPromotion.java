package pt.isec.pa.chess.ui;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import pt.isec.pa.chess.model.ChessGameManager;

public class AskPromotion extends Stage {
    ChessGameManager data;

    Node center;

    TextField tfWhitePlayer, tfBlackPlayer;

    ToggleGroup radioGroup;
    ToggleButton btnQueen, btnRook, btnBishop, btnKnight;
    Button btnConfirm;

    public AskPromotion(ChessGameManager data, Node center) {
        initModality(Modality.APPLICATION_MODAL);
        initStyle(StageStyle.UNDECORATED);
        setTitle("Promotion");

        this.center = center;
        this.centerOnScreen();
        this.data=data;

        createViews();
        registerHandlers();
        update();

        this.setOnCloseRequest(e -> {
            e.consume(); // This will prevent the dialog from closing.
        });

        this.setOnShown(e -> {
            double centerX = center.localToScene(0, 0).getX() + center.getScene().getWindow().getX();
            double centerY = center.localToScene(0, 0).getY() + center.getScene().getWindow().getY();

            double dialogWidth = this.getWidth();
            double dialogHeight = this.getHeight();

            this.setX(centerX + center.getBoundsInParent().getWidth() / 2 - dialogWidth / 2);
            this.setY(centerY + center.getBoundsInParent().getHeight() / 2 - dialogHeight / 2);
        });
    }

    private void createViews() {

        radioGroup = new ToggleGroup();

        btnQueen = new ToggleButton("Queen");
        btnQueen.setToggleGroup(radioGroup);
        btnQueen.setSelected(true);
        btnQueen.setPrefWidth(9999);

        btnRook = new ToggleButton("Rook");
        btnRook.setToggleGroup(radioGroup);
        btnRook.setPrefWidth(9999);

        btnBishop = new ToggleButton("Bishop");
        btnBishop.setToggleGroup(radioGroup);
        btnBishop.setPrefWidth(9999);

        btnKnight = new ToggleButton("Knight");
        btnKnight.setToggleGroup(radioGroup);
        btnKnight.setPrefWidth(9999);

        btnConfirm = new Button("Confirm");
        btnConfirm.setPrefWidth(9999);
        HBox btns1 = new HBox(btnQueen, btnRook);
        btns1.setSpacing(20);
        btns1.setAlignment(Pos.CENTER);
        HBox btns2 = new HBox(btnBishop, btnKnight);
        btns2.setSpacing(20);
        btns2.setAlignment(Pos.CENTER);
        HBox btnsC = new HBox(btnConfirm);
        btnsC.setSpacing(20);
        btnsC.setAlignment(Pos.CENTER);

        VBox root = new VBox(btns1, btns2, btnsC);
        root.setSpacing(10);
        root.setPadding(new Insets(16));

        root.setStyle("""
        -fx-background-color: white;
        -fx-border-color: #CCCCCC;
        -fx-border-radius: 10;
        -fx-background-radius: 10;
        -fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 5);
            """);

        Scene scene = new Scene(root,250,130);
        this.setScene(scene);
        this.setResizable(false);
    }

    private void registerHandlers() {
        //Confirm name
        this.btnConfirm.setOnAction(e -> {
            ToggleButton selected = (ToggleButton) this.radioGroup.getSelectedToggle();
            if (selected != null) {
                String promotion = selected.getText();
                data.promotePawn(promotion);
                this.close();
            }
        });
    }

    private void update() {
    }
}
