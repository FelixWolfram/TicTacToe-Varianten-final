package com.example.fertig.gameState.parameterWrapper;

import com.example.fertig.gameState.GameState;

public class MetaGameState implements GameState {
    public int[][] board;
    public int[] bigWins;
    public boolean player1;
    public int currentActiveGrid;

    public MetaGameState(int[][] board, int[] bigWins, boolean player1, int currentActiveGrid) {
        this.board = board;
        this.bigWins = bigWins;
        this.player1 = player1;
        this.currentActiveGrid = currentActiveGrid;
    }
}
