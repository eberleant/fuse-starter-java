package org.galatea.starter.service;

import java.sql.Date;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import lombok.extern.slf4j.Slf4j;
import org.galatea.starter.domain.StockPrice;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class StockPriceService {
  public List<StockPrice> findMostRecentStockPrices(int days, List<StockPrice> stockPrices) {
    return stockPrices.subList(0, Math.min(stockPrices.size(), days));
  }

}
