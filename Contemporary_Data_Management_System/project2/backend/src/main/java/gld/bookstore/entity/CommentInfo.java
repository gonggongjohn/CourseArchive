package gld.bookstore.entity;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentInfo {
    @JsonProperty("user_id")
    private String userName;
    private int star;
    private String text;
    private long time;
}
