<template>
  <div>
    <div class="row bg-white q-mt-sm">
      <div class="col-lg-5 col-md-5 col-sm-12 col-xs-12">
        <div class="q-pa-md">

          <!-- <q-carousel
            swipeable
            animated
            v-model="objData.slide"
            thumbnails
            infinite
            v-for="i in objData.book_info.pictures" v-bind:key="i"
          >
            <q-carousel-slide :name="i" :img-src="i"/>
          </q-carousel> -->
          <img :src="objData.book_info.pictures[0]" style="height:300px" contain>


        </div>
      </div>
      <div class="col-lg-7 col-md-7 col-sm-12 col-xs-12">
        <!--<q-scroll-area :style="{'height':(win_height-200)+'px'}">-->

          
        <div class="row">
          <div class="col-lg-7 col-md-7 col-sm-12 col-xs-12" :class="$q.platform.is.desktop ? '' : 'q-px-md'">
            <div class="text-subtitle1 text-grey-10 q-mt-sm q-pt-xs"> {{ objData.book_info.title }}
            </div>
            <div>
              <q-chip size="10px" class="text-weight-bold q-px-xs" square color="green-7" text-color="white"
                      icon-right="star">
                {{ star_avg }}
              </q-chip>
              <span class="text-caption text-weight-bold text-grey-6"> 评价</span>
            </div>
            <div>
              <div class="text-caption text-green-8 text-weight-bolder q-mt-sm">特价好货</div>
              <span class="text-h6">￥ {{ objData.book_info.price / 100 }} </span>
              <!-- <span class="q-ml-sm text-grey-6" style="text-decoration: line-through">￥48</span>
              <span class="q-ml-md text-caption text-green-8 text-weight-bolder q-mt-md">12.5% off</span> -->
            </div>
            <!--
            <div class="q-mt-sm text-weight-bold">
              Offers
              <ul class="q-pl-sm text-caption" style="list-style-type: none">
                <li class="q-mt-xs"><span class="text-weight-bold">Bank Offer</span> 5% Unlimited Cashback on Axis Bank
                  Credit
                  Card <a class="text-primary text-weight-bolder">T&C</a></li>
                <li class="q-mt-xs"><span class="text-weight-bold">Bank Offer</span> 5% Unlimited Cashback on Axis Bank
                  Credit
                  Card <a class="text-primary text-weight-bolder">T&C</a></li>
                <li class="q-mt-xs"><span class="text-weight-bold">Bank Offer</span> OfferExtra 5% off* <a
                  class="text-primary text-weight-bolder">T&C</a></li>
              </ul>
            </div>
            -->
            <div class="q-mt-sm">
              <div class="text-subtitle1 text-green-8 text-weight-bold">现货</div>
              <!-- <div class="text-caption q-mt-sm text-grey-8 text-weight-bold">48H内发货</div> -->
              <!--
              <div class="text-caption q-mt-sm text-grey-8 text-weight-bold">Delivery by: <span class="text-black">Tue, Mar 3</span>
              </div>
              <div class="text-caption text-subtitle2 text-grey-8 text-weight-bold">Fastest delivery: <span
                class="text-black">Mon, Mar 2 by 9pm</span></div>
              -->
            </div>
            <div class="q-mt-md">
              <q-btn class="q-mt-md" @click="addCart" color="orange-9" icon="shopping_cart" label="加入购物车"/>
              <q-btn class="q-mt-md q-ml-md" @click="objData.buy.dialog = true;" color="orange-8" icon="shopping_cart" label="立即购买"/>
            </div>
          </div>
          <div class="col-lg-5 col-md-5 col-sm-12 col-xs-12 q-mt-md q-pt-xs q-pl-lg">
            <div class="text-subtitle2">用户评分</div>
            <div class="text-h3">{{ star_avg }}</div>
            <div>
              <q-rating
                v-model="objData.star_avg"
                max="5"
                size="2em"
                color="orange"
                icon="star_border"
                icon-selected="star"
                icon-half="star_half"
                no-dimming
                readonly
              />
            </div>
            <div class="text-subtitle2 text-grey-8">(评价)</div>
            <div class="text-subtitle2 text-grey-10 q-mt-sm">推荐</div>

            <!-- <q-list dense bordered padding class="no-border q-mt-lg q-pr-xl">
              <q-item style="padding-left: 0 !important;" v-ripple>
                <span class="text-subtitle2 q-mr-xs">5</span>
                <q-icon name="star" size="1.5em" color="orange"></q-icon>
                <q-linear-progress class="q-ml-sm  q-mr-sm" style="margin-top: 5px;" size="13px" :value="objData.content.count[4] / eval(objData.content.count.join('+'))"/>
                <span style="margin-top: 2px" class="text-caption text-weight-bold text-grey-8"> {{ objData.content.count[4] }} </span>
              </q-item>
              <q-item style="padding-left: 0 !important;" v-ripple>
                <span class="text-subtitle2 q-mr-xs">4</span>
                <q-icon name="star" size="1.5em" color="orange"></q-icon>
                <q-linear-progress class="q-ml-sm  q-mr-sm" style="margin-top: 5px;" size="13px" :value="objData.content.count[3] / eval(objData.content.count.join('+'))"/>
                <span style="margin-top: 2px" class="text-caption text-weight-bold text-grey-8">&nbsp;&nbsp; {{ objData.content.count[3] }} </span>
              </q-item>
              <q-item style="padding-left: 0 !important;" v-ripple>
                <span class="text-subtitle2 q-mr-xs">3</span>
                <q-icon name="star" size="1.5em" color="orange"></q-icon>
                <q-linear-progress class="q-ml-sm  q-mr-sm" style="margin-top: 5px;" size="13px" :value="objData.content.count[2] / eval(objData.content.count.join('+'))"/>
                <span style="margin-top: 2px" class="text-caption text-weight-bold text-grey-8">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {{ objData.content.count[2] }} </span>
              </q-item>
              <q-item style="padding-left: 0 !important;" v-ripple>
                <span class="text-subtitle2 q-mr-xs">2</span>
                <q-icon name="star" size="1.5em" color="orange"></q-icon>
                <q-linear-progress class="q-ml-sm  q-mr-sm" style="margin-top: 5px;" size="13px" :value="objData.content.count[1] / eval(objData.content.count.join('+'))"/>
                <span style="margin-top: 2px" class="text-caption text-weight-bold text-grey-8">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {{ objData.content.count[1] }} </span>
              </q-item>
              <q-item style="padding-left: 0 !important;" v-ripple>
                <span class="text-subtitle2 q-mr-xs">1</span>
                <q-icon name="star" size="1.5em" color="orange"></q-icon>
                <q-linear-progress class="q-ml-sm  q-mr-sm" style="margin-top: 5px;" size="13px" :value="objData.content.count[0] / eval(objData.content.count.join('+'))"/>
                <span style="margin-top: 2px" class="text-caption text-weight-bold text-grey-8">&nbsp;&nbsp;&nbsp;&nbsp;&nbsp; {{ objData.content.count[0] }} </span>
              </q-item>

            </q-list> -->
          </div>
        </div>

        <!--</q-scroll-area>-->
      </div>
    </div>

    
    <div class="row q-mt-sm">
      <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">

        <q-tabs
          v-model="objData.tab"
          dense
          align="left"
          class="bg-primary text-white shadow-2"
          :breakpoint="0"
        >
          <q-tab name="Specifications" label="商品详情"/>
          <q-tab name="Ratings Reviews" label="商品评价"/>
          <q-tab name="Similar products" label="相似商品"/>
          <q-tab name="Bought together" label="组合优惠"/>
          <q-tab name="Recently Viewed" label="最近浏览"/>
        </q-tabs>

        <q-tab-panels style="border: 1px solid lightgrey" v-model="objData.tab">

          <q-tab-panel name="Specifications">
            <div class="row">
              <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                <q-list bordered class="rounded-borders" style="height: 100%">
                  <q-item-label class="text-weight-bolder" header>规格</q-item-label>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">页数：</span>
                      <span> {{ objData.book_info.pages }} </span>
                    </q-item-section>
                    <q-item-section>
                      <span class="text-caption text-grey">包装</span>
                      <span> {{ objData.book_info.binding }} </span>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">是否套装</span>
                      <span>否</span>
                    </q-item-section>
                    <q-item-section>
                      <span class="text-caption text-grey">ISBN：</span>
                      <span>9787532736553</span>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                <q-list bordered class="rounded-borders" style="height: 100%">
                  <q-item-label class="text-weight-bolder" header>详情</q-item-label>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">著者：</span>
                      <span> {{ objData.book_info.author }} </span>
                    </q-item-section>
                    <q-item-section>
                      <span class="text-caption text-grey">出版社：</span>
                      <span> {{ objData.book_info.publisher }} </span>
                    </q-item-section>
                    <q-item-section>
                      <span class="text-caption text-grey">原书名：</span>
                      <span> {{ objData.book_info.original_title }} </span>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">译者：</span>
                      <span> {{ objData.book_info.translator }} </span>
                    </q-item-section>
                    <q-item-section>
                      <span class="text-caption text-grey">出版时间：</span>
                      <span> {{ objData.book_info.pub_year }} </span>
                    </q-item-section>
                    <q-item-section>
                      <span class="text-caption text-grey">ISBN：</span>
                      <span> {{ objData.book_info.isbn }} </span>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">标签：</span>
                      <span> {{ objData.book_info.tags.join(" ") }} </span>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
              <div class="col-lg-4 col-md-4 col-sm-12 col-xs-12">
                <q-list bordered class="rounded-borders" style="height: 100%">
                  <q-item-label class="text-weight-bolder" header>简介</q-item-label>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">作者简介</span>
                      <span> {{ objData.book_info.authorIntro }} </span>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">内容简介</span>
                      <span> {{ objData.book_info.bookIntro }} </span>
                    </q-item-section>
                  </q-item>
                  <q-item>
                    <q-item-section>
                      <span class="text-caption text-grey">章节试读</span>
                      <span> {{ objData.book_info.content }} </span>
                    </q-item-section>
                  </q-item>
                </q-list>
              </div>
            </div>

          </q-tab-panel>

          <q-tab-panel name="Ratings Reviews">
            <div class="row">
              <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">
                <q-item-label class="text-weight-bolder text-green-8" header>评价</q-item-label>
                <q-list bordered class="rounded-borders" style=""
                v-for="comment in objData.content.comments" v-bind:key="comment.user_id">

                  <q-item v-ripple>
                    <q-item-section avatar>
                      <q-avatar>
                        <img src="https://cdn.quasar.dev/img/avatar2.jpg">
                      </q-avatar>
                    </q-item-section>

                    <q-item-section>
                      <q-item-label lines="1">打分：{{ comment.star }}</q-item-label>
                      <q-item-label caption lines="2">
                        <span class="text-weight-bold"> {{ comment.user_id }} </span>
                         {{ comment.text }} 
                      </q-item-label>
                    </q-item-section>

                    <q-item-section side top>
                      {{ comment.time }} 
                    </q-item-section>
                  </q-item>

                  <q-separator inset="item"/>

                </q-list>
              </div>
            </div>

          </q-tab-panel>


        </q-tab-panels>
      </div>
    </div>

    <!-- 直接购买 -->
    <q-dialog v-model="objData.buy.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">选择数量</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input
            v-model.number="objData.buy.amount"
            mask="####"
            type="number"
            filled
            style="max-width: 100px"
            lazy-rules
            :rules="[ val => val >= 1 && val % 1 === 0 || '数量必须为正整数']"
            @blur="() => {
              objData.buy.amount = objData.buy.amount % 1 === 0 ? objData.buy.amount : Math.floor(objData.buy.amount);
              objData.buy.amount = objData.buy.amount > 0 ? objData.buy.amount : 1;
              }"
          />

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消下单" v-close-popup />
          <q-btn flat @click="buy" label="确认下单" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 加购物车 -->
    <q-dialog v-model="objData.add.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">选择数量</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input
            v-model.number="objData.add.amount"
            mask="####"
            type="number"
            filled
            style="max-width: 100px"
            lazy-rules
            :rules="[ val => val >= 1 && val % 1 === 0 || '数量必须为正整数']"
            @blur="() => {
              objData.add.amount = objData.add.amount % 1 === 0 ? objData.add.amount : Math.floor(objData.add.amount);
              objData.add.amount = objData.add.amount > 0 ? objData.add.amount : 1;
              }"
          />

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消加购" v-close-popup />
          <q-btn flat @click="addCart" label="确认加购" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </div>
