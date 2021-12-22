package gld.bookstore.mapper;

import gld.bookstore.entity.BookInfo;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookInfoMapper extends MongoRepository<BookInfo,String> {

}
