package cryptoroll.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import io.swagger.annotations.ApiModelProperty;
import java.util.UUID;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;

public interface Order {
  Order id(UUID id);

  /**
   * Overwritten by server
   * @return id
   **/
  @ApiModelProperty(value = "Overwritten by server")

  @Valid
  UUID getId();

  void setId(UUID id);

  Order currency(OneTimeOrder.CurrencyEnum currency);

  /**
   * Three-digit trade code
   * @return currency
   **/
  @ApiModelProperty(required = true, value = "Three-digit trade code")
  @NotNull
  OneTimeOrder.CurrencyEnum getCurrency();

  void setCurrency(OneTimeOrder.CurrencyEnum currency);

  Order quantity(Double quantity);

  /**
   * Amount of currency to send
   * @return quantity
   **/
  @ApiModelProperty(required = true, value = "Amount of currency to send")
  @NotNull
  double getQuantity();

  void setQuantity(Double quantity);

  Order destination(String destination);

  /**
   * Get destination
   * @return destination
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull
  String getDestination();

  void setDestination(String destination);

  Order destinationType(OneTimeOrder.DestinationTypeEnum destinationType);

  /**
   * Get destinationType
   * @return destinationType
   **/
  @ApiModelProperty(required = true, value = "")
  @NotNull
  OneTimeOrder.DestinationTypeEnum getDestinationType();

  void setDestinationType(OneTimeOrder.DestinationTypeEnum destinationType);

  Order filled(Boolean filled);

  /**
   * Overwritten by server
   * @return filled
   **/
  @ApiModelProperty(value = "Overwritten by server")
  boolean isFilled();

  void setFilled(Boolean filled);

  /**
   * Convert the given object to string with each line indented by 4 spaces (except the first line).
   */
  String toIndentedString(Object o);

  boolean isRecurring();

  /**
   * Three-digit trade code
   */
  enum CurrencyEnum {
    BTC("BTC"),

    ETH("ETH"),

    LTC("LTC"),

    USD("USD");

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

}
