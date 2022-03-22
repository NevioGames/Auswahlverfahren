package de.neviogames.auswahlverfahren.utils;

import java.util.*;

public class randomizer {

    // randomize a list from the database and shuffle 'inputNumber' times
    public static ArrayList<UUID> randomList(House house, int inputNumber) {
        ArrayList<UUID> listeHouse = database.getAllMembers(house);
        Collections.shuffle(listeHouse, new Random(inputNumber));
        return listeHouse;
    }

}
