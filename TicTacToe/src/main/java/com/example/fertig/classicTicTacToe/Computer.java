package com.example.fertig.classicTicTacToe;

import com.example.fertig.gameState.ClassicComputerInterface;
import com.example.fertig.gameState.GameState;
import com.example.fertig.gameState.parameterWrapper.ClassicGameState;
import com.example.fertig.structure.ClassicErgebnis;
import com.example.fertig.structure.GewinnerCheck;

import java.util.ArrayList;

public class Computer implements ClassicComputerInterface {
    public float difficulty = 1;
    private final int maxRecursionDepth = 10;
    ArrayList<ClassicErgebnis> alleErgebnisse = new ArrayList<>();

    // player1 = -1 = Human, !player1 = 1 = Computer, 0 = Draw
    public int calculate(int[] board, int recursionDepth, boolean player1) {
        if (recursionDepth >= maxRecursionDepth * difficulty) {
            return 0;
        }

        int besterWert = player1 ? 10 : -10; // schlechter Wert am Anfang, welchen es zu maximieren gilt

        for (int i = 0; i < board.length; i++) {
            // null = Spiel noch nicht beendet, 0 = Unentschieden, -1 = Spieler 1 gewinnt, 1 = Spieler 2 gewinnt
            if (board[i] == 0) {
                board[i] = player1 ? -1 : 1;
                int score = calculate(board, recursionDepth + 1, !player1);
                Integer gewinner = GewinnerCheck.checkForWinner(board);
                board[i] = 0; // Feld wieder zurücksetzen
                // null = Spiel noch nicht beendet, 0 = Unentschieden, -1 = Spieler 1 gewinnt, 1 = Spieler 2 gewinnt
                if (gewinner != null) {
                    if (recursionDepth == 0) {
                        alleErgebnisse.clear();
                        alleErgebnisse.add(new ClassicErgebnis(score, i));
                    }
                    return (maxRecursionDepth - recursionDepth) * gewinner;
                    // näher liegende Gewinnpositionen höher bewerten, um immer die gewünschten Ergebnisse zu erhalten
                }

                // wenn bester Wert nicht erreicht wurde, aktuelle Situation zumindest maximieren
                if (recursionDepth == 0 && score == besterWert) {
                    alleErgebnisse.add(new ClassicErgebnis(score, i));
                }
                if (player1 && score < besterWert) {
                    besterWert = score;
                    if (recursionDepth == 0){
                        alleErgebnisse.clear();
                        alleErgebnisse.add(new ClassicErgebnis(score, i));
                    }
                } else if (!player1 && score > besterWert) {
                    besterWert = score;
                    if (recursionDepth == 0){
                        alleErgebnisse.clear();
                        alleErgebnisse.add(new ClassicErgebnis(score, i));
                    }
                }
            }
        }
        return besterWert;
    }

    public int computerZug(GameState gameState) {
        alleErgebnisse.clear();
        ClassicGameState state = (ClassicGameState) gameState;

        int unused = calculate(state.board, 0, state.player1);

        return alleErgebnisse.get((int) (Math.random() * alleErgebnisse.size())).zug;
    }
}
