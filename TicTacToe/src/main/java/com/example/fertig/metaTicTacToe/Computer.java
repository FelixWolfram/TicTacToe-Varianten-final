package com.example.fertig.metaTicTacToe;

import com.example.fertig.gameState.GameState;
import com.example.fertig.gameState.MetaComputerInterface;
import com.example.fertig.gameState.parameterWrapper.MetaGameState;
import com.example.fertig.structure.Constants;
import com.example.fertig.structure.MetaErgebnis;
import com.example.fertig.structure.GewinnerCheck;

import java.util.ArrayList;
import java.util.List;

public class Computer implements MetaComputerInterface {
    int maxRecursionDepth = 9;
    int bigWinPoints = 1000000;
    public float difficulty = 1;
    static final int[][] winLines = {
            {0, 1, 2}, {3, 4, 5}, {6, 7, 8},
            {0, 3, 6}, {1, 4, 7}, {2, 5, 8},
            {0, 4, 8}, {2, 4, 6}
    };

    // player1 = -1 = Human, !player1 = 1 = Computer, 0 = Draw
    private int calculate(int[][] board, int[] bigWins, int recursionDepth, boolean isMinimizing, int currentActiveGrid, int alpha, int beta) {
        // Gewinner für komplettes Board überprüfen → könnte auch in die Heuristik-Funktion gemacht werden
        Integer bigGewinner = GewinnerCheck.checkForWinner(bigWins, Constants.UNFINISHED);
        if (bigGewinner != null) {
            return (bigWinPoints - recursionDepth) * bigGewinner;
        }
        // wenn die maximale Rekursionstiefe erreicht ist, soll eine Evaluation der aktuellen Situation erstellt und zurückgegeben werden
        if (recursionDepth >= Math.round(maxRecursionDepth * difficulty)) {
            return evaluateCurrentPosition(board, bigWins, currentActiveGrid);
        }

        if (currentActiveGrid != -1 && bigWins[currentActiveGrid] != Constants.UNFINISHED) {
            currentActiveGrid = -1;
        }

        int startGrid = (currentActiveGrid == -1 ? 0 : currentActiveGrid);
        int checkAmountGrids = (currentActiveGrid == -1 ? 9 : 1);
        ArrayList<int[]> validMoves = new ArrayList<>();
        for (int i = startGrid; i < startGrid + checkAmountGrids; i++) {
            if (bigWins[i] == Constants.UNFINISHED) {
                for (int j = 0; j < Constants.INNER_GRID_SIZE * Constants.INNER_GRID_SIZE; j++) {
                    if (board[i][j] == 0) {
                        validMoves.add(new int[]{i, j});
                    }
                }
            }
        }

        int besterWert = (isMinimizing ? Integer.MAX_VALUE : Integer.MIN_VALUE);
        for (int[] move : validMoves) {
            int g = move[0];
            int l = move[1];

            board[g][l] = isMinimizing ? -1 : 1;
            Integer gewinner = GewinnerCheck.checkForWinner(board[g], 0);
            if (gewinner != null) {
                bigWins[g] = gewinner;
            }

            int score = calculate(board, bigWins, recursionDepth + 1, !isMinimizing, l, alpha, beta);
            resetMoves(board, bigWins, g, l);

            if (recursionDepth == 0)
                System.out.println(score);

            // wenn bestmöglicher Wert (kompletter Gewinn) nicht erreicht wurde, aktuelle Situation zumindest maximieren
            if (isMinimizing) {
                besterWert = Math.min(besterWert, score);
                beta = Math.min(besterWert, beta);
                if (beta <= alpha) {
                    break;
                }
            } else {
                besterWert = Math.max(besterWert, score);
                alpha = Math.max(besterWert, alpha);
                if (beta <= alpha) {
                    break;
                }
            }
        }
        return besterWert;
    }

    // es wurde jetzt eine extra Schleife für computer Zug hinzugefügt, damit die "grundlegenden" Züge nicht mehr in der calculate-Methode gemacht werden
    // es wird alpha und beta dabei jeweils auf den schlechtesten Wert gesetzt
    // andererseits kommt es zu Fehlern, da alpha-beta-Pruning nicht darauf ausgelegt ist, mehrere gleich gute, gewinnende Züge zu finden
    public int[] computerZug(GameState gameState) {
        ArrayList<MetaErgebnis> alleErgebnisse = new ArrayList<>();
        MetaGameState state = (MetaGameState) gameState;

        int[][] board = state.board;
        int[] bigWins = state.bigWins;
        int currentActiveGrid = state.currentActiveGrid;
        boolean player1 = state.player1;

        // Für jeden möglichen Zug prüfen, ob er zum Gewinn führt
        int startGrid = (currentActiveGrid == -1 ? 0 : currentActiveGrid);
        int checkAmountGrids = (currentActiveGrid == -1 ? 9 : 1);

        if (currentActiveGrid != -1 && bigWins[currentActiveGrid] != Constants.UNFINISHED) {
            startGrid = 0;
            checkAmountGrids = 9;
        }

        // Sammle zunächst alle gültigen Züge
        ArrayList<int[]> validMoves = new ArrayList<>();
        for (int i = startGrid; i < startGrid + checkAmountGrids; i++) {
            if (bigWins[i] == Constants.UNFINISHED) {
                for (int j = 0; j < Constants.INNER_GRID_SIZE * Constants.INNER_GRID_SIZE; j++) {
                    if (board[i][j] == 0) {
                        validMoves.add(new int[]{i, j});
                    }
                }
            }
        }

        // verschiedene Threads für die Berechnung der Züge zuweisen und starten
        Thread[] threads = new Thread[validMoves.size()];
        for (int t = 0; t < validMoves.size(); t++) {
            final int index = t;

            threads[t] = new Thread(() -> {
                int g = validMoves.get(index)[0];
                int l = validMoves.get(index)[1];

                int[][] boardCopy = deepCopyBoard(board);
                int[] bigWinsCopy = bigWins.clone();

                boardCopy[g][l] = player1 ? -1 : 1;
                Integer gewinner = GewinnerCheck.checkForWinner(boardCopy[g], 0);
                if (gewinner != null) {
                    bigWinsCopy[g] = gewinner;
                }

                // Score berechnen (mit vollem Pruning
                int score = calculate(boardCopy, bigWinsCopy, 1, !player1, l, Integer.MIN_VALUE, Integer.MAX_VALUE);

                alleErgebnisse.add(new MetaErgebnis(score, new int[]{g, l}));
                System.out.println("Zug: [" + g + ", " + l + "] Score: " + score);
            });

            threads[t].start();
        }

        // auf alle Threads warten
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        if (alleErgebnisse.isEmpty()) {
            // Falls keine guten Züge gefunden wurden, wird irgendeinen gültiger Zug genommen
            return validMoves.get((int) (Math.random() * validMoves.size()));
        } else {
            // es wird nach dem besten Zug gefiltert und einer davon zurückgegeben
            // zufällige Auswahl aus den besten Zügen, das sonst (vor allem am Anfang) der Computer meistens den gleichen Zug macht
            int maxScore = alleErgebnisse.stream()
                    .mapToInt(e -> e.gewinner)
                    .max()
                    .orElse(Integer.MIN_VALUE);
            ;
            List<MetaErgebnis> besteZuege = alleErgebnisse.stream().
                    filter(e -> e.gewinner == maxScore).
                    toList();
            return besteZuege.get((int) (Math.random() * besteZuege.size())).zug;
        }
    }


