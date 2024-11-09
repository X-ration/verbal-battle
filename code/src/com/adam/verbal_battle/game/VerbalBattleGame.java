package com.adam.verbal_battle.game;

import com.adam.verbal_battle.Assert;
import com.adam.verbal_battle.ConsoleUtils;
import com.adam.verbal_battle.DebugUtils;
import com.adam.verbal_battle.person.Character;
import com.adam.verbal_battle.player.ArtificialPlayer;
import com.adam.verbal_battle.player.ComputerPlayer;
import com.adam.verbal_battle.player.Player;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;

public class VerbalBattleGame {

    private static VerbalBattleGame INSTANCE;
    /**
     * 生成特殊卡片的概率
     */
    public static final double CHANCE_SPECIAL_CARD = 0.15;
    /**
     * 只出普通卡片的情况下回合环境可以保持的回合数
     */
    public static final int ROUND_TYPE_KEEP_ROUNDS = 5;
    /**
     * 愤怒状态持续的回合数
     */
    public static final int ANGRY_KEEP_ROUNDS = 3;
    /**
     * 莽撞性格愤怒状态产生的生命伤害
     */
    public static final int RASH_ANGRY_LIFE_HIT = -30;
    /**
     * 超级电脑最多使用熟虑的次数
     */
    public static final int SUPERCOMPUTER_REINITIALIZE_LIMIT = 3;
    /**
     * 超级电脑在双方同时有特殊牌时决定压制的概率
     */
    public static final double SUPERCOMPUTER_BOTH_SPECIAL_SUPPRESS_CHANCE = 0.8;
    /**
     * 超级电脑在对手有特殊牌时决定压制的概率
     */
    public static final double SUPERCOMPUTER_COMPONENT_SPECIAL_SUPPRESS_CHANCE = 0.5;
    /**
     * 超级电脑在对手有特殊牌时被压制决定出最小牌的概率
     */
    public static final double SUPERCOMPUTER_COMPONENT_SPECIAL_SUPPRESSED_CHANCE = 0.5;
    /**
     * 超级电脑在双方有普通牌时自己手牌与回合环境一致力量为中决定压制的概率
     */
    public static final double SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSING_MEDIUM_CHANCE = 0.8;
    /**
     * 超级电脑在双方有普通牌时自己手牌与回合环境一致力量为小决定压制的概率
     */
    public static final double SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSING_SMALL_CHANCE = 0.5;
    /**
     * 超级电脑在双方有普通牌时决定压制的概率
     */
    public static final double SUPERCOMPUTER_BOTH_NORMAL_SUPPRESS_CHANCE = 0.34;
    /**
     * 超级电脑在双方有普通牌时被压制决定出最小牌的概率
     */
    public static final double SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSED_SMALLEST_CHANCE = 0.5;
    /**
     * 超级电脑在双方有普通牌时被压制决定出最大牌的概率
     */
    public static final double SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSED_BIGGEST_CHANCE = 0.5;
    public static final int GAME_MODE_ARTIFICIAL_COMPUTER = 1;
    public static final int GAME_MODE_ARTIFICIAL_SUPERCOMPUTER = 2;
    public static final int GAME_MODE_ARTIFICIAL_ARTIFICIAL = 3;
    public static final int GAME_MODE_COMPUTER_COMPUTER = 4;
    public static final int GAME_MODE_COMPUTER_SUPERCOMPUTER = 5;
    public static final int GAME_MODE_SUPERCOMPUTER_SUPERCOMPUTER = 6;
    public static final int GAME_MODE_EXIT = 7;
    private static final Card[] smallCards = {NormalCard.PRINCIPLE_SMALL, NormalCard.SEASON_SMALL, NormalCard.STORY_SMALL},
            mediumCards = {NormalCard.PRINCIPLE_MEDIUM, NormalCard.SEASON_MEDIUM, NormalCard.STORY_MEDIUM},
            bigCards = {NormalCard.PRINCIPLE_BIG, NormalCard.SEASON_BIG, NormalCard.STORY_BIG};

