package pt.isec.pa.chess.model.data.pieces;

import pt.isec.pa.chess.model.data.Board;
import pt.isec.pa.chess.model.data.Piece;
import pt.isec.pa.chess.model.data.PieceFactory;
import pt.isec.pa.chess.model.data.Spot;

import java.util.ArrayList;
import java.util.List;

public class King extends Piece {
    private boolean moved; //"An asterisk (*) denotes that the King and/or Rook have not moved"
    private boolean isCastling;

    public King(Board board, boolean isWhite, int row, int col){
        super(board, isWhite, row, col, PieceFactory.PieceType.KING);
        moved = false;
        isCastling = false;
    }

    @Override
    public void calculateLegalMoves() {
        clearLegalMoves();

        // All 8 possible directions (1 square)
        int[][] directions = {
                {1, 0}, {-1, 0}, {0, 1}, {0, -1},  // Straight
                {1, 1}, {1, -1}, {-1, 1}, {-1, -1} // Diagonal
        };

        for (int[] direction : directions) {
            int newRow = getRow() + direction[0];
            int newCol = getCol() + direction[1];
            Spot potentialMove = new Spot(newRow, newCol);

            if (potentialMove.isValid(board.getBoardSize())) {
                Piece pieceAtDestination = board.getPiece(newRow, newCol);

                if ((pieceAtDestination == null || isEnemy(pieceAtDestination)) && isItReallyLegal(potentialMove)) {
                    addLegalMove(potentialMove);
                }
            }
        }

        // Verificar roque
        if (!hasMoved()) {
            // Roque do lado do rei
            if (canCastle(7)) {
                addLegalMove(new Spot(getRow(), getCol() + 2));
            }
            // Roque do lado da dama
            if (canCastle(0)) {
                addLegalMove(new Spot(getRow(), getCol() - 2));
            }
        }

        // Castling logic
        if (!hasMoved() && isCastling) {
            Spot[] lastMove = board.getLastMove();
            if (lastMove != null) {
                Spot rookStart = lastMove[0];
                Spot rookEnd = lastMove[1];

                // Verify the moved piece is a Rook and moved 2 or 3 squares horizontally
                Piece rookPiece = board.getPiece(rookEnd.getRow(), rookEnd.getCol());
                if (rookPiece instanceof Rook
                        && rookStart.getRow() == rookEnd.getRow()
                        && (Math.abs(rookStart.getCol() - rookEnd.getCol()) == 2
                        || Math.abs(rookStart.getCol() - rookEnd.getCol()) == 3)) {

                    // Calculate the King's target position
                    int targetCol = (rookEnd.getCol() > getCol()) ? getCol() + 2 : getCol() - 2;
                    addLegalMove(new Spot(getRow(), targetCol));
                }
            }
        }
    }

    private boolean canCastle(int rookCol) {
        if (isCastling) return false;
        // 1. Check if King/Rook have moved
        if (hasMoved() || board.getPiece(getRow(), rookCol) == null ||
                !(board.getPiece(getRow(), rookCol) instanceof Rook rook) || rook.hasMoved()) {
            return false;
        }

        // 2. Check if squares between are empty
        int step = rookCol > getCol() ? 1 : -1;
        for (int col = getCol() + step; col != rookCol; col += step) {
            if (!board.isEmpty(getRow(), col)) {
                return false;
            }
        }

        // 3. Check if king is currently in check
        if (isInCheck(new ArrayList<>()) > 0) {
            return false;
        }

        // 4. Check if squares the king moves through are under attack
        int kingEndCol = getCol() + 2 * step;

        for (int col = getCol(); col != kingEndCol + step; col += step) {
            if (col == getCol()) continue; // Skip starting position

            // Temporarily move the king to the new column
            int originalCol = getCol();
            setCol(col); // Directly update the king's position

            // Check if the king is in check at the new position
            if (isInCheck(new ArrayList<>()) > 0) {
                setCol(originalCol); // Restore original position
                return false;
            }

            setCol(originalCol); // Restore original position
        }

        return true;
    }

