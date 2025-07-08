package com.example.plugin;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class StarTeleport extends JavaPlugin implements Listener {

    private Map<UUID, Location> previousLocations = new HashMap<>();
    private Map<UUID, Location> savedLocations = new HashMap<>();

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("setstarpoint").setExecutor((sender, command, label, args) -> {
            if (sender instanceof Player) {
                Player player = (Player) sender;
                savedLocations.put(player.getUniqueId(), player.getLocation());
                player.sendMessage("§a텔레포트 위치가 설정되었습니다.");
                return true;
            }
            return false;
        });
    }

    @EventHandler
    public void onSwapHand(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        ItemStack item = player.getInventory().getItemInOffHand();

        if (item != null && item.getType() == Material.NETHER_STAR) {
            if (player.isSneaking()) {
                Location prev = previousLocations.get(player.getUniqueId());
                if (prev != null) {
                    Location current = player.getLocation();
                    player.teleport(prev);
                    previousLocations.put(player.getUniqueId(), current);
                    player.sendMessage("§e이전 위치로 이동했습니다.");
                } else {
                    player.sendMessage("§c이전 위치 정보가 없습니다.");
                }
            } else {
                Location saved = savedLocations.get(player.getUniqueId());
                if (saved != null) {
                    previousLocations.put(player.getUniqueId(), player.getLocation());
                    player.teleport(saved);
                    player.sendMessage("§b지정된 위치로 이동했습니다.");
                } else {
                    player.sendMessage("§c지정된 위치가 없습니다. /setstarpoint 명령어로 설정하세요.");
                }
            }
            event.setCancelled(true);
        }
    }
}
