package com.adam.verbal_battle;

import com.adam.verbal_battle.game.Round;
import com.adam.verbal_battle.game.VerbalBattleGame;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;
import com.adam.verbal_battle.player.ArtificialPlayer;
import com.adam.verbal_battle.player.ComputerPlayer;
import com.adam.verbal_battle.player.Player;

public class VerbalBattleMain {
    public static void main(String[] args) {
        PersonRepository.getINSTANCE().loadPersons();
        ConsoleUtils.println("舌战 v1.0");
        int menuChoice = 0;
        while(menuChoice != 4) {
            ConsoleUtils.println("1.玩家对电脑");
            ConsoleUtils.println("2.玩家对玩家");
            ConsoleUtils.println("3.电脑对电脑");
            ConsoleUtils.println("4.退出游戏");
            menuChoice = ConsoleUtils.inputWithRange("请选择游戏模式（序号）：", 1, 4);

            Player player = null, componentPlayer = null;
            int index;
            if(menuChoice != 4) {
                VerbalBattleGame.getINSTANCE().setGameMode(menuChoice);
            }
            switch (menuChoice) {
                case VerbalBattleGame.GAME_MODE_ARTIFICIAL_COMPUTER:
                    ConsoleUtils.println("玩家对电脑");
                    componentPlayer = new ComputerPlayer("电脑");
                    player = new ArtificialPlayer("玩家");
                    VerbalBattleGame.getINSTANCE().setPlayer(player);
                    VerbalBattleGame.getINSTANCE().setComponentPlayer(componentPlayer);
                    ConsoleUtils.print(PersonRepository.getINSTANCE().formatPersonsWithHeader());
                    Person computerPerson = ((ComputerPlayer) componentPlayer).choosePerson();
                    ConsoleUtils.println("电脑选择了：" + computerPerson.getName());
                    index = ConsoleUtils.inputWithRangeWithExclusion("请选择人物（序号）：", 1,
                            PersonRepository.getINSTANCE().getPersonSize(), computerPerson.getIndex());
                    Person artificialPerson = player.choosePerson(index);
                    ConsoleUtils.println("你选择了：" + artificialPerson.getName());
                    ConsoleUtils.println("-------------游戏开始------------");
                    VerbalBattleGame.getINSTANCE().initializeCards();
                    VerbalBattleGame.getINSTANCE().printPlayerStatus();
                    break;
                case VerbalBattleGame.GAME_MODE_ARTIFICIAL_ARTIFICIAL:
                    ConsoleUtils.println("玩家对玩家");
                    player = new ArtificialPlayer("玩家1");
                    componentPlayer = new ArtificialPlayer("玩家2");
                    VerbalBattleGame.getINSTANCE().setPlayer(player);
                    VerbalBattleGame.getINSTANCE().setComponentPlayer(componentPlayer);
                    ConsoleUtils.print(PersonRepository.getINSTANCE().formatPersonsWithHeader());
                    index = ConsoleUtils.inputWithRange(player.getPlayerName() + "请选择人物（序号）：", 1,
                        PersonRepository.getINSTANCE().getPersonSize());
                    Person playerPerson = player.choosePerson(index);
                    index = ConsoleUtils.inputWithRangeWithExclusion(componentPlayer.getPlayerName() + "请选择人物（序号）：", 1,
                            PersonRepository.getINSTANCE().getPersonSize(), index);
                    Person componentPlayerPerson = componentPlayer.choosePerson(index);
                    ConsoleUtils.println(player.getPlayerName() + "选择了：" + playerPerson.getName());
                    ConsoleUtils.println(componentPlayer.getPlayerName() + "选择了：" + componentPlayerPerson.getName());
                    ConsoleUtils.println("-------------游戏开始------------");
                    VerbalBattleGame.getINSTANCE().initializeCards();
                    VerbalBattleGame.getINSTANCE().printPlayerStatus();
                    break;
                case VerbalBattleGame.GAME_MODE_COMPUTER_COMPUTER:
                    ConsoleUtils.println("电脑对电脑");
                    player = new ComputerPlayer("电脑1");
                    componentPlayer = new ComputerPlayer("电脑2");
                    VerbalBattleGame.getINSTANCE().setPlayer(player);
                    VerbalBattleGame.getINSTANCE().setComponentPlayer(componentPlayer);
                    ConsoleUtils.print(PersonRepository.getINSTANCE().formatPersonsWithHeader());
                    Person computer1Person = ((ComputerPlayer) player).choosePerson();
                    Person computer2Person = ((ComputerPlayer) componentPlayer).choosePersonWithExclusion(computer1Person.getIndex());
                    ConsoleUtils.println(player.getPlayerName() + "选择了：" + computer1Person.getName());
                    ConsoleUtils.println(componentPlayer.getPlayerName() + "选择了：" + computer2Person.getName());
                    ConsoleUtils.println("-------------游戏开始------------");
                    VerbalBattleGame.getINSTANCE().initializeCards();
                    VerbalBattleGame.getINSTANCE().printPlayerStatus();
                    break;
            }
            if(menuChoice == 4) {
                break;
            }
            do {
                VerbalBattleGame.getINSTANCE().startNewRound();
                VerbalBattleGame.getINSTANCE().printPlayerCards();
                VerbalBattleGame.getINSTANCE().roundMove();
                VerbalBattleGame.getINSTANCE().roundJudge();
                VerbalBattleGame.getINSTANCE().roundTakeEffects();
                VerbalBattleGame.getINSTANCE().addCards();
            } while (!VerbalBattleGame.getINSTANCE().isGameOver());
            if(VerbalBattleGame.getINSTANCE().getGameMode() == VerbalBattleGame.GAME_MODE_ARTIFICIAL_COMPUTER) {
                ConsoleUtils.println("-------------游戏结束，你" + (VerbalBattleGame.getINSTANCE().isWin() ? "赢" : "输") + "了！------------");
            } else {
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("-------------游戏结束，");
                if(VerbalBattleGame.getINSTANCE().isWin()) {
                    stringBuilder.append(VerbalBattleGame.getINSTANCE().getPlayer().getPlayerName());
                } else {
                    stringBuilder.append(VerbalBattleGame.getINSTANCE().getComponentPlayer().getPlayerName());
                }
                stringBuilder.append("赢了！------------");
                ConsoleUtils.println(stringBuilder.toString());
            }
            VerbalBattleGame.getINSTANCE().reset();
        }
        ConsoleUtils.println("退出游戏");
    }
}