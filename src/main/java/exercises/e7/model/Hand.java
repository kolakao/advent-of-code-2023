package exercises.e7.model;

import com.google.common.collect.Streams;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

sealed public abstract class Hand implements Comparable<Hand> permits FiveOfAKind, FourOfAKind, FullHouse, ThreeOfAKind, TwoPair, OnePair, HighCard {
    private final List<Card> cards;
    private final int bid;
    protected int value;

    protected Hand(List<Card> cards, int bid) {
        this.cards = cards;
        this.bid = bid;
    }

    public static Hand ofCards(String cards, int bid) {
        var cardsList = Arrays
                .stream(cards.split("(?!^)"))
                .map(Card::fromSymbol)
                .toList();

        var cardsGrouped = cardsList.stream().collect(Collectors.groupingBy(Card::getSymbol, Collectors.counting()));
        return switch (cardsGrouped.size()) {
            case 1 -> new FiveOfAKind(cardsList, bid);
            case 2 -> {
                if (cardsGrouped.values().stream().max(Long::compareTo).orElseThrow() == 3) {
                    yield new FullHouse(cardsList, bid);
                } else {
                    yield new FourOfAKind(cardsList, bid);
                }
            }
            case 3 -> {
                if (cardsGrouped.values().stream().max(Long::compareTo).orElseThrow() == 3) {
                    yield new ThreeOfAKind(cardsList, bid);
                } else {
                    yield new TwoPair(cardsList, bid);
                }
            }
            case 4 -> new OnePair(cardsList, bid);
            case 5 -> new HighCard(cardsList, bid);
            default -> throw new IllegalStateException("REEEE");
        };
    }

    public static Hand ofCardsWithJoker(String cards, int bid) {
        var symbolsList = Arrays
                .stream(cards.split("(?!^)"))
                .toList();

        var cardsGrouped = symbolsList.stream().collect(Collectors.groupingBy(Function.identity(), Collectors.counting()));
        var numberOfJokers = cardsGrouped.entrySet().stream().filter(e -> e.getKey().equals("J")).map(Map.Entry::getValue).findFirst().orElse(0L);
        var maxNumberOfCardsOfOneKind = cardsGrouped
                .entrySet()
                .stream()
                .filter(e -> !e.getKey().equals("J"))
                .map(Map.Entry::getValue)
                .max(Long::compareTo)
                .orElse(0L).intValue() + numberOfJokers.intValue();
        var numberOfPairsWithoutJoker = cardsGrouped.entrySet().stream().filter(e -> e.getValue() == 2).count();
        var cardsList = symbolsList.stream().map(Card::fromSymbolWithJoker).toList();
        return switch (maxNumberOfCardsOfOneKind) {
            case 1 -> new HighCard(cardsList, bid);
            case 2 -> {
                if (numberOfPairsWithoutJoker <= 1) {
                    yield new OnePair(cardsList, bid);
                } else {
                    yield new TwoPair(cardsList, bid);
                }
            }
            case 3 -> {
                if (numberOfPairsWithoutJoker == 2 || (numberOfJokers == 0 && numberOfPairsWithoutJoker == 1)) {
                    yield new FullHouse(cardsList, bid);
                } else {
                    yield new ThreeOfAKind(cardsList, bid);
                }
            }
            case 4 -> new FourOfAKind(cardsList, bid);
            case 5 -> new FiveOfAKind(cardsList, bid);
            default -> throw new IllegalStateException("REEE");
        };
    }

    public int getBid() {
        return bid;
    }

    @Override
    public String toString() {
        return this.getClass().getSimpleName() + " cards=" + cards + ", bid=" + bid + '}';
    }


    @Override
    public int compareTo(Hand other) {
        if (this.value > other.value) {
            return 1;
        } else if (this.value < other.value) {
            return -1;
        } else {
            return compareCardByCard(other);
        }
    }

    private int compareCardByCard(Hand other) {
        return Streams.zip(this.cards.stream(), other.cards.stream(), Card::compareTo).filter(i -> i != 0).findFirst().orElseThrow();
    }

}

final class FiveOfAKind extends Hand {
    public FiveOfAKind(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 7;
    }
}

final class FourOfAKind extends Hand {
    public FourOfAKind(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 6;
    }
}

final class FullHouse extends Hand {
    public FullHouse(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 5;
    }
}

final class ThreeOfAKind extends Hand {
    public ThreeOfAKind(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 4;
    }
}

final class TwoPair extends Hand {
    public TwoPair(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 3;
    }
}

final class OnePair extends Hand {
    public OnePair(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 2;
    }
}

final class HighCard extends Hand {
    public HighCard(List<Card> cards, int bid) {
        super(cards, bid);
        this.value = 1;
    }
}