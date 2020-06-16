package org.galatea.starter.utils;

import static org.junit.Assert.assertEquals;

import java.time.Clock;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import lombok.EqualsAndHashCode;
import lombok.RequiredArgsConstructor;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffResult;
import org.junit.Test;

@RequiredArgsConstructor
@Slf4j
@ToString
@EqualsAndHashCode
public class HelpersTest {

  public static class SimpleObject {

    private String x = "X";

    void setX(String x) {
      this.x = x;
    }

    public String getX() {
      return x;
    }

    // Starts with "get" but is private, so it shouldn't be included in the diff
    private long getNanoTime() {
      return System.nanoTime();
    }
  }

  private Clock createClock(String strDate, String zoneId) {
    Instant instant = ZonedDateTime.of(LocalDateTime.parse(strDate), ZoneId.of(zoneId)).toInstant();
    return Clock.fixed(instant, ZoneId.of(zoneId));
  }

  @Test
  public void testNoDiff() {

    SimpleObject lhs = new SimpleObject();
    SimpleObject rhs = new SimpleObject();

    DiffResult diffResult = Helpers.diff(lhs, rhs);
    assertEquals("", diffResult.toString());
  }

  @Test
  public void testDiff() {

    SimpleObject lhs = new SimpleObject();
    SimpleObject rhs = new SimpleObject();
    rhs.setX("Y");

    DiffResult diffResult = Helpers.diff(lhs, rhs);
    assertEquals("HelpersTest.SimpleObject[getX=X] differs from HelpersTest.SimpleObject[getX=Y]",
        diffResult.toString());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testExceptionDiff() {
    String lhs = "hi";
    SimpleObject rhs = new SimpleObject();
    Helpers.diff(lhs, rhs);
  }

  @Test
  public void testStringToDate() {
    String strDate = "2000-06-15";
    LocalDate date = Helpers.stringToDate(strDate);

    assertEquals(2000, date.getYear());
    assertEquals(Month.JUNE, date.getMonth());
    assertEquals(15, date.getDayOfMonth());
  }

  @Test
  public void testGetDateNDaysAgo() {
    Clock clock = createClock("2020-06-15T12:00:00", "America/New_York");

    LocalDate today = LocalDate.now(clock);

    LocalDate fiveDaysAgo = Helpers.getDateNDaysAgo(clock, 5);

    assertEquals(today.toEpochDay(), fiveDaysAgo.plusDays(5).toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdaySaturday() {
    Clock clock = createClock("2020-06-13T12:00:00", "America/New_York");

    LocalDate saturday = LocalDate.now(clock);

    LocalDate mostRecentWeekday = Helpers.getMostRecentWeekday(clock);

    assertEquals(saturday.minusDays(1L).toEpochDay(), mostRecentWeekday.toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdaySunday() {
    Clock clock = createClock("2020-06-14T12:00:00", "America/New_York");

    LocalDate sunday = LocalDate.now(clock);

    LocalDate mostRecentWeekday = Helpers.getMostRecentWeekday(clock);

    assertEquals(sunday.minusDays(2L).toEpochDay(), mostRecentWeekday.toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdayWeekendTimeDoesNotMatter() {
    Clock midnight = createClock("2020-06-14T00:00:00", "America/New_York");
    Clock morning = createClock("2020-06-14T09:00:00", "America/New_York");
    Clock noon = createClock("2020-06-14T12:00:00", "America/New_York");
    Clock evening = createClock("2020-06-14T18:00:00", "America/New_York");

    assertEquals(Helpers.getMostRecentWeekday(midnight).toEpochDay(),
        Helpers.getMostRecentWeekday(morning).toEpochDay());
    assertEquals(Helpers.getMostRecentWeekday(morning).toEpochDay(),
        Helpers.getMostRecentWeekday(noon).toEpochDay());
    assertEquals(Helpers.getMostRecentWeekday(noon).toEpochDay(),
        Helpers.getMostRecentWeekday(evening).toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdayMondayIncomplete() {
    Clock clock = createClock("2020-06-15T12:00:00", "America/New_York");

    LocalDate monday = LocalDate.now(clock);

    LocalDate mostRecentWeekday = Helpers.getMostRecentWeekday(clock);

    assertEquals(monday.minusDays(3L).toEpochDay(), mostRecentWeekday.toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdayMondayComplete() {
    Clock clock = createClock("2020-06-15T17:00:00", "America/New_York");

    LocalDate monday = LocalDate.now(clock);

    LocalDate mostRecentWeekday = Helpers.getMostRecentWeekday(clock);

    assertEquals(monday.toEpochDay(), mostRecentWeekday.toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdayMidweekIncomplete() {
    Clock clock = createClock("2020-06-17T12:00:00", "America/New_York");

    LocalDate wednesday = LocalDate.now(clock);

    LocalDate mostRecentWeekday = Helpers.getMostRecentWeekday(clock);

    assertEquals(wednesday.minusDays(1L).toEpochDay(), mostRecentWeekday.toEpochDay());
  }

  @Test
  public void testGetMostRecentWeekdayMidweekComplete() {
    Clock clock = createClock("2020-06-17T17:00:00", "America/New_York");

    LocalDate wednesday = LocalDate.now(clock);

    LocalDate mostRecentWeekday = Helpers.getMostRecentWeekday(clock);

    assertEquals(wednesday.toEpochDay(), mostRecentWeekday.toEpochDay());
  }

}
