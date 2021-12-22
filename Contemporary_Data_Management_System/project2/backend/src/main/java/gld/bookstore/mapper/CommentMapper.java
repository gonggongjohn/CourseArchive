package gld.bookstore.mapper;

import gld.bookstore.entity.Comment;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CommentMapper extends MongoRepository<Comment,Long> {

}
