package pt.isec.pa.chess.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class ChessGameManagerTest {

    private ChessGameManager gameManager;

    @BeforeEach
    void setUp() {
        gameManager = new ChessGameManager();
    }

    @Test
    void testUndo() {

        // Arrange: Preparar o estado inicial do teste
        gameManager.startNewGame("JogadorBranco", "JogadorPreto");

        // Estado Inicial (T0)
        String boardState_T0 = gameManager.queryBoard();
        assertEquals("White", gameManager.getCurrentPlayer(), "T0: Deveria ser a vez das Brancas.");
        assertFalse(gameManager.canUndo(), "T0: Não deveria haver nada para desfazer inicialmente.");
        assertFalse(gameManager.canRedo(), "T0: Não deveria haver nada para refazer inicialmente.");

        // Act: Realizar os movimentos a serem testados
        ChessGame.MoveResult resultM1 = gameManager.move("e2", "e4");

        String boardState_T1 = gameManager.queryBoard();
        assertNotEquals(boardState_T0, boardState_T1, "T1: Tabuleiro deveria ter mudado após e2-e4.");
        assertEquals("Black", gameManager.getCurrentPlayer(), "T1: Deveria ser a vez das Pretas.");
        assertTrue(gameManager.canUndo(), "T1: Deveria ser possível fazer undo após o primeiro movimento.");
        assertFalse(gameManager.canRedo(), "T1: Não deveria ser possível fazer redo ainda.");

        ChessGame.MoveResult resultM2 = gameManager.move("d7", "d5");

        String boardState_T2 = gameManager.queryBoard();
        assertNotEquals(boardState_T1, boardState_T2, "T2: Tabuleiro deveria ter mudado após d7-d5.");
        assertEquals("White", gameManager.getCurrentPlayer(), "T2: Deveria ser a vez das Brancas.");
        assertTrue(gameManager.canUndo(), "T2: Deveria ser possível fazer undo após o segundo movimento.");
        assertFalse(gameManager.canRedo(), "T2: Não deveria ser possível fazer redo ainda.");

        // Act: Realizar os undos
        boolean undo1Success = gameManager.undo();

        // Assert: Verificar se o primeiro undo foi bem-sucedido e o estado está correto
        assertTrue(undo1Success, "Primeiro Undo (desfazer M2) deveria ser bem-sucedido.");
        assertEquals("Black", gameManager.getCurrentPlayer(), "Após Undo de M2: Deveria ser a vez das Pretas.");
        assertEquals(boardState_T1, gameManager.queryBoard(),
                "Após Undo de M2: Estado do tabuleiro deve ser idêntico ao estado T1 (após M1).");
        assertTrue(gameManager.canUndo(), "Após Undo de M2: Ainda deve ser possível fazer undo (para M1).");
        assertTrue(gameManager.canRedo(), "Após Undo de M2: Deveria ser possível fazer redo (para M2).");

        boolean undo2Success = gameManager.undo();

        // Assert: Verificar se o segundo undo foi bem-sucedido e o estado voltou ao inicial
        assertTrue(undo2Success, "Segundo Undo (desfazer M1) deveria ser bem-sucedido.");
        assertEquals("White", gameManager.getCurrentPlayer(), "Após Undo de M1: Deveria ser a vez das Brancas.");
        assertEquals(boardState_T0, gameManager.queryBoard(),
                "Após Undo de M1: Estado do tabuleiro deve ser idêntico ao estado T0 (inicial).");
        assertFalse(gameManager.canUndo(), "Após Undo de M1: Não deveria haver mais nada para desfazer.");
        assertTrue(gameManager.canRedo(), "Após Undo de M1: Deveria ser possível fazer redo (para M1 e depois M2).");
    }
}