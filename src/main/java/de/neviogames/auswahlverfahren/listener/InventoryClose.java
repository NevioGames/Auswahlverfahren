package de.neviogames.auswahlverfahren.listener;

import de.neviogames.auswahlverfahren.utils.NGEventInventoryHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

public class InventoryClose implements Listener {

    @EventHandler
    public void onClose(InventoryCloseEvent event) {
        if (event.getInventory() == null) return;
        if (event.getPlayer() == null) return;
        NGEventInventoryHandler.onClose(event);
    }
}
