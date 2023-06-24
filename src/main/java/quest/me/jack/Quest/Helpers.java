package quest.me.jack.Quest;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.persistence.PersistentDataType;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class Helpers {

    public static ItemStack applyNBT(ItemStack item){
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(quest.me.jack.QuestPlugin.getPlugin(quest.me.jack.QuestPlugin.class), "Quest.Activated.nbt");
        meta.getPersistentDataContainer().set(key, PersistentDataType.STRING, "quested");
        item.setItemMeta(meta);
        return item;
    }
    private static String prefix = quest.me.jack.QuestPlugin.getPlugin(quest.me.jack.QuestPlugin.class).getConfig().getString("prefix");

    public static String getPrefix() {
        return prefix.replace("&","§");
    }

    public static void setPrefix(String prefix) {
        Helpers.prefix = prefix;
    }
    public static String UpperFistChar(String str){
        char firstChar = str.charAt(0);
        char upperCaseChar = Character.toUpperCase(firstChar);
        String result = upperCaseChar + str.substring(1);
        return  result;
    }

    public static boolean hasQuestedNBT(ItemStack item) {
        ItemMeta meta = item.getItemMeta();
        NamespacedKey key = new NamespacedKey(quest.me.jack.QuestPlugin.getPlugin(quest.me.jack.QuestPlugin.class), "Quest.Activated.nbt");
        PersistentDataType<String, String> dataType = PersistentDataType.STRING;

        if (meta.getPersistentDataContainer().has(key, dataType)) {
            String value = meta.getPersistentDataContainer().get(key, dataType);
            return value.equals("quested");
        }
        return false;
    }
    public static ArrayList<Quest> quests = new ArrayList<>();
    public static Inventory QuestGui;

    public static Inventory getQuestGUI(Player  p){
        QuestGui = Bukkit.createInventory(null, 36, "Quests");
        for (int i = 0; i < 9; i++) {
            QuestGui.setItem(i, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            QuestGui.setItem(i + 27, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }

        for (int i = 1; i < 3; i++) {
            QuestGui.setItem(i * 9, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
            QuestGui.setItem(i * 9 + 8, new ItemStack(Material.BLACK_STAINED_GLASS_PANE));
        }
        for (Quest i : Helpers.quests) {
            int complete = 0;
            int max = i.quests.size();

            for (QuestIn j : i.quests) {
                if (j.amount == DataManager.getMainQuestProgress(p, i.id, j.id)) {
                    complete++;
                }
            }

            ItemStack item = new ItemStack(Material.BOOK);
            ItemMeta meta = item.getItemMeta();
            meta.setDisplayName(i.name.replace("&", "§"));

            ArrayList<String> lore = new ArrayList<>();
            lore.add("§f" + i.description);
            lore.add(" ");
            lore.add("§aComplete: §7- §e" + (complete * 100 / max) + " %");
            meta.setLore(lore);

            item.setItemMeta(meta);
            QuestGui.addItem(item);

            complete = 0;
            max = 0;
        }

        return  QuestGui;
    }
    public static Inventory QuestINGui;

    public static Inventory getQuestINGUI(Player  p, Quest quest){
        QuestINGui = Bukkit.createInventory(null, 36, quest.name.replace("&", "§"));
        for (int i = 0; i < 9; i++) {
            QuestINGui.setItem(i, new ItemStack(Material.BROWN_STAINED_GLASS_PANE));
            QuestINGui.setItem(i + 27, new ItemStack(Material.BROWN_STAINED_GLASS_PANE));
        }

        for (int i = 1; i < 3; i++) {
            QuestINGui.setItem(i * 9, new ItemStack(Material.BROWN_STAINED_GLASS_PANE));
            QuestINGui.setItem(i * 9 + 8, new ItemStack(Material.BROWN_STAINED_GLASS_PANE));
        }
        for(QuestIn i : quest.quests){
            ItemStack item;
            try{
                if(DataManager.getMainQuestProgress(p, quest.id, i.id) == i.amount){
                    item = new ItemStack(Material.FILLED_MAP);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§e"+i.name.replace("%amount%", i.amount+" ")+"§7- §aTeljesítve!");
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);
                    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                } else {
                    item = new ItemStack(Material.valueOf(i.requirement.toUpperCase()));
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§e"+Helpers.UpperFistChar(i.name.replace("%amount%", i.amount+""))+" -§7 §a"+DataManager.getMainQuestProgress(p, quest.id,i.id));
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);
                }
            }catch (IllegalArgumentException e){
                if(DataManager.getMainQuestProgress(p, quest.id, i.id) == i.amount){
                    item = new ItemStack(Material.FILLED_MAP);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§e"+i.name.replace("%amount%", i.amount+" ")+"§7- §aTeljesítve!");
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);
                    item.addUnsafeEnchantment(Enchantment.DURABILITY, 1);
                } else {
                    item = new ItemStack(Material.PAPER);
                    ItemMeta meta = item.getItemMeta();
                    meta.setDisplayName("§e"+Helpers.UpperFistChar(i.name.replace("%amount%", i.amount+""))+" -§7 §a"+DataManager.getMainQuestProgress(p, quest.id,i.id));
                    meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
                    item.setItemMeta(meta);

                }

            }
            QuestINGui.setItem(31, guiExitItem());


            QuestINGui.addItem(item);
        }
        return  QuestINGui;
    }
    public static ItemStack guiExitItem(){
        ItemStack item = new ItemStack(Material.BARRIER);
        ItemMeta meta = item.getItemMeta();
        meta.setDisplayName("§c « Vissza");
        item.setItemMeta(meta);
        return  item;
    }
    public static void GiveQuestRewardIF(Player p, Quest quest, QuestIn questIn){
        int max = quest.quests.size();
        int completed = 0;
        for(QuestIn i : quest.quests){{

            if(i.amount == DataManager.getMainQuestProgress(p, quest.id, i.id)){
                completed++;
            }
        }
        if(max == completed){
                //comleted!
            String[] commands = quest.GiveRewardCommands.split(";");
            ConsoleCommandSender console = Bukkit.getServer().getConsoleSender();
            if(commands.length > 1){
                for(String  j : commands){
                    Bukkit.dispatchCommand(console, j.replace("%player_name%", p.getName()));
                }
            } else {
                Bukkit.dispatchCommand(console, quest.GiveRewardCommands.replace("%player_name%", p.getName()));
            }
            }
        }
    }
}
