package pt.isec.pa.chess.ui;

import javafx.scene.control.Alert;
import javafx.stage.FileChooser;
import java.io.File;

public class Utils {
    private Utils(){}

    public static FileChooser createFileChooser(String title, FileChooser.ExtensionFilter... filters) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle(title);
        fileChooser.setInitialDirectory(new File("."));
        fileChooser.getExtensionFilters().addAll(filters);
        return fileChooser;
    }

    public static void showAlert(Alert.AlertType type, String title, String message) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}