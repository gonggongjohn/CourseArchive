package gld.bookstore.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchMessage {
    private String message;
    private int count;
    private List<BookVO> books;

}

