package io.swagger.api;

import com.coinbase.exchange.api.accounts.AccountService;
import com.coinbase.exchange.api.deposits.DepositService;
import com.coinbase.exchange.api.payments.PaymentService;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.Order;
import io.swagger.model.RecurringOrder;
import io.swagger.services.UltiOrderService;
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
  public ExecuteApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService service) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = service;
  }


  public ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true) @RequestHeader(value = "code", required = true) String code) {
    List<Order> oneTime = null;
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
      oneTime.add(recurring.getOrder());
    }

    double amountWeOwePayees = balanceApiController.calculateOwed("USD").getBody();

    double usdWeOwn = queryAccountBalance();

    double toPurchaseForCycle = (1.1 * amountWeOwePayees - usdWeOwn);
    //account loaded with cash
    placeOrderForUsd(toPurchaseForCycle);
    double amountOrderFilledFor = Double.MAX_VALUE;
    while (toPurchaseForCycle > 0) {
      try {
        Thread.sleep(1000);
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
      amountOrderFilledFor = cancelOrderForUsdReturnAmountAlreadySpent();
      toPurchaseForCycle -= amountOrderFilledFor;
      placeOrderForUsd(toPurchaseForCycle);
    }
    //Order for crypto is filled, just nest for all cryptocurrencies, checking for 0.

    ResponseEntity<List<Object>> ordersResponse = queueApiController.returnOrders();
    List<Object> ordersToFill = ordersResponse.getBody();
    for (Object orderObject : ordersToFill) {
      Order order = (Order) orderObject;
      payAmountToWallet(order.getQuantity(), order.getDestination(), order.getCurrency(), order.getDestinationType());
    }
    incrementOrResetAllRecurringPayments();

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
      res = GDAXAuthorizer.makeGDAXRequest(new HashMap<String, String>(), body, HttpMethod.POST, "/deposits/payment-method");
    } catch (IOException e) {
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

  private double cancelOrderForUsdReturnAmountAlreadySpent() {
    //TODO
    return 0.0;
  }

  private boolean ourOrderIsUnfilled() {
    //TODO
    return true;
  }

  private void placeOrderForUsd(double toPurchaseForCycle) {
    //TODO
  }

  private double queryAccountBalance() {
    //TODO
    return 0.0;
  }

  private void payAmountToWallet(double toPay, String address, Order.CurrencyEnum currency, Order.DestinationTypeEnum destinationType) {
    //TODO
  }

  private void incrementOrResetAllRecurringPayments() {
    //TODO
  }
}
