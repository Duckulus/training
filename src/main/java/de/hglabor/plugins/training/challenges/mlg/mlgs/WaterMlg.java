package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerBucketFillEvent;
import org.bukkit.inventory.ItemStack;

import java.util.Collections;
import java.util.List;

public class WaterMlg extends Mlg {
    private final ItemStack mlgItem;

    public WaterMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, Material.GOLD_BLOCK);
        this.mlgItem = new ItemBuilder(Material.WATER_BUCKET).setName(ChatColor.AQUA + this.getName() + " MLG").build();
    }

    @EventHandler
    public void onBucket(PlayerBucketEmptyEvent event) {
        Player player = event.getPlayer();
        Block blockClicked = event.getBlockClicked();
        Block block = event.getBlock();
        if (!isInChallenge(player)) {
            event.setCancelled(true);
            return;
        }
        if (!canMlgHere(blockClicked)) {
            player.sendMessage(ChatColor.RED + "Here you can't mlg"); //TODO localization
            event.setCancelled(true);
            return;
        }

        handleMlg(player);
        removeBlockLater(block, 10L);
    }

    @EventHandler
    public void onBucket(PlayerBucketFillEvent event) {
        Block blockClicked = event.getBlockClicked();
        if (!isInChallenge(event.getPlayer())) {
            return;
        }
        if (blockClicked.getType().equals(Material.WATER)) {
            event.setCancelled(true);
        }
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return Collections.singletonList(mlgItem);
    }

}
