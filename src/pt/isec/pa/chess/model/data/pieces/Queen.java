package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.Spot;

import java.util.List;

public class Queen extends Piece {
    public Queen(Board board, boolean isWhite, int row, int col){
        super(board, isWhite, row, col, PieceFactory.PieceType.QUEEN);
    }

    @Override
    public void calculateLegalMoves() {
        clearLegalMoves();

        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                {0, -1}      ,     {0, 1},
                {1, -1}, {1, 0}, {1, 1}
        };

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
                isWhite() ? "Q" : "q",
                'a' + getCol(), // Column (a-h)
                board.getBoardSize() - getRow());  // Row (1-8) - consedering 8 at the top, 1 at the bottom
    }

    @Override
    public Queen clone(Board board) {
        return new Queen(board, this.isWhite(), this.getRow(), this.getCol());
    }
}
