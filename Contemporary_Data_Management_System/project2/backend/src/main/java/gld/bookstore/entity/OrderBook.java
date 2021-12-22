package gld.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.io.Serializable;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "order_book")
@TableName("order_book")
public class OrderBook implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String orderId;
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private String bookInfoId;
    @Column(nullable = false)
    private int count;
    @Column(nullable = false)
    private int singlePrice;
}
