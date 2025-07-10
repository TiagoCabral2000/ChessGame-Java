package pt.isec.pa.chess.model;

import pt.isec.pa.chess.model.data.*;
import pt.isec.pa.chess.model.data.pieces.King;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.Rook;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Manages the whole chess game logic, including board state, player turns, move validation, special moves like castling, en passant, promotion, etc.
 *
 * @author Group 06
 * @version 1.0.0
 *
 * @see Board
 * @see Player
 * @see Piece
 */

public class ChessGame implements Serializable {
    //Incrementar este valor se forem feitas alterações a classe
    //Exemplo 1L -> 2L
    /**
     * Serial version UID for serialization compatibility.
     * Increment this value when making incompatible class changes.
     */
    private static final long serialVersionUID = 3L;

    private Board board;
    private Player white;
    private Player black;

    private Piece pawnToPromote;

    private static boolean whiteTurn = true;
    private GameState gameState = GameState.NOT_STARTED;


    private String whitePlayerName;
    private String blackPlayerName;

    /**
     * Constructs a new ChessGame with uninitialized players.
     * Player names must be set before starting the game.
     */
    public ChessGame() {

    }


    /**
     * Gets the current game state.
     *
     * @return The current GameState of the chess game
     */
    public GameState getGameState() {
        return gameState;
    }

    /**
     * Sets the current game state.
     *
     * @param gm The new GameState to set
     */
    public void setGameState(GameState gm) {
        gameState = gm;
    }

    /**
     * Returns a string representation of the current board state.
     *
     * @return String representation of the board, or null if game hasn't started
     */
    public String queryBoard() {
        if (board == null) return null;
        StringBuilder sb = new StringBuilder();
        sb.append(board);
        //sb.append(String.format("\n %s's turn (%s)", getCurrentPlayer(), whiteTurn ? whitePlayerName : blackPlayerName));
        return sb.toString();
    }

    /**
     * Gets the color of the current player ("White" or "Black").
     *
     * @return Current player's color as string
     */
    public String getCurrentPlayer() {
        return whiteTurn ? "White" : "Black";
    }

    /**
     * Gets the name of the current player.
     *
     * @return Current player's name
     */
    public String getCurrentNamePlayer() {
        return whiteTurn ? white.getName() : black.getName();
    }

    /**
     * Gets the name of the opposing player.
     *
     * @return Opposing player's name
     */
    public String getCurrentNameEnemy() {
        return !whiteTurn ? white.getName() : black.getName();
    }

    /**
     * Starts a new game with the current player names.
     *
     * @throws IllegalStateException if player names haven't been set
     */
    public void startNewGame() {
        if (whitePlayerName == null || blackPlayerName == null) {
            throw new IllegalStateException("Os nomes dos jogadores devem ser definidos antes de iniciar o jogo.");//////tratamento de extensoes
        }

        gameState = GameState.RUNNING;
        resetGame();
    }

    /**
     * Resets the game to initial state with current player names.
     */
    public void resetGame() {
        board = new Board();
        board.initializer();

        white = new Player(true, whitePlayerName);
        black = new Player(false, blackPlayerName);

        updatePlayerPieces();

        whiteTurn = true;
    }


