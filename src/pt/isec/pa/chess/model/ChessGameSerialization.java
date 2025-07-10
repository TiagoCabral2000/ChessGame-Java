package pt.isec.pa.chess.model;

import java.io.*;

public final class ChessGameSerialization {
    private ChessGameSerialization() {} // Prevent instantiation

    public static void serialize(ChessGame game, String filename) throws IOException {
        try (ObjectOutputStream oos = new ObjectOutputStream(
                new FileOutputStream(filename))) {
            oos.writeObject(game);
        }
    }

    public static ChessGame deserialize(String filename) throws IOException, ClassNotFoundException {
        try (ObjectInputStream ois = new ObjectInputStream(
                new FileInputStream(filename))) {
            return (ChessGame) ois.readObject();
        }
    }
}