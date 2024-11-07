import utils.FileUtils;

void main() {
    var input = FileUtils.readInput("Input4");
    fourthOneStar(input);
    fourthTwoStars(input);
}

private void fourthOneStar(String input) {
    List<Card> cards = getCards(input);
    var result = cards.stream().map(card -> {
        long numbersHit = card.cardsWon();
        return numbersHit == 0 ? 0 : Math.pow(2, numbersHit - 1);
    }).reduce(Double::sum);

    System.out.println(result);
}

private void fourthTwoStars(String input) {
    var cardPile = new HashMap<Integer, Integer>();
    var cards = getCards(input);
    for (Card card : cards) {
        cardPile.put(card.number, 1);
    }
    for (Card card : cards) {
        for (int i = 1; i <= card.cardsWon(); i++) {
            cardPile.compute(card.number + i, (_, v) -> v + cardPile.get(card.number));
        }
    }
    var result = cardPile.values().stream().reduce(Integer::sum);
    System.out.println(result);
}

private List<Card> getCards(String input) {
    return input.lines().map(line -> {
        var colonSplitCard = line.split(":");
        var numbersSplit = colonSplitCard[1].split("\\|");
        var winningNumbers = Arrays
                .stream(numbersSplit[0].split("\\s+"))
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        var numbers = Arrays
                .stream(numbersSplit[1].split("\\s+"))
                .filter(s -> !s.isBlank())
                .map(Integer::parseInt)
                .collect(Collectors.toSet());
        return new Card(extractOneNumber(colonSplitCard[0]), winningNumbers, numbers);
    }).toList();
}

private int extractOneNumber(String stringWithNumber) {
    Pattern digitRegex = Pattern.compile("\\d+");
    Matcher matcher = digitRegex.matcher(stringWithNumber);
    matcher.find();
    return Integer.parseInt(matcher.group());
}

record Card(int number, Set<Integer> winningNumbers, Set<Integer> numbers) {

    public long cardsWon() {
        return numbers.stream().filter(winningNumbers::contains).count();
    }
}