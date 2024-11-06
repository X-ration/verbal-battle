package com.adam.verbal_battle.game;

public class Round {

    private int index;
    private CardType roundType;
    private Card computerMove, artificialMove;
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

    public Card getComputerMove() {
        return computerMove;
    }

    public void setComputerMove(Card computerMove) {
        this.computerMove = computerMove;
    }

    public Card getArtificialMove() {
        return artificialMove;
    }

    public void setArtificialMove(Card artificialMove) {
        this.artificialMove = artificialMove;
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
