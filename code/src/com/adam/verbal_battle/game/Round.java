package com.adam.verbal_battle.game;

public class Round {

    private int index;
    private CardType roundType;
    private Card playerMove, componentPlayerMove;
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
