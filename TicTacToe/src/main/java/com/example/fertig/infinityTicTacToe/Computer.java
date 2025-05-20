package com.example.fertig.infinityTicTacToe;

import com.example.fertig.gameState.ClassicComputerInterface;
import com.example.fertig.gameState.GameState;
import com.example.fertig.gameState.parameterWrapper.InfinityGameState;
import com.example.fertig.structure.ClassicErgebnis;
import com.example.fertig.structure.GewinnerCheck;

import java.util.ArrayList;
import java.util.Deque;
import java.util.LinkedList;

public class Computer implements ClassicComputerInterface {
    public float difficulty = 1;
    private final int maxRecursionDepth = 10;
    ArrayList<ClassicErgebnis> alleErgebnisse = new ArrayList<>();

    // player1 = -1 = Human, !player1 = 1 = Computer, 0 = Draw
    public int calculate(int[] board, int recursionDepth, boolean isMinimizing, Deque<Integer> queue) {
        if (recursionDepth >= maxRecursionDepth * difficulty) {
            return 0; // einfach als Unentschieden zurückgeben
        }

        int besterWert = isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE; // schlechter Wert am Anfang, welchen es zu maximieren gilt

        Deque<Integer> copyQueue = new LinkedList<>(queue);
        // Arrays selber (in queue) sollte nicht verändert werden, da nur die Referenzen/Strukturen kopiert wird
        for (int i = 0; i < board.length; i++) {
                // null = Spiel noch nicht beendet, 0 = Unentschieden, -1 = Spieler 1 gewinnt, 1 = Spieler 2 gewinnt
                if (board[i] == 0) {
                    board[i] = isMinimizing ? -1 : 1;
                    copyQueue.add(i);

                    Integer deleteValue = null;
                    Integer deleteCell = null;
                    if (copyQueue.size() > 6) {
                        deleteCell = copyQueue.poll();
                        deleteValue = board[deleteCell];
                        board[deleteCell] = 0;
                    }

                    Integer gewinner = GewinnerCheck.checkForWinner(board);
                    if (gewinner != null) {
                        board[i] = 0;
                        if (deleteValue != null) {
                            board[deleteCell] = deleteValue;
                        }
                        if (recursionDepth == 0){
                            alleErgebnisse.clear();
                            alleErgebnisse.add(new ClassicErgebnis(gewinner, i));
                        }
                        return (maxRecursionDepth - recursionDepth) * gewinner;
                    }

                    int score = calculate(board, recursionDepth + 1, !isMinimizing, copyQueue);
                    // alle Änderungen zurücksetzen
                    board[i] = 0;
                    if (deleteValue != null) {
                        board[deleteCell] = deleteValue;
                        copyQueue.addFirst(deleteCell);
                    }
                    copyQueue.removeLast();

                    if (recursionDepth == 0 && score == besterWert) {
                        alleErgebnisse.add(new ClassicErgebnis(score, i));
                    }
                    // wenn bester Wert nicht erreicht wurde, aktuelle Situation zumindest maximieren
                    if (isMinimizing && score < besterWert) {
                        besterWert = score;
                        if (recursionDepth == 0) {
                            alleErgebnisse.clear();
                            alleErgebnisse.add(new ClassicErgebnis(score, i));
                        }
                    } else if (!isMinimizing && score > besterWert) {
                        besterWert = score;
                        if (recursionDepth == 0) {
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
        InfinityGameState state = (InfinityGameState) gameState;

        int unbenutzt = calculate(state.board, 0, state.player1, new LinkedList<>(state.queue));

        return alleErgebnisse.get((int) (Math.random() * alleErgebnisse.size())).zug;
    }
}