    /**
     * Checks if the specified player is in stalemate.
     * A stalemate occurs when the player is not in check but has no legal moves.
     *
     * @param cPlayer true to check the current player, false to check the opponent
     * @return true if the player is in stalemate, false otherwise
     */
    public boolean isStalemate(boolean cPlayer) {
        // 1. Identifica o jogador
        Player player;
        if (cPlayer)
            player = (whiteTurn ? white : black);
        else
            player = (!whiteTurn ? white : black);

        // 2. Encontra o rei desse jogador.
        King king = null;
        for (Piece piece : player.getAlive()) {
            if (piece instanceof King) {
                king = (King) piece;
                break;
            }
        }
        // Se o rei não existe, algo está muito errado.
        if (king == null) return false;

        // 3. CONDIÇÃO 1: O rei do próximo jogador está em xeque?
        // Se estiver, NÃO pode ser stalemate.
        if (king.isInCheck(new ArrayList<>()) > 0) {
            return false;
        }

        // 4. CONDIÇÃO 2: O próximo jogador tem algum movimento legal?
        // Itera por todas as peças do jogador.
        for (Piece piece : player.getAlive()) {
            // Pede à peça para calcular a sua lista de movimentos (que agora são 100% legais).
            piece.calculateLegalMoves();
            // Se a peça tiver QUALQUER movimento na sua lista, então não é stalemate.
            if (piece.hasMoves()) {
                return false;
            }
        }

        // 5. Se o código chegou até aqui, significa que:
        // O rei NÃO está em xeque (passo 3) E NÃO há movimentos legais (passo 4).
        // Esta é a definição de stalemate.
        return true;
    }

    /**
     * Checks if the specified player is in checkmate.
     * A checkmate occurs when the player's king is in check and there are no legal moves to escape.
     *
     * @param cPlayer true to check the current player, false to check the opponent
     * @return true if the player is in checkmate, false otherwise
     */
    public boolean isCheckmate(boolean cPlayer) {

        System.out.println("isCheckmate");

        // 1. Identifica o jogador
        Player player;
        if (cPlayer)
            player = (whiteTurn ? white : black);
        else
            player = (!whiteTurn ? white : black);
        System.out.println("player");


        // 2. Encontra o rei desse jogador.
        King king = null;
        for (Piece piece : player.getAlive()) {
            if (piece instanceof King) {
                king = (King) piece;
                break;
            }
        }
        if (king == null) return false;
        System.out.println("kingnotnull");
        System.out.println("[IS_CHECKMATE_DEBUG] Verificando jogador: " + (player.isWhite() ? "Branco" : "Preto"));
        System.out.println("[IS_CHECKMATE_DEBUG] Rei a verificar: " + king);
        int checkStatus = king.isInCheck(new ArrayList<>());
        System.out.println("[IS_CHECKMATE_DEBUG] Resultado de king.isInCheck(): " + checkStatus);
        if (checkStatus == 0) {
            System.out.println("[IS_CHECKMATE_DEBUG] ERRO? Rei não está em xeque, retornando false.");
            return false; // Esta é a sua lógica original
        }


        // 3. CONDIÇÃO 1: O rei do próximo jogador está em xeque?
        // Se NÃO estiver, NÃO pode ser xeque-mate.
        if (king.isInCheck(new ArrayList<>()) == 0) {
            return false;
        }
        System.out.println("kingnotnull");


        // 4. CONDIÇÃO 2: O próximo jogador tem algum movimento legal para escapar?
        // (A lógica é idêntica à do stalemate a partir daqui)
        for (Piece piece : player.getAlive()) {
            // Pede à peça para calcular a sua lista de movimentos (que agora são 100% legais).
            // Se o rei está em xeque, esta lista só conterá movimentos que o tirem do xeque.
            System.out.println("[IS_CHECKMATE_DEBUG] Verificando movimentos para a peça branca: " + piece);
            piece.calculateLegalMoves();
            if (piece.hasMoves()) {
                System.out.println("[IS_CHECKMATE_DEBUG] ERRO? Peça " + piece + " reporta que TEM movimentos legais. Retornando false.");
                for(Spot s : piece.getLegalMoves()){
                    System.out.println("    Movimento 'legal' encontrado: " + s.convertToChessNotation(board.getBoardSize()));
                }
                return false;
            } else {
                System.out.println("[IS_CHECKMATE_DEBUG] Peça " + piece + " NÃO tem movimentos legais.");
            }
        }

        // 5. Se o código chegou até aqui, significa que:
        // O rei ESTÁ em xeque (passo 3) E NÃO há movimentos legais para escapar (passo 4).
        // Esta é a definição de xeque-mate.
        return true;
    }


