package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import gld.bookstore.entity.BookInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddBookBody {
    @JsonProperty("user_id")
    private String sellerName;
    @JsonProperty("store_id")
    private String storeName;
    @JsonProperty("book_info")
    private BookInfo bookInfo;
    @JsonProperty("stock_level")
    private int stockLevel;
}
