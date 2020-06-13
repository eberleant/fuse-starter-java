package org.galatea.starter.utils;

import static org.springframework.util.ReflectionUtils.doWithMethods;
import static org.springframework.util.ReflectionUtils.invokeMethod;

import java.lang.reflect.Modifier;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
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
   * Converts String representation of date (yyyy-MM-dd) into LocalDate object.
   * @param strDate string representation of []
   * @return
   */
  public static LocalDate stringToDate(final String strDate) {
    return LocalDate.parse(strDate);
  }

  /**
   * Given a number of days N, returns a Date N days ago at 00:00 UTC.
   * @param daysAgo number of days ago to return
   * @return
   */
  public static LocalDate getDateNDaysAgo(final long daysAgo) {
    return LocalDate.now(ZoneId.of("America/New_York")).minusDays(daysAgo);
  }

  /**
   * Get the most recent weekday based on the current day. If it is before 4pm Eastern,
   * which is around when Alpha Vantage's API updates for the day, then the most recent weekday
   * is calculated from the previous day, instead of the current day.
   * @return
   */
  public static LocalDate getMostRecentWeekday() {
    LocalTime now = LocalTime.now(ZoneId.of("America/New_York"));
    LocalTime endOfWorkday = LocalTime.of(16, 10); // 4PM = closing time of NYSE

    int offset = 0;
    if (now.isBefore(endOfWorkday)) {
      offset++;
    }

    LocalDate today = getDateNDaysAgo(offset);
    if (today.getDayOfWeek() == DayOfWeek.SUNDAY) {
      return getDateNDaysAgo(2 + offset);
    } else if (today.getDayOfWeek() == DayOfWeek.SATURDAY) {
      return getDateNDaysAgo(1 + offset);
    } else {
      return getDateNDaysAgo(offset);
    }
  }
}
