package com.adam.verbal_battle;

import com.adam.verbal_battle.game.Round;
import com.adam.verbal_battle.game.VerbalBattleGame;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;
import com.adam.verbal_battle.player.ArtificialPlayer;
import com.adam.verbal_battle.player.ComputerPlayer;

public class VerbalBattleMain {
    public static void main(String[] args) {
        PersonRepository.getINSTANCE().loadPersons();
        ConsoleUtils.println("舌战 v1.0");
        ConsoleUtils.print(PersonRepository.getINSTANCE().formatPersonsWithHeader());
        Person computerPerson = ComputerPlayer.getINSTANCE().choosePerson();
        ConsoleUtils.println("电脑选择了：" + computerPerson.getName());
        int index = ConsoleUtils.inputWithRangeWithExclusion("请选择人物（序号）：", 1,
                PersonRepository.getINSTANCE().getPersonSize(), computerPerson.getIndex());
        Person artificialPerson = ArtificialPlayer.getINSTANCE().choosePerson(index);
        ConsoleUtils.println("你选择了：" + artificialPerson.getName());
        ConsoleUtils.println("-------------游戏开始------------");
        VerbalBattleGame.getINSTANCE().initializeCards();
        VerbalBattleGame.getINSTANCE().printPlayerStatus();
        do {
            VerbalBattleGame.getINSTANCE().startNewRound();
            VerbalBattleGame.getINSTANCE().printPlayerCards();
            VerbalBattleGame.getINSTANCE().roundMove();
            VerbalBattleGame.getINSTANCE().roundJudge();
            VerbalBattleGame.getINSTANCE().roundTakeEffects();
            VerbalBattleGame.getINSTANCE().addCards();
        } while(!VerbalBattleGame.getINSTANCE().isGameOver());
        ConsoleUtils.println("-------------游戏结束，你" + (VerbalBattleGame.getINSTANCE().isWin() ? "赢" : "输") + "了！------------");
    }
}