package gld.bookstore.mq;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import gld.bookstore.entity.Order;
import gld.bookstore.entity.OrderBook;
import org.bson.json.JsonObject;
import org.springframework.http.codec.json.Jackson2JsonEncoder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class KafkaProducer {
    KafkaTemplate<String, Object> kafkaTemplate;
    public static final String topicOrder = "topic.order";

    public KafkaProducer(KafkaTemplate<String, Object> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public boolean sendOrder(Order order, List<OrderBook> orderBooks){
        Map<String, Object> orderMap = new HashMap<>();
        orderMap.put("order", order);
        orderMap.put("books", orderBooks);
        ObjectMapper jsonMapper = new ObjectMapper();
        String orderStr;
        try{
           orderStr  = jsonMapper.writeValueAsString(orderMap);
        }
        catch (JsonProcessingException e){
            System.out.println("JSON字符串创建失败！");
            e.printStackTrace();
            return false;
        }
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicOrder, orderStr);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable exception) {
                System.out.println("消息队列发送失败：" + exception.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                System.out.println("消息队列发送成功：" + result.toString());
            }
        });
        return true;
    }

    public boolean sendPayment(String userID, String orderID){
        Map<String, String> paymentMap = new HashMap<>();
        paymentMap.put("payment_user", userID);
        paymentMap.put("payment_order", orderID);
        ObjectMapper jsonMapper = new ObjectMapper();
        String paymentStr;
        try{
            paymentStr = jsonMapper.writeValueAsString(paymentMap);
        }
        catch (JsonProcessingException e){
            System.out.println("JSON字符串创建失败！");
            e.printStackTrace();
            return false;
        }
        ListenableFuture<SendResult<String, Object>> future = kafkaTemplate.send(topicOrder, paymentStr);
        future.addCallback(new ListenableFutureCallback<SendResult<String, Object>>() {
            @Override
            public void onFailure(Throwable exception) {
                System.out.println("消息队列发送失败：" + exception.getMessage());
            }

            @Override
            public void onSuccess(SendResult<String, Object> result) {
                System.out.println("消息队列发送成功：" + result.toString());
            }
        });
        return true;
    }

}
