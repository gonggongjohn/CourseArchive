package gld.bookstore.service;

import gld.bookstore.controller.dto.AddBookBody;
import gld.bookstore.controller.dto.AddStockBody;
import gld.bookstore.controller.dto.CreateStoreBody;
import gld.bookstore.controller.dto.OrderSendBody;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.OrdersMessage;
import gld.bookstore.controller.vo.StoresMessage;
import org.springframework.http.ResponseEntity;

import java.util.Set;

public interface SellerService {

    ResponseEntity<Message> createStore(CreateStoreBody createStoreBody);

    ResponseEntity<Message> addBook(AddBookBody addBookBody);

    ResponseEntity<Message> addStock(AddStockBody addStockBody);

    Set<String> listStorename();

    ResponseEntity<Message> deleteStore(String sellerName, String storeName);

    ResponseEntity<StoresMessage> getStores(String sellerName);

    ResponseEntity<Message> deleteBook(String sellerName, long bookId);

    ResponseEntity<Message> orderSend(OrderSendBody orderSendBody);

    ResponseEntity<OrdersMessage> getOrders(String name, int status, int currentPage, int numPage);
}
