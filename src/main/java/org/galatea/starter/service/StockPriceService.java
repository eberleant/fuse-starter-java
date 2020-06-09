package org.galatea.starter.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URL;
import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import javax.validation.Validator;
import lombok.NonNull;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.SettlementMission;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.rpsy.IStockPriceRpsy;
import org.galatea.starter.utils.Helpers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockPriceService {
  // may change to @NonNull + @RequiredArgsConstructor, see SettlementService.java
  @Autowired
  IStockPriceRpsy stockPriceRpsy;

  public List<StockPrice> findFirstStockPrices(List<StockPrice> stockPrices, int num) {
    log.info("Finding {} most recent stock prices", num);
    return stockPrices.subList(0, Math.min(stockPrices.size(), num));
  }

  public List<StockPrice> saveStockPricesIfNotExists(List<StockPrice> stockPrices) {
    log.info("Filtering out StockPrices that are already in the database.");
//    List<StockPrice> stockPricesToSave = new ArrayList<>(List.copyOf(stockPrices));
//    stockPricesToSave.removeAll(stockPriceRpsy.findBySymbol(symbol));
    List<StockPrice> stockPricesToSave = stockPrices.stream().filter(sp ->
        stockPriceRpsy.findBySymbolIgnoreCaseAndDate(sp.getSymbol(), sp.getDate()).isEmpty())
        .collect(Collectors.toList());
    log.info("Saving filtered StockPrices.");
    stockPriceRpsy.saveAll(stockPricesToSave);
    log.info("Finished.");
//    stockPricesToSave.forEach(sp -> log.info(sp.toString()));
    return stockPricesToSave;
  }

  public List<StockPrice> findStockPricesBySymbol(final String symbol) {
    log.info("Retrieving StockPrices with symbol {}", symbol);
    List<StockPrice> found = stockPriceRpsy.findBySymbolIgnoreCaseOrderByDateDesc(symbol);
    log.info("Finished.");
//    found.forEach(sp -> log.info(sp.toString()));
    return found;
  }

  public boolean hasNecessaryStockPrices(List<StockPrice> stockPrices, final int days) {
    if (days == 0) {
      log.info("Has necessary stock prices because days = 0");
      return true;
    } else if (stockPrices.isEmpty()) {
      log.info("Does not have necessary stock prices because List is empty");
      return false;
    } else {
      Date mostRecentStockPrice = stockPrices.get(0).getDate();
      Date mostRecentWeekday = Helpers.getMostRecentWeekday();
      log.info("Most recent stock price: {}", mostRecentStockPrice);
      log.info("Most recent weekday: {}", mostRecentWeekday);
      return !mostRecentWeekday.after(mostRecentStockPrice) && stockPrices.size() >= days;
    }
  }

  @SneakyThrows
  public JsonNode makeApiCall(ObjectMapper objectMapper, String apiKey, String basePath,
      final String symbol, String outputSize) {
    URL requestUrl = new URL(basePath + "/query?function=TIME_SERIES_DAILY"
        + "&symbol=" + symbol
        + "&outputsize=" + outputSize
        + "&apikey=" + apiKey);
    log.info("Making api call: {}", requestUrl.toString());
    return objectMapper.readTree(requestUrl);
  }

}
