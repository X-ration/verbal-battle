package com.adam.verbal_battle.player;

import com.adam.verbal_battle.Assert;
import com.adam.verbal_battle.ConsoleUtils;
import com.adam.verbal_battle.game.*;
import com.adam.verbal_battle.person.Character;

import java.util.*;

public class SuperComputerPlayer extends ComputerPlayer{



    public SuperComputerPlayer(String playerName) {
        super(playerName);
    }

    /**
     * 超级电脑出牌策略：根据对手的状态找到可能压制对手的牌
     * 只能看到对手的性格、状态，对手的卡牌只能看出是普通卡牌还是特殊卡牌
     * @param componentPlayer 对手
     * @return 出牌
     */
    @Override
    public Card chooseCard(Player componentPlayer, Round round) {
        Card card = chooseSuppressingCard(componentPlayer, round, false);
        //处于愤怒状态的冷静性格电脑可以多次使用熟虑
        if(card == null && this.isAngry() && this.getPerson().getCharacter() == Character.CALM) {
            int reinitializeCount = 0;
            do {
                VerbalBattleGame.getINSTANCE().reinitializeCards(this);
                card = chooseSuppressingCard(componentPlayer, round, false);
                reinitializeCount++;
            } while(card == null && reinitializeCount <= VerbalBattleGame.SUPERCOMPUTER_REINITIALIZE_LIMIT);
        }
        if(card == null) {
            card = chooseSuppressingCard(componentPlayer, round, true);
        }
        Assert.assertTrue(card != null, "SuperComputerPlayer chooseCard card null");
        return card;
    }

    /**
     * 选择压制对手的卡牌
     * @param componentPlayer 对手对象
     * @param round 回合对象
     * @param isFinal 是否要求必须出一张牌
     * @return 决定要出的卡牌，当isFinal=true时不为null，isFinal=false时可能为null
     */
    private Card chooseSuppressingCard(Player componentPlayer, Round round, boolean isFinal) {
        Card card = null;
        Random random = new Random();
        boolean playerHasSpecialCard = checkHasSpecialCard(this)
                , componentPlayerHasSpecialCard = checkHasSpecialCard(componentPlayer);
        //1.对手先手状态下
        if(isComponentFirstMove(componentPlayer, round)) {
            boolean componentFirstMoveSpecial = isComponentFirstMoveSpecial(componentPlayer, round);
            //1.1对手出特殊牌，自己也有特殊牌
            if(componentFirstMoveSpecial && playerHasSpecialCard) {
                card = findSuppressingCardBothSpecial(componentPlayer, round, random, isFinal);
            }
            //1.2对手出特殊牌，自己没有特殊牌
            else if(componentFirstMoveSpecial) {
                card = findSuppressingCardComponentSpecialOnly(componentPlayer, round, random, isFinal);
            }
            //1.3对手出普通牌，自己有特殊牌
            else if(playerHasSpecialCard) {
                card = findSuppressingCardSelfSpecialOnly(componentPlayer, round, random, isFinal);
            }
            //1.4对手出普通牌，自己没有普通牌
            else {
                card = findSuppressingCardBothNormal(componentPlayer, round, random, isFinal);
            }
        }
        //2.对手后手状态下
        else {
            //2.1.双方都有特殊牌
            if (playerHasSpecialCard && componentPlayerHasSpecialCard) {
                card = findSuppressingCardBothSpecial(componentPlayer, round, random, isFinal);
            }
            //2.2.自己有特殊牌，对手没有特殊牌
            else if (playerHasSpecialCard) {
                card = findSuppressingCardSelfSpecialOnly(componentPlayer, round, random, isFinal);
            }
            //2.3.对手有特殊牌，自己没有特殊牌
            else if (componentPlayerHasSpecialCard) {
                card = findSuppressingCardComponentSpecialOnly(componentPlayer, round, random, isFinal);
            }
            //2.4.双方都没有特殊牌
            else {
                card = findSuppressingCardBothNormal(componentPlayer, round, random, isFinal);
            }
        }
        return card;
    }

    private Card findSuppressingCardBothSpecial(Player componentPlayer, Round round, Random random, boolean isFinal) {
        //同时有特殊牌按照概率决定是否压制
        double chance = random.nextDouble();
        if(chance <= VerbalBattleGame.SUPERCOMPUTER_BOTH_SPECIAL_SUPPRESS_CHANCE) {
            return chooseSpecialCard(componentPlayer, round, random, isFinal);
        }
        if(isFinal) {
            return chooseSpecialCard(componentPlayer, round, random, isFinal);
        } else {
            return null;
        }
    }


    private Card findSuppressingCardSelfSpecialOnly(Player componentPlayer, Round round, Random random, boolean isFinal) {
        //自己有特殊牌则优先出特殊牌
        return chooseSpecialCard(componentPlayer, round, random, isFinal);
    }

