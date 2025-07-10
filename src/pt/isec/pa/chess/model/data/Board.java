package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.pieces.King;
import pt.isec.pa.chess.model.data.pieces.Pawn;
import pt.isec.pa.chess.model.data.pieces.Rook;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Board implements Cloneable, Serializable {
    //Incrementar este valor se forem feitas alterações a classe
    //Exemplo 1L -> 2L
    private static final long serialVersionUID = 3L;
    private static final int BOARDSIZE = 8;

    private Piece[][] board;
    private List<Piece> pieces;
    private Spot[] lastMove; // Armazena o movimento anterior [start, end]



    public Board() {
        pieces = new ArrayList<Piece>();
        board = new Piece[BOARDSIZE][BOARDSIZE];

    }

    public void initializer() {
        // Clear board
        pieces.clear();
        for (int i = 0; i < BOARDSIZE; i++) {
            for (int j = 0; j < BOARDSIZE; j++) {
                board[i][j] = null;
            }
        }

        // Pawns
        for (int col = 0; col < BOARDSIZE; col++) {
            addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.PAWN, true, 6, col));   // White pawns
            addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.PAWN, false, 1, col));  // Black pawns
        }

        // White pieces
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.ROOK, true, 7, 0));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.KNIGHT, true, 7, 1));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.BISHOP, true, 7, 2));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.QUEEN, true, 7, 3));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.KING, true, 7, 4));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.BISHOP, true, 7, 5));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.KNIGHT, true, 7, 6));
        addPieceBoard(PieceFactory.PieceType.createPiece(this, PieceFactory.PieceType.ROOK, true, 7, 7));

        // Black pieces
        addPieceBoard(PieceFactory.createPieceFromText(this, "ra8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "nb8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "bc8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "qd8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "ke8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "bf8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "ng8"));
        addPieceBoard(PieceFactory.createPieceFromText(this, "rh8"));
    }

    public List<Piece> getPieces() {return pieces; }

    public Piece getPiece(int row, int col) {
        return board[row][col];
    }
    public Piece getPiece(String tag) {
        if(tag.length() >= 3)
        {
            int row = BOARDSIZE - (tag.charAt(2) - '0');
            int col = tag.charAt(1) - 'a';
            if(new Spot(row, col).isValid(BOARDSIZE))
                return board[row][col];
        }
        return null;
    }

    public boolean isEmpty(int row, int col) {
        return getPiece(row, col) == null;
    }

    public boolean addPieceBoard(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();

        if(row == -1 || col == -1)
        {
            return false;
        }

        if(isEmpty(row, col)) {
            addPiece(piece);
            board[row][col] = piece;
            return true;
        }
        return false;
    }

    public boolean addPieceBoard(Piece piece, int row, int col) {
        if (row == -1 || col == -1)
            return false;

        if (isEmpty(row, col)) {
            piece.setPosition(row, col);
            board[row][col] = piece;
            return true;
        }
        return false;
    }

    public boolean addPiece(Piece piece) {
        if(!pieces.contains(piece)) {
            pieces.add(piece);
            return true;
        }
        return false;
    }

    public boolean remPieceBoard(Piece piece) {
        int row = piece.getRow();
        int col = piece.getCol();

        if (piece != getPiece(row, col) || col == -1 || row == -1)
        {
            return false;
        }
        board[row][col] = null;
        piece.setPosition(-1, -1);
        return true;
    }

    public boolean remPieceBoard(int row, int col) {
        Piece temp = getPiece(row, col);
        if(temp == null)
        {
            return false;
        }
        board[row][col] = null;
        temp.setPosition(-1, -1);
        return true;
    }

    public boolean remPiece(Piece piece) {
        if(pieces.contains(piece)) {
            pieces.remove(piece);
            if(getPiece(piece.getRow(), piece.getCol()) == piece)
            {
                board[piece.getRow()][piece.getCol()] = null;
            }
            return true;
        }
        return false;
    }

    public int movePiece(Spot start, Spot end) {
        //return 0;     -> if can't move
        //return 1;     -> if can move
        //return 2;     -> if can move and kills
        int returnV = 0;
        if(!end.isValid(BOARDSIZE))
        {
            System.out.println("Invalid spot");
            return returnV;
        }

        Piece piece = board[start.getRow()][start.getCol()];
        if (piece == null)
        {
            System.out.println("No piece");
            return returnV;
        }

        if (!piece.canMove(end))
        {
            System.out.println("Cant move there");
            return returnV;
        }

        Piece capturedPiece = board[end.getRow()][end.getCol()];
        //If the final position doesn't have a piece I don't need to capture one
        if (capturedPiece != null)
        {
            remPieceBoard(end.getRow(), end.getCol());
            capturedPiece.setAlive(false);
            returnV = 2;
        } else {
            returnV = 1;
        }

        board[end.getRow()][end.getCol()] = piece;
        board[start.getRow()][start.getCol()] = null;
        piece.setPosition(end.getRow(), end.getCol());



        setLastMove(start, end);

        // After moving the piece:
        switch (piece) {
            case King king -> king.setMoved(true); // Mark king as moved
            case Rook rook -> rook.setMoved(true); // Mark rook as moved
            case Pawn pawn -> pawn.setHasMoved(true);
            default -> {
            }
        }

        return returnV;
    }

    void forceMove(Piece pieceToMove, Spot targetSpot) {
        if (pieceToMove == null || !targetSpot.isValid(getBoardSize())) {
            return;
        }

        Spot originalSpot = new Spot(pieceToMove.getRow(), pieceToMove.getCol());
        Piece capturedPiece = getPiece(targetSpot.getRow(), targetSpot.getCol());

        this.board[originalSpot.getRow()][originalSpot.getCol()] = null;

        if (capturedPiece != null) {
            capturedPiece.setAlive(false);
        }

        this.board[targetSpot.getRow()][targetSpot.getCol()] = pieceToMove;
        pieceToMove.setPosition(targetSpot.getRow(), targetSpot.getCol());

        switch (pieceToMove) {
            case Pawn pawn -> pawn.setHasMoved(true);
            case King king -> king.setMoved(true);
            case Rook rook -> rook.setMoved(true);
            default -> {
            }
        }

    }

    @Override
    public String toString() {
        StringBuilder output = new StringBuilder();
        //output.append(String.format("\t\t\t --- Board ---\n"));
        for (int row = 0; row < BOARDSIZE ; row++) {
            for (int col = 0; col < BOARDSIZE ; col++) {
                String content;
                if (isEmpty(row, col)) {
                    content = String.format("%c%d", 'a' + col, BOARDSIZE - row); // e.g., e4
                } else {
                    content = getPiece(row, col).toString(); // e.g., Qd1
                }
                output.append(String.format("%-6s", content));

                if (col == BOARDSIZE - 1) {
                    output.append("\n");
                }
            }
        }
        return output.toString();
    }

    public Spot[] getLastMove() {
        return lastMove;
    }

    public void setLastMove(Spot start, Spot end) {
        lastMove = new Spot[]{start, end};
    }

    public static int getBoardSize(){
        return BOARDSIZE;
    }

    public boolean undoMove(Spot start, Spot end, Piece capturedPiece, boolean hadMoved) {
        Piece movedPiece = getPiece(end.getRow(), end.getCol());
        if (movedPiece == null) return false;

        // Move piece back
        board[start.getRow()][start.getCol()] = movedPiece;
        board[end.getRow()][end.getCol()] = null;
        movedPiece.setPosition(start.getRow(), start.getCol());

        // Restore captured piece
        if (capturedPiece != null) {
            board[end.getRow()][end.getCol()] = capturedPiece;
            capturedPiece.setAlive(true);
            capturedPiece.setPosition(end.getRow(), end.getCol());
            if (!pieces.contains(capturedPiece)) {
                pieces.add(capturedPiece);
            }
        }

        // Restore pawn state
        if (movedPiece instanceof Pawn pawn) {
            pawn.setHasMoved(hadMoved);
        }



        return true;
    }

    @Override
    public Board clone() {
        try {
            Board cloned = (Board) super.clone();
            cloned.board = new Piece[BOARDSIZE][BOARDSIZE];
            cloned.pieces = new ArrayList<>();

            for (int i = 0; i < BOARDSIZE; i++) {
                for (int j = 0; j < BOARDSIZE; j++) {
                    if (this.board[i][j] != null) {
                        Piece clonedPiece = this.board[i][j].clone(cloned); // Clone da peça
                        cloned.board[i][j] = clonedPiece;
                        cloned.pieces.add(clonedPiece);
                    }
                }
            }

            cloned.lastMove = (this.lastMove != null) ?
                    new Spot[] { this.lastMove[0].clone(), this.lastMove[1].clone() } : null;

            return cloned;
        } catch (CloneNotSupportedException e) {
            throw new AssertionError();
        }
    }
}
