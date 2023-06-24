package quest.me.jack.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import quest.me.jack.Quest.DataManager;
import quest.me.jack.Quest.Helpers;

public class QuestCMD implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Player p = (Player) sender;
        p.openInventory(Helpers.getQuestGUI(p));
        return false;
    }
}
