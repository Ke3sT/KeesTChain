/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.keestchain.listeners;

import com.keest.keestchain.Principal;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerRespawnEvent;

/**
 *
 * @author Samuel
 */
public class PlayerRenasce implements Listener {

    private Principal plugin;

    public PlayerRenasce(Principal main) {
        this.plugin = main;
    }

    @EventHandler
    public void playerRespawna(PlayerRespawnEvent ev) {

        Player jogador = ev.getPlayer();

        this.plugin.desequipaJogador(jogador);

    }

}
