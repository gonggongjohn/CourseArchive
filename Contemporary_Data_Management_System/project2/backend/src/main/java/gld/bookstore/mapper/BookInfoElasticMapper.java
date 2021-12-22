package gld.bookstore.mapper;

import gld.bookstore.entity.BookInfoElastic;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface BookInfoElasticMapper extends ElasticsearchRepository<BookInfoElastic, String> {
    List<BookInfoElastic> findByTitleContaining(String title);
    List<BookInfoElastic> findByAuthorContaining(String author);
    List<BookInfoElastic> findByBookIntroContaining(String keyword);
    List<BookInfoElastic> findByContentContaining(String keyword);
}
