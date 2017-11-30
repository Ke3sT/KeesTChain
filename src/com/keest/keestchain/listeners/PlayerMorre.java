/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.keestchain.listeners;

import com.keest.keestchain.Principal;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

/**
 *
 * @author Samuel
 */
public class PlayerMorre implements Listener {

    private Principal plugin;

    public PlayerMorre(Principal main) {
        this.plugin = main;
    }

    @EventHandler
    public void playerMorreu(PlayerDeathEvent ev) {

        if (this.plugin.getJogadores().contains(ev.getEntity().getName())) {
            Player morreu = (Player) ev.getEntity();

            if (this.plugin.tocarSom()) {
                morreu.playSound(morreu.getLocation(), Sound.VILLAGER_NO, 1, 2);
            }

            ev.setDeathMessage(null);
            ev.setDroppedExp(0);

            if (this.plugin.getConfigCache().getString("Opcoes.NaoDroparItemAoMorrer").equals("true")) {
                ev.getDrops().clear();
            }

            if (ev.getEntity().getKiller() instanceof Player) {
                Player matou = (Player) ev.getEntity().getKiller();

                if (this.plugin.mostrarFogos()) {
                    this.plugin.efeitosFogos(matou);
                }

                if (this.plugin.getConfigCache().getString("Opcoes.DarMoneyAoMatar").equals("true")) {
                    this.plugin.adicionaMoney(matou.getName(), this.plugin.getConfigCache().getDouble("Opcoes.MoneyAoMatar"));
                }

                if (this.plugin.tocarSom()) {
                    matou.playSound(matou.getLocation(), Sound.ORB_PICKUP, 1, 2);
                }

                for (String jogadores : this.plugin.getJogadores()) {
                    if (matou.getName().equalsIgnoreCase(jogadores)) {
                        matou.sendMessage(this.plugin.getMsg("VoceMatou").replace("@jogador@", morreu.getName()).replace("@money@", this.plugin.getConfigCache().getString("Opcoes.MoneyAoMatar")));
                    } else if (morreu.getName().equalsIgnoreCase(jogadores)) {
                        morreu.sendMessage(this.plugin.getMsg("VoceMorreu").replace("@jogador@", matou.getName()));
                        this.plugin.getTitles.enviaTitulo(morreu, "VoceMorreu");
                    } else {
                        if (this.plugin.tocarSom()) {
                            Bukkit.getPlayer(jogadores).playSound(Bukkit.getPlayer(jogadores).getLocation(), Sound.ORB_PICKUP, 1, 2);
                        }

                        Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorMatou").replace("@jogadormorto@", morreu.getName()).replace("@jogadormatou@", matou.getName()));
                    }
                }

                for (String camarotes : this.plugin.getCamarotes()) {
                    Bukkit.getPlayer(camarotes).sendMessage(this.plugin.getMsg("JogadorMatou").replace("@jogadormorto@", morreu.getName()).replace("@jogadormatou@", matou.getName()));
                    Bukkit.getPlayer(camarotes).playSound(Bukkit.getPlayer(camarotes).getLocation(), Sound.ORB_PICKUP, 1, 2);
                }

                this.plugin.delJogador(morreu.getName());
                this.plugin.desequipaJogador(morreu);

                return;
            }

            for (String jogadores : this.plugin.getJogadores()) {
                if (morreu.getName().equalsIgnoreCase(jogadores)) {
                    morreu.sendMessage(this.plugin.getMsg("VoceMorreuDesconhecido"));
                } else {
                    if (this.plugin.tocarSom()) {
                        Bukkit.getPlayer(jogadores).playSound(Bukkit.getPlayer(jogadores).getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("MorreuDesconhecido").replace("@jogador@", morreu.getName()));
                }
            }

            for (String camarotes : this.plugin.getCamarotes()) {
                Bukkit.getPlayer(camarotes).sendMessage(this.plugin.getMsg("MorreuDesconhecido").replace("@jogador@", morreu.getName()));
                Bukkit.getPlayer(camarotes).playSound(Bukkit.getPlayer(camarotes).getLocation(), Sound.ORB_PICKUP, 1, 2);
            }

            this.plugin.delJogador(morreu.getName());
            this.plugin.desequipaJogador(morreu);
        }

        if (this.plugin.getCamarotes().contains(ev.getEntity().getName())) {

            if (this.plugin.getConfigCache().getString("Opcoes.NaoDroparItemAoMorrer").equals("true")) {
                ev.getDrops().clear();
            }

            this.plugin.delCamarote(ev.getEntity().getName());
        }

    }

}
