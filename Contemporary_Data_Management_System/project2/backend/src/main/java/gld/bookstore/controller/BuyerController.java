package gld.bookstore.controller;

import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.CartMessage;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.OrderMessage;
import gld.bookstore.controller.vo.OrdersMessage;
import gld.bookstore.entity.Book;
import gld.bookstore.service.BuyerService;
import gld.bookstore.service.SellerService;
import gld.bookstore.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Set;

@CrossOrigin
@RestController
@RequestMapping("/buyer")
@AllArgsConstructor
public class BuyerController {
    BuyerService buyerService;
    UserService userService;
    SellerService sellerService;

    @PostConstruct
    public void init(){
        Set<String> userSet = userService.listUsername();
        Set<String> storeSet = sellerService.listStorename();
        List<Book> bookList = buyerService.listBook();
        Boolean isCached = buyerService.startupCache(userSet, storeSet, bookList);
        if(isCached){
            System.out.println("缓存预热成功！");
        }
    }

    @PostMapping("/new_order")
    public ResponseEntity<OrderMessage> newOrder(@RequestBody NewOrderBody newOrderBody){
        return buyerService.newOrder(newOrderBody);
    }

    @PostMapping("/payment")
    public ResponseEntity<Message> payment(@RequestBody PayBody payBody){
        return buyerService.payment(payBody);
    }

    @PostMapping("/add_funds")
    public ResponseEntity<Message> addFunds(@RequestBody AddFundsBody addFundsBody){
        return buyerService.addFunds(addFundsBody);
    }

    @PostMapping("/cash")
    public ResponseEntity<Message> cash(@RequestBody CashBody cashBody){
        return buyerService.cash(cashBody);
    }

    @PostMapping("/order_receive")
    public ResponseEntity<Message> orderReceive(@RequestBody OrderDoBody orderDoBody){
        return buyerService.orderReceive(orderDoBody);
    }

    @GetMapping("/cart/{name}")
    public ResponseEntity<CartMessage>getCart(@PathVariable String name){
        return buyerService.getCart(name);
    }

    @PostMapping("/cart")
    public ResponseEntity<Message>addCart(@RequestBody DoCartBody doCartBody){
        return buyerService.addCart(doCartBody);
    }

    @PatchMapping("/cart")
    public ResponseEntity<Message>updateCart(@RequestBody DoCartBody doCartBody){
        return buyerService.updateCart(doCartBody);
    }

    @PostMapping("/cart_order")
    public ResponseEntity<OrderMessage> cartOrder(@RequestBody NewOrderBody newOrderBody){
        return buyerService.cartOrder(newOrderBody);
    }

    @PostMapping("/comment")
    public ResponseEntity<Message>comment(@RequestBody CommentBody commentBody){
        return buyerService.comment(commentBody);
    }

    @GetMapping("/order/{name}/{status}/{currentPage}/{numPage}")
    public ResponseEntity<OrdersMessage>getOrders(
            @PathVariable int currentPage, @PathVariable String name,
            @PathVariable int numPage, @PathVariable int status
    ){
        return buyerService.getOrders(name,status,currentPage,numPage);
    }

    @PostMapping("/order_cancel")
    public ResponseEntity<Message>orderCancel(@RequestBody OrderDoBody orderDoBody){
        return buyerService.orderCancel(orderDoBody);
    }
}
