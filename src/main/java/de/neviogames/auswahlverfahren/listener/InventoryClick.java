package de.neviogames.auswahlverfahren.listener;

import de.neviogames.auswahlverfahren.utils.NGEventInventoryHandler;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;

public class InventoryClick implements Listener {

    @EventHandler
    public void onClick(InventoryClickEvent event) {
        if (event.getWhoClicked() == null) return;
        if (event.getClickedInventory() == null) return;
        if (event.getCurrentItem() == null) return;
        if (NGEventInventoryHandler.onClick(event)) {
            event.setCancelled(true);
        }
    }
}
