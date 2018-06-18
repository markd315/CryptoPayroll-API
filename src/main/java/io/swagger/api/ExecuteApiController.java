package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestHeader;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:17:09.771-04:00")

@Controller
public class ExecuteApiController implements ExecuteApi {

  private static final Logger log = LoggerFactory.getLogger(ExecuteApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @org.springframework.beans.factory.annotation.Autowired
  public ExecuteApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    this.objectMapper = objectMapper;
    this.request = request;
  }

  public ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true) @RequestHeader(value = "code", required = true) String code) {
    String accept = request.getHeader("Accept");
    return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
  }

}
