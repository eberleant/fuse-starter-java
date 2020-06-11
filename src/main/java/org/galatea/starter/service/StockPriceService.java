package org.galatea.starter.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.exc.InvalidDefinitionException;
import java.net.URL;
import java.sql.Date;
import java.util.List;
import java.util.stream.Collectors;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
import org.galatea.starter.domain.rpsy.IStockPriceRpsy;
import org.galatea.starter.entrypoint.exception.DataNotFoundException;
import org.galatea.starter.entrypoint.messagecontracts.StockPriceMessages;
import org.galatea.starter.utils.Helpers;
import org.galatea.starter.utils.translation.ITranslator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class StockPriceService {

  @NonNull
  IStockPriceRpsy stockPriceRpsy;

  @NonNull
  ITranslator<StockPriceMessages, List<StockPrice>> stockMessagesTranslator;

  @Value("${alpha-vantage.api-key}")
  private String apiKey;

  @Value("${alpha-vantage.dailyTimeSeriesPath}")
  private String basePath;

  /**
   * Return a list (in Date descending order) containing the most recently available stock price
   * information for the given stock symbol and number of days.
   * @param symbol stock symbol to get stock price information about
   * @param days number of days to get stock price information for
   * @return
   */
  public List<StockPrice> getStockPrices(final String symbol, final int days) {
    // make db call
    List<StockPrice> stockPrices = findStockPricesBySymbol(symbol);

    // make api call to AlphaVantage if necessary
    if (!hasNecessaryStockPrices(stockPrices, days)) {
      // note: if db doesn't have all necessary stock prices, then the records retrieved from db
      // are not used *at all* (all data comes from API)
      StockPriceMessages
          result = makeApiCall(symbol, (days > 100 ? "full" : "compact"));
      stockPrices = stockMessagesTranslator.translate(result);
      // store result of api call in db
      log.info("Most recent stock price to save: {}", stockPrices.get(0).getDate());
      saveStockPricesIfNotExists(stockPrices);
    }

    // filter to only necessary stock prices
    return findFirstStockPrices(stockPrices, days);
  }

  /**
   * Return a sublist of the given list of StockPrice objects with the given size.
   * If not possible (eg, the given list has size < given size), return the entire list.
   * @param stockPrices list of StockPrice objects
   * @param size size of the list to return
   * @return
   */
  public List<StockPrice> findFirstStockPrices(final List<StockPrice> stockPrices, final int size) {
    log.info("Finding {} most recent stock prices", size);
    return stockPrices.subList(0, Math.min(stockPrices.size(), size));
  }

  /**
   * Given a list of StockPrice objects, for each object, save it only if it does not already
   * exist in the database.
   * @param stockPrices list of StockPrice objects to possibly save
   * @return
   */
  public List<StockPrice> saveStockPricesIfNotExists(final List<StockPrice> stockPrices) {
    log.info("Filtering out StockPrices that are already in the database.");
    // List<StockPrice> stockPricesToSave = new ArrayList<>(List.copyOf(stockPrices));
    // stockPricesToSave.removeAll(stockPriceRpsy.findBySymbol(symbol));
    List<StockPrice> stockPricesToSave = stockPrices.stream().filter(sp ->
        stockPriceRpsy.findBySymbolIgnoreCaseAndDate(sp.getSymbol(), sp.getDate()).isEmpty())
        .collect(Collectors.toList());
    log.info("Saving filtered StockPrices.");
    stockPriceRpsy.saveAll(stockPricesToSave);
    log.info("Finished.");
    return stockPricesToSave;
  }

  /**
   * Return all StockPrice records from the database with the given symbol.
   * @param symbol stock symbol
   * @return
   */
  public List<StockPrice> findStockPricesBySymbol(final String symbol) {
    log.info("Retrieving StockPrices with symbol {}", symbol);
    List<StockPrice> found = stockPriceRpsy.findBySymbolIgnoreCaseOrderByDateDesc(symbol);
    log.info("Finished.");
    return found;
  }

  /**
   * Return true if the given list has all necessary StockPrice objects to be considered a full
   * answer to a request for the *days* most recent stock prices.
   * @param stockPrices list of StockPrice objects
   * @param days number of days to get stock price information about
   * @return
   */
  public boolean hasNecessaryStockPrices(final List<StockPrice> stockPrices, final int days) {
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

  /**
   * Make an API call to Alpha Vantage's TIME_SERIES_DAILY API using the given parameters,
   * and return the resulting de-serialized StockPriceMessages object.
   * Alpha Vantage API documentation: https://www.alphavantage.co/documentation/
   * @param symbol stock symbol
   * @param outputSize outputsize parameter (full or compact): see Alpha Vantage documentation
   * @return
   */
  @SneakyThrows
  public StockPriceMessages makeApiCall(final String symbol, final String outputSize) {
    String urlString = basePath
        + "&symbol=" + symbol
        + "&outputsize=" + outputSize
        + "&apikey=" + apiKey;
    URL url = new URL(urlString);
    log.info("Making api call: {}", urlString.substring(0, urlString.lastIndexOf('&')));
    try {
      return (new ObjectMapper()).readValue(url, StockPriceMessages.class);
    } catch (InvalidDefinitionException ide) {
      throw new DataNotFoundException(symbol);
    }
  }
}
