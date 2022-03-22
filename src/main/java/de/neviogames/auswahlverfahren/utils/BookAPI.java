package de.neviogames.auswahlverfahren.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.BookMeta;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Map;

@SuppressWarnings({"unused", "UnusedReturnValue"})
public class BookAPI {

    final ItemStack itemStack;
    final BookMeta meta;

    public BookAPI() {
        this.itemStack = new ItemStack(Material.WRITTEN_BOOK);
        this.meta = (BookMeta) this.itemStack.getItemMeta();
    }

    public ItemStack create() {
        this.itemStack.setItemMeta(this.meta);
        return this.itemStack;
    }

    public boolean hasTitle() {
        return meta.hasTitle();
    }

    public String getTitle() {
        return meta.getTitle();
    }

    public BookAPI setTitle(String title) {
        this.meta.setTitle(title);
        return this;
    }

    public boolean hasAuthor() {
        return this.meta.hasAuthor();
    }

    public String getAuthor() {
        return this.meta.getAuthor();
    }

    public BookAPI setAuthor(String author) {
        this.meta.setAuthor(author);
        return this;
    }

    public boolean hasPages() {
        return this.meta.hasPages();
    }

    public String getPage(int pageNumber) {
        return this.meta.getPage(pageNumber);
    }

    public BookAPI setPage(int pageNumber, String pageContent) {
        this.meta.setPage(pageNumber, pageContent);
        return this;
    }

    public List<String> getPages() {
        return this.meta.getPages();
    }

    public BookAPI setPages(List<String> pageContentList) {
        this.meta.setPages(pageContentList);
        return this;
    }

    public BookAPI setPages(String... pageContents) {
        this.meta.setPages(pageContents);
        return this;
    }

    public BookAPI addPage(String... pageContent) {
        this.meta.addPage(pageContent);
        return this;
    }

    public int getPageCount() {
        return this.meta.getPageCount();
    }

    public BookMeta clone() {
        return this.meta.clone();
    }

    public boolean hasDisplayName() {
        return this.meta.hasDisplayName();
    }

    public String getDisplayName() {
        return this.meta.getDisplayName();
    }

    public BookAPI setDisplayName(String displayName) {
        this.meta.setDisplayName(displayName);
        return this;
    }

    public boolean hasLore() {
        return this.meta.hasLore();
    }

    public List<String> getLore() {
        return this.meta.getLore();
    }

    public BookAPI setLore(List<String> lore) {
        this.meta.setLore(lore);
        return this;
    }

    public boolean hasEnchants() {
        return this.meta.hasEnchants();
    }


    public boolean hasEnchant(Enchantment enchantment) {
        return this.meta.hasEnchant(enchantment);
    }

    public int getEnchantLevel(Enchantment enchantment) {
        return this.meta.getEnchantLevel(enchantment);
    }

    public Map<Enchantment, Integer> getEnchants() {
        return this.meta.getEnchants();
    }

    public BookAPI addEnchant(Enchantment enchantment, int i, boolean b) {
        this.meta.addEnchant(enchantment, i, b);
        return this;
    }

    public boolean removeEnchant(Enchantment enchantment) {
        return this.meta.removeEnchant(enchantment);
    }

    public boolean hasConflictingEnchant(Enchantment enchantment) {
        return this.meta.hasConflictingEnchant(enchantment);
    }

    public ItemMeta.Spigot spigot() {
        return this.meta.spigot();
    }

    public Map<String, Object> serialize() {
        return this.meta.serialize();
    }
}    