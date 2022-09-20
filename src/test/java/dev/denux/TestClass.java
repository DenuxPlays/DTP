package dev.denux;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestClass {

    private static final String multiline = "\n" +
            "some value\n" +
            "    with tabs\n" +
            "and some more stupid whitespaces\\\" and this\n" +
            "        kind of stuff\n";

    public static void main(String[] args) throws Exception {
        new TestClass().test();
    }


    @Test
    public void test() throws Exception {
        TestObject object = new DTP().fromToml(getTomlReader(), TestObject.class);
        assertEquals("Help", object.test);
        assertEquals(13033333333335803.13123, object.du);
        assertFalse(object.good);
        assertEquals(Double.POSITIVE_INFINITY, object.infy);
        assertEquals(Double.NaN, object.nanu);
        assertEquals("Echter Klassen Name", object.testClass.className);
        assertEquals(4, object.testClass.zahl);
        assertArrayEquals(new byte[]{1, 2, 3}, object.arrayTester.array);
        assertEquals("1979-05-27T07:32:00.999", object.date.toString());
        assertEquals("00:32:00.000999999", object.time.toString());
        assertEquals(3735928559L, object.hex);
        assertEquals(493L, object.oct);
        assertEquals(214L, object.bin);
        assertEquals(multiline, object.multiline);
    }

    public static BufferedReader getTomlReader() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() == null ? Thread.currentThread().getContextClassLoader() : TestClass.class.getClassLoader();
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(loader.getResource("test.toml")).openStream()));
    }
}