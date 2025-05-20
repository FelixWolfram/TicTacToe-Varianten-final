module com.example.fertig {
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;              // <<<< Hier ergänzt

    opens com.example.fertig to javafx.fxml;
    exports com.example.fertig;

    opens com.example.fertig.classicTicTacToe to javafx.fxml;
    exports com.example.fertig.classicTicTacToe;

    opens com.example.fertig.infinityTicTacToe to javafx.fxml;
    exports com.example.fertig.infinityTicTacToe;

    opens com.example.fertig.metaTicTacToe to javafx.fxml;
    exports com.example.fertig.metaTicTacToe;

    opens com.example.fertig.metaTicTacToe.support to javafx.fxml;
    exports com.example.fertig.metaTicTacToe.support;
}
