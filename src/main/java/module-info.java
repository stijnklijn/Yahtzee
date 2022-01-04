module nl.stijnklijn.yahtzee {
    requires javafx.controls;
    requires javafx.fxml;


    opens nl.stijnklijn.yahtzee to javafx.fxml;
    exports nl.stijnklijn.yahtzee;
}