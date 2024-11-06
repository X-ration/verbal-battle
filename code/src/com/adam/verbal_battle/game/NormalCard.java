package com.adam.verbal_battle.game;

public enum NormalCard implements Card{
    STORY_BIG(CardPower.BIG, CardType.STORY, "故事（大）", -15, 20),
    STORY_MEDIUM(CardPower.MEDIUM, CardType.STORY, "故事（中）", -10, 15),
    STORY_SMALL(CardPower.SMALL, CardType.STORY, "故事（小）", -5, 10),
    SEASON_BIG(CardPower.BIG, CardType.SEASON, "时节（大）", -15, 20),
    SEASON_MEDIUM(CardPower.MEDIUM, CardType.SEASON, "时节（中）", -10, 15),
    SEASON_SMALL(CardPower.SMALL, CardType.SEASON, "时节（小）", -5, 10),
    PRINCIPLE_BIG(CardPower.BIG, CardType.PRINCIPLE, "道理（大）", -15, 20),
    PRINCIPLE_MEDIUM(CardPower.MEDIUM, CardType.PRINCIPLE, "道理（中）", -10, 15),
    PRINCIPLE_SMALL(CardPower.SMALL, CardType.PRINCIPLE, "道理（小）", -5, 10),
    ;
    private CardPower power;
    private CardType type;
    private String desc;
    private int lifeHit, angerHit;
    NormalCard(CardPower power, CardType type, String desc, int lifeHit, int angerHit) {
        this.power = power;
        this.type = type;
        this.desc = desc;
        this.lifeHit = lifeHit;
        this.angerHit = angerHit;
    }

    public CardPower getPower() {
        return power;
    }

    public CardType getType() {
        return type;
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
