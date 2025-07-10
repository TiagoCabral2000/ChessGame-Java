package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.Spot;

import java.util.List;

//A bishop can move diagonally as many squares as it wants,
//as long as it's not blocked by another piece
public class Bishop extends Piece {

    public Bishop(Board board, boolean isWhite, int row, int col){
        super(board, isWhite, row, col, PieceFactory.PieceType.BISHOP);
    }


    @Override
    public void calculateLegalMoves() {
        clearLegalMoves();

        // Bishop - diagonal directions
        int[][] directions = {{1, 1}, {1, -1}, {-1, 1}, {-1, -1}};

        for (int[] direction : directions) {
            int row = getRow() + direction[0];
            int col = getCol() + direction[1];

            while (row >= 0 && row < board.getBoardSize() && col >= 0 && col < board.getBoardSize()) {
                Spot potentialMove = new Spot(row, col);
                Piece pieceAtDestination = board.getPiece(row, col);

                if (pieceAtDestination == null) {
                    if(isItReallyLegal(potentialMove))
                        addLegalMove(potentialMove);
                    else
                        break;
                } else {
                    if (isEnemy(pieceAtDestination)) {
                        if(isItReallyLegal(potentialMove))
                            addLegalMove(potentialMove);
                        else
                            break;
                    }
                    break;
                }

                // Continue in this diagonal direction
                row += direction[0];
                col += direction[1];
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
                isWhite() ? "B" : "b",
                'a' + getCol(), // Column (a-h)
                board.getBoardSize() - getRow() ); // Row (1-8) - consedering 8 at the top, 1 at the bottom
    }

    @Override
    public Bishop clone(Board board) {
        return new Bishop(board, this.isWhite(), this.getRow(), this.getCol());
    }

}
