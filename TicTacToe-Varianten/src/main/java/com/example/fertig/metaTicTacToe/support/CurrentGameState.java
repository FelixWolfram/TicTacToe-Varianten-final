package com.example.fertig.metaTicTacToe.support;

import com.example.fertig.structure.Constants;

import java.util.Arrays;

public class CurrentGameState {
    protected static int activeGrid = -1; // -1 = freie Auswahl, alles andere = nur in dieses Feld kann ein Kreuz gesetzt werden
    protected static int[][] boardValues = new int[(int) Math.pow(Constants.OUTER_GRID_SIZE, 2)][(int) Math.pow(Constants.INNER_GRID_SIZE, 2)];
    protected static int[] bigWins = new int [(int) Math.pow(Constants.OUTER_GRID_SIZE, 2)]; // fertig gespielte Grids
    protected static boolean gameOver = false;
    protected static boolean spieler1 = true;

    protected static void resetGameState(){
        for (int i = 0; i < boardValues.length; i++) {
            bigWins[i] = Constants.UNFINISHED;
            Arrays.fill(boardValues[i], 0);
        }
        gameOver = false;
        activeGrid = -1;
    }
}
