package cn.liibang.pinoko.model;

import java.util.List;

public class Emoji {
    private String label;
//    private String hexcode;
    private String emoji;
//    private String text;
//    private int type;
//    private int version;
    private int group;
    private List<String> tags;

    @Override
    public String toString() {
        return "Emoji{" +
                "label='" + label + '\'' +
//                ", hexcode='" + hexcode + '\'' +
                ", emoji='" + emoji + '\'' +
//                ", text='" + text + '\'' +
//                ", type=" + type +
//                ", version=" + version +
                ", group=" + group +
                ", tags=" + tags +
                '}';
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }



    public String getEmoji() {
        return emoji;
    }

    public void setEmoji(String emoji) {
        this.emoji = emoji;
    }


    public int getGroup() {
        return group;
    }

    public void setGroup(int group) {
        this.group = group;
    }

    public List<String> getTags() {
        return tags;
    }

    public void setTags(List<String> tags) {
        this.tags = tags;
    }
}