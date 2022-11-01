package de.neviogames.auswahlverfahren.utils;

import lombok.Getter;

@Getter
public class NGEventGroup {

    private final int groupId;
    private final int groupSize;
    private final String selectionText;

    public NGEventGroup(int groupId, int groupSize, String selectionText) {
        this.groupId = groupId;
        this.groupSize = groupSize;
        this.selectionText = selectionText;
    }

    public String getFormattedSelectionText(NGEventTeam team) {
        return getFormattedSelectionText(team.getDisplayName(), team.getColor());
    }

    public String getFormattedSelectionText(String teamDisplayName, String color) {
        return this.selectionText
                .replaceAll("%color", color)
                .replaceAll("%display", teamDisplayName);
    }

    public int getWhitelistId() {
        return this.groupId;
    }

}
