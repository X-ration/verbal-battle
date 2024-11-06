package com.adam.verbal_battle.game;

import com.adam.verbal_battle.Assert;
import com.adam.verbal_battle.ConsoleUtils;
import com.adam.verbal_battle.DebugUtils;
import com.adam.verbal_battle.player.ArtificialPlayer;
import com.adam.verbal_battle.player.ComputerPlayer;
import com.adam.verbal_battle.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class VerbalBattleGame {

    private static VerbalBattleGame INSTANCE;
    private static double CHANCE_SPECIAL_CARD = 0.15;
    private static int ROUND_TYPE_KEEP_ROUNDS = 5;

    private ArtificialPlayer artificialPlayer = (ArtificialPlayer) ArtificialPlayer.getINSTANCE();
    private ComputerPlayer computerPlayer = (ComputerPlayer) ComputerPlayer.getINSTANCE();
    private boolean gameOver;
    private boolean win;
    private List<Round> roundList = new LinkedList<>();
    private int lastRoundTypeChangedRound = 1;
    private boolean firstRoundChangeRoundType = false;

    public static VerbalBattleGame getINSTANCE() {
        if(INSTANCE == null) {
            synchronized (VerbalBattleGame.class) {
                if(INSTANCE == null) {
                    INSTANCE = new VerbalBattleGame();
                }
            }
        }
        return INSTANCE;
    }

    public boolean isGameOver() {
        return gameOver;
    }

    public void setGameOver(boolean gameOver) {
        this.gameOver = gameOver;
    }

    public boolean isWin() {
        return win;
    }

    public void setWin(boolean win) {
        this.win = win;
    }

    public void printPlayerStatus() {
        ConsoleUtils.print(artificialPlayer.toString());
        ConsoleUtils.print("|");
        ConsoleUtils.println(computerPlayer.toString());
    }

    public void printPlayerCards() {
        ConsoleUtils.print("玩家卡片:" + artificialPlayer.formatCards());
        ConsoleUtils.print("|");
        ConsoleUtils.println("电脑卡片:" + computerPlayer.formatCards());
    }

    public void initializeCards() {
        initializeCards(computerPlayer);
        initializeCards(artificialPlayer);
    }

    private void initializeCards(Player player) {
        int intelligence = player.getPerson().getIntelligence();
        int numOfCards = 0;
        if(intelligence < 60) {
            numOfCards = 2;
        } else if(intelligence < 80) {
            numOfCards = 3;
        } else if(intelligence < 90) {
            numOfCards = 4;
        } else {
            numOfCards = 5;
        }
        while(numOfCards-- > 0) {
            Card card = generateCard(player);
            player.addCard(card);
        }
    }

    private Card generateCard(Player player) {
        Random random = new Random();
        double chance = random.nextDouble();
        Card card;
        if(chance < CHANCE_SPECIAL_CARD) {
            card = SpecialCard.values()[random.nextInt(SpecialCard.values().length)];
        } else {
            //todo 发牌根据智力调整获得大牌的概率
            card = NormalCard.values()[random.nextInt(NormalCard.values().length)];
        }
        return card;
    }

    public void addCards() {
        addCard(artificialPlayer);
        addCard(computerPlayer);
    }

    private void addCard(Player player) {
        Card card = generateCard(player);
        player.addCard(card);
    }

    public void startNewRound() {
        Assert.assertTrue(!gameOver, "startNewRound invalid state");
        Random random = new Random();
        Round round = new Round(roundList.size() + 1);
        if(roundList.size() == 0) {
            round.setRoundType(CardType.values()[random.nextInt(CardType.values().length)]);
            round.setWin(true);
        } else {
            round.setWin(roundList.get(roundList.size() - 1).isWin());
            boolean changeRoundType = false;
            if(roundList.size() == 1) {
                changeRoundType = firstRoundChangeRoundType;
            } else {
                //特殊卡牌发挥作用改变回合环境
                if (lastRoundTypeChangedRound == roundList.size()) {
                    changeRoundType = true;
                } else if (roundList.size() - lastRoundTypeChangedRound < ROUND_TYPE_KEEP_ROUNDS) {
                    changeRoundType = false;
                } else {
                    changeRoundType = true;
                }
            }
            if(changeRoundType) {
                CardType lastRoundType = roundList.get(roundList.size() - 1).getRoundType();
                CardType roundType;
                do {
                    roundType = CardType.values()[random.nextInt(CardType.values().length)];
                } while (roundType == lastRoundType);
                round.setRoundType(roundType);
                lastRoundTypeChangedRound = round.getIndex() - 1;
                DebugUtils.debugPrintln("lastRoundTypeChangedRound=" + lastRoundTypeChangedRound);
            } else {
                round.setRoundType(roundList.get(roundList.size() - 1).getRoundType());
            }
        }
        roundList.add(round);
        ConsoleUtils.println("-------------第" + round.getIndex() + "回合,环境:" + round.getRoundType().getDesc() + "------------");
    }

    //todo 电脑出牌策略
    public Card computerMove() {
        Round round = roundList.get(roundList.size() - 1);
        Random random = new Random();
        int cardIndex = random.nextInt(computerPlayer.getCardSize()) + 1;
        Card card = computerPlayer.removeCard(cardIndex);
        round.setComputerMove(card);
        return card;
    }

    public Card artificialMove() {
        int index = ConsoleUtils.inputWithRange("请选择卡牌（序号）：", 1, artificialPlayer.getCardSize());
        Assert.assertTrue(index>0 && index <= artificialPlayer.getCardSize(), "artificialMove index invalid");
        Round round = roundList.get(roundList.size() - 1);
        Card card = artificialPlayer.removeCard(index);
        round.setArtificialMove(card);
        return card;
    }

    public void roundMove() {
        Card artificialMove, computerMove;
        if(roundList.size() == 1) {
            artificialMove = artificialMove();
            computerMove = computerMove();
            ConsoleUtils.println("玩家出牌：" + artificialMove.getDesc());
            ConsoleUtils.println("电脑出牌：" + computerMove.getDesc());
        } else {
            Round lastRound = roundList.get(roundList.size() - 2);
            if(lastRound.isWin()) {
                artificialMove = artificialMove();
                computerMove = computerMove();
                ConsoleUtils.println("玩家出牌：" + artificialMove.getDesc());
                ConsoleUtils.println("电脑出牌：" + computerMove.getDesc());
            } else {
                computerMove = computerMove();
                ConsoleUtils.println("电脑出牌：" + (computerMove instanceof SpecialCard ? "特殊卡片" : "普通卡片"));
                artificialMove = artificialMove();
                ConsoleUtils.println("玩家出牌：" + artificialMove.getDesc());
                ConsoleUtils.println("电脑出牌：" + computerMove.getDesc());
            }
        }
    }

    public void roundJudge() {
        Round round = roundList.get(roundList.size() - 1);
        Card computerMove = round.getComputerMove(),
                artificialMove = round.getArtificialMove();
        round.setEffect(Effect.NONE);
        //同时出特殊牌
        if(computerMove instanceof SpecialCard && artificialMove instanceof SpecialCard) {
            if(computerMove == artificialMove) {
            }
            else if(computerMove == SpecialCard.ROAR && artificialMove == SpecialCard.IGNORE) {
                round.setWin(true);
                round.setEffect(Effect.ARTIFICIAL);
            }
            else if(computerMove == SpecialCard.ROAR && artificialMove == SpecialCard.ANGRY) {
                round.setWin(false);
                round.setEffect(Effect.BOTH);
            }
            else if(computerMove == SpecialCard.ROAR && artificialMove == SpecialCard.CALM) {
                round.setWin(false);
                round.setEffect(Effect.BOTH);
            }
            else if(artificialMove == SpecialCard.ROAR && computerMove == SpecialCard.IGNORE) {
                round.setWin(false);
                round.setEffect(Effect.COMPUTER);
            }
            else if(artificialMove == SpecialCard.ROAR && computerMove == SpecialCard.ANGRY) {
                round.setWin(true);
                round.setEffect(Effect.BOTH);
            }
            else if(artificialMove == SpecialCard.ROAR && computerMove == SpecialCard.CALM) {
                round.setWin(true);
                round.setEffect(Effect.BOTH);
            }
            else if(computerMove == SpecialCard.IGNORE && artificialMove == SpecialCard.ANGRY) {
                round.setWin(false);
                round.setEffect(Effect.COMPUTER);
            }
            else if(computerMove == SpecialCard.IGNORE && artificialMove == SpecialCard.CALM) {
                round.setWin(false);
                round.setEffect(Effect.COMPUTER);
            }
            else if(artificialMove == SpecialCard.IGNORE && computerMove == SpecialCard.ANGRY) {
                round.setWin(true);
                round.setEffect(Effect.ARTIFICIAL);
            }
            else if(artificialMove == SpecialCard.IGNORE && computerMove == SpecialCard.CALM) {
                round.setWin(true);
                round.setEffect(Effect.ARTIFICIAL);
            }
            else if(computerMove == SpecialCard.ANGRY && artificialMove == SpecialCard.CALM) {
            }
            else if(artificialMove == SpecialCard.ANGRY && computerMove == SpecialCard.CALM) {
            }
            else {
                ConsoleUtils.printErrorAndExit("Invalid game state");
            }
        }
        //电脑特殊牌，玩家普通牌
        else if(computerMove instanceof SpecialCard && artificialMove instanceof NormalCard) {
            if(computerMove == SpecialCard.ROAR) {
                round.setWin(false);
                round.setEffect(Effect.COMPUTER);
            } else if(computerMove == SpecialCard.IGNORE) {
                round.setWin(false);
                round.setEffect(Effect.COMPUTER);
            } else if(computerMove == SpecialCard.ANGRY || computerMove == SpecialCard.CALM) {
                round.setWin(true);
                round.setEffect(Effect.BOTH);
            } else {
                ConsoleUtils.printErrorAndExit("Invalid game state");
            }
        }
        //电脑普通牌，玩家特殊牌
        else if(computerMove instanceof NormalCard && artificialMove instanceof SpecialCard) {
            if(artificialMove == SpecialCard.ROAR) {
                round.setWin(true);
                round.setEffect(Effect.ARTIFICIAL);
            } else if(artificialMove == SpecialCard.IGNORE) {
                round.setWin(true);
                round.setEffect(Effect.ARTIFICIAL);
            } else if(artificialMove == SpecialCard.ANGRY || artificialMove == SpecialCard.CALM) {
                round.setWin(false);
                round.setEffect(Effect.BOTH);
            } else {
                ConsoleUtils.printErrorAndExit("Invalid game state");
            }
        }
        //电脑普通牌，玩家普通牌
        else if(computerMove instanceof NormalCard && artificialMove instanceof NormalCard) {
            if(((NormalCard) computerMove).getType() == ((NormalCard) artificialMove).getType()) {
                if(((NormalCard) computerMove).getPower().ordinal() < ((NormalCard) artificialMove).getPower().ordinal()) {
                    round.setWin(true);
                    round.setEffect(Effect.ARTIFICIAL);
                }
                else if(((NormalCard) computerMove).getPower().ordinal() > ((NormalCard) artificialMove).getPower().ordinal()) {
                    round.setWin(false);
                    round.setEffect(Effect.COMPUTER);
                }
                else {
                }
            }
            else if(((NormalCard) computerMove).getType() == round.getRoundType()) {
                round.setWin(false);
                round.setEffect(Effect.COMPUTER);
            }
            else if(((NormalCard) artificialMove).getType() == round.getRoundType()) {
                round.setWin(true);
                round.setEffect(Effect.ARTIFICIAL);
            }
            else {
                if(((NormalCard) computerMove).getPower().ordinal() < ((NormalCard) artificialMove).getPower().ordinal()) {
                    round.setWin(true);
                    round.setEffect(Effect.ARTIFICIAL);
                }
                else if(((NormalCard) computerMove).getPower().ordinal() > ((NormalCard) artificialMove).getPower().ordinal()) {
                    round.setWin(false);
                    round.setEffect(Effect.COMPUTER);
                }
                else {
                }
            }
        } else {
            ConsoleUtils.printErrorAndExit("Invalid game state");
        }
        if(computerMove instanceof SpecialCard || artificialMove instanceof SpecialCard) {
            lastRoundTypeChangedRound = round.getIndex();
            DebugUtils.debugPrintln("lastRoundTypeChangedRound=" + lastRoundTypeChangedRound);
            if(roundList.size() == 1) {
                firstRoundChangeRoundType = true;
            }
        }
    }

    public void roundTakeEffects() {
        Round round = roundList.get(roundList.size() - 1);
        switch (round.getEffect()) {
            case BOTH:
                if(round.getComputerMove() == SpecialCard.ROAR) {
                    artificialPlayer.changeLife(((SpecialCard) round.getComputerMove()).getLifeHit());
                    artificialPlayer.changeAnger(((SpecialCard)round.getComputerMove()).getAngerHit());
                } else if(round.getComputerMove() == SpecialCard.ANGRY) {
                    computerPlayer.changeAnger(((SpecialCard) round.getComputerMove()).getAngerHit());
                } else if(round.getComputerMove() instanceof SpecialCard) {
                    artificialPlayer.changeAnger(((SpecialCard)round.getComputerMove()).getAngerHit());
                } else {
                    artificialPlayer.changeLife(((NormalCard)round.getComputerMove()).getLifeHit());
                    artificialPlayer.changeAnger(((NormalCard)round.getComputerMove()).getAngerHit());
                }
                if(round.getArtificialMove() == SpecialCard.ROAR) {
                    computerPlayer.changeLife(((SpecialCard) round.getArtificialMove()).getLifeHit());
                    computerPlayer.changeAnger(((SpecialCard)round.getArtificialMove()).getAngerHit());
                } else if(round.getArtificialMove() == SpecialCard.ANGRY) {
                    artificialPlayer.changeAnger(((SpecialCard) round.getArtificialMove()).getAngerHit());
                } else if(round.getArtificialMove() instanceof SpecialCard) {
                    computerPlayer.changeAnger(((SpecialCard)round.getArtificialMove()).getAngerHit());
                } else {
                    computerPlayer.changeLife(((NormalCard)round.getArtificialMove()).getLifeHit());
                    computerPlayer.changeAnger(((NormalCard)round.getArtificialMove()).getAngerHit());
                }
                break;
            case NONE:
                ConsoleUtils.println("平局，不产生效果");
                break;
            case COMPUTER:
                if(round.getComputerMove() == SpecialCard.ROAR) {
                    artificialPlayer.changeLife(((SpecialCard) round.getComputerMove()).getLifeHit());
                    artificialPlayer.changeAnger(((SpecialCard)round.getComputerMove()).getAngerHit());
                } else if(round.getComputerMove() == SpecialCard.ANGRY) {
                    computerPlayer.changeAnger(((SpecialCard) round.getComputerMove()).getAngerHit());
                } else if(round.getComputerMove() instanceof SpecialCard) {
                    artificialPlayer.changeAnger(((SpecialCard)round.getComputerMove()).getAngerHit());
                } else {
                    artificialPlayer.changeLife(((NormalCard)round.getComputerMove()).getLifeHit());
                    artificialPlayer.changeAnger(((NormalCard)round.getComputerMove()).getAngerHit());
                }
                break;
            case ARTIFICIAL:
                if(round.getArtificialMove() == SpecialCard.ROAR) {
                    computerPlayer.changeLife(((SpecialCard) round.getArtificialMove()).getLifeHit());
                    computerPlayer.changeAnger(((SpecialCard)round.getArtificialMove()).getAngerHit());
                } else if(round.getArtificialMove() == SpecialCard.ANGRY) {
                    artificialPlayer.changeAnger(((SpecialCard) round.getArtificialMove()).getAngerHit());
                } else if(round.getArtificialMove() instanceof SpecialCard) {
                    computerPlayer.changeAnger(((SpecialCard)round.getArtificialMove()).getAngerHit());
                } else {
                    computerPlayer.changeLife(((NormalCard)round.getArtificialMove()).getLifeHit());
                    computerPlayer.changeAnger(((NormalCard)round.getArtificialMove()).getAngerHit());
                }
                break;
        }
        printPlayerStatus();
        if(artificialPlayer.getLife() == 0) {
            setWin(false);
            setGameOver(true);
        } else if(computerPlayer.getLife() == 0) {
            setWin(true);
            setGameOver(true);
        }
    }
}
