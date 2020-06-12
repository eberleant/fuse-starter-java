package org.galatea.starter.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import com.fasterxml.jackson.databind.exc.UnrecognizedPropertyException;
import com.fasterxml.jackson.databind.node.ObjectNode;
import java.math.BigDecimal;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.ASpringTest;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceInfoMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessage;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessages;
import org.galatea.starter.testutils.TestDataGenerator;
import org.galatea.starter.utils.Helpers;
import org.galatea.starter.utils.translation.TranslationException;
import org.junit.BeforeClass;
import org.junit.Test;

@Slf4j
public class StockPriceMessagesTest extends ASpringTest {

  private static Validator validator;

  @BeforeClass
  public static void setUp() {
    ValidatorFactory factory = Validation.buildDefaultValidatorFactory();
    validator = factory.getValidator();
  }

  /**
   * Valid StockPriceMessages has no errors.
   */
  @Test
  public void validStockPriceMessages() {
    List<StockPriceMessage> data = new ArrayList<>();
    for (int i = 0; i < 3; i++) {
      data.add(TestDataGenerator.defaultStockPriceMessageData().build());
    }
    StockPriceMessages spm = StockPriceMessages.builder()
        .data(data).build();

    Set<ConstraintViolation<StockPriceMessages>> constraintViolations = validator.validate(spm);

    assertEquals(0, constraintViolations.size());
  }

  /**
   * Deserialization of valid JSON into StockPriceMessages object works.
   */
  @SneakyThrows
  @Test
  public void validStockPriceMessagesFromJson() {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode metadata = mapper.createObjectNode();
    metadata.put("2. Symbol", "IBM");

    ObjectNode dataNode = mapper.createObjectNode();
    dataNode.put("1. open", "0.00");
    dataNode.put("2. high", "0.00");
    dataNode.put("3. low", "0.00");
    dataNode.put("4. close", "0.00");
    dataNode.put("5. volume", "100");

    ObjectNode data = mapper.createObjectNode();
    data.set(new Date(0).toString(), dataNode);

    ObjectNode root = mapper.createObjectNode();
    root.set("Meta Data", metadata);
    root.set("Time Series (Daily)", data);

    StockPriceMessages result = mapper.readValue(mapper.writeValueAsString(root),
        StockPriceMessages.class);

    StockPriceMessage stockPriceMessage = StockPriceMessage.builder()
        .symbol("IBM")
        .date(new Date(0))
        .stockInfo(StockPriceInfoMessage.builder()
            .open(new BigDecimal("0.00"))
            .high(new BigDecimal("0.00"))
            .low(new BigDecimal("0.00"))
            .close(new BigDecimal("0.00"))
            .volume(100).build()).build();

    StockPriceMessages stockPriceMessages = StockPriceMessages.builder()
        .data(Collections.singletonList(stockPriceMessage)).build();

    assertEquals("De-serializing JSON into StockPriceMessages object did not produce expected result",
        stockPriceMessages, result);
  }

  /**
   * Deserialization of valid JSON into StockPriceMessages object works.
   */
  @SneakyThrows
  @Test
  public void emptyStockPriceMessagesFromJson() {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode metadata = mapper.createObjectNode();
    metadata.put("2. Symbol", "IBM");

    ObjectNode data = mapper.createObjectNode();

    ObjectNode root = mapper.createObjectNode();
    root.set("Meta Data", metadata);
    root.set("Time Series (Daily)", data);

    StockPriceMessages result = mapper.readValue(mapper.writeValueAsString(root),
        StockPriceMessages.class);

    StockPriceMessages stockPriceMessages = StockPriceMessages.builder()
        .data(new ArrayList<>()).build();

    assertEquals("De-serializing JSON into StockPriceMessages object did not produce expected result",
        stockPriceMessages, result);
  }

  /**
   * Deserialization of invalid JSON into StockPriceMessages object throws TranslationException.
   */
  @SneakyThrows
  @Test(expected = InvalidDefinitionException.class)
  public void invalidStockPriceMessagesFromJsonNoMetadata() {
    ObjectMapper mapper = new ObjectMapper();

//    ObjectNode metadata = mapper.createObjectNode();
//    metadata.put("2. Symbol", "IBM");

    ObjectNode dataNode = mapper.createObjectNode();
    dataNode.put("1. open", "0.00");
    dataNode.put("2. high", "0.00");
    dataNode.put("3. low", "0.00");
    dataNode.put("4. close", "0.00");
    dataNode.put("5. volume", "100");

    ObjectNode data = mapper.createObjectNode();
    data.set(new Date(0).toString(), dataNode);

    ObjectNode root = mapper.createObjectNode();
//    root.set("Meta Data", metadata);
    root.set("Time Series (Daily)", data);

    mapper.readValue(mapper.writeValueAsString(root),
        StockPriceMessages.class);

    assertTrue("Should have thrown TranslationException", false);
  }

  @SneakyThrows
  @Test(expected = InvalidDefinitionException.class)
  public void invalidStockPriceMessagesFromJsonNoSymbol() {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode metadata = mapper.createObjectNode();
//    metadata.put("2. Symbol", "IBM");

    ObjectNode dataNode = mapper.createObjectNode();
    dataNode.put("1. open", "0.00");
    dataNode.put("2. high", "0.00");
    dataNode.put("3. low", "0.00");
    dataNode.put("4. close", "0.00");
    dataNode.put("5. volume", "100");

    ObjectNode data = mapper.createObjectNode();
    data.set(new Date(0).toString(), dataNode);

    ObjectNode root = mapper.createObjectNode();
    root.set("Meta Data", metadata);
    root.set("Time Series (Daily)", data);

    mapper.readValue(mapper.writeValueAsString(root),
        StockPriceMessages.class);

    assertTrue("Should have thrown TranslationException", false);
  }

  @SneakyThrows
  @Test(expected = InvalidDefinitionException.class)
  public void invalidStockPriceMessagesFromJsonNoData() {
    ObjectMapper mapper = new ObjectMapper();

    ObjectNode metadata = mapper.createObjectNode();
    metadata.put("2. Symbol", "IBM");

    ObjectNode root = mapper.createObjectNode();
    root.set("Meta Data", metadata);

    mapper.readValue(mapper.writeValueAsString(root),
        StockPriceMessages.class);

    assertTrue("Should have thrown TranslationException", false);
  }

}