    /**
     * Attempts to move a piece from one position to another.
     *
     * @param from Starting position in chess notation (e.g., "e2")
     * @param to Target position in chess notation (e.g., "e4")
     * @return MoveResult indicating the outcome of the move
     * @throws IllegalStateException if game hasn't been started
     */
    public MoveResult move(String from, String to) {
        if (board == null) {
            throw new IllegalStateException("O jogo ainda não foi iniciado. Por favor, inicie o jogo antes de realizar uma jogada.");
        }

        MoveResult outCome = MoveResult.VALID_MOVE;

        Player player = (whiteTurn ? white : black);
        Player enemy = (whiteTurn ? black : white);


        int fromRow = board.getBoardSize() - (from.charAt(1) - '0');
        int fromCol = from.charAt(0) - 'a';
        int toRow = board.getBoardSize() - (to.charAt(1) - '0');
        int toCol = to.charAt(0) - 'a';
        int result = -1;

        if (!new Spot(toRow, toCol).isValid(board.getBoardSize()) || !new Spot(fromRow, fromCol).isValid(board.getBoardSize())) {
            outCome = MoveResult.OUT_OF_BOUNDS;
        }

        Piece playerP = board.getPiece(fromRow, fromCol);
        Piece enemyP = board.getPiece(toRow, toCol);

        if (playerP == null || !player.getAlive().contains(playerP)) {
            outCome = MoveResult.INVALID_ORIGIN;
        }

        // En Passant
        if (playerP instanceof Pawn && enemyP == null && fromCol != toCol) {
            Spot[] lastMove = board.getLastMove();
            if (lastMove != null) {
                Spot start = lastMove[0];
                Spot end = lastMove[1];
                Piece capturedPawn = board.getPiece(end.getRow(), end.getCol());
                if (capturedPawn instanceof Pawn && Math.abs(end.getRow() - start.getRow()) == 2 && end.getCol() == start.getCol()) {
                    board.movePiece(new Spot(fromRow, fromCol), new Spot(toRow, toCol));
                    to = capturedPawn.toString();
                    board.remPieceBoard(end.getRow(), end.getCol());
                    enemy.kill(capturedPawn);

                    result = 2;
                    outCome = MoveResult.EN_PASSANT;
                }
            }
        }

        // Castling
        boolean castling = false;
        if (playerP instanceof King king && Math.abs(fromCol - toCol) == 2) {
            int rookCol = toCol > fromCol ? board.getBoardSize() - 1 : 0; // h1 or a1
            int rookTargetCol = toCol > fromCol ? toCol - 1 : toCol + 1; // f1 or d1

            Piece rook = board.getPiece(fromRow, rookCol);
            if (rook instanceof Rook r) {
                board.movePiece(new Spot(fromRow, rookCol), new Spot(fromRow, rookTargetCol));
                king.setIsCastling(true);
                r.setMoved(true); // Explicitly mark rook as moved
                castling = true;
            }
        }

        if(result == -1) {
            result = board.movePiece(new Spot(fromRow, fromCol), new Spot(toRow, toCol));
            if (result == 0) {
                outCome = MoveResult.INVALID_MOVE;
            }

            if (result == 2 && enemyP != null) {
                enemy.kill(enemyP);
            }
        }

        int promotionRow = playerP.isWhite() ? 0 : board.getBoardSize() - 1;
        if (playerP instanceof Pawn) {
            if (toRow == promotionRow) {
                pawnToPromote = playerP;
                outCome = MoveResult.PROMOTION;
            }
        }

        King king = null;
        for (Piece piece : player.getAlive()) {
            if (piece instanceof King) {
                king = (King) piece;
                break;
            }
        }


        if (castling) {
            outCome = MoveResult.CASTLING;
            ((King) playerP).setIsCastling(false);
        }


        if (outCome != MoveResult.EN_PASSANT && outCome != MoveResult.CASTLING) {
            if (result == 2 && toRow == promotionRow && playerP instanceof Pawn) {
                outCome = MoveResult.PROMOTION;
            } else if (result == 2) {
                outCome = MoveResult.CAPTURE;
            }
        }

        if (isCheckmate(false)) {
            outCome = MoveResult.CHECKMATEc;
        } else if (king != null && king.isInCheck(new ArrayList<>()) > 0) {
            outCome = MoveResult.CHECKMATEe;
        } else if (isStalemate(false)) {
            outCome = MoveResult.STALEMATE;
        }

        // Add comprehensive logging
        switch (outCome) {
            case INVALID_MOVE ->
                    ModelLog.getInstance().addLog("Invalid move: " + from + " to " + to);

            case INVALID_ORIGIN ->
                    ModelLog.getInstance().addLog("Invalid origin: " + from + " - No piece or wrong player");

            case OUT_OF_BOUNDS ->
                    ModelLog.getInstance().addLog("Out of bounds: " + from + " or " + to + " is outside board");

            case CAPTURE ->
                    ModelLog.getInstance().addLog("Capture: " + from + " takes " + to);

            case CASTLING ->
                    ModelLog.getInstance().addLog(getCurrentPlayer() + " castles " +
                    (to.charAt(0) > from.charAt(0) ? "kingside" : "queenside"));

            case VALID_MOVE ->
                    ModelLog.getInstance().addLog("Valid move: " + from + " to " + to);

            case CHECKMATEc -> {
                ModelLog.getInstance().addLog("CHECKMATE! " + getCurrentNamePlayer() + " wins");
                setGameState(GameState.CHECKMATEc);
                return outCome;
            }

            case CHECKMATEe -> {
                ModelLog.getInstance().addLog("CHECKMATE! " + getCurrentNameEnemy() + " wins");
                setGameState(GameState.CHECKMATEe);
                return outCome;
            }

            case STALEMATE -> {
                ModelLog.getInstance().addLog("STALEMATE! Game ends in draw");
                setGameState(GameState.STALEMATE);
                return outCome;
            }

            case EN_PASSANT ->
                    ModelLog.getInstance().addLog("En Passant: " + from + " takes " + to);
        }

        whiteTurn = !whiteTurn;

        return outCome;
    }