</template>

<script>
import { reactive, computed, onMounted } from 'vue' 
import { api, backend_port, testurl } from "../boot/axios";
import { useRouter, useRoute } from "vue-router";

  export default {
    setup() {
      const router = useRouter()
      const route = useRoute()

      let objData = reactive({
        user_id: '',
        itemID: '',
        store_id: '',
        slide: 1,
        tab: 'Specifications',
        rating_point: 4.3,

        buy: {
          dialog: false,
          amount: 1,
        },

        add: {
          dialog: false,
          amount: 1,
        },

        book_info:{
          id: '',
        	title: '',
        	author: '',
        	publisher: '',
        	original_title: '',
        	translator: '',
        	pub_year: '',
        	pages: '',
        	price: '',
        	binding: '',
        	isbn: '',
        	authorIntro: '',
        	bookIntro: '',
        	content: '',
        	tags:[''],
        	pictures:['']
        },

        content: {
          book_id: '', 
          count:[1,2,3,4,5], // 长度是5的数组，表示从1-5星的数量
          comments:[
            {
            	user_id: '',
          	  star: '', // 1-5
          	  text: '',
          	  time: ''
            },
          ]
	      }


      });

      function buy() {
        let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/new_order";
        let body = {
          "user_id": objData.user_id,
          "store_id": objData.store_id,
          "books": [{
            id: objData.book_info.id,
            count: objData.buy.amount,
          }]
        }
        api.post(url, body).then((response) => {
          if(response.status == 200) {
            alert('下单成功!')
            router.push({path:"/order", query:{type:"buy"}})
          }
        })
        .catch((error) => {
          alert(error.response.data.message);
        });
      }

      function addCart() {
        let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/cart";
        let body = {
          buyer_id: objData.user_id, // string 用户名
          book_id: objData.itemID, // long 商品id
          count: objData.add.amount, // int 数量
        }
        api.post(url, body).then((response) => {
          if(response.status == 200) {
            alert('添加成功!')
          }
        })
        .catch((error) => {
          alert(error.response.data.message);
        });
      }

      onMounted(() => {
        objData.user_id = window.localStorage.getItem('user_id');
        objData.itemID = route.query.itemID;
        let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/book_info/" + objData.itemID;
        api.get(url).then((response) => {
          if(response.status == 200){
            objData.book_info = response.data.book_info
            objData.store_id = response.data.store.store_id // seller_id / sell
            for (let i=0; i<objData.book_info.pictures.length; i++) {
              objData.book_info.pictures[i] = 'data:image/png;base64,' +  objData.book_info.pictures[i]
            }
          }
        })
        .catch((error) => {
          alert(error.response.data.message);
        });

        let url2 = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/comment/" 
        + objData.itemID + '/1/10';
        api.get(url2).then((response) => {
          if(response.status == 200){
            objData.content = response.data.content
          }
        })
        .catch((error) => {
          alert(error.response.data.message);
        });
      });

      let star_avg = computed(() => {
        let total = 0;
        let sum = 0;
        let star = 1;
        objData.content.count.forEach(element => {
          total += element;
          sum += element * star;
          star++
        });
        return (sum / total).toFixed(2)
      });

      let win_width = computed(() => {
        return this.$q.screen.width - 59;
      });
      
      let win_height = computed(() => {
        return this.$q.screen.height - 0;
      });

      return {
        objData,
        buy,
        addCart,
        star_avg,
        win_width,
        win_height
      }
    },
  }
</script>

<style scoped>

</style>
