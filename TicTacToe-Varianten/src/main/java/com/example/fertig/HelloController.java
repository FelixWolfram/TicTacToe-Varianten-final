package com.example.fertig;

import javafx.animation.FadeTransition;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.AudioClip;
import javafx.scene.media.MediaPlayer;
import javafx.scene.media.MediaView;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    @FXML private MediaView mediaView;
    @FXML private VBox mainContainer;
    @FXML private Button classicButton;
    @FXML private Button infinityButton;
    @FXML private Button metaButton;
    @FXML private Button startButton;
    private String selectedVariant;
    private AudioClip clickSound1;
    private AudioClip clickSound2;
    private AudioClip startup;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        String mediaPath = Objects.requireNonNull(getClass().getResource("/media/backgroundv2.mp4")).toExternalForm();
        MediaPlayer player = new MediaPlayer(new Media(mediaPath));
        player.setCycleCount(MediaPlayer.INDEFINITE);
        player.setAutoPlay(true);
        mediaView.setMediaPlayer(player);
        mediaView.setOpacity(0);
        clickSound1 = new AudioClip(Objects.requireNonNull(getClass().getResource("/media/click.wav")).toExternalForm());
        clickSound2 = new AudioClip(Objects.requireNonNull(getClass().getResource("/media/click2.wav")).toExternalForm());
        startup = new AudioClip(Objects.requireNonNull(getClass().getResource("/media/startup.mp3")).toExternalForm());
        classicButton.setOpacity(0);
        infinityButton.setOpacity(0);
        metaButton.setOpacity(0);
        startButton.setOpacity(0);
        for (Button b : new Button[]{classicButton, infinityButton, metaButton}) {
            b.getStyleClass().addAll("outline", "small");
            b.setOnAction(e -> {
                clickSound1.play();
                selectedVariant = b.getText();
                classicButton.getStyleClass().remove("selected");
                infinityButton.getStyleClass().remove("selected");
                metaButton.getStyleClass().remove("selected");
                b.getStyleClass().add("selected");
            });
        }
        player.setOnReady(() -> Platform.runLater(this::playIntro));
    }

    private void playIntro() {
        startup.play();
        FadeTransition ftVideo = new FadeTransition(Duration.seconds(2.75), mediaView);
        ftVideo.setFromValue(0);
        ftVideo.setToValue(1);
        ftVideo.play();
        ftVideo.setOnFinished(e -> fadeButtons());
    }

    private void fadeButtons() {
        FadeTransition ft1 = new FadeTransition(Duration.seconds(0.75), classicButton);
        ft1.setFromValue(0);
        ft1.setToValue(1);
        FadeTransition ft2 = new FadeTransition(Duration.seconds(0.75), infinityButton);
        ft2.setFromValue(0);
        ft2.setToValue(1);
        FadeTransition ft3 = new FadeTransition(Duration.seconds(0.75), metaButton);
        ft3.setFromValue(0);
        ft3.setToValue(1);
        ft1.play();
        ft1.setOnFinished(e -> ft2.play());
        ft2.setOnFinished(e -> ft3.play());
        ft3.setOnFinished(e -> fadeStartButton());
    }

    private void fadeStartButton() {
        FadeTransition ftStart = new FadeTransition(Duration.seconds(1), startButton);
        ftStart.setFromValue(0);
        ftStart.setToValue(1);
        ftStart.play();
    }

    @FXML
    private void startGame(ActionEvent event) throws Exception {
        clickSound2.play();
        String variant = selectedVariant != null ? selectedVariant : "Klassisches TicTacToe";
        String fxml = switch (variant) {
            case "Infinity-TicTacToe" -> "/com/example/fertig/infinityFXML/infinity-tictactoe.fxml";
            case "Meta-TicTacToe" -> "/com/example/fertig/metaFXML/meta-tictactoe.fxml";
            default -> "/com/example/fertig/classicFXML/classic-tictactoe.fxml";
        };
        FXMLLoader loader = new FXMLLoader(getClass().getResource(fxml));
        Parent root = loader.load();
        Stage stage = (Stage) mainContainer.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }
}