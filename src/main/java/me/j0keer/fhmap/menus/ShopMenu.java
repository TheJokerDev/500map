package me.j0keer.fhmap.menus;

import com.cryptomorin.xseries.XMaterial;
import me.j0keer.fhmap.Main;
import me.j0keer.fhmap.type.DataPlayer;
import org.bukkit.entity.Player;
import org.bukkit.entity.Villager;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.MerchantRecipe;

import java.util.ArrayList;
import java.util.List;

public class ShopMenu implements Listener {
    private final Main plugin;

    public ShopMenu(Main plugin){
        this.plugin = plugin;
        plugin.listener(this);
    }

    private boolean cooldown = false;

    @EventHandler
    public void onInventoryOpen(InventoryOpenEvent event){
        if (cooldown) return;
        cooldown = false;
        Player p = (Player) event.getPlayer();
        DataPlayer dp = plugin.getDataManager().getDataPlayer(p);
        if (dp.isInGame()){
            InventoryType type = event.getInventory().getType();
            if (type == InventoryType.MERCHANT){
                List<MerchantRecipe> recipeList = new ArrayList<>();
                MerchantRecipe recipe = new MerchantRecipe(XMaterial.GOLDEN_SWORD.parseItem(), 20);
                ItemStack emerald = XMaterial.EMERALD.parseItem();
                emerald.setAmount(3);
                recipe.addIngredient(emerald);
                recipeList.add(recipe);

                //Change the recipe list of the villager to the new one
                Villager villager = (Villager) event.getInventory().getHolder();
                villager.setRecipes(recipeList);
                cooldown = true;
                p.openInventory(villager.getInventory());
            }
        }
    }
}
