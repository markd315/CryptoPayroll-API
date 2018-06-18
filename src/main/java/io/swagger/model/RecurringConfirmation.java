package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.Valid;
import org.springframework.validation.annotation.Validated;

/**
 * RecurringConfirmation
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T10:30:50.994-04:00")

public class RecurringConfirmation {
  @JsonProperty("code")
  private Integer code = null;

  @JsonProperty("type")
  private String type = null;

  @JsonProperty("message")
  private String message = null;

  @JsonProperty("order")
  private RecurringOrder order = null;

  public RecurringConfirmation code(Integer code) {
    this.code = code;
    return this;
  }

  /**
   * Get code
   * @return code
   **/
  @ApiModelProperty(value = "")

  public Integer getCode() {
    return code;
  }

  public void setCode(Integer code) {
    this.code = code;
  }

  public RecurringConfirmation type(String type) {
    this.type = type;
    return this;
  }

  /**
   * Get type
   * @return type
   **/
  @ApiModelProperty(value = "")

  public String getType() {
    return type;
  }

  public void setType(String type) {
    this.type = type;
  }

  public RecurringConfirmation message(String message) {
    this.message = message;
    return this;
  }

  /**
   * Get message
   * @return message
   **/
  @ApiModelProperty(value = "")

  public String getMessage() {
    return message;
  }

  public void setMessage(String message) {
    this.message = message;
  }

  public RecurringConfirmation order(RecurringOrder order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
   **/
  @ApiModelProperty(value = "")

  @Valid

  public RecurringOrder getOrder() {
    return order;
  }

  public void setOrder(RecurringOrder order) {
    this.order = order;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecurringConfirmation recurringConfirmation = (RecurringConfirmation) o;
    return Objects.equals(this.code, recurringConfirmation.code) &&
        Objects.equals(this.type, recurringConfirmation.type) &&
        Objects.equals(this.message, recurringConfirmation.message) &&
        Objects.equals(this.order, recurringConfirmation.order);
  }

  @Override
  public int hashCode() {
    return Objects.hash(code, type, message, order);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecurringConfirmation {\n");

    sb.append("    code: ").append(toIndentedString(code)).append("\n");
    sb.append("    type: ").append(toIndentedString(type)).append("\n");
    sb.append("    message: ").append(toIndentedString(message)).append("\n");
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

