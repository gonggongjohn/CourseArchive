package gld.bookstore.controller.vo;

import gld.bookstore.entity.BookInfo;
import gld.bookstore.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookInfoMessage {
    String message;
    BookInfo bookInfo;
    Store store;
}
