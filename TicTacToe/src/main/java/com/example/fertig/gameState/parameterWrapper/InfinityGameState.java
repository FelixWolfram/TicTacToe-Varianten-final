package com.example.fertig.gameState.parameterWrapper;

import com.example.fertig.gameState.GameState;

import java.util.Deque;

public class InfinityGameState implements GameState {
    public int[] board;
    public boolean player1;
    public Deque<Integer> queue;

    public InfinityGameState(int[] board, boolean player1, Deque<Integer> queue) {
        this.board = board;
        this.player1 = player1;
        this.queue = queue;
    }
}
