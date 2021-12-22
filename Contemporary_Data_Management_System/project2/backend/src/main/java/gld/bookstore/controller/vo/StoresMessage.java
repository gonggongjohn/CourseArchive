package gld.bookstore.controller.vo;

import gld.bookstore.entity.Store;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoresMessage {
    String message;
    List<Store> stores;
}
