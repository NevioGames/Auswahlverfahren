package de.neviogames.auswahlverfahren.utils;

import lombok.Getter;
import org.bukkit.ChatColor;

@Getter
public enum House {

    MUGGLE("Muggle", ChatColor.GRAY),
    GRYFFINDOR("Gryffindor", ChatColor.RED),
    RAVENCLAW("Ravenclaw", ChatColor.BLUE),
    HUFFLEPUFF("Hufflepuff", ChatColor.YELLOW),
    SLYTHERIN("Slytherin", ChatColor.GREEN);

    private final String displayName;
    private final ChatColor color;

    House(String displayName, ChatColor color) {
        this.displayName = displayName;
        this.color = color;
    }

    public static House fromName(final String Name) {
        for(House r : House.values()) {
            if(r.toString().equalsIgnoreCase(Name)) return r;
        }
        return null;
    }

}
