import com.google.common.collect.Streams;
import utils.FileUtils;

void main() {
    var input = FileUtils.readInput("Input6");
    sixthOneStar(input);
    var input2 = FileUtils.readInput("Input6TwoStars");
    sixthOneStar(input2);
}

private void sixthOneStar(String input) {
    var lines = input.lines().toArray(String[]::new);
    var times = Arrays.stream(lines[0].split(":")[1].split("\\s+")).filter(s -> !s.isBlank());
    var records = Arrays.stream(lines[1].split(":")[1].split("\\s+")).filter(s -> !s.isBlank());
    List<Race> races = Streams.zip(times, records, (t, r) -> new Race(new BigInteger(t), new BigInteger(r))).toList();
    System.out.println(races.stream().map(Race::possibilitiesOfWin).reduce(BigInteger::multiply).orElseThrow());
}

record Race(BigInteger time, BigInteger record) {
    BigInteger possibilitiesOfWin() {
        return Stream.iterate(BigInteger.ZERO, bi -> bi.compareTo(time) < 0, bi -> bi.add(BigInteger.ONE))
                .parallel()
                .map(buttonPushed -> buttonPushed.multiply(time.subtract(buttonPushed)))
                .filter(result -> result.compareTo(record) > 0)
                .map(_ -> BigInteger.ONE)
                .reduce(BigInteger.ZERO, BigInteger::add);
    }
}

