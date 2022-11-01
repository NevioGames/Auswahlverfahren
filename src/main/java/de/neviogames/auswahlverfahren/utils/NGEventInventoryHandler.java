package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.inventory.InvItem;
import de.neviogames.auswahlverfahren.inventory.InventoryCreator;
import de.neviogames.auswahlverfahren.inventory.ItemAPIv2;
import de.neviogames.nglib.utils.utility;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class NGEventInventoryHandler {

    @Getter
    private static final Map<UUID, NGEventInventoryHandler> handler = new HashMap<>();
    private static final String inventoryTitle = ""+ChatColor.AQUA+ChatColor.BOLD+"Bewerbung";
    private static final String teamspeakTitle = ""+ChatColor.AQUA+ChatColor.BOLD+"Teamspeak";
    private static final String voiceTitle = ""+ChatColor.LIGHT_PURPLE+ChatColor.BOLD+"Deine Stimme wird aufgenommen";
    private static final String dateTitle = ""+ChatColor.GOLD+ChatColor.BOLD+"Hast du Zeit?";
    private static final String contactTitle = ""+ChatColor.BLUE+ChatColor.BOLD+"Kontaktdaten";
    private static final String sendTitle = ""+ChatColor.RED+ChatColor.BOLD+"Bewerben";

    private final Player player;
    private final NGEventCandidate candidate;

    private boolean teamspeak = false;
    private boolean voice = false;
    private boolean date = false;
    private boolean contact = false;


    public NGEventInventoryHandler(Player player, NGEventCandidate candidate) {
        this.player = player;
        this.candidate = candidate;
        handler.put(player.getUniqueId(), this);
    }

    public void open() {
        InventoryCreator inv = new InventoryCreator(9*5);
        inv.setInventoryTitel(inventoryTitle);
        inv.setDefaultFillItem();
        inv.addItems(getTeamspeakItem(false),
                getVoiceItem(false),
                getDateItem(false),
                getContactItem(false),
                getSendItem(false, false, false, false));
        inv.open(this.player);
    }


    private InvItem getTeamspeakItem(boolean selected) {
        ItemAPIv2 item = new ItemAPIv2(Material.GREEN_RECORD)
                .setDisplayName(teamspeakTitle)
                .addLoreLine(
                        ""+ChatColor.AQUA+"Hiermit bestätigst du, dass du Teamspeak³ besitzt",
                        ""+ChatColor.AQUA+"oder es dir für das Special herunterlädst.",
                        "",
                        ""+(!selected ? ChatColor.DARK_RED+"Du bist "+ChatColor.DARK_RED+ChatColor.BOLD+"NICHT"+ChatColor.DARK_RED+" einverstanden." : ChatColor.GREEN+"Du bist einverstanden."),
                        "",
                        ""+(!selected ? ChatColor.GRAY+"Klicke auf die Schallplatte, um zuzustimmen." : ChatColor.GRAY+"Klicke auf die Schallplatte, um nicht mehr zuzustimmen."),
                        "",
                        ""+ChatColor.AQUA+"Hinweise:",
                        ""+ChatColor.GRAY+"Teamspeak³ ist für den PC/Laptop kostenlos.");
        if (selected) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return new InvItem(10, item.toItemStack());
    }

    private InvItem getVoiceItem(boolean selected) {
        ItemAPIv2 item = new ItemAPIv2(Material.BOOK)
                .setDisplayName(voiceTitle)
                .addLoreLine(
                        ""+ChatColor.RED+"Hiermit bestätigst du, dass du damit einverstanden bist,",
                        ""+ChatColor.RED+"dass deine Stimme während des Specials '"+ChatColor.YELLOW+Configuration.getInstance().getEventName()+ChatColor.RED+"'",
                        ""+ChatColor.RED+"aufgenommen und in Videos veröffentlicht wird.",
                        "",
                        ""+(!selected ? ChatColor.DARK_RED+"Du bist "+ChatColor.DARK_RED+ChatColor.BOLD+"NICHT"+ChatColor.DARK_RED+" einverstanden." : ChatColor.GREEN+"Du bist einverstanden."),
                        "",
                        ""+(!selected ? ChatColor.GRAY+"Klicke auf das Buch, um dein Einverständnis zu geben." : ChatColor.GRAY+"Klicke auf das Buch, um dein Einverständnis zurückzuziehen."));
        if (selected) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return new InvItem(12, item.toItemStack());
    }

    private InvItem getDateItem(boolean selected) {
        ItemAPIv2 item = new ItemAPIv2(Material.WATCH)
                .setDisplayName(dateTitle)
                .addLoreLine(
                        ""+ChatColor.RED+"Hiermit bestätigst du, dass du am "+ChatColor.YELLOW+Configuration.getInstance().getEventDate(),
                        ""+ChatColor.RED+"von "+ChatColor.YELLOW+Configuration.getInstance().getEventTimeFrom()+" Uhr"+ChatColor.RED+" bis "+ChatColor.YELLOW+Configuration.getInstance().getEventTimeTo()+" Uhr"+ChatColor.RED+" Zeit hast.",
                        "",
                        ""+(!selected ? ChatColor.DARK_RED+"Du hast "+ChatColor.DARK_RED+ChatColor.BOLD+"KEINE"+ChatColor.DARK_RED+" Zeit." : ChatColor.GREEN+"Du hast Zeit."),
                        "",
                        ""+(!selected ? ChatColor.GRAY+"Klicke auf die Uhr, um zuzustimmen." : ChatColor.GRAY+"Klicke auf die Uhr, um deine Zustimmung zurückzuziehen."),
                        "",
                        ""+ChatColor.GOLD+"Hinweise:",
                        ""+ChatColor.GRAY+"Wenn du doch keine Zeit mehr haben solltest, kannst du",
                        ""+ChatColor.GRAY+"bis zur Auslosung mit dem Befehl "+ChatColor.AQUA+"/leaveEvent"+ChatColor.GRAY+" deine Teilnahme",
                        ""+ChatColor.GRAY+"zurückziehen. Solltest du ausgelost werden und keine Zeit",
                        ""+ChatColor.GRAY+"haben, wirst du leider auch für die nächsten Specials gesperrt.");
        if (selected) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return new InvItem(14, item.toItemStack());
    }

    private InvItem getContactItem(boolean selected) {
        ItemAPIv2 item = new ItemAPIv2(Material.PAPER)
                .setDisplayName(contactTitle)
                .addLoreLine(
                        ""+ChatColor.BLUE+"Überprüfe deine angegebenen Kontaktdaten.",
                        ""+ChatColor.GRAY+"• Handelt es sich um einen E-Mail oder Twitter Account?",
                        ""+ChatColor.GRAY+"• Ist die E-Mail Adresse und/oder der Accountname richtig geschrieben?",
                        "",
                        ""+ChatColor.BLUE+"Deine Daten: "+ChatColor.WHITE+this.candidate.getContact(),
                        "",
                        ""+(!selected ? ChatColor.RED+"Du musst deine Daten bestätigen, um am Event teilnehmen zu können." : ChatColor.GREEN+"Du hast deine Daten bestätigt."),
                        ""+ChatColor.GRAY+(!selected ? "Klicke auf das Papier, um deine Kontaktdaten zu bestätigen." : "Klicke auf das Papier, um deine Kontaktdaten nicht mehr zu bestätigen."),
                        "",
                        ""+ChatColor.BLUE+"Hinweise:",
                        ""+ChatColor.GRAY+"Falls du einen Twitteraccount angegeben hast, achte bitte darauf,",
                        ""+ChatColor.GRAY+"dass du Nachrichten empfangen kannst, wenn du ausgelost wirst.",
                        "",
                        ""+ChatColor.GRAY+"Deine Kontaktdaten werden nach dem Event unwiederbringlich gelöscht.");
        if (selected) item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
        return new InvItem(16, item.toItemStack());
    }

    private InvItem getSendItem(boolean selectedTeamspeak, boolean selectedVoice, boolean selectedDate, boolean selectedContact) {
        boolean allTrue = selectedTeamspeak && selectedVoice && selectedDate && selectedContact;
        return new InvItem(31, new ItemAPIv2((allTrue ? Material.EMERALD_BLOCK : Material.REDSTONE_BLOCK))
                .setDisplayName(sendTitle)
                .addLoreLine(
                        "",
                        ""+ChatColor.AQUA+ChatColor.BOLD+"Teamspeak"+ChatColor.GRAY+ChatColor.BOLD+": "+(!selectedTeamspeak ? ChatColor.RED+"Nicht zugestimmt" : ChatColor.GREEN+"Zugestimmt"),
                        ""+ChatColor.LIGHT_PURPLE+ChatColor.BOLD+"Aufnahme der Stimme"+ChatColor.GRAY+ChatColor.BOLD+": "+(!selectedVoice ? ChatColor.RED+"Nicht erlaubt" : ChatColor.GREEN+"Erlaubt"),
                        ""+ChatColor.GOLD+ChatColor.BOLD+"Hast du Zeit"+ChatColor.GRAY+ChatColor.BOLD+": "+(!selectedDate ? ChatColor.RED+"Nicht bestätigt" : ChatColor.GREEN+"Bestätigt"),
                        ""+ChatColor.BLUE+ChatColor.BOLD+"Kontaktdaten"+ChatColor.GRAY+ChatColor.BOLD+": "+(!selectedContact ? ChatColor.RED+"Nicht bestätigt" : ChatColor.GREEN+"Bestätigt"),
                        "",
                        ""+ChatColor.GRAY+"Du musst allen Punkten zustimmen, um am Event teilnehmen zu können.")
                .toItemStack());
    }


    public static boolean onClick(InventoryClickEvent event) {
        if (!event.getInventory().getTitle().equals(inventoryTitle)) return false;
        if (!(event.getWhoClicked() instanceof Player)) return false;
        Player player = (Player) event.getWhoClicked();
        if (player == null) return false;
        ItemStack stack = event.getCurrentItem();
        if (!stack.hasItemMeta()) return false;
        if (!stack.getItemMeta().hasDisplayName()) return false;

        NGEventInventoryHandler handler = getHandler().get(player.getUniqueId());

        if (stack.getItemMeta().getDisplayName().equals(" ")) {
            if (stack.getType().equals(Material.STAINED_GLASS_PANE)) {
                return true;
            }

        } else if (stack.getItemMeta().getDisplayName().equals(teamspeakTitle)) {
            handler.teamspeak = !stack.getItemMeta().hasEnchants();
            InvItem item = handler.getTeamspeakItem(handler.teamspeak);
            event.getInventory().setItem(item.getSlot(), item.getItemStack());

        } else if (stack.getItemMeta().getDisplayName().equals(voiceTitle)) {
            handler.voice = !stack.getItemMeta().hasEnchants();
            InvItem item = handler.getVoiceItem(handler.voice);
            event.getInventory().setItem(item.getSlot(), item.getItemStack());

        } else if (stack.getItemMeta().getDisplayName().equals(dateTitle)) {
            handler.date = !stack.getItemMeta().hasEnchants();
            InvItem item = handler.getDateItem(handler.date);
            event.getInventory().setItem(item.getSlot(), item.getItemStack());

        } else if (stack.getItemMeta().getDisplayName().equals(contactTitle)) {
            handler.contact = !stack.getItemMeta().hasEnchants();
            InvItem item = handler.getContactItem(handler.contact);
            event.getInventory().setItem(item.getSlot(), item.getItemStack());

        } else if (stack.getItemMeta().getDisplayName().equals(sendTitle)) {
            if (!handler.teamspeak || !handler.voice || !handler.date || !handler.contact) {
                player.sendMessage(awv.getPrefix()+ChatColor.RED+"Du musst den Event bedingungen zustimmen.");
                return true;
            }

            // Put player into database, send success messages
            database.add(handler.candidate);
            getHandler().remove(player.getUniqueId());
            player.closeInventory();
            player.sendMessage(awv.getPrefix() + "Du hast dich erfolgreich als Teilnehmer für das Haus "+handler.candidate.getTeam().getColor()+handler.candidate.getTeam().getDisplayName()+ ChatColor.GRAY+" beworben.");
            awv.getInstance().getLogger().info(handler.player.getUniqueId() + " hat sich beim Event f"+ utility.ue+"r das Haus " + handler.candidate.getTeam().getDisplayName() + " beworben");
            return true;
        }

        InvItem item = handler.getSendItem(handler.teamspeak, handler.voice, handler.date, handler.contact);
        event.getInventory().setItem(item.getSlot(), item.getItemStack());
        player.updateInventory();
        return true;
    }

    public static void onClose(InventoryCloseEvent event) {
        if (!event.getInventory().getTitle().equals(inventoryTitle)) return;
        if (!(event.getPlayer() instanceof Player)) return;
        Player player = (Player) event.getPlayer();
        if (player == null) return;
        if (!getHandler().containsKey(player.getUniqueId())) return;
        player.sendMessage(awv.getPrefix()+ChatColor.RED+"Du hast deinen Bewerbungsvorgang abgebrochen!");
    }
}
