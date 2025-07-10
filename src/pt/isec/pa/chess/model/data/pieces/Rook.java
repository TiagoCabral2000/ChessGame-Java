package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.Spot;

import java.util.List;


//A rook can move any number of squares in a straight line,
//as long as it's not blocked by another piece

public class Rook extends Piece {
    private boolean moved; //"An asterisk (*) denotes that the King and/or Rook have not moved"

    public Rook(Board board,boolean isWhite, int row, int col){
        super(board, isWhite, row, col, PieceFactory.PieceType.ROOK);
        moved = false;
    }

    @Override
    public void calculateLegalMoves() {
        clearLegalMoves(); // Clear previous legal moves

        // Rook - 4 straight directions (up, down, left, right)
        int[][] directions = {{1, 0}, {-1, 0}, {0, 1}, {0, -1}};

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

                // Continue in this direction
                row += direction[0];
                col += direction[1];
            }
        }
    }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    @Override
    public int isInCheck(List<Spot> spots) {
        return 0;
    }

    @Override
    public String toString() {
        return String.format("%s%c%d%s",
                isWhite() ? "R" : "r",
                'a' + getCol(), // Column (a-h)
                board.getBoardSize() - getRow(),   // Row (1-8) - consedering 8 at the top, 1 at the bottom
                moved ? "" : "*");
    }

    @Override
    public Rook clone(Board board) {
        Rook clone = new Rook(board, this.isWhite(), this.getRow(), this.getCol());
        clone.moved = this.moved;
        return clone;
    }
}
