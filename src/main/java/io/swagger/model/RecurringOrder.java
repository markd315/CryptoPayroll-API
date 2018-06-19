package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;

/**
 * Be sure to clear the inherited filled field after every trigger
 */
@ApiModel(description = "Be sure to clear the inherited filled field after every trigger")
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

public class RecurringOrder implements Order {
  @JsonProperty("order")
  private OneTimeOrder order = null;

  @JsonProperty("cyclePeriod")
  private Integer cyclePeriod = 1;

  @JsonProperty("cyclesSinceLast")
  private Integer cyclesSinceLast = 0;

  @JsonProperty("id")
  @Id
  private UUID id = null;

  public RecurringOrder order(OneTimeOrder order) {
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

  public OneTimeOrder getOrder() {
    return order;
  }

  public void setOrder(OneTimeOrder order) {
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

  @Override
  public Order id(UUID id) {
    return order.id(id);
  }

  @Override
  public UUID getId() {
    return order.getId();
  }

  @Override
  public void setId(UUID id) {
    order.setId(id);
  }

  @Override
  public Order currency(OneTimeOrder.CurrencyEnum currency) {
    return order.currency(currency);
  }

  @Override
  public OneTimeOrder.CurrencyEnum getCurrency() {
    return order.getCurrency();
  }

  @Override
  public void setCurrency(OneTimeOrder.CurrencyEnum currency) {
    order.setCurrency(currency);
  }

  @Override
  public Order quantity(Double quantity) {
    return order.quantity(quantity);
  }

  @Override
  public double getQuantity() {
    return order.getQuantity();
  }

  @Override
  public void setQuantity(Double quantity) {
    order.setQuantity(quantity);
  }

  @Override
  public Order destination(String destination) {
    return order.destination(destination);
  }

  @Override
  public String getDestination() {
    return order.getDestination();
  }

  @Override
  public void setDestination(String destination) {
    order.setDestination(destination);
  }

  @Override
  public Order destinationType(OneTimeOrder.DestinationTypeEnum destinationType) {
    return order.destinationType(destinationType);
  }

  @Override
  public OneTimeOrder.DestinationTypeEnum getDestinationType() {
    return order.getDestinationType();
  }

  @Override
  public void setDestinationType(OneTimeOrder.DestinationTypeEnum destinationType) {
    order.setDestinationType(destinationType);
  }

  @Override
  public Order filled(Boolean filled) {
    return order.filled(filled);
  }

  @Override
  public boolean isFilled() {
    return order.isFilled();
  }

  @Override
  public void setFilled(Boolean filled) {
    order.setFilled(filled);
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  @Override
  public String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  @Override
  public boolean isRecurring() {
    return true;
  }
}

