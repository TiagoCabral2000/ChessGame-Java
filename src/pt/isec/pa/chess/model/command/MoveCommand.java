package pt.isec.pa.chess.model.command;

import pt.isec.pa.chess.model.ChessGame;

import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.Spot;

public class MoveCommand extends AbstractCommand implements ICommand {
    private final String from;
    private final String to;
    private Piece capturedPiece;
    private boolean pawnHadMoved;
    private ChessGame.MoveResult moveResult;
    private ChessGame.GameState gameState;
    private boolean wasCastling;
    private String rookFrom, rookTo;
    private Piece rookPiece;
    private boolean wasEnPassant;
    private Spot enPassantCapturedSpot;
    private Piece enPassantCapturedPiece;
    private boolean wasPromotion;
    private Piece promotedPiece;
    private Pawn originalPawn;
    private int promotionRow, promotionCol;

    public MoveCommand(ChessGame receiver, String from, String to) {
        super(receiver);
        this.from = from;
        this.to = to;
    }

    @Override
    public boolean execute() {
        int fromRow = receiver.getBoardSize() - (from.charAt(1) - '0');
        int fromCol = from.charAt(0) - 'a';
        int toRow = receiver.getBoardSize() - (to.charAt(1) - '0');
        int toCol = to.charAt(0) - 'a';

        // redo of castling
        if (wasCastling && moveResult == ChessGame.MoveResult.CASTLING) {
            // Move the king
            Piece king = receiver.getBoard().getPiece(fromRow, fromCol);
            receiver.getBoard().remPieceBoard(fromRow, fromCol);
            receiver.getBoard().addPieceBoard(king, toRow, toCol);

            // Move the rook
            int rookFromRow = receiver.getBoardSize() - (rookFrom.charAt(1) - '0');
            int rookFromCol = rookFrom.charAt(0) - 'a';
            int rookToRow = receiver.getBoardSize() - (rookTo.charAt(1) - '0');
            int rookToCol = rookTo.charAt(0) - 'a';

            Piece rook = receiver.getBoard().getPiece(rookFromRow, rookFromCol);
            receiver.getBoard().remPieceBoard(rookFromRow, rookFromCol);
            receiver.getBoard().addPieceBoard(rook, rookToRow, rookToCol);

            receiver.switchTurn();
            return true;
        }

        // Store original piece
        Piece piece = receiver.getBoard().getPiece(fromRow, fromCol);

        // Store original pawn state
        if (piece instanceof Pawn pawn) {
            this.pawnHadMoved = pawn.hasMoved();
            this.originalPawn = new Pawn(receiver.getBoard(), pawn.isWhite(), pawn.getRow(), pawn.getCol());
            this.originalPawn.setHasMoved(pawn.hasMoved());

            // Check for possible en passant before the move
            if (fromCol != toCol && receiver.getBoard().getPiece(toRow, toCol) == null) {
                wasEnPassant = true;
                int capturedRow = fromRow;
                int capturedCol = toCol;
                enPassantCapturedSpot = new Spot(capturedRow, capturedCol);
                enPassantCapturedPiece = receiver.getBoard().getPiece(capturedRow, capturedCol);
            }
        }

        //Store game state
        this.gameState = receiver.getGameState();

        // Store captured piece
        this.capturedPiece = receiver.getBoard().getPiece(toRow, toCol);

        // Execute and validate move
        this.moveResult = receiver.move(from, to);

        // Confirm en passant only if move result matches
        if (wasEnPassant && moveResult != ChessGame.MoveResult.EN_PASSANT) {
            wasEnPassant = false;
        }

        // Check for special moves
        if (moveResult == ChessGame.MoveResult.CASTLING) {
            wasCastling = true;
            if (to.charAt(0) == 'g') { // Kingside
                rookFrom = "h" + from.charAt(1);
                rookTo = "f" + from.charAt(1);
            } else if (to.charAt(0) == 'c') { // Queenside
                rookFrom = "a" + from.charAt(1);
                rookTo = "d" + from.charAt(1);
            }
            // Store the rook piece
            int rookToRow = receiver.getBoardSize() - (rookTo.charAt(1) - '0');
            int rookToCol = rookTo.charAt(0) - 'a';
            rookPiece = receiver.getBoard().getPiece(rookToRow, rookToCol);
        }

        if (moveResult == ChessGame.MoveResult.PROMOTION) {
            wasPromotion = true;
            promotionRow = toRow;
            promotionCol = toCol;
            promotedPiece = receiver.getBoard().getPiece(toRow, toCol);
        }

        return moveResult != ChessGame.MoveResult.INVALID_MOVE &&
                moveResult != ChessGame.MoveResult.INVALID_ORIGIN &&
                moveResult != ChessGame.MoveResult.OUT_OF_BOUNDS;
    }

    @Override
    public boolean undo() {
        if (moveResult == null) return false;
        receiver.setGameState(gameState);

        int fromRow = receiver.getBoardSize() - (from.charAt(1) - '0');
        int fromCol = from.charAt(0) - 'a';
        int toRow = receiver.getBoardSize() - (to.charAt(1) - '0');
        int toCol = to.charAt(0) - 'a';

        if (wasPromotion && originalPawn != null) {
            // Handle promotion undo
            receiver.getBoard().remPieceBoard(toRow, toCol);

            Pawn restoredPawn = new Pawn(receiver.getBoard(), originalPawn.isWhite(), fromRow, fromCol);
            restoredPawn.setHasMoved(pawnHadMoved);
            receiver.getBoard().addPieceBoard(restoredPawn, fromRow, fromCol);

            if (capturedPiece != null) {
                receiver.getBoard().addPieceBoard(capturedPiece, toRow, toCol);
            }
        }
        else if (wasCastling) {
            // 1st, undo king move
            Spot kingStart = new Spot(fromRow, fromCol);
            Spot kingEnd = new Spot(toRow, toCol);
            boolean kingSuccess = receiver.getBoard().undoMove(kingStart, kingEnd, null, false);
            if (!kingSuccess) return false;

            // 2nd undo rook move
            if (rookFrom != null && rookTo != null && rookPiece != null) {
                int rookFromRow = receiver.getBoardSize() - (rookFrom.charAt(1) - '0');
                int rookFromCol = rookFrom.charAt(0) - 'a';
                int rookToRow = receiver.getBoardSize() - (rookTo.charAt(1) - '0');
                int rookToCol = rookTo.charAt(0) - 'a';

                // Remove rook from castling position
                receiver.getBoard().remPieceBoard(rookToRow, rookToCol);
                // Add rook back to original position
                receiver.getBoard().addPieceBoard(rookPiece, rookFromRow, rookFromCol);
                // Reset rook's moved status
                rookPiece.setMoved(false);


                Piece king = receiver.getBoard().getPiece(fromRow, fromCol);
                if (king != null) {
                    king.setMoved(false);
                }
            }
        }
        else {
            // normal moves
            Spot start = new Spot(fromRow, fromCol);
            Spot end = new Spot(toRow, toCol);
            boolean success = receiver.getBoard().undoMove(start, end, capturedPiece, pawnHadMoved);
            if (!success) return false;
        }

        if (wasEnPassant) {
            if (enPassantCapturedSpot != null && enPassantCapturedPiece != null) {
                receiver.getBoard().addPieceBoard(
                        enPassantCapturedPiece,
                        enPassantCapturedSpot.getRow(),
                        enPassantCapturedSpot.getCol()
                );
            }
        }

        receiver.switchTurn();
        return true;
    }


    public ChessGame.MoveResult getMoveResult() {
        return moveResult;
    }
}
