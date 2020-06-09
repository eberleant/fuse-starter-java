package org.galatea.starter;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.Prices;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.utils.Helpers;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
public class MessageTranslationConfig {

  /**
   * Returns a translator to convert SettlementMissions to protobuf messages.
   */
  @Bean
  public ITranslator<SettlementMission, SettlementMissionMessage> settlementMissionTranslator() {
    return mission -> SettlementMissionMessage.builder()
        .id(mission.getId())
        .instrument(mission.getInstrument())
        .externalParty(mission.getExternalParty())
        .direction(mission.getDirection())
        .depot(mission.getDepot())
        .qty(mission.getQty())
        .version(mission.getVersion()).build();
  }

  /**
   * Returns a translator to convert SettlementMissionMessages to SettlementMissions.
   */
  @Bean
  public ITranslator<SettlementMissionMessage, SettlementMission> settlementMissionMsgTranslator() {
    return message -> SettlementMission.builder()
        .id(message.getId())
        .instrument(message.getInstrument())
        .externalParty(message.getExternalParty())
        .direction(message.getDirection())
        .depot(message.getDepot())
        .qty(message.getQty())
        .version(message.getVersion()).build();
  }

  /**
   * Returns a translator to convert protobuf messages to TradeAgreements.
   */
  @Bean
  public ITranslator<TradeAgreementMessage, TradeAgreement> tradeAgreementMessageTranslator() {
    return message -> TradeAgreement.builder()
        .buySell(message.getBuySell())
        .externalParty(message.getExternalParty())
        .instrument(message.getInstrument())
        .internalParty(message.getInternalParty())
        .qty(message.getQty()).build();
  }

  /**
   * Returns a translator to convert protobuf messages to a list of TradeAgreements.
   */
  @Bean
  public ITranslator<TradeAgreementMessages, List<TradeAgreement>> tradeAgreementMessagesTranslator(
      final ITranslator<TradeAgreementMessage, TradeAgreement> translator) {
    return messages -> messages.getAgreements().stream().map(translator::translate)
        .collect(Collectors.toList());
  }

  /**
   * Returns a translator to convert a JsonNode from Alpha Vantage's TIME_SERIES_DAILY API call
   * to a list of StockPrices.
   */
  @Bean
  public ITranslator<JsonNode, List<StockPrice>> timeSeriesJsonTranslator(
      @Value("${alpha-vantage.metadata-key}") final String metadataKey,
      @Value("${alpha-vantage.symbol-key}") final String symbolKey,
      @Value("${alpha-vantage.daily-time-series-key}") final String timeSeriesKey,
      @Value("${alpha-vantage.open-price-key}") final String openPriceKey,
      @Value("${alpha-vantage.high-price-key}") final String highPriceKey,
      @Value("${alpha-vantage.low-price-key}") final String lowPriceKey,
      @Value("${alpha-vantage.close-price-key}") final String closePriceKey) {
    return timeSeriesJson -> {
      // get stock symbol
      String symbol = timeSeriesJson.get(metadataKey).get(symbolKey).textValue().toUpperCase();
      List<StockPrice> stockPrices = new ArrayList<>();
      Iterator<Entry<String, JsonNode>> timeSeriesIter = timeSeriesJson.get(timeSeriesKey).fields();
      // build a StockPrice object for each entry in timeSeriesIter
      while (timeSeriesIter.hasNext()) {
        Map.Entry<String, JsonNode> timeSeriesEntry = timeSeriesIter.next();
        stockPrices.add(
            StockPrice.builder()
                .date(Helpers.stringToDate(timeSeriesEntry.getKey()))
                .prices(Prices.builder()
                    .open(new BigDecimal(timeSeriesEntry.getValue().get(openPriceKey).textValue()))
                    .high(new BigDecimal(timeSeriesEntry.getValue().get(highPriceKey).textValue()))
                    .low(new BigDecimal(timeSeriesEntry.getValue().get(lowPriceKey).textValue()))
                    .close(new BigDecimal(timeSeriesEntry.getValue().get(closePriceKey).textValue()))
                    .build())
                .symbol(symbol).build());
      }
      log.info("Translated JsonNode into {} StockPrices, symbol {}", stockPrices.size(), symbol);
      return stockPrices;
    };
  }
}
