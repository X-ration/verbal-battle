package com.adam.verbal_battle.game;

import com.adam.verbal_battle.ConsoleUtils;

/**
 * 游戏菜单选择项
 */
public enum MenuChoice {
    ARTIFICIAL_COMPUTER("玩家对电脑"),
    ARTIFICIAL_SUPERCOMPUTER("玩家对超级电脑"),
    ARTIFICIAL_ARTIFICIAL("玩家对玩家"),
    COMPUTER_COMPUTER("电脑对电脑"),
    COMPUTER_SUPERCOMPUTER("电脑对超级电脑"),
    SUPERCOMPUTER_SUPERCOMPUTER("超级电脑对超级电脑"),
    EXIT("退出游戏"),
    ;

    private String desc;
    MenuChoice(String desc) {
        this.desc = desc;
    }

    public String getDesc() {
        return desc;
    }

    public static void printMenuChoices() {
        ConsoleUtils.println("舌战 v" + VerbalBattleGame.GAME_VERSION +" 游戏菜单");
        for(int i=0;i< values().length;i++) {
            ConsoleUtils.println(i+1+"."+values()[i].desc);
        }
    }

}
