package com.example.fertig.metaTicTacToe;

import com.example.fertig.GameController;
import com.example.fertig.gameState.parameterWrapper.MetaGameState;
import com.example.fertig.metaTicTacToe.support.CurrentGameState;
import com.example.fertig.metaTicTacToe.support.GridStructure;
import com.example.fertig.structure.Constants;
import com.example.fertig.structure.GewinnerCheck;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.util.Arrays;

public class MetaController extends CurrentGameState implements GameController {
    private final Computer computer = new Computer();
    private float difficulty = 0;
    private boolean spieler1Anfang = spieler1;
    private boolean opponentComputer = false;
    private boolean tempOpponentComputer = opponentComputer;
    private final GridStructure grids = new GridStructure(this::setField);
    private final GewinnerCheck gewinnerCheck = new GewinnerCheck();
    private AudioClip clickSound;
    private AudioClip gewonnenSound;


    @FXML private Button opponent;
    @FXML private Button beginner;
    @FXML private TextField spielerZug;
    @FXML private GridPane outerGrid;
    @FXML private Slider difficultySlider;
    @FXML private TextField difficultyText;
    @FXML private Button back;
    @FXML private Button reset;
    @FXML private GridPane navigateGrid;

    @FXML
    public void initialize() {
        for (int i = 0; i < 9; i++) {
            bigWins[i] = Constants.UNFINISHED;
            for (int j = 0; j < 9; j++)
                boardValues[i][j] = 0; // 0 = noch frei, -1 = player1, 1 = player2
        }
        grids.createGrids(outerGrid);
        beginner.setText("Anfang: X");
        spielerZug.setText("Spieler " + (spieler1 ? "1 (X)" : "2 (O)") + " am Zug");

        for (Button button : new Button[]{opponent, beginner, back, reset}) {
            button.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR +
                    "; -fx-border-color: " + Constants.BUTTON_BORDER_COLOR +
                    "; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR);
        }
        navigateGrid.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR);
        spielerZug.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR + "; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR);
        difficultyText.setStyle("-fx-background-color: TRANSPARENT; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR + ";");
        difficultySlider.setStyle("-fx-control-inner-background: " + Constants.SLIDER_BG_COLOR +
                "; -fx-base: " + Constants.SLIDER_BUTTON_COLOR +
                "; -fx-background-color: TRANSPARENT;");
        outerGrid.setStyle("-fx-background-color: " + Constants.OUTER_GRID_BG_COLOUR + ";");

        outerGrid.sceneProperty().addListener((obs, oldScene, newScene) -> {
            if (newScene != null) {
                Platform.runLater(() -> {
                    Stage stage = (Stage) newScene.getWindow();
                    stage.centerOnScreen();
                });
            }
        });
        clickSound = new AudioClip(getClass().getResource("/media/click.wav").toExternalForm());
        gewonnenSound = new AudioClip(getClass().getResource("/media/explosion.wav").toExternalForm());

        resetGameState();
        resetBoard();
    }

    @FXML
    public void setField(ActionEvent event) {
        Button buttonClicked = ((Button) event.getSource());

        // Position im äußeren Grid
        GridPane innerGrid = (GridPane) buttonClicked.getParent();
        StackPane gridPane = (StackPane) innerGrid.getParent();
        Integer outerGridRow = GridPane.getRowIndex(gridPane);
        Integer outerGridCol = GridPane.getColumnIndex(gridPane);

        outerGridRow = outerGridRow == null ? 0 : outerGridRow;
        outerGridCol = outerGridCol == null ? 0 : outerGridCol;
        int outerGridIndex = outerGridRow * 3 + outerGridCol;

        if (outerGridIndex == activeGrid || activeGrid == -1) {
            // Position im inneren Grid
            Integer innerGridRow = GridPane.getRowIndex(buttonClicked); // gibt null anstatt 0 zurück
            Integer innerGridCol = GridPane.getColumnIndex(buttonClicked); // gibt null anstatt 0 zurück
            innerGridRow = innerGridRow == null ? 0 : innerGridRow;
            innerGridCol = innerGridCol == null ? 0 : innerGridCol;
            int innerGridIndex = innerGridRow * 3 + innerGridCol;

            if (boardValues[outerGridIndex][innerGridIndex] == 0 && !gameOver && bigWins[outerGridIndex] == Constants.UNFINISHED) {
                setFieldValues(outerGridIndex, innerGridIndex);
            }
            if (!gameOver)
                computerZug();
        }
    }

    private void setFieldValues(int outerGridCell, int innerGridCell){
        grids.innerGrids.get(innerGridCell).setBackground(grids.innerGridBg);
        grids.buttons.get(outerGridCell).get(innerGridCell).setText(spieler1 ? "X" : "O");
        String textColor = spieler1 ? Constants.ColorX : Constants.ColorO;
        grids.buttons.get(outerGridCell).get(innerGridCell).setStyle(grids.buttonStyle(Constants.BUTTON_COLOR_HOVER, textColor));
        boardValues[outerGridCell][innerGridCell] = spieler1 ? -1: 1;
        spieler1 = !spieler1;
        spielerZug.setText("Spieler " + (spieler1 ? "1 (X)" : "2 (O)") + " am Zug");

        Integer gewinner = gewinnerCheck.checkForWinner(outerGridCell);
        if (gewinner != null){
            bigWins[outerGridCell] = gewinner;
            grids.winnerTexts.get(outerGridCell).setText(gewinner == -1 ? "X" : gewinner == 1 ? "O" : "");
            grids.winnerTexts.get(outerGridCell).setFill(Paint.valueOf(gewinner == -1 ? Constants.ColorX : Constants.ColorO));

            // muss kurz entfernt werden, da es ansonsten (wenn das Ziel-Grid das gleiche wie das aktuelle ist) zu einem (visuellen) Problem führen kann
            grids.innerGrids.get(outerGridCell).setBackground(grids.newBackground(Constants.INNER_GRID_BG_COLOUR));
            gewinnerAktion(gewinnerCheck.checkForWinner(bigWins, Constants.UNFINISHED));
        }
        if (bigWins[innerGridCell] == Constants.UNFINISHED) {
            activeGrid = innerGridCell;
        }
        else {
            activeGrid = -1;
        }
        farbigFokusSetzen();
        if (clickSound != null) {
            clickSound.play();
        }
    }

