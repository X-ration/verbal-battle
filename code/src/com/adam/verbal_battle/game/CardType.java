package com.adam.verbal_battle.game;

public enum CardType {

    STORY("故事"), SEASON("时节"), PRINCIPLE("道理");
    private String desc;
    CardType(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }
}
