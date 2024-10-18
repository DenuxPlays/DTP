package dev.denux.dtp;

import dev.denux.dtp.exception.TomlReadException;
import dev.denux.dtp.internal.reader.helper.ParseType;
import dev.denux.dtp.objects.TestEnum;
import dev.denux.dtp.objects.TestObject;
import dev.denux.dtp.objects.WriteObject;
import dev.denux.dtp.util.ArrayUtil;
import dev.denux.dtp.util.Constant;
import lombok.NonNull;
import lombok.experimental.NonFinal;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class DTPTest {

    private static final String multilineString = "\n" +
            "some value\n" +
            "    with tabs\n" +
            "and some more stupid whitespaces\\\" and this\n" +
            "        kind of stuff\n";

    public static void main(String[] args) throws Exception {
        TestObject testObject = new DTP().fromToml(getTomlReader(), TestObject.class);
        DTPTest tester = new DTPTest();
        DTPTest.parseTests(tester, testObject);
        DTPTest.writeTest(tester, new File("test.toml"));
    }

    public static void parseTests(DTPTest dtpTest, TestObject testObject) {
        dtpTest.stringTester(testObject);
        dtpTest.numberTester(testObject);
        dtpTest.booleanTester(testObject);
        //dtpTest.arrayTester(testObject);
        dtpTest.dateTimeTester(testObject);
        dtpTest.enumTester(testObject);
    }

    public static void writeTest(DTPTest dtpTest, File file) {
        dtpTest.testWrite(file);
    }

    private void stringTester(TestObject object) {
        assertEquals("Quoted key.", object.stringKey);
        assertEquals("Testing a normal string.", object.normalString);
        assertEquals(multilineString, object.multilineString);
        assertEquals("Some single quotes.", object.singleQuotes);
        assertEquals("", object.emptyString);
    }

    private void booleanTester(TestObject object) {
        assertTrue(object.trueBoolean);
        assertFalse(object.falseBoolean);
    }

    private void enumTester(TestObject object) {
        assertEquals(TestEnum.SUPER, object.testEnum);
    }

    private void numberTester(TestObject object) {
        assertEquals(120, object.numberTester.aByte);
        assertEquals(234, object.numberTester.aShort);
        assertEquals(1000, object.numberTester.anInt);
        assertEquals(-99999232, object.numberTester.aLong);
        assertEquals(-0.05F, object.numberTester.aFloat);
        assertEquals(6.626E-34, object.numberTester.aDouble);

        assertEquals(584836, object.numberTester.hex);
        assertEquals(493, object.numberTester.oct);
        assertEquals(214, object.numberTester.bin);

        assertEquals(Double.POSITIVE_INFINITY, object.numberTester.infinity);
        assertEquals(Double.NEGATIVE_INFINITY, object.numberTester.negativInfinity);
        assertEquals(Double.NaN, object.numberTester.notANumber);
    }

    public void dateTimeTester(TestObject object) {
        assertEquals("1979-05-27T07:32:00.999", object.dateTimeTester.dateTime.toString());
        assertEquals("00:32:00.000999999", object.dateTimeTester.time.toString());
    }

    public void arrayTester(TestObject object) {
        assertArrayEquals(new byte[]{1, 2, 3}, object.arrayTester.array);
        assertArrayEquals(new byte[][]{{3,2,1}, {1,2,3}}, object.arrayTester.twoDimensionalArray);
        assertArrayEquals(new String[]{"Test1", "Test2"}, object.arrayTester.stringArray);
        assertArrayEquals(new Long[]{3424L, 234L}, object.arrayTester.primitiveObjectArray);
    }

    public void testWrite(File file) {
        try {
            new DTP().writeTomlToFile(new WriteObject(), file, StandardOpenOption.CREATE);
        } catch (IOException exception) {
            throw new UncheckedIOException(exception);
        }
    }

    public static BufferedReader getTomlReader() throws Exception {
        ClassLoader loader = Thread.currentThread().getContextClassLoader() == null ? Thread.currentThread().getContextClassLoader() : DTPTest.class.getClassLoader();
        return new BufferedReader(new InputStreamReader(Objects.requireNonNull(loader.getResource("test.toml")).openStream()));
    }
}