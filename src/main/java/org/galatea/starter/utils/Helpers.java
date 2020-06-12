package org.galatea.starter.utils;

import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.Modifier;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.ReflectionUtils.MethodFilter;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
@Slf4j
public class Helpers {

  /**
   * Compares the values of all methods between the left-hand-side and right-hand-side. Checks for
   * methods that begin with 'get' and 'is'.
   */
  public static DiffResult diff(final Object lhs, final Object rhs) {
    return diff(lhs, rhs,
        method -> (method.getName().startsWith("get") || method.getName().startsWith("is"))
            && Modifier.isPublic(method.getModifiers()));
  }

  /**
   * Compares the values of all methods between the left-hand-side and right-hand-side. Uses the
   * provided MethodFilter to determine which methods to check.
   */
  public static DiffResult diff(final Object lhs, final Object rhs, final MethodFilter filter) {
    final DiffBuilder builder = new DiffBuilder(lhs, rhs, ToStringStyle.SHORT_PREFIX_STYLE);

    if (!lhs.getClass().isAssignableFrom(rhs.getClass())) {
      throw new IllegalArgumentException("lhs is not assignable from rhs");
    }

    doWithMethods(lhs.getClass(), method -> builder.append(method.getName(),
        invokeMethod(method, lhs), invokeMethod(method, rhs)), filter);

    return builder.build();
  }

  /**
   * Converts String representation of date (yyyy-MM-dd) into instance of java.sql.Date class.
   * @param strDate string representation of date using pattern yyyy-MM-dd
   * @return
   */
  public static Date stringToDate(final String strDate) {
    String[] strDateSplit = strDate.split("-");
    int year = Integer.parseInt(strDateSplit[0]);
    int month = Integer.parseInt(strDateSplit[1]) - 1; // Calendar months start at 0
    int day = Integer.parseInt(strDateSplit[2]);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone(TimeZone.getTimeZone("Universal"));
    calendar.set(year, month, day, 0, 0, 0);
    return getStartOfDay(new Date(calendar.getTimeInMillis()));
  }

  /**
   * Given a Date object, returns a new Date with the same year, month, and day but time 00:00 UTC.
   * @param date any date
   * @return
   */
  public static Date getStartOfDay(final Date date) {
    // Calendar calendar = Calendar.getInstance();
    // calendar.setTimeZone(TimeZone.getTimeZone("Universal"));
    // calendar.setTime(date);
    // ZoneId zoneId = ZoneId.of("Universal");
    // LocalDate today = LocalDate.of(
    //     calendar.get(Calendar.YEAR),
    //     calendar.get(Calendar.MONTH) + 1,
    //     calendar.get(Calendar.DAY_OF_MONTH));
    // ZonedDateTime zdtStart = today.atStartOfDay(zoneId);
    // return new Date(Date.from(zdtStart.toInstant()).getTime());
    int days = (int) (date.getTime() / (1000L * 60 * 60 * 24));
    return new Date(days * 24L * 60 * 60 * 1000);
  }

  /**
   * Given a number of days N, returns a Date N days ago at 00:00 UTC.
   * @param daysAgo number of days ago to return
   * @return
   */
  public static Date getDateNDaysAgo(final int daysAgo) {
    return getStartOfDay(new Date(
        Date.from(Instant.now().minus(Duration.ofDays(daysAgo))).getTime()));
  }

  /**
   * Get the most recent weekday based on the current day. If it is before 5pm Eastern,
   * which is around when Alpha Vantage's API updates for the day, then the most recent weekday
   * is calculated from the previous day, instead of the current day.
   * @return
   */
  public static Date getMostRecentWeekday() {
    Calendar endOfWorkday = Calendar.getInstance();
    endOfWorkday.setTimeZone(TimeZone.getTimeZone("Universal"));
    endOfWorkday.setTime(getDateNDaysAgo(0));
    endOfWorkday.set(Calendar.HOUR_OF_DAY, 21); // 5PM ET

    Calendar currentDate = Calendar.getInstance();
    currentDate.setTimeZone(TimeZone.getTimeZone("Universal"));
    currentDate.setTime(new Date(Instant.now().toEpochMilli())); // current time

    int offset = 0;
    if (currentDate.before(endOfWorkday)) { // if current time before 5PM, treat as yesterday
      currentDate.setTime(getDateNDaysAgo(1));
      offset++;
    }

    if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SUNDAY) {
      return getDateNDaysAgo(2 + offset);
    } else if (currentDate.get(Calendar.DAY_OF_WEEK) == Calendar.SATURDAY) {
      return getDateNDaysAgo(1 + offset);
    } else {
      return getDateNDaysAgo(offset);
    }
  }
}
