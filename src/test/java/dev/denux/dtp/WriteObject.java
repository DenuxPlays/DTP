package dev.denux.dtp;

public class WriteObject {

    private final WriteClass writeClass = new WriteClass();

    private final String testString = "Hallo welt";
    private int integer;
    private final boolean isRight = true;
    private final double notANumber = Double.NaN;
    private final double infinity = Double.POSITIVE_INFINITY;

    private static class WriteClass {
        private final String subClassTest = "This tests something, can you believe it?";
    }
}
