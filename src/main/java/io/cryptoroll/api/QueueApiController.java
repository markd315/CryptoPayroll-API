package io.cryptoroll.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.cryptoroll.services.UltiOrderService;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Controller
public class QueueApiController implements QueueApi {

  private static final Logger log = LoggerFactory.getLogger(QueueApiController.class);

  private final ObjectMapper objectMapper;

  private final HttpServletRequest request;
  private final UltiOrderService orderService;

  @Autowired
  public QueueApiController(ObjectMapper objectMapper, HttpServletRequest request, UltiOrderService orderService) {
    this.objectMapper = objectMapper;
    this.request = request;
    this.orderService = orderService;
  }

  public ResponseEntity<List<Object>> returnOrders() {
    String accept = request.getHeader("Accept");
    List<Object> currentOrders = new ArrayList<>();
    try {
      currentOrders.addAll(orderService.getAllRecurringOrders());
      currentOrders.addAll(orderService.getAllOneTimeOrders());
    } catch (NotFoundException e) {
      log.error(e.getMessage(), e);
      return ResponseEntity.notFound().build();
    }

    return ResponseEntity.ok().body(currentOrders);
  }


}
