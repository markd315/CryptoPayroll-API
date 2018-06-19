package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
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
public class BalanceApiController implements BalanceApi {

  private static final Logger log = LoggerFactory.getLogger(BalanceApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @Autowired
  public BalanceApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    this.objectMapper = objectMapper;
    this.request = request;
  }

  public ResponseEntity<Double> calculateOwed(
      @ApiParam(value = "Three character currency code", required = true, allowableValues = "USD, BTC, LTC, ETH") @RequestHeader(value = "currency", required = true) String currency) {
    String accept = request.getHeader("Accept");
    return new ResponseEntity<Double>(HttpStatus.NOT_IMPLEMENTED);
  }

}
