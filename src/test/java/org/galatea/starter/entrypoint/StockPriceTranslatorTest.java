package org.galatea.starter.entrypoint;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.MessageTranslationConfig;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.TradeAgreement;
import org.galatea.starter.entrypoint.messagecontracts.ProtobufMessages.TradeAgreementProtoMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessages;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.translation.ITranslator;
import org.galatea.starter.utils.translation.TranslationException;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Import;

@Import({MessageTranslationConfig.class})
public class StockPriceTranslatorTest extends ASpringTest {

  @Autowired
  protected ITranslator<StockPriceMessage, StockPrice> stockPriceMessageTranslator;

  @Autowired
  protected ITranslator<StockPriceMessages, List<StockPrice>> stockPriceMessagesTranslator;

  @Test
  public void translateMessage() {
    StockPriceMessage message = TestDataGenerator.defaultStockPriceMessageData().build();
    StockPrice stockPrice = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build())
        .build();

    StockPrice result = stockPriceMessageTranslator.translate(message);
    assertEquals("The object produced by the translator did not match what was expected.",
        stockPrice, result);
  }

  @Test
  public void translateMessages() {
    StockPriceMessages messages = TestDataGenerator.defaultStockPriceMessagesData().build();
    StockPrice stockPrice = StockPrice.builder()
        .symbol("IBM")
        .date(new Date(0))
        .prices(TestDataGenerator.defaultStockPriceInfoData().build())
        .build();
    List<StockPrice> stockPrices = Collections.singletonList(stockPrice);

    List<StockPrice> result = stockPriceMessagesTranslator.translate(messages);
    assertEquals("The object produced by the translator did not match what was expected.",
        stockPrices, result);
  }

}
