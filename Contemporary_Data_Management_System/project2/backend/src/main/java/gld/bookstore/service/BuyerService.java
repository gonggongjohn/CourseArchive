package gld.bookstore.service;

import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.CartMessage;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.OrderMessage;
import gld.bookstore.controller.vo.OrdersMessage;
import gld.bookstore.entity.Book;
import org.springframework.http.ResponseEntity;

import java.util.List;
import java.util.Set;

public interface BuyerService {

    ResponseEntity<OrderMessage> newOrder(NewOrderBody newOrderBody);

    ResponseEntity<Message> payment(PayBody payBody);

    ResponseEntity<Message> addFunds(AddFundsBody addFundsBody);

    List<Book> listBook();

    Boolean startupCache(Set<String> userSet, Set<String> storeSet, List<Book> bookList);

    ResponseEntity<Message> cash(CashBody cashBody);

    ResponseEntity<Message> orderReceive(OrderDoBody orderDoBody);

    ResponseEntity<CartMessage> getCart(String name);

    ResponseEntity<Message> addCart(DoCartBody doCartBody);

    ResponseEntity<Message> updateCart(DoCartBody doCartBody);

    ResponseEntity<OrderMessage> cartOrder(NewOrderBody newOrderBody);

    ResponseEntity<Message> comment(CommentBody commentBody);

    ResponseEntity<OrdersMessage> getOrders(String name, int status, int currentPage, int numPage);

    ResponseEntity<Message> orderCancel(OrderDoBody orderDoBody);
}
