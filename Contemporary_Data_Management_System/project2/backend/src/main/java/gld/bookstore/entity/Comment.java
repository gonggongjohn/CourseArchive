package gld.bookstore.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("comment")
public class Comment {
    @Id
    @Field(name = "_id")
    private Long bookId;
    List<Integer> count;
    List<CommentInfo>comments;
}

