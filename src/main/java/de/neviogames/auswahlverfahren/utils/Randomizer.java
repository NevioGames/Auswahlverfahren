package de.neviogames.auswahlverfahren.utils;

import java.util.*;

public class Randomizer {

    // randomize a list from the database and shuffle 'inputNumber' times
    public static ArrayList<UUID> randomList(NGEventTeam team, int inputNumber) {
        ArrayList<UUID> listeHouse = database.getAllMembers(team);
        Collections.shuffle(listeHouse, new Random(inputNumber));
        return listeHouse;
    }

    // random number between min and max
    public static int fairRoundedRandom(int min, int max) {
        int num;
        do {
            num = (int)Math.floor(Math.random() * (double)(max - min) + (double)min);
        } while(num > max - 1);
        return num;
    }

}
