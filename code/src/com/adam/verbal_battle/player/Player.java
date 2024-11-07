package com.adam.verbal_battle.player;

import com.adam.verbal_battle.Assert;
import com.adam.verbal_battle.ConsoleUtils;
import com.adam.verbal_battle.DebugUtils;
import com.adam.verbal_battle.game.Card;
import com.adam.verbal_battle.game.VerbalBattleGame;
import com.adam.verbal_battle.person.Character;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;

import java.util.LinkedList;
import java.util.List;

public abstract class Player {

    private static int INVALID_LAST_ANGRY_ROUNDS = 0;

    protected String playerName;
    protected Person person;
    protected int life;
    protected int anger;
    protected boolean angry;
    protected int lastAngryRound;
    protected List<Card> cardList;

    public Player(String playerName) {
        Assert.assertTrue(playerName != null, "new Player playerName null");
        this.playerName = playerName;
        this.life = 100;
        this.anger = 0;
        this.cardList = new LinkedList<>();
        this.angry = false;
        this.lastAngryRound = INVALID_LAST_ANGRY_ROUNDS;
    }

    public Person choosePerson(int index) {
        Assert.assertTrue(index > 0 && index <= PersonRepository.getINSTANCE().getPersonSize(),
                "choosePerson index invalid");
        this.person = PersonRepository.getINSTANCE().getPerson(index);
        return this.person;
    }

    public String getPlayerName() {
        return playerName;
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

    public boolean isAngry() {
        return angry;
    }

    public void setAngry(boolean angry) {
        this.angry = angry;
    }

    public int getLastAngryRound() {
        return lastAngryRound;
    }

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

    public int changeAnger(int change, int roundIndex) {
        Assert.assertTrue(change >= -100 && change <= 100, "changeAnger change  invalid");
        if (change + anger < 0) {
            change = -anger;
        } else if (change + anger > 100) {
            change = 100 - anger;
        }
        anger += change;
        if (change < 0) {
            ConsoleUtils.println(getPlayerName() + "怒气值减少" + change + "点");
        } else if (change > 0) {
            ConsoleUtils.println(getPlayerName() + "怒气值增加" + change + "点");
        } else {
        }
        if(!angry && anger == 100) {
            lastAngryRound = roundIndex;
            DebugUtils.debugPrintln("lastAngryRound="+lastAngryRound);
            angry = true;
            ConsoleUtils.println(getPlayerName() + "进入愤怒状态");
        } else if(angry && anger < 100) {
            lastAngryRound = INVALID_LAST_ANGRY_ROUNDS;
            DebugUtils.debugPrintln("lastAngryRound="+lastAngryRound);
            angry = false;
            ConsoleUtils.println(getPlayerName() + "回归正常状态");
        }
        return anger;
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getPlayerName()).append(":").append(person.getName()).append(" ")
                .append("性格:").append(person.getCharacter().getDesc()).append(" ")
                .append("状态:").append(angry ? "愤怒" : "正常").append(" ")
                .append("智力:").append(person.getIntelligence()).append(" ")
                .append("生命值:").append(getLife()).append(" ")
                .append("怒气值:").append(getAnger());
        return stringBuilder.toString();

    }
}
