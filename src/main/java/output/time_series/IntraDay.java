package output.time_series;

import com.msiops.ground.either.Either;
import input.time_series.Interval;
import org.joda.time.DateTime;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class IntraDay implements Response {
  private final Map<String, String> metaData;
  private final List<StockData> stocks;

  public IntraDay(Map<String, String> metaData, List<StockData> stocks) {
    this.metaData = metaData;
    this.stocks = stocks;
  }

  @Override
  public Map<String, String> getMetaData() {
    return metaData;
  }

  @Override
  public List<StockData> getStockData() {
    return stocks;
  }

  public static Either<IntraDay, Exception> from(Interval interval, String json) {
    Parser parser = new Parser(interval);
    return parser.parseJson(json);
  }

  private static class Parser extends TimeSeriesParser<IntraDay> {
    private final Interval interval;

    Parser(Interval interval) {
      this.interval = interval;
    }

    @Override
    String getStockDataKey() {
      return "Time Series (" + interval.getValue() + ")";
    }

    @Override
    IntraDay resolve(Map<String, String> metaData, Map<String, Map<String, String>> stockData) {
      List<StockData> stocks = new ArrayList<>();
      stockData.forEach((key, values) -> stocks.add(new StockData(
              DateTime.parse(key, DATE_WITH_TIME_FORMAT),
              Double.parseDouble(values.get("1. open")),
              Double.parseDouble(values.get("2. high")),
              Double.parseDouble(values.get("3. low")),
              Double.parseDouble(values.get("4. close")),
              Long.parseLong(values.get("5. volume"))
      )));
      return new IntraDay(metaData, stocks);
    }
  }

}