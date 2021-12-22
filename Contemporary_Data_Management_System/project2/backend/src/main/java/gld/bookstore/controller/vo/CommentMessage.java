package gld.bookstore.controller.vo;

import gld.bookstore.entity.Comment;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentMessage {
    private String message;
    private int count;
    private Comment comment;
}
