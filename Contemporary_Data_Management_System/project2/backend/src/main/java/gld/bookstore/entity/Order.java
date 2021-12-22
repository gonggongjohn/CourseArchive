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
@Table(name = "\"order\"", indexes = {
        @Index(name = "order__buyer", columnList = "buyerName"),
        @Index(name = "order__store", columnList = "storeName"),
        @Index(name = "order__time", columnList = "time")
})
@TableName("\"order\"")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.INPUT)
    private String uuid;
    @Column(nullable = false)
    private String buyerName;
    @Column(nullable = false)
    private String storeName;
    @Column(nullable = false)
    private int price;
    @Column(nullable = false)
    private int status;
    @Column(nullable = false)
    private long time;
    private String fromAddress;
    private String toAddress;
    private String logisticId;
}
