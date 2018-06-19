package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.configuration.GDAXAuthorizer;
import io.swagger.services.OrderService;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import javax.servlet.http.HttpServletRequest;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class ExecuteApiController implements ExecuteApi {

  private static final Logger log = LoggerFactory.getLogger(ExecuteApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @Autowired
  private final OrderService service;


  @org.springframework.beans.factory.annotation.Autowired
  public ExecuteApiController(ObjectMapper objectMapper, HttpServletRequest request, OrderService service) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = service;
  }

  public ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true) @RequestHeader(value = "code", required = true) String code) {
    /*List<Order> oneTime;
    try {
      oneTime = service.getAllOneTimeOrders();
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    List<RecurringOrder> toReturn = null;
    try {
      toReturn = service.getAllRecurringOrders();
    } catch (Exception e1) {
      e1.printStackTrace();
    }*/
    //TODO a bunch of summation logic, then a bunch of API hits.
    //TODO after we successfully order each type of the crypto, release it to its owners.
    JSONObject body = new JSONObject("{\n"
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

    return new ResponseEntity<Void>(/*(MultiValueMap<String, String>) toReturn,*/
        HttpStatus.OK);
  }
}
