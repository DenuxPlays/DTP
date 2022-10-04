package dev.denux.dtp.objects;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestObject {

    public final NumberTester numberTester = new NumberTester();
    public final DateTimeTester dateTimeTester = new DateTimeTester();
    public final ArrayTester arrayTester = new ArrayTester();

    public String stringKey;
    public String normalString;
    public String multilineString;
    public String singleQuotes;
    public String emptyString;

    public boolean trueBoolean;
    public boolean falseBoolean;

    public TestEnum testEnum;

    public static class ArrayTester {
        public byte[] array;
        public String[] stringArray;
        public byte[][] twoDimensionalArray;
        public Long[] primitiveObjectArray;
    }

    public static class NumberTester {
        public byte aByte;
        public short aShort;
        public int anInt;
        public long aLong;
        public float aFloat;
        public double aDouble;

        public int hex;
        public int oct;
        public int bin;

        public double infinity;
        public double negativInfinity;
        public double notANumber;
    }

    public static class DateTimeTester {
        public LocalDateTime dateTime;
        public LocalTime time;
    }
}
