package gld.bookstore.controller.vo;

import com.fasterxml.jackson.annotation.JsonProperty;
import gld.bookstore.entity.Store;
import gld.bookstore.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StoreVO {
    @JsonProperty("store_id")
    private String storeName;
    @JsonProperty("seller_id")
    private String sellerName;
    private String address;

    public StoreVO(Store store, User seller) {
        this.storeName=store.getName();
        this.sellerName=store.getSellerName();
        this.address=seller.getAddress();
    }
}
