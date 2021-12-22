package gld.bookstore.controller;

import gld.bookstore.controller.vo.*;
import gld.bookstore.service.SearchService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/search")
@AllArgsConstructor
public class SearchController {
    SearchService searchService;

    @GetMapping("/book/{storeName}/{currentPage}/{numPerPage}/{strategy}")
    public ResponseEntity<SearchMessage> bookSearch(@PathVariable String storeName, @PathVariable int currentPage,
                                                    @PathVariable int numPerPage, @PathVariable int strategy,
                                                    @RequestParam("keyword") String keyword){
        return searchService.bookSearch(storeName, currentPage, numPerPage, strategy, keyword);
    }

    @GetMapping("/book_info/{bookId}")
    public ResponseEntity<BookInfoMessage>getBookInfo(@PathVariable long bookId){
        return searchService.getBookInfo(bookId);
    }

    @GetMapping("/comment/{bookId}/{currentPage}/{numPage}")
    public ResponseEntity<CommentMessage>getComment(
            @PathVariable long bookId, @PathVariable int currentPage, @PathVariable int numPage
    ){
        return searchService.getComment(bookId,currentPage,numPage);
    }

    @GetMapping("/star/{bookId}")
    public ResponseEntity<StarMessage>getStar(@PathVariable long bookId){
        return searchService.getStar(bookId);
    }

    @GetMapping("/store/{storeName}")
    public ResponseEntity<StoreMessage>getStore(@PathVariable String storeName){
        return searchService.getStore(storeName);
    }

    @GetMapping("/stores/{num}")
    public ResponseEntity<VStoresMessage>getStores(@PathVariable int num){
        return searchService.getStores(num);
    }
}
