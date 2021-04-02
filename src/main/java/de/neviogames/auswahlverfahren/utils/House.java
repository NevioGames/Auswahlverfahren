package de.neviogames.auswahlverfahren.utils;

import org.bukkit.ChatColor;

public enum House {

    Muggle ("MUGGLE", ChatColor.GRAY),
    Gryffindor ("GRYFFINDOR", ChatColor.RED),
    Ravenclaw ("RAVENCLAW", ChatColor.BLUE),
    Hufflepuff ("HUFFLEPUFF", ChatColor.YELLOW),
    Slytherin ("SLYTHERIN", ChatColor.GREEN);

    private final String DB_Name;
    private final ChatColor color;

    House(String DB_Name, ChatColor color) {
        this.DB_Name = DB_Name;
        this.color = color;
    }

    public String getDB_Name() {
        return DB_Name;
    }

    public ChatColor getColor() {
        return color;
    }

    public static House fromName(final String Name) {
        for(House r : House.values()) {
            if(r.toString().equalsIgnoreCase(Name)) return r;
        }
        return null;
    }

}
