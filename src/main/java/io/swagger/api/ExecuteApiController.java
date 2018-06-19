package io.swagger.api;

import com.coinbase.exchange.api.accounts.Account;
import com.coinbase.exchange.api.accounts.AccountService;
import com.coinbase.exchange.api.deposits.DepositService;
import com.coinbase.exchange.api.entity.NewLimitOrderSingle;
import com.coinbase.exchange.api.entity.NewOrderSingle;
import com.coinbase.exchange.api.entity.PaymentResponse;
import com.coinbase.exchange.api.marketdata.MarketData;
import com.coinbase.exchange.api.marketdata.MarketDataService;
import com.coinbase.exchange.api.marketdata.OrderItem;
import com.coinbase.exchange.api.orders.OrderService;
import com.coinbase.exchange.api.payments.PaymentService;
import com.coinbase.exchange.api.payments.PaymentType;
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

  private List<NewOrderSingle> ourOpenOrders = new ArrayList<NewOrderSingle>();

  @Autowired
  public ExecuteApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService service) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = service;
  }


  public ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true)
      @RequestHeader(value = "code", required = true)
          String code)
      throws InsufficientResourcesException {
    //TODO remove
    placeUSDDespoit(32.21);

    List<OneTimeOrder> oneTime = null;
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
        oneTime.add(recurring.getOrder()); //add it to the ones we are executing.
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
        {balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.BTC), balanceApiController.getTotalOrderAmountForCurrency(Order.CurrencyEnum.ETH),
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
    //TODO
    //Make sure that we only make one request per call of this method, or that we use Thread.sleep(334) between calls.
    return true;
  }

  //Bank->USD
  private void placeUSDDespoit(double toPurchaseForCycle) throws InsufficientResourcesException {
    List<PaymentType> pmts = paymentService.getPaymentTypes();
    PaymentType usdDepositer = null;
    for (PaymentType pmt : pmts) {
      if (pmt.getCurrency().equalsIgnoreCase("USD")) {
        usdDepositer = pmt;
      }
    }
    /*
    PaymentType{id='b22911ee-ef35-5c97-bdd4-aef3f65618d9', type='fiat_account', name='GBP Wallet', currency='GBP', primary_buy=false, primary_sell=false, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@4f99769a}
    PaymentType{id='e49c8d15-547b-464e-ac3d-4b9d20b360ec', type='fiat_account', name='USD Wallet', currency='USD', primary_buy=false, primary_sell=false, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@196c6487}
    PaymentType{id='ec3c2e04-e877-4c21-b6d2-1f26744c00c3', type='fiat_account', name='EUR Wallet', currency='EUR', primary_buy=false, primary_sell=false, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@ec949f6}
    PaymentType{id='123bbe6f-28c3-4e47-9be5-300b628f80a0', type='bank_wire', name='Bank Wire The Toronto-Dominion Bank ******2778', currency='USD', primary_buy=false, primary_sell=false, allow_buy=false, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@2bb77ffb}
    PaymentType{id='6a23926d-74b6-4373-8434-9d437c2bafb2', type='ach_bank_account', name='TD Bank ******2778', currency='USD', primary_buy=true, primary_sell=true, allow_buy=true, allow_sell=true, allow_deposit=true, allow_withdraw=true, limits=com.coinbase.exchange.api.payments.Limit@2f9ef1b1}
     */
    double accountLimit = usdDepositer.getLimits().getDeposit()[0].getRemaining().getAmount().doubleValue();
    if (accountLimit < toPurchaseForCycle) {
      throw new InsufficientResourcesException("Not enough payment method limit to order USD");
    }
    PaymentResponse res = depositService.depositViaPaymentMethod(new BigDecimal(toPurchaseForCycle), "USD", usdDepositer.getId());

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
