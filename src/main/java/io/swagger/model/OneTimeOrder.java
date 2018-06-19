package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import org.springframework.data.annotation.Id;
import org.springframework.validation.annotation.Validated;

/**
 * Order
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:50:46.970-04:00")

public class OneTimeOrder implements Order {
  @JsonProperty("id")
  @Id
  private UUID id = null;

  @JsonProperty("quantity")
  private Double quantity = null;

  @JsonProperty("destination")
  private String destination = null;

  @JsonProperty("currency")
  private CurrencyEnum currency = null;

  @JsonProperty("destinationType")
  private DestinationTypeEnum destinationType = null;

  @JsonProperty("filled")
  private Boolean filled = null;

  @JsonProperty("recurring")
  protected Boolean recurring = false;

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

  @Override
  public boolean equals(Object o) {
    if (this == o) {
      return true;
    }

    if (o instanceof OneTimeOrder) {
      return false;
    }

    OneTimeOrder order = (OneTimeOrder) o;
    return Objects.equals(this.id, order.id) &&
        Objects.equals(this.currency, order.currency) &&
        Objects.equals(this.quantity, order.quantity) &&
        Objects.equals(this.destination, order.destination) &&
        Objects.equals(this.destinationType, order.destinationType) &&
        Objects.equals(this.filled, order.filled);
  }

  @Override
  public int hashCode() {
    return Objects.hash(id, currency, quantity, destination, destinationType, filled);
  }

  @Override
  public String toString() {
    StringBuilder sb = new StringBuilder();
    sb.append("class Order {\n");

    sb.append("    id: ").append(toIndentedString(id)).append("\n");
    sb.append("    currency: ").append(toIndentedString(currency)).append("\n");
    sb.append("    quantity: ").append(toIndentedString(quantity)).append("\n");
    sb.append("    destination: ").append(toIndentedString(destination)).append("\n");
    sb.append("    destinationType: ").append(toIndentedString(destinationType)).append("\n");
    sb.append("    filled: ").append(toIndentedString(filled)).append("\n");
    sb.append("}");
    return sb.toString();
  }

  @Override
  public String toIndentedString(java.lang.Object o) {
    if (o == null) {
      return "null";
    }
    return o.toString().replace("\n", "\n    ");
  }

  @Override
  public boolean isRecurring() {
    return false;
  }
}

