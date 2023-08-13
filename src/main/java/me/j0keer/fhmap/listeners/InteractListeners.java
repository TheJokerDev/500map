package me.j0keer.fhmap.listeners;

import com.cryptomorin.xseries.XMaterial;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.Button;
import me.j0keer.fhmap.type.DataPlayer;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.Merchant;
import org.bukkit.inventory.MerchantRecipe;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class InteractListeners implements Listener {
    private Main plugin;

    public InteractListeners(Main plugin){
        this.plugin = plugin;
    }

    @EventHandler
    public void onInteract(PlayerInteractEvent e){
        Player p = e.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);

            if (dp.isInGame() && p.getLocation().getX() <= -110){
                Merchant merchant = null;
                Villager villager = null;
                for(Entity entity : p.getNearbyEntities(50, 50, 50)){
                    if(!(entity instanceof Merchant))
                        continue;

                    merchant = (Merchant) entity;
                    villager = (Villager) entity;
                    break;
                }
                if(merchant == null){
                    return;
                }

                villager.addPotionEffect(new PotionEffect(PotionEffectType.INVISIBILITY, 100000, 1, false, false));
                List<ItemStack> ingredients = new ArrayList<>();
                List<MerchantRecipe> recipes = new ArrayList<>();
                ItemStack emerald = XMaterial.EMERALD.parseItem();
                ItemStack sword = XMaterial.GOLDEN_SWORD.parseItem();
                emerald.setAmount(3);
                ingredients.add(emerald);

                MerchantRecipe recipe = new MerchantRecipe(sword, 1, 10, false, 1, 1);
                recipe.setIngredients(ingredients);
                recipes.add(recipe);
                merchant.setRecipes(recipes);
                p.openMerchant(merchant, false);
            }
    }

    @EventHandler
    public void onMoveItem(InventoryClickEvent event){
        Player p = (Player) event.getWhoClicked();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (dp.isInGame()){
            ItemStack item = event.getCurrentItem();
            if (item == null || item.getType() == Material.AIR){
                return;
            }
            if (item.getType() == Material.VILLAGER_SPAWN_EGG){
                event.setCancelled(true);
            }
        }
    }

    @EventHandler
    public void onDropItem(PlayerDropItemEvent event){
        Player p = event.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (dp.isInGame()){
            ItemStack item = event.getItemDrop().getItemStack();
            if (item.getType() == Material.AIR){
                return;
            }
            if (item.getType() == Material.VILLAGER_SPAWN_EGG){
                event.setCancelled(true);
            }
        }
    }

}
