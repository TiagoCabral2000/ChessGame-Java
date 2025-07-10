package pt.isec.pa.chess.model.data;

import java.io.Serializable;

public class Spot implements Serializable {
    //Incrementar este valor se forem feitas alterações a classe
    //Exemplo 1L -> 2L
    private static final long serialVersionUID = 1L;

    private int row;
    private int col;

    public Spot(int row, int col){
        this.row = row;
        this.col = col;
    }

    public int getRow() {
        return row;
    }

    public void setRow(int row) {
        this.row = row;
    }

    public int getCol() {
        return col;
    }

    public void setCol(int col) {
        this.col = col;
    }

    public void setPosition(int row, int col){
        this.row = row;
        this.col = col;
    }

    public boolean isValid(int boardSize){ //check if it's in the bord
        return row >= 0 && row < boardSize && col >= 0 && col < boardSize;
    }

    public String convertToChessNotation(int boardSize) {
        char colChar = (char) ('a' + this.getCol());
        int rowNumber = boardSize - this.getRow();
        return "" + colChar + rowNumber;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null) return false;
        if (this == obj) return true;
        if (obj instanceof Spot other) return this.row == other.row && this.col == other.col;
        return false;
    }

    @Override
    public Spot clone() {
        return new Spot(this.row, this.col);
    }
}
