package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.OneTimeOrder;
import io.swagger.model.Order;
import io.swagger.model.RecurringOrder;
import io.swagger.services.UltiOrderService;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.InvalidPropertyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class RecurringApiController implements RecurringApi {

  private static final Logger log = LoggerFactory.getLogger(RecurringApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @Autowired
  private final UltiOrderService service;

  @org.springframework.beans.factory.annotation.Autowired
  public RecurringApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService orderService) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = orderService;
  }

  public ResponseEntity<RecurringOrder> addRecurring(@ApiParam(value = "New recurring order", required = true) @Valid @RequestBody RecurringOrder body) {
    String accept = request.getHeader("Accept");
    body.filled(false).id(UUID.randomUUID());//Server overridden fields
    RecurringOrder toReturn = null;

    try {
      if (body == null) {
        throw new NullPointerException("Request body cannot be null");
      }
      //check if exist if yes delete, then save

      // Check valid currency
      if (body.getCurrency() == Order.CurrencyEnum.USD)
        return ResponseEntity.badRequest().build();

      service.addRecurringOrder(body);
      toReturn = service.findRecurringOrderById(body.getId()); // retrieve stored entry to respond with real UUID

    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<RecurringOrder>(toReturn, HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<RecurringOrder>(toReturn, HttpStatus.OK);
  }

  public ResponseEntity<Void> deleteRecurring(
      @ApiParam(value = "The recurring order to remove", required = true) @PathVariable("target") String target,
      @ApiParam(value = "The currency to remove (USD: all)", allowableValues = "USD, BTC, ETH, LTC") @RequestHeader(value = "code", required = false) String code) {
    String accept = request.getHeader("Accept");
    return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
  }

  public ResponseEntity<RecurringOrder> retrieveRecurring(
      @ApiParam(value = "x-tenant to retrieve orders for", required = true) @PathVariable("target") String target) {
    String accept = request.getHeader("Accept");
    RecurringOrder toReturn = null;
    try {
      toReturn = service.findRecurringOrderById(UUID.fromString(target));
    } catch (Exception e) {
      e.printStackTrace();
    }
    return new ResponseEntity<RecurringOrder>(toReturn, HttpStatus.OK);

  }

  public ResponseEntity<Void> updateRecurring(@ApiParam(value = "Update to recurring order", required = true) @Valid @RequestBody RecurringOrder body,
                                              @ApiParam(value = "The recurring order to replace", required = true) @PathVariable("target") String target,
                                              @ApiParam(value = "The currency to override (USD: all)", allowableValues = "USD, BTC, ETH, LTC") @RequestHeader(value = "code", required = false) String code) {
    String accept = request.getHeader("Accept");
    body.filled(false);
    try {
      if (!code.equals("USD") && !body.getCurrency().toString().equals(code))
        throw new NotFoundException(401, "Currency does not match");
      service.updateRecurringOrder(body, UUID.fromString(target), code);
    } catch (Exception e) {
      e.printStackTrace();
      return new ResponseEntity<Void>(HttpStatus.BAD_REQUEST);
    }
    return new ResponseEntity<Void>(HttpStatus.OK);
  }

}
