package org.galatea.starter.domain.rpsy;

import java.sql.Date;
import java.util.List;
import java.util.Optional;
import org.galatea.starter.domain.StockPrice;
import org.springframework.data.repository.CrudRepository;

public interface IStockPriceRpsy extends CrudRepository<StockPrice, Long> {
  List<StockPrice> findBySymbol(String symbol);
  List<StockPrice> findBySymbolAndDate(String symbol, Date date);
  List<StockPrice> findBySymbolOrderByDateDesc(String symbol);
  Optional<StockPrice> findById(long id);
}
