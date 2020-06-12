package org.galatea.starter.utils;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.Instant;
import java.util.Calendar;
import java.util.TimeZone;
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
    Date date = Helpers.stringToDate(strDate);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeInMillis(date.getTime());
    calendar.setTimeZone(TimeZone.getTimeZone("Universal"));

    assertEquals(2000, calendar.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, calendar.get(Calendar.MONTH));
    assertEquals(15, calendar.get(Calendar.DAY_OF_MONTH));
  }

  @Test
  public void testGetStartOfDay() {
    Calendar calendarBefore = Calendar.getInstance();
    calendarBefore.set(2000, Calendar.JUNE, 15, 12, 30,0);
    Date date = Helpers.getStartOfDay(new Date(calendarBefore.getTimeInMillis()));
    Calendar calendarAfter = Calendar.getInstance();
    calendarAfter.setTimeInMillis(date.getTime());
    calendarAfter.setTimeZone(TimeZone.getTimeZone("Universal"));

    log.info("Year: {}", calendarAfter.get(Calendar.YEAR));
    log.info("Month: {}", calendarAfter.get(Calendar.MONTH));
    log.info("Day of month: {}", calendarAfter.get(Calendar.DAY_OF_MONTH));
    log.info("Millis before: {}", calendarBefore.getTimeInMillis());
    log.info("Millis after: {}", calendarAfter.getTimeInMillis());


    assertEquals(2000, calendarAfter.get(Calendar.YEAR));
    assertEquals(Calendar.JUNE, calendarAfter.get(Calendar.MONTH));
    assertEquals(15, calendarAfter.get(Calendar.DAY_OF_MONTH));

    BigDecimal days = BigDecimal.valueOf(calendarAfter.getTimeInMillis() / (1000.0 * 60 * 60 * 24));
    assertTrue(days.stripTrailingZeros().scale() <= 0);
  }

  @Test
  public void testGetDateNDaysAgo() {
    Calendar today = Calendar.getInstance();
    today.setTime(Date.from(Instant.now()));
    today.setTimeZone(TimeZone.getTimeZone("Universal"));

    Calendar fiveDaysAgo = Calendar.getInstance();
    fiveDaysAgo.setTime(Helpers.getDateNDaysAgo(5));
    fiveDaysAgo.setTimeZone(TimeZone.getTimeZone("Universal"));

    assertEquals(today.get(Calendar.DAY_OF_YEAR), (fiveDaysAgo.get(Calendar.DAY_OF_YEAR) + 5)
        % fiveDaysAgo.getActualMaximum(Calendar.DAY_OF_YEAR));
  }

}
