package gld.bookstore.controller.vo;

import gld.bookstore.entity.BookInfo;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookInfoVO {
    private String id;
    private String title;
    private String author;
    private String picture;

    public BookInfoVO(BookInfo bookInfo){
        this.id=bookInfo.getId();
        this.title=bookInfo.getTitle();
        this.author=bookInfo.getAuthor();
        if(bookInfo.getPictures()==null || bookInfo.getPictures().isEmpty()){
            picture="";
        }else {
            picture=bookInfo.getPictures().get(0);
        }
    }
}
