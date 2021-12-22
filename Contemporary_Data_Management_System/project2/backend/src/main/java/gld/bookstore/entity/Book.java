package gld.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "book", indexes = {
        @Index(name = "book__info_and_store", columnList = "infoId"),
        @Index(name = "book__info_and_store", columnList = "storeName"),
        @Index(name = "book__time", columnList = "time"),
        @Index(name = "book__sale", columnList = "sale"),
        @Index(name = "book__price", columnList = "price")
})
@TableName("book")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @TableId(type = IdType.AUTO)
    private Long id;
    @Column(nullable = false)
    private String infoId;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int stockLevel;
    @Column(nullable = false)
    private String storeName;
    @Column(nullable = false)
    private int sale;
    @Column(nullable = false)
    private long time;
}
