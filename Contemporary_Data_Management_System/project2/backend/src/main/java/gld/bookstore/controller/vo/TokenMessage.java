package gld.bookstore.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class TokenMessage {
    private String message;
    private String token;
}
