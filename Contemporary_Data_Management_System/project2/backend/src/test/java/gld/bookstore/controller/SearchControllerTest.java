package gld.bookstore.controller;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.controller.dto.*;
import gld.bookstore.controller.vo.*;
import gld.bookstore.entity.Book;
import gld.bookstore.entity.BookInfo;
import gld.bookstore.entity.CommentInfo;
import gld.bookstore.mapper.BookMapper;
import gld.bookstore.service.SearchService;
import gld.bookstore.utils.IHttpStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class SearchControllerTest {
    int ok=200;

    SearchService searchService;
    BookMapper bookMapper;
    IMockMvc iMockMvc;

    public @Autowired
    SearchControllerTest(
            MockMvc mockMvc,
            SearchService searchService,
            BookMapper bookMapper,
            ObjectMapper objectMapper
    ) {
        this.searchService = searchService;
        this.bookMapper = bookMapper;
        this.iMockMvc = new IMockMvc(mockMvc,objectMapper);
    }

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
        OrderMessage orderMessage = iMockMvc.testPost("/buyer/new_order", newOrderBody, ok, OrderMessage.class, token);
        orderId = orderMessage.getOrderId();
        // 用户充值
        AddFundsBody addFundsBody = new AddFundsBody(buyerName, buyerName, totalPrice);
        iMockMvc.testPost("/buyer/add_funds", addFundsBody, ok, Message.class);
        // 买家支付
        PayBody payBody = new PayBody(buyerName, orderId, buyerName);
        iMockMvc.testPost("/buyer/payment", payBody, ok, Message.class, token);
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
        // 买家评论
        List<CommentBookDto>comments=new ArrayList<>();
        comments.add(new CommentBookDto(book.getId(),new CommentInfo(buyerName,5,"good",System.currentTimeMillis() / 1000)));
        CommentBody commentBody=new CommentBody(buyerName,orderId,comments);
        iMockMvc.testPost("/buyer/comment",commentBody,ok,Message.class,token);
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestBookSearch {

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/search/book"+"/0"+"/1"+"/10"+"/0?keyword=", ok,SearchMessage.class,token);
            iMockMvc.testGet("/search/book"+"/"+storeName+"/1"+"/10"+"/1?keyword=红", ok,SearchMessage.class,token);
            iMockMvc.testGet("/search/book"+"/"+storeName+"/1"+"/10"+"/2?keyword=绿", ok,SearchMessage.class,token);
            iMockMvc.testGet("/search/book"+"/"+storeName+"/1"+"/10"+"/3?keyword=蓝", ok,SearchMessage.class,token);
            iMockMvc.testGet("/search/book"+"/"+storeName+"/1"+"/10"+"/4?keyword=黄", ok,SearchMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetBookInfo {

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/search/book_info/"+book.getId(),ok,SearchMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetComment {

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/search/comment/"+book.getId()+"/1/10",ok,CommentMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetStar {

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/search/star/"+book.getId(),ok, StarMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetStore {

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/search/store/"+storeName,ok, StoreMessage.class,token);
        }

        @Test
        void test_non_exist_store() throws Exception {
            iMockMvc.testGet("/search/store/"+storeName+"_x", IHttpStatus.NON_EXIST_STORE, StoreMessage.class,token);
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    class TestGetStores {

        @Test
        void test_ok() throws Exception {
            iMockMvc.testGet("/search/stores/10",ok, StoresMessage.class,token);
        }
    }
}