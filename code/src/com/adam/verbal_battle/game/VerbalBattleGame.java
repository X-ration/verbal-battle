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
    public static final int GAME_MODE_ARTIFICIAL_COMPUTER = 1;
    public static final int GAME_MODE_ARTIFICIAL_ARTIFICIAL = 2;
    public static final int GAME_MODE_COMPUTER_COMPUTER = 3;

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
            //todo 发牌根据智力调整获得大牌的概率
            card = NormalCard.values()[random.nextInt(NormalCard.values().length)];
        }
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
     * todo 电脑出牌策略
     * @param player
     * @return
     */
    public Card computerMove(Player player) {
        if(player.isAngry() && player.getPerson().getCharacter() == Character.CALM) {
            ConsoleUtils.println(player.getPlayerName() + "使用了熟虑");
            initializeCards(player);
            ConsoleUtils.println(player.getPlayerName() + "卡片:" + player.formatCards());
        }
        Round round = roundList.get(roundList.size() - 1);
        Random random = new Random();
        int cardIndex = random.nextInt(player.getCardSize()) + 1;
        Card card = player.removeCard(cardIndex);
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
            componentMove = playerMove(this.componentPlayer);
            ConsoleUtils.println(this.player.getPlayerName() + "出牌：" + playerMove.getDesc());
            ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
        } else {
            Round lastRound = roundList.get(roundList.size() - 2);
            if(lastRound.isWin()) {
                playerMove = playerMove(this.player);
                componentMove = playerMove(this.componentPlayer);
                ConsoleUtils.println(this.player.getPlayerName() + "出牌：" + playerMove.getDesc());
                ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
            } else {
                componentMove = playerMove(this.componentPlayer);
                if(gameMode == GAME_MODE_ARTIFICIAL_COMPUTER) {
                    ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + (componentMove instanceof SpecialCard ? "特殊卡片" : "普通卡片"));
                }
                playerMove = playerMove(this.player);
                ConsoleUtils.println(this.player.getPlayerName() + "出牌：" + playerMove.getDesc());
                ConsoleUtils.println(this.componentPlayer.getPlayerName() + "出牌：" + componentMove.getDesc());
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
                ConsoleUtils.printErrorAndExit("Invalid game state");
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
                round.setEffect(firstMove ? Effect.BOTH_FIRST: Effect.BOTH_SECOND);
            } else {
                ConsoleUtils.printErrorAndExit("Invalid game state");
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
                round.setEffect(firstMove ? Effect.BOTH_FIRST: Effect.BOTH_SECOND);
            } else {
                ConsoleUtils.printErrorAndExit("Invalid game state");
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
            ConsoleUtils.printErrorAndExit("Invalid game state");
        }
        if(componentPlayerMove instanceof SpecialCard || playerMove instanceof SpecialCard) {
            lastRoundTypeChangedRound = round.getIndex();
            DebugUtils.debugPrintln("lastRoundTypeChangedRound=" + lastRoundTypeChangedRound);
            if(roundList.size() == 1) {
                firstRoundChangeRoundType = true;
            }
        }
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
                automaticEndAngryStatus(player, roundIndex);
                automaticEndAngryStatus(componentPlayer, roundIndex);
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
        cardToPlayerEffect(round.getComponentPlayerMove(), this.componentPlayer, this.player, roundIndex, true);
    }

    private void playerEffect(Round round, int roundIndex) {
        cardToPlayerEffect(round.getPlayerMove(), this.player, this.componentPlayer, roundIndex, true);
    }

    /**
     * 卡牌的效果对玩家发动
     * @param card 卡牌
     * @param player 拥有卡牌的玩家
     * @param componentPlayer 拥有卡牌的对手玩家
     * @param roundIndex 回合索引
     * @param firstLevelCall 是否外层调用，避免递归调用
     */
    private void cardToPlayerEffect(Card card, Player player, Player componentPlayer, int roundIndex, boolean firstLevelCall) {
        if(card == SpecialCard.ROAR) {
            componentPlayer.changeLife(((SpecialCard)card).getLifeHit());
            componentPlayer.changeAnger(((SpecialCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                proceedAngryStatus(componentPlayer, player, roundIndex);
                proceedAngryStatus(player, componentPlayer, roundIndex);
            }
        } else if(card == SpecialCard.ANGRY) {
            player.changeAnger(((SpecialCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                proceedAngryStatus(player, componentPlayer, roundIndex);
                proceedAngryStatus(componentPlayer, player, roundIndex);
            }
        } else if(card instanceof SpecialCard) {
            componentPlayer.changeAnger(((SpecialCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                proceedAngryStatus(componentPlayer, player, roundIndex);
                proceedAngryStatus(player, componentPlayer, roundIndex);
            }
        } else {
            componentPlayer.changeLife(((NormalCard)card).getLifeHit());
            componentPlayer.changeAnger(((NormalCard)card).getAngerHit(), roundIndex);
            if(firstLevelCall) {
                proceedAngryStatus(componentPlayer, player, roundIndex);
                proceedAngryStatus(player, componentPlayer, roundIndex);
            }
        }
    }

    /**
     * 处理当player玩家处于愤怒状态时的后续操作
     * @param player
     * @param componentPlayer
     * @param roundIndex
     */
    private void proceedAngryStatus(Player player, Player componentPlayer, int roundIndex) {
        if(player.getLife() != 0 && player.isAngry()) {
            switch (player.getPerson().getCharacter()) {
                case TIMID:
                    int cardIndex = player.getCardSize();
                    while(cardIndex-- > 0) {
                        Card card = player.removeCard(cardIndex + 1);
                        ConsoleUtils.println(player.getPlayerName() + "出牌:" + card.getDesc());
                        cardToPlayerEffect(card, player, componentPlayer, roundIndex, false);
                    }
                    initializeCards(player);
                    player.removeCard(player.getCardSize());
                    player.changeAnger(-100, roundIndex);
                    break;
                case CALM:
                    automaticEndAngryStatus(player, roundIndex);
                    break;
                case RASH:
                    componentPlayer.changeLife(RASH_ANGRY_LIFE_HIT);
                    player.changeAnger(-100, roundIndex);
                    break;
                case RESOLUTE:
                    automaticEndAngryStatus(player, roundIndex);
                    break;
            }
        }
    }

    private void automaticEndAngryStatus(Player player, int roundIndex) {
        if(player.isAngry() && roundIndex - player.getLastAngryRound() >= ANGRY_KEEP_ROUNDS) {
            player.changeAnger(-100, roundIndex);
        }
    }

}
