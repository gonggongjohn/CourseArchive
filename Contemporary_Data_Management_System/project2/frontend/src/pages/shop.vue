<template>
  <q-page class="q-mt-sm">
    <div class="text-grey-9 text-weight-bold">

      <div class="row">
        <div class="col-sm-12 col-md-3">
          <!--店铺信息-->
          <div class="row items-center q-mx-sm">
            <div class="col-12 q-mt-sm">
              <div class="q-pl-md bg-white q-py-md">
                <p class="text-grey-9 text-h4 text-weight-bold"> {{ objData.default.shopName }} </p>
                <p class="text-grey-9"> 店主： {{ objData.default.seller }} </p>
                <p class="text-grey-9"> 发货地址：{{ objData.default.address }} </p>
              </div>
            </div>
          </div>
    
          <!--店内搜素-->
          <div class="row items-center q-mx-sm">
            <div class="col-12 q-mt-sm">
              <div class="q-pl-md bg-white q-py-md">
                <p class="text-grey-9 text-h6 text-weight-bold">店内搜索</p>
                  <q-input outlined v-model="objData.default.searchText" class="q-pb-sm" square bg-color="white" dense
                  >
                    <template v-slot:after>
                      <q-btn round dense flat icon="search" 
                      @click="objData.current_page=1;getItem(objData.current_page, objData.num_per_page, objData.default.orderType);"/>
                    </template>
                  </q-input>
                  <div class="q-pa-md">
                    <q-btn-toggle
                      v-model="objData.default.orderType"
                      toggle-color="primary"
                      :options="[
                        {label: '新品↓', value: 0},
                        {label: '销量↓', value: 1},
                        {label: '价格↑', value: 2},
                        {label: '价格↓', value: 3}
                      ]"
                      @click="objData.current_page=1;getItem(objData.current_page, objData.num_per_page, objData.default.orderType);"
                    />
                  </div>
              </div>
            </div>
          </div>

          <!--商品操作-->
          <div class="row items-center q-mx-sm">
            <div class="col-12 q-mt-sm">
              <div class="q-pl-md bg-white q-py-md">
                <q-btn class="q-my-xs" label="上架商品" @click="objData.create.dialog = true" color="primary"/>

                <q-btn class="q-my-xs" label="下架商品" @click="objData.withdraw.dialog = true" color="primary"/>

                <q-btn class="q-my-xs" label="添加库存" @click="objData.add.dialog = true" color="primary"/>

                <q-btn class="q-my-xs" label="删除商店" @click="objData.delete.dialog = true" color="primary"/>
              </div>
            </div>
          </div>
        </div>

        <!--商品-->
        <div class="col-sm-12 col-md-9">
          <div class="row items-center q-mx-sm">
            <div class="col-12 q-mt-sm">
              <div class="q-pl-md bg-white q-py-sm ">
                <span class="text-grey-9 text-h6 text-weight-bold">商品列表</span>
                <div class="q-pa-lg flex flex-center">
                  <q-pagination
                    v-model="objData.current_page"
                    :max="page_count"
                    :max-pages="6"
                    direction-links
                    @input="getItem(objData.current_page, objData.num_per_page, objData.default.orderType)"
                  />
                </div>
  
                <div class="row fit justify-start items-center q-gutter-xs q-col-gutter no-wrap">
                  <q-card @click="$router.push('/details')" 
                    v-for="i in objData.item" v-bind:key="i.itemID"
                    class="col-lg-3 col-md-3 col-sm-12 col-xs-12 text-center full-height" style="cursor: pointer;">
  
                    <img :src="i.img" :contain=True style="height:300px">
  
                    <q-card-section class="q-mb-sm" style="height:80px">
                      <div class="text-subtitle"> {{ i.itemName }} </div>
                      <div class="text-subtitle2 ">作者：{{ i.author }} </div>
                    </q-card-section>
  
                    <q-card-section class="q-pt-none" style="height:50px">
                      <div>
                        <span>￥ {{ i.price }}</span>
                      </div>
                    </q-card-section>
  
                  </q-card>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 上架商品 -->
    <q-dialog v-model="objData.create.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">上架商品</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input dense v-model="objData.create.id" filled mask="##########" 
          :type="text" label="[必填]书籍ID">
          </q-input>
          <q-input dense v-model="objData.create.title" filled 
          :type="text" label="[必填]书名">
          </q-input>
          <q-input dense v-model="objData.create.author" filled 
          :type="text" label="著者">
          </q-input>
          <q-input dense v-model="objData.create.publisher" filled 
          :type="text" label="出版社">
          </q-input>
          <q-input dense v-model="objData.create.original_title" filled 
          :type="text" label="原书题目">
          </q-input>
          <q-input dense v-model="objData.create.translator" filled 
          :type="text" label="译者">
          </q-input>
          <q-input dense v-model="objData.create.pub_year" filled 
          :type="text" label="出版年月">
          </q-input>
          <q-input dense v-model="objData.create.pages" filled mask="##########" 
          :type="text" label="页数">
          </q-input>
          <q-input dense v-model="objData.create.price" filled mask="##########" 
          :type="text" label="[必填]价格（以分为单位）">
          </q-input>
          <q-input dense v-model="objData.create.binding" filled 
          :type="text" label="装帧（精装/平装）">
          </q-input>
          <q-input dense v-model="objData.create.isbn" filled mask="#############" 
          :type="text" label="ISBN">
          </q-input>
          <q-input dense v-model="objData.create.author_intro" filled 
          :type="text" label="著者介绍">
          </q-input>
          <q-input dense v-model="objData.create.book_intro" filled 
          :type="text" label="书籍介绍">
          </q-input>
          <q-input dense v-model="objData.create.content" filled 
          :type="text" label="样章试读">
          </q-input>
          <q-input dense v-model="objData.create.tags" filled 
          :type="text" label="标签（请用空格隔开）">
          </q-input>
          <q-input dense v-model="objData.create.stock_level" filled mask="##########" 
          :type="text" label="初始库存">
          </q-input>

          <q-file
            v-model="objData.create.images"
            filled dense
            label="点击上传图片"
            multiple
            accept="image/*"
          />

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消创建" @click="objData.create.images=null" v-close-popup />
          <q-btn flat label="确认创建" @click="add_book" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 下架商品 -->
    <q-dialog v-model="objData.withdraw.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">下架商品</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input dense v-model="objData.withdraw.book_id" filled mask="##########" 
          :type="text" label="书籍ID">
          </q-input>

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消" v-close-popup />
          <q-btn flat @click="withdraw" label="确认" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 添加库存 -->
    <q-dialog v-model="objData.add.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">添加库存</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input dense v-model="objData.add.book_id" filled mask="##########" 
          :type="text" label="书籍ID">
          </q-input>
          <q-input dense v-model="objData.add.add_stock_level" filled mask="##########" 
          :type="text" label="添加库存">
          </q-input>

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消" v-close-popup />
          <q-btn flat @click="add_stock_level" label="确认" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 删除商店 -->
    <q-dialog v-model="objData.add.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <q-avatar icon="location_disabled" color="primary" text-color="white" />
          <span class="q-ml-sm">您确认删除商店吗？该过程不可逆。</span>
        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消" v-close-popup />
          <q-btn flat label="确认" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>


  </q-page>
