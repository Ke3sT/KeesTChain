/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.keest.keestchain;

import com.keest.keestchain.comandos.Comandos;
import com.keest.keestchain.extras.Titulos;
import com.keest.keestchain.listeners.Outros;
import com.keest.keestchain.listeners.PlayerDamage;
import com.keest.keestchain.listeners.PlayerDigiteCmd;
import com.keest.keestchain.listeners.PlayerMorre;
import com.keest.keestchain.listeners.PlayerRenasce;
import com.keest.keestchain.listeners.PlayerSaiu;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.milkbowl.vault.economy.Economy;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Color;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Firework;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.FireworkMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Samuel
 */
public class Principal extends JavaPlugin {

    public static Plugin plugin;

    public static Plugin getPlugin() {
        return plugin;
    }

    public Titulos getTitles = new Titulos(this);

    ////////////////// [ VARS DO PLUGIN ] /////////////////////
    private ConfigurationSection configCache;
    private ArrayList<String> jogadoresChain = new ArrayList<>();
    private ArrayList<String> jogadoresCamarote = new ArrayList<>();
    private Location spawn, camarote, saida;
    private HashMap<Integer, ItemStack> chainKit = new HashMap<>();
    private HashMap<String, HashMap<Integer, ItemStack>> jogadoresItens = new HashMap<>();

    private File chainDados;
    public FileConfiguration editaDados;
    //////////////////////////////////////////////////

    //////////////////////////// [ VARS DE API's ] ///////////////////
    private static Economy economy = null;

