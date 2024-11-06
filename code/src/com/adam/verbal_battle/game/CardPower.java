package com.adam.verbal_battle.game;

public enum CardPower {

    SMALL("小"), MEDIUM("中"), BIG("大");

    private String desc;
    CardPower(String desc) {
        this.desc = desc;
    }

}
