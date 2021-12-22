package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DoCartBody {
    @JsonProperty("buyer_id")
    private String buyerName;
    private long bookId;
    private int count;
}