    /**
     * Attempts a move and returns whether it was successful.
     *
     * @param from Starting position in chess notation
     * @param to Target position in chess notation
     * @return true if move was valid, false otherwise
     */
    public boolean tryMove(String from, String to) {
        MoveResult result = move(from, to);
        return switch (result) {
            case VALID_MOVE, CAPTURE, PROMOTION, CASTLING -> true;
            default -> false;
        };
    }

    /**
     * Exports the current game state to a string.
     *
     * @return String representation of game state
     */
    public String exportGame() {
        StringBuilder sb = new StringBuilder();
        sb.append(whiteTurn ? "WHITE" : "BLACK").append(",\n");

        for (Piece piece : board.getPieces()) {
            if (piece.isAlive()) {
                sb.append(piece.toString());
                sb.append(", ");
            }
        }

        // Remove trailing comma if any
        if (sb.length() > 0 && sb.charAt(sb.length() - 2) == ',') {
            sb.delete(sb.length() - 2, sb.length());
        }

        return sb.toString();
    }

    /**
     * Imports a game state from a string.
     *
     * @param gameState String representation of game state
     */
    public void importGame(String gameState) {
        board = new Board();
        if (gameState == null || gameState.isEmpty()) {
            return;
        }

        String[] parts = gameState.split(",");
        if (parts.length < 1) return;

        whiteTurn = parts[0].trim().equalsIgnoreCase("WHITE");

        for (int i = 1; i < parts.length; i++) {
            String pieceStr = parts[i].trim();
            if (pieceStr.isEmpty()) continue;

            boolean hadAsterisk = pieceStr.endsWith("*");
            if (hadAsterisk) {
                pieceStr = pieceStr.substring(0, pieceStr.length() - 1);
            }

            boolean isWhite = Character.isUpperCase(pieceStr.charAt(0));
            Piece piece = PieceFactory.createPieceFromText(board, pieceStr);
            if (piece != null) {
                if (hadAsterisk) {
                    if (piece instanceof King k) { k.setMoved(false); }
                    else if (piece instanceof Rook r) { r.setMoved(false); }
                } else {
                    if (piece instanceof King k) { k.setMoved(true); }
                    else if (piece instanceof Rook r) { r.setMoved(true); }
                    else if (piece instanceof Pawn p) {
                        p.setHasMoved(true);
                    }
                }
                board.addPieceBoard(piece);
            }
        }

        this.gameState = GameState.RUNNING;
        updatePlayerPieces();
        if (isCheckmate(false)) {
            this.gameState = whiteTurn ? GameState.CHECKMATEc : GameState.CHECKMATEe;
            ModelLog.getInstance().addLog("CHECKMATE! " + (this.gameState == GameState.CHECKMATEc ? getCurrentNamePlayer() : getCurrentNameEnemy()) + " wins");
        } else if (isCheckmate(true)) {
            this.gameState = !whiteTurn ? GameState.CHECKMATEc : GameState.CHECKMATEe;
            ModelLog.getInstance().addLog("CHECKMATE! " + (this.gameState == GameState.CHECKMATEe ? getCurrentNameEnemy() : getCurrentNamePlayer()) + " wins");
        } else if (isStalemate(false) || isStalemate(true)) {
            this.gameState = GameState.STALEMATE;
            ModelLog.getInstance().addLog("STALEMATE! Game ends in draw");
        } else {
            this.gameState = GameState.RUNNING;
        }
    }

