package com.adam.verbal_battle.game;

import com.adam.verbal_battle.DebugUtils;

public class Round {

    private int index;
    private CardType roundType;
    private Card playerMove, componentPlayerMove;
    private boolean playerEndAngryStatus, componentPlayerEndAngryStatus;
    /**
     * 当前回合的胜负，下一回合根据此变量确定先手/后手
     */
    private boolean win;
    private Effect effect;

    public Round(int index) {
        this.index = index;
    }

    public int getIndex() {
        return index;
    }

    public CardType getRoundType() {
        return roundType;
    }

    public void setRoundType(CardType roundType) {
        this.roundType = roundType;
    }

    public Card getPlayerMove() {
        return playerMove;
    }

    public void setPlayerMove(Card playerMove) {
        this.playerMove = playerMove;
    }

    public Card getComponentPlayerMove() {
        return componentPlayerMove;
    }

    public void setComponentPlayerMove(Card componentPlayerMove) {
        this.componentPlayerMove = componentPlayerMove;
    }

    public boolean isPlayerEndAngryStatus() {
        return playerEndAngryStatus;
    }

    public void setPlayerEndAngryStatus(boolean playerEndAngryStatus) {
        this.playerEndAngryStatus = playerEndAngryStatus;
    }

    public boolean isComponentPlayerEndAngryStatus() {
        return componentPlayerEndAngryStatus;
    }

    public void setComponentPlayerEndAngryStatus(boolean componentPlayerEndAngryStatus) {
        this.componentPlayerEndAngryStatus = componentPlayerEndAngryStatus;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }
}