</template>

<script>
import { reactive, computed, onMounted } from 'vue'
import { useRouter, useRoute } from "vue-router";
import { api, backend_port, testurl } from "../boot/axios";

export default {
  setup () {
    const router = useRouter()
    const route = useRoute()

    let objData = reactive({
      current_page: 1,
      user_id: '',
      store_id:  '',
      is_my_shop: false,
      num_per_page: 4,
      item_count: 20,
      keyword: '',

      add: {
        dialog: false,
        book_id: "",
        add_stock_level: 0,
      },
      delete: {
        dialog: false,
      },
      withdraw: {
        dialog: false,
        book_id: '',
      },
      create: {
        dialog: false,
        tags: "",
        id: "",
        title: "",
        author: "",
        publisher: "",
        original_title: "",
        translator: "",
        pub_year: "",
        pages: "",
        price: "",
        binding: "",
        isbn: "",
        author_intro: "",
        book_intro: "",
        content: "",
        stock_level: "",
        images: null,
      },
      default: {
        slide: "first",
        latest_slide: 1,
        shopName: "商店A",
        seller: "小王",
        address: "上海 闵行",
        searchText: "",
        orderType: 0,
      },
      item: [
        {
          itemID: '1234512345',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《他改变了中国》',
          author: 'ABC',
          price: 48,
        },
        {
          itemID: '1234512345',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《他改变了中国》',
          author: 'ABC',
          price: 48,
        },
        {
          itemID: '12345123456',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《数据科学与工程专业人才培养方案与核心课程体系》',
          author: 'ABC',
          price: 20,
        },
        {
          itemID: '12345123456',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《数据科学与工程专业人才培养方案与核心课程体系》',
          author: 'ABC',
          price: 20,
        }
      ]
    });

    function add_book() {
      let pictures = warn(objData.create.images);
      let tags = str.trim().split(/\s+/);
      let body = {
        "user_id": objData.user_id,
        "store_id": objData.store_id,
        "book_info": {
          "tags": tags,
          "pictures": pictures,
          "title": objData.create.title,
          "author": objData.create.author,
          "publisher": objData.create.publisher,
          "original_title": objData.create.original_title,
          "translator": objData.create.translator,
          "pub_year": objData.create.pub_year,
          "pages": objData.create.pages,
          "price": objData.create.price,
          "binding": objData.create.binding,
          "isbn": objData.create.isbn,
          "author_intro": objData.create.author_intro,
          "book_intro": objData.create.book_intro,
          "content": objData.create.content,
        },
        "stock_level": 0
      };
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/add_book";
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("添加成功！");
          location.reload();
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function delete_shop() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/store/" + objData.user_id + '/' + objData.store_id;
      api.delete(url).then((response) => {
        if(response.status == 200) {
          response.data.stores.forEach(element => {
            if (element.store_id == objData.store_id) {
              objData.is_my_shop = true;
            }
          });
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function add_stock_level() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/add_stock_level";
      let body = {
        "user_id": objData.user_id,
        "store_id": objData.store_id,
        "book_id": objData.add.book_id,
        "add_stock_level": objData.add.add_stock_level,
      };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("库存添加成功！")
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function withdraw() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/book/" + objData.user_id + '/' + objData.withdraw.book_id;
      api.delete(url).then((response) => {
        if(response.status == 200) {
          alert('下架成功！');
          location.reload();
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    };

    function getItem(page, num_per_page, strategy) {
      objData.item = [];
      objData.current_page = page;
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/book/"
       + objData.store_id + "/" + page + "/" + num_per_page + "/" + (strategy+1) + "?keyword="+ objData.default.searchText
      api.get(url).then((response) => {
        if(response.status == 200) {
          objData.item_count = response.data.count;
          response.data.books.forEach(element => {
            objData.item.push({
              itemID: element.id,
              img: 'data:image/png;base64,' + element.book_info.picture,
              itemName: element.book_info.title,
              author: element.book_info.author,
              price: element.price / 100,
            });
          });
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function getImgToBase64(img) {//将图片转换为Base64
      // let url = URL.createObjectURL(img);
      var canvas = document.createElement('canvas'),
        ctx = canvas.getContext('2d'),
        img = new Image;
      img.crossOrigin = 'Anonymous';
      alert(canvas.toDataURL('image/png'));
      return canvas.toDataURL('image/png');
    };

    function warn(imgs) {
      var base = new Array()
      for (let i=0; i<imgs.length; i++) {
        base.push(getImgToBase64(imgs[i]))
      }
      return base
    };

    onMounted(() => {
      objData.user_id = window.localStorage.getItem('user_id');
      objData.store_id = route.query.store_id;

      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/store/" + objData.user_id;
      api.get(url).then((response) => {
        if(response.status == 200) {
          response.data.stores.forEach(element => {
            if (element.store_id == objData.store_id) {
              objData.is_my_shop = true;
            }
          });
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });

      let url2 = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/store/" + objData.store_id;
      api.get(url2).then((response) => {
        if(response.status == 200) {
          objData.default.shopName = response.data.store.store_id;
          objData.default.seller = response.data.store.seller_id;
          objData.default.address = response.data.store.address;
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });

      getItem(1, objData.num_per_page, objData.default.orderType);

    })

    let page_count = computed(() => {
      return Math.ceil(objData.item_count / objData.num_per_page)
    })

    let tags = computed(() => {
      objData.keyword = objData.default.searchText.trim().split(/\s+/).join(' ')
      objData.default.searchText.trim().split(/\s+/)
    })

    return {
      objData,
      add_book,
      delete_shop,
      add_stock_level,
      withdraw,
      getItem,
      page_count,
      tags,
    }
  },
};
</script>

<style lang="sass" scoped>
.custom-caption
  text-align: center
  padding: 8px
  color: white
  background-color: rgba(0, 0, 0, .3)
</style>