    /**
     * Updates both players' piece lists to match the current board.
     * Clears and refills the alive/dead pieces for white and black players.
     * White pieces go to white player, black pieces to black player.
     */
    private void updatePlayerPieces() {
        white.getAlive().clear();
        white.getDead().clear();
        black.getAlive().clear();
        black.getDead().clear();

        for (Piece p : board.getPieces()) {
            if (p.isWhite()) {
                if (p.isAlive()) {
                    white.addAlive(p);
                } else {
                    white.addDead(p);
                }
            } else {
                if (p.isAlive()) {
                    black.addAlive(p);
                } else {
                    black.addDead(p);
                }
            }
        }
    }

    /**
     * Sets the names for both players.
     *
     * @param whiteName Name for the white player
     * @param blackName Name for the black player
     * @throws IllegalArgumentException if either name is null or empty
     */
    public void setPlayerNames(String whiteName, String blackName) {
        if (whiteName == null || whiteName.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogador branco não pode ser nulo ou vazio.");
        }
        if (blackName == null || blackName.trim().isEmpty()) {
            throw new IllegalArgumentException("O nome do jogador preto não pode ser nulo ou vazio.");
        }

        if (white == null) {
            white = new Player(true, whiteName);
        } else {
            white.setName(whiteName);
        }

        if (black == null) {
            black = new Player(false, blackName);
        } else {
            black.setName(blackName);
        }

        this.whitePlayerName = whiteName;
        this.blackPlayerName = blackName;
    }

    /**
     * Resets both player names to null.
     */
    public void resetPlayerNames() {
        whitePlayerName = null;
        blackPlayerName = null;
    }

    /**
     * Gets the white player's name.
     *
     * @return White player's name
     */
    public String getWhitePlayerName() {
        return whitePlayerName;
    }

    /**
     * Gets the black player's name.
     *
     * @return Black player's name
     */
    public String getBlackPlayerName() {
        return blackPlayerName;
    }

    /**
     * Gets the size of the chess board.
     *
     * @return Board size (typically 8 for standard chess)
     */
    public int getBoardSize() {
        return board.getBoardSize();
    }

