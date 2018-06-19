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
import io.swagger.model.OneTimeOrder;
import javax.validation.Valid;

import io.swagger.model.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

@Api(value = "bonus", description = "the bonus API")
public interface BonusApi {

  @ApiOperation(value = "Add a one-time order to be executed in the next cycle", nickname = "addBonus", notes = "", tags = {"x-tenant",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 400, message = "Invalid Body"),
      @ApiResponse(code = 401, message = "Unauthorized")})
  @RequestMapping(value = "/bonus",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.POST)
  ResponseEntity<Order> addBonus(@ApiParam(value = "Update to recurring order", required = true) @Valid @RequestBody OneTimeOrder body);

}
