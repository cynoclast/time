package com.cynoclast.time;

import org.apache.commons.lang3.tuple.Pair;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.regex.Pattern;

/**
 * Calculates when to leave today, a day's time in decimal hours, or when to leave on Friday so we get a 40 hour work
 * week with arbitrary start, lunch, return and quitting times.
 *
 * Require no arguments and derives what calculation to run based solely on input.
 * @author cynoclast
 */
@SuppressWarnings("Duplicates")
public class Time {
    private static final Pattern CONTAINS_AT_LEAST_ONE_DIGIT = Pattern.compile("\\d");

    private static final DecimalFormat JUST_WHOLE_NUMBERS = new DecimalFormat("##");
    private static final DecimalFormat JUST_FRACTION = new DecimalFormat(".##");

    static {
        JUST_WHOLE_NUMBERS.setRoundingMode(RoundingMode.DOWN);
    }

    /**
     * Use cases:
     * <p>
     * When to leave today:
     * 8:00-1:00 1:30-
     * <p>
     * A day's time:
     * <p>
     * 8:44-12:30 1:00-6:30
     * <p>
     * When to leave on Friday:
     * <p>
     * 8.5 9.27 8.83 8.67 9:45-12:45 12:55-
     *
     * @param args one of the above inputs
     */
    public static void main(String[] args) {
        if (args == null || args.length == 0) {
            System.err.println("No time!");
            System.exit(1);
        } else {
            if (args.length == 1 || args.length == 2) {
                final String timeToLeaveOutputString = calculateOneDay(args);
                if (CONTAINS_AT_LEAST_ONE_DIGIT.matcher(timeToLeaveOutputString).find()) {
                    // If there are numbers (indicating a time) output it
                    System.out.println(timeToLeaveOutputString);
                } else {
                    // else it's an error message
                    System.err.println(timeToLeaveOutputString);
                    System.exit(1);
                }
            } else if (args.length == 6) {
                final String fridayTimeToLeave = calculateFridayTimeToLeave(args);
                System.out.println(fridayTimeToLeave);
            } else {
                System.err.println("Unknown number of arguments");
                System.exit(1);
            }
        }
    }

    /**
     * Calculates the time to leave on a Friday targeting 40.0 hours worked
     *
     * @param args the input string of four floats (hours for Monday-Thursday) and a morning start+end and afternoon start
     * @return the time to leave in 24h time
     */
    private static String calculateFridayTimeToLeave(String[] args) {
        // 8.5 9.27 8.83 8.57 9:30-12:30 12:40-
        final Pair<Integer, Integer> morningDurations = calculateMorningDurations(args[4]);

        float hoursAlreadyWorked = Float.parseFloat(args[0]) + Float.parseFloat(args[1]) + Float.parseFloat(args[2]) + Float.parseFloat(args[3]);
        float hoursToGo = 40f - hoursAlreadyWorked - morningDurations.getLeft() - (morningDurations.getRight() / 60f);

        final String[] afternoonStartTimePieces = args[5].split("-")[0].split(":");
        int afternoonStartTimeHour = Integer.parseInt(afternoonStartTimePieces[0]);
        if (afternoonStartTimeHour < 6) {
            afternoonStartTimeHour += 12;
        }
        int afternoonStartTimeMinute = Integer.parseInt(afternoonStartTimePieces[1]);

        String hoursToGoString = JUST_WHOLE_NUMBERS.format(hoursToGo);
        int afternoonStopTimeHour = afternoonStartTimeHour + Integer.parseInt(hoursToGoString);

        int afternoonStopTimeMinute = afternoonStartTimeMinute + morningDurations.getRight();
        if (afternoonStopTimeMinute > 60) {
            afternoonStopTimeHour++;
            afternoonStopTimeMinute -= 60;
        }
        return afternoonStopTimeHour + ":" + afternoonStopTimeMinute;
    }

    /**
     * Takes "8:00-1:00" and returns "5,0".
     *
     * @param morningStartEndTimes the input time string
     * @return Integer pair of hours and minutes
     */
    static Pair<Integer, Integer> calculateMorningDurations(String morningStartEndTimes) {
        String[] morningTimes = morningStartEndTimes.split("-");

        String morningString = morningTimes[0];

        final String[] morningStartTimePieces = morningString.split(":");
        int morningStartHour = Integer.parseInt(morningStartTimePieces[0]);
        int morningStartMinute = Integer.parseInt(morningStartTimePieces[1]);

        String morningStopString = morningTimes[1];
        final String[] morningTimeStringParts = morningStopString.split(":");
        final String morningStartHourString = morningTimeStringParts[0];
        int morningStopHour = Integer.parseInt(morningStartHourString);
        if (morningStopString.length() == 4) {
            // 1:00, so 1300
            morningStopHour = Integer.parseInt(morningStartHourString) + 12;
        }

        int morningStopMinute = Integer.parseInt(morningTimeStringParts[1]);

        // subtract

        if (morningStopMinute < morningStartMinute) {
            // uncarry the one...
            morningStopMinute += 60;
            morningStopHour--;
        }

        int morningDurationHours = morningStopHour - morningStartHour;
        int morningDurationMinutes = morningStopMinute - morningStartMinute;
        return Pair.of(morningDurationHours, morningDurationMinutes);
    }

