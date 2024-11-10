package com.adam.verbal_battle;

import com.adam.verbal_battle.game.MenuChoice;
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
        MenuChoice menuChoice = null;
        while(menuChoice == null || menuChoice != MenuChoice.EXIT) {
            MenuChoice.printMenuChoices();
            int choice = ConsoleUtils.inputWithRange("请选择（序号）：", 1, MenuChoice.values().length);
            menuChoice = MenuChoice.values()[choice-1];

            Player player = null, componentPlayer = null;
            int index;
            if(menuChoice != MenuChoice.EXIT) {
                VerbalBattleGame.getINSTANCE().setGameMode(menuChoice);
            }
            ConsoleUtils.println(menuChoice.getDesc());
            switch (menuChoice) {
                case ARTIFICIAL_COMPUTER:
                case ARTIFICIAL_SUPERCOMPUTER:
                    if(menuChoice == MenuChoice.ARTIFICIAL_COMPUTER) {
                        player = new ArtificialPlayer("玩家");
                        componentPlayer = new ComputerPlayer("电脑");
                    } else if(menuChoice == MenuChoice.ARTIFICIAL_SUPERCOMPUTER) {
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
                case ARTIFICIAL_ARTIFICIAL:
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
                case SUPERCOMPUTER_SUPERCOMPUTER:
                case COMPUTER_COMPUTER:
                case COMPUTER_SUPERCOMPUTER:
                    if(menuChoice == MenuChoice.COMPUTER_COMPUTER) {
                        player = new ComputerPlayer("电脑1");
                        componentPlayer = new ComputerPlayer("电脑2");
                    } else if(menuChoice == MenuChoice.SUPERCOMPUTER_SUPERCOMPUTER) {
                        player = new SuperComputerPlayer("超级电脑1");
                        componentPlayer = new SuperComputerPlayer("超级电脑2");
                    } else if(menuChoice == MenuChoice.COMPUTER_SUPERCOMPUTER) {
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
            if(menuChoice == MenuChoice.EXIT) {
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
            if(VerbalBattleGame.getINSTANCE().getGameMode() == MenuChoice.ARTIFICIAL_COMPUTER
                    || VerbalBattleGame.getINSTANCE().getGameMode() == MenuChoice.ARTIFICIAL_SUPERCOMPUTER) {
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
            menuChoice = null;
        }
    }
}