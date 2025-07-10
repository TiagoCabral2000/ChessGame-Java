package pt.isec.pa.chess.ui;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import pt.isec.pa.chess.model.ChessGame;
import pt.isec.pa.chess.model.ChessGameManager;
import pt.isec.pa.chess.model.ModelLog;
import pt.isec.pa.chess.model.data.Spot;
import pt.isec.pa.chess.ui.res.ImageManager;
import pt.isec.pa.chess.ui.res.SoundManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class BoardCanvas extends Canvas {
    private final ChessGameManager game;
    private final Pane parent;
    private final RootPane rootpane;

    private double boardSize;
    private double margin;
    private double cellSize;
    private double initialX;
    private double initialY;
    private Spot selectedSpot, targetSpot;

    private final Map<Character, Image> images;

    public BoardCanvas(ChessGameManager game, Pane parent, RootPane rp) {
        this.game = game;
        selectedSpot = null;
        targetSpot = null;
        this.parent = parent;
        this.rootpane = rp;

        // Mapeamento de peças para imagens (usando primeiro caractere)
        Map<Character, String> pieceImages = new HashMap<>();
        pieceImages.put('R', "rookW.png");    pieceImages.put('r', "rookB.png");
        pieceImages.put('N', "knightW.png");  pieceImages.put('n', "knightB.png");
        pieceImages.put('B', "bishopW.png");  pieceImages.put('b', "bishopB.png");
        pieceImages.put('Q', "queenW.png");   pieceImages.put('q', "queenB.png");
        pieceImages.put('K', "kingW.png");    pieceImages.put('k', "kingB.png");
        pieceImages.put('P', "pawnW.png");    pieceImages.put('p', "pawnB.png");

        // Carrega as imagens
        images = new HashMap<>();
        for (var entry : pieceImages.entrySet()) {
            Image img = ImageManager.getImage("pieces/" + entry.getValue());
            if (img != null) {
                images.put(entry.getKey(), img);
            }
        }

        createViews();
        registerHandlers();
        update();
    }

    private void centerCanvas(Pane parent) {
        setLayoutX((parent.getWidth() - getWidth()) / 2);
        setLayoutY((parent.getHeight() - getHeight()) / 2);
    }

    private void reSize() {
        double size = Math.min(parent.getWidth(), parent.getHeight());
        setWidth(size);
        setHeight(size);
    }

    private void createViews() {
        centerCanvas(parent);
        reSize();

    }

    private void registerHandlers() {

        // Vincula largura e altura ao menor valor entre largura e altura do container pai
        parent.widthProperty().addListener((obs, oldVal, newVal) -> {
            reSize();
            centerCanvas(parent);
            update();
        });
        parent.heightProperty().addListener((obs, oldVal, newVal) -> {
            reSize();
            centerCanvas(parent);
            update();
        });

        game.addPropertyChangeListener(ChessGameManager.PROP_PROMOTION, evt -> {
            AskPromotion askPromotion = new AskPromotion(game, parent);
            askPromotion.showAndWait();
        });

        game.addPropertyChangeListener(ChessGameManager.PROP_UPDATE_BOARD, evt -> {
            System.out.println("PROP_UPDATE_BOARD");
            update(); //Atualiza o canvas quando o tabuleiro muda
        });

        game.addPropertyChangeListener(ChessGameManager.PROP_GAME_STATE, evt -> clearSelection());

        this.setOnMousePressed(event -> {
            double x = event.getX();
            double y = event.getY();
            System.out.println("Coordenadas clicadas: (" + x + ", " + y + ")");

            // Coordenadas do clique para posição tabuleiro
            int col = (int) ((x - initialX - margin) / cellSize);
            int row = (int) ((y - initialY - margin) / cellSize);

            if (row < 0 || row >= game.getBoardSize() || col < 0 || col >= game.getBoardSize()) {
                selectedSpot = null;
                return;
            }

            // Primeiro clique
            System.out.println(game.getGameState());
            System.out.println(selectedSpot);
            if (selectedSpot == null && game.getGameState() == ChessGame.GameState.RUNNING) {

                String piece = game.getPieceAt(row, col);
                System.out.println(selectedSpot);
                System.out.println(piece);
                if(piece == null)
                    return;
                System.out.println(selectedSpot);
                if(!game.hasMoves(row, col))
                    return;
                System.out.println(selectedSpot);
                boolean isWhitePiece = Character.isUpperCase(piece.charAt(0));
                boolean isCurrentPlayerWhite = game.getCurrentPlayer().equals("White");
                if (isWhitePiece != isCurrentPlayerWhite)
                    return;
                selectedSpot = new Spot(row, col);
            }
            else if (game.getGameState() == ChessGame.GameState.RUNNING) { //segundo clique
                boolean isCurrentPlayerWhite = game.getCurrentPlayer().equals("White");
                if (rootpane.getGameStarted()) {
                    String piece = game.getPieceAt(row, col);

                    if (piece != null) {
                        boolean isWhitePiece = Character.isUpperCase(piece.charAt(0));
                        isCurrentPlayerWhite = game.getCurrentPlayer().equals("White");
                        if (isWhitePiece == isCurrentPlayerWhite)
                            return;
                    }
                    if (!game.hasMove(selectedSpot, row, col)) {
                        return;
                    }
                    targetSpot = new Spot(row, col);

                    // Converte posições para notação de xadrez (ex: "a2" para "a4")
                    String from = selectedSpot.convertToChessNotation(game.getBoardSize());
                    String to = targetSpot.convertToChessNotation(game.getBoardSize());

                    // Tenta fazer o movimento
                    String capturedPiece = game.getPieceAt(targetSpot.getRow(), targetSpot.getCol());
                    ChessGame.MoveResult result = game.move(from, to);

                    List<String> soundsPlay = new ArrayList<>();
                    if (result == ChessGame.MoveResult.CHECKMATEc){
                        soundsPlay.add("checkmate");
                        soundsPlay.add(isCurrentPlayerWhite ? "white" : "black");
                        soundsPlay.add("wins");
                        SoundManager.playSequentially(soundsPlay, game.getSoundLanguage());
                    } else if (result == ChessGame.MoveResult.CHECKMATEe){
                        soundsPlay.add("checkmate");
                        soundsPlay.add(isCurrentPlayerWhite ? "black" : "white");
                        soundsPlay.add("wins");
                        SoundManager.playSequentially(soundsPlay, game.getSoundLanguage());
                    }
                    else if (result == ChessGame.MoveResult.CAPTURE) {
                        String capturingPiece = game.getPieceAt(targetSpot.getRow(), targetSpot.getCol());
                        char capturingPieceChar = (capturingPiece != null && !capturingPiece.isEmpty()) ?
                                capturingPiece.charAt(0) : 'P'; // Default to pawn

                        char capturedPieceChar = (capturedPiece != null && !capturedPiece.isEmpty()) ?
                                capturedPiece.charAt(0) : 'P'; // Default to pawn

                        soundsPlay.add(isCurrentPlayerWhite ? "white" : "black");
                        soundsPlay.add(getPieceName(capturingPieceChar));
                        soundsPlay.add(String.valueOf(from.charAt(0))); // From square
                        soundsPlay.add(String.valueOf(from.charAt(1)));
                        soundsPlay.add("capture");

                        soundsPlay.add(isCurrentPlayerWhite ? "black" : "white");
                        soundsPlay.add(getPieceName(capturedPieceChar));
                        soundsPlay.add(String.valueOf(to.charAt(0))); // To square
                        soundsPlay.add(String.valueOf(to.charAt(1)));

                        SoundManager.playSequentially(soundsPlay, game.getSoundLanguage());
                    } else if (result == ChessGame.MoveResult.VALID_MOVE) {
                        System.out.printf("GUI Click: (%d,%d) -> Chess: (%d,%d)%n", targetSpot.getRow(), targetSpot.getCol(), targetSpot.getRow(), targetSpot.getCol());

                        String pieceNotation = game.getPieceAt(targetSpot.getRow(), targetSpot.getCol());
                        System.out.printf("Sound lookup at (%d,%d): %s%n",
                                targetSpot.getRow(), targetSpot.getCol(), pieceNotation);

                        char pieceChar = (pieceNotation != null && !pieceNotation.isEmpty()) ?
                                pieceNotation.charAt(0) : 'P';

                        soundsPlay.add(isCurrentPlayerWhite ? "white" : "black");
                        soundsPlay.add(getPieceName(pieceChar));
                        soundsPlay.add(String.valueOf(from.charAt(0)));
                        soundsPlay.add(String.valueOf(from.charAt(1)));
                        soundsPlay.add("to");
                        soundsPlay.add(String.valueOf(to.charAt(0)));
                        soundsPlay.add(String.valueOf(to.charAt(1)));
                        SoundManager.playSequentially(soundsPlay, game.getSoundLanguage());
                    } else {
                        soundsPlay.add("invalid");
                        SoundManager.playSequentially(soundsPlay, game.getSoundLanguage());
                    }

                    selectedSpot = null; // possibilita nova jogada
                    targetSpot = null;
                } else {
                    ModelLog.getInstance().addLog("O jogo ainda não foi iniciado. Por favor, inicie o jogo antes de realizar uma jogada.");
                }
            }
            update();
        });
    }

    private String getPieceName(char pieceChar) {
        return switch (Character.toLowerCase(pieceChar)) {
            case 'p' -> "pawn";
            case 'r' -> "rook";
            case 'n' -> "knight";
            case 'b' -> "bishop";
            case 'q' -> "queen";
            case 'k' -> "king";
            default -> ""; // or throw an exception for invalid pieces
        };
    }

    private void update(){
        GraphicsContext gc = getGraphicsContext2D();

        //draw
        clear();

        boardSize = Math.min(getWidth(), getHeight()) * 0.8;
        margin = boardSize * 0.01;
        cellSize = (boardSize - 2 * margin) / game.getBoardSize();
        initialX = (getWidth() - boardSize) / 2;
        initialY = (getHeight() - boardSize) / 2;

        centerCanvas(parent);
        drawBoard(gc);
        drawLabels(gc);
        String boardState = game.queryBoard();
        if(rootpane.isShowMoves())
        {
            drawMoves(gc);
        }
        drawPieces(gc);
        drawSelection(gc);
    }

    private void clear() {
        getGraphicsContext2D().clearRect(0, 0, getWidth(), getHeight());
    }

    private void drawBoard(GraphicsContext gc) {

        // Outer gray border
        gc.setFill(Color.GRAY);
        gc.fillRect(initialX, initialY, boardSize, boardSize + margin * 4);

        // Inner light gray border
        gc.setFill(Color.LIGHTGRAY);
        gc.fillRect(initialX + margin * 0.2, initialY + margin * 0.2, boardSize - margin * 0.4, boardSize - margin * 0.4 + margin * 4);

        // Inner gray border (optional depth effect)
        gc.setFill(Color.GRAY);
        gc.fillRect(initialX + margin * 0.8, initialY + margin * 0.8, boardSize - margin * 1.6, boardSize - margin * 1.6);

        // Desenha o tabuleiro dentro das margens
        for (int row = 0; row < game.getBoardSize(); row++) {
            for (int col = 0; col < game.getBoardSize(); col++) {
                boolean isLightSquare = (row + col) % 2 == 0;
                gc.setFill(isLightSquare ? javafx.scene.paint.Color.BEIGE : Color.SADDLEBROWN);
                gc.fillRect(margin + col * cellSize + initialX, margin + row * cellSize + initialY, cellSize, cellSize);
            }
        }
    }

    private void drawMoves(GraphicsContext gc) {
        if(selectedSpot != null) {
            String[] moves = game.getLegalMovesString(selectedSpot.getRow(), selectedSpot.getCol()).strip().split(" ");
            for (String move : moves) {
                move = move.trim();

                int col = move.charAt(0) - 'a';
                int row = game.getBoardSize() - (move.charAt(1) - '0');

                double x = initialX + margin + (col * cellSize);
                double y = initialY + margin + (row * cellSize);

                double imgSize = cellSize * 0.8;
                double imgX = x + (cellSize - imgSize) / 2;
                double imgY = y + (cellSize - imgSize) / 2;

                gc.setFill(Color.GRAY);
                gc.setGlobalAlpha(0.4);
                gc.fillOval(imgX, imgY, imgSize, imgSize);
                gc.setGlobalAlpha(1);
            }
        }
    }

    private void drawLabels(GraphicsContext gc) {

        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(cellSize * 0.3));


        // Letras (a-h) nas margens superior e inferior
        for (int col = 0; col < game.getBoardSize(); col++) {
            String label = String.valueOf((char) ('a' + col));
            double x = margin + col * cellSize + initialX + cellSize / 2 - gc.getFont().getSize() / 2;
            gc.fillText(label, x, initialY - margin - 6); // Superior
            gc.fillText(label, x, 20 + initialY + boardSize + margin + gc.getFont().getSize()); // Inferior
        }

        // Números (1-8) nas margens esquerda e direita
        for (int row = 0; row < game.getBoardSize(); row++) {
            String label = String.valueOf(game.getBoardSize() - row);
            double y = margin + row * cellSize + initialY + cellSize / 2 + gc.getFont().getSize() / 4;
            gc.fillText(label, initialX - margin - gc.getFont().getSize(), y); // Esquerda
            gc.fillText(label, initialX + boardSize + margin + 5, y); // Direita
        }

        // Desenhar imagens dos reis na área cinza-clara
        Image blackKing = ImageManager.getImage("pieces/KingB.png");
        Image whiteKing = ImageManager.getImage("pieces/KingW.png");
        Image flagIcon = ImageManager.getImage("flag.png");

        double kingSize = margin * 4;

        // Nome do jogador preto à direita do rei preto
        String blackPlayerName = game.getBlackPlayerName();
        if (blackPlayerName == null) {
            blackPlayerName = "Black player";
        }
        // Nome do jogador branco à esquerda do rei branco
        String whitePlayerName = game.getWhitePlayerName();
        if (whitePlayerName == null) {
            whitePlayerName = "White Player";
        }

        double widthBOX = Math.max(Math.max(kingSize + 2 * margin + gc.getFont().getSize() * blackPlayerName.length() / 2, kingSize + 2 * margin + gc.getFont().getSize() * whitePlayerName.length() / 2), boardSize * 0.2);

        // Jogador preto à esquerda
        //BOX
        gc.setFill(Color.GRAY);
        gc.fillRect(initialX + margin, initialY + boardSize - margin * 0.4, widthBOX, kingSize);
        gc.setFill((game.getCurrentPlayer() == "White" ? Color.WHITE : Color.LIGHTGREEN));
        gc.fillRect(initialX + margin + 1, initialY + boardSize - margin * 0.4 + 1, widthBOX - 2, kingSize - 2);

        //NAME
        gc.drawImage(blackKing, initialX + margin, initialY + boardSize - margin * 0.4, kingSize, kingSize);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(kingSize * 0.8));
        gc.fillText(blackPlayerName, initialX + kingSize + margin + margin, initialY + boardSize - margin * 0.4 + kingSize * 0.8);

        // Jogador branco à direita
        //BOX
        gc.setFill(Color.GRAY);
        gc.fillRect(initialX + boardSize - widthBOX - margin, initialY + boardSize - margin * 0.4, widthBOX, kingSize);
        gc.setFill((game.getCurrentPlayer() == "White" ? Color.LIGHTGREEN : Color.WHITE));
        gc.fillRect(initialX + boardSize - widthBOX + 1 - margin, initialY + boardSize - margin * 0.4 + 1, widthBOX - 2, kingSize - 2);

        //NAME
        gc.drawImage(whiteKing, initialX + boardSize - kingSize - margin, initialY + boardSize - margin * 0.4, kingSize, kingSize);
        gc.setFill(Color.BLACK);
        gc.setFont(javafx.scene.text.Font.font(kingSize * 0.8));
        gc.fillText(whitePlayerName, initialX + boardSize - kingSize - gc.getFont().getSize() * whitePlayerName.length() / 2 - margin, initialY + boardSize - margin * 0.4 + kingSize * 0.8);

        //FLAG
        gc.drawImage(flagIcon, (game.getCurrentPlayer() == "White" ? initialX + boardSize - widthBOX - margin : initialX + widthBOX - margin * 3.5), initialY + boardSize - margin * 0.4, kingSize, kingSize);
    }

    private void drawPieces(GraphicsContext gc) {

        // Processa o tabuleiro
        String boardState = game.queryBoard();
        if (boardState == null || boardState.isEmpty()) return;
        String[] lines = boardState.strip().split("\n");

        for (int row = 0; row < 8 && row < lines.length; row++) {
            String[] cells = lines[row].trim().split("\\s+");
            for (int col = 0; col < 8 && col < cells.length; col++) {
                String cell = cells[col];
                if (cell.length() < 3) continue; // Pelo menos peça + casa, ex: "Ra1"

                char pieceChar = cell.charAt(0);
                if (!images.containsKey(pieceChar)) continue;

                Image img = images.get(pieceChar);
                if (img == null) continue;

                double x = initialX + margin + (col * cellSize);
                double y = initialY + margin + (row * cellSize);

                double imgSize = cellSize * 0.8;
                double imgX = x + (cellSize - imgSize) / 2;
                double imgY = y + (cellSize - imgSize) / 2;

                gc.drawImage(img, imgX, imgY, imgSize, imgSize);
            }
        }
    }

    private void drawSelection(GraphicsContext gc) { //Auxiliar para ver celulas selecionadas
        if (selectedSpot != null) {

            gc.setStroke(Color.BLACK);
            gc.setLineWidth(2);

            double x = initialX + margin + selectedSpot.getCol() * cellSize;
            double y = initialY + margin + selectedSpot.getRow() * cellSize;

            gc.strokeRect(x, y, cellSize, cellSize);
        }
    }

    public boolean hasSelection() {
        return selectedSpot != null;
    }

    public void clearSelection() {
        selectedSpot = null;
        update();
    }
}