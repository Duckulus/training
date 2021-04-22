package de.hglabor.plugins.training.challenges.mlg.mlgs;

import de.hglabor.plugins.training.Training;
import de.hglabor.plugins.training.challenges.mlg.Mlg;
import de.hglabor.plugins.training.user.User;
import de.hglabor.plugins.training.user.UserList;
import de.hglabor.plugins.training.warp.WarpItems;
import de.hglabor.utils.noriskutils.ItemBuilder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByBlockEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.inventory.ItemStack;
import org.spigotmc.event.entity.EntityMountEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class StriderMlg extends Mlg {
    private final List<ItemStack> mlgItems = new ArrayList<>();
    private final List<Strider> striders;
    private final int striderAmount;

    public StriderMlg(String name, ChatColor color, Class<? extends Entity> type) {
        super(name, color, type, Material.QUARTZ_BLOCK, new Material[] {Material.GOLD_BLOCK, Material.LAVA});
        this.striders = new ArrayList<>();
        this.striderAmount = 150;
        this.addMlgMaterial(Material.SADDLE);
    }

    private void addMlgMaterial(Material material) {
        this.mlgItems.add(new ItemBuilder(material).setName(ChatColor.AQUA + this.getName() + " MLG").build()); // e.g. Block Mlg - COBWEB
    }

    @Override
    public void start() {
        super.start();
        for (int i = 0; i < striderAmount; i++) {
            Strider strider = (Strider) spawn.getWorld().spawnEntity(spawn.clone().add(0, 1, 0), EntityType.STRIDER);
            strider.setPersistent(false);
            // Check for strider spawned with saddle
            if (strider.hasSaddle()) strider.setSaddle(false);
            striders.add(strider);
        }
    }

    @Override
    public void stop() {
        super.stop();
        striders.forEach(Entity::remove);
        striders.clear();
    }

    @EventHandler
    public void onClickStrider(PlayerInteractAtEntityEvent evt) {
        Player player = evt.getPlayer();
        if (!isInChallenge(player)) {
            evt.setCancelled(true);
            return;
        }
        if (!(evt.getRightClicked() instanceof Strider)) return;

        handleMlg(player);
        // Remove saddle after 1 second (20 ticks)
        Bukkit.getScheduler().runTaskLater(Training.getInstance(), () -> ((Strider)evt.getRightClicked()).setSaddle(false), 20L);
    }

    @EventHandler
    public void onLavaDamage(EntityDamageByBlockEvent evt) {
        if (!(evt.getEntity() instanceof Player)) return;
        Player player = ((Player) evt.getEntity());
        if (!isInChallenge(player)) {
            evt.setCancelled(true);
            return;
        }
        if (evt.getCause().equals(EntityDamageEvent.DamageCause.LAVA)) {
            // Player got lava damage
            onFailure(player);
            player.setFireTicks(0);
        }
    }

    @Override
    public List<ItemStack> getMlgItems() {
        return mlgItems;
    }

    public void setMlgReady(Player player) {
        setMaxHealth(player);
        player.setFoodLevel(100);
        player.getInventory().clear();
        player.getInventory().setItem(0, WarpItems.WARP_SELECTOR);
        player.getInventory().setItem(4, mlgItems.get(0));
        player.getInventory().setItem(7, WarpItems.HUB);
        player.getInventory().setItem(8, WarpItems.RESPAWN_ANCHOR);
    }
}
