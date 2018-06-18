package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.Order;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T10:30:50.994-04:00")

@Controller
public class BonusApiController implements BonusApi {

  private static final Logger log = LoggerFactory.getLogger(BonusApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @org.springframework.beans.factory.annotation.Autowired
  public BonusApiController(ObjectMapper objectMapper, HttpServletRequest request) {
    this.objectMapper = objectMapper;
    this.request = request;
  }

  public ResponseEntity<Void> addBonus(@ApiParam(value = "Update to recurring order", required = true) @Valid @RequestBody Order body) {
    System.out.println("MEthod");
    String accept = request.getHeader("Accept");
    System.out.println(body);
    return new ResponseEntity<Void>(HttpStatus.NOT_IMPLEMENTED);
  }

}
