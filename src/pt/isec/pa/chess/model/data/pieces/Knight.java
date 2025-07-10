package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.Spot;

import java.util.List;

public class Knight extends Piece {
    public Knight(Board board, boolean isWhite, int row, int col){
        super(board, isWhite, row, col, PieceFactory.PieceType.KNIGHT);
    }


    @Override
    public void calculateLegalMoves() {
        clearLegalMoves();

        // knight - L shaped moves
        int[][] knightMoves = {
                {2, 1}, {2, -1},    // Down-right and down-left
                {-2, 1}, {-2, -1},  // Up-right and up-left
                {1, 2}, {1, -2},     // Right-up and right-down
                {-1, 2}, {-1, -2}    // Left-up and left-down
        };

        for (int[] move : knightMoves) {
            int newRow = getRow() + move[0];
            int newCol = getCol() + move[1];
            Spot potentialMove = new Spot(newRow, newCol);

            if (potentialMove.isValid(board.getBoardSize())) {
                Piece pieceAtDestination = board.getPiece(newRow, newCol);

                if ((pieceAtDestination == null || isEnemy(pieceAtDestination)) && isItReallyLegal(potentialMove)) {
                    addLegalMove(potentialMove);
                }
            }
        }
    }

    @Override
    public boolean hasMoved() {
        return false;
    }

    @Override
    public int isInCheck(List<Spot> spots) {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s%c%d",
                isWhite() ? "N" : "n",
                'a' + getCol(), // Column (a-h)
                board.getBoardSize() - getRow());   // Row (1-8) - consedering 8 at the top, 1 at the bottom
    }

    @Override
    public Knight clone(Board board) {
        return new Knight(board, this.isWhite(), this.getRow(), this.getCol());
    }
}
