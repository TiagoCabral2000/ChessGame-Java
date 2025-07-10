package pt.isec.pa.chess.model.command;

import pt.isec.pa.chess.model.ChessGame;

public class AbstractCommand {
    protected ChessGame receiver;

    protected AbstractCommand(ChessGame receiver) {
        this.receiver = receiver;
    }
}
