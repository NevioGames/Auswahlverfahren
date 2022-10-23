package de.neviogames.auswahlverfahren.inventory;

import lombok.Getter;
import lombok.Setter;
import net.minecraft.server.v1_7_R4.NBTTagString;
import org.bukkit.inventory.ItemStack;

@Getter
public class InvItem {


    private final int slot;
    @Setter
    private boolean visible;
    private ItemStack itemStack;


    public InvItem(int slot, ItemStack itemStack) {
        this(slot, true, itemStack);
    }

    public InvItem(int slot, boolean visible, ItemStack itemStack) {
        this.slot = slot;
        this.visible = visible;
        this.itemStack = itemStack;
    }

    public InvItem addNBTTag(String key, String value) {
        this.itemStack = ItemAPIv2.setNBTTag(key, value, this.itemStack, NBTTagString.class);
        return this;
    }

    public boolean isVisible() {
        return itemStack != null && this.slot >= -1 && this.visible;
    }

}
