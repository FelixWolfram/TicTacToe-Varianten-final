package com.example.fertig;

import javafx.event.ActionEvent;

import java.io.IOException;

public interface GameController {
    void setField(ActionEvent event);

    void resetBoard();

    void changePlayer();

    void anfangSpielerAendern();

    void setNewDifficulty();

    void toMainMenu(ActionEvent event) throws IOException;
}