package quest.me.jack.Quest;

public class QuestIn {
    public String id;
    public String name;
    public QuestType type;
    public String requirement;
    public int amount;
    public QuestIn(String id, String name, QuestType type, String requirement, int amount){
        this.id = id;
        this.name = name;
        this.type = type;
        this.requirement = requirement;
        this.amount = amount;
    }
}
