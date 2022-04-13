package uk.ac.soton.comp1206.component;

import javafx.beans.property.SimpleListProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

/**
 * A multiplayer variant of the Scores List
 */
public class Leaderboard extends ScoresList{
    /**
     * Create a new game with the specified rows and columns. Creates a corresponding grid model.
     *
     * @param scores list of scores (paired with names)
     */
    public Leaderboard(SimpleListProperty<Pair<SimpleStringProperty, Integer>> scores) {
        super(scores);
    }
}
