package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.pieces.*;

public class PieceFactory {
    public enum PieceType {
        KING, QUEEN, BISHOP, KNIGHT, ROOK, PAWN;

        public static Piece createPiece(Board board, PieceType type, boolean isWhite, int row, int col) {
            return switch (type) {
                case KING -> new King(board, isWhite, row, col);
                case QUEEN -> new Queen(board, isWhite, row, col);
                case BISHOP -> new Bishop(board, isWhite, row, col);
                case KNIGHT -> new Knight(board, isWhite, row, col);
                case ROOK -> new Rook(board, isWhite, row, col);
                case PAWN -> new Pawn(board, isWhite, row, col);
            };
        }
    }

    public static Piece createPieceFromText(Board board, String text) {
        if (text == null || text.length() != 3)
            return null;

        // Formato: Kd1 / ba4 / etc.
        char typeChar = text.charAt(0);
        char colChar = text.charAt(1);
        char rowChar = text.charAt(2);

        boolean isWhite = Character.isUpperCase(typeChar);
        char typeCharUpper = Character.toUpperCase(typeChar);

        PieceType type = switch (typeCharUpper) {
            case 'K' -> PieceType.KING;
            case 'Q' -> PieceType.QUEEN;
            case 'B' -> PieceType.BISHOP;
            case 'N' -> PieceType.KNIGHT;
            case 'R' -> PieceType.ROOK;
            case 'P' -> PieceType.PAWN;
            default -> null;
        };

        int col = colChar - 'a';
        int row = board.getBoardSize() - (rowChar - '0');

        if (type == null)
            return null;

        return PieceType.createPiece(board, type, isWhite, row, col);
    }
}
