package com.example.fertig.structure;

import com.example.fertig.metaTicTacToe.support.CurrentGameState;

public class GewinnerCheck extends CurrentGameState {
    // wird benutzt, um die einzelnen (kleinen) Grids auf einen Gewinner zu überprüfen
    public static Integer checkForWinner(int checkGridCell){
        return checkForWinner(boardValues[checkGridCell], 0);
    }

    public static Integer checkForWinner(int[] board){
        return checkForWinner(board, 0);
    }

    public static Integer checkForWinner(int[] gridBoardValues, int unfinished) {
        // da der Check für das globale Grid einige anderen Anforderungen benötigt, ist das hier aktuell ggf. nicht optimal gelöst
        // beim lokalen Grid wird zweimal auf != 0 überprüft, aber ohne das wird beim globalen Grid ansonsten, wenn 3 Unentschieden
        // in einer Reihe/Spalte/Diagonal sind, 0 zurückgegeben → soll aber nur bei einem Unentschieden zurückgegeben werden
        for (int i = 0; i < Constants.INNER_GRID_SIZE; i++) {
            // horizontal überprüfen
            if (gridBoardValues[i*3] == gridBoardValues[i*3+1] && gridBoardValues[i*3+1] == gridBoardValues[i*3+2] && gridBoardValues[i*3] != unfinished && gridBoardValues[i*3] != 0) {
                return gridBoardValues[i*3];
            }
            // vertikal überprüfen
            else if (gridBoardValues[i] == gridBoardValues[i+3] && gridBoardValues[i+3] == gridBoardValues[i+6] && gridBoardValues[i] != unfinished && gridBoardValues[i] != 0) {
                return gridBoardValues[i];
            }
        }
        // diagonal überprüfen
        if ((gridBoardValues[0] == gridBoardValues[4] && gridBoardValues[4] == gridBoardValues[8] && gridBoardValues[4] != unfinished && gridBoardValues[4] != 0) ||
                (gridBoardValues[2] == gridBoardValues[4] && gridBoardValues[4] == gridBoardValues[6] && gridBoardValues[4] != unfinished && gridBoardValues[4] != 0)) {
            return gridBoardValues[4];
        }
        // Spiel noch nicht beendet
        for (int cell: gridBoardValues)
            if (cell == unfinished)
                return null;
        // Unentschieden
        return 0;
    }
}
