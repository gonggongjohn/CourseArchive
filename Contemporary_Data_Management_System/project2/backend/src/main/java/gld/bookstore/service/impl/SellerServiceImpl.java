package gld.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import gld.bookstore.controller.dto.AddBookBody;
import gld.bookstore.controller.dto.AddStockBody;
import gld.bookstore.controller.dto.CreateStoreBody;
import gld.bookstore.controller.dto.OrderSendBody;
import gld.bookstore.controller.vo.*;
import gld.bookstore.entity.*;
import gld.bookstore.mapper.*;
import gld.bookstore.service.SellerService;
import gld.bookstore.utils.IHttpStatus;
import gld.bookstore.utils.SnowflakeUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class SellerServiceImpl implements SellerService {
    StoreMapper storeMapper;
    UserMapper userMapper;
    BookMapper bookMapper;
    BookInfoMapper bookInfoMapper;
    OrderMapper orderMapper;
    OrderBookMapper orderBookMapper;
    RedisTemplate redisTemplate;
    BookInfoElasticMapper bookInfoElasticMapper;

    @Override
    public ResponseEntity<Message> createStore(CreateStoreBody createStoreBody) {
        Store store=new Store();
        store.setName(createStoreBody.getStoreName());
        store.setSellerName(createStoreBody.getSellerName());
        try{
            storeMapper.insert(store);
        }catch (DuplicateKeyException e){
            return new ResponseEntity<>(
                    new Message("创建商铺失败，商铺ID已存在"),
                    null, IHttpStatus.EXISTED_STORE
            );
        }
        redisTemplate.boundSetOps("store").add(store.getName());
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> addBook(AddBookBody addBookBody) {
        User seller=userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getName,addBookBody.getSellerName())
        );
        if(seller==null){
            return new ResponseEntity<>(
                    new Message("添加图书信息失败，卖家用户ID不存在"),
                    null,IHttpStatus.NON_EXIST_USER
            );
        }
        Store store=storeMapper.selectOne(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getName,addBookBody.getStoreName())
        );
        if(store==null){
            return new ResponseEntity<>(
                    new Message("添加图书信息失败，商铺ID不存在"),
                    null,IHttpStatus.NON_EXIST_STORE
            );
        }
        BookInfo bookInfo=addBookBody.getBookInfo();
        String id=addBookBody.getBookInfo().getId();
        if(id==null || id.equals("")){
            id=SnowflakeUtils.getId();
            bookInfo.setId(id);
        }else {
            Book bk=bookMapper.selectOne(
                    new LambdaQueryWrapper<Book>()
                            .eq(Book::getInfoId,id)
                            .eq(Book::getStoreName,addBookBody.getStoreName())
            );
            if(bk!=null){
                return new ResponseEntity<>(
                        new Message("添加图书信息失败，图书ID已存在"),
                        null,IHttpStatus.EXISTED_BOOK
                );
            }
        }
        Book book=new Book();
        book.setInfoId(id);
        book.setPrice(addBookBody.getBookInfo().getPrice());
        book.setStockLevel(addBookBody.getStockLevel());
        book.setStoreName(addBookBody.getStoreName());
        book.setTime(System.currentTimeMillis() / 1000);
        try{
            bookInfoMapper.insert(bookInfo);
        }catch (DuplicateKeyException e){
            // book_info_id 已存在
        }
        bookMapper.insert(book);
        BookInfoElastic bookInfoElastic = new BookInfoElastic(bookInfo);
        try{
            bookInfoElasticMapper.save(bookInfoElastic);
        }
        catch (Exception e){
            e.printStackTrace();
        }
        /* Update redis cache */
        redisTemplate.boundValueOps("stock_" + book.getInfoId() + "_" + book.getStoreName()).set(book.getStockLevel());
        redisTemplate.boundValueOps("price_" + book.getInfoId() + "_" + book.getStoreName()).set(book.getPrice());
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> addStock(AddStockBody addStockBody) {
        Store store=storeMapper.selectOne(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getName,addStockBody.getStoreName())
        );
        if (store==null){
            return new ResponseEntity<>(
                    new Message("添加库存失败，商铺ID不存在"),
                    null,IHttpStatus.NON_EXIST_STORE
            );
        }else if(!store.getSellerName().equals(addStockBody.getSellerName())){
            return new ResponseEntity<>(
                    new Message("添加库存失败，卖家用户ID错误"),
                    null,IHttpStatus.WRONG_USER
            );
        }
        LambdaUpdateWrapper<Book> update=new LambdaUpdateWrapper<>();
        update.eq(Book::getInfoId,addStockBody.getBookInfoId())
                .eq(Book::getStoreName,addStockBody.getStoreName())
                .setSql("stock_level = stock_level + "+addStockBody.getAddStock());
        if(bookMapper.update(null,update)<=0){
            return new ResponseEntity<>(
                    new Message("添加库存失败，图书ID不存在"),
                    null,IHttpStatus.NON_EXIST_BOOK
            );
        }
        Book book = bookMapper.selectOne(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getInfoId,addStockBody.getBookInfoId())
                        .eq(Book::getStoreName,addStockBody.getStoreName())
        );
        redisTemplate.boundValueOps("stock_" + book.getInfoId() + "_" + book.getStockLevel()).set(book.getStockLevel());
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public Set<String> listStorename() {
        LambdaQueryWrapper<Store> query = new LambdaQueryWrapper<>();
        query.select(Store::getName);
        List<Store> storeList = storeMapper.selectList(query);
        Set<String> nameSet = new HashSet<>();
        for(Store store: storeList){
            nameSet.add(store.getName());
        }
        return nameSet;
    }

    @Override
    public ResponseEntity<Message> deleteStore(String sellerName, String storeName) {
        int num=storeMapper.delete(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getName,storeName)
                        .eq(Store::getSellerName,sellerName)
        );
        if(num<=0){
            return new ResponseEntity<>(
                    new Message("删除失败，商店不存在"),
                    null,IHttpStatus.NON_EXIST_STORE
            );
        }
        bookMapper.delete(
                new LambdaQueryWrapper<Book>()
                        .eq(Book::getStoreName,storeName)
        );
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<StoresMessage> getStores(String sellerName) {
        List<Store> stores=storeMapper.selectList(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getSellerName,sellerName)
        );
        return new ResponseEntity<>(
                new StoresMessage("ok",stores),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> deleteBook(String sellerName, long bookId) {
        Book book=bookMapper.selectById(bookId);
        if(book==null){
            return new ResponseEntity<>(
                    new Message("删除失败，商品不存在"),
                    null,IHttpStatus.NON_EXIST_BOOK
            );
        }
        Store store=storeMapper.selectOne(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getName,book.getStoreName())
        );
        if(!store.getSellerName().equals(sellerName)){
            return new ResponseEntity<>(
                    new Message("删除失败，用户ID错误"),
                    null,IHttpStatus.WRONG_USER
            );
        }
        bookMapper.deleteById(bookId);
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> orderSend(OrderSendBody orderSendBody) {
        Order order=orderMapper.selectById(orderSendBody.getUuid());
        if(order==null || order.getStatus()!=1){
            return new ResponseEntity<>(
                    new Message("发货失败，订单号无效"),
                    null,IHttpStatus.INVALID_ORDER
            );
        }
        Store store=storeMapper.selectById(order.getStoreName());
        if(!store.getSellerName().equals(orderSendBody.getSellerName())){
            return new ResponseEntity<>(
                    new Message("发货失败，用户ID错误"),
                    null,IHttpStatus.WRONG_USER
            );
        }
        User buyer=userMapper.selectById(order.getBuyerName());
        User seller=userMapper.selectById(store.getSellerName());
        orderMapper.update(null,new LambdaUpdateWrapper<Order>()
                .eq(Order::getUuid,orderSendBody.getUuid())
                .set(Order::getStatus,2)
                .set(Order::getFromAddress,seller.getAddress())
                .set(Order::getToAddress,buyer.getAddress())
                .set(Order::getLogisticId,orderSendBody.getLogisticId())
        );
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<OrdersMessage> getOrders(String name, int status, int currentPage, int numPage) {
        List<OrderVO>vOrders=new ArrayList<>();
        List<Store>stores=storeMapper.selectList(
                new LambdaQueryWrapper<Store>()
                        .eq(Store::getSellerName,name)
        );
        int count=0;
        if(stores!=null){
            LambdaQueryWrapper<Order> orderQuery=new LambdaQueryWrapper<>();
            if(status!=-1){
                orderQuery.eq(Order::getStatus,status);
            }
            for(Store store:stores){
                orderQuery.eq(Order::getStoreName,store.getName()).or();
            }
            IPage<Order> page=new Page<>(currentPage,numPage);
            IPage<Order>orderIPage=orderMapper.selectPage(page,orderQuery);
            count= (int) orderIPage.getTotal();
            List<Order>orders=orderIPage.getRecords();
            if(orders!=null){
                for(Order order:orders){
                    OrderVO orderVO=new OrderVO(order);
                    List<OrderCartBookVO>vBooks=new ArrayList<>();
                    List<OrderBook> orderBooks=orderBookMapper.selectList(
                            new LambdaQueryWrapper<OrderBook>()
                                    .eq(OrderBook::getOrderId,order.getUuid())
                    );
                    for(OrderBook orderBook:orderBooks){
                        Book book=bookMapper.selectOne(
                                new LambdaQueryWrapper<Book>()
                                        .eq(Book::getInfoId,orderBook.getBookInfoId())
                                        .eq(Book::getStoreName,order.getStoreName())
                        );
                        Optional<BookInfo> bookInfo=bookInfoMapper.findById(orderBook.getBookInfoId());
                        vBooks.add(new OrderCartBookVO(orderBook,book,bookInfo.get()));
                    }
                    orderVO.setItems(vBooks);
                    vOrders.add(orderVO);
                }
            }
        }
        return new ResponseEntity<>(
                new OrdersMessage("ok",count,vOrders),
                HttpStatus.OK
        );
    }
}
