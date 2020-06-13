package org.galatea.starter.testutils;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPriceInfo;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceInfoMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessages;

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
    LocalDate localDate = LocalDate.of(2020, 1, 1);
    return StockPrice.builder()
        .id(100L)
        .symbol("IBM")
        .date(localDate)
        .prices(defaultStockPriceInfoData().build());
  }

  /**
   * Generate a given number of StockPrice objects with the given symbol.
   * @param symbol for each StockPrice
   * @param num size of the resulting list of StockPrices
   * @return
   */
  public static List<StockPrice> generateStockPrices(String symbol, int num) {
    Random rand = new Random();
    List<StockPrice> stockPrices = new ArrayList<>();
    for (int i = 0; i < num; i++) {
      int year = rand.nextInt(30) + 1980;
      int dayOfYear = rand.nextInt(365) + 1;

      LocalDate localDate = LocalDate.ofYearDay(year, dayOfYear);

      stockPrices.add(StockPrice.builder()
          .date(localDate)
          .prices(TestDataGenerator.defaultStockPriceInfoData().build())
          .symbol(symbol)
          .id((long) i).build());
    }
    return stockPrices;
  }

  /**
   * Generate a StockPriceInfo builder with some default test values.
   */
  public static StockPriceInfo.StockPriceInfoBuilder defaultStockPriceInfoData() {
    return StockPriceInfo.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0))
        .volume(100L);
  }

  /**
   * Generate a StockPriceMessages builder with some default test values.
   */
  public static StockPriceMessages.StockPriceMessagesBuilder defaultStockPriceMessagesData() {
    return StockPriceMessages.builder()
        .data(Collections.singletonList(TestDataGenerator.defaultStockPriceMessageData().build()));
  }

  /**
   * Generate a StockPriceMessage builder with some default test values.
   */
  public static StockPriceMessage.StockPriceMessageBuilder defaultStockPriceMessageData() {
    return StockPriceMessage.builder()
        .symbol("IBM")
        .date(LocalDate.ofEpochDay(0))
        .stockInfo(TestDataGenerator.defaultStockPriceInfoMessageData().build());
  }

  /**
   * Generate a StockPriceInfoMessage builder with some default test values.
   */
  public static StockPriceInfoMessage.StockPriceInfoMessageBuilder
  defaultStockPriceInfoMessageData() {
    return StockPriceInfoMessage.builder()
        .open(new BigDecimal(0))
        .high(new BigDecimal(0))
        .low(new BigDecimal(0))
        .close(new BigDecimal(0))
        .volume(100);
  }
}
