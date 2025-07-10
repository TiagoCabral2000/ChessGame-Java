package pt.isec.pa.chess.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import pt.isec.pa.chess.model.ChessGameManager;

public class ChessMainJFX extends Application {
    ChessGameManager game;
    public ChessMainJFX() { game = new ChessGameManager(); }
    @Override
    public void start(Stage stage) throws Exception {
        createStage(stage);
        //createStage(new Stage());
        createLog(stage.getX()+stage.getWidth(),stage.getY());
    }

    void createStage(Stage stage){
        RootPane root = new RootPane(game);
        Scene scene = new Scene(root,600,600);
        stage.setScene(scene);
        stage.setTitle("ChessGame - PA 24/25");
        stage.setMinWidth(350);
        stage.setMinHeight(350);

        stage.show();
    }

    void createLog(double x, double y){
        Stage stage = new Stage();
        ListView modelLog = new ListView(game);
        Scene scene = new Scene(modelLog,300,400);
        stage.setScene(scene);
        stage.setTitle("Drawing List");
        stage.setX(x);
        stage.setY(y);
        stage.show();
    }
}