    private Player player;
    private Player componentPlayer;
    private boolean gameOver;
    private boolean win;
    private List<Round> roundList = new LinkedList<>();
    private int lastRoundTypeChangedRound = 1;
    private int gameMode;
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

    /**
     * 重新恢复单例对象的初始状态
     */
    public void reset() {
        this.player = null;
        this.componentPlayer = null;
        this.gameOver = false;
        this.win = false;
        this.roundList = new LinkedList<>();
        this.lastRoundTypeChangedRound = 1;
        this.gameMode = 0;
        this.firstRoundChangeRoundType = false;
    }

    public int getGameMode() {
        return gameMode;
    }

    public void setGameMode(int gameMode) {
        this.gameMode = gameMode;
    }

    public Player getPlayer() {
        return player;
    }

    public void setPlayer(Player player) {
        this.player = player;
    }

    public Player getComponentPlayer() {
        return componentPlayer;
    }

    public void setComponentPlayer(Player componentPlayer) {
        this.componentPlayer = componentPlayer;
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
        ConsoleUtils.print(this.player.toString());
        ConsoleUtils.print("|");
        ConsoleUtils.println(this.componentPlayer.toString());
    }

    public void printPlayerCards() {
        ConsoleUtils.print(player.getPlayerName() + "卡片:" + this.player.formatCards());
        ConsoleUtils.print("|");
        if(gameMode == GAME_MODE_ARTIFICIAL_COMPUTER) {
            ConsoleUtils.println(componentPlayer.getPlayerName() + "卡片:" + ((ComputerPlayer)this.componentPlayer).formatCoveringCards());
        } else {
            ConsoleUtils.println(componentPlayer.getPlayerName() + "卡片:" + this.componentPlayer.formatCards());
        }
    }

    public void initializeCards() {
        initializeCards(this.componentPlayer);
        initializeCards(this.player);
    }

