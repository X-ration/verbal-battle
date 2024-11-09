package com.adam.verbal_battle.player;

import com.adam.verbal_battle.game.Card;
import com.adam.verbal_battle.game.NormalCard;
import com.adam.verbal_battle.game.Round;
import com.adam.verbal_battle.game.VerbalBattleGame;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;

import java.util.Random;

public class ComputerPlayer extends Player{

    public ComputerPlayer(String playerName) {
        super(playerName);
    }

    public Person choosePerson() {
        Random random = new Random();
        return choosePerson(
                random.nextInt(PersonRepository.getINSTANCE().getPersonSize()) + 1);
    }

    public Person choosePersonWithExclusion(int exclusion) {
        Random random = new Random();
        int index;
        do {
            index = random.nextInt(PersonRepository.getINSTANCE().getPersonSize()) + 1;
        } while (index == exclusion);
        return choosePerson(index);
    }

    public String formatCoveringCards() {
        StringBuilder stringBuilder = new StringBuilder();
        for(Card card:cardList) {
            stringBuilder.append(card instanceof NormalCard ? "普通卡片" : "特殊卡片").append(" ");
        }
        stringBuilder.deleteCharAt(stringBuilder.length() - 1);
        return stringBuilder.toString();
    }

    /**
     * 普通电脑出牌策略：随机
     * @param componentPlayer 对手
     * @return 出牌
     */
    public Card chooseCard(Player componentPlayer, Round round) {
        VerbalBattleGame.getINSTANCE().reinitializeCards(this);
        Random random = new Random();
        int cardIndex = random.nextInt(getCardSize()) + 1;
        return removeCard(cardIndex);
    }

}
