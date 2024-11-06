package com.adam.verbal_battle.player;

import com.adam.verbal_battle.game.Card;

public class ArtificialPlayer extends Player{

    private static ArtificialPlayer INSTANCE;

    public static ArtificialPlayer getINSTANCE() {
        if(INSTANCE == null) {
            synchronized (ArtificialPlayer.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ArtificialPlayer();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public String formatCards() {
        StringBuilder stringBuilder = new StringBuilder();
        int i = 1;
        for(Card card:cardList) {
            stringBuilder.append(i++).append(".").append(card.getDesc()).append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
