package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.command.CommandManager;
import pt.isec.pa.chess.model.command.MoveCommand;
import pt.isec.pa.chess.model.data.Spot;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;

/**
 * The main controller class for the chess game implementing the Facade pattern.
 * Manages all game operations including move validation, game state persistence,
 * undo/redo functionality, and UI notifications.
 * This class serves as the sole interface between the UI and the game logic.
 * <p>The UI should only interact with this class and never directly with {@link ChessGame}.</p>
 *
 * @author Group 06
 * @version 1.0.0
 * @since 1.0.0
 *
 * @see ChessGame
 * @see CommandManager
 * @see MoveCommand
 * @see PropertyChangeSupport
 * @see ChessGameSerialization
 * @see ModelLog
 */
public class ChessGameManager {
    /** Event fired when a new game starts */
    public static final String PROP_GAME_STATE = "prop_game_state";
    /** Event fired when a pawn needs promotion */
    public static final String PROP_PROMOTION = "prop_promotion";
    /** Event fired when the board changes */
    public static final String PROP_UPDATE_BOARD = "prop_update_board";
    /** Event fired when game logs update */
    public static final String PROP_LOGS = "prop_logs";
    /** Event fired when sound language changes */
    private final ChessGame game;

    PropertyChangeSupport pcs;
    private final CommandManager cm;

    /**
     * Constructs a new ChessGameManager with empty game state.
     * Initializes:
     * <ul>
     *   <li>New {@link ChessGame} instance</li>
     *   <li>{@link PropertyChangeSupport} for UI notifications</li>
     *   <li>{@link CommandManager} for undo/redo functionality</li>
     * </ul>
     */
    public ChessGameManager() {
        this.game = new ChessGame();
        pcs = new PropertyChangeSupport(this);
        this.cm = new CommandManager();
    }

    /**
     * Adds a property change listener for specific game events.
     *
     * @param property The property to listen to (PROP_* constants)
     * @param listener The listener to be notified
     *
     * @see PropertyChangeListener
     * @see #PROP_GAME_STATE
     * @see #PROP_PROMOTION
     * @see #PROP_UPDATE_BOARD
     * @see #PROP_LOGS
     * @see #PROP_SOUND_LANGUAGE
     */
    public void addPropertyChangeListener(String property, PropertyChangeListener listener) {
        pcs.addPropertyChangeListener(property, listener);
    }

    /**
     * Starts a new game with the given player names.
     * @param whitePlayerName Name for white player
     * @param blackPlayerName Name for black player
     */
    public void startNewGame(String whitePlayerName, String blackPlayerName) {
        game.setPlayerNames(whitePlayerName, blackPlayerName);
        game.startNewGame();
        cm.reset();

        pcs.firePropertyChange(PROP_UPDATE_BOARD, null, queryBoard()); // Notifica ouvintes
        pcs.firePropertyChange(PROP_GAME_STATE, null, null); // Notifica ouvintes
    }

    public void resetGame() {
        game.resetGame();
    }

    /**
     * Gets the current board state as text.
     * @return String representation of the board
     */
    public String queryBoard() {
        return game.queryBoard();
    }

    /**
     * Saves the game to a file.
     * @param file Destination file (.paf extension added if missing)
     */
    public void saveGame(File file) throws IOException {
        if (file == null) return;

        if (!file.getName().toLowerCase().endsWith(".paf")) {
            file = new File(file.getAbsolutePath() + ".paf");
        }
        ChessGameSerialization.serialize(game, file.getAbsolutePath());
    }

    /**
     * Loads a game from file.
     * @param file File to load (.paf format)
     */
    public void loadGame(File file) throws IOException, ClassNotFoundException {
        if (file == null) return;

        String old = queryBoard(); // Estado anterior do tabuleiro
        ChessGame loadedGame = ChessGameSerialization.deserialize(file.getAbsolutePath());
        this.game.resetGame();
        this.game.importGame(loadedGame.exportGame());
        this.game.setPlayerNames(loadedGame.getWhitePlayerName(), loadedGame.getBlackPlayerName());
        cm.reset();
        fireBoardUpdate();
        pcs.firePropertyChange(PROP_GAME_STATE, null, null); // Notifica ouvintes
        pcs.firePropertyChange(PROP_LOGS, null, ModelLog.getInstance().getList()); // Notifica ouvintes
    }

    /**
     * Exports game to text file.
     * @param file Destination file (.txt)
     * @throws IOException if write fails
     */
    public void exportGame(File file) throws IOException {
        if (file == null) return;

        if (!file.getName().toLowerCase().endsWith(".txt")) {
            file = new File(file.getAbsolutePath() + ".txt");
        }
        java.nio.file.Files.writeString(file.toPath(), game.exportGame());
    }

    /**
     * Imports game from text file.
     * @param file File to import (.txt)
     * @throws IOException if read fails
     */
    public void importGame(File file) throws IOException {
        if (file == null) return;

        String old = queryBoard(); // Estado anterior do tabuleiro
        String gameState = java.nio.file.Files.readString(file.toPath());
        game.importGame(gameState);
        cm.reset();
        fireBoardUpdate();
        pcs.firePropertyChange(PROP_GAME_STATE, null, null); // Notifica ouvintes
        pcs.firePropertyChange(PROP_LOGS, null, ModelLog.getInstance().getList()); // Notifica ouvintes
    }

    /**
     * Gets whose turn it is ("White" or "Black").
     */
    public String getCurrentPlayer() {
        return game.getCurrentPlayer();
    }

