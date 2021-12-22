package gld.bookstore.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.*;
import gld.bookstore.entity.Book;
import gld.bookstore.entity.BookInfo;
import gld.bookstore.entity.Comment;
import gld.bookstore.entity.CommentInfo;
import gld.bookstore.mapper.BookMapper;
import gld.bookstore.service.BuyerService;
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
class BuyerControllerTest {
    String baseUrl="/buyer";
    int ok=200;
    int unauthorized=401;

    IMockMvc iMockMvc;
    BuyerService buyerService;
    BookMapper bookMapper;

    public @Autowired
    BuyerControllerTest(
            MockMvc mockMvc,
            BuyerService buyerService,
            BookMapper bookMapper,
            ObjectMapper objectMapper
    ) {
        this.buyerService = buyerService;
        this.bookMapper=bookMapper;
        this.iMockMvc = new IMockMvc(mockMvc,objectMapper);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestNewOrder{
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo>bookInfos;

        @BeforeEach
        void pre_run_initialization() throws Exception{
            buyerName="test_new_order_buyer_id_"+UUID.randomUUID();
            sellerName="test_new_order_seller_id_"+UUID.randomUUID();
            storeName="test_new_order_store_id_"+UUID.randomUUID();
            // 买家注册
            RegisterBody registerBody=new RegisterBody(buyerName,buyerName);
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 卖家注册
            registerBody=new RegisterBody(sellerName,sellerName);
            iMockMvc.testPost("/auth/register",registerBody,ok, Message.class);
            // 获取token
            LoginBody loginBody=new LoginBody(buyerName,buyerName,"");
            TokenMessage response=iMockMvc.testPost("/auth/login",loginBody,ok,TokenMessage.class);
            token=response.getToken();
            // 卖家创建商店
            CreateStoreBody createStoreBody=new CreateStoreBody(sellerName,storeName);
            iMockMvc.testPost("/seller/create_store",createStoreBody,ok,Message.class,token);

            bookInfos=BookInfoList.get();
        }

        @Test
        void test_non_exist_book_id() throws Exception {
            // 卖家添加图书
            for(BookInfo info:bookInfos){
                AddBookBody addBookBody=new AddBookBody(sellerName,storeName,info,100);
                iMockMvc.testPost("/seller/add_book",addBookBody,ok,Message.class,token);
            }
            // 创建订单
            List<OrderBookDto>books=new ArrayList<>();
            for(BookInfo info:bookInfos){
                books.add(new OrderBookDto(info.getId()+"_x",10));
            }
            NewOrderBody newOrderBody=new NewOrderBody(buyerName,storeName,books);
            iMockMvc.testPost(baseUrl+"/new_order",newOrderBody,IHttpStatus.NON_EXIST_BOOK,Message.class,token);
        }

        @Test
        void test_low_stock_level() throws Exception {
            // 卖家添加图书
            for(BookInfo info:bookInfos){
                AddBookBody addBookBody=new AddBookBody(sellerName,storeName,info,0);
                iMockMvc.testPost("/seller/add_book",addBookBody,ok,Message.class,token);
            }
            // 创建订单
            List<OrderBookDto>books=new ArrayList<>();
            for(BookInfo info:bookInfos){
                books.add(new OrderBookDto(info.getId(),10));
            }
            NewOrderBody newOrderBody=new NewOrderBody(buyerName,storeName,books);
            iMockMvc.testPost(baseUrl+"/new_order",newOrderBody,IHttpStatus.LOW_STOCK_LEVEL,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            // 卖家添加图书
            for(BookInfo info:bookInfos){
                AddBookBody addBookBody=new AddBookBody(sellerName,storeName,info,100);
                iMockMvc.testPost("/seller/add_book",addBookBody,ok,Message.class,token);
            }
            // 创建订单
            List<OrderBookDto>books=new ArrayList<>();
            for(BookInfo info:bookInfos){
                books.add(new OrderBookDto(info.getId(),10));
            }
            NewOrderBody newOrderBody=new NewOrderBody(buyerName,storeName,books);
            iMockMvc.testPost(baseUrl+"/new_order",newOrderBody,ok,Message.class,token);
        }

        @Test
        void test_non_exist_user_id() throws Exception {
            // 卖家添加图书
            for(BookInfo info:bookInfos){
                AddBookBody addBookBody=new AddBookBody(sellerName,storeName,info,100);
                iMockMvc.testPost("/seller/add_book",addBookBody,ok,Message.class,token);
            }
            // 创建订单
            List<OrderBookDto>books=new ArrayList<>();
            for(BookInfo info:bookInfos){
                books.add(new OrderBookDto(info.getId(),10));
            }
            NewOrderBody newOrderBody=new NewOrderBody(buyerName+"_x",storeName,books);
            iMockMvc.testPost(baseUrl+"/new_order",newOrderBody,IHttpStatus.NON_EXIST_USER,Message.class,token);
        }

        @Test
        void test_non_exist_store_id() throws Exception {
            // 卖家添加图书
            for(BookInfo info:bookInfos){
                AddBookBody addBookBody=new AddBookBody(sellerName,storeName,info,100);
                iMockMvc.testPost("/seller/add_book",addBookBody,ok,Message.class,token);
            }
            // 创建订单
            List<OrderBookDto>books=new ArrayList<>();
            for(BookInfo info:bookInfos){
                books.add(new OrderBookDto(info.getId(),10));
            }
            NewOrderBody newOrderBody=new NewOrderBody(buyerName,storeName+"_x",books);
            iMockMvc.testPost(baseUrl+"/new_order",newOrderBody,IHttpStatus.NON_EXIST_STORE,Message.class,token);
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
            OrderMessage orderMessage = iMockMvc.testPost(baseUrl + "/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
        }

        @Test
        void test_ok() throws Exception {
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName);
            iMockMvc.testPost(baseUrl + "/payment", payBody, ok, Message.class, token);
        }

        @Test
        void test_authorization_error() throws Exception {
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName + "_x");
            iMockMvc.testPost(baseUrl + "/payment", payBody, unauthorized, Message.class, token);
        }

        @Test
        void test_not_suff_funds() throws Exception {
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice-1);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName);
            iMockMvc.testPost(baseUrl + "/payment", payBody, IHttpStatus.LOW_MONEY, Message.class, token);
        }

