package com.cynoclast.time;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

/**
 * @author cynoclast
 */
public class TimeTest {

    @Test
    public void testMain() {
        String[] args = {"8:00-1:00", "1:30-"};
        Time.main(args);
    }

    @Test
    public void testMain_Friday() {
        // 8.5 9.27 8.83 8.57 8:00-1:00 1:30-
        String[] args = {"8.5", "9.27", "8.83", "8.57", "9:30-12:55", "1:05-"};
        Time.main(args);
    }

    @Test
    public void testCalculateMorningDurationsHappyPath() {
        String aMorning = "8:00-1:00";
        final Pair<Integer, Integer> integerIntegerPair = Time.calculateMorningDurations(aMorning);
        assertNotNull(integerIntegerPair);
        assertEquals("Should be five hours", new Integer(5), integerIntegerPair.getLeft());
        assertEquals("Should be four hours and no minutes", new Integer(0), integerIntegerPair.getRight());
    }

    @Test
    public void testCalculateMorningDurations_weirdMinutes() {
        String aMorning = "8:30-1:00";
        final Pair<Integer, Integer> integerIntegerPair = Time.calculateMorningDurations(aMorning);
        assertNotNull(integerIntegerPair);
        assertEquals("Should be five hours", new Integer(4), integerIntegerPair.getLeft());
        assertEquals("Should be four hours and no minutes", new Integer(30), integerIntegerPair.getRight());
    }

    @Test
    public void testCalculateHoursForCompletedDay_happyPath() {
        String[] args = {"8:35-1:00", "1:10-6:10"};
        final float calculatedHoursForCompletedDay = Time.calculateHoursForCompletedDay(args, 3, 0);
        assertEquals("should be aasfasd", 8.0, calculatedHoursForCompletedDay, 0.0);
    }

    @Test
    public void testCalculateHoursForCompletedDay_weirdMinutes() {
        String[] args = {"8:35-1:22", "1:55-7:22"};
        final float calculatedHoursForCompletedDay = Time.calculateHoursForCompletedDay(args, 3, 0);
        assertEquals("should be aasfasd", 8.449999809265137, calculatedHoursForCompletedDay, 0.0);
    }

    @Test
    public void testCalculateTimeToLeave() {
        // 12:25-
        final Pair<Integer, String> integerStringPair = Time.calculateTimeToLeave("12:25-", 4, 0);
        assertNotNull(integerStringPair);
        assertEquals(16.0, integerStringPair.getLeft(), 0.0);
        assertEquals("25", integerStringPair.getRight());
    }
}
