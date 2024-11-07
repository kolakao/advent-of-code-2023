import org.apache.commons.lang3.tuple.Triple;
import utils.FileUtils;

static final Set<Character> PIPE_TYPES = Set.of('L', 'F', '|', '-', 'J', '7');

void main() {
    var input = FileUtils.readInput("Input10");
    tenthOneStar(input);
    tenthTwoStar(input);
}

private void tenthTwoStar(String input) {
    char[][] pipesCharBoard = input.lines().map(String::toCharArray).toArray(char[][]::new);
    Pipe[][] pipesBoard = charBoardToPipesBoard(pipesCharBoard);
    var startingPosition = findStartingPosition(pipesCharBoard);
    var startingPipe = startingPipe(startingPosition, pipesCharBoard);
    System.out.println(startingPipe);
    var forwardPosition = startingPosition;
    var backwardPosition = startingPosition;
    var previousForward = startingPipe.getRight();
    var previousBackward = startingPipe.getMiddle();
    pipesBoard[startingPosition.x][startingPosition.y] = new Pipe(startingPipe.getLeft(), startingPosition, true, false);
    pipesCharBoard[startingPosition.x][startingPosition.y] = startingPipe.getLeft();
    boolean backAtTheStart = false;
    var counter = 0;
    while (!backAtTheStart) {
        var tempForward = forwardPosition;
        var tempBackward = backwardPosition;
        forwardPosition = advance(forwardPosition, previousForward, pipesCharBoard);
        backwardPosition = advance(backwardPosition, previousBackward, pipesCharBoard);
        previousForward = tempForward;
        previousBackward = tempBackward;
        backAtTheStart = forwardPosition.equals(startingPosition);
        pipesBoard[forwardPosition.x][forwardPosition.y] = new Pipe(pipesCharBoard[forwardPosition.x][forwardPosition.y], forwardPosition, true, false);
        counter++;
    }

    System.out.println(counter);

    for (Pipe[] pipes : pipesBoard) {
        for (Pipe pipe : pipes) {
            System.out.print(pipe.isPartOfALoop ? "X" : ".");
        }
        System.out.println();
    }
}

private Pipe[][] charBoardToPipesBoard(char[][] pipesCharBoard) {
    var pipes = new Pipe[pipesCharBoard.length][];
    for (int i = 0; i < pipesCharBoard.length; i++) {
        pipes[i] = new Pipe[pipesCharBoard[i].length];
        for (int i1 = 0; i1 < pipesCharBoard[i].length; i1++) {
            pipes[i][i1] = new Pipe(pipesCharBoard[i][i1], new Position(i, i1), false, false);
        }
    }
    return pipes;
}

private void tenthOneStar(String input) {
    char[][] pipesBoard = input.lines().map(String::toCharArray).toArray(char[][]::new);
    var startingPosition = findStartingPosition(pipesBoard);
    var startingPipe = startingPipe(startingPosition, pipesBoard);
    System.out.println(startingPipe);
    var forwardPosition = startingPosition;
    var backwardPosition = startingPosition;
    var previousForward = startingPipe.getRight();
    var previousBackward = startingPipe.getMiddle();
    pipesBoard[startingPosition.x][startingPosition.y] = startingPipe.getLeft();
    boolean pointersMet = false;
    var counter = 0;
    while (!pointersMet) {
        var tempForward = forwardPosition;
        var tempBackward = backwardPosition;
        forwardPosition = advance(forwardPosition, previousForward, pipesBoard);
        backwardPosition = advance(backwardPosition, previousBackward, pipesBoard);
        previousForward = tempForward;
        previousBackward = tempBackward;
        pointersMet = forwardPosition.equals(backwardPosition);
        counter++;
    }
    System.out.println(counter);
}

private Position advance(Position position, Position previousPosition, char[][] pipesBoard) {
    return switch (pipesBoard[position.x][position.y]) {
        case '-' -> switch (previousPosition.y < position.y) {
            case true -> new Position(position.x, position.y + 1);
            case false -> new Position(position.x, position.y - 1);
        };
        case '|' -> switch (previousPosition.x < position.x) {
            case true -> new Position(position.x + 1, position.y);
            case false -> new Position(position.x - 1, position.y);
        };
        case 'L' -> switch (previousPosition.x == position.x) {
            case true -> new Position(position.x - 1, position.y);
            case false -> new Position(position.x, position.y + 1);
        };
        case 'F' -> switch (previousPosition.x == position.x) {
            case true -> new Position(position.x + 1, position.y);
            case false -> new Position(position.x, position.y + 1);
        };
        case 'J' -> switch (previousPosition.x == position.x) {
            case true -> new Position(position.x - 1, position.y);
            case false -> new Position(position.x, position.y - 1);
        };
        case '7' -> switch (previousPosition.x == position.x) {
            case true -> new Position(position.x + 1, position.y);
            case false -> new Position(position.x, position.y - 1);
        };
        default -> throw new IllegalStateException("Pipeline is not closed!");
    };
}

private Triple<Character, Position, Position> startingPipe(Position start, char[][] pipesBoard) {
    final var xLimit = pipesBoard.length - 1;
    final var yLimit = pipesBoard[0].length - 1;

    final var connectsLeft = connects('L', pipesBoard[start.x][Math.max(start.y - 1, 0)]);
    final var connectsRight = connects('R', pipesBoard[start.x][Math.min(start.y + 1, yLimit)]);
    final var connectsTop = connects('T', pipesBoard[Math.max(start.x - 1, 0)][start.y]);
    final var connectsBot = connects('B', pipesBoard[Math.min(start.x + 1, xLimit)][start.y]);

    if (connectsBot && connectsTop) {
        return Triple.of('|', new Position(start.x - 1, start.y), new Position(start.x + 1, start.y));
    } else if (connectsBot && connectsLeft) {
        return Triple.of('7', new Position(start.x, start.y - 1), new Position(start.x + 1, start.y));
    } else if (connectsBot && connectsRight) {
        return Triple.of('F', new Position(start.x + 1, start.y), new Position(start.x, start.y + 1));
    } else if (connectsLeft && connectsRight) {
        return Triple.of('-', new Position(start.x, start.y - 1), new Position(start.x, start.y + 1));
    } else if (connectsLeft && connectsTop) {
        return Triple.of('J', new Position(start.x, start.y - 1), new Position(start.x - 1, start.y));
    } else if (connectsRight && connectsTop) {
        return Triple.of('L', new Position(start.x - 1, start.y), new Position(start.x, start.y + 1));
    } else {
        throw new IllegalStateException("Starting point cannot be matched to any pipe");
    }

}

private boolean connects(char direction, char subject) {
    return switch (direction) {
        case 'L' -> switch (subject) {
            case '-', 'F', 'L' -> true;
            default -> false;
        };
        case 'R' -> switch (subject) {
            case '-', 'J', '7' -> true;
            default -> false;
        };
        case 'T' -> switch (subject) {
            case '|', 'F', '7' -> true;
            default -> false;
        };
        case 'B' -> switch (subject) {
            case '|', 'J', 'L' -> true;
            default -> false;
        };
        default -> false;
    };
}

private Position findStartingPosition(char[][] board) {
    Position position = null;
    for (int i = 0; i < board.length; i++) {
        for (int j = 0; j < board[i].length; j++) {
            if (board[i][j] == 'S') {
                position = new Position(i, j);
                break;
            }
        }
    }
    return position;
}

private record Position(int x, int y) {
}

private record Pipe(char pipeSymbol, Position position, boolean isPartOfALoop, boolean isEnclosedByALoop) {
}