package de.neviogames.auswahlverfahren.utils;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;

public class Util {

    public static boolean isNullOrEmpty(String target) {
        return target == null || target.trim().isEmpty();
    }

    public static boolean isNullOrEmpty(Map<?, ?> target) {
        return target == null || target.isEmpty();
    }

    public static boolean isNullOrEmpty(Collection<?> target) {
        return target == null || target.isEmpty();
    }

    public static int getFreeSlots(Inventory inventory) {
        int free = 0;
        for (ItemStack itemStack : inventory.getContents()) {
            if (itemStack == null) free++;
        }
        return free;
    }

}