    /**
     * Gets the name of the current player.
     */
    public String getCurrentNamePlayer() {
        return game.getCurrentNamePlayer();
    }

    /**
     * Gets the size of the chess board.
     */
    public int getBoardSize() {
        return game.getBoardSize();
    }

    /**
     * Gets the current game state.
     *
     * @return The current GameState of the chess game.
     */
    public ChessGame.GameState getGameState() {
        return game.getGameState();
    }

    /**
     * Sets the current game state.
     *
     * @param state The GameState to set.
     */
    public void setGameState(ChessGame.GameState state) {
        game.setGameState(state);
    }

    /**
     * Gets white player's name.
     * @return White player name
     */
    public String getWhitePlayerName() {
        return game.getWhitePlayerName();
    }

    /**
     * Gets black player's name.
     * @return Black player name
     */
    public String getBlackPlayerName() {
        return game.getBlackPlayerName();
    }

    //Moving pieces
    public boolean tryMove(String from, String to) {
        String old = queryBoard();
        boolean result = game.tryMove(from, to);
        if (result) {

        }
        return result;
    }

    /**
     * Attempts a chess move.
     * @param from Starting position (e.g., "e2")
     * @param to Target position (e.g., "e4")
     * @return MoveResult indicating what happened
     */
    public ChessGame.MoveResult move(String from, String to) {
        String old = queryBoard();
        MoveCommand cmd = new MoveCommand(game, from, to);

        if (cm.invokeCommand(cmd)) {
            ChessGame.MoveResult result = cmd.getMoveResult();

            fireBoardUpdate();

            if (result == ChessGame.MoveResult.PROMOTION) {
                pcs.firePropertyChange(PROP_PROMOTION, null, null);
            } else if (result == ChessGame.MoveResult.CHECKMATEc || result == ChessGame.MoveResult.CHECKMATEe || result == ChessGame.MoveResult.STALEMATE) {
                pcs.firePropertyChange(PROP_GAME_STATE, null, null); // Notifica ouvintes
            }

            pcs.firePropertyChange(PROP_LOGS, old, ModelLog.getInstance().getList());
            return result;
        }
        return ChessGame.MoveResult.INVALID_MOVE;
    }

    /**
     * Promotes a pawn to another piece.
     * @param promotion Piece type ("QUEEN", "ROOK", etc.)
     */
    public void promotePawn(String promotion) {
        ModelLog.getInstance().addLog("Pawn promoted to " + promotion + " at " + game.promotePawn(promotion));
    }

    /**
     * Gets the piece at specified board coordinates.
     * @param row Row index (0-based)
     * @param col Column index (0-based)
     * @return Piece notation or null if empty
     */
    public String getPieceAt(int row, int col) {
        return game.getPieceAt(row, col);
    }

    /**
     * Checks if piece has any legal moves.
     * @param row Piece's row
     * @param col Piece's column
     * @return true if legal moves exist
     */
    public boolean hasMoves(int row, int col) {
        return game.hasMoves(row, col);
    }

    /**
     * Checks if specific move is valid.
     * @param start Starting position
     * @param row Target row
     * @param col Target column
     * @return true if move is legal
     */
    public boolean hasMove(Spot start, int row, int col) {
        return game.hasMove(start, row, col);
    }

    /**
     * Gets formatted legal moves for a piece.
     * @param row Piece's row
     * @param col Piece's column
     * @return String of legal moves
     */
    public String getLegalMovesString(int row, int col) {
        return game.getLegalMovesString(row, col);
    }

    /**
     * Undoes the last move.
     * @return true if undo was successful
     */
    public boolean undo() {
        String old = queryBoard();
        boolean success = cm.undo();
        if (success) {
            fireBoardUpdate();
            pcs.firePropertyChange(PROP_LOGS, null, ModelLog.getInstance().getList());
            pcs.firePropertyChange(PROP_GAME_STATE, null, null); // Notifica ouvintes
        }
        return success;
    }

    /**
     * Redoes the last undone move.
     * @return true if redo was successful
     */
    public boolean redo() {
        String old = queryBoard();
        boolean success = cm.redo();
        if (success) {
            fireBoardUpdate();
            pcs.firePropertyChange(PROP_LOGS, null, ModelLog.getInstance().getList());
            pcs.firePropertyChange(PROP_GAME_STATE, null, null); // Notifica ouvintes
        }
        return success;
    }

    /**
     * Checks if undo is possible.
     */
    public boolean canUndo() {
        return cm.hasUndo();
    }

    /**
     * Checks if redo is possible.
     */
    public boolean canRedo() {
        return cm.hasRedo();
    }

    public static final String PROP_SOUND_LANGUAGE = "prop_sound_language";
    private int soundLanguage = -1; // -1 = disabled, 0 = English, 1 = Portuguese

    /**
     * Sets the sound language preference.
     * @param language 0=English, 1=Portuguese, -1=disabled
     */
    public void setSoundLanguage(int language) {
        int old = this.soundLanguage;
        this.soundLanguage = language;
        pcs.firePropertyChange(PROP_SOUND_LANGUAGE, old, language);
    }

    /**
     * Gets the current sound language setting.
     */
    public int getSoundLanguage() {
        return soundLanguage;
    }

    /**
     * Notifies listeners of board updates.
     */
    public void fireBoardUpdate() {
        pcs.firePropertyChange(PROP_UPDATE_BOARD, null, queryBoard());
    }

}