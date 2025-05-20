package com.example.fertig.metaTicTacToe.support;

import com.example.fertig.structure.Constants;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.util.ArrayList;


public class GridStructure extends CurrentGameState {
    public ArrayList<GridPane> innerGrids = new ArrayList<>();
    public ArrayList<ArrayList<Button>> buttons = new ArrayList<>();
    public ArrayList<Text> winnerTexts = new ArrayList<>();

    private final BackgroundFill bgColor = new BackgroundFill(Constants.INNER_GRID_BG_COLOUR, CornerRadii.EMPTY, Insets.EMPTY);
    public final Background innerGridBg = new Background(bgColor);

    private final EventHandler<ActionEvent> buttonAction;

    public GridStructure(EventHandler<ActionEvent> buttonAction) {
        this.buttonAction = buttonAction;
    }

    public Background newBackground(Color color) {
        return new Background(new BackgroundFill(color, CornerRadii.EMPTY, Insets.EMPTY));
    }

    public String buttonStyle(String buttonColor, String textColor){
        return "-fx-background-color:" + buttonColor + "; -fx-border-radius: 0; -fx-border-width: 1; " +
                "-fx-border-color: GRAY; -fx-font-size: 32; -fx-padding: 0; -fx-text-fill: " + textColor + ";";
    }

    public String buttonStyle(String buttonColor, int outIndex, int inIndex, boolean hoverPlayer){
        String color;
        if (hoverPlayer) {
            color = (spieler1 ? Constants.ColorXVanish : Constants.ColorOVanish);
        } else {
            switch (boardValues[outIndex][inIndex]) {
                case -1 -> color = Constants.ColorX;
                case 1 -> color = Constants.ColorO;
                default -> color = Constants.ColorDefault;
            }
        }
        return buttonStyle(buttonColor, color);
    }

    public String buttonStyle(String buttonColor, int outIndex, int inIndex){
        return buttonStyle(buttonColor, outIndex, inIndex, false);
    }

    public void createGrids(GridPane outerGrid){
        for (int outerRow = 0; outerRow < Constants.OUTER_GRID_SIZE; outerRow++) {
            for (int outerCol = 0; outerCol < Constants.OUTER_GRID_SIZE; outerCol++) {

                GridPane innerGrid = createInnerGrid();
                innerGrids.add(innerGrid);

                Text overlayText = new Text();
                overlayText.setFill(Paint.valueOf(Constants.ColorDefault));
                overlayText.setScaleX(2.2);
                overlayText.setScaleY(2.2);
                overlayText.setFont(Font.font(100));
                overlayText.setMouseTransparent(true);
                winnerTexts.add(overlayText);

                StackPane innerCellPane = new StackPane(innerGrid, overlayText);

                outerGrid.add(innerCellPane, outerCol, outerRow);

                // create and save Buttons
                ArrayList<Button> innerButtons = addAllButtons(innerGrid, outerRow, outerCol);
                buttons.add(innerButtons);
            }
        }
    }

    private  ArrayList<Button> addAllButtons(GridPane innerGrid, int outerRow, int outerCol) {
        ArrayList<Button> innerButtons = new ArrayList<>();
        for (int innerRow = 0; innerRow < Constants.INNER_GRID_SIZE; innerRow++) {
            for (int innerCol = 0; innerCol < Constants.INNER_GRID_SIZE; innerCol++) {
                final int outerGridNumber = outerRow * Constants.OUTER_GRID_SIZE + outerCol;
                final int innerGridNumber = innerRow * Constants.INNER_GRID_SIZE + innerCol;

                Button btn = new Button();
                btn.setStyle(buttonStyle(Constants.BUTTON_COLOR_ACTIVE, outerGridNumber, innerGridNumber));

                // hover Effekt für die Buttons
                btn.setOnMouseEntered((e) -> {
                    if (!gameOver) {
                        if ((activeGrid == outerGridNumber || activeGrid == -1)
                                && bigWins[outerGridNumber] == Constants.UNFINISHED
                                && boardValues[outerGridNumber][innerGridNumber] == 0) {
                            btn.setStyle(buttonStyle(Constants.BUTTON_COLOR_HOVER, outerGridNumber, innerGridNumber, true));
                            btn.setText(spieler1 ? "X" : "O");

                            if (bigWins[innerGridNumber] == Constants.UNFINISHED && boardValues[outerGridNumber][innerGridNumber] == 0) {
                                innerGrids.get(innerGridNumber).setBackground(newBackground(Constants.INNER_GRID_HOVER_COLOR));
                            }
                        }
                    }
                });
                btn.setOnMouseExited(e -> {
                    if (!gameOver) {
                        if ((activeGrid == outerGridNumber || activeGrid == -1)
                                && bigWins[outerGridNumber] == Constants.UNFINISHED) {
                            btn.setStyle(buttonStyle(Constants.BUTTON_COLOR_ACTIVE, outerGridNumber, innerGridNumber));
                            if (boardValues[outerGridNumber][innerGridNumber] == 0) {
                                btn.setText("");
                            }
                        } else if (bigWins[outerGridNumber] == Constants.UNFINISHED) {
                            btn.setStyle(buttonStyle(Constants.BUTTON_COLOR_INACTIVE, outerGridNumber, innerGridNumber));
                        }
                        if (bigWins[innerGridNumber] == Constants.UNFINISHED) {
                            innerGrids.get(innerGridNumber).setBackground(innerGridBg);
                        }
                    }
                });
                btn.setOnAction(this.buttonAction);

                // Buttons sollen die komplette Grid-Zelle ausfüllen
                GridPane.setHgrow(btn, Priority.ALWAYS);
                GridPane.setVgrow(btn, Priority.ALWAYS);
                btn.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);

                String id = String.format("btn_%d_%d", outerRow * 3 + outerCol, innerRow * 3 + innerCol);
                btn.setId(id);

                innerGrid.add(btn, innerCol, innerRow);
                innerButtons.add(btn);
            }
        }
        return innerButtons;
    }

    private GridPane createInnerGrid(){
        GridPane innerGrid = new GridPane();
        innerGrid.setBackground(innerGridBg);
        innerGrid.setHgap(3);
        innerGrid.setVgap(3);
        innerGrid.setPadding(new Insets(3, 3, 3, 3));

        // Columns Constraints damit die Breite sich nicht automatisch dem Inhalt anpasst
        for (int i = 0; i < 3; i++) {
            ColumnConstraints col = new ColumnConstraints();
            col.setPercentWidth(100.0 / 3);
            col.setHgrow(Priority.ALWAYS);
            innerGrid.getColumnConstraints().add(col);

            RowConstraints row = new RowConstraints();
            row.setPercentHeight(100.0 / 3);
            row.setVgrow(Priority.ALWAYS);
            innerGrid.getRowConstraints().add(row);
        }
        return innerGrid;
    }
}
