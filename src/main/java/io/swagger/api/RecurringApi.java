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
import io.swagger.model.RecurringOrder;
import javax.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T10:30:50.994-04:00")

@Api(value = "recurring", description = "the recurring API")
public interface RecurringApi {

  @ApiOperation(value = "", nickname = "addRecurring", notes = "", tags = {"x-tenant",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 400, message = "Invalid Body"),
      @ApiResponse(code = 401, message = "Unauthorized")})
  @RequestMapping(value = "/recurring",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.POST)
  ResponseEntity<Void> addRecurring(@ApiParam(value = "New recurring order", required = true) @Valid @RequestBody RecurringOrder body);


  @ApiOperation(value = "Stops a recurring order from the next and all future cycles", nickname = "deleteRecurring", notes = "", tags = {"x-tenant",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Target not found")})
  @RequestMapping(value = "/recurring/{target}",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.DELETE)
  ResponseEntity<Void> deleteRecurring(@ApiParam(value = "The recurring order to remove", required = true) @PathVariable("target") String target);


  @ApiOperation(value = "Retrieve details about a specific recurring order", nickname = "retrieveRecurring", notes = "", tags = {"x-tenant",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Target not found")})
  @RequestMapping(value = "/recurring/{target}",
      produces = {"application/json"},
      method = RequestMethod.GET)
  ResponseEntity<Void> retrieveRecurring(@ApiParam(value = "The recurring order to query", required = true) @PathVariable("target") String target);


  @ApiOperation(value = "Update details about an existing recurring order", nickname = "updateRecurring", notes = "", tags = {"x-tenant",})
  @ApiResponses(value = {
      @ApiResponse(code = 200, message = "Valid"),
      @ApiResponse(code = 400, message = "Invalid Body"),
      @ApiResponse(code = 401, message = "Unauthorized"),
      @ApiResponse(code = 404, message = "Target not found")})
  @RequestMapping(value = "/recurring/{target}",
      produces = {"application/json"},
      consumes = {"application/json"},
      method = RequestMethod.PUT)
  ResponseEntity<Void> updateRecurring(@ApiParam(value = "Update to recurring order", required = true) @Valid @RequestBody RecurringOrder body,
                                       @ApiParam(value = "The recurring order to replace", required = true) @PathVariable("target") String target);

}
