/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.keestchain.listeners;

import com.keest.keestchain.Principal;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.potion.PotionEffect;

/**
 *
 * @author Samuel
 */
public class PlayerSaiu implements Listener {

    private Principal plugin;

    public PlayerSaiu(Principal main) {
        this.plugin = main;
    }

    @EventHandler
    public void playerSai(PlayerQuitEvent ev) {
        Player jogador = ev.getPlayer();
        if (this.plugin.getJogadores().contains(jogador.getName())) {

            for (String jogadores : this.plugin.getJogadores()) {
                Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorDeslogou").replace("@jogador@", String.valueOf(this.plugin.getJogadores().size())));
            }

            for (String jogadores : this.plugin.getCamarotes()) {
                Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorDeslogou").replace("@jogador@", String.valueOf(this.plugin.getJogadores().size())));
            }

            for (PotionEffect efeitos : jogador.getActivePotionEffects()) {
                jogador.removePotionEffect(efeitos.getType());
            }

            this.plugin.desequipaJogador(jogador);

            this.plugin.delJogador(jogador.getName());
        }

        if (this.plugin.getCamarotes().contains(jogador.getName())) {
            this.plugin.delCamarote(jogador.getName());
        }

    }
}
