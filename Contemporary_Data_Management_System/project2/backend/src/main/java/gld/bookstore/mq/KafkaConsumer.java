package gld.bookstore.mq;

import com.baomidou.mybatisplus.core.conditions.update.LambdaUpdateWrapper;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.entity.Book;
import gld.bookstore.entity.Order;
import gld.bookstore.entity.OrderBook;
import gld.bookstore.entity.User;
import gld.bookstore.mapper.BookMapper;
import gld.bookstore.mapper.OrderBookMapper;
import gld.bookstore.mapper.OrderMapper;
import gld.bookstore.mapper.UserMapper;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.handler.annotation.Header;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;
import java.util.Optional;

@Component
public class KafkaConsumer {
    BookMapper bookMapper;
    OrderMapper orderMapper;
    OrderBookMapper orderBookMapper;
    UserMapper userMapper;

    public KafkaConsumer(BookMapper bookMapper, OrderMapper orderMapper, OrderBookMapper orderBookMapper, UserMapper userMapper) {
        this.bookMapper = bookMapper;
        this.orderMapper = orderMapper;
        this.orderBookMapper = orderBookMapper;
        this.userMapper = userMapper;
    }

    @KafkaListener(topics = KafkaProducer.topicOrder, groupId = KafkaProducer.topicOrder)
    public void receiveOrder(ConsumerRecord<?, ?> record, Acknowledgment ack, @Header(KafkaHeaders.RECEIVED_TOPIC) String topic){
        Optional message = Optional.ofNullable(record.value());
        if(message.isPresent()){
            Object payload = message.get();
            ObjectMapper mapper = new ObjectMapper();
            Map<String, Object> messageMap;
            try {
                messageMap = mapper.readValue((String) payload, new TypeReference<Map<String, Object>>() {});
                if(messageMap.containsKey("order")) {
                    Object orderRaw = messageMap.get("order");
                    Object booksRaw = messageMap.get("books");
                    Order order = mapper.convertValue(orderRaw, new TypeReference<Order>() {
                    });
                    List<OrderBook> books = mapper.convertValue(booksRaw, new TypeReference<List<OrderBook>>() {
                    });
                    orderMapper.insert(order);
                    for (OrderBook orderBook : books) {
                        bookMapper.update(
                                null, new LambdaUpdateWrapper<Book>()
                                        .eq(Book::getInfoId, orderBook.getBookInfoId())
                                        .eq(Book::getStoreName, order.getStoreName())
                                        .setSql("stock_level = stock_level - " + orderBook.getCount())
                        );
                        orderBookMapper.insert(orderBook);
                    }
                    System.out.println("下单消息处理成功！订单号：" + order.getUuid());
                }
                else if(messageMap.containsKey("payment_order")){
                    String orderID = (String) messageMap.get("payment_order");
                    String userID = (String) messageMap.get("payment_user");
                    Order order = orderMapper.selectOne(
                            new LambdaUpdateWrapper<Order>()
                                    .eq(Order::getUuid, orderID));
                    userMapper.update(
                            null, new LambdaUpdateWrapper<User>()
                                    .eq(User::getName, userID)
                                    .setSql("money = money - " + order.getPrice())
                    );
                    orderMapper.update(
                            null, new LambdaUpdateWrapper<Order>()
                                    .eq(Order::getUuid, orderID)
                                    .set(Order::getStatus, 1)
                    );
                    System.out.println("付款消息处理成功！订单号：" + order.getUuid());
                }
            }
            catch (JsonProcessingException e){
                e.printStackTrace();
            }
            ack.acknowledge();
        }
    }
}
