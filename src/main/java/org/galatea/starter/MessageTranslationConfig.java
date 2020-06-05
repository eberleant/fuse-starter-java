package org.galatea.starter;

import com.fasterxml.jackson.databind.JsonNode;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
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
   * Returns a translator to convert a Map.Entry<String, JsonNode> to a StockPrice.
   * Key: String representation of a date (yyyy-MM-dd).
   * Value: String representations of the stock prices throughout the day and volume.
   */
//  @Bean
//  public ITranslator<Map.Entry<String, JsonNode>, StockPrice> timeSeriesEntryTranslator() {
//    return message -> StockPrice.builder()
//        .date(Helpers.stringToDate(message.getKey()))
//        .price(new BigDecimal(message.getValue().get(priceKey).textValue()))
//        .stock(stock).build();
//  }

  /**
   * Returns a translator to convert a JsonNode from Alpha Vantage's TIME_SERIES_DAILY API call
   * to a list of StockPrices.
   */
//  @Bean
//  public ITranslator<JsonNode, List<StockPrice>> timeSeriesJsonTranslator(
//      final ITranslator<Map.Entry<String, JsonNode>, StockPrice> translator,
//      @Value("${alpha-vantage.price-key}") String priceKey) {
//    return messages -> {
//      // get stock symbol
//      List<Map.Entry<String, JsonNode>> entryList = new ArrayList<>();
//      messages.fields().forEachRemaining(entryList::add);
//      return entryList.stream().map(translator::translate).collect(Collectors.toList());
//    };
//  }
}
