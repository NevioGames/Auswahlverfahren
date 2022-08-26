package de.neviogames.auswahlverfahren.utils;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.UUID;

@Getter @Setter
public class NGEventCandidate {

    private final Player player;
    private final UUID uniqueId;
    private final long timestamp;
    private final String contact;
    private NGEventTeam team;
    private NGEventGroup group;


    public NGEventCandidate(Player player, String contact) {
        this.player = player;
        this.uniqueId = player.getUniqueId();
        this.contact = contact;
        this.timestamp = System.currentTimeMillis();
    }

    public NGEventCandidate(Player player, String contact, NGEventTeam team) {
        this(player, contact);
        this.team = team;
    }

    public int getWhitelistId() {
        return this.group == null ? 0 : this.group.getWhitelistId();
    }
}
