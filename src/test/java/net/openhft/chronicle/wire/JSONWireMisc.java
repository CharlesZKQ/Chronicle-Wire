package net.openhft.chronicle.wire;

import net.openhft.chronicle.bytes.Bytes;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalTime;
import java.util.*;

import static net.openhft.chronicle.wire.JsonUtil.assertBalancedBrackets;


/**
 * relates to https://github.com/OpenHFT/Chronicle-Wire/issues/324
 */
@RunWith(value = Parameterized.class)
public class JSONWireMisc {

    private final String TEXT = "abc";
    private final boolean useTypes;
    private JSONWire wire;

    @Parameterized.Parameters(name = "useTypes={0}")
    public static Collection<Object[]> wireTypes() {
        return Arrays.asList(
                new Object[]{true},
                new Object[]{false}
        );
    }

    public JSONWireMisc(boolean useTypes) {
        this.useTypes = useTypes;
    }

    @Before
    public void before() {
        wire = new JSONWire().useTypes(useTypes);
    }

    @Test
    public void bytesByteArray() {
        final byte[] arr = TEXT.getBytes(StandardCharsets.UTF_8);
        wire.getValueOut().bytes(arr);
        final String actual = wire.toString();
        assertBalancedBrackets(actual);
        System.out.println("actual = " + actual);
    }

    @Test
    public void bytesStringByteArray() {
        final byte[] arr = TEXT.getBytes(StandardCharsets.UTF_8);
        wire.getValueOut().bytes("binary", arr);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void bytesStringBytes() {
        final Bytes<?> bytes = Bytes.from(TEXT);
        wire.getValueOut().bytes("binary", bytes);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void sequenceOfStrings() {
        final List<String> list = Arrays.asList("A", "B", "C");
        wire.getValueOut().sequence(list);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    enum A {
        FIRST, SECOND, THIRD;
    }

    @Test
    public void asEnum() {
        wire.getValueOut().asEnum(A.SECOND);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void sequenceOfEnums() {
        final List<A> list = Arrays.asList(A.values());
        wire.getValueOut().sequence(list);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void sequenceOfSet() {
        final Set<A> set = new HashSet<>(Arrays.asList(A.values()));
        wire.getValueOut().sequence(set);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void sequenceOfSortedSet() {
        final Set<A> set = new TreeSet<>(Arrays.asList(A.values()));
        wire.getValueOut().sequence(set);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void localTime() {
        final LocalTime localTime = LocalTime.parse("17:01");
        wire.getValueOut().object(localTime);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    final static class Foo {
        final int value;

        public Foo(int value) {
            this.value = value;
        }
    }

    @Test
    public void sequenceOfCustomClass() {
        final List<Foo> list = Arrays.asList(new Foo(0), new Foo(1), new Foo(2));
        wire.getValueOut().sequence(list);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    final static class Bar {

        final String value;

        public Bar(String value) {
            this.value = value;
        }

    }

    @Test
    public void customClass() {
        wire.getValueOut().object(new Bar("Bazz"));
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void duration() {
        final Duration duration = Duration.ofSeconds(63);
        wire.getValueOut().object(duration);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    static final class Ser implements Serializable {
        int foo;
    }

    @Test
    public void serializable() {
        final Ser s = new Ser();
        wire.getValueOut().object(s);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);
    }

    @Test
    public void durationHolder() {
        final DurationHolder holder = new DurationHolder();
        holder.foo = 1;
        holder.duration = Duration.ofSeconds(63);

        wire.getValueOut().object(holder);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);

        final String h = Marshallable.$toString(holder);
        final DurationHolder deserialized = Marshallable.fromString(h);

        System.out.println(deserialized);

    }

    static final class DurationHolder extends SelfDescribingMarshallable {
        int foo;
        Duration duration;
    }

    @Test
    public void durationHolderMarshallable() {
        final DurationHolder holder = new DurationHolder();
        holder.foo = 1;
        holder.duration = Duration.ofSeconds(63);

        final String h = Marshallable.$toString(holder);
        System.out.println("h = " + h);
        // This used to cause writing outside memory confinements
        final DurationHolder deserialized = Marshallable.fromString(h);
        System.out.println("deserialized = " + deserialized);
    }

    static final class EnumHolder extends SelfDescribingMarshallable {
        int foo;
        private EnumSet<DayOfWeek> activeDays;
    }

    @Test
    public void enums() {
        final EnumHolder holder = new EnumHolder();
        holder.foo = 1;
        holder.activeDays = EnumSet.of(DayOfWeek.MONDAY, DayOfWeek.TUESDAY, DayOfWeek.WEDNESDAY);

        wire.getValueOut().object(holder);
        final String actual = wire.toString();
        System.out.println("actual = " + actual);
        assertBalancedBrackets(actual);

        JSONWire deserializer = new JSONWire(wire.bytes()).useTypes(useTypes);
        EnumHolder eh = deserializer.getValueIn().object(EnumHolder.class);

        System.out.println("eh = " + eh);

    }

}
