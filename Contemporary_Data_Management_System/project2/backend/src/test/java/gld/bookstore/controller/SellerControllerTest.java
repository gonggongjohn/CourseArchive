package gld.bookstore.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.Message;
import gld.bookstore.controller.vo.OrderMessage;
import gld.bookstore.controller.vo.OrdersMessage;
import gld.bookstore.controller.vo.TokenMessage;
import gld.bookstore.entity.Book;
import gld.bookstore.entity.BookInfo;
import gld.bookstore.mapper.BookMapper;
import gld.bookstore.service.SellerService;
import gld.bookstore.utils.IHttpStatus;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@SpringBootTest
@AutoConfigureMockMvc
class SellerControllerTest {
    String baseUrl="/seller";
    int ok=200;

    SellerService sellerService;
    IMockMvc iMockMvc;
    BookMapper bookMapper;

    public @Autowired SellerControllerTest(
            MockMvc mockMvc,
            SellerService sellerService,
            ObjectMapper objectMapper,
            BookMapper bookMapper
    ) {
        this.sellerService = sellerService;
        this.bookMapper = bookMapper;
        this.iMockMvc = new IMockMvc(mockMvc,objectMapper);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestCreateStore{
        String username;
        String storeName;
        String password;
        String token;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            username="test_create_store_user_"+ UUID.randomUUID();
            storeName="test_create_store_store_"+UUID.randomUUID();
            password=username;
            // 注册
            // given
            RegisterBody registerBody=new RegisterBody(username,password);
            // when/then
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 登录
            // given
            LoginBody loginBody=new LoginBody(username,password,"");
            // when/then
            TokenMessage response=iMockMvc.testPost("/auth/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
        }

        @Test
        void test_ok() throws Exception {
            // given
            CreateStoreBody createStoreBody=new CreateStoreBody(username,storeName);
            // when/then
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,ok,Message.class,token);
        }

        @Test
        void test_error_exist_store_id() throws Exception {
            // given
            CreateStoreBody createStoreBody=new CreateStoreBody(username,storeName);
            // when/then
            // 第一次创建
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,ok,Message.class,token);
            // 第二次创建
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,IHttpStatus.EXISTED_STORE,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestAddBook{
        String username;
        String storeName;
        String password;
        String token;
        List<BookInfo> bookInfos;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            username="test_add_books_seller_id_"+ UUID.randomUUID();
            storeName="test_add_books_store_id_"+UUID.randomUUID();
            password=username;
            // 注册
            // given
            RegisterBody registerBody=new RegisterBody(username,password);
            // when/then
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 登录
            // given
            LoginBody loginBody=new LoginBody(username,password,"");
            // when/then
            TokenMessage response=iMockMvc.testPost("/auth/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
            // 创建商店
            // given
            CreateStoreBody createStoreBody=new CreateStoreBody(username,storeName);
            // when/then
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,ok,Message.class,token);

            bookInfos=BookInfoList.get();
        }

        @Test
        void test_ok() throws Exception {
            for(BookInfo info : bookInfos){
                // given
                AddBookBody addBookBody=new AddBookBody(username,storeName,info,0);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_book",addBookBody,ok,Message.class,token);
            }
        }

        @Test
        void test_error_non_exist_store_id() throws Exception {
            for(BookInfo info : bookInfos){
                // given
                AddBookBody addBookBody=new AddBookBody(username,storeName+"_x",info,0);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_book",addBookBody,IHttpStatus.NON_EXIST_STORE,Message.class,token);
            }
        }

        @Test
        void test_error_exist_book_id() throws Exception {
            for(BookInfo info : bookInfos){
                // given
                AddBookBody addBookBody=new AddBookBody(username,storeName,info,0);
                // when/then
                // 第一次添加
                iMockMvc.testPost(baseUrl+"/add_book",addBookBody,ok,Message.class,token);
                // 第二次添加
                iMockMvc.testPost(baseUrl+"/add_book",addBookBody,IHttpStatus.EXISTED_BOOK,Message.class,token);
            }
        }

        @Test
        void test_error_non_exist_user_id() throws Exception {
            for(BookInfo info : bookInfos){
                // given
                AddBookBody addBookBody=new AddBookBody(username+"_x",storeName,info,0);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_book",addBookBody,IHttpStatus.NON_EXIST_USER,Message.class,token);
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestAddStockLevel{
        String username;
        String storeName;
        String password;
        String token;
        List<AddBookBody> books=new ArrayList<>();

        @BeforeEach
        void pre_run_initialization() throws Exception {
            username="test_add_book_stock_level_user_"+ UUID.randomUUID();
            storeName="test_add_book_stock_level_store_"+UUID.randomUUID();
            password=username;
            // 注册
            RegisterBody registerBody=new RegisterBody(username,password);
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 登录
            LoginBody loginBody=new LoginBody(username,password,"");
            TokenMessage response=iMockMvc.testPost("/auth/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
            // 创建商店
            CreateStoreBody createStoreBody=new CreateStoreBody(username,storeName);
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,ok,Message.class,token);
            // 添加商品
            List<BookInfo> bookInfos=BookInfoList.get();
            for(BookInfo info : bookInfos){
                AddBookBody addBookBody=new AddBookBody(username,storeName,info,0);
                books.add(addBookBody);
                iMockMvc.testPost(baseUrl+"/add_book",addBookBody,ok,Message.class,token);
            }
        }

        @Test
        void test_error_user_id() throws Exception {
            for(AddBookBody add : books){
                // given
                AddStockBody addStockBody=new AddStockBody(username+"_x",storeName,add.getBookInfo().getId(),10);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_stock_level",addStockBody,IHttpStatus.WRONG_USER,Message.class,token);
            }
        }

        @Test
        void test_error_store_id() throws Exception {
            for(AddBookBody add : books){
                // given
                AddStockBody addStockBody=new AddStockBody(username,storeName+"_x",add.getBookInfo().getId(),10);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_stock_level",addStockBody,IHttpStatus.NON_EXIST_STORE,Message.class,token);
            }
        }

        @Test
        void test_error_book_id() throws Exception {
            for(AddBookBody add : books){
                // given
                AddStockBody addStockBody=new AddStockBody(username,storeName,add.getBookInfo().getId()+"_x",10);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_stock_level",addStockBody,IHttpStatus.NON_EXIST_BOOK,Message.class,token);
            }
        }

        @Test
        void test_ok() throws Exception {
            for(AddBookBody add : books){
                // given
                AddStockBody addStockBody=new AddStockBody(username,storeName,add.getBookInfo().getId(),10);
                // when/then
                iMockMvc.testPost(baseUrl+"/add_stock_level",addStockBody,ok,Message.class,token);
            }
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestDeleteStore{
        String username;
        String storeName;
        String password;
        String token;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            username="test_delete_store_user_"+ UUID.randomUUID();
            storeName="test_delete_store_store_"+UUID.randomUUID();
            password=username;
            // 注册
            RegisterBody registerBody=new RegisterBody(username,password);
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 登录
            LoginBody loginBody=new LoginBody(username,password,"");
            TokenMessage response=iMockMvc.testPost("/auth/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
            // 创建商店
            CreateStoreBody createStoreBody=new CreateStoreBody(username,storeName);
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,ok,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testDelete(baseUrl+"/store/"+username+"/"+storeName,ok,Message.class,token);
        }

        @Test
        void test_non_exit_store() throws Exception {
            iMockMvc.testDelete(baseUrl+"/store/"+username+"/"+storeName+"_x",IHttpStatus.NON_EXIST_STORE,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetStores{
        String username;
        String storeName;
        String password;
        String token;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            username="test_get_stores_user_"+ UUID.randomUUID();
            storeName="test_get_stores_store_"+UUID.randomUUID();
            password=username;
            // 注册
            RegisterBody registerBody=new RegisterBody(username,password);
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 登录
            LoginBody loginBody=new LoginBody(username,password,"");
            TokenMessage response=iMockMvc.testPost("/auth/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
            // 创建商店
            CreateStoreBody createStoreBody=new CreateStoreBody(username,storeName);
            iMockMvc.testPost(baseUrl+"/create_store",createStoreBody,ok,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet(baseUrl+"/store/"+username,ok,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestDeleteBook {
        String username;
        String storeName;
        String password;
        String token;
        Book book;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            username = "test_delete_book_user_" + UUID.randomUUID();
            storeName = "test_delete_book_store_" + UUID.randomUUID();
            password = username;
            // 注册
            RegisterBody registerBody = new RegisterBody(username, password);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 登录
            LoginBody loginBody = new LoginBody(username, password, "");
            TokenMessage response = iMockMvc.testPost("/auth/login", loginBody, ok, TokenMessage.class);
            token = response.getToken();
            // 创建商店
            CreateStoreBody createStoreBody = new CreateStoreBody(username, storeName);
            iMockMvc.testPost(baseUrl + "/create_store", createStoreBody, ok, Message.class, token);
            // 添加商品
            List<BookInfo> bookInfos = BookInfoList.get();
            for (BookInfo info : bookInfos) {
                AddBookBody addBookBody = new AddBookBody(username, storeName, info, 0);
                iMockMvc.testPost(baseUrl + "/add_book", addBookBody, ok, Message.class, token);
            }
            book=bookMapper.selectOne(new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,bookInfos.get(0).getId())
                    .eq(Book::getStoreName,storeName)
            );
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testDelete(baseUrl+"/book/"+username+"/"+book.getId(),ok,Message.class,token);
        }

        @Test
        void test_non_exist_book() throws Exception {
            iMockMvc.testDelete(baseUrl+"/book/"+username+"/"+book.getId()+"1",IHttpStatus.NON_EXIST_BOOK,Message.class,token);
        }

        @Test
        void test_wrong_user() throws Exception {
            iMockMvc.testDelete(baseUrl+"/book/"+username+"_x"+"/"+book.getId(),IHttpStatus.WRONG_USER,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestPayment {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        String orderId;
        int totalPrice;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            buyerName = "test_payment_buyer_id_" + UUID.randomUUID();
            sellerName = "test_payment_seller_id_" + UUID.randomUUID();
            storeName = "test_payment_store_id_" + UUID.randomUUID();
            // 买家注册
            RegisterBody registerBody = new RegisterBody(buyerName, buyerName);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 卖家注册
            registerBody = new RegisterBody(sellerName, sellerName);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 获取token
            LoginBody loginBody = new LoginBody(buyerName, buyerName, "");
            TokenMessage response = iMockMvc.testPost("/auth/login", loginBody, ok, TokenMessage.class);
            token = response.getToken();
            // 卖家创建商店
            CreateStoreBody createStoreBody = new CreateStoreBody(sellerName, storeName);
            iMockMvc.testPost("/seller/create_store", createStoreBody, ok, Message.class, token);
            // 卖家添加图书
            bookInfos=BookInfoList.get();
            for (BookInfo info : bookInfos) {
                AddBookBody addBookBody = new AddBookBody(sellerName, storeName, info, 100);
                iMockMvc.testPost("/seller/add_book", addBookBody, ok, Message.class, token);
            }
            // 买家下单
            List<OrderBookDto> books = new ArrayList<>();
            totalPrice=0;
            for (BookInfo info : bookInfos) {
                books.add(new OrderBookDto(info.getId(), 10));
                totalPrice += info.getPrice() * 10;
            }
            NewOrderBody newOrderBody = new NewOrderBody(buyerName, storeName, books);
            OrderMessage orderMessage = iMockMvc.testPost("/buyer/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestOrderSend {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        String orderId;
        int totalPrice;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            buyerName = "test_order_send_buyer_" + UUID.randomUUID();
            sellerName = "test_order_send_seller_" + UUID.randomUUID();
            storeName = "test_order_send_store_" + UUID.randomUUID();
            // 买家注册
            RegisterBody registerBody = new RegisterBody(buyerName, buyerName);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 卖家注册
            registerBody = new RegisterBody(sellerName, sellerName);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 获取token
            LoginBody loginBody = new LoginBody(buyerName, buyerName, "");
            TokenMessage response = iMockMvc.testPost("/auth/login", loginBody, ok, TokenMessage.class);
            token = response.getToken();
            // 卖家创建商店
            CreateStoreBody createStoreBody = new CreateStoreBody(sellerName, storeName);
            iMockMvc.testPost("/seller/create_store", createStoreBody, ok, Message.class, token);
            // 卖家添加图书
            bookInfos=BookInfoList.get();
            for (BookInfo info : bookInfos) {
                AddBookBody addBookBody = new AddBookBody(sellerName, storeName, info, 100);
                iMockMvc.testPost("/seller/add_book", addBookBody, ok, Message.class, token);
            }
            // 买家下单
            List<OrderBookDto> books = new ArrayList<>();
            totalPrice=0;
            for (BookInfo info : bookInfos) {
                books.add(new OrderBookDto(info.getId(), 10));
                totalPrice += info.getPrice() * 10;
            }
            NewOrderBody newOrderBody = new NewOrderBody(buyerName, storeName, books);
            OrderMessage orderMessage = iMockMvc.testPost("/buyer/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
            iMockMvc.testPost("/buyer/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName);
            iMockMvc.testPost("/buyer/payment", payBody, ok, Message.class, token);
            Thread.sleep(1000);
        }

        @Test
        void test_ok() throws Exception {
            OrderSendBody orderSendBody=new OrderSendBody(orderId,sellerName,"1");
            iMockMvc.testPost(baseUrl+"/order_send",orderSendBody,ok,Message.class,token);
        }

        @Test
        void test_invalid_order() throws Exception {
            // 第一次发货
            OrderSendBody orderSendBody=new OrderSendBody(orderId,sellerName,"1");
            iMockMvc.testPost(baseUrl+"/order_send",orderSendBody,ok,Message.class,token);
            // 再次发货
            iMockMvc.testPost(baseUrl+"/order_send",orderSendBody,IHttpStatus.INVALID_ORDER,Message.class,token);
        }

        @Test
        void test_wrong_user() throws Exception {
            OrderSendBody orderSendBody=new OrderSendBody(orderId,sellerName+"_x","1");
            iMockMvc.testPost(baseUrl+"/order_send",orderSendBody,IHttpStatus.WRONG_USER,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetOrders {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        String orderId;
        int totalPrice;

        @BeforeEach
        void pre_run_initialization() throws Exception {
            buyerName = "test_order_send_buyer_" + UUID.randomUUID();
            sellerName = "test_order_send_seller_" + UUID.randomUUID();
            storeName = "test_order_send_store_" + UUID.randomUUID();
            // 买家注册
            RegisterBody registerBody = new RegisterBody(buyerName, buyerName);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 卖家注册
            registerBody = new RegisterBody(sellerName, sellerName);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 获取token
            LoginBody loginBody = new LoginBody(buyerName, buyerName, "");
            TokenMessage response = iMockMvc.testPost("/auth/login", loginBody, ok, TokenMessage.class);
            token = response.getToken();
            // 卖家创建商店
            CreateStoreBody createStoreBody = new CreateStoreBody(sellerName, storeName);
            iMockMvc.testPost("/seller/create_store", createStoreBody, ok, Message.class, token);
            // 卖家添加图书
            bookInfos=BookInfoList.get();
            for (BookInfo info : bookInfos) {
                AddBookBody addBookBody = new AddBookBody(sellerName, storeName, info, 100);
                iMockMvc.testPost("/seller/add_book", addBookBody, ok, Message.class, token);
            }
            // 买家下单
            List<OrderBookDto> books = new ArrayList<>();
            totalPrice=0;
            for (BookInfo info : bookInfos) {
                books.add(new OrderBookDto(info.getId(), 10));
                totalPrice += info.getPrice() * 10;
            }
            NewOrderBody newOrderBody = new NewOrderBody(buyerName, storeName, books);
            OrderMessage orderMessage = iMockMvc.testPost("/buyer/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/seller/order/"+sellerName+"/"+0+"/"+1+"/"+10,ok, OrdersMessage.class,token);
            iMockMvc.testGet("/seller/order/"+sellerName+"/"+"-1"+"/"+1+"/"+10,ok, OrdersMessage.class,token);
        }
    }
}