package gld.bookstore.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import gld.bookstore.controller.vo.*;
import gld.bookstore.entity.*;
import gld.bookstore.mapper.*;
import gld.bookstore.service.SearchService;
import gld.bookstore.utils.IHttpStatus;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
@AllArgsConstructor
public class SearchServiceImpl implements SearchService {
    BookMapper bookMapper;
    BookInfoMapper bookInfoMapper;
    StoreMapper storeMapper;
    UserMapper userMapper;
    BookInfoElasticMapper bookInfoElasticMapper;
    CommentMapper commentMapper;

    @Override
    public BookVO getMessageBook(Book book) {
        Optional<BookInfo> bookInfo = bookInfoMapper.findById(book.getInfoId());
        if(bookInfo.isPresent()){
            BookVO messageBook = new BookVO(book);
            messageBook.setBookInfo(new BookInfoVO(bookInfo.get()));
            return messageBook;
        }
        else{
            System.out.println("书本信息不存在");
            return null;
        }
    }

    @Override
    public ResponseEntity<BookInfoMessage> getBookInfo(long bookId) {
        Book book=bookMapper.selectById(bookId);
        Optional<BookInfo> bookInfo=bookInfoMapper.findById(book.getInfoId());
        Store store=storeMapper.selectById(book.getStoreName());
        return new ResponseEntity<>(
                new BookInfoMessage("ok",bookInfo.get(),store),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<CommentMessage> getComment(long bookId, int currentPage, int numPage) {
        Optional<Comment> comment=commentMapper.findById(bookId);
        Comment result=null;
        int count=0;
        if(comment.isPresent()){
            result=comment.get();
            count=result.getComments().size();
            int i=count - currentPage * numPage;
            if(i>=0){
                int i1=i + numPage - 1;
                result.setComments(result.getComments().subList(i,i1));
                Collections.reverse(result.getComments());
            }else {
                result.setComments(null);
            }
        }
        return new ResponseEntity<>(
                new CommentMessage("ok", count, result),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<StarMessage> getStar(long bookId) {
        Optional<Comment> comment=commentMapper.findById(bookId);
        List<Integer> count;
        if(comment.isPresent()){
            count=comment.get().getCount();
        }else {
            count= new ArrayList<>();
            for (int i = 0; i < 5; i++) {
                count.add(0);
            }
        }
        return new ResponseEntity<>(
                new StarMessage("ok", count),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<SearchMessage> bookSearch(String storeName, int currentPage, int numPerPage, int strategy, String keywords) {
        System.out.println(keywords);
        List<BookVO> messageBooks = new ArrayList<>();
        Set<String> infoIDSet = new HashSet<>();
        List<String> infoIDOrder = new ArrayList<>();
        List<BookInfoElastic> searchResults = new ArrayList<>();
        String[] keyword_arr = keywords.split(" ");
        for(String keyword : keyword_arr) {
            searchResults.addAll(bookInfoElasticMapper.findByTitleContaining(keyword));
        }
        for(String keyword : keyword_arr) {
            searchResults.addAll(bookInfoElasticMapper.findByAuthorContaining(keyword));
        }
        for(String keyword : keyword_arr) {
            searchResults.addAll(bookInfoElasticMapper.findByBookIntroContaining(keyword));
        }
        for(String keyword : keyword_arr) {
            searchResults.addAll(bookInfoElasticMapper.findByContentContaining(keyword));
        }
        LambdaQueryWrapper<Book> bookCondition = new LambdaQueryWrapper<Book>();
        if(searchResults.size() > 0) {
            bookCondition.eq(Book::getInfoId, searchResults.get(0).getId());
            infoIDSet.add(searchResults.get(0).getId());
            infoIDOrder.add(searchResults.get(0).getId());
            searchResults.remove(0);
        }
        for(BookInfoElastic searchResult : searchResults){
            if(!infoIDSet.contains(searchResult.getId())) {
                bookCondition.or().eq(Book::getInfoId, searchResult.getId());
                infoIDSet.add(searchResult.getId());
                infoIDOrder.add(searchResult.getId());
            }
        }
        if(!storeName.equals("0")){
            bookCondition.eq(Book::getStoreName, storeName);
        }
        switch (strategy){
            case 1:
                bookCondition.orderBy(true, false, Book::getTime);
            case 2:
                bookCondition.orderBy(true, false, Book::getSale);
                break;
            case 3:
                bookCondition.orderBy(true, true, Book::getPrice);
                break;
            case 4:
                bookCondition.orderBy(true, false, Book::getPrice);
                break;
            default:
                break;
        }
        System.out.println(searchResults.size());
        System.out.println(infoIDSet.size());
        IPage<Book> queryPage = bookMapper.selectPage(
                new Page<>(currentPage, numPerPage),
                bookCondition
        );
        int count = (int) queryPage.getTotal();
        List<Book> books = queryPage.getRecords();
        if(strategy == 0){
            books.sort(new Comparator<Book>() {
                @Override
                public int compare(Book o1, Book o2) {
                    return infoIDOrder.indexOf(o1.getInfoId()) - infoIDOrder.indexOf(o2.getInfoId());
                }
            });
        }
        for(Book book : books){
            messageBooks.add(getMessageBook(book));
        }
        return new ResponseEntity<>(
                new SearchMessage("ok", count, messageBooks),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<StoreMessage> getStore(String storeName) {
        Store store=storeMapper.selectById(storeName);
        if(store==null){
            return new ResponseEntity<>(
                    new StoreMessage("获取失败，商店不存在",null),
                    null,IHttpStatus.NON_EXIST_STORE
            );
        }
        User seller=userMapper.selectById(store.getSellerName());
        StoreVO vStore=new StoreVO(store,seller);
        return new ResponseEntity<>(
                new StoreMessage("ok",vStore),
                HttpStatus.OK
        );
    }

    @Override
    public ResponseEntity<VStoresMessage> getStores(int num) {
        IPage<Store>page=new Page<>(1, num);
        IPage<Store>storeIPage=storeMapper.selectPage(page,null);
        List<Store>stores=storeIPage.getRecords();
        List<StoreVO>vStores=new ArrayList<>();
        for(Store store:stores){
            User seller=userMapper.selectById(store.getSellerName());
            vStores.add(new StoreVO(store,seller));
        }
        return new ResponseEntity<>(
                new VStoresMessage("ok",vStores),
                HttpStatus.OK
        );
    }
}
