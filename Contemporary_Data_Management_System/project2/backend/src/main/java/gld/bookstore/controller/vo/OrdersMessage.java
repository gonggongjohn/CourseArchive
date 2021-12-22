package gld.bookstore.controller.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrdersMessage {
    String message;
    int count;
    List<OrderVO>orders;
}