    /////////////////////////////////////////////
    //public Dados sql = new Dados();
    @Override
    public void onEnable() {
        plugin = this;
        if (plugin.getName().equals("KeesTChain")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "-#-#-#-#-#-#-#-#-#--#-[KeesTChain]-#-#-#-#-#-#-#-#-#-#-");
            Bukkit.getConsoleSender().sendMessage(ChatColor.YELLOW + "                   Plugin Ativando...!");
            criaConfig();
            if (estaAtualizado()) {
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "                   Plugin Ativado!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "             Este plugin foi criado por KeesT!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.AQUA + "      Caso encontre bugs, me informe no Skype: samukatb!");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "                     Versao: 1.0.1");
                Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "   #-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#-#--#-#");
                carregaConfig();
                carregaEconomy();
                getCommand("chain").setExecutor(new Comandos(this));
                carregaListeners();
            } else {
                Bukkit.getPluginManager().disablePlugin(this);
            }

        } else {
            Bukkit.getPluginManager().disablePlugin(this);
        }

        //sql.load("localhost", "servidor1", "root", "123");
        // sql.update("create table if not exists jogadores (jogador varchar(16) not null, dinheiro int, primary key (jogador));");
    }

    @Override
    public void onDisable() {

        for (String jogadores : getJogadores()) {
            tpSaida(Bukkit.getPlayer(jogadores));
            desequipaJogador(Bukkit.getPlayer(jogadores));
        }

        for (String camarotes : getCamarotes()) {
            tpSaida(Bukkit.getPlayer(camarotes));
        }

        getJogadores().clear();
        getCamarotes().clear();

        plugin = null;
    }

    ////////////// [ METODOS DE GET/SET ]/////////////////////////////////////////
    public String getMsg(String msg) {
        return ff(configCache.getString("Mensagens." + msg));
    }

    public ArrayList<String> getMsgMulti(String msg) {
        return ffM((ArrayList<String>) configCache.getStringList("Mensagens." + msg));
    }
    ////////////////////////////////////////////////////////////////

    ////////////////// [ METODOS DE CONFIG ] /////////////////
    public void criaConfig() {
        File confiG = new File(plugin.getDataFolder().getName() + File.separatorChar + "config.yml", "UTF-8");

        if (!confiG.exists()) {

            saveResource("config.yml", false);
            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "            Criando um novo arquivo config.yml!");
        } else {

            Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN + "                A configuracao foi carregada!");
        }

    }

    public void carregaConfig() {
        configCache = getConfig().getConfigurationSection("Configurar");
        getTitles.carregaTitulos();
        carregaKit();
        carregaLugar();
    }

    public ConfigurationSection getConfigCache() {
        return configCache;
    }

    public void salvaConfig() {
        reloadConfig();
    }
    //////////////////////////////////////////////////////////////////

    //////////////////[ METODOS EXTRAS] /////////////////////
    private void carregaListeners() {
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerSaiu(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDamage(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerMorre(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerDigiteCmd(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new Outros(this), plugin);
        Bukkit.getServer().getPluginManager().registerEvents(new PlayerRenasce(this), plugin);
    }

    private boolean estaAtualizado() {
        String vers = getConfig().getString("Versao");

        if (!vers.equalsIgnoreCase("1.0.1")) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "          A versão da config plugin esta incorreta!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "  Salve suas informacoes da config.yml que querer e apague-a!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "         Para que a versao mais recente seja gerada!!");
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "                      Plugin desativado!");
            return false;
        } else {
            return true;
        }
    }

    public boolean tocarSom() {
        if (getConfigCache().getString("Opcoes.AtivarSons").equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public boolean mostrarFogos() {
        if (getConfigCache().getString("Opcoes.EfeitosFogos").equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public void efeitosFogos(Player jogador) {
        if (jogador != null) {
            Firework f = (Firework) jogador.getPlayer().getWorld().spawn(jogador.getPlayer().getLocation(), Firework.class);
            FireworkMeta fm = f.getFireworkMeta();
            fm.addEffect(FireworkEffect.builder()
                    .flicker(true)
                    .trail(false)
                    .with(FireworkEffect.Type.BURST)
                    .withColor(Color.GREEN, Color.BLUE)
                    .withFade(Color.RED)
                    .build());
            fm.setPower(0);
            f.setFireworkMeta(fm);
        }
    }

    private String ff(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    private ArrayList<String> ffM(ArrayList<String> msg) {
        ArrayList<String> array = new ArrayList<>();

        for (String linha : msg) {
            array.add(ff(linha));
        }

        return array;
    }

    public ArrayList<String> getJogadores() {
        return jogadoresChain;
    }

    public ArrayList<String> getCamarotes() {
        return jogadoresCamarote;
    }

    public HashMap<Integer, ItemStack> getKit() {
        return chainKit;
    }

    public void tpSpawn(Player jogador) {
        jogador.teleport(spawn);
    }

    public void tpSaida(Player jogador) {
        jogador.teleport(saida);
    }

    public void tpCamarote(Player jogador) {
        jogador.teleport(camarote);
    }

    public boolean contemItems(Player jogador) {
        for (ItemStack item : jogador.getInventory().getContents()) {
            if (item != null) {
                return true;
            }
        }

        return false;
    }

    public boolean guardaInventario() {
        if (getConfigCache().getString("Opcoes.GuardarInventario").equals("true")) {
            return true;
        } else {
            return false;
        }
    }

    public void desequipaJogador(Player jogador) {
        if (getConfigCache().getString("Opcoes.NaoRemoverCabecas").equals("true")) {
            for (int loop = 0; loop <= 39; loop++) {

                if (jogador.getInventory().getItem(loop) != null) {

                    if (jogador.getInventory().getItem(loop).getType() != Material.SKULL || jogador.getInventory().getItem(loop).getType() != Material.SKULL_ITEM) {
                        jogador.getInventory().setItem(loop, null);
                    }

                }

            }
        } else {
            jogador.getInventory().clear();
            jogador.getInventory().setArmorContents(null);
        }

        devolveInventario(jogador);
    }

    public void devolveInventario(Player jogador) {
        if (guardaInventario()) {
            if (jogadoresItens.containsKey(jogador.getName())) {
                
                if (jogadoresItens.containsKey(jogador.getName())) {
                    HashMap<Integer, ItemStack> map = jogadoresItens.get(jogador.getName());

                    for (int item : map.keySet()) {

                        if (jogador.getInventory().getItem(item) == null) {
                            jogador.getInventory().setItem(item, map.get(item));
                        } else {
                            jogador.getInventory().addItem(map.get(item));
                        }
                    }

                }
                
                jogadoresItens.remove(jogador.getName());
            }
        }
    }

    public void equipaJogador(Player jogador) {
        if (guardaInventario()) {
            HashMap<Integer, ItemStack> map = new HashMap<>();

            for (int loop = 0; loop <= 35; loop++) {
                map.put(loop, jogador.getInventory().getItem(loop));
            }

            for (int loop = 36; loop <= 39; loop++) {
                map.put(loop, jogador.getInventory().getItem(loop));
            }

            jogadoresItens.put(jogador.getName(), map);
        }

        jogador.getInventory().clear();
        jogador.getInventory().setArmorContents(null);

        for (int item : getKit().keySet()) {
            jogador.getInventory().setItem(item, getKit().get(item));
        }
    }

    public void salvaKit() {
        try {
            editaDados.save(chainDados);
        } catch (IOException ex) {
            Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void setLocal(String local, Location novolocal) {
        if (local.equalsIgnoreCase("spawn")) {
            spawn = novolocal;
        } else if (local.equalsIgnoreCase("camarote")) {
            camarote = novolocal;
        } else if (local.equalsIgnoreCase("saida")) {
            saida = novolocal;
        }
    }

    public void addJogador(String jogador) {
        getJogadores().add(jogador);
    }

    public void addCamarote(String jogador) {
        getCamarotes().add(jogador);
    }

    public void delCamarote(String jogador) {
        getCamarotes().remove(jogador);
    }

    public void delJogador(String jogador) {
        getJogadores().remove(jogador);
    }

    private void carregaLugar() {
        try {

            String[] pos22 = editaDados.getString("Lugares.Spawn").split(";");
            spawn = new Location(Bukkit.getServer().getWorld(pos22[0]), Double.parseDouble(pos22[1]), Double.parseDouble(pos22[2]), Double.parseDouble(pos22[3]), Float.parseFloat(pos22[4]), Float.parseFloat(pos22[5]));

            String[] cm = editaDados.getString("Lugares.Camarote").split(";");
            camarote = new Location(Bukkit.getServer().getWorld(cm[0]), Double.parseDouble(cm[1]), Double.parseDouble(cm[2]), Double.parseDouble(cm[3]), Float.parseFloat(cm[4]), Float.parseFloat(cm[5]));

            String[] sai = editaDados.getString("Lugares.Saida").split(";");
            saida = new Location(Bukkit.getServer().getWorld(sai[0]), Double.parseDouble(sai[1]), Double.parseDouble(sai[2]), Double.parseDouble(sai[3]), Float.parseFloat(sai[4]), Float.parseFloat(sai[5]));

        } catch (Exception ex) {

        }
    }

    private void carregaKit() {
        File file = new File(getDataFolder(), "dados.yml");
        chainDados = new File(plugin.getDataFolder(), "dados.yml");

        if (file.exists()) {
            getKit().clear();
            editaDados = YamlConfiguration.loadConfiguration(chainDados);

            if (editaDados.contains("Itens")) {
                ConfigurationSection itensKit = editaDados.getConfigurationSection("Itens.Slot");

                for (String slot : itensKit.getKeys(false)) {
                    getKit().put(Integer.parseInt(slot), itensKit.getItemStack(slot));
                }
            }

            if (editaDados.contains("Armadura")) {
                ConfigurationSection armorKit = editaDados.getConfigurationSection("Armadura.Slot");
                for (String slot : armorKit.getKeys(false)) {
                    getKit().put(Integer.parseInt(slot), armorKit.getItemStack(slot));
                }
            }

        } else {
            try {
                file.createNewFile();
                editaDados = YamlConfiguration.loadConfiguration(chainDados);
            } catch (IOException ex) {
                Logger.getLogger(Principal.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

    }

    //////////////////////////////////////////////////////////////////////
    ///////////////////[ METODOS DE API ] ///////////////////////////////////
    private boolean carregaEconomy() {

        try {
            if (getServer().getPluginManager().getPlugin("Vault") != null) {
                RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
                if (economyProvider != null) {
                    economy = economyProvider.getProvider();
                }

                return (economy != null);
            }
        } catch (Exception ex) {
            return false;
        }
        return false;
    }

    public void adicionaMoney(String jogador, Double quantidade) {
        economy.depositPlayer(jogador, quantidade);
    }

    private boolean versaoAcima18() {
        String versioN = Bukkit.getServer().getVersion();
        if (versioN.contains("1.5") || versioN.contains("1.6") || versioN.contains("1.7") || versioN.contains("1.4") || versioN.contains("1.3")) {
            return false;
        } else {
            return true;
        }
    }

    public boolean getVersao() {
        return versaoAcima18();
    }

    public void removeMoney(String jogador, Double quantidade) {
        economy.withdrawPlayer(jogador, quantidade);
    }

    ////////////////////////////////////////////////////////////////////
}
