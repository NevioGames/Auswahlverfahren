package de.neviogames.auswahlverfahren.utils;

import lombok.Getter;

import java.util.UUID;

@Getter
public class NGEventFixedPlayer {


    private final UUID uniqueId;
    private final String team;
    private final int group;

    public NGEventFixedPlayer(UUID uniqueId, String team, int group) {
        this.uniqueId = uniqueId;
        this.team = team;
        this.group = group;
    }

    public NGEventTeam getNGEventTeam() {
        if (!Configuration.getInstance().getTeams().containsKey(this.team)) return null;
        return Configuration.getInstance().getTeams().get(this.team);
    }

    public NGEventGroup getNGEventGroup() {
        for (NGEventGroup eventGroup : Configuration.getInstance().getGroups()) {
            if (eventGroup.getGroupId() == this.group) return eventGroup;
        }
        return null;
    }
}
