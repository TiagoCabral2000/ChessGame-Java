package pt.isec.pa.chess.model.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Player implements Serializable {
    //Incrementar este valor se forem feitas alterações a classe
    //Exemplo 1L -> 2L
    private static final long serialVersionUID = 2L;

    private final boolean isWhite;
    private String name;
    private List<Piece> Alive;
    private List<Piece> Dead;

    public Player(boolean isWhite, String name)
    {
        Alive = new ArrayList<Piece>();
        Dead = new ArrayList<Piece>();
        this.name = name;
        this.isWhite = isWhite;
    }

    public boolean isWhite() { return this.isWhite; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public boolean addAlive(Piece piece) {
        if(!Alive.contains(piece))
        {
            if(piece.isWhite() == isWhite)
            {
                Alive.add(piece);
                return true;
            }
        }
        return false;
    }
    public boolean remAlive(Piece piece) {
        if(Alive.contains(piece))
        {
            Alive.remove(piece);
            return true;
        }
        return false;
    }
    
    public boolean addDead(Piece piece) {
        if(!Dead.contains(piece))
        {
            if(piece.isWhite() == isWhite)
            {
                Dead.add(piece);
                return true;
            }
        }
        return false;
    }

    public boolean remDead(Piece piece) {
        if(!Dead.contains(piece))
        {
            if(piece.isWhite() == isWhite)
            {
                Dead.add(piece);
                return true;
            }
        }
        return false;
    }

    public boolean kill(Piece piece) {
        if(Alive.contains(piece))
        {
            if(addDead(piece))
                return remAlive(piece);
        }
        return false;
    }

    public List<Piece> getAlive() {
        return Alive;
    }

    public List<Piece> getDead() {
        return Dead;
    }
}
