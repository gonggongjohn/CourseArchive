package gld.bookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import javax.persistence.Id;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document(indexName = "bookinfoindex")
public class BookInfoElastic {
    @Id
    private String id;
    @Field(type = FieldType.Text, name = "title", analyzer = "ik_max_word")
    private String title;
    @Field(type = FieldType.Text, name="author", analyzer = "ik_max_word")
    private String author;
    @Field(type = FieldType.Text, name="publisher", analyzer = "ik_max_word")
    private String publisher;
    @Field(type = FieldType.Text, name="originaltitle", analyzer = "ik_max_word")
    private String originalTitle;
    @Field(type = FieldType.Text, name="translator", analyzer = "ik_max_word")
    private String translator;
    @Field(type = FieldType.Text, name="authorintro", analyzer = "ik_max_word")
    private String authorIntro;
    @Field(type = FieldType.Text, name="bookintro", analyzer = "ik_max_word")
    private String bookIntro;
    @Field(type = FieldType.Text, name="content", analyzer = "ik_max_word")
    private String content;

    public BookInfoElastic(BookInfo bookInfo) {
        this.id = bookInfo.getId();
        this.title = bookInfo.getTitle();
        this.author = bookInfo.getAuthor();
        this.publisher = bookInfo.getPublisher();
        this.originalTitle = bookInfo.getOriginalTitle();
        this.translator = bookInfo.getTranslator();
        this.authorIntro = bookInfo.getAuthorIntro();
        this.bookIntro = bookInfo.getBookIntro();
        this.content = bookInfo.getContent();
    }
}
