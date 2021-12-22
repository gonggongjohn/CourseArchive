package gld.bookstore.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "store", indexes = {
        @Index(name = "store__seller", columnList = "sellerName"),
        @Index(name = "store__sale", columnList = "sale")
})
@TableName("store")
public class Store {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @TableId(type = IdType.INPUT)
    @JsonProperty("store_id")
    private String name;
    @Column(nullable = false)
    @JsonProperty("seller_id")
    private String sellerName;
    @Column(nullable = false)
    private int sale;
}
