package de.neviogames.auswahlverfahren.utils;

import de.neviogames.auswahlverfahren.awv;
import de.neviogames.auswahlverfahren.inventory.ItemAPIv2;
import de.neviogames.nglib.utils.io.MathUtil;
import de.neviogames.nglib.utils.misc.FormatUtil;
import de.neviogames.nglib.utils.utility;
import lombok.Getter;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.CommandSender;
import org.bukkit.inventory.ItemStack;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;

@Getter
public class NGEventSelectHandler {

    private final CommandSender sender;
    private final NGEventTeam team;
    private final int minTeamSize;
    private final Map<Integer, List<UUID>> fixedPlayer;
    private final ArrayList<UUID> randomList;


    public NGEventSelectHandler(CommandSender sender, NGEventTeam team, int random) {
        this.sender = sender;
        this.team = team;
        this.minTeamSize = getMinTeamSize();
        this.fixedPlayer = initFixedPlayers();
        // Get a randomized list from a team
        this.randomList = Randomizer.randomList(team, random);

    }

    private int getMinTeamSize() {
        if (Util.isNullOrEmpty(Configuration.getInstance().getGroups())) {
            throw new NullPointerException("No Groups loaded");
        }
        return Configuration.getInstance().getGroups().stream().mapToInt(NGEventGroup::getGroupSize).sum();
    }

    private Map<Integer, List<UUID>> initFixedPlayers() {
        Map<Integer, List<UUID>> fixedPlayer = new HashMap<>();
        if (Util.isNullOrEmpty(Configuration.getInstance().getFixedPlayers())) return fixedPlayer;
        if (!Configuration.getInstance().getFixedPlayers().containsKey(this.team.getName())) return fixedPlayer;
        Configuration.getInstance().getFixedPlayers().get(this.team.getName()).forEach(fp -> {
            List<UUID> uuids = fixedPlayer.getOrDefault(fp.getGroup(), new ArrayList<>());
            uuids.add(fp.getUniqueId());
            fixedPlayer.put(fp.getGroup(), uuids);
        });
        return fixedPlayer;
    }

    // Check team size
    @SuppressWarnings("BooleanMethodIsAlwaysInverted")
    public boolean hasTeamEnoughPlayer() {
        if (this.randomList.size()+this.fixedPlayer.size() < this.minTeamSize) {
            this.sender.sendMessage(awv.getPrefix() + ChatColor.RED+ "Es haben sich nicht genug Spieler im Haus " +this.team.getColor()+ this.team.getDisplayName()+ChatColor.RED+ " beworben.");
            return false;
        }
        return true;
    }

    // Get a random candidate from the randomized list and build a String
    public String[] select() {
        String[] selectedCandidates = new String[Configuration.getInstance().getGroups().size()];
        int pageCounter = 0;

        for (NGEventGroup group : Configuration.getInstance().getGroups()) {
            selectedCandidates[pageCounter] = select(group);
            pageCounter++;
        }

        saveCandidates(selectedCandidates);
        return selectedCandidates;
    }

    private String select(NGEventGroup group) {
        StringBuilder page = new StringBuilder(group.getFormattedSelectionText(this.team) + "\n\n");
        int totalFixedPlayers = this.fixedPlayer.containsKey(group.getGroupId()) ? this.fixedPlayer.get(group.getGroupId()).size() : 0;

        for (int i = 0; i < group.getGroupSize(); i++) {
            UUID candidateUUID;
            if (i >= totalFixedPlayers) {
                candidateUUID = this.randomList.get(MathUtil.fairRoundedRandom(0, this.randomList.size()));
            } else {
                candidateUUID = this.fixedPlayer.get(group.getGroupId()).get(i);
            }

            String candidateName = utility.getNameFromUUID(candidateUUID);
            page.append(ChatColor.BLACK).append(ChatColor.BOLD)
                    .append(i+1).append(". ")
                    .append(this.team.getColor())
                    .append(candidateName)
                    .append("\n");

            removeAndWhitelist(candidateUUID, group.getWhitelistId());
        }
        return page.toString().trim();
    }

    private void removeAndWhitelist(UUID candidateUUID, int whitelistId) {
        // remove candidate from the randomized list and whitelist the player for the event
        randomList.remove(candidateUUID);
        database.setWhitelist(candidateUUID, whitelistId);
    }

    // save candidates in a file
    private void saveCandidates(String[] candidateString) {
        try {
            FileWriter outStream = new FileWriter(awv.getInstance().getDataFolder()+"/candidates-"+Configuration.getInstance().getEventName()+".txt", true);
            BufferedWriter out = new BufferedWriter(outStream);

            StringBuilder saveText = new StringBuilder();
            for (int i = 0; i < candidateString.length; i++) {
                saveText.append(new SimpleDateFormat("dd.MM.yyyy HH:mm:ss").format(System.currentTimeMillis()))
                        .append(" - Team: ").append(this.team.getDisplayName())
                        .append(" - Group: ").append(i+1)
                        .append("\n")
                        .append(candidateString[i])
                        .append("\n\n");
            }

            String save = FormatUtil.stripFormat(saveText.toString());
            awv.getInstance().getLogger().info(save);

            save += "\n\n";
            save = save.replaceAll("\n", System.getProperty("line.separator"));

            out.append(save);
            out.close();
            outStream.close();
        } catch (IOException ioException) {
            ioException.printStackTrace();
        }
    }

    public ItemStack getBook(String[] candidateString) {
        return new ItemAPIv2(Material.WRITTEN_BOOK)
                .setBookAuthor(ChatColor.AQUA+"Auswahlverfahren")
                .setDisplayName(""+this.team.getColor()+ChatColor.BOLD+this.team.getDisplayName())
                .setBookTitle("Die Auserw"+utility.ae+"hlten von " +this.team.getColor()+ this.team.getDisplayName())
                .setBookPages(candidateString)
                .toItemStack();
    }


}
