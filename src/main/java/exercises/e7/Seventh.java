import exercises.e7.model.Hand;
import utils.FileUtils;

void main() {
    var input = FileUtils.readInput("Input7");
    seventhOneStar(input);
    seventhTwoStars(input);
}

private void seventhOneStar(String input) {
    seventh(input, Hand::ofCards);
}

private void seventhTwoStars(String input) {
    seventh(input, Hand::ofCardsWithJoker);
}

private void seventh(String input, BiFunction<String, Integer, Hand> handCreationStrategy) {
    var hands = input.lines().map(line -> {
        var arr = line.split(" ");
        return handCreationStrategy.apply(arr[0], Integer.parseInt(arr[1]));
    }).toArray(Hand[]::new);

    var result = 0;
    Arrays.sort(hands);
    for (int i = 0; i < hands.length; i++) {
        result += hands[i].getBid() * (i + 1);
    }
    System.out.println(result);
}
