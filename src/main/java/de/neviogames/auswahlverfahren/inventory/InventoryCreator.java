package de.neviogames.auswahlverfahren.inventory;

import de.neviogames.auswahlverfahren.utils.Util;
import de.neviogames.nglib.utils.misc.Paginator;
import lombok.Getter;
import lombok.Setter;
import net.jodah.expiringmap.ExpirationPolicy;
import net.jodah.expiringmap.ExpiringMap;
import net.minecraft.server.v1_7_R4.NBTTagString;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.concurrent.TimeUnit;

@SuppressWarnings("unused")
@Getter @Setter
public class InventoryCreator {

    @Getter
    private static final Map<UUID, InventoryCreator> cache = ExpiringMap.builder()
            .expirationPolicy(ExpirationPolicy.CREATED)
            .expiration(5, TimeUnit.MINUTES)
            .build();

    private final InventoryType inventoryType;
    private int inventorySize;
    private boolean dynamicSize = false;
    private final Map<Integer, InvItem> itemsPos = new TreeMap<>();
    private final List<InvItem> itemsNeg = new ArrayList<>();
    private ItemStack fillItem = null;
    private String inventoryTitel;
    private PageMenu pageMenu;
    private PageMenuState pageMenuState = PageMenuState.AUTO;
    private Paginator<InvItem> paginator;
    private int highestFixSlot = 0;

    public InventoryCreator(InventoryType inventoryType) {
        this.inventoryType = inventoryType;
        this.inventorySize = inventoryType.getDefaultSize();
    }

    public InventoryCreator(int inventorySize) {
        this.inventoryType = null;
        this.inventorySize = inventorySize;
    }

    public InventoryCreator(InvItem... items) {
        this(Arrays.asList(items));
    }

    public InventoryCreator(List<InvItem> items) {
        this.inventoryType = null;
        this.dynamicSize = true;
        this.addItems(items);
    }

    /**
     * Adds the given items to the inventory as ItemStacks.
     *
     * @param items The list of items to add.
     */
    public void addItemsAsItemStack(ItemStack... items) {
        addItemsAsItemStack(Arrays.asList(items));
    }

    /**
     * It takes a list of ItemStacks and adds them to the inventory as InvItems
     *
     * @param items The list of items to add.
     */
    public void addItemsAsItemStack(List<ItemStack> items) {
        items.forEach(item -> this.itemsNeg.add(new InvItem(-1, item)));
        this.paginator = null;
    }

    /**
     * Adds the given items to the inventory.
     *
     * @param items The list of items to add to the inventory.
     */
    public void addItems(InvItem... items) {
        addItems(Arrays.asList(items));
    }

    /**
     * Add all the items in the given list to the items list.
     *
     * @param items The list of items to add to the inventory.
     */
    public void addItems(List<InvItem> items) {
        items.forEach(invItem -> {
            if (invItem.getSlot() >= 0) {
                this.itemsPos.put(invItem.getSlot(), invItem);
                if (invItem.getSlot() > this.highestFixSlot) this.highestFixSlot = invItem.getSlot();
            } else {
                this.itemsNeg.add(invItem);
            }
        });
        this.paginator = null;
    }

    /**
     * This function takes a string and cuts it to 32 characters
     *
     * @param inventoryTitel The title of the inventory.
     */
    public void setInventoryTitel(String inventoryTitel) {
        this.inventoryTitel = Util.cutString(inventoryTitel, 32);
    }

