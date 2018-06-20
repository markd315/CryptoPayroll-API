package io.swagger.api;

import com.coinbase.exchange.api.accounts.Account;
import com.coinbase.exchange.api.accounts.AccountService;
import com.coinbase.exchange.api.deposits.DepositService;
import com.coinbase.exchange.api.entity.NewLimitOrderSingle;
import com.coinbase.exchange.api.entity.PaymentResponse;
import com.coinbase.exchange.api.marketdata.MarketData;
import com.coinbase.exchange.api.marketdata.MarketDataService;
import com.coinbase.exchange.api.marketdata.OrderItem;
import com.coinbase.exchange.api.orders.OrderService;
import com.coinbase.exchange.api.payments.CoinbaseAccount;
import com.coinbase.exchange.api.payments.PaymentService;
import com.coinbase.exchange.api.withdrawals.WithdrawalsService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.OneTimeOrder;
import io.swagger.model.Order;
import io.swagger.model.RecurringOrder;
import io.swagger.services.UltiOrderService;
import java.math.BigDecimal;
import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;
import javax.naming.InsufficientResourcesException;
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

  @Autowired
  public ExecuteApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService service) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = service;
  }

  public ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true) @RequestHeader(value = "code", required = true) String code) {
    List<Order> ordersToFill = new ArrayList();
    try {
      ordersToFill.addAll(service.getAllOneTimeOrders());
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
        ordersToFill.add(recurring); //add it to the ones we are executing.
      }
    }

    double amountWeOwePayees = balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.USD);

    double usdWeOwn = queryAccountBalance();

    double toPurchaseForCycle = (1.1 * amountWeOwePayees - usdWeOwn);
    try {
      placeUSDDespoit(toPurchaseForCycle);
    } catch (InsufficientResourcesException e1) {
      e1.printStackTrace();
    }
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

    for (Order order : ordersToFill) {
      payAmountToWallet(order.getQuantity(), order.getDestination(), order.getCurrency(), order.getDestinationType());
    }
    service.incrementOrResetAllRecurringOrders(); //Do this down here so we don't forget to pay people.
    try {
      service.wipeAllOneTimeOrders();
    } catch (NotFoundException e) {
      e.printStackTrace();
    }
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

  private void orderCurrencyProtocol(double toPurchaseForCycle, String currencyCode) {
    if (toPurchaseForCycle < .01) {
      return;
    }
    double amountOrderFilledFor = Double.MAX_VALUE;
    while (toPurchaseForCycle > 10) { //TODO reconsider this constant 10. But with 0, if we get very-nearly-complete
      placeOrderForUsdAmount(toPurchaseForCycle, Order.CurrencyEnum.fromValue(currencyCode));
      //TODO fills we might have a tiny amount remaining to buy and our small buys will be REJECTED by GDAX. This is a workaround.
      try {
        Thread.sleep(500);//Strictest rate limit is 3 per second
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      //Wait 1/2 second before cancelling request.
      try {
        amountOrderFilledFor = cancelOrderForUsdReturnAmountAlreadySpent();
        if (amountOrderFilledFor < 0.0) {
          amountOrderFilledFor = 0.0;//Forbid negative
        }
      } catch (UnexpectedException e) {
        e.printStackTrace();
      }
      toPurchaseForCycle -= amountOrderFilledFor;
      if (toPurchaseForCycle < 0.0) {
        toPurchaseForCycle = 0.0; //Forbid negative
      }
    }
  }

  //Not concurrently safe to be used for multiple open orders.
  private double cancelOrderForUsdReturnAmountAlreadySpent() throws UnexpectedException {
    try {
      Thread.sleep(334);
    } catch (InterruptedException e) {
      e.printStackTrace();
    }
    List<com.coinbase.exchange.api.orders.Order> openOrders = orderService.getOpenOrders();
    if (openOrders.size() > 1) {
      throw new UnexpectedException("Cryptoroll API currently only supports one open order, please order your coins one at a time.");
    }
    if (openOrders.isEmpty()) {
      return 999999999999999.99;//We have filled the order.
    }
    com.coinbase.exchange.api.orders.Order order = openOrders.get(0);
    double filledAmount = Double.valueOf(order.getFilled_size()) * Double.valueOf(order.getPrice());
    orderService.cancelOrder(order.getId());
    /*for (com.coinbase.exchange.api.orders.Order order : openOrders) {
      filledAmount += order.getFilled_size()/order.getSize();
    }*/

    //We don't want to do the above because we would need to distinguish between which cryptocurrency orders we are looking at.
    //For now, constrain to one order.

    return filledAmount;
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

  //TODO Wire transfers are slow. Unless we're going to hold crypto,
  //TODO we might need to break this into two seperate execution stages over several hours/days, one for loading USD and another for paying out.
  //Bank->USD
  private void placeUSDDespoit(double toPurchaseForCycle) throws InsufficientResourcesException {
    if (toPurchaseForCycle <= 0) {
      return;
    }

    List<CoinbaseAccount> pmts = paymentService.getCoinbaseAccounts();
    CoinbaseAccount usdDepositer = null;
//    for (PaymentType pmt : pmts) {
//      if (pmt.getCurrency().equalsIgnoreCase("USD")) {
//        usdDepositer = pmt;
//      }
//    }
    usdDepositer = paymentService.getCoinbaseAccounts().get(4);
    // usdDepositer = paymentService.getPaymentTypes().get(3);
    /*
    PaymentType{id='b22911ee-ef35-5c97-bdd4-aef3f65618d9', type='fiat_account', name='GBP Wallet', currency='GBP', primary_buy=false, primary_sell=false, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@4f99769a}
    PaymentType{id='e49c8d15-547b-464e-ac3d-4b9d20b360ec', type='fiat_account', name='USD Wallet', currency='USD', primary_buy=false, primary_sell=false, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@196c6487}
    PaymentType{id='ec3c2e04-e877-4c21-b6d2-1f26744c00c3', type='fiat_account', name='EUR Wallet', currency='EUR', primary_buy=false, primary_sell=false, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@ec949f6}
    PaymentType{id='123bbe6f-28c3-4e47-9be5-300b628f80a0', type='bank_wire', name='Bank Wire The Toronto-Dominion Bank ******2778', currency='USD', primary_buy=false, primary_sell=false, allow_buy=false, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@2bb77ffb}
    PaymentType{id='6a23926d-74b6-4373-8434-9d437c2bafb2', type='ach_bank_account', name='TD Bank ******2778', currency='USD', primary_buy=true, primary_sell=true, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@2f9ef1b1}
     */
//    double accountLimit = usdDepositer.getLimits().getDeposit()[0].getRemaining().getAmount().doubleValue();
//    if (accountLimit < toPurchaseForCycle) {
//      throw new InsufficientResourcesException("Not enough payment method limit to order USD");
//    }
    PaymentResponse res = depositService.depositViaCoinbase(BigDecimal.valueOf(2), "USD", usdDepositer.getId());
    log.error(res.toString());
  }

  //USD->Crypto
  private void placeOrderForUsdAmount(double toPurchaseForCycle, Order.CurrencyEnum currencyEnum) {
    //TODO
    double cryptoQuote = gdaxAskForPrice(currencyEnum);
    BigDecimal price = new BigDecimal(cryptoQuote);
    price.setScale(2, BigDecimal.ROUND_FLOOR); //We want to undercut the market price by one cent.
    BigDecimal toPayUSD = new BigDecimal(toPurchaseForCycle);
    BigDecimal sizeBTC = toPayUSD.divide(price, 8, BigDecimal.ROUND_UP);//GDAX can handle 8 decimal points
    NewLimitOrderSingle
        ourOrder =
        new NewLimitOrderSingle(sizeBTC, price.setScale(2, BigDecimal.ROUND_DOWN), Boolean.TRUE, currencyEnum.toString() + "-USD");//Post_only
    ourOrder.setSide("buy");
    ourOrder.setProduct_id(currencyEnum.toString() + "-USD");
    //TODO recover gracefully in case we aren't fast enough to get the current price, and it goes down.
    com.coinbase.exchange.api.orders.Order forDebug = orderService.createOrder(ourOrder);
    System.out.println(forDebug);
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

  public double gdaxAskForPrice(OneTimeOrder.CurrencyEnum currency) {
    //Use MarketDataService highest BID.
    MarketData data = marketDataService.getMarketDataOrderBook(currency.toString() + "-USD", "1");
    List<OrderItem> bids = data.getBids();
    OrderItem highestBid = null;
    for (OrderItem bid : bids) {
      if (highestBid == null || bid.getPrice().doubleValue() > highestBid.getPrice().doubleValue()) {
        highestBid = bid;
      }
    }
    return highestBid.getPrice().doubleValue();
  }
}