    private Card findSuppressingCardComponentSpecialOnly(Player componentPlayer, Round round, Random random, boolean isFinal) {
        double chance = random.nextDouble();
        boolean hasNormalCard = checkHasNormalCard(this);
        Assert.assertTrue(hasNormalCard, "findSuppressingCardComponentSpecialOnly invalid state");
        //对手有特殊牌，按照概率决定压制（对手大喝、无视为被压制，愤怒、冷静为压制）
        if(hasNormalCard && chance <= VerbalBattleGame.SUPERCOMPUTER_COMPONENT_SPECIAL_SUPPRESS_CHANCE) {
            return chooseBiggestNormalCard(round);
        }
        //在被压制的情况下，按照概率出最小牌或是等待熟虑
        else {
            chance = random.nextDouble();
            if(hasNormalCard && chance <= VerbalBattleGame.SUPERCOMPUTER_COMPONENT_SPECIAL_SUPPRESSED_CHANCE) {
                return chooseSmallestNormalCard(round);
            }
        }
        if(isFinal) {
            //出最小牌保存实力
            return chooseSmallestNormalCard(round);
        }
        return null;
    }

    private Card findSuppressingCardBothNormal(Player componentPlayer, Round round, Random random, boolean isFinal) {
        NormalCard card = findBiggestNormalCard(round);
        //同时有普通牌，如果手牌最大牌环境与回合环境一致，且力量为大，则出最大牌
        if(card.getType() == round.getRoundType() && card.getPower() == CardPower.BIG) {
            removeCard(card);
            return card;
        }
        //手牌最大牌与环境一致，且力量为中
        else if(card.getType() == round.getRoundType() && card.getPower() == CardPower.MEDIUM) {
            double chance = random.nextDouble();
            if(chance <= VerbalBattleGame.SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSING_MEDIUM_CHANCE) {
                removeCard(card);
                return card;
            }
        }
        //手牌最大牌与环境一致，且力量为小
        else if(card.getType() == round.getRoundType() && card.getPower() == CardPower.SMALL) {
            double chance = random.nextDouble();
            if(chance <= VerbalBattleGame.SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSING_SMALL_CHANCE) {
                removeCard(card);
                return card;
            }
        }
        //否则根据概率决定是否压制（对手普通牌环境与回合环境一致为压制）
        double chance = random.nextDouble();
        boolean resoluteSuppressing = componentPlayer.isAngry() && componentPlayer.getPerson().getCharacter() == Character.RESOLUTE;
        //被压制情况下根据概率决定出最小牌
        if(resoluteSuppressing || chance <= VerbalBattleGame.SUPERCOMPUTER_BOTH_NORMAL_SUPPRESS_CHANCE) {
            chance = random.nextDouble();
            if(chance <= VerbalBattleGame.SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSED_SMALLEST_CHANCE) {
                return chooseSmallestNormalCard(round);
            }
        }
        //不被压制情况下根据概率决定出最大牌
        else {
            chance = random.nextDouble();
            if(chance <= VerbalBattleGame.SUPERCOMPUTER_BOTH_NORMAL_SUPPRESSED_BIGGEST_CHANCE) {
                removeCard(card);
                return card;
            }
        }
        if(isFinal) {
            //对手性格刚毅，在愤怒状态下是被压制，出最小牌减少损失
            if(resoluteSuppressing) {
                return chooseSmallestNormalCard(round);
            }
            //出最大牌与对手争夺回合
            else {
                removeCard(card);
                return card;
            }
        }
        return null;
    }

    private boolean checkHasSpecialCard(Player player) {
        for(Card card: player.cardList) {
            if(card instanceof SpecialCard) {
                return true;
            }
        }
        return false;
    }

    private boolean checkHasNormalCard(Player player) {
        for(Card card: player.cardList) {
            if(card instanceof NormalCard) {
                return true;
            }
        }
        return false;
    }

    /**
     * 查找指定卡牌在手牌中的索引
     * @param card
     * @return 索引值，-1表示没有找到
     */
    private int findCard(Card card) {
        for(int i=0;i<this.getCardSize();i++) {
            Card card1 = this.cardList.get(i);
            if(card1 == card) {
                return i+1;
            }
        }
        return Card.INDEX_NOT_FOUND;
    }

    /**
     * 检查对手在当前回合先出了牌
     * @param componentPlayer
     * @param round
     * @return
     */
    private boolean isComponentFirstMove(Player componentPlayer, Round round) {
        Card card = getComponentFirstMove(componentPlayer, round);
        return card != null;
    }

    /**
     * 对手先手状态下获取对手出的卡牌，此方法不适合直接调用
     * @param componentPlayer
     * @param round
     * @return
     */
    private Card getComponentFirstMove(Player componentPlayer, Round round) {
        return componentPlayer == VerbalBattleGame.getINSTANCE().getPlayer() ? round.getPlayerMove() : round.getComponentPlayerMove();
    }

    /**
     * 对手先手状态下检查对手出的卡牌是否是特殊牌，<b>调用此方法需要确保对手是先手状态</b>
     * @param componentPlayer
     * @param round
     * @return
     */
    private boolean isComponentFirstMoveSpecial(Player componentPlayer, Round round) {
        Card card = getComponentFirstMove(componentPlayer, round);
        if(card == null) {
            ConsoleUtils.printErrorAndExit("componentFirstMoveIsSpecial must be called after componentFirstMove");
            return false;
        }
        return card instanceof SpecialCard;
    }

