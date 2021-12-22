package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewOrderBody {
    @JsonProperty("user_id")
    private String buyerName;
    @JsonProperty("store_id")
    private String storeName;
    @JsonProperty("books")
    private List<OrderBookDto>books;
}
