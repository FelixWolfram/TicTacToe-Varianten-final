package com.example.fertig.classicTicTacToe;

import com.example.fertig.GameController;
import com.example.fertig.gameState.parameterWrapper.ClassicGameState;
import com.example.fertig.structure.Constants;
import com.example.fertig.structure.GewinnerCheck;
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
import javafx.scene.media.AudioClip;
import javafx.stage.Stage;
import javafx.scene.input.MouseEvent;

import java.io.IOException;
import java.util.Arrays;

public class ClassicController implements GameController {
    private AudioClip clickSound;
    private AudioClip gewonnenSound;
    private final Computer computer = new Computer();
    private boolean spielZuende = false;
    private float difficulty = 1;
    private boolean spieler1 = true;
    private boolean spieler1Anfang = spieler1;
    private boolean opponentComputer = false;
    private boolean tempOpponentComputer = opponentComputer;
    private final int[] boardValues = {
            0, 0, 0,
            0, 0, 0,
            0, 0, 0
    }; // 0 = noch frei, -1 = player1, 1 = player2

    @FXML private Button opponent;
    @FXML private Slider difficultySlider;
    @FXML private Button beginner;
    @FXML private TextField spielerZug;
    @FXML private TextField difficultyText;
    @FXML private Button reset;
    @FXML private Button back;
    @FXML private GridPane navigateGrid;
    @FXML private Button field0;
    @FXML private Button field1;
    @FXML private Button field2;
    @FXML private Button field3;
    @FXML private Button field4;
    @FXML private Button field5;
    @FXML private Button field6;
    @FXML private Button field7;
    @FXML private Button field8;

    private Button[] buttons;
    @FXML
    public void initialize() {
        buttons = new Button[]{
                field0, field1, field2,
                field3, field4, field5,
                field6, field7, field8
        };
        beginner.setText("Anfang: X");

        for (Button button : new Button[]{opponent, beginner, back, reset}) {
            button.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR +
                    "; -fx-border-color: " + Constants.BUTTON_BORDER_COLOR +
                    "; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR);
        }
        navigateGrid.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR);
        spielerZug.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR + "; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR);
        difficultyText.setStyle("-fx-background-color: TRANSPARENT; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR + ";");
        difficultySlider.setStyle("-fx-control-inner-background: " + Constants.SLIDER_BG_COLOR + "; -fx-base: " + Constants.SLIDER_BUTTON_COLOR + "; -fx-background-color: TRANSPARENT;");
        clickSound = new AudioClip(getClass().getResource("/media/click.wav").toExternalForm());
        gewonnenSound = new AudioClip(getClass().getResource("/media/explosion.wav").toExternalForm());

        resetBoard();
    }

    @FXML
    public void setField(ActionEvent event) {
        Button buttonClicked = ((Button) event.getSource());

        int cell = getButtonCell(buttonClicked);

        if (boardValues[cell] == 0 && !spielZuende) {
            setFieldValues(cell);
        }
        if (!spielZuende)
            computerZug();

        for (Button button : new Button[]{opponent, beginner, back, reset}) {
            button.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR +
                    "; -fx-border-color: " + Constants.BUTTON_BORDER_COLOR +
                    "; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR);
        }
        navigateGrid.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR);
        spielerZug.setStyle("-fx-background-color:" + Constants.NAVIGATE_BUTTON_COLOR + "; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR);
        difficultyText.setStyle("-fx-background-color: TRANSPARENT; -fx-text-fill: " + Constants.NAVIGATE_BUTTON_TEXT_COLOR + ";");
    }

    private int getButtonCell(Button buttonClicked) {
        Integer row = GridPane.getRowIndex(buttonClicked); // gibt null anstatt 0 zurück
        Integer col = GridPane.getColumnIndex(buttonClicked); // gibt null anstatt 0 zurück
        row = row == null ? 0 : row;
        col = col == null ? 0 : col;
        return row * 3 + col;
    }

    public String getButtonStyle(String backgroundColor, int cellValue){
        return getButtonStyle(backgroundColor, cellValue, false);
    }

    public String getButtonStyle(String backgroundColor, int cellValue, boolean hoverButton){
        String textColor;
        if (hoverButton){
            textColor = (spieler1 ? Constants.ColorXVanish : Constants.ColorOVanish);
        } else {
            textColor = switch (cellValue) {
                case -1 -> Constants.ColorX;
                case 1 -> Constants.ColorO;
                default -> Constants.ColorDefault;
            };
        }
        return  "-fx-background-color: " + backgroundColor + "; -fx-border-color: " + Constants.BUTTON_BORDER_COLOR
                + "; -fx-font-size: 50; " + "-fx-text-fill: " + textColor + "; -fx-border-width: 2;";
    }

