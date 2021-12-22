package gld.bookstore.controller.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginBody {
    @JsonProperty("user_id")
    private String name;
    @JsonProperty("password")
    private String password;
    @JsonProperty("terminal")
    private String terminal;
}
