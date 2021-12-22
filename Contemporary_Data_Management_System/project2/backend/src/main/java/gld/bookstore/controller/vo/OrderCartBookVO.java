package gld.bookstore.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import gld.bookstore.entity.Book;
import gld.bookstore.entity.BookInfo;
import gld.bookstore.entity.OrderBook;
import gld.bookstore.entity.ShoppingCartBook;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderCartBookVO {
    private long bookId;
    @JsonProperty("store_id")
    private String storeName;
    private BookInfoVO bookInfo;
    private int singlePrice;
    private int count;

    public OrderCartBookVO(ShoppingCartBook shoppingCartBook){
        this.bookId=shoppingCartBook.getBookId();
        this.storeName=shoppingCartBook.getStoreName();
        this.singlePrice=shoppingCartBook.getSinglePrice();
        this.count=shoppingCartBook.getCount();
        this.bookInfo=null;
    }
    public OrderCartBookVO(ShoppingCartBook shoppingCartBook, BookInfo bookInfo){
        this(shoppingCartBook);
        this.bookInfo=new BookInfoVO(bookInfo);
    }

    public OrderCartBookVO(OrderBook orderBook, Book book, BookInfo bookInfo) {
        this.bookId=book.getId();
        this.storeName=book.getStoreName();
        this.singlePrice=orderBook.getSinglePrice();
        this.count=orderBook.getCount();
        this.bookInfo=new BookInfoVO(bookInfo);
    }
}
