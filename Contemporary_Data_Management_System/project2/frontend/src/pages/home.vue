<template>
  <q-page class="q-mt-sm">

    <div class="text-grey-9 text-weight-bold">

      <!--商品-->
      <div class="row items-center q-mx-sm">
        <div class="col-12 q-mt-sm">
          <div class="q-pl-md bg-white q-pt-sm ">
            <span class="text-grey-9 text-h6 text-weight-bold">最新发布</span>
            <a class="text-primary q-ml-sm cursor-pointer">[更多]</a>
          </div>
          <q-carousel
            v-model="objData.default.latest_slide"
            transition-prev="slide-right"
            transition-next="slide-left"
            swipeable
            animated
            control-color="primary"
            navigation
            padding
            arrows
            height="540px"
            class="rounded-borders"
          >
            <q-carousel-slide
              v-for="(val, idx) in [1, 2, 3]"
              :name="val"
              :key="idx"
              class="column no-wrap"
            >
              <div class="row fit justify-start items-center q-gutter-xs q-col-gutter no-wrap">
                <q-card @click="$router.push({path: '/details', query:{itemID: i.itemID}})" v-for="i in objData.item.slice(objData.default.latest_slide*4-4, objData.default.latest_slide*4)" :name="i.itemName" :key="i.itemID"
                  class="col-lg-3 col-md-3 col-sm-12 col-xs-12 text-center full-height" style="cursor: pointer;">
                  <img :src="i.img" style="height:300px" contain>
                  <q-card-section>
                    <div class="text-h6"> {{ i.itemName }} </div>
                    <div class="text-subtitle2">作者：{{ i.author }} </div>
                  </q-card-section>

                  <q-card-section class="q-pt-none">
                    <div>
                      <span>￥{{ i.price }}</span>
                    </div>
                  </q-card-section>
                </q-card>
                
              </div>

            </q-carousel-slide>
          </q-carousel>
        </div>
      </div>

      <!--商店-->
      <div class="row items-center q-mx-sm">
        <div class="col-12 q-mt-sm">
          <div class="q-pl-md bg-white q-pt-sm ">
            <span class="text-grey-9 text-h6 text-weight-bold">逛逛好店</span>
            <a class="text-primary q-ml-sm cursor-pointer">[更多]</a>
          </div>
          <q-carousel
            v-model="objData.default.shop_slide"
            transition-prev="slide-right"
            transition-next="slide-left"
            swipeable
            animated
            control-color="primary"
            navigation
            padding
            arrows
            height="450px"
            class="rounded-borders"
          >
            <q-carousel-slide
              v-for="(val, idx) in [1, 2, 3]"
              :name="val"
              :key="idx"
              class="column no-wrap"
            >
              <div class="row fit justify-start items-center q-gutter-xs q-col-gutter no-wrap">
                <div class="row fit justify-start items-center q-gutter-xs q-col-gutter no-wrap">
                  <q-card @click="$router.push({path:'/shop', query:{store_id:s.shopName}})" 
                  v-for="s in objData.shop.slice(objData.default.shop_slide*4-4, objData.default.shop_slide*4)" :name="s.itemName" :key="s.itemID"
                  class="col-lg-3 col-md-3 col-sm-12 col-xs-12 text-center full-height" style="cursor: pointer;">
                  <img src="https://cdn.quasar.dev/img/mountains.jpg" :contain=True style="height:200px">

                  <q-card-section>
                    <div class="text-h6"> {{ s.shopName }} </div>
                    <div class="text-subtitle2">卖家：{{ s.seller }} </div>
                    <div class="text-subtitle2">地址：{{ s.address }} </div>
                  </q-card-section>

                  </q-card>
                </div>
              </div>
            </q-carousel-slide>
          </q-carousel>
        </div>
      </div>


      <!--流行标签-->
      <!--
      <div class="row items-center q-mx-sm">
        <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12 q-mt-sm">
          <div class="q-pl-md bg-white q-pt-sm">
            <span class="text-grey-9 text-h6 text-weight-bold">流行标签</span>
            <a class="text-primary q-ml-sm cursor-pointer">[更多]</a>
          </div>
          <q-carousel
            v-model="objData.default.trending_slide"
            transition-prev="slide-right"
            transition-next="slide-left"
            swipeable
            animated
            control-color="primary"
            navigation
            padding
            arrows
            height="260px"
            class="rounded-borders"
          >
            <q-carousel-slide
              v-for="(val, idx) in [1, 2, 3]"
              :name="val"
              :key="idx"
              class="column no-wrap"
            >
              <div class="row fit justify-start items-center q-gutter-xs q-col-gutter no-wrap">
                <q-card class="col-lg-3 col-md-3 col-sm-12 col-xs-12 "
                v-for="t in objData.tag.slice(objData.default.trending_slide*4-4, objData.default.trending_slide*4)" :key="t.tagName">
                  <q-img
                  @click="$router.push('/category')"
                  style="border:1px solid lightgrey;"
                  class="rounded-borders cursor-pointer full-height"
                  src="https://www.graphicsprings.com/filestorage/stencils/1cdf3eae16b6d0da3f16ff12f7650a24.png"
                  >
                    {{ objData.default.trending_slide }}
                    <div
                      class="absolute-bottom custom-caption"
                      style="background-color:rgba(0, 0, 0, .5);"
                    >
                      <div class="text-caption text-weight-bolder"> {{ t.tagName }} </div>
                    </div>
                  </q-img>
                </q-card>
              </div>
            </q-carousel-slide>
          </q-carousel>
        </div>
      </div>
      -->
    </div>
  </q-page>
