package org.galatea.starter.domain.rpsy;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.galatea.starter.domain.StockPrice;
import org.springframework.data.repository.CrudRepository;

public interface IStockPriceRpsy extends CrudRepository<StockPrice, Long> {

  /**
   * Return a list of StockPrice objects from the database with the given symbol and date.
   * The symbol is case insensitive. Ideally, this should return a list with only one object.
   * @param symbol stock symbol of all returned StockPrice objects
   * @param date date of all returned StockPrice objects
   * @return
   */
  List<StockPrice> findBySymbolIgnoreCaseAndDate(String symbol, Date date);

  /**
   * Return a list of all StockPrice objects in the database with the given symbol, sorted by
   * date descending. The symbol is case insensitive.
   * @param symbol stock symbol of all returned StockPrice objects
   * @return
   */
  List<StockPrice> findBySymbolIgnoreCaseOrderByDateDesc(String symbol);

  /**
   * Return a StockPrice objects (if one exists) with the given ID.
   * @param id id of the StockPrice object to return
   * @return
   */
  Optional<StockPrice> findById(long id);
}
