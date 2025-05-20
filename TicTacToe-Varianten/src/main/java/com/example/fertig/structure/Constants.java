package com.example.fertig.structure;

import javafx.scene.paint.Color;

public class Constants {
    public static final int INNER_GRID_SIZE = 3;
    public static final int OUTER_GRID_SIZE = 3;

    public static final Color INNER_GRID_BG_COLOUR =  Color.TRANSPARENT;
    public static final String OUTER_GRID_BG_COLOUR =  "rgb(5, 5, 5)"; // aktuell gleich wie BUTTON_COLOR_INACTIVE
    public static final Color INNER_GRID_HOVER_COLOR = Color.rgb(101, 91, 150); // welches grid gespielt werden kann => nur bei meta-tictactoe
    public static final String BUTTON_COLOR_ACTIVE = "rgb(30, 30, 30)"; //meta ttt wo man setzen kann
    public static final String BUTTON_COLOR_HOVER = "rgb(75, 75, 75)"; //darüber fahren mit maus
    public static final String BUTTON_COLOR_INACTIVE = "rgb(50, 50, 50)"; //meta ttt nicht benutzbar
    public static final String BUTTON_BORDER_COLOR = "rgb(101, 91, 150)"; //umrandung der felder (classic tictactoe)
    public static final String NAVIGATE_BUTTON_COLOR = "rgb(208, 151, 255)"; //hintergrund (gerade lila)
    public static final String NAVIGATE_BUTTON_TEXT_COLOR = "rgb(5, 5, 5)";
    public static final String SLIDER_BG_COLOR = "rgb(224, 179, 255)"; //hintergrund slider
    public static final String SLIDER_BUTTON_COLOR = "rgb(170, 0, 255)"; // farbe des schiebereglers (knopf)
    public static final String ColorX = "rgb(100, 175, 212)";
    public static final String ColorO = "rgb(238, 138, 248)";
    public static final String ColorXVanish = "rgba(100, 175, 212, 0.4)"; // infinity tictactoe nach drei zügen verschwinden
    public static final String ColorOVanish = "rgba(238, 138, 248, 0.4)";
    public static final String ColorDefault = "rgb(255, 255, 255)";
    public static final String FinishedBgX = "rgba(100, 175, 212, 0.2)"; // meta tictactoe kästchen gewonnen
    public static final String FinishedBgO = "rgba(238, 138, 248, 0.2)";

    public static final int UNFINISHED = 10;
}
