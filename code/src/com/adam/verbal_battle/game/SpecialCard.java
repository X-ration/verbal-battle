package com.adam.verbal_battle.game;

public enum SpecialCard implements Card{

    ROAR("大喝", -20, 20),
    IGNORE("无视", 0, 30),
    ANGRY("愤怒", 0, 30),
    CALM("冷静", 0,-30),
    ;
    private String desc;
    private int lifeHit, angerHit;
    SpecialCard(String desc, int lifeHit, int angerHit) {
        this.desc = desc;
        this.lifeHit = lifeHit;
        this.angerHit = angerHit;
    }

    public String getDesc() {
        return desc;
    }

    public int getLifeHit() {
        return lifeHit;
    }

    public int getAngerHit() {
        return angerHit;
    }
}
