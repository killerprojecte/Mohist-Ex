package org.bukkit.command.defaults;

import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;

import java.io.File;
import java.io.IOException;

public class SpaceListener implements Listener {
    @EventHandler
    public void onClose(InventoryCloseEvent event){
        String title = event.getView().getTitle();
        if (title.equals(color("&7[&b更多背包&7]"))){
            String data = System.getProperty("user.dir") + "/MoreSpaceData/";
            Player p = (Player) event.getPlayer();
            File file = new File(data + p.getDisplayName() + ".yml");
            FileConfiguration config = YamlConfiguration.loadConfiguration(file);
            for (int i = 0;i<event.getInventory().getContents().length;i++){
                config.set("items." + i,event.getInventory().getContents()[i]);
            }
            try {
                config.save(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    private String color(String text){
        return ChatColor.translateAlternateColorCodes('&',text);
    }
}
