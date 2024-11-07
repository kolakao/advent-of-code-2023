import utils.FileUtils;

void main() {
    var input = FileUtils.readInput("Input3");
    thirdOneStar(input);
    thirdTwoStars(input);
}


private void thirdOneStar(String input) {
    var listOfParts = new LinkedList<Integer>();
    char[][] charTable = input.lines().map(String::toCharArray).toArray(char[][]::new);
    for (int i = 0; i < charTable.length; i++) {
        for (int j = 0; j < charTable[0].length; j++) {
            var c = charTable[i][j];
            if (c != '.' && !Character.isDigit(c)) {
                listOfParts.addAll(findPart(charTable, i, j));
            }
        }
    }
    System.out.println(listOfParts);
    System.out.println(listOfParts.stream().reduce(Integer::sum).orElseThrow());
}

private void thirdTwoStars(String input) {
    var gearRatios = new LinkedList<Integer>();
    char[][] charTable = input.lines().map(String::toCharArray).toArray(char[][]::new);
    for (int i = 0; i < charTable.length; i++) {
        for (int j = 0; j < charTable[0].length; j++) {
            var c = charTable[i][j];
            if (c == '*') {
                Optional<Integer> gearRatio = Stream.of(findPart(charTable, i, j))
                        .filter(parts -> parts.size() == 2)
                        .findFirst()
                        .flatMap(partNumbers -> partNumbers
                                .stream()
                                .reduce((i1, i2) -> i1 * i2)
                        );
                gearRatio.ifPresent(
                        gearRatios::add
                );
            }
        }
    }
    System.out.println(gearRatios);
    System.out.println(gearRatios.stream().reduce(Integer::sum).orElseThrow());
}

private Set<Integer> findPart(char[][] charTable, int i, int j) {
    var result = new HashSet<Integer>();

    var top = new Position(Math.max(0, i - 1), Math.max(0, j - 1));
    var mid = new Position(i, Math.max(0, j - 1));
    var bot = new Position(Math.min(charTable.length - 1, i + 1), Math.max(0, j - 1));

    Stream.of(top, mid, bot).forEach(pos -> {
        int boundary;
        if (j == 0) {
            boundary = Math.min(2, charTable[pos.i].length);
        } else {
            boundary = Math.min(pos.j + 3, charTable[pos.i].length);
        }
        for (int k = pos.j; k < boundary; k++) {
            findPartNumber(charTable[pos.i], k).ifPresent(result::add);
        }
    });
    System.out.println("FOR CHAR " + charTable[i][j] + "ON POSITION [" + i + "][" + j + "] FOUND " + result);
    return result;
}


private Optional<Integer> findPartNumber(char[] potentialPartChars, int position) {
    int i = position;
    int j = position;
    while (i >= 0 && Character.isDigit(potentialPartChars[i])) {
        i--;
    }
    int startPosition = i + 1;

    while (j < potentialPartChars.length && Character.isDigit(potentialPartChars[j])) {
        j++;
    }
    int endPosition = j - 1;
    StringBuilder sb = new StringBuilder();
    if (startPosition != endPosition || Character.isDigit(potentialPartChars[startPosition])) {
        for (int k = startPosition; k <= endPosition; k++) {
            sb.append(potentialPartChars[k]);
        }
    }
    if (sb.isEmpty()) {
        return Optional.empty();
    }
    var partFound = sb.toString();
    return Optional.of(Integer.parseInt(partFound));
}

record Position(int i, int j) {
}
