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
import org.bukkit.event.player.PlayerCommandPreprocessEvent;

/**
 *
 * @author Samuel
 */
public class PlayerDigiteCmd implements Listener {

    private Principal plugin;

    public PlayerDigiteCmd(Principal main) {
        this.plugin = main;
    }

    @EventHandler
    public void playerDigita(PlayerCommandPreprocessEvent ev) {

        if (this.plugin.getJogadores().contains(ev.getPlayer().getName())) {
            Player jogador = ev.getPlayer();

            if (jogador.hasPermission("keestchain.admin")) {
                return;
            }

            if (ev.getMessage().contains(" ")) {

                if (!this.plugin.getConfigCache().getStringList("Opcoes.ComandosPermitidos").contains(ev.getMessage().substring(0, ev.getMessage().lastIndexOf(" ")))) {
                    jogador.sendMessage(this.plugin.getMsg("NaoPodeUsarCmds"));
                    ev.setCancelled(true);

                }
            } else if (!this.plugin.getConfigCache().getStringList("Opcoes.ComandosPermitidos").contains(ev.getMessage())) {
                jogador.sendMessage(this.plugin.getMsg("NaoPodeUsarCmds"));
                ev.setCancelled(true);

            }
        }

    }
}
