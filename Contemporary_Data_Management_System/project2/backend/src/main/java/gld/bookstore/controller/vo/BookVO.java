package gld.bookstore.controller.vo;

import gld.bookstore.entity.Book;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookVO {
    private Long id;
    private int price;
    private int stockLevel;
    private String storeId;
    private int sale;
    private long time;
    private BookInfoVO bookInfo;

    public BookVO(Book book){
        this.id = book.getId();
        this.price = book.getPrice();
        this.stockLevel = book.getStockLevel();
        this.storeId = book.getStoreName();
        this.sale = book.getSale();
        this.time = book.getTime();
    }
}
