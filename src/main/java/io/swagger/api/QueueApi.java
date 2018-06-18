/**
 * NOTE: This class is auto generated by the swagger code generator program (unset).
 * https://github.com/swagger-api/swagger-codegen
 * Do not edit the class manually.
 */
package io.swagger.api;

import io.swagger.annotations.*;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import javax.validation.constraints.*;
import java.util.List;
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T10:30:50.994-04:00")

@Api(value = "queue", description = "the queue API")
public interface QueueApi {

    @ApiOperation(value = "Returns a list of all orders that will execute on the next payment cycle", nickname = "returnOrders", notes = "", tags={ "admin", })
    @ApiResponses(value = { 
        @ApiResponse(code = 200, message = "Valid"),
        @ApiResponse(code = 401, message = "Unauthorized") })
    @RequestMapping(value = "/queue",
        produces = { "application/json" }, 
        method = RequestMethod.GET)
    ResponseEntity<Void> returnOrders();

}
