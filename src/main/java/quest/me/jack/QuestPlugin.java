package quest.me.jack;

import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import quest.me.jack.Commands.QuestCMD;
import quest.me.jack.Quest.Events.QuestEvents;
import quest.me.jack.Quest.Helpers;
import quest.me.jack.Quest.Quest;
import quest.me.jack.Quest.QuestIn;
import quest.me.jack.Quest.QuestType;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
;
import java.util.List;

public final class QuestPlugin extends JavaPlugin {
    private static FileConfiguration QuestConfig;
    private static File QuestFile;

    private static FileConfiguration QuestDataConfig;
    private static File QuestDataFile;

    @Override
    public void onEnable() {
        getServer().getPluginCommand("quest").setExecutor(new QuestCMD());
        saveDefaultConfig();
        createQuestConfig();
        setupQuests();
        createQuestDataConfig();
        getServer().getPluginManager().registerEvents(new QuestEvents(), this);

    }
    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }


    private void createQuestConfig() {
        QuestFile = new File(getDataFolder(), "quest.yml");
        if (!QuestFile.exists()) {
            QuestFile.getParentFile().mkdirs();
            saveResource("quest.yml", false);
        }

        QuestConfig = YamlConfiguration.loadConfiguration(QuestFile);
    }
    private void createQuestDataConfig() {
        QuestDataFile = new File(getDataFolder(), "questdata.yml");
        if (!QuestDataFile.exists()) {
            QuestDataFile.getParentFile().mkdirs();
            saveResource("questdata.yml", false);
        }

        QuestDataConfig = YamlConfiguration.loadConfiguration(QuestDataFile);
    }

    public static FileConfiguration getQuestConfig() {
        return QuestConfig;
    }

    public static void saveQuestConfig() {
        try {
            QuestConfig.save(QuestFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static FileConfiguration getQuestDataConfig() {
        return QuestDataConfig;
    }
    public static void saveQuestDataConfig() {
        try {
            QuestDataConfig.save(QuestDataFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private static void setupQuests(){
        FileConfiguration questConfig = getQuestConfig();
        String subQuestDescription = "";
        String subQuestReward = "";

        if (questConfig != null) {
            for (String questId : questConfig.getKeys(false)) {
                ConfigurationSection questSection = questConfig.getConfigurationSection(questId);
                String questName = questSection.getString("name");
                subQuestDescription = questSection.getString("description");
                subQuestReward = questSection.getString("reward-give-commands");

                ConfigurationSection subQuestsSection = questSection.getConfigurationSection("quests");
                if (subQuestsSection != null) {
                    List<QuestIn> subQuests = new ArrayList<>();
                    for (String subQuestId : subQuestsSection.getKeys(false)) {
                        ConfigurationSection subQuestSection = subQuestsSection.getConfigurationSection(subQuestId);
                        String subQuestName = subQuestSection.getString("name");
                        String subQuestType = subQuestSection.getString("type");
                        String subQuestRequirement = subQuestSection.getString("requirement");
                        int subQuestAmount = subQuestSection.getInt("amount");

                        QuestIn questIn = new QuestIn(subQuestId, subQuestName, QuestType.valueOf(subQuestType), subQuestRequirement, subQuestAmount);
                        subQuests.add(questIn);
                    }
                    Helpers.quests.add(new Quest(questId, questName, (ArrayList<QuestIn>) subQuests, subQuestDescription, subQuestReward));
                }
            }
        }
    }

}
