import org.apache.commons.lang3.Range;
import org.apache.commons.lang3.tuple.Pair;
import utils.FileUtils;


void main() {
    var input = FileUtils.readInput("Input5");
//    fifthOneStar(input);
//    fifthTwoStars(input);
    var before = Instant.now();
    fifthTwoStarsBis(input);
    var after = Instant.now();
    System.out.println("Processing took " + Duration.between(before, after).toMillis() + " ms");
}


private void fifthOneStar(String input) {
    var lines = input.lines().toList();
    var seeds = Arrays.stream(lines
                    .getFirst()
                    .split(":")[1]
                    .split("\\s+"))
            .filter(s -> !s.isBlank())
            .map(s -> Range.of(new BigInteger(s), new BigInteger(s)))
            .toList();
    var almanac = Almanac.parse(seeds, input);

    System.out.println(seeds.stream().map(Range::getMaximum)
            .map(almanac::seedToLocation)
            .min(BigInteger::compareTo)
            .orElseThrow());
}


void fifthTwoStars(String input) {
    var lines = input.lines().toList();
    var seedsInput = Arrays.stream(lines
                    .getFirst()
                    .split(":")[1]
                    .split("\\s+"))
            .filter(s -> !s.isBlank())
            .map(BigInteger::new)
            .toArray(BigInteger[]::new);
    var seeds = new LinkedList<Range<BigInteger>>();
    for (int i = 0; i < seedsInput.length; i += 2) {
        seeds.add(Range.of(seedsInput[i], seedsInput[i].add(seedsInput[i + 1].subtract(BigInteger.ONE))));
    }
    var almanac = Almanac.parse(seeds, input);

    System.out.println(seeds.stream()
            .parallel()
            .flatMap(r -> Stream.iterate(r.getMinimum(), r::contains, bd -> bd.add(BigInteger.ONE)))
            .map(almanac::seedToLocation)
            .min(BigInteger::compareTo));
}

void fifthTwoStarsBis(String input) {
    var lines = input.lines().toList();
    var seedsInput = Arrays.stream(lines
                    .getFirst()
                    .split(":")[1]
                    .split("\\s+"))
            .filter(s -> !s.isBlank())
            .map(BigInteger::new)
            .toArray(BigInteger[]::new);
    var seeds = new LinkedList<Range<BigInteger>>();
    for (int i = 0; i < seedsInput.length; i += 2) {
        seeds.add(Range.of(seedsInput[i], seedsInput[i].add(seedsInput[i + 1].subtract(BigInteger.ONE))));
    }
    var almanac = Almanac.parse(seeds, input);

    var upperBound = almanac.seeds.stream().map(Range::getMaximum).max(BigInteger::compareTo).orElseThrow();
    var dataChunk = 100000;
    for (BigInteger i = BigInteger.ZERO; i.compareTo(upperBound) < 0; i = i.add(BigInteger.valueOf(dataChunk))) {
        BigInteger location = i;
        var result = Stream.iterate(location, next -> next.compareTo(location.add(BigInteger.valueOf(dataChunk))) < 0, pr -> pr.add(BigInteger.ONE))
                .parallel()
                .map(loc -> Pair.of(loc, almanac.locationToSeed(loc)))
                .filter(p -> almanac.seeds.stream().anyMatch(r -> r.contains(p.getRight())))
                .min(Comparator.comparing(Pair::getLeft));

        if (result.isPresent()) {
            System.out.println(result.get().getLeft());
            break;
        }
    }

}

