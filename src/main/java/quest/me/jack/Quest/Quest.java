package quest.me.jack.Quest;

import org.checkerframework.checker.units.qual.A;

import java.util.ArrayList;

public class Quest {
    public String id;
    public String name;
    public String description;
    public String GiveRewardCommands;
    public ArrayList<QuestIn> quests = new ArrayList<>();
    public Quest (String id, String name, ArrayList<QuestIn> quests, String description, String giveRewardCommands){
        this.id = id;
        this.name = name;
        this.quests = quests;
        this.description = description;
        this.GiveRewardCommands = giveRewardCommands;

    }
}
