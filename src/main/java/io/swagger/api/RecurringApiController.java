package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.RecurringOrder;
import io.swagger.model.Order;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import io.swagger.services.OrderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.aggregation.BooleanOperators;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

import java.util.List;
import java.util.UUID;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class RecurringApiController implements RecurringApi {

  private static final Logger log = LoggerFactory.getLogger(RecurringApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @Autowired
  private final OrderService service;

  @org.springframework.beans.factory.annotation.Autowired
  public RecurringApiController(ObjectMapper objectMapper, HttpServletRequest request, OrderService orderService) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = orderService;
  }

  public ResponseEntity<Void> addRecurring(@ApiParam(value = "New recurring order", required = true) @Valid @RequestBody RecurringOrder body) {
    String accept = request.getHeader("Accept");
    try {
      if(body == null) {
        throw new NullPointerException("Request body cannot be null");
      }
      service.addOrder(body.getOrder());
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<Void>(HttpStatus.OK);
    //TODO update cyclePeriod & cyclesSinceLast
  }

  public ResponseEntity<Void> deleteRecurring(
      @ApiParam(value = "The recurring order to remove", required = true) @PathVariable("target") String target,
      @ApiParam(value = "The currency to remove (USD: all)", allowableValues = "USD, BTC, ETH, LTC") @RequestHeader(value = "code", required = false) String code) {
    String accept = request.getHeader("Accept");
    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
  }

  public ResponseEntity<Order> retrieveRecurring(
      @ApiParam(value = "x-tenant to retrieve orders for", required = true) @PathVariable("target") String target) {
    String accept = request.getHeader("Accept");
    Order toReturn = null;
    try {
      toReturn = service.findOrderById(UUID.fromString(target));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<Order>(toReturn, HttpStatus.OK);
  }

  public ResponseEntity<Void> updateRecurring(@ApiParam(value = "Update to recurring order", required = true) @Valid @RequestBody RecurringOrder body,
                                              @ApiParam(value = "The recurring order to replace", required = true) @PathVariable("target") String target,
                                              @ApiParam(value = "The currency to override (USD: all)", allowableValues = "USD, BTC, ETH, LTC") @RequestHeader(value = "code", required = false) String code) {
    String accept = request.getHeader("Accept");
    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
  }

}
