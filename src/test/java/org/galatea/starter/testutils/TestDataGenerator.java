package org.galatea.starter.testutils;

import java.math.BigDecimal;
import java.sql.Date;
import java.util.Calendar;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.Prices;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages;
import org.galatea.starter.utils.Helpers;

/**
 * Utility class for generating default domain objects for tests.
 *
 * <p>Objects are returned in their builder forms so that objects without setter methods can still
 * be easily modified during test setup.
 */
// Mainly intended to be used when the specific data in the object doesn't matter, or when a test
// only cares about one or two fields (in which case those values should be explicitly set inside
// the test). The specific values assigned in this class should not be relied upon.
@Slf4j
public class TestDataGenerator {

  // Private constructor to appease Sonar
  private TestDataGenerator() {}

  /**
   * Generate a TradeAgreement builder populated with some default test values.
   */
  public static TradeAgreement.TradeAgreementBuilder defaultTradeAgreementData() {
    return TradeAgreement.builder()
        .instrument("IBM")
        .internalParty("INT-1")
        .externalParty("EXT-1")
        .buySell("B")
        .qty(100d);
  }

  /**
   * Generate a TradeAgreementProtoMessage builder populated with some default test values.
   */
  public static ProtobufMessages.TradeAgreementProtoMessage.Builder
  defaultTradeAgreementProtoMessageData() {
    return ProtobufMessages.TradeAgreementProtoMessage.newBuilder()
        .setInstrument("IBM")
        .setInternalParty("INT-1")
        .setExternalParty("EXT-1")
        .setBuySell("B")
        .setQty(100);
  }

  /**
   * Generate a SettlementMission builder populated with some default test values.
   */
  public static SettlementMission.SettlementMissionBuilder defaultSettlementMissionData() {
    return SettlementMission.builder()
        .id(100L)
        .depot("DTC")
        .externalParty("EXT-1")
        .instrument("IBM")
        .direction("REC")
        .qty(100d)
        .version(0L);
  }

  /**
   * Generate a StockPrice builder populated with some default test values.
   */
  public static StockPrice.StockPriceBuilder defaultStockPriceData() {
    Calendar calendar = Calendar.getInstance();
    calendar.set(2020, Calendar.JANUARY, 1, 0, 0, 0);
    Date date = new Date(calendar.getTimeInMillis());
    return StockPrice.builder()
        .id(100L)
        .symbol("IBM")
        .date(date)
        .prices(defaultPricesData().build());
  }

  /**
   * Generate a Prices builder with some default test values.
   */
  public static Prices.PricesBuilder defaultPricesData() {
    return Prices.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0));
  }
}
