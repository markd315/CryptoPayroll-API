package io.swagger.model;

import java.util.Objects;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonCreator;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.model.Order;
import org.springframework.validation.annotation.Validated;
import javax.validation.Valid;
import javax.validation.constraints.*;

/**
 * Be sure to clear the inherited filled field after every trigger
 */
@ApiModel(description = "Be sure to clear the inherited filled field after every trigger")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T10:30:50.994-04:00")

public class RecurringOrder   {
  @JsonProperty("order")
  private Order order = null;

  @JsonProperty("cyclePeriod")
  private Integer cyclePeriod = 1;

  @JsonProperty("cyclesSinceLast")
  private Integer cyclesSinceLast = 0;

  public RecurringOrder order(Order order) {
    this.order = order;
    return this;
  }

  /**
   * Get order
   * @return order
  **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

  public Order getOrder() {
    return order;
  }

  public void setOrder(Order order) {
    this.order = order;
  }

  public RecurringOrder cyclePeriod(Integer cyclePeriod) {
    this.cyclePeriod = cyclePeriod;
    return this;
  }

  /**
   * Send this recurring order every N pay cycles
   * @return cyclePeriod
  **/
  @ApiModelProperty(value = "Send this recurring order every N pay cycles")


  public Integer getCyclePeriod() {
    return cyclePeriod;
  }

  public void setCyclePeriod(Integer cyclePeriod) {
    this.cyclePeriod = cyclePeriod;
  }

  public RecurringOrder cyclesSinceLast(Integer cyclesSinceLast) {
    this.cyclesSinceLast = cyclesSinceLast;
    return this;
  }

  /**
   * Once this = cyclePeriod-1, we send on the next cycle.
   * @return cyclesSinceLast
  **/
  @ApiModelProperty(value = "Once this = cyclePeriod-1, we send on the next cycle.")


  public Integer getCyclesSinceLast() {
    return cyclesSinceLast;
  }

  public void setCyclesSinceLast(Integer cyclesSinceLast) {
    this.cyclesSinceLast = cyclesSinceLast;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    RecurringOrder recurringOrder = (RecurringOrder) o;
    return Objects.equals(this.order, recurringOrder.order) &&
        Objects.equals(this.cyclePeriod, recurringOrder.cyclePeriod) &&
        Objects.equals(this.cyclesSinceLast, recurringOrder.cyclesSinceLast);
  }

  @Override
  public int hashCode() {
    return Objects.hash(order, cyclePeriod, cyclesSinceLast);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecurringOrder {\n");
    
    sb.append("    order: ").append(toIndentedString(order)).append("\n");
    sb.append("    cyclePeriod: ").append(toIndentedString(cyclePeriod)).append("\n");
    sb.append("    cyclesSinceLast: ").append(toIndentedString(cyclesSinceLast)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces
   * (except the first line).
   */
  private String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }
}