    public static int[][] deepCopyBoard(int[][] original) {
        if (original == null) return null;
        int[][] copy = new int[original.length][];
        for (int i = 0; i < original.length; i++) {
            // Sicherstellen, dass jede Zeile (inneres Array) separat kopiert wird!
            copy[i] = original[i].clone(); // clone() ist für 1D int[] okay
        }
        return copy;
    }

    private void resetMoves(int[][] board, int[] bigWins, int outerGrid, int innerGrid) {
        bigWins[outerGrid] = Constants.UNFINISHED;
        board[outerGrid][innerGrid] = 0;
    }

    private int evaluateCurrentPosition(int[][] board, int[] bigWins, int currentActiveGrid) {
        int value = 0;
        // 1. Punkte für gewonnene kleine Grids (+1000 für Computer, -1000 für Gegner)
        for (int i = 0; i < 9; i++) {
            if (bigWins[i] == 1) {
                value += 1000;  // Computer hat diesen kleinen Grid gewonnen
                // Extrapunkte für strategische Positionen
                if (i == 4) {
                    value += 300;
                } else if (i == 0 || i == 2 || i == 6 || i == 8) {
                    value += 200;
                }
            } else if (bigWins[i] == -1) {
                value -= 1000;  // Gegner hat gewonnen
                if (i == 4) {
                    value -= 300;
                } else if (i == 0 || i == 2 || i == 6 || i == 8) {
                    value -= 200;
                }
            } else if (bigWins[i] == Constants.UNFINISHED) {
                int[] smallBoard = board[i];
                for (int[] line : winLines) {
                    int countComp = 0, countOpp = 0;
                    for (int idx : line) {
                        if (smallBoard[idx] == 1) countComp++;
                        if (smallBoard[idx] == -1) countOpp++;
                    }
                    if (countComp == 2 && countOpp == 0) value += 40;
                    if (countOpp == 2 && countComp == 0) value -= 40;
                }
            }
        }
        // 2. Bewertung potenzieller Gewinnlinien im großen Grid
        for (int[] line : winLines) {
            int countComp = 0, countOpp = 0, countDraw = 0;
            for (int idx : line) {
                if (bigWins[idx] == 1) countComp++;
                if (bigWins[idx] == -1) countOpp++;
                if (bigWins[idx] == 0) countDraw++;
            }
            if (countDraw > 0) continue;
            if (countComp == 2 && countOpp == 0) value += 200;
            if (countOpp == 2 && countComp == 0) value -= 200;
        }


        // 3) Ein-Stein-Bewertung für offene kleine Grids:
        //    +5 für Computer-Stein im Zentrum, –5 für Gegener
        //    +3 für Computer-Stein in Ecke, –3 für Gegener
        int[] corners = {0, 2, 6, 8};
        for (int i = 0; i < 9; i++) {
            if (bigWins[i] == Constants.UNFINISHED) {
                int[] sb = board[i];
                // Zentrum
                if (sb[4] == 1) value += 5;
                else if (sb[4] == -1) value -= 5;
                // Ecken
                for (int c : corners) {
                    if (sb[c] == 1) value += 3;
                    else if (sb[c] == -1) value -= 3;
                }
            }
        }

        // 4) Berücksichtigung des nächsten aktiven Grids:
        //    Wenn der Gegner im kommenden Grid eine "2-in-Linie" hat, Strafe −50
        //    Wenn wir selber im kommenden Grid eine "2-in-Linie" haben, Bonus +50
        if (currentActiveGrid >= 0
                && bigWins[currentActiveGrid] == Constants.UNFINISHED) {
            int[] next = board[currentActiveGrid];
            for (int[] line : winLines) {
                int cComp = 0, cOpp = 0;
                for (int idx : line) {
                    if (next[idx] == 1) cComp++;
                    else if (next[idx] == -1) cOpp++;
                }
                if (cComp == 2 && cOpp == 0) value += 50;
                else if (cOpp == 2 && cComp == 0) value -= 50;
            }
        }

        return value;
    }
}
