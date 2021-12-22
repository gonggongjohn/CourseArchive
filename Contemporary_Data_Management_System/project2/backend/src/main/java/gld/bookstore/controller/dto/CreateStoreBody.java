package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CreateStoreBody {
    @JsonProperty("user_id")
    private String sellerName;
    @JsonProperty("store_id")
    private String storeName;
}
