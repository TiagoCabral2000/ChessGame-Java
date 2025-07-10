package pt.isec.pa.chess.model.data;

import pt.isec.pa.chess.model.data.pieces.King;
import pt.isec.pa.chess.model.data.pieces.Pawn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

//General class for the pieces
public abstract class Piece extends Spot implements Serializable, Cloneable {
    //Incrementar este valor se forem feitas alterações a classe
    //Exemplo 1L -> 2L
    private static final long serialVersionUID = 1L;

    protected Board board;
    private boolean isWhite; // true - White / false - Black
    private boolean isAlive; // true - Alive / false - Dead
    private final PieceFactory.PieceType type;
    private ArrayList<Spot> legalMoves;

    public Piece(Board board, boolean isWhite, int row, int col, PieceFactory.PieceType type){
        super(row,col);
        this.board = board;
        this.isAlive = true;
        this.isWhite = isWhite;
        this.type = type;
        legalMoves = new ArrayList<>();
    }

    public boolean isWhite(){
        return this.isWhite;
    }

    public boolean isAlive(){
        return this.isAlive;
    }

    public boolean setAlive(boolean kill){
        return this.isAlive = kill;
    }

    public boolean isEnemy(Piece other) {
        return other != null && this.isWhite() != other.isWhite();
    }

    public boolean canMove(Spot end) {
        if (!end.isValid(board.getBoardSize())) //check if destination spot is within the board
            return false;
        calculateLegalMoves();
        return getLegalMoves().contains(end);
    }

    public void setMoved(boolean moved){};

    public ArrayList<Spot> getLegalMoves() {return legalMoves;}

    public void addLegalMove(Spot move) {legalMoves.add(move); }

    public void clearLegalMoves() {legalMoves.clear(); }

    public abstract void calculateLegalMoves();

    public boolean hasMoves() {return !legalMoves.isEmpty(); }

    public PieceFactory.PieceType getType() {return type; }

    public abstract boolean hasMoved();

    public abstract int isInCheck(List<Spot> spots);

    protected boolean isItReallyLegal(Spot potentialMove) {
        System.out.println("IS_IT_REALLY_LEGAL");
        // Clona o tabuleiro para a simulação, para não alterar o jogo real.
        Board boardCopy = this.board.clone();
        System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Estado do boardCopy ANTES da verificação de xeque:");
        System.out.println(boardCopy.toString()); // Imprime o tabuleiro clonado
        // Encontra esta mesma peça, mas no tabuleiro clonado.
        Piece cpiece = boardCopy.getPiece(this.getRow(), this.getCol());
        if (cpiece == null) {
            // Falha de sanidade, não deveria acontecer.
            System.out.println("IS_IT_REALLY_LEGAL_NULL");
            return false;
        }

        // Executa o movimento no tabuleiro clonado.
        boardCopy.forceMove(cpiece, new Spot(potentialMove.getRow(), potentialMove.getCol()));

        // Tratamento especial para En Passant dentro da simulação
        if (cpiece instanceof Pawn &&
                this.getCol() != potentialMove.getCol() &&
                this.board.getPiece(potentialMove.getRow(), potentialMove.getCol()) == null) {

            int capturedPawnRow = this.getRow();
            int capturedPawnCol = potentialMove.getCol();

            Piece presumedCapturedPawn = boardCopy.getPiece(this.getRow(), capturedPawnCol);
            if (presumedCapturedPawn instanceof Pawn && presumedCapturedPawn.isWhite() != cpiece.isWhite()) {
                boardCopy.remPieceBoard(this.getRow(), capturedPawnCol);
                presumedCapturedPawn.setAlive(false);
            }
        }

        for (Piece pTest : boardCopy.getPieces()) {
            System.out.println("  --> Peça na lista do clone: " + pTest + " (Tipo: " + pTest.getType() + ", Cor: " + (pTest.isWhite() ? "Branca" : "Preta") + ")");
        }

        // Encontra o nosso próprio rei no tabuleiro clonado.
        King ownKingOnCopy = null;
        for (Piece pOnCopy : boardCopy.getPieces()) {
            if (pOnCopy instanceof King && pOnCopy.isWhite() == this.isWhite()) {
                ownKingOnCopy = (King) pOnCopy;
                break;
            }
        }

        // Se o rei não for encontrado (não deveria acontecer), o movimento é considerado inseguro.
        if (ownKingOnCopy == null) {
            System.out.println("IS_IT_REALLY_LEGAL_KINGNULL");

            return false;
        }

        // O movimento só é legal se, após a sua execução no tabuleiro clonado,
        // o nosso próprio rei NÃO estiver em xeque.
        // Dentro de Piece.isItReallyLegal(), antes do return final
        System.out.println("----------------------------------------------------");
        System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Peça Original: " + this + " (Cor: " + (this.isWhite() ? "Branca" : "Preta") + ")");
        System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Movimento Potencial Testado: " + potentialMove.convertToChessNotation(this.board.getBoardSize()));
        System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Estado do boardCopy ANTES da verificação de xeque:");
        System.out.println(boardCopy.toString()); // Imprime o tabuleiro clonado
        if (ownKingOnCopy != null) {
            System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Rei Próprio no boardCopy: " + ownKingOnCopy + " em " + ownKingOnCopy.getRow() + "," + ownKingOnCopy.getCol());
            System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Chamando isInCheck para este rei no boardCopy...");
        } else {
            System.out.println("[IS_IT_REALLY_LEGAL DEBUG] ERRO: Rei próprio não encontrado no boardCopy!");
        }
        boolean isKingInCheckResult = (ownKingOnCopy != null) ? (ownKingOnCopy.isInCheck(new ArrayList<>()) > 0) : true; // true se rei não encontrado
        System.out.println("[IS_IT_REALLY_LEGAL DEBUG] Resultado de ownKingOnCopy.isInCheck() > 0: " + isKingInCheckResult);
        System.out.println("[IS_IT_REALLY_LEGAL DEBUG] isItReallyLegal vai retornar: " + !isKingInCheckResult);
        System.out.println("----------------------------------------------------");
        return !isKingInCheckResult; // O seu return original

        //return ownKingOnCopy.isInCheck(new ArrayList<>()) == 0;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof Piece other) return getRow() == other.getRow() && getCol() == other.getCol() && getType() == other.getType() && isWhite() == other.isWhite();
        return false;
    }

    public abstract Piece clone(Board board);
}
