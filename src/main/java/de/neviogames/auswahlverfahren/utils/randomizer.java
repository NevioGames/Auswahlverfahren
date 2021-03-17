package de.neviogames.auswahlverfahren.utils;

import java.util.*;

public class randomizer {

    public static ArrayList<UUID> randomlist (House house, int eingabezahl){
        ArrayList<UUID> listeHouse = database.getAllMembers(house);
        Collections.shuffle(listeHouse, new Random(eingabezahl));
         return listeHouse;

    }

}
