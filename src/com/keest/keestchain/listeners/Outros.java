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
import org.bukkit.event.player.PlayerDropItemEvent;

/**
 *
 * @author Samuel
 */
public class Outros implements Listener {
    
    private Principal plugin;
    
    public Outros(Principal main) {
        this.plugin = main;
    }
    
    @EventHandler
    public void playerDropaItem(PlayerDropItemEvent ev) {
        
        if (this.plugin.getJogadores().contains(ev.getPlayer().getName())) {
            Player jogador = ev.getPlayer();
            
            if (jogador.hasPermission("keestchain.admin")) {
                return;
            }
            
            if (this.plugin.getConfigCache().getString("Opcoes.BloquearOPlayerDeDroparItem").equals("true")) {
                ev.setCancelled(true);
                jogador.sendMessage(this.plugin.getMsg("NaoPodeDroparItem"));
            }
        }   
    }
    
}
