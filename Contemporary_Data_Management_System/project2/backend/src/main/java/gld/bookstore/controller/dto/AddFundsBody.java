package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddFundsBody {
    @JsonProperty("user_id")
    private String buyerName;
    @JsonProperty("password")
    private String password;
    @JsonProperty("add_value")
    private int addMoney;
}
