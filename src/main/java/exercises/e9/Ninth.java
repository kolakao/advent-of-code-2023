import utils.FileUtils;


void main() {
    var input = FileUtils.readInput("Input9");
    ninthOneStar(input);
    ninthTwoStars(input);
}

private void ninthOneStar(String input) {
    findSumOfNewValues(input, this::nextValue);
}

private void ninthTwoStars(String input) {
    findSumOfNewValues(input, this::previousValue);
}

private void findSumOfNewValues(String input, Function<Integer[], Integer> newValueMapper) {
    var result = input
            .lines()
            .map(s -> Arrays.stream(s.split(" ")).filter(num -> !num.isBlank()).map(Integer::parseInt).toArray(Integer[]::new))
            .map(newValueMapper)
            .reduce(Integer::sum)
            .orElseThrow();
    System.out.println(result);
}

private int nextValue(Integer[] sequence) {
    if (Arrays.stream(sequence).map(Math::abs).reduce(Integer::sum).orElseThrow() == 0) {
        return 0;
    }
    var newSequence = new Integer[sequence.length - 1];
    for (int i = 0, j = 0; i < sequence.length - 1; i++, j++) {
        newSequence[j] = sequence[i + 1] - sequence[i];
    }
    return sequence[sequence.length - 1] + nextValue(newSequence);
}

private int previousValue(Integer[] sequence) {
    if (Arrays.stream(sequence).map(Math::abs).reduce(Integer::sum).orElseThrow() == 0) {
        return 0;
    }
    var newSequence = new Integer[sequence.length - 1];
    for (int i = 0, j = 0; i < sequence.length - 1; i++, j++) {
        newSequence[j] = sequence[i + 1] - sequence[i];
    }
    return sequence[0] - previousValue(newSequence);
}