    /**
     * Promotes a pawn to the specified piece type.
     *
     * @param promotion Type of piece to promote to (e.g., "QUEEN")
     * @return Chess notation of the new piece's position
     */
    public String promotePawn(String promotion) {
        Piece promotedPiece = PieceFactory.PieceType.createPiece(board, PieceFactory.PieceType.valueOf(promotion.toUpperCase()), !whiteTurn, pawnToPromote.getRow(), pawnToPromote.getCol());
        board.remPieceBoard(pawnToPromote);
        board.addPieceBoard(promotedPiece);
        if (!whiteTurn) {
            white.remAlive(pawnToPromote);
            white.addAlive(promotedPiece);
        } else {
            black.remAlive(pawnToPromote);
            black.addAlive(promotedPiece);
        }

        return promotedPiece.convertToChessNotation(getBoardSize());
    }

    /**
     * Gets the piece at specified board coordinates.
     *
     * @param row Row index (0-based)
     * @param col Column index (0-based)
     * @return String representation of piece, or null if empty
     */
    public String getPieceAt(int row, int col) {
        if (board != null)
        {
            Piece piece = board.getPiece(row, col);
            return piece != null ? piece.toString() : null;
        }
        return null;
    }

    /**
     * Checks if the piece at given coordinates has any legal moves.
     *
     * @param row Row index (0-based)
     * @param col Column index (0-based)
     * @return true if piece has legal moves, false otherwise
     */
    public boolean hasMoves(int row, int col) {
        Piece piece = board.getPiece(row, col);
        piece.calculateLegalMoves();
        return piece.hasMoves();
    }

    /**
     * Checks if a piece can move from start to target position.
     *
     * @param start Starting position
     * @param row Target row index
     * @param col Target column index
     * @return true if move is legal, false otherwise
     */
    public boolean hasMove(Spot start, int row, int col) {
        Piece cPiece = board.getPiece(start.getRow(), start.getCol());
        return cPiece.getLegalMoves().contains(new Spot(row, col));
    }

    /**
     * Gets a string listing all legal moves for a piece.
     *
     * @param row Piece's row index
     * @param col Piece's column index
     * @return String listing legal moves in chess notation
     */
    public String getLegalMovesString(int row, int col) {
        Piece piece = board.getPiece(row, col);
        if (piece == null) return "No piece at this position.";

        piece.calculateLegalMoves();
        StringBuilder sb = new StringBuilder();
        sb.append(piece.toString()).append(" legal moves: ");

        for (Spot move : piece.getLegalMoves()) {
            sb.append(move.convertToChessNotation(board.getBoardSize())).append(" ");
        }
        return sb.toString().trim();
    }

    /**
     * Gets the current board object.
     *
     * @return Current Board instance
     */
    public Board getBoard() { return board; }

    /**
     * Switches turn to the other player.
     */
    public void switchTurn() {
        whiteTurn = !whiteTurn;
    }

    /**
     * Enum representing possible move outcomes in the chess game.
     */
    public enum MoveResult {
        /** The move is invalid according to chess rules */
        INVALID_MOVE,
        /** The origin square contains no piece or opponent's piece */
        INVALID_ORIGIN,
        /** The move attempts to go outside board boundaries */
        OUT_OF_BOUNDS,
        /** The move captures an opponent's piece */
        CAPTURE,
        /** A pawn has reached promotion rank */
        PROMOTION,
        /** The move is a castling move */
        CASTLING,
        /** The move is an en passant capture */
        EN_PASSANT,
        /** The move is valid but doesn't capture anything */
        VALID_MOVE,
        /** The move results in checkmate for current player */
        CHECKMATEc,
        /** The move results in checkmate for enemy player */
        CHECKMATEe,
        /** The game ends in stalemate */
        STALEMATE
    }

    /**
     * Enum representing the overall state of the chess game.
     */
    public enum GameState {
        /** The game has not started yet */
        NOT_STARTED,
        /** The game is currently running */
        RUNNING,
        /** The game ended in checkmate for current player */
        CHECKMATEc,
        /** The game ended in checkmate for enemy player */
        CHECKMATEe,
        /** The game ended in stalemate (draw) */
        STALEMATE,
    }


}
