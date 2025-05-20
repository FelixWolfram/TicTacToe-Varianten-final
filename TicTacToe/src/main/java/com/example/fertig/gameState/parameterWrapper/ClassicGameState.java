package com.example.fertig.gameState.parameterWrapper;

import com.example.fertig.gameState.GameState;

public class ClassicGameState implements GameState {
    public int[] board;
    public boolean player1;

    public ClassicGameState(int[] board, boolean player1) {
        this.board = board;
        this.player1 = player1;
    }
}