    private void computerZug(){
        if (!spieler1 && opponentComputer) {
            int computerZug = computer.computerZug(new ClassicGameState(boardValues, spieler1));
            setFieldValues(computerZug);
        }
    }

    public void setFieldValues(int cell){
        boardValues[cell] = spieler1 ? -1: 1;
        buttons[cell].setText(spieler1 ? "X" : "O");
        buttons[cell].setStyle(getButtonStyle(Constants.BUTTON_COLOR_ACTIVE, boardValues[cell]));
        if (clickSound != null) {
            clickSound.play();
        }
        spieler1 = !spieler1;
        spielerZug.setText("Spieler " + (spieler1 ? "1 (X)" : "2 (O)") + " am Zug");

        gewinnerAktion(GewinnerCheck.checkForWinner(boardValues));
    }

    @FXML
    public void resetBoard(){
        for (int i = 0; i < boardValues.length; i++) {
            buttons[i].setText("");
            boardValues[i] = 0;
        }
        spieler1 = spieler1Anfang;
        spielerZug.setText("Spieler 1 (X) am Zug");
        spielZuende = false;
        opponentComputer = tempOpponentComputer;
        computer.difficulty = difficulty;
        for (Button button : buttons) {
            button.setStyle(getButtonStyle(Constants.BUTTON_COLOR_ACTIVE, 0)); // alle Zellen sind anfangs sowieso leer
        }

        computerZug(); // überprüft, ob der Computer am Anfang an der Reihe ist und macht einen Zug in diesem Fall
    }

    @FXML
    public void changePlayer() {
        tempOpponentComputer = !tempOpponentComputer;
        opponent.setText(tempOpponentComputer ? "PvE" : "PvP");

        // direkt den Wert ändern, falls das Spiel noch nicht begonnen hat. Andernfalls wird später in resetBoard geändert
        if (Arrays.stream(boardValues).allMatch(c -> c == 0)) {
            opponentComputer = tempOpponentComputer;
            computerZug();
        }
    }

    private void gewinnerAktion(Integer gewinner) {
        if (gewinner == null) {
            return;
        }
        spielZuende = true;
        if (gewinner == 0)
            spielerZug.setText("Unentschieden!");
        else if (gewinner == -1)
            spielerZug.setText("Spieler 1 hat gewonnen!");
        else if (gewinner == 1){
            spielerZug.setText("Spieler 2 hat gewonnen!");
        }
        if(gewonnenSound != null){
            gewonnenSound.play();
        }
    }

    @FXML
    public void anfangSpielerAendern(){
        spieler1Anfang = !spieler1Anfang;
        beginner.setText(spieler1Anfang ? "Anfang: X" : "Anfang: O");

        // direkt den Wert ändern, falls das Spiel noch nicht begonnen hat. Andernfalls wird später in resetBoard geändert
        if (Arrays.stream(boardValues).allMatch(c -> c == 0)) {
            spieler1 = spieler1Anfang;
            spielerZug.setText("Spieler " + (spieler1 ? "1 (X)" : "2 (O)") + " am Zug");
            computerZug();
        }
    }

    @FXML
    public void setNewDifficulty() {
        difficulty = (float) difficultySlider.getValue()/10;
        if (Arrays.stream(boardValues).allMatch(c -> c == 0)) {
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

    @FXML
    public void mouseEntered(MouseEvent event) {
        if (!spielZuende){
            Button hoveredButton = (Button) event.getSource();
            int cellValue = boardValues[getButtonCell(hoveredButton)];
            if (cellValue == 0) {
                hoveredButton.setStyle(getButtonStyle(Constants.BUTTON_COLOR_HOVER, cellValue, true));
                hoveredButton.setText(spieler1 ? "X" : "O");
            }
        }
    }

    @FXML
    public void mouseExited(MouseEvent event) {
        if (!spielZuende){
            Button hoveredButton = (Button) event.getSource();
            int cellValue = boardValues[getButtonCell(hoveredButton)];
            hoveredButton.setStyle(getButtonStyle(Constants.BUTTON_COLOR_ACTIVE, cellValue));
            if (cellValue == 0)
                hoveredButton.setText("");
        }
    }
}