</template>

<script>
import { onMounted, reactive } from 'vue'
import { api, backend_port, testurl } from "../boot/axios";

export default {
  setup () {
    let objData = reactive({
      default: {
        latest_slide: 1,
        shop_slide: 1,
        trending_slide: 1,
      },
      shop: [
        {
          shopName: '商店A',
          seller: '小王',
          address: '上海 闵行',
        },
        {
          shopName: '商店A',
          seller: '小王',
          address: '上海 闵行',
        },
        {
          shopName: '商店A',
          seller: '小王',
          address: '上海 闵行',
        },
        {
          shopName: '商店A',
          seller: '小王',
          address: '上海 闵行',
        },
        {
          shopName: '商店A',
          seller: '小王',
          address: '上海 闵行',
        },
        {
          shopName: '商店A',
          seller: '小王',
          address: '上海 闵行',
        },
      ],
      tag: [
        {
          tagName: "旅游",
        },
        {
          tagName: "美食",
        },
        {
          tagName: "养生",
        },
        {
          tagName: "时尚",
        },
        {
          tagName: "科普",
        },
        {
          tagName: "计算机"
        },
        {
          tagName: "医学",
        },
        {
          tagName: "传记",
        },
      ],
      item: [
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
      ]
    });

    function refresh() {
      // let url = getHostUrl() + ":5000/auth/login";
      let url = 'https://www.runoob.com/try/ajax/json_demo.json'
      api.get(url).then((response) => {
        alert(response)
      });
      // let body = { user_id: state.user_id, password: state.password, terminal: 1 };
      // api.post(url, body).then((response) => {
      //   state.is_loading = false;
      //   objData.info = response.data.name
      //   // if(response.status == 200){
      //   //   router.push("/board");
      //   // }
      //   // else if(response.status == 401){
      //   //   alert("用户名或密码错误！");
      //   // }
      //   // else{
      //   //   alert("网络请求发生错误！");
      //   // }
      // })
      // .catch((error) => {
      //   console.log(error);
      // });
    };
    
    onMounted(() => {
      objData.item = [];

      objData.shop = [];
      let url2 = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/stores/12"
      api.get(url2).then((response) => {
        if(response.status == 200) {
          response.data.stores.forEach(element => {
            objData.shop.push({
              shopName: element.store_id,
              seller: element.seller_id,
              address: element.address 
            });
          });
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });

      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/book/0/1/12/0?keyword="
      api.get(url).then((response) => {
        if(response.status == 200) {
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
    });

    return {
      objData,
      refresh,
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
