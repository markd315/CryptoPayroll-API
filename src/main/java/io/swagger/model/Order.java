package io.swagger.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import java.util.Objects;
import javax.validation.constraints.NotNull;
import org.springframework.validation.annotation.Validated;

/**
 * Order
 */
@Validated
@javax.annotation.Generated(value = "io.swagger.codegen.languages.SpringCodegen", date = "2018-06-18T11:17:09.771-04:00")

public class Order {
  @JsonProperty("id")
  private String id = null;

  /**
   * Three-digit trade code
   */
  public enum CurrencyEnum {
    BTC("BTC"),

    ETH("ETH"),

    LTC("LTC");

    private String value;

    CurrencyEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static CurrencyEnum fromValue(String text) {
      for (CurrencyEnum b : CurrencyEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("currency")
  private CurrencyEnum currency = null;

  @JsonProperty("quantity")
  private Integer quantity = null;

  @JsonProperty("destination")
  private String destination = null;

  /**
   * Gets or Sets destinationType
   */
  public enum DestinationTypeEnum {
    COINBASE("coinbase"),

    WALLET("wallet");

    private String value;

    DestinationTypeEnum(String value) {
      this.value = value;
    }

    @Override
    @JsonValue
    public String toString() {
      return String.valueOf(value);
    }

    @JsonCreator
    public static DestinationTypeEnum fromValue(String text) {
      for (DestinationTypeEnum b : DestinationTypeEnum.values()) {
        if (String.valueOf(b.value).equals(text)) {
          return b;
        }
      }
      return null;
    }
  }

  @JsonProperty("destinationType")
  private DestinationTypeEnum destinationType = null;

  @JsonProperty("filled")
  private Boolean filled = null;

  public Order id(String id) {
    this.id = id;
    return this;
  }

  /**
   * Overwritten by server
   * @return id
   **/
  @ApiModelProperty(value = "Overwritten by server")

  public String getId() {
    return id;
  }

  public void setId(String id) {
    this.id = id;
  }

  public Order currency(CurrencyEnum currency) {
    this.currency = currency;
    return this;
  }

  /**
   * Three-digit trade code
   * @return currency
   **/
  @ApiModelProperty(required = true, value = "Three-digit trade code")
  @NotNull

  public CurrencyEnum getCurrency() {
    return currency;
  }

  public void setCurrency(CurrencyEnum currency) {
    this.currency = currency;
  }

  public Order quantity(Integer quantity) {
    this.quantity = quantity;
    return this;
  }

  /**
   * Amount of currency to send
   * @return quantity
   **/
  @ApiModelProperty(required = true, value = "Amount of currency to send")
  @NotNull

  public Integer getQuantity() {
    return quantity;
  }

  public void setQuantity(Integer quantity) {
    this.quantity = quantity;
  }

  public Order destination(String destination) {
    this.destination = destination;
    return this;
  }

  /**
   * Get destination
   * @return destination
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  public String getDestination() {
    return destination;
  }

  public void setDestination(String destination) {
    this.destination = destination;
  }

  public Order destinationType(DestinationTypeEnum destinationType) {
    this.destinationType = destinationType;
    return this;
  }

  /**
   * Get destinationType
   * @return destinationType
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull

  public DestinationTypeEnum getDestinationType() {
    return destinationType;
  }

  public void setDestinationType(DestinationTypeEnum destinationType) {
    this.destinationType = destinationType;
  }

  public Order filled(Boolean filled) {
    this.filled = filled;
    return this;
  }

  /**
   * Overwritten by server
   * @return filled
   **/
  @ApiModelProperty(value = "Overwritten by server")

  public Boolean isFilled() {
    return filled;
  }

  public void setFilled(Boolean filled) {
    this.filled = filled;
  }


  @Override
  public boolean equals(java.lang.Object o) {
    if (this == o) {
      return true;
    }
    if (o == null || getClass() != o.getClass()) {
      return false;
    }
    Order order = (Order) o;
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