    /**
     * Sets the fill item to the default fill item.
     */
    public void setDefaultFillItem() {
        this.fillItem = new ItemAPIv2(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte)15).setDisplayName(" ").toItemStack();
    }

    /**
     * Returns true if the inventory is custom, false if it is not.
     *
     * @return The inventoryType is being returned.
     */
    public boolean isInventoryCustom() {
        return this.inventoryType == null;
    }

    /**
     * This function creates an inventory with the given UUID and the given size.
     *
     * @param uuid The UUID of the player you want to create an inventory for.
     * @return An Inventory object
     */
    public Inventory create(UUID uuid) {
        return create(uuid, 0);
    }

    /**
     * It creates an inventory with the given size, fills it with the fill item, adds the items to the inventory and caches
     * the inventory
     *
     * @param uuid The UUID of the player.
     * @param page The page of the inventory.
     * @return The inventory.
     */
    public Inventory create(UUID uuid, int page) {
        checkInventoryValues();

        // It creates the inventory.
        int columnSize = 9;
        Inventory inventory;
        if (this.isInventoryCustom()) {
            if (this.dynamicSize) this.inventorySize = getDynamicInventorySize();
            inventory = Bukkit.createInventory(null, this.inventorySize, this.inventoryTitel);
        } else {
            columnSize = getInventoryColumnSize();
            inventory = Bukkit.createInventory(null, this.inventoryType, this.inventoryTitel);
        }
        
        // It fills the inventory with the fill item.
        if (this.fillItem != null) {
            for (int i = 0; i < this.inventorySize; i++) {
                inventory.setItem(i, this.fillItem);
            }
        }


        if (this.paginator == null) createPaginator();
        int itemsPerPage = this.paginator.getTot() > this.inventorySize ? this.inventorySize-columnSize : this.inventorySize;
        // Adding the page menu.
        if (!this.pageMenuState.equals(PageMenuState.DISABLED)) {
            int maxPages = this.paginator.totalPage(itemsPerPage)-1;

            // Creating the PageMenu if the pageMenuState is enabled or auto and the maxPages is greater than 1.
            if (this.pageMenuState.equals(PageMenuState.ENABLED) || (this.pageMenuState.equals(PageMenuState.AUTO) && maxPages > 1)) {
                if (this.pageMenu == null) this.pageMenu = new PageMenu();
            }

            // Adding the page menu buttons to the inventory.
            if (this.pageMenu != null) {
                if (columnSize == 0)
                    throw new IndexOutOfBoundsException("The inventory does not have enough space for a PageMenu");

                for (int i = 0; i < columnSize; i++) {
                    int slot = this.inventorySize -= 1;
                    if (i == 0 && maxPages > page + 1) {
                        inventory.setItem(slot, this.pageMenu.getNextPageItem(page + 1));
                    } else if (i == columnSize - 1 && page + 1 > 1) {
                        inventory.setItem(slot, this.pageMenu.getPreviousPageItem(page - 1));
                    } else if (this.pageMenu.getPageFillItem() != null) {
                        inventory.setItem(slot, this.pageMenu.getPageFillItem());
                    }
                }
            }
        } else {
            itemsPerPage = this.inventorySize;
        }

        // It adds the items to the inventory.
        List<InvItem> item = this.paginator.paginate(page, itemsPerPage);
        for (int i = 0; i < item.size(); i++) {
            if (!item.get(i).isVisible()) continue;
            inventory.setItem(i, item.get(i).getItemStack());
        }

        // Caching the inventory.
        cache.put(uuid, this);

        return inventory;
    }

    /**
     * It closes the player's current inventory, then opens the inventory created by the create() function
     *
     * @param player The player who is opening the inventory.
     */
    public void open(Player player) {
        Inventory inventory = create(player.getUniqueId());
        player.closeInventory();
        player.openInventory(inventory);
    }

    /**
     * It opens the inventory for the player, and the page number is the page number.
     *
     * @param player The player who is opening the inventory.
     * @param page The page you want to open.
     */
    public void open(Player player, int page) {
        Inventory inventory = create(player.getUniqueId(), page);
        player.closeInventory();
        player.openInventory(inventory);
    }

    /**
     * It creates a paginator from the itemsPos and itemsNeg maps
     */
    private void createPaginator() {
        List<InvItem> items = new ArrayList<>();
        int negIndex = 0;
        int max = Math.max(this.itemsPos.size() + this.itemsNeg.size(), this.highestFixSlot);
        for (int i = 0; i <= max; i++) {
            if (this.itemsPos.containsKey(i)) {
                items.add(this.itemsPos.get(i));
            } else if (negIndex < this.itemsNeg.size()) {
                items.add(this.itemsNeg.get(negIndex));
                negIndex++;
            } else {
                items.add(new InvItem(-1, false, new ItemStack(Material.AIR)));
            }
        }
        this.paginator = new Paginator<>(items);
    }

    /**
     * Get the number of slots in the inventory, based on the number of items in the inventory.
     *
     * @return The number of lines in the inventory.
     */
    private int getDynamicInventorySize() {
        int lines = 0;
        int max = Math.max(this.itemsPos.size() + this.itemsNeg.size(), this.highestFixSlot);
        for (int i = 0; i < max; i += 9) {
            lines++;
        }
        return Math.min(lines, 6) * 9;
    }

    /**
     * If the inventory type is a chest, ender chest, or player, return 9. If the inventory type is a hopper, return 5. If
     * the inventory type is a dispenser, dropper, or workbench, return 3. Otherwise, return 0.
     *
     * @return The number of columns in the inventory.
     */
    private int getInventoryColumnSize() {
        switch (this.inventoryType) {
            case CHEST:
            case ENDER_CHEST:
            case PLAYER:
                return 9;
            case HOPPER:
                return 5;
            case DISPENSER:
            case DROPPER:
            case WORKBENCH:
                return 3;
            default:
                return 0;
        }
    }

    /**
     * If the inventory is custom, the inventory size must be divisible by 9.
     */
    private void checkInventoryValues() {
        if (this.itemsPos.isEmpty() && this.itemsNeg.isEmpty())
            throw new NullPointerException("No Items found");

        if (this.isInventoryCustom() && this.inventorySize % 9 != 0)
            throw new IllegalArgumentException("The inventory size must be divisible by 9");

        for (InvItem item : this.itemsPos.values()) {
            if (this.inventorySize <= item.getSlot()) {
                if (this.pageMenuState.equals(PageMenuState.DISABLED)) {
                    throw new IndexOutOfBoundsException("The Inventory slot is not reachable. Remove the Item with the slot '" + item.getSlot() + "' or enable the PageMenu");
                }
            }
        }

        if (this.itemsPos.size() + this.itemsNeg.size() > this.inventorySize) {
            if (this.pageMenuState.equals(PageMenuState.DISABLED)) {
                throw new IndexOutOfBoundsException("The Inventory is full. Remove Items or enable the PageMenu");
            }
        }
    }

    @Setter
    public static class PageMenu {
        private ItemStack previousPageItem;
        private ItemStack nextPageItem;
        @Getter
        private ItemStack pageFillItem;

        public PageMenu() {
            this.previousPageItem = new ItemAPIv2(Material.APPLE).setDisplayName("Vorherige Seite").toItemStack();
            this.nextPageItem = new ItemAPIv2(Material.EMERALD).setDisplayName("Nächste Seite").toItemStack();
            this.pageFillItem = new ItemAPIv2(Material.STAINED_GLASS_PANE, 1, (short) 0, (byte)7).setDisplayName(" ").toItemStack();
        }

        public ItemStack getPreviousPageItem(int previousPage) {
            return ItemAPIv2.setNBTTag("IC-PM-PreviousPage", previousPage, this.previousPageItem, NBTTagString.class);
        }

        public ItemStack getNextPageItem(int nextPage) {
            return ItemAPIv2.setNBTTag("IC-PM-NextPage", nextPage, this.previousPageItem, NBTTagString.class);
        }
    }

    public enum PageMenuState {
        AUTO,
        ENABLED,
        DISABLED
    }
}
