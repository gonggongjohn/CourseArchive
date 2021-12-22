package gld.bookstore.controller;

import gld.bookstore.entity.BookInfo;

import java.util.ArrayList;
import java.util.List;

public class BookInfoList {

    public static List<BookInfo> get() {
        List<BookInfo> bookInfos=new ArrayList<>();
        BookInfo info1=new BookInfo();
        info1.setId("1000067");info1.setTitle("美丽心灵");info1.setPrice(3879);
        bookInfos.add(info1);
        BookInfo info2=new BookInfo();
        info2.setId("1000134");info2.setTitle("三毛流浪记全集");info2.setPrice(3000);
        bookInfos.add(info2);
        BookInfo info3=new BookInfo();
        info3.setId("1000135");info3.setTitle("黑色棉花田");info3.setPrice(1380);
        bookInfos.add(info3);
        return bookInfos;
    }
}
