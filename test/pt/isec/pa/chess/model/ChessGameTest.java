package pt.isec.pa.chess.model; // Certifique-se que este é o mesmo pacote de ChessGame.java

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ChessGameTest {

    private ChessGame game;

    @BeforeEach
    void setUp() {
        game = new ChessGame();
        game.setPlayerNames("JogadorBranco", "JogadorPreto");
    }

    @Test
    void testCheckmate() {
        // Arrange: Configura o estado inicial do teste para um xeque-mate simples
        String gameStateString = "WHITE,kh8,Qg7,Kg6";
        game.importGame(gameStateString);

        // Act: Verifica se a função identifica corretamente o xeque-mate
        boolean isCheckmate = game.isCheckmate(false);

        // Assert: Verifica se o xeque-mate foi detetado e o estado do jogo está correto
        assertTrue(isCheckmate,
                "A função isCheckmate(true) deveria detetar o xeque-mate para as Brancas. Posição: " + gameStateString);

        assertEquals(ChessGame.GameState.CHECKMATEc, game.getGameState(),
                "O GameState deveria ser CHECKMATEc (Brancas vencem) após importar esta posição de xeque-mate.");
    }

    @Test
    void testStalemate() {
        // Arrange: Configura o estado inicial do teste para um empate (stalemate)
        String gameStateString = "BLACK,kh1,Qf2,Ka8";
        game.importGame(gameStateString);

        // Act: Executa a verificação de stalemate
        boolean isStale = game.isStalemate(true);

        // Assert: Verifica se o stalemate foi corretamente detetado
        assertTrue(isStale, "A função isStalemate(true) deveria detetar o stalemate para as Pretas. Posição: " + gameStateString);
        assertEquals(ChessGame.GameState.STALEMATE, game.getGameState(),
                "O GameState deveria ser STALEMATE após importar esta posição.");
    }

    @Test
    void testEnPassant() {

        // Arrange: Configura o estado inicial do teste para permitir En Passant
        game.resetGame();

        game.move("e2", "e4"); // Brancas jogam e4
        game.move("c7", "c5"); // Pretas jogam c5
        game.move("e4", "e5"); // Brancas avançam o peão para e5

        // Verifica se o peão branco está em e5
        String whitePawnE5String = game.getPieceAt(3, 4);
        assertNotNull(whitePawnE5String, "Deveria haver um peão branco em e5.");
        assertEquals("P", whitePawnE5String.substring(0, 1));

        // Pretas jogam d7-d5, preparando o En Passant
        ChessGame.MoveResult blackPawnMoveResult = game.move("d7", "d5");

        // Verifica se o peão preto está em d5
        String blackPawnD5String = game.getPieceAt(3, 3);
        assertNotNull(blackPawnD5String, "Deveria haver um peão preto em d5.");
        assertEquals("p", blackPawnD5String.substring(0, 1));
        assertEquals("White", game.getCurrentPlayer(), "Deveria ser a vez das Brancas.");

        // Act: Executa o movimento En Passant (e5xd6)
        ChessGame.MoveResult resultEnPassant = game.move("e5", "d6");

        // Assert: Verifica se o resultado do movimento é EN_PASSANT
        assertEquals(ChessGame.MoveResult.EN_PASSANT, resultEnPassant, "O resultado do movimento e5-d6 deveria ser EN_PASSANT.");

        // Assert: Verifica o estado do tabuleiro após o En Passant
        String whitePawnAtD6String = game.getPieceAt(2, 3); // Peão branco em d6
        assertNotNull(whitePawnAtD6String, "Peão Branco deveria estar em d6 após a captura En Passant.");
        assertTrue(Character.isUpperCase(whitePawnAtD6String.charAt(0)), "Peça em d6 deveria ser branca.");
        assertEquals('P', Character.toUpperCase(whitePawnAtD6String.charAt(0)), "Peça em d6 deveria ser um Peão.");

        // Assert: Verifica se a casa d5 (onde estava o peão preto capturado) está vazia
        assertNull(game.getPieceAt(3, 3), "A casa d5 (onde estava o peão preto capturado) deveria estar vazia.");
        // Assert: Verifica se a casa e5 (origem do peão branco) está vazia
        assertNull(game.getPieceAt(3, 4), "A casa e5 (origem do peão branco) deveria estar vazia.");
        // Assert: Verifica se o turno passou para as Pretas
        assertEquals("Black", game.getCurrentPlayer(), "Deveria ser a vez das Pretas após o En Passant.");
    }

    @Test
    void testCastling() {
        // Arrange: Configura o estado inicial do teste para permitir o roque do lado do rei (Brancas)
        String gameStateString = "WHITE,Ke1*,Rh1*,kc8";
        game.importGame(gameStateString);

        assertEquals("White", game.getCurrentPlayer(), "Deveria ser a vez das Brancas.");

        // Act: Executa o movimento de roque (rei de e1 para g1)
        ChessGame.MoveResult result = game.move("e1", "g1");

        // Assert: Verifica se o resultado do movimento é CASTLING
        assertEquals(ChessGame.MoveResult.CASTLING, result, "O resultado do movimento e1-g1 deveria ser CASTLING.");

        // Assert: Verifica a posição do rei e da torre após o roque
        String kingAfterCastleString = game.getPieceAt(7, 6);
        String rookAfterCastleString = game.getPieceAt(7, 5);

        assertNotNull(kingAfterCastleString, "Rei Branco deveria estar em g1.");
        assertEquals('K', kingAfterCastleString.charAt(0));

        assertNotNull(rookAfterCastleString, "Torre Branca deveria estar em f1.");
        assertEquals('R', rookAfterCastleString.charAt(0));

        // Assert: Verifica se as casas originais do rei e da torre estão vazias
        assertNull(game.getPieceAt(7, 4), "Casa e1 (original do Rei) deveria estar vazia.");
        assertNull(game.getPieceAt(7, 7), "Casa h1 (original da Torre) deveria estar vazia.");

        // Assert: Verifica se o turno passou para as Pretas
        assertEquals("Black", game.getCurrentPlayer(), "Deveria ser a vez das Pretas após o Roque.");
    }


    @Test
    void testPawnPromotion() {
        // Arrange: Configura o estado inicial do teste
        String gameStateString = "WHITE,Pe7,Ka1,ka8";
        game.importGame(gameStateString);
        assertEquals("White", game.getCurrentPlayer(), "Deveria ser a vez das Brancas.");
        assertNotNull(game.getPieceAt(1, 4), "Peão branco não encontrado em e7.");

        // Act: Executa a ação a ser testada (promoção do peão)
        ChessGame.MoveResult resultMove = game.move("e7", "e8");

        // Assert: Verifica se o movimento resulta em promoção
        assertEquals(ChessGame.MoveResult.PROMOTION, resultMove, "Movimento e7-e8 deveria resultar em PROMOTION.");

        // Act: Realiza a promoção para Dama
        String promotionSquareNotation = game.promotePawn("QUEEN");

        // Assert: Verifica o estado do tabuleiro após a promoção
        String promotedPieceString = game.getPieceAt(0, 4);
        assertNotNull(promotedPieceString, "Deveria haver uma peça em e8 após a promoção.");
        assertEquals('Q', promotedPieceString.charAt(0), "A peça em e8 deveria ser uma Dama Branca.");
        assertEquals("e8", promotionSquareNotation, "A notação da casa retornada pela promoção deveria ser e8.");
        assertNull(game.getPieceAt(1, 4), "A casa e7 (origem do peão) deveria estar vazia.");

        // Assert: Verifica se o turno passou para as Pretas
        assertEquals("Black", game.getCurrentPlayer(), "Deveria ser a vez das Pretas após a promoção.");
    }


}



