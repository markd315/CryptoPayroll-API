package cryptoroll.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import cryptoroll.model.Order;
import cryptoroll.services.UltiOrderService;
import java.io.IOException;
import java.net.URL;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class BalanceApiController implements BalanceApi {
  private static final Logger log = LoggerFactory.getLogger(BalanceApiController.class);

  private final ObjectMapper objectMapper;
  private final UltiOrderService orderService;
  private final HttpServletRequest request;
  private CoinPrices price;

  @Autowired
  public BalanceApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService orderService) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.orderService = orderService;
  }

  public ResponseEntity<Double> calculateOwed(
      @ApiParam(value = "Three character currency code", required = true, allowableValues = "USD, BTC, LTC, ETH") @RequestHeader(value = "currency", required = true) String currency)
      throws NotFoundException {
    String accept = request.getHeader("Accept");
    Order.CurrencyEnum requestedCurrency;
    try {
      requestedCurrency = Order.CurrencyEnum.valueOf(currency);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().build();
    }

    updatePrices();
    final double orderAmount = getTotalOrderAmountForCurrency(requestedCurrency);
    return ResponseEntity.ok(orderAmount);
  }

  public double getTotalOrderAmountForCurrency(Order.CurrencyEnum requestedCurrency) {
    try {
      double orderAmount = 0;
      for (Order order : orderService.getAllOneTimeOrders()) {
        orderAmount += getOrderAmountForOrder(requestedCurrency, order);
      }

      for (Order order : orderService.getAllRecurringOrders()) {
          orderAmount += getOrderAmountForOrder(requestedCurrency, order);
      }

      return orderAmount;
    } catch (NotFoundException e) {
      throw new RuntimeException(e);
    }
  }

  private double getOrderAmountForOrder(Order.CurrencyEnum requestedCurrency, Order order) {
    if (requestedCurrency == order.getCurrency() || requestedCurrency == Order.CurrencyEnum.USD) {
      return order.getQuantity();
    }

    return 0;
  }

  private void updatePrices() {
    try {
      price = objectMapper.readValue(new URL("https://min-api.cryptocompare.com/data/price?fsym=USD&tsyms=BTC,ETH,LTC"), CoinPrices.class);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
  }

  private static final class CoinPrices {
    public double BTC;
    public double LTC;
    public double ETH;

    public double getAmountOfCoinFor(Order order) {
      return getAmountOfCoinFor(order.getCurrency(), order.getQuantity());
    }

    public double getAmountOfCoinFor(Order.CurrencyEnum currency, double qty) {
      switch (currency) {
        case BTC:
          return qty * BTC;
        case ETH:
          return qty * ETH;
        case LTC:
          return qty * LTC;
        default:
          throw new IllegalArgumentException(currency.name() + " is unknown");
      }
    }
  }
}

