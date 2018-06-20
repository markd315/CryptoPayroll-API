package io.cryptoroll.model;

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

public class RecurringOrder implements Order{

  @JsonProperty("id")
  @Id
  private UUID id = null;

  @JsonProperty("quantity")
  private Double quantity = null;

  @JsonProperty("destination")
  private String destination = null;

  @JsonProperty("currency")
  private Order.CurrencyEnum currency = null;

  @JsonProperty("destinationType")
  private Order.DestinationTypeEnum destinationType = null;

  @JsonProperty("filled")
  private Boolean filled = null;

  @JsonProperty("recurring")
  protected Boolean recurring = true;


  @JsonProperty("cyclePeriod")
  private Integer cyclePeriod = 1;

  @JsonProperty("cyclesSinceLast")
  private Integer cyclesSinceLast = 0;

  /**
   * Get order
   * @return order
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  @Valid

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
    return Objects.equals(this.getId(), recurringOrder.getId()) &&
        Objects.equals(this.getCurrency(), recurringOrder.getCurrency()) &&
        Objects.equals(this.getQuantity(), recurringOrder.getQuantity()) &&
        Objects.equals(this.getDestination(), recurringOrder.getDestination()) &&
        Objects.equals(this.getDestinationType(), recurringOrder.getDestinationType()) &&
        Objects.equals(this.isFilled(), recurringOrder.isFilled()) &&
        Objects.equals(this.getCyclePeriod(), recurringOrder.getCyclePeriod()) &&
        Objects.equals(this.getCyclesSinceLast(), recurringOrder.getCyclesSinceLast());
  }

  //@Override
  public int hashCode() {
    return Objects.hash(getId(), getCurrency(), getQuantity(), getDestination(), getDestinationType(), isFilled(), getCyclePeriod(), getCyclesSinceLast());
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class RecurringOrder {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    destination: ").append(toIndentedString(destination)).append("\n");
    sb.append("    destinationType: ").append(toIndentedString(destinationType)).append("\n");
    sb.append("    filled: ").append(toIndentedString(filled)).append("\n");
    sb.append("    cyclePeriod: ").append(toIndentedString(cyclePeriod)).append("\n");
    sb.append("    cyclesSinceLast: ").append(toIndentedString(cyclesSinceLast)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  @Override
  public Order id(UUID id) {
    this.id = id;
    return this;
  }

  @Override
  @ApiModelProperty(value = "Overwritten by server")

  public UUID getId() {
    return id;
  }

  @Override
  public void setId(UUID id) {
    this.id = id;
  }

  @Override
  public Order currency(CurrencyEnum currency) {
    this.currency = currency;
    return this;
  }

  @Override
  @ApiModelProperty(required = true, value = "Three-digit trade code")
  @NotNull

  public CurrencyEnum getCurrency() {
    return currency;
  }

  @Override
  public void setCurrency(CurrencyEnum currency) {
    this.currency = currency;
  }

  @Override
  public Order quantity(Double quantity) {
    this.quantity = quantity;
    return this;
  }

  @Override
  @ApiModelProperty(required = true, value = "Amount of currency to send")
  @NotNull

  public double getQuantity() {
    return quantity;
  }

  @Override
  public void setQuantity(Double quantity) {
    this.quantity = quantity;
  }

  @Override
  public Order destination(String destination) {
    this.destination = destination;
    return this;
  }

  @Override
  @ApiModelProperty(required = true, value = "")
  @NotNull

  public String getDestination() {
    return destination;
  }

  @Override
  public void setDestination(String destination) {
    this.destination = destination;
  }

  @Override
  public Order destinationType(DestinationTypeEnum destinationType) {
    this.destinationType = destinationType;
    return this;
  }

  @Override
  @ApiModelProperty(required = true, value = "")
  @NotNull
  public DestinationTypeEnum getDestinationType() {
    return destinationType;
  }

  @Override
  public void setDestinationType(DestinationTypeEnum destinationType) {
    this.destinationType = destinationType;
  }

  @Override
  public Order filled(Boolean filled) {
    this.filled = filled;
    return this;
  }

  @Override
  @ApiModelProperty(value = "Overwritten by server")
  public boolean isFilled() {
    return filled;
  }

  @Override
  public void setFilled(Boolean filled) {
    this.filled = filled;
  }

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  //@Override
  public String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  //@Override
  public boolean isRecurring() {
    return true;
  }
}

