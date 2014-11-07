package ch.ethz.itet.pps.budgetSplit;

/**
 * Created by Chrissy on 06.11.2014.
 */
public class ParticipantTagsLinker {
    public String name;
    public String tags;
    public String money;

    public ParticipantTagsLinker() {
        super();
    }

    public ParticipantTagsLinker(String n, String t, String m) {
        super();
        this.name = n;
        this.tags = t;
        this.money = m;
    }
}
