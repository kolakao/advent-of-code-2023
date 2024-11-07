import com.google.common.collect.Iterables;
import org.apache.commons.lang3.tuple.Pair;
import utils.FileUtils;

void main() {
    var input = FileUtils.readInput("Input8");
    var input2 = FileUtils.readInput("Input8");
    eightOneStar(input);
    eightTwoStars(input2);

}

private void eightOneStar(String input) {
    List<String> lines = input.lines().toList();
    String instructions = lines.getFirst();

    List<String> nodes = lines.subList(2, lines.size());
    var firstNode = "AAA";
    var lastNode = "ZZZ";

    Map<String, Pair<String, String>> nodesMap = nodes
            .stream()
            .map(node -> node.split("="))
            .collect(
                    Collectors
                            .toMap(node -> node[0].trim(),
                                    node -> {
                                        var leftRight = node[1].trim().split(",");
                                        return Pair.of(leftRight[0].trim().substring(1, 4), leftRight[1].trim().substring(0, 3));
                                    }
                            )
            );

    var next = firstNode;
    var counter = 0;
    for (String s : Iterables.cycle(instructions.split(""))) {
        if ("L".equals(s)) {
            next = nodesMap.get(next).getLeft();
        } else {
            next = nodesMap.get(next).getRight();
        }
        counter++;

        if (Objects.equals(next, lastNode)) {
            break;
        }
    }

    System.out.println(counter);
}

private void eightTwoStars(String input) {
    List<String> lines = input.lines().toList();
    String instructions = lines.getFirst();

    List<String> nodes = lines.subList(2, lines.size());

    Map<String, Pair<String, String>> nodesMap = nodes
            .stream()
            .map(node -> node.split("="))
            .collect(
                    Collectors
                            .toMap(node -> node[0].trim(),
                                    node -> {
                                        var leftRight = node[1].trim().split(",");
                                        return Pair.of(leftRight[0].trim().substring(1, 4), leftRight[1].trim().substring(0, 3));
                                    }
                            )
            );
    var nodesToProcess = nodesMap.keySet().stream().filter(n -> n.endsWith("A")).toList();
    var dd = nodesToProcess.stream().parallel().map(s -> findPeriod(s, instructions, nodesMap)).map(BigInteger::valueOf).toList();
    System.out.println(lcm(dd));

    System.out.println(dd);
}

private int findPeriod(String node, String instructions, Map<String, Pair<String, String>> nodesMap) {
    var current = node;
    var counter = 0;
    for (String s : Iterables.cycle(instructions.split(""))) {
        if ("L".equals(s)) {
            current = nodesMap.get(current).getLeft();
        } else {
            current = nodesMap.get(current).getRight();
        }
        counter++;
        if (current.endsWith("Z")) {
            return counter;
        }
    }
    throw new IllegalStateException("REEE");
}

private static BigInteger gcd(BigInteger x, BigInteger y) {
    return (y.equals(BigInteger.ZERO)) ? x : gcd(y, x.mod(y));
}

public static BigInteger lcm(List<BigInteger> numbers) {
    return numbers.stream().reduce(BigInteger.ONE, (x, y) -> x.multiply(y.divide(gcd(x, y))));
}