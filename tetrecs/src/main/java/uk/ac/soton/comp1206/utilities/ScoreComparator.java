package uk.ac.soton.comp1206.utilities;

import javafx.beans.property.SimpleStringProperty;
import javafx.util.Pair;

import java.util.Comparator;

public class ScoreComparator implements Comparator<Pair<SimpleStringProperty,Integer>> {

    @Override
    public int compare(Pair<SimpleStringProperty,Integer> pair1, Pair<SimpleStringProperty,Integer> pair2) {
        return pair2.getValue() - pair1.getValue();
    }

}
