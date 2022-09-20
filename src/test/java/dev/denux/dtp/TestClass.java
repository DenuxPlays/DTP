package dev.denux.dtp;

import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.StandardOpenOption;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class TestClass {

    private static final String multiline = "\n" +
            "some value\n" +
            "    with tabs\n" +
            "and some more stupid whitespaces\\\" and this\n" +
            "        kind of stuff\n";

    public static void main(String[] args) throws Exception {
        new TestClass().testParse();
        new TestClass().testWrite();
    }


    @Test
    public void testParse() throws Exception {
        TestObject object = new DTP().fromToml(getTomlReader(), TestObject.class);
        assertEquals("Help", object.test);
        assertEquals(13033333333335803.13123, object.du);
        assertFalse(object.good);
        assertEquals(Double.POSITIVE_INFINITY, object.infy);
        assertEquals(Double.NaN, object.nanu);
        assertEquals("Echter Klassen Name", object.testClass.className);
        assertEquals(4, object.testClass.zahl);
        assertArrayEquals(new byte[]{1, 2, 3}, object.arrayTester.array);
        assertArrayEquals(new String[]{"Test1", "Test2"}, object.arrayTester.mArray);
        assertEquals("1979-05-27T07:32:00.999", object.date.toString());
        assertEquals("00:32:00.000999999", object.time.toString());
        assertEquals(3735928559L, object.hex);
        assertEquals(493L, object.oct);
        assertEquals(214L, object.bin);
        assertEquals(TestEnum.SUPER, object.enumTest);
        assertEquals(multiline, object.multiline);
    }

    @Test
    public void testWrite() {
        try {
            new DTP().writeTomlToFile(new WriteObject(), new File("test.toml"), StandardOpenOption.CREATE);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public static BufferedReader getTomlReader() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() == null ? Thread.currentThread().getContextClassLoader() : TestClass.class.getClassLoader();
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(loader.getResource("test.toml")).openStream()));
    }
}