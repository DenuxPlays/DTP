package dev.denux;

public class TestObject {

    public final TestClass testClass = new TestClass();
    public final ArrayTester arrayTester = new ArrayTester();

    public double du;
    public boolean good;
    public String test;
    public double infy;
    public double nanu;

    public static class TestClass {
        public String className;
        public int zahl;
    }

    public static class ArrayTester {
        public byte[] array;
    }
}
