package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PayBody {
    @JsonProperty("user_id")
    private String buyerName;
    @JsonProperty("order_id")
    private String orderId;
    @JsonProperty("password")
    private String password;
}
