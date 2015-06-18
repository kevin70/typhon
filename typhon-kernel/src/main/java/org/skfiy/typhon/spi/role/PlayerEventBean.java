package org.skfiy.typhon.spi.role;

import org.skfiy.typhon.domain.Player;

import org.skfiy.typhon.domain.Incident;

public class PlayerEventBean {

    private final Player player;
    private final Incident incident;

    public PlayerEventBean(Player player, Incident incident) {
        this.player = player;
        this.incident = incident;
    }

    public Player getPlayer() {
        return player;
    }

    public Incident getIncident() {
        return incident;
    }

}
