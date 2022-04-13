package uk.ac.soton.comp1206.utilities;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

import java.util.Comparator;

/**
 * Used to compare scores
 */
public class ScoreComparator implements Comparator<Pair<SimpleStringProperty,Integer>> {
    /**
     * Compares two scores to find which one is bigger
     * @param pair1 the first score
     * @param pair2 the second score
     * @return the second score - the first score
     */
    @Override
    public int compare(Pair<SimpleStringProperty,Integer> pair1, Pair<SimpleStringProperty,Integer> pair2) {
        return pair2.getValue() - pair1.getValue();
    }

}
