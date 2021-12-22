package gld.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "shopping_cart_book")
public class ShoppingCartBook implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String buyerName;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private long bookId;
    @Column(nullable = false)
    private int singlePrice;
    @Column(nullable = false)
    private int count;
    @Column(nullable = false)
    private String storeName;
}
