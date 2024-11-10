package com.adam.verbal_battle;

import com.adam.verbal_battle.game.Round;
import com.adam.verbal_battle.game.VerbalBattleGame;
import com.adam.verbal_battle.person.Person;
import com.adam.verbal_battle.person.PersonRepository;
import com.adam.verbal_battle.player.ArtificialPlayer;
import com.adam.verbal_battle.player.ComputerPlayer;
import com.adam.verbal_battle.player.Player;
import com.adam.verbal_battle.player.SuperComputerPlayer;

public class VerbalBattleMain {
    public static void main(String[] args) {
        PersonRepository.getINSTANCE().loadPersons();
        ConsoleUtils.println("舌战 v1.0");
        int menuChoice = 0;
        while(menuChoice != VerbalBattleGame.GAME_MODE_EXIT) {
            ConsoleUtils.println("1.玩家对电脑");
            ConsoleUtils.println("2.玩家对超级电脑");
            ConsoleUtils.println("3.玩家对玩家");
            ConsoleUtils.println("4.电脑对电脑");
            ConsoleUtils.println("5.电脑对超级电脑");
            ConsoleUtils.println("6.超级电脑对超级电脑");
            ConsoleUtils.println("7.退出游戏");
            menuChoice = ConsoleUtils.inputWithRange("请选择游戏模式（序号）：", 1, 7);

            Player player = null, componentPlayer = null;
            int index;
            if(menuChoice != VerbalBattleGame.GAME_MODE_EXIT) {
                VerbalBattleGame.getINSTANCE().setGameMode(menuChoice);
            }
            switch (menuChoice) {
                case VerbalBattleGame.GAME_MODE_ARTIFICIAL_COMPUTER:
                case VerbalBattleGame.GAME_MODE_ARTIFICIAL_SUPERCOMPUTER:
                    if(menuChoice == VerbalBattleGame.GAME_MODE_ARTIFICIAL_COMPUTER) {
                        ConsoleUtils.println("玩家对电脑");
                        player = new ArtificialPlayer("玩家");
                        componentPlayer = new ComputerPlayer("电脑");
                    } else if(menuChoice == VerbalBattleGame.GAME_MODE_ARTIFICIAL_SUPERCOMPUTER) {
                        ConsoleUtils.println("玩家对超级电脑");
                        player = new ArtificialPlayer("玩家");
                        componentPlayer = new ComputerPlayer("超级电脑");
                    } else {
                        ConsoleUtils.printErrorAndExit("Game invalid state");
                    }
                    VerbalBattleGame.getINSTANCE().setPlayer(player);
                    VerbalBattleGame.getINSTANCE().setComponentPlayer(componentPlayer);
                    ConsoleUtils.print(PersonRepository.getINSTANCE().formatPersonsWithHeader());
                    Person computerPerson = ((ComputerPlayer) componentPlayer).choosePerson();
                    ConsoleUtils.println(componentPlayer.getPlayerName() + "选择了：" + computerPerson.getName());
                    index = ConsoleUtils.inputWithRangeWithExclusion("请选择人物（序号）：", 1,
                            PersonRepository.getINSTANCE().getPersonSize(), computerPerson.getIndex());
                    Person artificialPerson = player.choosePerson(index);
                    ConsoleUtils.println("你选择了：" + artificialPerson.getName());
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
                    break;
                case VerbalBattleGame.GAME_MODE_SUPERCOMPUTER_SUPERCOMPUTER:
                case VerbalBattleGame.GAME_MODE_COMPUTER_COMPUTER:
                case VerbalBattleGame.GAME_MODE_COMPUTER_SUPERCOMPUTER:
                    if(menuChoice == VerbalBattleGame.GAME_MODE_COMPUTER_COMPUTER) {
                        ConsoleUtils.println("电脑对电脑");
                        player = new ComputerPlayer("电脑1");
                        componentPlayer = new ComputerPlayer("电脑2");
                    } else if(menuChoice == VerbalBattleGame.GAME_MODE_SUPERCOMPUTER_SUPERCOMPUTER) {
                        ConsoleUtils.println("超级电脑对超级电脑");
                        player = new SuperComputerPlayer("超级电脑1");
                        componentPlayer = new SuperComputerPlayer("超级电脑2");
                    } else if(menuChoice == VerbalBattleGame.GAME_MODE_COMPUTER_SUPERCOMPUTER) {
                        ConsoleUtils.println("电脑对超级电脑");
                        player = new ComputerPlayer("电脑");
                        componentPlayer = new SuperComputerPlayer("超级电脑");
                    } else {
                        ConsoleUtils.printErrorAndExit("Game invalid state");
                    }
                    VerbalBattleGame.getINSTANCE().setPlayer(player);
                    VerbalBattleGame.getINSTANCE().setComponentPlayer(componentPlayer);
                    ConsoleUtils.print(PersonRepository.getINSTANCE().formatPersonsWithHeader());
                    Person computer1Person = ((ComputerPlayer) player).choosePerson();
                    Person computer2Person = ((ComputerPlayer) componentPlayer).choosePersonWithExclusion(computer1Person.getIndex());
                    ConsoleUtils.println(player.getPlayerName() + "选择了：" + computer1Person.getName());
                    ConsoleUtils.println(componentPlayer.getPlayerName() + "选择了：" + computer2Person.getName());
                    break;
            }
            if(menuChoice == VerbalBattleGame.GAME_MODE_EXIT) {
                break;
            }
            VerbalBattleGame.getINSTANCE().initializeCards();
            VerbalBattleGame.getINSTANCE().printPlayerStatus();
            ConsoleUtils.println("-------------游戏开始------------");
            do {
                VerbalBattleGame.getINSTANCE().startNewRound();
                VerbalBattleGame.getINSTANCE().printPlayerCards();
                VerbalBattleGame.getINSTANCE().roundMove();
                VerbalBattleGame.getINSTANCE().roundJudge();
                VerbalBattleGame.getINSTANCE().roundTakeEffects();
                if(!VerbalBattleGame.getINSTANCE().isGameOver()) {
                    VerbalBattleGame.getINSTANCE().addCards();
                    VerbalBattleGame.getINSTANCE().automaticEndAngryStatus();
                }
                VerbalBattleGame.getINSTANCE().printPlayerStatus();
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