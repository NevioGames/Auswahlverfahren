package de.neviogames.auswahlverfahren.inventory;

import lombok.SneakyThrows;
import net.minecraft.server.v1_7_R4.NBTBase;
import net.minecraft.server.v1_7_R4.NBTTagCompound;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.inventory.CraftItemStack;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@SuppressWarnings("unused")
public class ItemAPIv2 {

    private ItemStack item;


    @SuppressWarnings("deprecation")
    public ItemAPIv2(int type) {
        this.item = new ItemStack(type);
    }

    public ItemAPIv2(ItemStack item) {
        this.item = item;
    }

    public ItemAPIv2(Material material) {
        this.item = new ItemStack(material);
    }

    @SuppressWarnings("deprecation")
    public ItemAPIv2(int type, int amount) {
        this.item = new ItemStack(type, amount);
    }

    public ItemAPIv2(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    @SuppressWarnings("deprecation")
    public ItemAPIv2(int type, int amount, short damage) {
        this.item = new ItemStack(type, amount, damage);
    }

    public ItemAPIv2(Material material, int amount, short damage) {
        this.item = new ItemStack(material, amount, damage);
    }

    @SuppressWarnings("deprecation")
    public ItemAPIv2(int type, int amount, short damage, Byte data) {
        this.item = new ItemStack(type, amount, damage, data);
    }

    @SuppressWarnings("deprecation")
    public ItemAPIv2(Material material, int amount, short damage, byte data) {
        this.item = new ItemStack(material, amount, damage, data);
    }


    @SuppressWarnings("MethodDoesntCallSuperMethod")
    public ItemAPIv2 clone() {
        return new ItemAPIv2(this.item);
    }

    public ItemMeta cloneItemMeta() {
        return this.item.getItemMeta().clone();
    }

    public ItemAPIv2 setDurability(short durability) {
        this.item.setDurability(durability);
        return this;
    }

    public boolean hasDisplayName() {
        return this.item.getItemMeta().hasDisplayName();
    }

    public String getDisplayName() {
        return this.item.getItemMeta().getDisplayName();
    }

    public ItemAPIv2 setDisplayName(String name) {
        ItemMeta im = this.item.getItemMeta();
        im.setDisplayName(name);
        this.item.setItemMeta(im);
        return this;
    }

    public boolean hasEnchantments() {
        return this.item.getItemMeta().hasEnchants();
    }

    public boolean hasEnchantment(Enchantment enchantment) {
        return this.item.getItemMeta().hasEnchant(enchantment);
    }

    public int getEnchantmentLevel(Enchantment enchantment) {
        return this.item.getItemMeta().getEnchantLevel(enchantment);
    }

    public Map<Enchantment, Integer> getEnchantments() {
        return this.item.getItemMeta().getEnchants();
    }

    public boolean hasConflictingEnchantment(Enchantment enchantment) {
        return this.item.getItemMeta().hasConflictingEnchant(enchantment);
    }

    public ItemAPIv2 addUnsafeEnchantment(Enchantment enchantment, int level) {
        this.item.addUnsafeEnchantment(enchantment, level);
        return this;
    }

    public ItemAPIv2 removeEnchantment(Enchantment enchantment) {
        this.item.removeEnchantment(enchantment);
        return this;
    }

    public ItemAPIv2 addEnchant(Enchantment enchantment, int level) {
        ItemMeta im = this.item.getItemMeta();
        im.addEnchant(enchantment, level, true);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemAPIv2 addEnchantments(Map<Enchantment, Integer> enchantments) {
        this.item.addEnchantments(enchantments);
        return this;
    }

    public ItemAPIv2 setInfinityDurability() {
        this.item.setDurability(Short.MAX_VALUE);
        return this;
    }

    public boolean hasLore() {
        return this.item.getItemMeta().hasLore();
    }

    public List<String> getLore() {
        return this.item.getItemMeta().getLore();
    }

    public ItemAPIv2 setLore(String... lore) {
        ItemMeta im = this.item.getItemMeta();
        im.setLore(Arrays.asList(lore));
        this.item.setItemMeta(im);
        return this;
    }

    public ItemAPIv2 setLore(List<String> lore) {
        ItemMeta im = this.item.getItemMeta();
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemAPIv2 removeLoreLine(String line) {
        ItemMeta im = this.item.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (!lore.contains(line)) return this;
        lore.remove(line);
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemAPIv2 removeLoreLine(int index) {
        ItemMeta im = this.item.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        if (im.hasLore()) lore.addAll(im.getLore());
        lore.remove(index);
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemAPIv2 addLoreLine(String... line) {
        ItemMeta im = this.item.getItemMeta();
        List<String> lore = new ArrayList<>();
        if (im.hasLore()) lore.addAll(im.getLore());
        lore.addAll(Arrays.asList(line));
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemAPIv2 addLoreLine(String line, int pos) {
        ItemMeta im = this.item.getItemMeta();
        List<String> lore = new ArrayList<>(im.getLore());
        lore.set(pos, line);
        im.setLore(lore);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemStack toItemStack() {
        return this.item;
    }


    // BookMeta //

    public boolean hasBookTitle() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).hasTitle();
    }

    public String getBookTitle() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).getTitle();
    }

    public ItemAPIv2 setBookTitle(String title) throws ClassCastException {
        BookMeta meta = (BookMeta) this.item.getItemMeta();
        meta.setTitle(title);
        this.item.setItemMeta(meta);
        return this;
    }

    public boolean hasBookAuthor() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).hasAuthor();
    }

    public String getBookAuthor() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).getAuthor();
    }

    public ItemAPIv2 setBookAuthor(String author) throws ClassCastException {
        BookMeta meta = (BookMeta) this.item.getItemMeta();
        meta.setAuthor(author);
        this.item.setItemMeta(meta);
        return this;
    }

    public boolean hasBookPages() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).hasPages();
    }

    public String getBookPage(int pageNumber) throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).getPage(pageNumber);
    }

    public ItemAPIv2 setBookPage(int pageNumber, String pageContent) throws ClassCastException {
        BookMeta meta = (BookMeta) this.item.getItemMeta();
        meta.setPage(pageNumber, pageContent);
        this.item.setItemMeta(meta);
        return this;
    }

    public List<String> getBookPages() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).getPages();
    }

    public ItemAPIv2 setBookPages(List<String> pageContentList) throws ClassCastException {
        BookMeta meta = (BookMeta) this.item.getItemMeta();
        meta.setPages(pageContentList);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemAPIv2 setBookPages(String... pageContents) throws ClassCastException {
        BookMeta meta = (BookMeta) this.item.getItemMeta();
        meta.setPages(pageContents);
        this.item.setItemMeta(meta);
        return this;
    }

    public ItemAPIv2 addBookPage(String... pageContent) throws ClassCastException {
        BookMeta meta = (BookMeta) this.item.getItemMeta();
        meta.addPage(pageContent);
        this.item.setItemMeta(meta);
        return this;
    }

    public int getBookPageCount() throws ClassCastException {
        return ((BookMeta) this.item.getItemMeta()).getPageCount();
    }

    // NBT //

    /**
     * If the item has an NBT tag, return true if the tag has the specified key, otherwise return false.
     *
     * @param nbtKey The key of the NBT tag you want to check.
     * @param is The itemStack you want to check
     * @return A boolean value.
     */
    public static boolean existsNBTTag(String nbtKey, ItemStack is) {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return nbtTagCompound.hasKey(nbtKey);
    }

    /**
     * If the item has a tag, remove the tag with the given key
     *
     * @param nbtKey The key of the tag you want to remove.
     * @param is The ItemStack you want to remove the NBT tag from.
     * @return The itemStack with the nbt tag removed.
     */
    public static ItemStack removeNBTTag(String nbtKey, ItemStack is) {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        if (nbtTagCompound.hasKey(nbtKey)) {
            nbtTagCompound.remove(nbtKey);
        } else {
            return CraftItemStack.asCraftMirror(nmsItem);
        }
        nmsItem.setTag(nbtTagCompound);
        return CraftItemStack.asCraftMirror(nmsItem);
    }

    /**
     * It takes an ItemStack, a key, a value, and a value type, and sets the value of the key in the ItemStack's NBT tag to
     * the value
     *
     * @param nbtKey The key of the NBT tag.
     * @param nbtValue The value you want to set the NBT tag to.
     * @param is The ItemStack you want to add the NBT tag to.
     * @param valueType The type of NBT value you want to set.
     * @return An ItemStack
     */
    @SuppressWarnings("JavaReflectionInvocation")
    @SneakyThrows
    public static ItemStack setNBTTag(String nbtKey, Object nbtValue, ItemStack is, Class<? extends NBTBase> valueType) {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        Constructor<?> nbtConstructor = valueType.getConstructor();
        if (!nbtTagCompound.hasKey(nbtKey)) {
            nbtTagCompound.set(nbtKey, (NBTBase) nbtConstructor.newInstance(nbtValue));
        } else {
            nbtTagCompound.remove(nbtKey);
            nbtTagCompound.set(nbtKey, (NBTBase) nbtConstructor.newInstance(nbtValue));
        }
        nmsItem.setTag(nbtTagCompound);
        return CraftItemStack.asCraftMirror(nmsItem);
    }

    /**
     * If the item has an NBT tag, return the value of the NBT tag with the given key, otherwise return an empty string.
     *
     * @param nbtKey The key of the NBT tag you want to get.
     * @param is The itemStack you want to get the NBT tag from.
     * @return The value of the NBT tag with the key nbtKey.
     */
    public static String getNBTTag(String nbtKey, ItemStack is) {
        net.minecraft.server.v1_7_R4.ItemStack nmsItem = CraftItemStack.asNMSCopy(is);
        NBTTagCompound nbtTagCompound = nmsItem.hasTag() ? nmsItem.getTag() : new NBTTagCompound();
        return nbtTagCompound.getString(nbtKey);
    }
}
