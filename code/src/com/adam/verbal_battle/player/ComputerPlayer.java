package com.adam.verbal_battle.player;

import com.adam.verbal_battle.game.Card;
import com.adam.verbal_battle.game.NormalCard;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;

import java.util.Random;

public class ComputerPlayer extends Player{

    private static ComputerPlayer INSTANCE;

    public Person choosePerson() {
        Random random = new Random();
        return choosePerson(
                random.nextInt(PersonRepository.getINSTANCE().getPersonSize()) + 1);
    }

    public static ComputerPlayer getINSTANCE() {
        if(INSTANCE == null) {
            synchronized (ComputerPlayer.class) {
                if(INSTANCE == null) {
                    INSTANCE = new ComputerPlayer();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public String formatCards() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Card card:cardList) {
            stringBuilder.append(card instanceof NormalCard ? "普通卡片" : "特殊卡片").append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

}
