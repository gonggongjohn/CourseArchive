package gld.bookstore.controller;

import gld.bookstore.controller.dto.AddBookBody;
import gld.bookstore.controller.dto.AddStockBody;
import gld.bookstore.controller.dto.CreateStoreBody;
import gld.bookstore.controller.dto.OrderSendBody;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.OrdersMessage;
import gld.bookstore.controller.vo.StoresMessage;
import gld.bookstore.service.SellerService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@CrossOrigin
@RestController
@RequestMapping("/seller")
@AllArgsConstructor
public class SellerController {
    SellerService sellerService;

    @PostMapping("/create_store")
    public ResponseEntity<Message> createStore(@RequestBody CreateStoreBody createStoreBody){
        return sellerService.createStore(createStoreBody);
    }

    @PostMapping("/add_book")
    public ResponseEntity<Message> addBook(@RequestBody AddBookBody addBookBody){
        return sellerService.addBook(addBookBody);
    }

    @PostMapping("/add_stock_level")
    public ResponseEntity<Message> addStock(@RequestBody AddStockBody addStockBody){
        return sellerService.addStock(addStockBody);
    }

    @DeleteMapping("/store/{sellerName}/{storeName}")
    public ResponseEntity<Message> deleteStore(
            @PathVariable String sellerName,@PathVariable String storeName
    ){
        return sellerService.deleteStore(sellerName,storeName);
    }

    @GetMapping("/store/{sellerName}")
    public ResponseEntity<StoresMessage>getStores(@PathVariable String sellerName){
        return sellerService.getStores(sellerName);
    }

    @DeleteMapping("/book/{sellerName}/{bookId}")
    public ResponseEntity<Message>deleteBook(
            @PathVariable String sellerName, @PathVariable long bookId){
        return sellerService.deleteBook(sellerName,bookId);
    }

    @PostMapping("/order_send")
    public ResponseEntity<Message>orderSend(@RequestBody OrderSendBody orderSendBody){
        return sellerService.orderSend(orderSendBody);
    }

    @GetMapping("/order/{name}/{status}/{currentPage}/{numPage}")
    public ResponseEntity<OrdersMessage>getOrders(
            @PathVariable int currentPage, @PathVariable String name,
            @PathVariable int numPage, @PathVariable int status
    ){
        return sellerService.getOrders(name,status,currentPage,numPage);
    }
}