record Almanac(List<Range<BigInteger>> seeds,
               List<SourceToDestinationMapping> seedToSoil,
               List<SourceToDestinationMapping> soilToFertilizer,
               List<SourceToDestinationMapping> fertilizerToWater,
               List<SourceToDestinationMapping> waterToLight,
               List<SourceToDestinationMapping> lightToTemperature,
               List<SourceToDestinationMapping> temperatureToHumidity,
               List<SourceToDestinationMapping> humidityToLocation) {

    static Almanac parse(List<Range<BigInteger>> seeds, String mappings) {
        var lines = mappings.lines().toList();
        List<SourceToDestinationMapping> mapping = null;
        List<List<SourceToDestinationMapping>> almanacParts = new ArrayList<>();
        for (String line : lines.subList(2, lines.size())) {
            if (line.isBlank()) {
                almanacParts.add(mapping);
            } else if (Character.isDigit(line.charAt(0))) {
                var sourceDestinationRange = line.split("\\s+");
                var destination = sourceDestinationRange[0];
                var source = sourceDestinationRange[1];
                var range = new BigInteger(sourceDestinationRange[2]);
                var sourceRange = Range.of(new BigInteger(source), new BigInteger(source).add(range));
                var destinationRange = Range.of(new BigInteger(destination), new BigInteger(destination).add(range));
                mapping.add(new SourceToDestinationMapping(destinationRange,
                        sourceRange
                ));
            } else {
                mapping = new LinkedList<>();
            }
        }
        almanacParts.add(mapping);
        return new Almanac(seeds,
                almanacParts.get(0),
                almanacParts.get(1),
                almanacParts.get(2),
                almanacParts.get(3),
                almanacParts.get(4),
                almanacParts.get(5),
                almanacParts.get(6));
    }

    BigInteger seedToLocation(BigInteger seed) {
        return Stream.of(seed).map(
                s -> this.seedToSoil.stream().map(m -> m.getDestination(s)).filter(Objects::nonNull).findFirst().orElse(s)
        ).map(
                s -> this
                        .soilToFertilizer.stream().map(m -> m.getDestination(s)).filter(Objects::nonNull).findFirst().orElse(s)
        ).map(
                f -> this
                        .fertilizerToWater.stream().map(m -> m.getDestination(f)).filter(Objects::nonNull).findFirst().orElse(f)
        ).map(
                w -> this
                        .waterToLight.stream().map(m -> m.getDestination(w)).filter(Objects::nonNull).findFirst().orElse(w)
        ).map(
                l -> this
                        .lightToTemperature.stream().map(m -> m.getDestination(l)).filter(Objects::nonNull).findFirst().orElse(l)
        ).map(
                t -> this
                        .temperatureToHumidity.stream().map(m -> m.getDestination(t)).filter(Objects::nonNull).findFirst().orElse(t)
        ).map(
                h -> this
                        .humidityToLocation.stream().map(m -> m.getDestination(h)).filter(Objects::nonNull).findFirst().orElse(h)
        ).findFirst().orElseThrow();
    }

    BigInteger locationToSeed(BigInteger location) {
        return Stream.of(location).map(itr -> this.humidityToLocation.stream().map(
                        m -> m.getSource(itr)
                ).filter(Objects::nonNull).findFirst().orElse(itr))
                .map(h -> this.temperatureToHumidity.stream().map(
                        m -> m.getSource(h)
                ).filter(Objects::nonNull).findFirst().orElse(h))
                .map(t -> this.lightToTemperature.stream().map(
                        m -> m.getSource(t)
                ).filter(Objects::nonNull).findFirst().orElse(t))
                .map(l -> this.waterToLight.stream().map(
                        m -> m.getSource(l)
                ).filter(Objects::nonNull).findFirst().orElse(l))
                .map(w -> this.fertilizerToWater.stream().map(
                        m -> m.getSource(w)
                ).filter(Objects::nonNull).findFirst().orElse(w))
                .map(f -> this.soilToFertilizer.stream().map(
                        m -> m.getSource(f)
                ).filter(Objects::nonNull).findFirst().orElse(f))
                .map(s -> this.seedToSoil.stream().map(
                        m -> m.getSource(s)
                ).filter(Objects::nonNull).findFirst().orElse(s)).findFirst().orElseThrow();
    }
}

record SourceToDestinationMapping(Range<BigInteger> destinationRange, Range<BigInteger> sourceRange) {
    BigInteger getDestination(BigInteger source) {
        return sourceRange.contains(source) ? this.destinationRange.getMinimum().add(source.subtract(this.sourceRange.getMinimum())) : null;
    }

    BigInteger getSource(BigInteger destination) {
        return destinationRange.contains(destination) ? this.sourceRange.getMinimum().add(destination.subtract(this.destinationRange.getMinimum())) : null;
    }
}

