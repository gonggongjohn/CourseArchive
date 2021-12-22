package gld.bookstore.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import gld.bookstore.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVO {
    private String uuid;
    @JsonProperty("buyer_id")
    private String buyerName;
    @JsonProperty("store_id")
    private String storeName;
    private int price;
    private int status;
    private long time;
    private String fromAddress;
    private String toAddress;
    private String logisticId;
    List<OrderCartBookVO>items;

    public OrderVO(Order order) {
        this.uuid=order.getUuid();
        this.buyerName=order.getBuyerName();
        this.storeName=order.getStoreName();
        this.price=order.getPrice();
        this.status=order.getStatus();
        this.time=order.getTime();
        this.fromAddress=order.getFromAddress();
        this.toAddress=order.getToAddress();
        this.logisticId=order.getLogisticId();
    }
}