    private void alleButtonsAktivSetzen(){
        // alle Felder auf die aktive Farbe setzen
        for (int i = 0; i < grids.buttons.size(); i++){
            for (int j = 0; j < grids.buttons.get(i).size(); j++){
                grids.buttons.get(i).get(j).setStyle(grids.buttonStyle(Constants.BUTTON_COLOR_ACTIVE, i, j));
            }
        }
    }

    private void farbigFokusSetzen() {
        // alle anderen Felder ausgrauen
        for (int i = 0; i < grids.buttons.size(); i++){
            for (int j = 0; j < grids.buttons.get(i).size(); j++){
                String color;
                if (bigWins[i] != Constants.UNFINISHED && bigWins[i] != 0) {
                    color = bigWins[i] == -1 ? Constants.FinishedBgX : Constants.FinishedBgO;
                }
                else if ((i == activeGrid || activeGrid == -1) && bigWins[i] == Constants.UNFINISHED && !gameOver)
                    color = Constants.BUTTON_COLOR_ACTIVE;
                else
                    color = Constants.BUTTON_COLOR_INACTIVE;
                grids.buttons.get(i).get(j).setStyle(grids.buttonStyle(color, i, j));
            }
        }
    }

    private void computerZug(){
        if (!spieler1 && opponentComputer) {
            int[] computerZug = computer.computerZug(new MetaGameState(boardValues, bigWins, spieler1, activeGrid));
            System.out.println("entgültiger Zug des Computers: " + computerZug[0] + " " + computerZug[1]);
            setFieldValues(computerZug[0], computerZug[1]);
        }
    }

    @FXML
    public void resetBoard(){
        resetGameState();
        for (int i = 0; i < boardValues.length; i++) {
            bigWins[i] = Constants.UNFINISHED;
            for (int j = 0; j < boardValues[i].length; j++) {
                grids.buttons.get(i).get(j).setText("");
                boardValues[i][j] = 0;
            }
        }
        spieler1 = spieler1Anfang;
        spielerZug.setText("Spieler " + (spieler1 ? "1 (X)" : "2 (O)") + " am Zug");
        gameOver = false;
        opponentComputer = tempOpponentComputer;
        activeGrid = -1;
        for (Text text: grids.winnerTexts) {
            text.setText("");
        }
        alleButtonsAktivSetzen();

        computerZug(); // überprüft, ob der Computer am Anfang an der Reihe ist und macht einen Zug in diesem Fall
    }

    @FXML
    public void changePlayer() {
        tempOpponentComputer = !tempOpponentComputer;
        opponent.setText(tempOpponentComputer ? "PvE" : "PvP");

        // direkt den Wert ändern, falls das Spiel noch nicht begonnen hat. Andernfalls wird später in resetBoard geändert
        if (Arrays.stream(boardValues).allMatch(r -> Arrays.stream(r).allMatch(c -> c == 0))) {
            opponentComputer = tempOpponentComputer;
            computerZug();
        }
    }

    private void gewinnerAktion(Integer gewinner) {
        if (gewinner == null) {
            // Spiel ist nicht beendet oder 3 Unentschieden-Grids sind in einer Reihe sind → Spiel soll nicht beendet werden
            return;
        }
        gameOver = true;
        if (gewinner == -1){
            spielerZug.setText("Spieler 1 hat gewonnen!");}
        else if (gewinner == 1){
            spielerZug.setText("Spieler 2 hat gewonnen!");}
        else {
            spielerZug.setText("Unentschieden!");}
        farbigFokusSetzen();
        if (gewonnenSound != null){
            gewonnenSound.play();
        }
    }

    @FXML
    public void anfangSpielerAendern(){
        spieler1Anfang = !spieler1Anfang;
        beginner.setText(spieler1Anfang ? "Anfang: X" : "Anfang: O");

        // direkt den Wert ändern, falls das Spiel noch nicht begonnen hat. Andernfalls wird später in resetBoard geändert
        if (Arrays.stream(boardValues).allMatch(r -> Arrays.stream(r).allMatch(c -> c == 0))) {
            spieler1 = spieler1Anfang;
            spielerZug.setText("Spieler " + (spieler1 ? "1 (X)" : "2 (O)") + " am Zug");
            computerZug();
        }
    }

    @FXML
    public void setNewDifficulty() {
        difficulty = (float) difficultySlider.getValue()/10;
        if (Arrays.stream(boardValues).allMatch(r -> Arrays.stream(r).allMatch(c -> c == 0))) {
            computer.difficulty = difficulty;
            computerZug();
        }
    }

    @FXML
    public void toMainMenu(ActionEvent event) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/example/fertig/start-view.fxml"));
        Parent root = loader.load();

        Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}