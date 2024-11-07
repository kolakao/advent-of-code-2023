import utils.FileUtils;

import static java.lang.Character.isDigit;

void main() {
    var input = FileUtils.readInput("Input1");
    System.out.println(firstOneStar(input));
    System.out.println(firstTwoStars(input));
}


int firstOneStar(String input) {
    return input
            .lines()
            .map(this::getCalibrationValueOneStar)
            .reduce(Integer::sum).orElseThrow();
}

int firstTwoStars(String input) {
    return input
            .lines()
            .map(this::getCalibrationValueTwoStars)
            .reduce(Integer::sum).orElseThrow();
}

int getCalibrationValueOneStar(String input) {
    Integer first = null;
    Integer last = null;


    for (char c : input.toCharArray()) {
        if (isDigit(c)) {
            var number = Character.getNumericValue(c);
            if (first == null) {
                first = number;
            }
            last = number;
        }
    }

    return Integer.parseInt(first + "" + last);
}


int getCalibrationValueTwoStars(String input) {
    int first = 0;
    int last = 0;

    char[] inputArray = input.toCharArray();
    for (int i = 0; i < inputArray.length; i++) {
        var optionalDigit = getDigit(input.substring(i, Math.min(input.length(), i + 5)));
        if (optionalDigit.isPresent()) {
            var number = optionalDigit.get();
            if (first == 0) {
                first = number;
            }
            last = number;
        }
    }

    return Integer.parseInt(first + "" + last);
}


Optional<Integer> getDigit(String s) {
    if (s.isEmpty()) {
        return Optional.empty();
    }
    if (isDigit(s.charAt(0))) {
        return Optional.of(Character.getNumericValue(s.charAt(0)));
    }
    return Arrays
            .stream(DIGIT.values())
            .filter(digit -> s.toLowerCase().startsWith(digit.name))
            .findFirst()
            .map(d -> d.value);
}

enum DIGIT {

    ZERO("zero", 0), ONE("one", 1), TWO("two", 2), THREE("three", 3), FOUR("four", 4), FIVE("five", 5), SIX("six", 6), SEVEN("seven", 7), EIGHT("eight", 8), NINE("nine", 9);
    final String name;
    final int value;

    DIGIT(String s, int number) {
        name = s;
        value = number;
    }
}
