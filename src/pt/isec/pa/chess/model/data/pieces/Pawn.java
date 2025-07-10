package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.Spot;

import java.util.List;

public class Pawn extends Piece {
    private boolean hasMoved; //the pawn can advance 2 tiles if its is first move


    public Pawn(Board board, boolean isWhite, int row, int col) {
        super(board,isWhite, row, col, PieceFactory.PieceType.PAWN);
        hasMoved = false;
    }

    public boolean hasMoved() {return hasMoved; }

    public void setHasMoved(boolean hasMoved) {
        this.hasMoved = hasMoved;
    }

    @Override
    public int isInCheck(List<Spot> spots) {
        return 0;
    }

    @Override
    public void calculateLegalMoves() {
        clearLegalMoves();
        int direction = isWhite() ? -1 : 1; // White moves up (-1), Black moves down (+1)
        int currentRow = getRow();
        int currentCol = getCol();

        // Standard one-square move forward
        Spot oneForward = new Spot(currentRow + direction, currentCol);
        if (oneForward.isValid(board.getBoardSize()) && board.isEmpty(oneForward.getRow(), oneForward.getCol()) && isItReallyLegal(oneForward)) {
            addLegalMove(oneForward);

            // Can move two-square move from starting position
            if (!hasMoved) {
                Spot twoForward = new Spot(currentRow + 2 * direction, currentCol);
                if (twoForward.isValid(board.getBoardSize()) && board.isEmpty(twoForward.getRow(), twoForward.getCol()) && isItReallyLegal(twoForward)) {
                    addLegalMove(twoForward);
                }
            }
        }

        // Diagonal captures
        int[] captureCols = {currentCol - 1, currentCol + 1};
        for (int col : captureCols) {
            if (col >= 0 && col < board.getBoardSize()) {
                Spot captureSpot = new Spot(currentRow + direction, col);
                if (captureSpot.isValid(board.getBoardSize())) {
                    Piece target = board.getPiece(captureSpot.getRow(), captureSpot.getCol());
                    if (target != null && isEnemy(target) && isItReallyLegal(captureSpot)) {
                        addLegalMove(captureSpot);
                    }
                }
            }
        }


        //En Passant
        Spot[] lastMove = board.getLastMove();
        if (lastMove != null) {
            Spot start = lastMove[0];
            Spot end = lastMove[1];
            Piece movedPiece = board.getPiece(end.getRow(), end.getCol());

            if (movedPiece instanceof Pawn && Math.abs(start.getRow() - end.getRow()) == 2 && start.getCol() == end.getCol()) {
                int enPassantRow = currentRow + direction;
                if (end.getRow() == currentRow && Math.abs(end.getCol() - currentCol) == 1) {
                    Spot enPassantSpot = new Spot(enPassantRow, end.getCol());
                    if(isItReallyLegal(enPassantSpot))
                        addLegalMove(enPassantSpot);
                }
            }
        }
    }


    @Override
    public String toString() {
        return String.format("%s%c%d",
                isWhite() ? "P" : "p",
                'a' + getCol(), // Column (a-h)
                board.getBoardSize() - getRow());  // Row (1-8) - consedering 8 at the top, 1 at the bottom
    }

    @Override
    public Pawn clone(Board board) {
        Pawn clone = new Pawn(board, this.isWhite(), this.getRow(), this.getCol());
        clone.setHasMoved(this.hasMoved());
        return clone;
    }
}
