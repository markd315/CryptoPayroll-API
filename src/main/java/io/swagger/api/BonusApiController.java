package io.swagger.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiParam;
import io.swagger.model.Order;
import io.swagger.services.OrderService;
import java.util.List;
import java.util.UUID;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@RestController
@EnableSpringDataWebSupport
@RequestMapping(produces = MediaType.APPLICATION_JSON_VALUE)
public class BonusApiController implements BonusApi {

  private static final Logger log = LoggerFactory.getLogger(BonusApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;

  @Autowired
  private final OrderService service;

  @org.springframework.beans.factory.annotation.Autowired
  public BonusApiController(ObjectMapper objectMapper, HttpServletRequest request, OrderService orderService) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.service = orderService;
  }

  public ResponseEntity<Order> addBonus(@ApiParam(value = "One-time order", required = true) @Valid @RequestBody Order body) {
    body.filled(false).id(UUID.randomUUID());//Server overridden fields
    service.addOrder(body);
    Order toReturn = null;
    try {
      toReturn = service.findOrderById(body.getId());
    } catch (Exception e1) {
      e1.printStackTrace();
    }
    return new ResponseEntity<Order>(toReturn,
        HttpStatus.OK);
  }
}
