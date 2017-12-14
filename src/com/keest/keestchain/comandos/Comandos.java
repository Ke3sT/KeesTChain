/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.keestchain.comandos;

import com.keest.keestchain.Principal;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

/**
 *
 * @author Samuel
 */
public class Comandos implements CommandExecutor {

    private Principal plugin;

    public Comandos(Principal main) {
        this.plugin = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Player jogador = (Player) sender;

        if (sender instanceof Player) {

            if (cmd.getName().equalsIgnoreCase("chain")) {

                if (args.length == 0) {

                    for (String msg : this.plugin.getMsgMulti("ComandoNormal")) {
                        jogador.sendMessage(msg);
                    }

                    if (jogador.hasPermission("keestchain.admin")) {
                        for (String msg : this.plugin.getMsgMulti("ComandoAdmin")) {
                            jogador.sendMessage(msg);
                        }
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("reload")) {
                    if (!jogador.hasPermission("keestchain.admin")) {
                        jogador.sendMessage(this.plugin.getMsg("SemPermissao"));
                        return true;
                    }

                    this.plugin.salvaConfig();
                    this.plugin.carregaConfig();

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    jogador.sendMessage(this.plugin.getMsg("ConfigReload"));
                    return true;
                }

                if (args[0].equalsIgnoreCase("setarkit")) {
                    if (!jogador.hasPermission("keestchain.admin")) {
                        jogador.sendMessage(this.plugin.getMsg("SemPermissao"));
                        return true;
                    }

                    for (int loop = 0; loop <= 35; loop++) {
                        this.plugin.editaDados.set("Itens.Slot." + loop, jogador.getInventory().getItem(loop));
                    }

                    for (int loop = 36; loop <= 39; loop++) {
                        this.plugin.editaDados.set("Armadura.Slot." + loop, jogador.getInventory().getItem(loop));
                    }
                    this.plugin.salvaKit();

                    jogador.getInventory().clear();
                    jogador.getInventory().setArmorContents(null);
                    jogador.sendMessage(this.plugin.getMsg("KitDefinido"));

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.HORSE_ARMOR, 1, 2);
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("setarspawn")) {
                    if (!jogador.hasPermission("keestchain.admin")) {
                        jogador.sendMessage(this.plugin.getMsg("SemPermissao"));
                        return true;
                    }

                    this.plugin.setLocal("spawn", jogador.getLocation());
                    this.plugin.editaDados.set("Lugares.Spawn", jogador.getWorld().getName() + ";" + jogador.getLocation().getBlockX()+ ";" + jogador.getLocation().getBlockY() + ";" + jogador.getLocation().getBlockZ() + ";" + jogador.getLocation().getYaw() + ";" + jogador.getLocation().getPitch());
                    this.plugin.salvaKit();
                    jogador.sendMessage(this.plugin.getMsg("SpawnSetado"));
                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("setarcamarote")) {
                    if (!jogador.hasPermission("keestchain.admin")) {
                        jogador.sendMessage(this.plugin.getMsg("SemPermissao"));
                        return true;
                    }

                    this.plugin.setLocal("camarote", jogador.getLocation());
                    this.plugin.editaDados.set("Lugares.Camarote", jogador.getWorld().getName() + ";" + jogador.getLocation().getBlockX()+ ";" + jogador.getLocation().getBlockY() + ";" + jogador.getLocation().getBlockZ() + ";" + jogador.getLocation().getYaw() + ";" + jogador.getLocation().getPitch());
                    this.plugin.salvaKit();
                    jogador.sendMessage(this.plugin.getMsg("CamaroteSetado"));
                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("setarsaida")) {
                    if (!jogador.hasPermission("keestchain.admin")) {
                        jogador.sendMessage(this.plugin.getMsg("SemPermissao"));
                        return true;
                    }

                    this.plugin.setLocal("saida", jogador.getLocation());
                    this.plugin.editaDados.set("Lugares.Saida", jogador.getWorld().getName() + ";" + jogador.getLocation().getBlockX() + ";" + jogador.getLocation().getBlockY() + ";" + jogador.getLocation().getBlockZ() + ";" + jogador.getLocation().getYaw() + ";" + jogador.getLocation().getPitch());
                    this.plugin.salvaKit();
                    jogador.sendMessage(this.plugin.getMsg("SaidaSetada"));
                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    return true;
                }

                if (args[0].equalsIgnoreCase("entrar")) {

                    if (this.plugin.getJogadores().contains(jogador.getName())) {
                        jogador.sendMessage(this.plugin.getMsg("JaEntrou"));
                        return true;
                    }

                    if (!this.plugin.guardaInventario()) {
                        if (this.plugin.contemItems(jogador)) {
                            jogador.sendMessage(this.plugin.getMsg("EsvazieInventario"));
                            return true;
                        }
                    }

                    if (this.plugin.getCamarotes().contains(jogador.getName())) {
                        this.plugin.delCamarote(jogador.getName());
                    }

                    for (PotionEffect efeitos : jogador.getActivePotionEffects()) {
                        jogador.removePotionEffect(efeitos.getType());
                    }

                    jogador.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 28, 10, true, false));

                    this.plugin.tpSpawn(jogador);
                    this.plugin.getTitles.enviaTitulo(jogador, "VoceEntrou");
                    jogador.sendMessage(this.plugin.getMsg("VoceEntrou").replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));

                    for (String jogadores : this.plugin.getJogadores()) {
                        Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorEntrou").replace("@jogador@", jogador.getName()).replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));
                    }

                    for (Player jogadores : Bukkit.getServer().getOnlinePlayers()) {
                        if (!this.plugin.getJogadores().contains(jogadores.getName()) && !jogadores.getName().equalsIgnoreCase(jogador.getName())) {
                            jogadores.sendMessage(this.plugin.getMsg("JogadorEntrouGlobal").replace("@jogador@", jogador.getName()));
                        }
                    }

                    for (String jogadores : this.plugin.getCamarotes()) {
                        Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorEntrou").replace("@jogador@", jogador.getName()).replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));
                    }

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.AMBIENCE_THUNDER, 1, 2);
                    }

                    this.plugin.addJogador(jogador.getName());
                    this.plugin.equipaJogador(jogador);

                    return true;
                }

                if (args[0].equalsIgnoreCase("sair")) {
                    if (!this.plugin.getJogadores().contains(jogador.getName())) {
                        jogador.sendMessage(this.plugin.getMsg("NaoEsta"));
                        return true;
                    }

                    for (PotionEffect efeitos : jogador.getActivePotionEffects()) {
                        jogador.removePotionEffect(efeitos.getType());
                    }

                    jogador.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 28, 10, true, false));

                    this.plugin.tpSaida(jogador);
                    this.plugin.getTitles.enviaTitulo(jogador, "VoceSaiu");
                    jogador.sendMessage(this.plugin.getMsg("VoceSaiu").replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));
                    this.plugin.delJogador(jogador.getName());

                    for (String jogadores : this.plugin.getJogadores()) {
                        Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorSaiu").replace("@jogador@", jogador.getName()).replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));
                    }

                    for (String jogadores : this.plugin.getCamarotes()) {
                        Bukkit.getPlayer(jogadores).sendMessage(this.plugin.getMsg("JogadorSaiu").replace("@jogador@", jogador.getName()).replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));
                    }

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.AMBIENCE_RAIN, 1, 2);
                    }

                    this.plugin.desequipaJogador(jogador);

                    return true;
                }

                if (args[0].equalsIgnoreCase("assistir")) {

                    if (this.plugin.getJogadores().contains(jogador.getName())) {
                        jogador.sendMessage(this.plugin.getMsg("PrecisaSairDoChain"));
                        return true;
                    }

                    if (this.plugin.getCamarotes().contains(jogador.getName())) {
                        jogador.sendMessage(this.plugin.getMsg("JaEstaCamarote"));
                        return true;
                    }

                    jogador.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 28, 10, true, false));

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    jogador.sendMessage(this.plugin.getMsg("VoceEntrouCamarote"));

                    this.plugin.addCamarote(jogador.getName());
                    this.plugin.tpCamarote(jogador);
                    return true;
                }

                if (args[0].equalsIgnoreCase("sairassistir")) {

                    if (!this.plugin.getCamarotes().contains(jogador.getName())) {
                        jogador.sendMessage(this.plugin.getMsg("NaoEstaCamarote"));
                        return true;
                    }

                    jogador.addPotionEffect(new PotionEffect(PotionEffectType.BLINDNESS, 28, 10, true, false));

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    jogador.sendMessage(this.plugin.getMsg("VoceSaiuCamarote"));

                    this.plugin.tpSaida(jogador);
                    this.plugin.delCamarote(jogador.getName());

                    return true;
                }

                if (args[0].equalsIgnoreCase("status")) {

                    jogador.sendMessage(this.plugin.getMsg("Status").replace("@jogadores@", String.valueOf(this.plugin.getJogadores().size())));

                    if (this.plugin.tocarSom()) {
                        jogador.playSound(jogador.getLocation(), Sound.ORB_PICKUP, 1, 2);
                    }

                    return true;
                }

                return true;
            }
        }
        return true;
    }

    private String ff(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

}