        @Test
        void test_repeat_pay() throws Exception {
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName);
            iMockMvc.testPost(baseUrl + "/payment", payBody, ok, Message.class, token);
            Thread.sleep(1000);
            // 再次支付
            iMockMvc.testPost(baseUrl + "/payment", payBody, IHttpStatus.INVALID_PARAM, Message.class, token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestAddFunds{
        String buyerName;
        String password;

        @BeforeEach
        void pre_run_initialization() throws Exception{
            buyerName="test_add_funds_"+UUID.randomUUID();
            password=buyerName;
            // 买家注册
            RegisterBody registerBody = new RegisterBody(buyerName, password);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
        }

        @Test
        void test_ok() throws Exception {
            AddFundsBody addFundsBody=new AddFundsBody(buyerName,password,1000);
            iMockMvc.testPost(baseUrl+"/add_funds",addFundsBody,ok,Message.class);
            addFundsBody.setAddMoney(-1000);
            iMockMvc.testPost(baseUrl+"/add_funds",addFundsBody,IHttpStatus.INVALID_PARAM,Message.class);
        }

        @Test
        void test_error_user_id() throws Exception {
            AddFundsBody addFundsBody=new AddFundsBody(buyerName+"_x",password,10);
            iMockMvc.testPost(baseUrl+"/add_funds",addFundsBody,unauthorized,Message.class);
        }

        @Test
        void test_error_password() throws Exception {
            AddFundsBody addFundsBody=new AddFundsBody(buyerName,password+"_x",10);
            iMockMvc.testPost(baseUrl+"/add_funds",addFundsBody,unauthorized,Message.class);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestCash{
        String buyerName;
        String password;
        String token;

        @BeforeEach
        void pre_run_initialization() throws Exception{
            buyerName="test_add_funds_"+UUID.randomUUID();
            password=buyerName;
            // 买家注册
            RegisterBody registerBody = new RegisterBody(buyerName, password);
            iMockMvc.testPost("/auth/register", registerBody, ok, Message.class);
            // 获取token
            LoginBody loginBody = new LoginBody(buyerName, buyerName, "");
            TokenMessage response = iMockMvc.testPost("/auth/login", loginBody, ok, TokenMessage.class);
            token = response.getToken();
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, 999);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
        }

        @Test
        void test_ok() throws Exception {
            CashBody cashBody=new CashBody(buyerName,999);
            iMockMvc.testPost(baseUrl+"/cash",cashBody,ok,Message.class,token);
        }

        @Test
        void test_non_exist_user() throws Exception {
            CashBody cashBody=new CashBody(buyerName+"_x",999);
            iMockMvc.testPost(baseUrl+"/cash",cashBody,IHttpStatus.NON_EXIST_USER,Message.class,token);
        }

        @Test
        void test_low_money() throws Exception {
            CashBody cashBody=new CashBody(buyerName,1000);
            iMockMvc.testPost(baseUrl+"/cash",cashBody,IHttpStatus.LOW_MONEY,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestOrderReceive {
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
            OrderMessage orderMessage = iMockMvc.testPost(baseUrl + "/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName);
            iMockMvc.testPost(baseUrl + "/payment", payBody, ok, Message.class, token);
            Thread.sleep(1000);
            // 商家发货
            OrderSendBody orderSendBody=new OrderSendBody(orderId,sellerName,"");
            iMockMvc.testPost("/seller/order_send",orderSendBody,ok,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            OrderDoBody orderDoBody=new OrderDoBody(orderId,buyerName);
            iMockMvc.testPost("/buyer/order_receive",orderDoBody,ok,Message.class,token);
        }

        @Test
        void test_invalid_order() throws Exception {
            OrderDoBody orderDoBody=new OrderDoBody(orderId,buyerName);
            iMockMvc.testPost("/buyer/order_receive",orderDoBody,ok,Message.class,token);
            iMockMvc.testPost("/buyer/order_receive",orderDoBody,IHttpStatus.INVALID_ORDER,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestAddCart {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        Book book;

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
            book=bookMapper.selectOne(new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,bookInfos.get(0).getId())
                    .eq(Book::getStoreName,storeName)
            );
        }

        @Test
        void test_ok() throws Exception {
            DoCartBody doCartBody=new DoCartBody(buyerName,book.getId(),10);
            iMockMvc.testPost("/buyer/cart",doCartBody,ok,Message.class,token);
        }

        @Test
        void test_non_exist_book() throws Exception {
            DoCartBody doCartBody=new DoCartBody(buyerName,0,10);
            iMockMvc.testPost("/buyer/cart",doCartBody,IHttpStatus.NON_EXIST_BOOK,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetCart {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        Book book;

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
            book=bookMapper.selectOne(new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,bookInfos.get(0).getId())
                    .eq(Book::getStoreName,storeName)
            );
            // 加入购物车
            DoCartBody doCartBody=new DoCartBody(buyerName,book.getId(),10);
            iMockMvc.testPost("/buyer/cart",doCartBody,ok,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/buyer/cart/"+buyerName,ok, CartMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestUpdateCart {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        Book book;

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
            book=bookMapper.selectOne(new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,bookInfos.get(0).getId())
                    .eq(Book::getStoreName,storeName)
            );
            // 加入购物车
            DoCartBody doCartBody=new DoCartBody(buyerName,book.getId(),10);
            iMockMvc.testPost("/buyer/cart",doCartBody,ok,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            DoCartBody doCartBody=new DoCartBody(buyerName,book.getId(),10);
            iMockMvc.testPatch("/buyer/cart/",doCartBody,ok, Message.class,token);
            doCartBody.setCount(0);
            iMockMvc.testPatch("/buyer/cart/",doCartBody,ok, Message.class,token);
        }

        @Test
        void test_non_exist_book() throws Exception {
            DoCartBody doCartBody=new DoCartBody(buyerName,book.getId()+1,10);
            iMockMvc.testPatch("/buyer/cart/",doCartBody,IHttpStatus.NON_EXIST_BOOK, Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestCartOrder {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        Book book;

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
            book=bookMapper.selectOne(new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,bookInfos.get(0).getId())
                    .eq(Book::getStoreName,storeName)
            );
            // 加入购物车
            DoCartBody doCartBody=new DoCartBody(buyerName,book.getId(),10);
            iMockMvc.testPost("/buyer/cart",doCartBody,ok,Message.class,token);
        }

        @Test
        void test_ok() throws Exception {
            List<OrderBookDto>books=new ArrayList<>();
            books.add(new OrderBookDto(book.getInfoId(),10));
            NewOrderBody newOrderBody=new NewOrderBody(buyerName,storeName,books);
            iMockMvc.testPost("/buyer/cart_order",newOrderBody,ok,Message.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestComment {
        String buyerName;
        String sellerName;
        String storeName;
        String token;
        List<BookInfo> bookInfos;
        String orderId;
        int totalPrice;
        Book book;

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
            OrderMessage orderMessage = iMockMvc.testPost(baseUrl + "/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
            // 用户充值
            AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
            iMockMvc.testPost(baseUrl + "/add_funds", addFundsBody, ok, Message.class);
            // 买家支付
            PayBody payBody = new PayBody(buyerName, orderId, buyerName);
            iMockMvc.testPost(baseUrl + "/payment", payBody, ok, Message.class, token);
            Thread.sleep(1000);
            // 商家发货
            OrderSendBody orderSendBody=new OrderSendBody(orderId,sellerName,"");
            iMockMvc.testPost("/seller/order_send",orderSendBody,ok,Message.class,token);
            // 买家收货
            OrderDoBody orderDoBody=new OrderDoBody(orderId,buyerName);
            iMockMvc.testPost("/buyer/order_receive",orderDoBody,ok,Message.class,token);

            book=bookMapper.selectOne(new LambdaUpdateWrapper<Book>()
                    .eq(Book::getInfoId,bookInfos.get(0).getId())
                    .eq(Book::getStoreName,storeName)
            );
        }

        @Test
        void test_ok() throws Exception {
            List<CommentBookDto>comments=new ArrayList<>();
            comments.add(new CommentBookDto(book.getId(),new CommentInfo(buyerName,5,"good",System.currentTimeMillis() / 1000)));
            CommentBody commentBody=new CommentBody(buyerName,orderId,comments);
            iMockMvc.testPost("/buyer/comment",commentBody,ok,Message.class,token);
        }

        @Test
        void test_invalid_order() throws Exception {
            List<CommentBookDto>comments=new ArrayList<>();
            comments.add(new CommentBookDto(book.getId(),new CommentInfo(buyerName,5,"good",System.currentTimeMillis() / 1000)));
            CommentBody commentBody=new CommentBody(buyerName,orderId,comments);
            iMockMvc.testPost("/buyer/comment",commentBody,ok,Message.class,token);
            iMockMvc.testPost("/buyer/comment",commentBody,IHttpStatus.INVALID_ORDER,Message.class,token);
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
            OrderMessage orderMessage = iMockMvc.testPost(baseUrl + "/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
        }

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/buyer/order/"+buyerName+"/"+0+"/"+1+"/"+10,ok, OrdersMessage.class,token);
            iMockMvc.testGet("/buyer/order/"+buyerName+"/"+"-1"+"/"+1+"/"+10,ok, OrdersMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestOrderCancel {
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
            OrderMessage orderMessage = iMockMvc.testPost(baseUrl + "/new_order", newOrderBody, ok, OrderMessage.class, token);
            orderId = orderMessage.getOrderId();
            Thread.sleep(1000);
        }

        @Test
        void test_ok() throws Exception {
            OrderDoBody orderDoBody=new OrderDoBody(orderId,buyerName);
            iMockMvc.testPost("/buyer/order_cancel",orderDoBody,ok,Message.class,token);
        }

        @Test
        void test_invalid_order() throws Exception {
            OrderDoBody orderDoBody=new OrderDoBody(orderId,buyerName);
            iMockMvc.testPost("/buyer/order_cancel",orderDoBody,ok,Message.class,token);
            iMockMvc.testPost("/buyer/order_cancel",orderDoBody,IHttpStatus.INVALID_ORDER,Message.class,token);
        }
    }
}