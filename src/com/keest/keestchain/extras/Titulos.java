/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.keestchain.extras;

import com.keest.keestchain.Principal;
import java.util.HashMap;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

/**
 *
 * @author Samuel
 */
public class Titulos {

    private Principal plugin;

    public Titulos(Principal main) {
        this.plugin = main;
    }

    private static HashMap<String, String> titleMessages = new HashMap<>();
    private static HashMap<String, Boolean> titleConfig = new HashMap<>();

    public void carregaTitulos() {

        titleMessages.clear();

        ConfigurationSection titlesMsg = this.plugin.getConfigCache().getConfigurationSection("Titulos");

        for (String key : titlesMsg.getKeys(true)) {
            String msg;

            if (key.contains("Titulo") || key.contains("Subtitulo")) {
                msg = titlesMsg.getString(key);

                titleMessages.put(key, msg);

            } else if (key.contains("Ativar")) {
                titleConfig.put(key, titlesMsg.getBoolean(key));
            }
        }
    }

    public void enviaTitulo(Player jogador, String titulo) {

        if (this.plugin.getVersao()) {

            if (titleConfig.get(titulo + ".Ativar") == true) {

                String msg1 = titleMessages.get(titulo + ".Titulo");
                String msg2 = titleMessages.get(titulo + ".Subtitulo");

                jogador.sendTitle(ff(msg1), ff(msg2));
            }
        }
    }

    private String ff(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
