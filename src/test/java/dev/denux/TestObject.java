package dev.denux;

import java.time.LocalDateTime;
import java.time.LocalTime;

public class TestObject {

    public final TestClass testClass = new TestClass();
    public final ArrayTester arrayTester = new ArrayTester();

    public double du;
    public boolean good;
    public String test;
    public double infy;
    public double nanu;
    public LocalDateTime date;
    public LocalTime time;
    public long hex;
    public long oct;
    public long bin;
    public String multiline;

    public static class TestClass {
        public String className;
        public int zahl;
    }

    public static class ArrayTester {
        public byte[] array;
        public String[] mArray;
    }
}
