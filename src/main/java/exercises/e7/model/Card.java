package exercises.e7.model;


import org.apache.commons.lang3.StringUtils;

public sealed abstract class Card implements Comparable<Card> permits Ace, King, Queen, Jack, Ten, Regular, Joker {

    private final String symbol;

    public Card(String symbol) {
        this.symbol = symbol;
    }

    public abstract int value();

    @Override
    public int compareTo(Card other) {
        return Integer.compare(this.value(), other.value());
    }

    public static Card fromSymbol(String symbol) {
        return switch (symbol) {
            case "A" -> new Ace();
            case "K" -> new King();
            case "Q" -> new Queen();
            case "J" -> new Jack();
            case "T" -> new Ten();
            case String s when StringUtils.isNumeric(s) -> new Regular(Integer.parseInt(s));
            default -> throw new IllegalStateException("Illegal symbol: " + symbol);
        };
    }

    public static Card fromSymbolWithJoker(String symbol) {
        return switch (symbol) {
            case "A" -> new Ace();
            case "K" -> new King();
            case "Q" -> new Queen();
            case "J" -> new Joker();
            case "T" -> new Ten();
            case String s when StringUtils.isNumeric(s) -> new Regular(Integer.parseInt(s));
            default -> throw new IllegalStateException("Illegal symbol: " + symbol);
        };
    }

    public String getSymbol() {
        return symbol;
    }

    @Override
    public String toString() {
        return symbol;
    }
}


final class Ace extends Card {
    public Ace() {
        super("A");
    }

    @Override
    public int value() {
        return 14;
    }
}

final class King extends Card {
    public King() {
        super("K");
    }

    @Override
    public int value() {
        return 13;
    }
}

final class Queen extends Card {
    public Queen() {
        super("Q");
    }

    @Override
    public int value() {
        return 12;
    }
}

final class Jack extends Card {
    public Jack() {
        super("J");
    }

    @Override
    public int value() {
        return 11;
    }
}

final class Joker extends Card {
    public Joker() {
        super("J");
    }

    @Override
    public int value() {
        return 1;
    }
}

final class Ten extends Card {
    public Ten() {
        super("T");
    }

    @Override
    public int value() {
        return 10;
    }
}

final class Regular extends Card {
    private final int value;

    public Regular(int value) {
        super(Integer.toString(value));
        if (value >= 10 || value < 2) {
            throw new IllegalArgumentException("Illegal regular card value: " + value);
        }
        this.value = value;
    }

    @Override
    public int value() {
        return this.value;
    }
}