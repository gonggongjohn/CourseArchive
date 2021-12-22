package gld.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.benmanes.caffeine.cache.Cache;
import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.*;
import gld.bookstore.entity.*;
import gld.bookstore.mapper.*;
import gld.bookstore.mq.KafkaProducer;
import gld.bookstore.service.BuyerService;
import gld.bookstore.utils.IHttpStatus;
import gld.bookstore.utils.SnowflakeUtils;
import lombok.AllArgsConstructor;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;

import java.util.*;

@Service
@AllArgsConstructor
public class BuyerServiceImpl implements BuyerService {
    StoreMapper storeMapper;
    UserMapper userMapper;
    BookMapper bookMapper;
    OrderMapper orderMapper;
    OrderBookMapper orderBookMapper;
    ShoppingCartBookMapper shoppingCartBookMapper;
    BookInfoMapper bookInfoMapper;
    CommentMapper commentMapper;
    RedisTemplate redisTemplate;
    Cache<String, Integer> caffeineCache;
    KafkaProducer kafkaProducer;

    @Override
    @Transactional
    public ResponseEntity<OrderMessage> newOrder(NewOrderBody newOrderBody) {
        String buyerName = newOrderBody.getBuyerName();
        String storeName = newOrderBody.getStoreName();
        /* Check if buyer exists */
        if(Boolean.TRUE.equals(redisTemplate.hasKey("user"))) {
            if (Boolean.FALSE.equals(redisTemplate.boundSetOps("user").isMember(buyerName))) {
                return new ResponseEntity<>(
                        new OrderMessage("下单失败，买家用户ID不存在", null),
                        null, IHttpStatus.NON_EXIST_USER
                );
            }
        }
        else{
            User buyer = userMapper.selectOne(
                    new LambdaQueryWrapper<User>()
                            .eq(User::getName, buyerName)
            );
            if (buyer == null) {
                return new ResponseEntity<>(
                        new OrderMessage("下单失败，买家用户ID不存在", null),
                        null, IHttpStatus.NON_EXIST_USER
                );
            }
        }
        /* Check if seller exists */
        if(Boolean.TRUE.equals(redisTemplate.hasKey("store"))){
            if (Boolean.FALSE.equals(redisTemplate.boundSetOps("store").isMember(storeName))) {
                return new ResponseEntity<>(
                        new OrderMessage("下单失败，商铺ID不存在", null),
                        null, IHttpStatus.NON_EXIST_STORE
                );
            }
        }
        else {
            Store store = storeMapper.selectOne(
                    new LambdaQueryWrapper<Store>()
                            .eq(Store::getName, storeName)
            );
            if (store == null) {
                return new ResponseEntity<>(
                        new OrderMessage("下单失败，商铺ID不存在", null),
                        null, IHttpStatus.NON_EXIST_STORE
                );
            }
        }

        String uuid= SnowflakeUtils.getId();
        List<OrderBook>orderBooks = new ArrayList<>();
        int price=0;
        for(OrderBookDto orderBook:newOrderBody.getBooks()){
            String bookInfoId = orderBook.getBookInfoId();
            int orderCount = orderBook.getCount();
            String stockRedisKey = "stock_" + bookInfoId + "_" + storeName;
            String priceRedisKey = "price_" + bookInfoId + "_" + storeName;
            String cacheKey = bookInfoId + "_" + storeName;
            /* Redundant request blocking */
            if(caffeineCache.getIfPresent(cacheKey) != null){
                return new ResponseEntity<>(
                        new OrderMessage("下单失败，商品库存不足", null),
                        null, IHttpStatus.LOW_STOCK_LEVEL
                );
            }
            if(redisTemplate.hasKey(stockRedisKey) && redisTemplate.hasKey(priceRedisKey)){
                /* Redis cache hit procedure */
                Long stockLevel = redisTemplate.boundValueOps(stockRedisKey).decrement(orderCount);
                if(stockLevel < 0) {
                    stockLevel = redisTemplate.boundValueOps(stockRedisKey).increment(orderCount);
                    if(stockLevel <= 0){
                        caffeineCache.put(cacheKey, 0);
                    }
                    return new ResponseEntity<>(
                            new OrderMessage("下单失败，商品库存不足", null),
                            null, IHttpStatus.LOW_STOCK_LEVEL
                    );
                }
                else{
                    Integer singlePrice = (Integer) redisTemplate.boundValueOps(priceRedisKey).get();
                    orderBooks.add(new OrderBook(
                                    uuid,
                                    bookInfoId,
                                    orderCount,
                                    singlePrice
                            )
                    );
                    price += orderCount * singlePrice;
                }
            }
            else {
                /* Redis cache miss procedure */
                Book book = bookMapper.selectOne(
                        new LambdaQueryWrapper<Book>()
                                .eq(Book::getInfoId, bookInfoId)
                                .eq(Book::getStoreName, storeName)
                );
                if (book == null) {
                    return new ResponseEntity<>(
                            new OrderMessage("下单失败，购买的图书不存在", null),
                            null, IHttpStatus.NON_EXIST_BOOK
                    );
                } else if (book.getStockLevel() < orderBook.getCount()) {
                    return new ResponseEntity<>(
                            new OrderMessage("下单失败，商品库存不足", null),
                            null, IHttpStatus.LOW_STOCK_LEVEL
                    );
                } else {
                    orderBooks.add(new OrderBook(
                                    uuid,
                                    orderBook.getBookInfoId(),
                                    orderBook.getCount(),
                                    book.getPrice()
                            )
                    );
                    price += orderBook.getCount() * book.getPrice();
                }
            }
        }

        String orderRedisKey = "order_" + uuid;
        redisTemplate.boundValueOps(orderRedisKey).set(price);

        Order order=new Order(
                uuid,
                newOrderBody.getBuyerName(),
                newOrderBody.getStoreName(),
                price, 0,
                System.currentTimeMillis() / 1000,
                null,null,null
        );

        boolean isSent = kafkaProducer.sendOrder(order, orderBooks);

        if(!isSent) {
            orderMapper.insert(order);
            for (OrderBook orderBook : orderBooks) {
                bookMapper.update(
                        null, new LambdaUpdateWrapper<Book>()
                                .eq(Book::getInfoId, orderBook.getBookInfoId())
                                .eq(Book::getStoreName, newOrderBody.getStoreName())
                                .setSql("stock_level = stock_level - " + orderBook.getCount())
                );
                orderBookMapper.insert(orderBook);
            }
        }
        return new ResponseEntity<>(
                new OrderMessage("ok",uuid),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ResponseEntity<Message> payment(PayBody payBody) {
        String md5Password = DigestUtils.md5DigestAsHex(payBody.getPassword().getBytes());
        String userRedisKey = "user_" + payBody.getBuyerName();
        String orderRedisKey = "order_" + payBody.getOrderId();
        if(Boolean.TRUE.equals(redisTemplate.hasKey(userRedisKey)) && Boolean.TRUE.equals(redisTemplate.hasKey(orderRedisKey))){
            /* Redis cache hit */
            if (Boolean.FALSE.equals(Objects.equals(redisTemplate.boundHashOps(userRedisKey).get("password"), md5Password))) {
                return new ResponseEntity<>(
                        new Message("付款失败，授权失败"),
                        HttpStatus.UNAUTHORIZED
                );
            }
            if((int)redisTemplate.boundHashOps(userRedisKey).get("money") < (int)redisTemplate.boundValueOps(orderRedisKey).get()){
                return new ResponseEntity<>(
                        new Message("付款失败，账户余额不足"),
                        null, IHttpStatus.LOW_MONEY
                );
            }

            boolean isSent = kafkaProducer.sendPayment(payBody.getBuyerName(), payBody.getOrderId());
            redisTemplate.delete(orderRedisKey);
            if (isSent){
                return new ResponseEntity<>(
                        new Message("ok"),
                        HttpStatus.OK
                );
            }
        }
        /* Redis cache miss or message sending failed */
        User buyer = userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getName, payBody.getBuyerName())
                        .eq(User::getPassword, payBody.getPassword())
        );
        if (buyer == null) {
            return new ResponseEntity<>(
                    new Message("付款失败，授权失败"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        Order order = orderMapper.selectOne(
                new LambdaQueryWrapper<Order>()
                        .eq(Order::getUuid, payBody.getOrderId())
                        .eq(Order::getStatus, 0)
        );
        if (order == null) {
            return new ResponseEntity<>(
                    new Message("付款失败，无效参数"),
                    null, IHttpStatus.INVALID_PARAM
            );
        }
        if (buyer.getMoney() < order.getPrice()) {
            return new ResponseEntity<>(
                    new Message("付款失败，账户余额不足"),
                    null, IHttpStatus.LOW_MONEY
            );
        }
        userMapper.update(
                null, new LambdaUpdateWrapper<User>()
                        .eq(User::getName, payBody.getBuyerName())
                        .eq(User::getPassword, payBody.getPassword())
                        .setSql("money = money - " + order.getPrice())
        );
        orderMapper.update(
                null, new LambdaUpdateWrapper<Order>()
                        .eq(Order::getUuid, payBody.getOrderId())
                        .set(Order::getStatus, 1)
        );
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> addFunds(AddFundsBody addFundsBody) {
        User buyer=userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getName,addFundsBody.getBuyerName())
                        .eq(User::getPassword,addFundsBody.getPassword())
        );
        if(buyer==null){
            return new ResponseEntity<>(
                    new Message("充值失败，授权失败"),
                    HttpStatus.UNAUTHORIZED
            );
        }
        if(addFundsBody.getAddMoney()<=0){
            return new ResponseEntity<>(
                    new Message("充值失败，无效参数"),
                    null,IHttpStatus.INVALID_PARAM
            );
        }
        userMapper.update(
                null,new LambdaUpdateWrapper<User>()
                        .eq(User::getName,addFundsBody.getBuyerName())
                        .eq(User::getPassword,addFundsBody.getPassword())
                        .setSql("money = money +"+addFundsBody.getAddMoney())
        );
        redisTemplate.boundHashOps("user_" + addFundsBody.getBuyerName()).increment("money", addFundsBody.getAddMoney());
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public List<Book> listBook() {
        return bookMapper.selectList(null);
    }

    @Override
    public Boolean startupCache(Set<String> userSet, Set<String> storeSet, List<Book> bookList) {
        redisTemplate.execute(new RedisCallback() {
            @Override
            public Object doInRedis(RedisConnection connection) throws DataAccessException {
                connection.flushDb();
                return "ok";
            }
        });
        for(String name : userSet) {
            redisTemplate.boundSetOps("user").add(name);
        }
        for(String store: storeSet){
            redisTemplate.boundSetOps("store").add(store);
        }
        for (Book book: bookList) {
            String bookStockKey = "stock_" + book.getInfoId() + "_" + book.getStoreName();
            String bookPriceKey = "price_" + book.getInfoId() + "_" + book.getStoreName();
            int stockLevel = book.getStockLevel();
            redisTemplate.boundValueOps(bookStockKey).set(stockLevel);
            int price = book.getPrice();
            redisTemplate.boundValueOps(bookPriceKey).set(price);
        }
        return true;
    }

    @Override
    public ResponseEntity<Message> cash(CashBody cashBody) {
        User buyer=userMapper.selectOne(
                new LambdaQueryWrapper<User>()
                        .eq(User::getName,cashBody.getName())
        );
        if(buyer==null){
            return new ResponseEntity<>(
                    new Message("提现失败，用户ID不存在"),
                    null,IHttpStatus.NON_EXIST_USER
            );
        }else if(buyer.getMoney()<cashBody.getCash()){
            return new ResponseEntity<>(
                    new Message("提现失败，余额不足"),
                    null,IHttpStatus.LOW_MONEY
            );
        }
        userMapper.update(null, new LambdaUpdateWrapper<User>()
                .eq(User::getName,cashBody.getName())
                .setSql("money = money -" + cashBody.getCash())
        );
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ResponseEntity<Message> orderReceive(OrderDoBody orderDoBody) {
        if(orderMapper.update(null,new LambdaUpdateWrapper<Order>()
                .eq(Order::getBuyerName, orderDoBody.getBuyerName())
                .eq(Order::getUuid, orderDoBody.getUuid())
                .eq(Order::getStatus,2)
                .set(Order::getStatus,3)
        )<=0){
            return new ResponseEntity<>(
                    new Message("收货失败，订单无效"),
                    null,IHttpStatus.INVALID_ORDER
            );
        }
        // 增加销量
        Order order=orderMapper.selectById(orderDoBody.getUuid());
        List<OrderBook>orderBooks=orderBookMapper.selectList(
                new LambdaQueryWrapper<OrderBook>()
                        .eq(OrderBook::getOrderId,orderDoBody.getUuid())
        );
        // 增加商品销量
        int count=0;
        for(OrderBook orderBook:orderBooks){
            count+=orderBook.getCount();
            bookMapper.update(null,new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,orderBook.getBookInfoId())
                    .eq(Book::getStoreName,order.getStoreName())
                    .setSql("sale = sale + " + orderBook.getCount())
            );
        }
        // 增加店铺销量
        storeMapper.update(null,new LambdaUpdateWrapper<Store>()
                .eq(Store::getName,order.getStoreName())
                .setSql("sale = sale + " + count)
        );
        // 增加商家金额
        Store store=storeMapper.selectById(order.getStoreName());
        userMapper.update(null,new LambdaUpdateWrapper<User>()
                .eq(User::getName,store.getSellerName())
                .setSql("money = money + " + order.getPrice())
        );
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );

    }

    @Override
    public ResponseEntity<CartMessage> getCart(String name) {
        List<OrderCartBookVO>items=new ArrayList<>();
        List<ShoppingCartBook>shoppingCartBooks=shoppingCartBookMapper.selectList(
                new LambdaQueryWrapper<ShoppingCartBook>()
                        .eq(ShoppingCartBook::getBuyerName,name)
        );
        if(shoppingCartBooks!=null){
            for(ShoppingCartBook shoppingCartBook:shoppingCartBooks){
                Book book=bookMapper.selectById(shoppingCartBook.getBookId());
                OrderCartBookVO orderCartBookVO;
                if(book!=null){
                    Optional<BookInfo> bookInfo=bookInfoMapper.findById(book.getInfoId());
                    if (bookInfo.isPresent()){
                        orderCartBookVO =new OrderCartBookVO(shoppingCartBook,bookInfo.get());
                    }else {
                        orderCartBookVO =new OrderCartBookVO(shoppingCartBook);
                    }
                }else {
                    orderCartBookVO =new OrderCartBookVO(shoppingCartBook);
                }
                items.add(orderCartBookVO);
            }
        }
        return new ResponseEntity<>(
                new CartMessage("ok",items),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> addCart(DoCartBody doCartBody) {
        Book book=bookMapper.selectById(doCartBody.getBookId());
        if(book==null){
            return new ResponseEntity<>(
                    new Message("添加失败，商品不存在"),
                    null,IHttpStatus.NON_EXIST_BOOK
            );
        }
        try {
            shoppingCartBookMapper.insert(new ShoppingCartBook(
                    doCartBody.getBuyerName(),
                    doCartBody.getBookId(),
                    book.getPrice(),
                    doCartBody.getCount(),
                    book.getStoreName()
            ));
        }catch (DuplicateKeyException e){
            shoppingCartBookMapper.update(
                    null,new LambdaUpdateWrapper<ShoppingCartBook>()
                            .eq(ShoppingCartBook::getBuyerName, doCartBody.getBuyerName())
                            .eq(ShoppingCartBook::getBookId, doCartBody.getBookId())
                            .setSql("count = count + " + doCartBody.getCount())
            );
        }
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> updateCart(DoCartBody doCartBody) {
        int num;
        if(doCartBody.getCount()==0){
            num=shoppingCartBookMapper.delete(new LambdaQueryWrapper<ShoppingCartBook>()
                    .eq(ShoppingCartBook::getBuyerName,doCartBody.getBuyerName())
                    .eq(ShoppingCartBook::getBookId,doCartBody.getBookId())
            );
        }else {
            num=shoppingCartBookMapper.update(null,new LambdaUpdateWrapper<ShoppingCartBook>()
                    .eq(ShoppingCartBook::getBuyerName,doCartBody.getBuyerName())
                    .eq(ShoppingCartBook::getBookId,doCartBody.getBookId())
                    .set(ShoppingCartBook::getCount,doCartBody.getCount())
            );
        }
        if(num<=0){
            return new ResponseEntity<>(
                    new Message("修改失败，商品不存在"),
                    null,IHttpStatus.NON_EXIST_BOOK
            );
        }
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    @Transactional
    public ResponseEntity<OrderMessage> cartOrder(NewOrderBody newOrderBody) {
        ResponseEntity<OrderMessage> response=newOrder(newOrderBody);
        if(response.getBody().getMessage().equals("ok")){
            for(OrderBookDto orderBook:newOrderBody.getBooks()){
                Book book=bookMapper.selectOne(
                        new LambdaQueryWrapper<Book>()
                                .eq(Book::getInfoId,orderBook.getBookInfoId())
                                .eq(Book::getStoreName,newOrderBody.getStoreName())
                );
                shoppingCartBookMapper.delete(new LambdaQueryWrapper<ShoppingCartBook>()
                        .eq(ShoppingCartBook::getBuyerName,newOrderBody.getBuyerName())
                        .eq(ShoppingCartBook::getBookId,book.getId())
                );
            }
        }
        return response;
    }

    @Override
    @Transactional
    public ResponseEntity<Message> comment(CommentBody commentBody) {
        if(orderMapper.update(null,new LambdaUpdateWrapper<Order>()
                .eq(Order::getUuid, commentBody.getUuid())
                .eq(Order::getStatus,3)
                .set(Order::getStatus,4)
        )<=0){
            return new ResponseEntity<>(
                    new Message("评价失败，订单无效"),
                    null,IHttpStatus.INVALID_ORDER
            );
        }
        for(CommentBookDto book:commentBody.getBooks()){
            Optional<Comment> comment=commentMapper.findById(book.getBookId());
            if(comment.isEmpty()){
                List<Integer>count=new ArrayList<>();
                for (int i = 0; i < 5; i++) {
                    count.add(0);
                }
                count.set(book.getComment().getStar()-1,1);
                List<CommentInfo> comments=new ArrayList<>();
                comments.add(new CommentInfo(
                        commentBody.getBuyerName(),
                        book.getComment().getStar(),
                        book.getComment().getText(),
                        System.currentTimeMillis() / 1000
                ));
                commentMapper.insert(new Comment(book.getBookId(),count,comments));
            }else {
                List<Integer>count=comment.get().getCount();
                int num=count.get(book.getComment().getStar()-1);
                count.set(book.getComment().getStar()-1,num+1);
                comment.get().getComments().add(new CommentInfo(
                        commentBody.getBuyerName(),
                        book.getComment().getStar(),
                        book.getComment().getText(),
                        System.currentTimeMillis() / 1000
                ));
            }
        }
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<OrdersMessage> getOrders(String name, int status, int currentPage, int numPage) {
        List<OrderVO>vOrders=new ArrayList<>();
        LambdaQueryWrapper<Order> orderQuery=new LambdaQueryWrapper<>();
        orderQuery.eq(Order::getBuyerName,name);
        if(status!=-1){
            orderQuery.eq(Order::getStatus,status);
        }
        IPage<Order>page=new Page<>(currentPage,numPage);
        IPage<Order>orderIPage=orderMapper.selectPage(page,orderQuery);
        int count= (int) orderIPage.getTotal();
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
        return new ResponseEntity<>(
                new OrdersMessage("ok",count,vOrders),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<Message> orderCancel(OrderDoBody orderDoBody) {
        if(orderMapper.update(null,new LambdaUpdateWrapper<Order>()
                .eq(Order::getBuyerName, orderDoBody.getBuyerName())
                .eq(Order::getUuid, orderDoBody.getUuid())
                .eq(Order::getStatus,0)
                .set(Order::getStatus,5)
        )<=0){
            return new ResponseEntity<>(
                    new Message("取消失败，订单无效"),
                    null,IHttpStatus.INVALID_ORDER
            );
        }
        return new ResponseEntity<>(
                new Message("ok"),
                HttpStatus.OK
        );
    }
}
