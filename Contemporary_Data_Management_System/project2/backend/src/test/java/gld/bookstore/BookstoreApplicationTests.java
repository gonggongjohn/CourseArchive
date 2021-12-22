package gld.bookstore;

import gld.bookstore.utils.SnowflakeUtils;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class BookstoreApplicationTests {

    @Test
    void contextLoads() {
    }

    @Test
    void getSnowflakeId(){
        for (int i = 0; i < 10; i++) {
            System.out.println(SnowflakeUtils.getId());
        }
    }

}
