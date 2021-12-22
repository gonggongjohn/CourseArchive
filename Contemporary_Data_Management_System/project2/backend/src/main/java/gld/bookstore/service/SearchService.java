package gld.bookstore.service;

import gld.bookstore.controller.vo.*;
import gld.bookstore.entity.Book;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

@Service
public interface SearchService {

    BookVO getMessageBook(Book book);

    ResponseEntity<BookInfoMessage> getBookInfo(long bookId);

    ResponseEntity<CommentMessage> getComment(long bookId, int currentPage, int numPage);

    ResponseEntity<StarMessage> getStar(long bookId);

    ResponseEntity<SearchMessage> bookSearch(String storeName, int currentPage, int numPerPage, int strategy, String keywords);

    ResponseEntity<StoreMessage> getStore(String storeName);

    ResponseEntity<VStoresMessage> getStores(int num);
}