    private void initializeCards(Player player) {
        removeAllCards(player);
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

    private void removeAllCards(Player player) {
        int total = player.getCardSize();
        while(total-- > 0) {
            player.removeCard(total + 1);
        }
    }

    private Card generateCard(Player player) {
        Random random = new Random();
        double chance = random.nextDouble();
        Card card;
        if(chance < CHANCE_SPECIAL_CARD) {
            card = SpecialCard.values()[random.nextInt(SpecialCard.values().length)];
        } else {
            int intelligence = player.getPerson().getIntelligence();
            double smallChance = 0.34, mediumChance = 0.67, bigChance = 1.0;
            //根据智力大小调整发大牌的概率
            if(intelligence > 60 && intelligence <= 80) {
                smallChance = 0.3; mediumChance = 0.65;
            } else if(intelligence > 80 && intelligence <= 100) {
                smallChance = 0.26; mediumChance = 0.62;
            }
            chance = random.nextDouble();
            if(chance >= 0 && chance <= smallChance) {
                card = smallCards[random.nextInt(3)];
            } else if(chance > smallChance && chance <= mediumChance) {
                card = mediumCards[random.nextInt(3)];
            } else if(chance > mediumChance && chance <= bigChance) {
                card = bigCards[random.nextInt(3)];
            } else {
                ConsoleUtils.printErrorAndExit("generateCard invalid state");
                card = null;
            }
            DebugUtils.debugPrintln("generateCard chance="+chance+",smallChance="+smallChance+",mediumChance="+mediumChance);
        }
        DebugUtils.debugPrintln("generateCard player=" + player.getPlayerName() + ",card=" + card.getDesc());
        return card;
    }

    public void addCards() {
        addCard(this.player);
        addCard(this.componentPlayer);
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

    /**
     * 电脑出牌
     * @param player
     * @return
     */
    public Card computerMove(Player player) {
        Round round = roundList.get(roundList.size() - 1);
        Card card = ((ComputerPlayer)player).chooseCard(player == this.player ? this.componentPlayer : this.player, round);
        if(player == this.player) {
            round.setPlayerMove(card);
        } else if(player == this.componentPlayer) {
            round.setComponentPlayerMove(card);
        } else {
            ConsoleUtils.printErrorAndExit("computerMove invalid state");
        }
        return card;
    }

    /**
     * 处于愤怒状态的冷静性格玩家使用一次熟虑
     * @param player
     */
    public void reinitializeCards(Player player) {
        if(player.isAngry() && player.getPerson().getCharacter() == Character.CALM) {
            ConsoleUtils.println(player.getPlayerName() + "使用了熟虑");
            initializeCards(player);
            ConsoleUtils.println(player.getPlayerName() + "卡片:" + player.formatCards());
        }
    }

    /**
     * 玩家出牌
     * @param player
     * @return
     */
    public Card artificialMove(Player player) {
        boolean calmCharacterAngry = player.isAngry() && player.getPerson().getCharacter() == Character.CALM;
        int min = calmCharacterAngry ? 0 : 1;
        int index = ConsoleUtils.inputWithRange(player.getPlayerName() + "请选择卡牌（序号）：", min, player.getCardSize());
        Assert.assertTrue(index>=min && index <= player.getCardSize(), "artificialMove index invalid");
        while(index == 0) {
            ConsoleUtils.println(player.getPlayerName() + "使用了熟虑");
            initializeCards(player);
            ConsoleUtils.println(player.getPlayerName() + "卡片:" + player.formatCards());
            index = ConsoleUtils.inputWithRange(player.getPlayerName() + "请选择卡牌（序号）：", min, player.getCardSize());
            Assert.assertTrue(index>=min && index <= player.getCardSize(), "artificialMove index invalid");
        }
        Round round = roundList.get(roundList.size() - 1);
        Card card = player.removeCard(index);
        if(player == this.player) {
            round.setPlayerMove(card);
        } else if(player == this.componentPlayer) {
            round.setComponentPlayerMove(card);
        } else {
            ConsoleUtils.printErrorAndExit("artificialMove invalid state");
        }
        return card;
    }

    private Card playerMove(Player player) {
        if(player instanceof ArtificialPlayer) {
            return artificialMove(player);
        } else if(player instanceof ComputerPlayer) {
            return computerMove(player);
        } else {
            ConsoleUtils.printErrorAndExit("playerMove invalid state");
            return null;
        }
    }

    public void roundMove() {
        Card playerMove, componentMove;
        if(roundList.size() == 1) {
            playerMove = playerMove(this.player);
            ConsoleUtils.println(this.player.getPlayerName() + "出牌：" + playerMove.getDesc());
            componentMove = playerMove(this.componentPlayer);
            ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
        } else {
            Round lastRound = roundList.get(roundList.size() - 2);
            if(lastRound.isWin()) {
                playerMove = playerMove(this.player);
                ConsoleUtils.println(this.player.getPlayerName() + "出牌：" + playerMove.getDesc());
                componentMove = playerMove(this.componentPlayer);
                ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
            } else {
                componentMove = playerMove(this.componentPlayer);
                if(gameMode == GAME_MODE_ARTIFICIAL_COMPUTER) {
                    ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + (componentMove instanceof SpecialCard ? "特殊卡片" : "普通卡片"));
                } else {
                    ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
                }
                playerMove = playerMove(this.player);
                if(gameMode == GAME_MODE_ARTIFICIAL_COMPUTER) {
                    ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
                }
                ConsoleUtils.println(this.player.getPlayerName() + "出牌：" + playerMove.getDesc());
            }
        }
    }

    public void roundJudge() {
        Round round = roundList.get(roundList.size() - 1);
        Card playerMove = round.getPlayerMove(),
                componentPlayerMove = round.getComponentPlayerMove();
        round.setEffect(Effect.NONE);
        boolean firstMove;
        if(roundList.size() == 1) {
            firstMove = true;
        } else {
            firstMove = roundList.get(roundList.size() - 2).isWin();
        }
        //同时出特殊牌
        if(componentPlayerMove instanceof SpecialCard && playerMove instanceof SpecialCard) {
            if(componentPlayerMove == playerMove && componentPlayerMove == SpecialCard.ROAR) {
            }
            else if(componentPlayerMove == playerMove && (componentPlayerMove == SpecialCard.IGNORE ||
                    componentPlayerMove == SpecialCard.ANGRY || componentPlayerMove == SpecialCard.CALM)) {
                round.setEffect(firstMove ? Effect.BOTH_FIRST : Effect.BOTH_SECOND);
            }
            else if(componentPlayerMove == SpecialCard.ROAR && playerMove == SpecialCard.IGNORE) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            }
            else if(componentPlayerMove == SpecialCard.ROAR && playerMove == SpecialCard.ANGRY) {
                round.setWin(false);
                round.setEffect(Effect.BOTH_SECOND);
            }
            else if(componentPlayerMove == SpecialCard.ROAR && playerMove == SpecialCard.CALM) {
                round.setWin(false);
                round.setEffect(Effect.BOTH_SECOND);
            }
            else if(playerMove == SpecialCard.ROAR && componentPlayerMove == SpecialCard.IGNORE) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            }
            else if(playerMove == SpecialCard.ROAR && componentPlayerMove == SpecialCard.ANGRY) {
                round.setWin(true);
                round.setEffect(Effect.BOTH_FIRST);
            }
            else if(playerMove == SpecialCard.ROAR && componentPlayerMove == SpecialCard.CALM) {
                round.setWin(true);
                round.setEffect(Effect.BOTH_FIRST);
            }
            else if(componentPlayerMove == SpecialCard.IGNORE && playerMove == SpecialCard.ANGRY) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            }
            else if(componentPlayerMove == SpecialCard.IGNORE && playerMove == SpecialCard.CALM) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            }
            else if(playerMove == SpecialCard.IGNORE && componentPlayerMove == SpecialCard.ANGRY) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            }
            else if(playerMove == SpecialCard.IGNORE && componentPlayerMove == SpecialCard.CALM) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            }
            else if(componentPlayerMove == SpecialCard.ANGRY && playerMove == SpecialCard.CALM) {
                round.setEffect(firstMove ? Effect.BOTH_FIRST: Effect.BOTH_SECOND);
            }
            else if(playerMove == SpecialCard.ANGRY && componentPlayerMove == SpecialCard.CALM) {
                round.setEffect(firstMove ? Effect.BOTH_FIRST: Effect.BOTH_SECOND);
            }
            else {
                ConsoleUtils.printErrorAndExit("roundJudge invalid game state");
            }
        }
        //对手特殊牌，自己普通牌
        else if(componentPlayerMove instanceof SpecialCard && playerMove instanceof NormalCard) {
            if(this.componentPlayer.isAngry() && this.componentPlayer.getPerson().getCharacter() == Character.RESOLUTE) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            }
            else if(componentPlayerMove == SpecialCard.ROAR) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            } else if(componentPlayerMove == SpecialCard.IGNORE) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            } else if(componentPlayerMove == SpecialCard.ANGRY || componentPlayerMove == SpecialCard.CALM) {
                round.setWin(true);
                round.setEffect(Effect.BOTH_FIRST);
            } else {
                ConsoleUtils.printErrorAndExit("roundJudge invalid game state");
            }
        }
        //对手普通牌，自己特殊牌
        else if(componentPlayerMove instanceof NormalCard && playerMove instanceof SpecialCard) {
            if(this.player.isAngry() && this.player.getPerson().getCharacter() == Character.RESOLUTE) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            }
            else if(playerMove == SpecialCard.ROAR) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            } else if(playerMove == SpecialCard.IGNORE) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            } else if(playerMove == SpecialCard.ANGRY || playerMove == SpecialCard.CALM) {
                round.setWin(false);
                round.setEffect(Effect.BOTH_SECOND);
            } else {
                ConsoleUtils.printErrorAndExit("roundJudge invalid game state");
            }
        }
        //同时出普通牌
        else if(componentPlayerMove instanceof NormalCard && playerMove instanceof NormalCard) {
            if(this.componentPlayer.isAngry() && this.componentPlayer.getPerson().getCharacter() == Character.RESOLUTE
                    && this.player.isAngry() && this.player.getPerson().getCharacter() == Character.RESOLUTE) {
            }
            else if(this.componentPlayer.isAngry() && this.componentPlayer.getPerson().getCharacter() == Character.RESOLUTE) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            }
            else if(this.player.isAngry() && this.player.getPerson().getCharacter() == Character.RESOLUTE) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            }
            else if(((NormalCard) componentPlayerMove).getType() == ((NormalCard) playerMove).getType()) {
                if(((NormalCard) componentPlayerMove).getPower().ordinal() < ((NormalCard) playerMove).getPower().ordinal()) {
                    round.setWin(true);
                    round.setEffect(Effect.PLAYER);
                }
                else if(((NormalCard) componentPlayerMove).getPower().ordinal() > ((NormalCard) playerMove).getPower().ordinal()) {
                    round.setWin(false);
                    round.setEffect(Effect.COMPONENT_PLAYER);
                }
                else {
                }
            }
            else if(((NormalCard) componentPlayerMove).getType() == round.getRoundType()) {
                round.setWin(false);
                round.setEffect(Effect.COMPONENT_PLAYER);
            }
            else if(((NormalCard) playerMove).getType() == round.getRoundType()) {
                round.setWin(true);
                round.setEffect(Effect.PLAYER);
            }
            else {
                if(((NormalCard) componentPlayerMove).getPower().ordinal() < ((NormalCard) playerMove).getPower().ordinal()) {
                    round.setWin(true);
                    round.setEffect(Effect.PLAYER);
                }
                else if(((NormalCard) componentPlayerMove).getPower().ordinal() > ((NormalCard) playerMove).getPower().ordinal()) {
                    round.setWin(false);
                    round.setEffect(Effect.COMPONENT_PLAYER);
                }
                else {
                }
            }
        } else {
            ConsoleUtils.printErrorAndExit("roundJudge invalid game state");
        }
        if(componentPlayerMove instanceof SpecialCard || playerMove instanceof SpecialCard) {
            lastRoundTypeChangedRound = round.getIndex();
            DebugUtils.debugPrintln("lastRoundTypeChangedRound=" + lastRoundTypeChangedRound);
            if(roundList.size() == 1) {
                firstRoundChangeRoundType = true;
            }
        }
        DebugUtils.debugPrintln("roundJudge win="+round.isWin()+",effect="+round.getEffect());
    }

    public void roundTakeEffects() {
        Round round = roundList.get(roundList.size() - 1);
        int roundIndex = round.getIndex();
        switch (round.getEffect()) {
            case BOTH_FIRST:
                playerEffect(round, roundIndex);
                componentPlayerEffect(round, roundIndex);
                break;
            case BOTH_SECOND:
                componentPlayerEffect(round, roundIndex);
                playerEffect(round, roundIndex);
                break;
            case NONE:
                ConsoleUtils.println("平局，不产生效果");
                automaticEndAngryStatus(player, componentPlayer, roundIndex);
                automaticEndAngryStatus(componentPlayer, player, roundIndex);
                break;
            case COMPONENT_PLAYER:
                componentPlayerEffect(round, roundIndex);
                break;
            case PLAYER:
                playerEffect(round, roundIndex);
                break;
        }
        printPlayerStatus();
        if(this.player.getLife() == 0) {
            setWin(false);
            setGameOver(true);
        } else if(this.componentPlayer.getLife() == 0) {
            setWin(true);
            setGameOver(true);
        }
    }

    private void componentPlayerEffect(Round round, int roundIndex) {
        AngryStatusChangeRoundWin changeRoundWin = cardToPlayerEffect(round.getComponentPlayerMove(), this.componentPlayer, this.player, round, roundIndex, true);
        //v1.3修改：player进入愤怒状态可能影响回合的胜负
        if(changeRoundWin == AngryStatusChangeRoundWin.PLAYER) {
            round.setWin(false);
        } else if(changeRoundWin == AngryStatusChangeRoundWin.COMPONENT_PLAYER) {
            round.setWin(true);
        }
    }

    private void playerEffect(Round round, int roundIndex) {
        AngryStatusChangeRoundWin changeRoundWin = cardToPlayerEffect(round.getPlayerMove(), this.player, this.componentPlayer, round, roundIndex, true);
        //v1.3修改：player进入愤怒状态可能影响回合的胜负
        if(changeRoundWin == AngryStatusChangeRoundWin.PLAYER) {
            round.setWin(true);
        } else if(changeRoundWin == AngryStatusChangeRoundWin.COMPONENT_PLAYER) {
            round.setWin(false);
        }
    }

    /**
     * 卡牌的效果对玩家发动
     * @param card 卡牌
     * @param player 拥有卡牌的玩家
     * @param componentPlayer 拥有卡牌的对手玩家
     * @param roundIndex 回合索引
     * @param firstLevelCall 是否外层调用，避免递归调用
     * @return 愤怒状态改变回合胜负的类型，参见枚举类定义
     */
    private AngryStatusChangeRoundWin cardToPlayerEffect(Card card, Player player, Player componentPlayer, Round round, int roundIndex, boolean firstLevelCall) {
        boolean componentPlayerChangeRoundWin = false, playerChangeRoundWin = false;
        if(card == SpecialCard.ROAR) {
            componentPlayer.changeLife(((SpecialCard)card).getLifeHit());
            componentPlayer.changeAnger(((SpecialCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                componentPlayerChangeRoundWin = angryStatusTakeEffects(componentPlayer, player, round, roundIndex);
                playerChangeRoundWin = angryStatusTakeEffects(player, componentPlayer, round, roundIndex);
            }
        } else if(card == SpecialCard.ANGRY) {
            player.changeAnger(((SpecialCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                playerChangeRoundWin = angryStatusTakeEffects(player, componentPlayer, round, roundIndex);
                componentPlayerChangeRoundWin = angryStatusTakeEffects(componentPlayer, player, round, roundIndex);
            }
        } else if(card instanceof SpecialCard) {
            componentPlayer.changeAnger(((SpecialCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                componentPlayerChangeRoundWin = angryStatusTakeEffects(componentPlayer, player, round, roundIndex);
                playerChangeRoundWin = angryStatusTakeEffects(player, componentPlayer, round, roundIndex);
            }
        } else {
            componentPlayer.changeLife(((NormalCard)card).getLifeHit());
            componentPlayer.changeAnger(((NormalCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                componentPlayerChangeRoundWin = angryStatusTakeEffects(componentPlayer, player, round, roundIndex);
                playerChangeRoundWin = angryStatusTakeEffects(player, componentPlayer, round, roundIndex);
            }
        }
        if(firstLevelCall) {
            if(playerChangeRoundWin && componentPlayerChangeRoundWin) {
                return AngryStatusChangeRoundWin.NO_OP;
            } else if(playerChangeRoundWin) {
                return AngryStatusChangeRoundWin.PLAYER;
            } else if(componentPlayerChangeRoundWin) {
                return AngryStatusChangeRoundWin.COMPONENT_PLAYER;
            } else {
                return AngryStatusChangeRoundWin.NO_OP;
            }
        } else {
            return AngryStatusChangeRoundWin.NO_OP;
        }
    }

    /**
     * 处理当player玩家处于愤怒状态时的后续操作
     * @param player
     * @param componentPlayer
     * @param roundIndex
     * @return player进入愤怒状态是否足以改变回合的胜负
     */
    private boolean angryStatusTakeEffects(Player player, Player componentPlayer, Round round, int roundIndex) {
        if(player.getLife() != 0 && player.isAngry()) {
            switch (player.getPerson().getCharacter()) {
                case TIMID:
                    boolean changeRoundWin = true;
                    int cardIndex = player.getCardSize();
                    if(cardIndex == 0) {
                        break;
                    }
                    //将冷静牌统一放在最后出
                    moveCalmCardsToRight(player);
                    int index = findTimidAngryMakeComponentAngryCardIndex(player, componentPlayer);
                    boolean componentChangeRoundWin = index != Card.INDEX_NOT_FOUND;
                    if(componentChangeRoundWin) {
                        //1.最后一张牌，则看对方性格
                        if(index == player.getCardSize()) {
                            round.setForceSetWin(true);
                            round.setWin(false);
                        }
                        //2.不是最后一张牌，则回合判自己胜
                        else {
                        }
                    }
                    else {
                    }
                    while(cardIndex-- > 0) {
                        Card card = player.removeCard(cardIndex + 1);
                        ConsoleUtils.println(player.getPlayerName() + "出牌:" + card.getDesc());
                        cardToPlayerEffect(card, player, componentPlayer, round, roundIndex, false);
                    }
                    initializeCards(player);
                    player.removeCard(player.getCardSize());
                    player.changeAnger(-100, roundIndex);
                    return changeRoundWin;
                case CALM:
                    automaticEndAngryStatus(player, componentPlayer, roundIndex);
                    break;
                case RASH:
                    componentPlayer.changeLife(RASH_ANGRY_LIFE_HIT);
                    player.changeAnger(-100, roundIndex);
                    return true;
                case RESOLUTE:
                    automaticEndAngryStatus(player, componentPlayer, roundIndex);
                    break;
            }
        }
        return false;
    }

    private int findTimidAngryMakeComponentAngryCardIndex(Player player, Player componentPlayer) {
        if(!componentPlayer.isAngry() && (componentPlayer.getPerson().getCharacter() == Character.TIMID
                || componentPlayer.getPerson().getCharacter() == Character.RASH)) {
            int angerHit = 0;
            for(int i=1;i<=player.getCardSize();i++) {
                Card card = player.getCard(i);
                if(card instanceof NormalCard) {
                    angerHit += ((NormalCard) card).getAngerHit();
                } else if(card instanceof SpecialCard){
                    angerHit += ((SpecialCard)card).getAngerHit();
                } else {
                    ConsoleUtils.printErrorAndExit("findTimidAngryMakeComponentAngryCardIndex invalid state");
                }
                if(angerHit + componentPlayer.getAnger() > 100) {
                    return i;
                }
            }
        }
        return Card.INDEX_NOT_FOUND;
    }

    //todo 当对手的生命值为0时不结束愤怒状态
    private void automaticEndAngryStatus(Player player, Player componentPlayer, int roundIndex) {
        //生命值为0时怒气值不可变动
        if(player.getLife() == 0 || componentPlayer.getLife() == 0) {
            return;
        }
        if(player.isAngry() && roundIndex - player.getLastAngryRound() >= ANGRY_KEEP_ROUNDS) {
            player.changeAnger(-100, roundIndex);
        }
    }


    private void moveCalmCardsToRight(Player player) {
        player.getCardList().sort((c1,c2)->{
            if(c1 == c2) {
                return 0;
            } else if(c1 == SpecialCard.CALM) {
                return 1;
            } else if(c2 == SpecialCard.CALM) {
                return -1;
            } else {
                return 0;
            }
        });
    }

    public static void main(String[] args) {
        List<Card> cards = new LinkedList<>();
        cards.add(SpecialCard.CALM);
        cards.add(NormalCard.STORY_SMALL);
        cards.add(NormalCard.SEASON_BIG);
        cards.add(SpecialCard.ROAR);
        cards.add(SpecialCard.CALM);
        cards.sort((c1,c2)->{
            if(c1 == c2) {
                return 0;
            } else if(c1 == SpecialCard.CALM) {
                return 1;
            } else if(c2 == SpecialCard.CALM) {
                return -1;
            } else {
                return 0;
            }
        });
        System.out.println(cards);
    }

    /**
     * 愤怒状态足以改变回合胜负的枚举类
     */
    private enum AngryStatusChangeRoundWin {
        /**
         * 不影响
         */
        NO_OP,
        /**
         * player回合胜
         */
        PLAYER,
        /**
         * componentPlayer回合胜
         */
        COMPONENT_PLAYER
    }

}
