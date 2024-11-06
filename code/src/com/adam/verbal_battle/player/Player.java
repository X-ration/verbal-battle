package com.adam.verbal_battle.player;

import com.adam.verbal_battle.Assert;
import com.adam.verbal_battle.ConsoleUtils;
import com.adam.verbal_battle.game.Card;
import com.adam.verbal_battle.game.VerbalBattleGame;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;

import java.util.LinkedList;
import java.util.List;

public abstract class Player {

    protected Person person;
    protected int life;
    protected int anger;
    protected List<Card> cardList;

    public Player() {
        this.life = 100;
        this.anger = 0;
        this.cardList = new LinkedList<>();
    }

    public Person choosePerson(int index) {
        Assert.assertTrue(index > 0 && index <= PersonRepository.getINSTANCE().getPersonSize(),
                "choosePerson index invalid");
        this.person = PersonRepository.getINSTANCE().getPerson(index);
        return this.person;
    }

    public String getPlayerName() {
        if(this instanceof ArtificialPlayer) {
            return "玩家";
        } else if(this instanceof ComputerPlayer) {
            return "电脑";
        } else {
            return "未知";
        }
    }

    public int getLife() {
        return life;
    }

    public int getAnger() {
        return anger;
    }

    public Person getPerson() {
        return person;
    }

    public int getCardSize() {
        return cardList.size();
    }

    public abstract String formatCards();

    public void addCard(Card card) {
        Assert.assertTrue(card != null, "addCard card is null");
        cardList.add(card);
    }

    public Card removeCard(int index) {
        Assert.assertTrue(index>0 && index<=getCardSize(), "removeCard index invalid");
        return cardList.remove(index-1);
    }

    public int changeLife(int change) {
        Assert.assertTrue(change >= -100 && change <= 100, "changeLife change  invalid");
        if(change + life < 0) {
            change = -life;
        } else if(change + life > 100) {
            change = 100 - life;
        }
        life += change;
        if(change < 0) {
            ConsoleUtils.println(getPlayerName() + "受到" + change + "点伤害");
        } else if(change > 0) {
            ConsoleUtils.println(getPlayerName() + "恢复" + change + "点生命");
        } else {
        }
        return life;
    }

    public int changeAnger(int change) {
        Assert.assertTrue(change >= -100 && change <= 100, "changeAnger change  invalid");
        if(change + anger < 0) {
            change = -anger;
        } else if(change + anger > 100) {
            change = 100 - anger;
        }
        anger += change;
        if(change < 0) {
            ConsoleUtils.println(getPlayerName() + "怒气值减少" + change + "点");
        } else if(change > 0) {
            ConsoleUtils.println(getPlayerName() + "怒气值增加" + change + "点");
        } else {
        }
        return anger;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPlayerName()).append(":").append(person.getName()).append(" ")
                .append("智力:").append(person.getIntelligence()).append(" ")
                .append("生命值:").append(getLife()).append(" ")
                .append("怒气值:").append(getAnger());
        return stringBuilder.toString();

    }
}
