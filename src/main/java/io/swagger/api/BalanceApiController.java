package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;

import javax.servlet.http.HttpServletRequest;

import io.swagger.model.Order;
import io.swagger.model.RecurringOrder;
import io.swagger.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

import java.io.IOException;
import java.net.URL;

import static io.swagger.model.Order.CurrencyEnum.USD;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class BalanceApiController implements BalanceApi {
  private static final Logger log = LoggerFactory.getLogger(BalanceApiController.class);

  private final ObjectMapper objectMapper;
  private final OrderService orderService;
  private final HttpServletRequest request;
  private CoinPrices price;

  @Autowired
  public BalanceApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.orderService = orderService;
  }

  public ResponseEntity<Double> calculateOwed(
      @ApiParam(value = "Three character currency code", required = true, allowableValues = "USD, BTC, LTC, ETH") @RequestHeader(value = "currency", required = true) String currency) {
    String accept = request.getHeader("Accept");
    Order.CurrencyEnum requestedCurrency;
    try {
      requestedCurrency = Order.CurrencyEnum.valueOf(currency);
    } catch (IllegalArgumentException ex) {
      return ResponseEntity.badRequest().build();
    }

    updatePrices();
    final double orderAmount = getTotalOrderAmount(requestedCurrency);
    return ResponseEntity.ok(new Object() {
      public Order.CurrencyEnum ticker = requestedCurrency;
      public double qty = orderAmount;
    });
  }

  private double getTotalOrderAmount(Order.CurrencyEnum requestedCurrency) throws NotFoundException {
    double orderAmount = 0;
    for (Order order : orderService.getAllOneTimeOrders()) {
      orderAmount += getOrderAmountForOrder(requestedCurrency, order);
    }

    for (RecurringOrder order : orderService.getAllRecurringOrders()) {
      orderAmount += getOrderAmountForOrder(requestedCurrency, order.getOrder());
    }
    return orderAmount;
  }

  private double getOrderAmountForOrder(Order.CurrencyEnum requestedCurrency, Order order) {
    if (requestedCurrency == order.getCurrency()) {
      return price.getAmountOfCoinFor(order);
    } else if (requestedCurrency == USD) {
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
