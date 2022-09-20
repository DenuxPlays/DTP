package dev.denux.dtp.utils;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.ZoneOffset;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.TimeZone;
import java.util.regex.Matcher;

public class RFC3339Util {
    private RFC3339Util() {}

    public static LocalDateTime parseDateTime(String str) {
        Matcher matcher = Constant.RFC3339_REGEX.matcher(str);
        if (!matcher.matches()) {
            throw new NumberFormatException("Invalid RFC3339 date: " + str);
        }
        int year = Integer.parseInt(matcher.group(1));
        int month = Integer.parseInt(matcher.group(2)) - 1;
        int day = Integer.parseInt(matcher.group(3));
        boolean isTimeGiven = matcher.group(4) != null;
        String tzShiftRegexGroup = matcher.group(9);
        boolean isTzShiftGiven = tzShiftRegexGroup != null;
        int hour = 0;
        int minute = 0;
        int second = 0;
        int milliseconds = 0;

        if (isTzShiftGiven && !isTimeGiven) {
            throw new NumberFormatException("Invalid date/time format, cannot specify time zone shift" +
                    " without specifying time: " + str);
        }
        if (isTimeGiven) {
            hour = Integer.parseInt(matcher.group(5)); // HH
            minute = Integer.parseInt(matcher.group(6)); // mm
            second = Integer.parseInt(matcher.group(7)); // ss
            if (matcher.group(8) != null) { // contains .milliseconds?
                milliseconds = Integer.parseInt(matcher.group(8).substring(1)); // milliseconds
                int fractionDigits = matcher.group(8).substring(1).length() - 3;
                milliseconds = (int) ((float) milliseconds / Math.pow(10, fractionDigits));
            }
        }
        Calendar dateTime = new GregorianCalendar(TimeZone.getTimeZone("GMT"));
        dateTime.set(year, month, day, hour, minute, second);
        dateTime.set(Calendar.MILLISECOND, milliseconds);
        long value = dateTime.getTimeInMillis();
        if (isTimeGiven && isTzShiftGiven) {
            int tzShift;
            if (Character.toUpperCase(tzShiftRegexGroup.charAt(0)) != 'Z') {
                tzShift = Integer.parseInt(matcher.group(11)) * 60 // time zone shift HH
                        + Integer.parseInt(matcher.group(12)); // time zone shift mm
                if (matcher.group(10).charAt(0) == '-') { // time zone shift + or -
                    tzShift = -tzShift;
                }
                value -= tzShift * 60000L;
            }
        }
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(value), ZoneOffset.UTC);
    }

    public static LocalTime parseTime(String str) {
        Matcher matcher = Constant.RFC3339_TIME_REGEX.matcher(str);
        if (!matcher.matches()) {
            throw new NumberFormatException("Invalid RFC3339 time: " + str);
        }
        int hour = Integer.parseInt(matcher.group(1)); // HH
        int minute = Integer.parseInt(matcher.group(2)); // mm
        String secondRegexGroup = matcher.group(3);
        int second = 0; // ss
        if (secondRegexGroup != null) {
            second = Integer.parseInt(secondRegexGroup); // ss
        }
        String fractionDigits = matcher.group(4); // .milliseconds
        int milliseconds = 0;
        if (fractionDigits != null) {
            milliseconds = Integer.parseInt(matcher.group(4).replace(".", "")); // milliseconds
        }
        return LocalTime.of(hour, minute, second, milliseconds);
    }
}
