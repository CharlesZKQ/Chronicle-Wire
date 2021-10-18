package net.openhft.chronicle.wire;

import org.junit.Test;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static net.openhft.chronicle.wire.JsonUtil.assertBalancedBrackets;
import static org.junit.Assert.assertEquals;

public class Issue328 {

    @Test
    public void map() {
        final Wire wire = new JSONWire().useTypes(true);
        final int size = 3;
        // keys must be strings in JSON
        final Map<String, String> map = IntStream.range(0, size)
                .boxed()
                .collect(Collectors.toMap(i -> Integer.toString(i), i -> Integer.toString(i)));

        wire.getValueOut().object(map);
        final String actual = wire.toString();
        final String expected = IntStream.range(0, size)
                .boxed()
                .map(i -> String.format("\"%d\":\"%d\"", i, i))
                .collect(Collectors.joining(",", "{", "}"));

        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
        assertEquals(expected, actual);
    }

}
