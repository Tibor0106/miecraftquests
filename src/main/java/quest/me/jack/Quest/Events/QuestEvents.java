package quest.me.jack.Quest.Events;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.CraftItemEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerPickupItemEvent;
import org.bukkit.inventory.CraftingInventory;
import org.bukkit.inventory.ItemStack;
import quest.me.jack.Quest.*;

public class QuestEvents implements Listener {
    //Protect quests GUI
    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        try{
            if (event.getInventory().equals(Helpers.QuestGui)) {
                event.setCancelled(true);
                String itemName = event.getCurrentItem().getItemMeta().getDisplayName();
                for(Quest i : Helpers.quests){
                    for (QuestIn j : i.quests){
                        if(i.name.replace("&", "§").equalsIgnoreCase(itemName)){
                            Player p = (Player) event.getWhoClicked();
                            event.getWhoClicked().openInventory(Helpers.getQuestINGUI(p, i));
                        }
                    }
                }
            }
            if (event.getInventory().equals(Helpers.QuestINGui)) {
                event.setCancelled(true);
                if(event.getCurrentItem().equals(Helpers.guiExitItem())){
                    Player p = (Player) event.getWhoClicked();
                    p.openInventory(Helpers.getQuestGUI(p));
                }
            }
        }catch (NullPointerException e){
        }
    }
    @EventHandler
    public void PICKUP(PlayerPickupItemEvent e) {
        Player p = e.getPlayer();
        Item item = e.getItem();
        if (Helpers.hasQuestedNBT(item.getItemStack())) {
            return;
        }
        e.getItem().setItemStack((Helpers.applyNBT(e.getItem().getItemStack())));
        for (Quest i : Helpers.quests) {
            for (QuestIn j : i.quests) {
                if (j.type == QuestType.PICKUP) {
                   if(DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount){
                   } else {
                       if (j.requirement.toUpperCase().equalsIgnoreCase(item.getItemStack().getType().name())) {
                           int amount = item.getItemStack().getAmount();
                           int now = DataManager.getMainQuestProgress(p, i.id, j.id);

                           if (now + amount  <= j.amount+1) {
                               DataManager.setMainQuestProgress(p, i.id, j.id, now + amount);
                               p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Helpers.getPrefix() + " §eSikeresen szereztél §f" + amount + "x§e " + Helpers.UpperFistChar(item.getName()) +
                                       "-t! §8- §eEnnyire van még szükséged: §c"
                                       + (j.amount - DataManager.getMainQuestProgress(p, i.id, j.id) + "x !")));
                           } else {
                               DataManager.setMainQuestProgress(p, i.id, j.id, j.amount);
                           }
                           if (DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount) {
                               p.sendMessage(Helpers.getPrefix()+" §eSikeresen teljesítetted a §a"+j.name.replace("&", "§").replace("%amount%", j.amount+
                                       "")+ " §eküldetést!");
                                p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                               Helpers.GiveQuestRewardIF(p, i, j);
                           }
                       }
                   }
                }
            }
        }
    }
    @EventHandler
    public void DAMAGE_MOB(EntityDamageByEntityEvent event) {
        Entity damager = event.getDamager();
        Entity entity = event.getEntity();
        try {
            if (damager instanceof Player) {
                Player p = (Player) damager;
                if (entity instanceof Mob) {
                    for (Quest i : Helpers.quests) {
                        for (QuestIn j : i.quests) {
                            if (j.type == QuestType.DAMAGE_MOB) {
                                if(DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount){
                                } else {
                                    if(event.getEntity().getName().equalsIgnoreCase(EntityType.valueOf(j.requirement.toUpperCase()).getName())) {
                                        int now = DataManager.getMainQuestProgress(p, i.id, j.id);
                                        double amount = event.getDamage();
                                        if (now + amount <= j.amount + 1) {
                                            DataManager.setMainQuestProgress(p, i.id, j.id, now + (int) amount);
                                            p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Helpers.getPrefix() + " §eSikeresen leadtál §f" + amount + " sebzést egy "
                                                    + Helpers.UpperFistChar(event.getEntity().getName()) +
                                                    "nak! §8- §eEnnyire van még szükséged: §c"
                                                    + (j.amount - DataManager.getMainQuestProgress(p, i.id, j.id) + "x !")));
                                        } else {
                                            DataManager.setMainQuestProgress(p, i.id, j.id, j.amount);
                                        }
                                        if (DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount) {
                                            p.sendMessage(Helpers.getPrefix() + " §eSikeresen teljesítetted a §a" + j.name.replace("&", "§").replace("%amount%", j.amount +
                                                    "") + " §eküldetést!");
                                            p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                            Helpers.GiveQuestRewardIF(p, i, j);
                                        }
                                    }

                                }

                            }
                        }
                    }
                }
            }
        } catch (NullPointerException e) {

        }
    }
    @EventHandler
    public void CRAFT(CraftItemEvent event) {
        if (event.getWhoClicked() instanceof Player) {
            Player p = (Player) event.getWhoClicked();
            ItemStack craftedItem = event.getCurrentItem();
            for (Quest i : Helpers.quests) {
                for (QuestIn j : i.quests) {
                    if (j.type == QuestType.CRAFT) {
                        if (DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount) {
                            // Már teljesítetted a küldetést, ne csinálj semmit
                            return;
                        } else {
                            if (craftedItem.getType().name().equalsIgnoreCase(j.requirement)) {
                                int now = DataManager.getMainQuestProgress(p, i.id, j.id);
                                int amount = calculateCraftedAmount(event); // A helyesen kiszámolt mennyiség
                                if (now + amount <= j.amount) {
                                    DataManager.setMainQuestProgress(p, i.id, j.id, now + amount);
                                    p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Helpers.getPrefix() + " §eSikeresen készítettél §f" + amount + "x "
                                            + Helpers.UpperFistChar(toFriendlyName(craftedItem.getType())) +
                                            "-ot/et! §8- §eEnnyire van még szükséged: §c"
                                            + (j.amount - DataManager.getMainQuestProgress(p, i.id, j.id) + "x !")));
                                } else {
                                    DataManager.setMainQuestProgress(p, i.id, j.id, j.amount);
                                }
                                if (DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount) {
                                    p.sendMessage(Helpers.getPrefix() + " §eSikeresen teljesítetted a §a" + j.name.replace("&", "§").replace("%amount%", j.amount +
                                            "") + " §eküldetést!");
                                    p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                    Helpers.GiveQuestRewardIF(p, i, j);
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    private String toFriendlyName(Material material) {
        String name = material.name();
        String[] words = name.split("_");
        StringBuilder friendlyName = new StringBuilder();
        for (String word : words) {
            friendlyName.append(word.substring(0, 1)).append(word.substring(1).toLowerCase()).append(" ");
        }
        return friendlyName.toString().trim();
    }

    private int calculateCraftedAmount(CraftItemEvent event) {
        ItemStack craftedItem = event.getCurrentItem();

        if (craftedItem == null || craftedItem.getType().equals(Material.AIR)) {
            return 0;
        }
        CraftingInventory inventory = (CraftingInventory) event.getInventory();
        ItemStack[] matrix = inventory.getMatrix();
        int craftedAmount = 0;
        int itemsChecked = 0;
        int possibleCreations = 1;

        if (event.isShiftClick()) {
            for (ItemStack item : matrix) {
                if (item != null && !item.getType().equals(Material.AIR)) {
                    if (itemsChecked == 0) {
                        possibleCreations = item.getAmount();
                    } else {
                        possibleCreations = Math.min(possibleCreations, item.getAmount());
                    }
                    itemsChecked++;
                }
            }
            craftedAmount = possibleCreations;
        } else {
            for (ItemStack item : matrix) {
                if (item != null && item.isSimilar(craftedItem)) {
                    craftedAmount += item.getAmount();
                }
            }
        }
        if(craftedAmount == 0){
            return craftedItem.getAmount();
        }

        return craftedAmount;
    }
    @EventHandler
    public void KILL_MOB(EntityDeathEvent event) {
        if (event.getEntity().getKiller() != null) {
            if(event.getEntity().getKiller() instanceof  Player){
                Player p = event.getEntity().getKiller();
                for (Quest i : Helpers.quests) {
                    for (QuestIn j : i.quests) {
                        if (j.type == QuestType.KILL_MOB) {
                            if(DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount){
                            } else {
                                if (j.requirement.toUpperCase().equalsIgnoreCase(event.getEntityType().name())) {
                                    int amount = 1;
                                    int now = DataManager.getMainQuestProgress(p, i.id, j.id);
                                    if (now + amount  <= j.amount+1) {
                                        DataManager.setMainQuestProgress(p, i.id, j.id, now + amount);
                                        p.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(Helpers.getPrefix() + " §eSikeresen megöltél §f" + amount + "x§e " + Helpers.UpperFistChar(event.getEntity().getName()) +
                                                "-t! §8- §eEnnyire van még szükséged: §c"
                                                + (j.amount - DataManager.getMainQuestProgress(p, i.id, j.id) + "x !")));
                                    } else {
                                        DataManager.setMainQuestProgress(p, i.id, j.id, j.amount);
                                    }
                                    if (DataManager.getMainQuestProgress(p, i.id, j.id) == j.amount) {
                                        p.sendMessage(Helpers.getPrefix()+" §eSikeresen teljesítetted a §a"+j.name.replace("&", "§").replace("%amount%", j.amount+
                                                "")+ " §eküldetést!");
                                        p.playSound(p, Sound.ENTITY_PLAYER_LEVELUP, 1f, 1f);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
