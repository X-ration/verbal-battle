package com.adam.verbal_battle.person;

public enum Character {

    /**
     * 胆小者愤怒时将手牌全部打出
     */
    TIMID("胆小"),
    /**
     * 冷静者获得3回合熟虑机会
     */
    CALM("冷静"),
    /**
     * 莽撞者愤怒时一次性攻击
     */
    RASH("莽撞"),
    /**
     * 刚毅者愤怒时让对手的普通牌失效
     */
    RESOLUTE("刚毅")
    ;

    private String desc;
    Character(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static Character findByDesc(String desc) {
        Character character = null;
        for(Character character1: values()) {
            if(character1.desc.equals(desc)) {
                character = character1;
            }
        }
        return character;
    }

}