    public int isInCheck(List<Spot> spots) {
        System.out.println("[DEBUG] isInCheck called for King at (" + getRow() + ", " + getCol() + ")");
        spots.clear();
        int count = 0;
        int[][] directions = {
                {-1, -1}, {-1, 0}, {-1, 1},
                { 0, -1}     ,     { 0, 1},
                { 1, -1}, { 1, 0}, { 1, 1}
        };

        PieceFactory.PieceType[][] pieceRelations = {
                {PieceFactory.PieceType.BISHOP, PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.PAWN}, {PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.ROOK}, {PieceFactory.PieceType.BISHOP, PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.PAWN},
                {PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.ROOK}                              ,                        {PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.ROOK},
                {PieceFactory.PieceType.BISHOP, PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.PAWN}, {PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.ROOK}, {PieceFactory.PieceType.BISHOP, PieceFactory.PieceType.QUEEN, PieceFactory.PieceType.PAWN}
        };

        for (int i = 0; i < directions.length; i++) {
            List<Spot> spotsTemp = new ArrayList<Spot>();
            int[] direction = directions[i];
            int row = getRow() + direction[0];
            int col = getCol() + direction[1];

            while (row >= 0 && row < board.getBoardSize() && col >= 0 && col < board.getBoardSize()) {
                Piece pieceAtDestination = board.getPiece(row, col);
                spotsTemp.add(new Spot(row, col));

                if (pieceAtDestination != null) {
                    if (isEnemy(pieceAtDestination)) {
                        PieceFactory.PieceType pieceType = pieceAtDestination.getType();
                        System.out.println("[DEBUG] Enemy piece found at (" + row + ", " + col + ") of type " + pieceType);

                        for (PieceFactory.PieceType pt : pieceRelations[i]) {
                            if (pieceType == pt) {
                                int rowDiff = row - getRow(); // direção do peão relativo ao rei
                                int colDiff = Math.abs(col - getCol());
                                boolean pawnDirectionMatches =
                                        (isWhite() && rowDiff == 1) || (!isWhite() && rowDiff == -1); // peões inimigos movemse na direção oposta
                                if (pieceType == PieceFactory.PieceType.PAWN && pawnDirectionMatches && colDiff == 1) {
                                    System.out.println("[DEBUG] King is in check by PAWN at (" + row + ", " + col + ")");
                                    count++;
                                    spots.addAll(spotsTemp);
                                    break;
                                }
                                if(pieceType != PieceFactory.PieceType.PAWN)
                                {
                                    System.out.println("[DEBUG] King is in check by " + pieceType + " at (" + row + ", " + col + ")");
                                    count++;
                                    spots.addAll(spotsTemp);
                                    break;
                                }
                            }
                        }
                    }
                    // Pára independentemente de ser amigo ou inimigo
                    break;
                }

                row += direction[0];
                col += direction[1];
            }

        }

        // Check for Knight threats
        int[][] knightMoves = {
                {2, 1}, {2, -1}, {-2, 1}, {-2, -1},
                {1, 2}, {1, -2}, {-1, 2}, {-1, -2}
        };
        for (int[] move : knightMoves) {
            int newRow = getRow() + move[0];
            int newCol = getCol() + move[1];
            Spot potentialMove = new Spot(newRow, newCol);

            if (potentialMove.isValid(board.getBoardSize())) {
                Piece pieceAtDestination = board.getPiece(newRow, newCol);
                if (pieceAtDestination != null && isEnemy(pieceAtDestination) && pieceAtDestination.getType() == PieceFactory.PieceType.KNIGHT) {
                    System.out.println("[DEBUG] King is in check by KNIGHT at (" + newRow + ", " + newCol + ")");
                    count++;
                    spots.add(new Spot(newRow, newCol));
                }
            }
        }

        // Verificar se o Rei inimigo está adjacente
        int[] kingPossibleMoves = {-1,-1, -1,0, -1,1, 0,-1, 0,1, 1,-1, 1,0, 1,1}; // 8 direções
        for (int i = 0; i < kingPossibleMoves.length; i += 2) {
            int r = getRow() + kingPossibleMoves[i];
            int c = getCol() + kingPossibleMoves[i+1];

            if (r >= 0 && r < board.getBoardSize() && c >= 0 && c < board.getBoardSize()) {
                Piece p = board.getPiece(r, c);
                if (p instanceof King && isEnemy(p)) {
                    System.out.println("[DEBUG] King at (" + getRow() + "," + getCol() + ") is adjacent to enemy King at (" + r + "," + c + ") - this square is attacked.");
                    count++; // Considera esta casa como atacada pelo rei inimigo
                    spots.add(new Spot(r,c)); // Adiciona a posição do rei inimigo como "ameaça" à casa
                    // Não precisa de 'break' aqui se quiser contar todas as ameaças, mas para esta situação, um é suficiente.
                }
            }
        }

        System.out.println("[DEBUG] isInCheck finished for King at (" + getRow() + ", " + getCol() + ") with count = " + count);
        return count;
    }

    public boolean hasMoved() {
        return moved;
    }

    public void setMoved(boolean moved) {
        this.moved = moved;
    }

    public void setIsCastling(boolean isCastling) {
        this.isCastling = isCastling;
    }

    @Override
    public String toString() {
        return String.format("%s%c%d%s",
                isWhite() ? "K" : "k",
                'a' + getCol(), // Column (a-h)
                board.getBoardSize() - getRow(), // Row (1-8) - consedering 8 at the top, 1 at the bottom
                moved ? "" : "*");
    }

    @Override
    public King clone(Board board) {
        King clone = new King(board, this.isWhite(), this.getRow(), this.getCol());
        clone.moved = this.moved;
        return clone;
    }
}
