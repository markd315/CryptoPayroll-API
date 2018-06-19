package io.swagger.api;

import com.coinbase.exchange.api.accounts.Account;
import com.coinbase.exchange.api.accounts.AccountService;
import com.coinbase.exchange.api.deposits.DepositService;
import com.coinbase.exchange.api.entity.NewLimitOrderSingle;
import com.coinbase.exchange.api.entity.NewOrderSingle;
import com.coinbase.exchange.api.marketdata.MarketData;
import com.coinbase.exchange.api.marketdata.MarketDataService;
import com.coinbase.exchange.api.marketdata.OrderItem;
import com.coinbase.exchange.api.orders.OrderService;
import com.coinbase.exchange.api.payments.PaymentService;
import com.coinbase.exchange.api.withdrawals.WithdrawalsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.OneTimeOrder;
import io.swagger.model.Order;
import io.swagger.model.RecurringOrder;
import io.swagger.services.UltiOrderService;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class ExecuteApiController implements ExecuteApi {

  private static final Logger log = LoggerFactory.getLogger(ExecuteApiController.class);

  private ObjectMapper objectMapper;

  private HttpServletRequest request;

  @Autowired
  private UltiOrderService service;
  @Autowired
  private BalanceApi balanceApiController;
  @Autowired
  private QueueApi queueApiController;
  @Autowired
  private AccountService accountService;
  @Autowired
  private PaymentService paymentService;
  @Autowired
  private DepositService depositService;
  @Autowired
  private MarketDataService marketDataService;
  @Autowired
  private OrderService orderService;
  @Autowired
  private WithdrawalsService withdrawalsService;

  private List<NewOrderSingle> ourOpenOrders = new ArrayList<NewOrderSingle>();

  @Autowired
  public ExecuteApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService service) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = service;
  }


  public ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true) @RequestHeader(value = "code", required = true) String code) {
    List<OneTimeOrder> oneTime = null;
    ourOrderIsUnfilled();
    try {
      oneTime = service.getAllOneTimeOrders();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    List<RecurringOrder> toExtractSingle = null;
    try {
      toExtractSingle = service.getAllRecurringOrders();
    } catch (Exception e1) {
      e1.printStackTrace();
    }

    for (RecurringOrder recurring : toExtractSingle) {
      if (recurring.getCyclesSinceLast() + 1 == recurring.getCyclePeriod()) { //If we are executing payroll for this recurringOrder
        oneTime.add(new OneTimeOrder(recurring.getId(), recurring.getQuantity(), recurring.getDestination(), recurring.getCurrency(),
            recurring.getDestinationType(),
            false, false)); //add it to the ones we are executing.
      }
    }

    service.incrementOrResetAllRecurringOrders();

    double amountWeOwePayees = balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.USD);

    double usdWeOwn = queryAccountBalance();

    double toPurchaseForCycle = (1.1 * amountWeOwePayees - usdWeOwn);
    placeUSDDespoit(toPurchaseForCycle);

    //account loaded with cash
    double[]
        owed =
        {balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.BTC),
            balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.ETH),
            balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.LTC)};
    String[] currCodes = {Order.CurrencyEnum.BTC.toString(), Order.CurrencyEnum.ETH.toString(), Order.CurrencyEnum.LTC.toString()};
    for (int i = 0; i < 3; i++) {
      orderCurrencyProtocol(owed[i], currCodes[i]);
    }

    //Order for money to buy the crypto is filled, just nest for all cryptocurrencies, checking for 0.
    List<OneTimeOrder> ordersToFill = oneTime;

    for (Order order : ordersToFill) {
      payAmountToWallet(order.getQuantity(), order.getDestination(), order.getCurrency(), order.getDestinationType());
    }

    //access services like this!
    //String paymentTypeId = paymentService.getPaymentTypes().get(0).getId();
    //PaymentResponse res = depositService.depositViaPaymentMethod(new BigDecimal(usdToPurchase), "USD", paymentTypeId);
    //TODO npe next line

    //TODO a bunch of summation logic, then a bunch of API hits, as shown.
    //TODO after we successfully order each type of the crypto, release it to its owners.
    /*JSONObject body = new JSONObject("{\n"
        + "    \"amount\": 10.00,\n"
        + "    \"currency\": \"USD\",\n"
        + "    \"payment_method_id\": \"bc677162-d934-5f1a-968c-a496b1c1270b\"\n"
        + "}");

    JSONObject res = null;
    try {
      res = } catch (IOException e) {
      e.printStackTrace();
    } catch (URISyntaxException e) {
      e.printStackTrace();
    }
    System.out.println(res.toString());
    service.incrementOrResetAllRecurringOrders();
    try {
      service.wipeAllOneTimeOrders();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    */
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  private void orderCurrencyProtocol(double toPurchaseForCycle, String currencyCode) {
    placeOrderForUsdAmount(toPurchaseForCycle, Order.CurrencyEnum.fromValue(currencyCode));
    double amountOrderFilledFor = Double.MAX_VALUE;
    while (toPurchaseForCycle > 0) {
      try {
        Thread.sleep(334);//Strictest rate limit is 3 per second
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      amountOrderFilledFor = cancelOrderForUsdReturnAmountAlreadySpent();
      toPurchaseForCycle -= amountOrderFilledFor;
      placeOrderForUsdAmount(toPurchaseForCycle, Order.CurrencyEnum.fromValue(currencyCode));
    }
  }

  private double cancelOrderForUsdReturnAmountAlreadySpent() {
    //TODO
    return 0.0;
  }

  private boolean ourOrderIsUnfilled() {
    try {
      Thread.sleep(334);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    List<com.coinbase.exchange.api.orders.Order> openOrders = orderService.getOpenOrders();
    if (openOrders.isEmpty()) {
      return false;
    }
    return true;
  }

  //Bank->USD
  private void placeUSDDespoit(double toPurchaseForCycle) {
    //TODO
  }

  //USD->Crypto
  private void placeOrderForUsdAmount(double toPurchaseForCycle, Order.CurrencyEnum currencyEnum) {
    //TODO
    double cryptoQuote = gdaxAskForPrice(currencyEnum);
    BigDecimal price = new BigDecimal(cryptoQuote);
    price.setScale(2, BigDecimal.ROUND_FLOOR); //We want to undercut the market price by one cent.
    BigDecimal toPayUSD = new BigDecimal(toPurchaseForCycle);
    BigDecimal sizeBTC = toPayUSD.divide(price);
    NewLimitOrderSingle ourOrder = new NewLimitOrderSingle(sizeBTC, price, Boolean.TRUE);//Post_only
    orderService.createOrder(ourOrder);
    ourOpenOrders.add(ourOrder); //TODO do we really need this?
    //Use NewLimitOrderSingle
    //Make sure that we only make one request per call of this method, or that we use Thread.sleep(334) between calls.
    //Make sure we place this order as a LIMIT BUY order SLIGHTLY under the market price, no fill-or-kill, no expiry.
    //Make sure that we only make one request per call, or that we use Thread.sleep(334) between calls.
  }

  private double queryAccountBalance() {
    List<Account> accs = accountService.getAccounts();
    double usdAccountBalance = 0.0;
    for (Account acc : accs) { //Find the USD account.
      if (acc.getCurrency().equalsIgnoreCase("USD")) {
        usdAccountBalance += acc.getBalance().doubleValue();
      }
    }
    //Make sure that we only make one request per call of this method, or that we use Thread.sleep(334) between calls.
    return usdAccountBalance;
  }

  private void payAmountToWallet(double toPay, String address, OneTimeOrder.CurrencyEnum currency, OneTimeOrder.DestinationTypeEnum destinationType) {
    if (destinationType == Order.DestinationTypeEnum.WALLET) {
      withdrawalsService.makeWithdrawalToCryptoAccount(new BigDecimal(toPay), currency.toString(), address);
    } else if (destinationType == Order.DestinationTypeEnum.COINBASE) {
      withdrawalsService.makeWithdrawalToCoinbase(new BigDecimal(toPay), currency.toString(), address);
    }
  }

  private double gdaxAskForPrice(OneTimeOrder.CurrencyEnum currency) {
    //Use MarketDataService highest BID.
    MarketData data = marketDataService.getMarketDataOrderBook(currency.toString(), "1");
    List<OrderItem> bids = data.getBids();
    OrderItem highestBid = null;
    for (OrderItem bid : bids) {
      if (highestBid == null | bid.getPrice().doubleValue() > highestBid.getPrice().doubleValue()) {
        highestBid = bid;
      }
    }
    return highestBid.getPrice().doubleValue();
  }
}

