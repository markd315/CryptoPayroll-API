/**
 * NOTE: This class is auto generated by the swagger code generator program (unset). https://github.com/swagger-api/swagger-codegen Do not edit the
 * class manually.
 */
package io.swagger.api;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Api(value = "balance", description = "the balance API")
public interface BalanceApi {

  @ApiOperation(value = "Return the current commitment for this cryptocurrency, or the total USD commitment", nickname = "calculateOwed", notes = "", tags = {
      "admin",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 401, message = "Unauthorized")})
  @RequestMapping(value = "/balance",
      produces = {"application/json"},
      method = RequestMethod.GET)
  ResponseEntity<Double> calculateOwed(
      @ApiParam(value = "Three character currency code", required = true, allowableValues = "USD, BTC, LTC, ETH") @RequestHeader(value = "currency", required = true) String currency);

}
