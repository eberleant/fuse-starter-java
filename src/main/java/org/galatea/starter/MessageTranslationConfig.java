package org.galatea.starter;

import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.StockPriceInfo;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.SettlementMissionMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceInfoMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessages;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessage;
import org.galatea.starter.entrypoint.messagecontracts.TradeAgreementMessages;
import org.galatea.starter.utils.translation.ITranslator;
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
   * Returns a translator to convert a StockInfoMessage to a Prices object.
   */
  @Bean
  public ITranslator<StockPriceInfoMessage, StockPriceInfo> stockPriceInfoMessageTranslator() {
    return message -> StockPriceInfo.builder()
        .open(message.getOpen())
        .high(message.getHigh())
        .low(message.getLow())
        .close(message.getClose())
        .volume(message.getVolume()).build();
  }

  /**
   * Returns a translator to convert a StockMessage to a StockPrice.
   */
  @Bean
  public ITranslator<StockPriceMessage, StockPrice> stockPriceMessageTranslator(
      final ITranslator<StockPriceInfoMessage, StockPriceInfo> translator
  ) {
    return message -> StockPrice.builder()
        .symbol(message.getSymbol())
        .date(message.getDate())
        .prices(translator.translate(message.getStockInfo()))
        .build();
  }

  /**
   * Returns a translator to convert a StockMessages object to a list of StockPrices.
   */
  @Bean
  public ITranslator<StockPriceMessages, List<StockPrice>> stockPriceMessagesTranslator(
      final ITranslator<StockPriceMessage, StockPrice> translator) {
    return messages -> messages.getData().stream().map(translator::translate)
        .collect(Collectors.toList());
  }
}
