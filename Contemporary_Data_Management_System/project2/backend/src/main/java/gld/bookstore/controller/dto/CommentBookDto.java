package gld.bookstore.controller.dto;

import gld.bookstore.entity.CommentInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CommentBookDto {
    private long bookId;
    private CommentInfo comment;
}
