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
import org.bukkit.event.entity.EntityDamageByEntityEvent;

/**
 *
 * @author Samuel
 */
public class PlayerDamage implements Listener {

    private Principal plugin;

    public PlayerDamage(Principal main) {
        this.plugin = main;
    }

    @EventHandler
    public void playerHita(EntityDamageByEntityEvent ev) {

        if (ev.getDamager() instanceof Player) {
            Player damager = (Player) ev.getDamager();

            if (ev.getEntity() instanceof Player) {
                Player hitado = (Player) ev.getEntity();

                if (!this.plugin.getJogadores().contains(damager.getName()) && this.plugin.getJogadores().contains(hitado.getName())) {
                    ev.setCancelled(true);
                    damager.sendMessage(this.plugin.getMsg("NaoPodeAcertar"));
                } else if (this.plugin.getJogadores().contains(damager.getName()) && !this.plugin.getJogadores().contains(hitado.getName())) {
                    ev.setCancelled(true);
                    damager.sendMessage(this.plugin.getMsg("NaoPodeAcertar2"));
                } else if (this.plugin.getCamarotes().contains(damager.getName()) && this.plugin.getCamarotes().contains(hitado.getName())) {
                    ev.setCancelled(true);
                    damager.sendMessage(this.plugin.getMsg("NaoPodeAcertar3"));
                }
            }

        }

    }
}
