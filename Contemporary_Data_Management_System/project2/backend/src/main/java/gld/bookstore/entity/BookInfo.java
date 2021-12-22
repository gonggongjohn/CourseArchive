package gld.bookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("book_info")
public class BookInfo {
    @Id
    private String id;
    @Indexed
    private String title;
    private String author;
    private String publisher;
    private String originalTitle;
    private String translator;
    private String pubYear;
    private int pages;
    private int price;
    private String binding;
    private String isbn;
    private String authorIntro;
    private String bookIntro;
    private String content;
    private List<String>tags;
    private List<String>pictures;
}
