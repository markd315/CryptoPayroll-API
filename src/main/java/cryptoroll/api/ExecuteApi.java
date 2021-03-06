/**
 * NOTE: This class is auto generated by the swagger code generator program (unset). https://github.com/swagger-api/swagger-codegen Do not edit the
 * class manually.
 */
package cryptoroll.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import javax.naming.InsufficientResourcesException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Api(value = "execute", description = "the execute API")
public interface ExecuteApi {

  @ApiOperation(value = "Execute all orders and log results", nickname = "executePayments", notes = "", tags = {"admin",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 400, message = "Invalid Body"),
      @ApiResponse(code = 401, message = "Unauthorized")})
  @RequestMapping(value = "/execute",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.POST)
  ResponseEntity<Void> executePayments(
      @ApiParam(value = "confirm code", required = true) @RequestHeader(value = "code", required = true) String code)
      throws InsufficientResourcesException;

}