    /**
     * Input:
     * "8:35-1:00" Just morning
     * or "8:35-1:00 1:10-" Morning with unknown afternoon end time.
     *
     * @param args The morning times or morning and afternoon start.
     * @return time to leave output string for System.out-ing.
     */
    private static String calculateOneDay(String[] args) {
        final Pair<Integer, Integer> morningDurations = calculateMorningDurations(args[0]);

        if (args.length == 1) {
            System.out.println(args[0] + " " + morningDurations.getLeft() + ":" + morningDurations.getRight());
        }
        int timeLeftHours = 8;
        int timeLeftMinutes = 0;

        if (morningDurations.getRight() > 60) {
            // uncarry the one...
            timeLeftMinutes = 60;
            timeLeftHours--;
        }

        if (timeLeftMinutes < morningDurations.getRight()) {
            timeLeftHours--;
            timeLeftMinutes += 60;
        }

        timeLeftMinutes = timeLeftMinutes - morningDurations.getRight();
        timeLeftHours = timeLeftHours - morningDurations.getLeft();


        if (args.length == 1) {
            System.out.println("Time left: " + timeLeftHours + ":" + timeLeftMinutes);
        }

        if (args.length == 2) {
            if (args[1].endsWith("-")) {
                //8:35-1:00 1:10-
                final Pair<Integer, String> integerStringPair = calculateTimeToLeave(args[1], timeLeftHours, timeLeftMinutes);
                return "Time to leave: " + integerStringPair.getLeft() + ":" + integerStringPair.getRight();
            } else {
                //8:35-1:00 1:10-6:10
                final float calculatedHoursForCompletedDay = calculateHoursForCompletedDay(args, morningDurations.getLeft(), morningDurations.getRight());
                return args[0] + " " + args[1] + " > " + calculatedHoursForCompletedDay;
            }
        } else {
            return "Need one or two arguments.";
        }
    }

    /**
     * Calculates the decimal hours for a day's work to two significant figures.
     *
     * @param args                   the day's log of time worked in roughly this format 8:35-1:00 1:20-5:10
     * @param morningDurationHours   the number of hours worked in the morning
     * @param morningDurationMinutes the number of minutes worked in the morning
     * @return the day's time in decimal hours
     */
    static float calculateHoursForCompletedDay(String[] args, int morningDurationHours, int morningDurationMinutes) {
        //8:35-1:00 1:20-5:10
        final String[] afternoonStartTimePieces = args[1].split("-")[0].split(":");
        int afternoonStartTimeHour = Integer.parseInt(afternoonStartTimePieces[0]);
        if (afternoonStartTimeHour < 6) {
            afternoonStartTimeHour += 12;
        }
        int afternoonStartTimeMinute = Integer.parseInt(afternoonStartTimePieces[1]);

        final String[] afternoonStopTimePieces = args[1].split("-")[1].split(":");
        int afternoonStopTimeHour = Integer.parseInt(afternoonStopTimePieces[0]);
        if (afternoonStopTimeHour < 8) {
            afternoonStopTimeHour += 12;
        }
        int afternoonStopTimeMinute = Integer.parseInt(afternoonStopTimePieces[1]);

        if (afternoonStartTimeMinute > afternoonStopTimeMinute) {
            afternoonStopTimeMinute += 60;
            afternoonStopTimeHour--;
        }

        int afternoonDurationMinutes = afternoonStopTimeMinute - afternoonStartTimeMinute;
        int afternoonDurationHours = afternoonStopTimeHour - afternoonStartTimeHour;

        int hoursWorked = morningDurationHours + afternoonDurationHours;
        int minutesWorked = morningDurationMinutes + afternoonDurationMinutes;
        if (minutesWorked >= 60) {
            minutesWorked -= 60;
            hoursWorked++;
        }

        float fractionalHoursWorked = minutesWorked / 60f;

        // gross hack
        String fractionalHoursWorkedrounded = hoursWorked + JUST_FRACTION.format(fractionalHoursWorked);
        fractionalHoursWorked = Float.parseFloat(fractionalHoursWorkedrounded);
        // /gross hack
        return fractionalHoursWorked;
    }

    static Pair<Integer, String> calculateTimeToLeave(String args, int timeLeftHours, int timeLeftMinutes) {
        // 12:25-
        String afternoonStartTimeString = "";
        if (args.length() == 5) {
            // ex: 1:30
            afternoonStartTimeString = args.substring(2, 4);
        } else if (args.length() == 6) {
            // ex: 12:30
            afternoonStartTimeString = args.substring(3, 5);
        }
        if (!afternoonStartTimeString.matches("^[0-9]+$")) {
            System.err.println("Invalid afternoon start time: [" + afternoonStartTimeString + "]");
            System.exit(1);
        }
        int afternoonStartTimeMinutes = Integer.parseInt(afternoonStartTimeString);

        final String afternoonStartTimeHoursString = args.split(":")[0];
        int afternoonStartTimeHour = Integer.parseInt(afternoonStartTimeHoursString);

        int timeToGoMinute = afternoonStartTimeMinutes + timeLeftMinutes;

        int timeToGoHour = afternoonStartTimeHour + timeLeftHours;

        if (timeToGoMinute >= 60) {
            timeToGoMinute -= 60;
            timeToGoHour++;
        }

        String timeToGoMinuteString;
        if (timeToGoMinute < 10) {
            timeToGoMinuteString = "0" + timeToGoMinute;
        } else {
            timeToGoMinuteString = String.valueOf(timeToGoMinute);
        }
        return Pair.of(timeToGoHour, timeToGoMinuteString);
    }
}
