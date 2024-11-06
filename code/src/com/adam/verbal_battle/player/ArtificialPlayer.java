package com.adam.verbal_battle.player;

import com.adam.verbal_battle.game.Card;
import com.adam.verbal_battle.person.Character;

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
        if(isAngry() && getPerson().getCharacter() == Character.CALM) {
            stringBuilder.append("0.熟虑 ");
        }
        int i = 1;
        for(Card card:cardList) {
            stringBuilder.append(i++).append(".").append(card.getDesc()).append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }
}
