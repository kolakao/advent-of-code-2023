import utils.FileUtils;

void main() {
    var input = FileUtils.readInput("Input2");
    secondOneStar(input);
    secondTwoStars(input);
}


private void secondOneStar(String input) {
    var games = getGames(input);

    int result = games
            .stream()
            .filter(
                    game -> game.isPossible(12, 13, 14)
            ).map(Game::id)
            .reduce(
                    Integer::sum
            ).orElseThrow();

    System.out.println(result);
}

private void secondTwoStars(String input) {
    var games = getGames(input);

    var result = games
            .stream()
            .map(Game::minimumPossible)
            .map(g -> g.rolls.getFirst())
            .map(Roll::power)
            .reduce(Integer::sum);
    System.out.println(result);
}

private List<Game> getGames(String input) {
    return input.lines()
            .map(inputLine -> inputLine.split(":"))
            .map(this::game)
            .toList();
}

private Game game(String[] gameIdWithRolls) {
    var rolls = Arrays.stream(gameIdWithRolls[1].split(";"))
            .map(s -> s.split(","))
            .map(rollsArr -> {
                int red = 0;
                int blue = 0;
                int green = 0;
                for (String rollStr : rollsArr) {
                    var rollArr = rollStr.split(" ");
                    switch (rollArr[2]) {
                        case "red" -> red = Integer.parseInt(rollArr[1]);
                        case "green" -> green = Integer.parseInt(rollArr[1]);
                        case "blue" -> blue = Integer.parseInt(rollArr[1]);
                    }
                }
                return new Roll(red, green, blue);
            }).toList();
    return new Game(Integer.parseInt(gameIdWithRolls[0].split(" ")[1]), rolls);
}

record Game(int id, List<Roll> rolls) {
    public boolean isPossible(int red, int green, int blue) {
        return rolls.stream().filter(r -> r.red > red
                        || r.green > green
                        || r.blue > blue)
                .findAny()
                .isEmpty();
    }

    public Game minimumPossible() {
        return new Game(id,
                List.of(
                        rolls.stream().reduce(
                                (r1, r2) -> new Roll(Math.max(r1.red, r2.red),
                                        Math.max(r1.green, r2.green),
                                        Math.max(r1.blue, r2.blue))
                        ).orElseThrow()
                ));
    }
}

record Roll(int red, int green, int blue) {
    public int power() {
        return red * green * blue;
    }
}