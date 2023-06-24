package quest.me.jack.Quest;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import quest.me.jack.QuestPlugin;


public class DataManager {


    public static void setMainQuestProgress(Player player, String mainQuestName, String quest, int progress) {
        String playerName = player.getName();
        QuestPlugin.getQuestDataConfig().set(playerName + "." + mainQuestName+"."+quest, progress);
        QuestPlugin.saveQuestDataConfig();
    }

    public static int getMainQuestProgress(Player player, String mainQuestName, String quest) {
        String playerName = player.getName();
        if (QuestPlugin.getQuestDataConfig().contains(playerName + "." + mainQuestName)) {
            return QuestPlugin.getQuestDataConfig().getInt(playerName + "." + mainQuestName+"."+quest);
        }

        return 0;
    }

}
