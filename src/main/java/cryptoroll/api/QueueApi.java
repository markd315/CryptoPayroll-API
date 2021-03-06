/**
 * NOTE: This class is auto generated by the swagger code generator program (unset). https://github.com/swagger-api/swagger-codegen Do not edit the
 * class manually.
 */
package cryptoroll.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Api(value = "queue", description = "the queue API")
public interface QueueApi {

  @ApiOperation(value = "Returns a list of all orders that will execute on the next payment cycle", nickname = "returnOrders", notes = "", tags = {
      "admin",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 401, message = "Unauthorized")})
  @RequestMapping(value = "/queue",
      produces = {"application/json"},
      method = RequestMethod.GET)
  ResponseEntity<List<Object>> returnOrders();

}
