package quest.me.jack.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class QuestCMD_ADMIN implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if(sender instanceof Player){
            if(args.length != 0){
                //reload cmd
                if(args[0].equalsIgnoreCase("reload")){

                }

            }
        }
        return false;
    }
}
