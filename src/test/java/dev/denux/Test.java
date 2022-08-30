package dev.denux;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class Test {

    public static void main(String[] args) throws Exception {
        test();
    }


    public static void test() throws Exception {
        TestObject object = new DTP().fromToml(getTomlReader(), TestObject.class);
        assertEquals("Help", object.test);
        assertEquals(13033333333335803.13123, object.du);
        assertFalse(object.good);
        assertEquals(Double.POSITIVE_INFINITY, object.infy);
        assertEquals(Double.NaN, object.nanu);
        assertEquals("Echter Klassen Name", object.testClass.className);
        assertEquals(4, object.testClass.zahl);
        assertArrayEquals(new byte[]{1, 2,3}, object.arrayTester.array);
        System.out.println("object.date = " + object.date);
        System.out.println("object.time = " + object.time);
    }

    public static BufferedReader getTomlReader() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() == null ? Thread.currentThread().getContextClassLoader() : Test.class.getClassLoader();
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(loader.getResource("test.toml")).openStream()));
    }
}
