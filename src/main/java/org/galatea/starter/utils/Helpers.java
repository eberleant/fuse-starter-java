package org.galatea.starter.utils;

import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.Modifier;
import java.sql.Date;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;
import java.util.TimeZone;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.builder.DiffBuilder;
import org.apache.commons.lang3.builder.DiffResult;
import org.apache.commons.lang3.builder.ToStringStyle;
import org.springframework.util.ReflectionUtils.MethodFilter;


@RequiredArgsConstructor(access = AccessLevel.PRIVATE)
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
   * @param strDate
   * @return
   */
  public static Date stringToDate(String strDate) {
    String[] strDateSplit = strDate.split("-");
    int year = Integer.parseInt(strDateSplit[0]);
    int month = Integer.parseInt(strDateSplit[1]) - 1;
    int day = Integer.parseInt(strDateSplit[2]);
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    calendar.set(year, month, day, 0, 0, 0);
    return new Date(calendar.getTimeInMillis());
  }

  /**
   * Given a Date object, returns a new Date with the same year, month, and day but time 00:00.
   * @param date
   * @return
   */
  public static Date getStartOfDay(Date date) {
    Calendar calendar = Calendar.getInstance();
    calendar.setTimeZone(TimeZone.getTimeZone("America/New_York"));
    calendar.setTime(date);
    calendar.set(
        calendar.get(Calendar.YEAR),
        calendar.get(Calendar.MONTH),
        calendar.get(Calendar.DAY_OF_MONTH),
        0, 0, 0);
    return new Date(calendar.getTimeInMillis());
  }

  /**
   * Given a number of days N, returns a Date N days ago at the beginning of the day
   * @param daysAgo
   * @return
   */
  public static Date getDateNDaysAgo(int daysAgo) {
    return getStartOfDay(new Date(
        Date.from(Instant.now().minus(Duration.ofDays(daysAgo))).getTime()));
  }
}
