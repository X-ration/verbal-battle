package com.adam.verbal_battle.game;

import com.adam.verbal_battle.DebugUtils;

public class Round {

    private int index;
    private CardType roundType;
    private Card playerMove, componentPlayerMove;
    /**
     * 当前回合的胜负，下一回合根据此变量确定先手/后手
     */
    private boolean win;
    /**
     * 是否强制写入回合胜负，为true则后续不能再更改
     */
    private boolean forceSetWin;
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

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        if(!forceSetWin) {
            this.win = win;
        } else {
            DebugUtils.debugPrintln("Round["+index+"]forceSetWin=true,not setting win to "+win);
        }
    }

    public boolean isForceSetWin() {
        return forceSetWin;
    }

    public void setForceSetWin(boolean forceSetWin) {
        this.forceSetWin = forceSetWin;
    }

    public Effect getEffect() {
        return effect;
    }

    public void setEffect(Effect effect) {
        this.effect = effect;
    }
}