    private Card chooseSpecialCard(Player componentPlayer, Round round, Random random, boolean isFinal) {
        int roarCardIndex = Card.INDEX_NOT_FOUND, ignoreCardIndex = Card.INDEX_NOT_FOUND,
                angryCardIndex = Card.INDEX_NOT_FOUND, calmCardIndex = Card.INDEX_NOT_FOUND;
        //手中有大喝，直接出大喝
        if((roarCardIndex = findCard(SpecialCard.ROAR)) != Card.INDEX_NOT_FOUND) {
            return removeCard(roarCardIndex);
        }
        //手中有无视，如果不足以使对手进入愤怒状态，则出无视
        if((ignoreCardIndex = findCard(SpecialCard.IGNORE)) != Card.INDEX_NOT_FOUND) {
            if(componentPlayer.getAnger() + SpecialCard.IGNORE.getAngerHit() < 100) {
                return removeCard(ignoreCardIndex);
            }
        }
        //手中有愤怒，如果此时不在愤怒状态，则出愤怒
        if((angryCardIndex = findCard(SpecialCard.ANGRY)) != Card.INDEX_NOT_FOUND) {
            if(!isAngry()) {
                return removeCard(angryCardIndex);
            }
        }
        //手中有冷静，如果对手有怒气值，则出冷静
        if((calmCardIndex = findCard(SpecialCard.CALM)) != Card.INDEX_NOT_FOUND) {
            if(componentPlayer.getAnger() > 0) {
                return removeCard(calmCardIndex);
            }
        }
        if(isFinal) {
            if(checkHasNormalCard(this)) {
                return chooseBiggestNormalCard(round);
            }
            else {
                if(angryCardIndex != Card.INDEX_NOT_FOUND) {
                    return removeCard(angryCardIndex);
                }
                else if(calmCardIndex != Card.INDEX_NOT_FOUND) {
                    return removeCard(calmCardIndex);
                }
                else if(ignoreCardIndex != Card.INDEX_NOT_FOUND) {
                    return removeCard(ignoreCardIndex);
                }
                else {
                    ConsoleUtils.printErrorAndExit("chooseSpecialCard invalid state");
                    return null;
                }
            }
        } else {
            return null;
        }
    }

    /**
     * 出最大的普通牌，调用此方法要确保手牌有普通牌
     * @param round
     * @return
     */
    private Card chooseBiggestNormalCard(Round round) {
        Card card = findBiggestNormalCard(round);
        Assert.assertTrue(card != null, "chooseBiggestNormalCard card null");
        removeCard(card);
        return card;
    }

    /**
     * 出最小的普通牌，调用此方法要确保手牌有普通牌
     * @param round
     * @return
     */
    private Card chooseSmallestNormalCard(Round round) {
        Card card = findSmallestNormalCard(round);
        Assert.assertTrue(card != null, "chooseSmallestNormalCard card null");
        removeCard(card);
        return card;
    }

    private NormalCard findBiggestNormalCard(Round round) {
        List<NormalCard> cardList = sortNormalCards(round);
        return cardList.get(cardList.size() - 1);
    }

    private NormalCard findSmallestNormalCard(Round round) {
        List<NormalCard> cardList = sortNormalCards(round);
        return cardList.get(0);
    }

    private List<NormalCard> sortNormalCards(Round round) {
        List<NormalCard> cardList = new LinkedList<>();
        for(Card card: this.cardList) {
            if(card instanceof NormalCard) {
                cardList.add((NormalCard) card);
            }
        }
        cardList.sort((o1, o2) -> {
            if (o1.getType() == o2.getType()) {
                return o1.getPower().compareTo(o2.getPower());
            } else if (o1.getType() == round.getRoundType()) {
                return 1;
            } else if (o2.getType() == round.getRoundType()) {
                return -1;
            } else {
                return o1.getPower().compareTo(o2.getPower());
            }
        });
        return cardList;
    }

    public static void main(String[] args) {
        List<NormalCard> cards = new LinkedList<>();
        cards.add(NormalCard.PRINCIPLE_BIG);
        cards.add(NormalCard.PRINCIPLE_MEDIUM);
        cards.add(NormalCard.PRINCIPLE_SMALL);
        cards.add(NormalCard.STORY_MEDIUM);
        cards.add(NormalCard.SEASON_BIG);
        cards.add(NormalCard.SEASON_SMALL);
        cards.add(NormalCard.STORY_SMALL);
        Round round = new Round(0);
        round.setRoundType(CardType.PRINCIPLE);
        cards.sort((o1, o2) -> {
            if (o1.getType() == o2.getType()) {
                return o1.getPower().compareTo(o2.getPower());
            } else if (o1.getType() == round.getRoundType()) {
                return 1;
            } else if (o2.getType() == round.getRoundType()) {
                return -1;
            } else {
                return o1.getPower().compareTo(o2.getPower());
            }
        });
        System.out.println(cards);
    }
}
