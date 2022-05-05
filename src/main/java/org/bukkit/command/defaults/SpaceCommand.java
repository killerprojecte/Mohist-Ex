package org.bukkit.command.defaults;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.io.File;
import java.io.IOException;
import java.util.Collections;

public class SpaceCommand extends Command {
    public SpaceCommand(String name) {
        super(name);

        this.description = "Open MoreSpace Inventory Command";
        this.usageMessage = "/morespace";
        this.setAliases(Collections.singletonList("mspace"));
        this.setPermission("morespace.use");
    }

    @Override
    public boolean execute(CommandSender sender, String currentAlias, String[] args) {
        String data = System.getProperty("user.dir") + "/MoreSpaceData/";
        if (sender instanceof Player){
            Player p = (Player) sender;
            int size = 0;
            if (p.hasPermission("morespace.row.6")){
                size = 6;
            } else if (p.hasPermission("morespace.row.5")){
                size = 5;
            } else if (p.hasPermission("morespace.row.4")){
                size = 4;
            } else if (p.hasPermission("morespace.row.3")){
                size = 3;
            } else if (p.hasPermission("morespace.row.2")){
                size = 2;
            } else if (p.hasPermission("morespace.row.1")){
                size = 1;
            } else {
                sender.sendMessage(color("&c无权限使用此命令"));
                return false;
            }
            Inventory inv = Bukkit.createInventory(null,size*9,color("&7[&b更多背包&7]"));
            File file = new File(data + p.getDisplayName() + ".yml");
            if (!file.exists()){
                if (!file.getParentFile().exists()){
                    file.getParentFile().mkdirs();
                }
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                FileConfiguration config = YamlConfiguration.loadConfiguration(file);
                if (config.get("items")!=null){
                    for (String key : config.getConfigurationSection("items").getKeys(false)){
                        ItemStack is = config.getItemStack("items."+key);
                        int local = Integer.parseInt(key);
                        if (local<(size*9)){
                            inv.setItem(local,is);
                        }
                    }
                }
            }
            p.openInventory(inv);
        } else {
            sender.sendMessage("Console can't use morespace");
        }
        return true;
    }
    private String color(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }
}