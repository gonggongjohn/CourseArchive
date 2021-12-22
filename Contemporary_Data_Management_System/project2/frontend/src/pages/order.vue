<template>
  <q-page class="q-mt-sm">
    <div class="text-grey-9 text-weight-bold">

      <!--商品-->
      <div class="row items-center q-mx-sm">
        <div class="col-12 bg-white q-mt-sm">
          <div class="q-pl-md q-pt-sm ">
            <span class="text-grey-9 text-h6 text-weight-bold">我的订单</span>
          </div>

            <div class="q-pa-lg flex flex-center">
              <q-pagination
                v-model="objData.current_page"
                :max="1"
                :max-pages="6"
                direction-links
              />
            </div>

            <div v-for="order in objData.data" v-bind:key="order.orderID">
              <q-card class="order center" style="min-height:200px;width:100%">
                <div class="row">
                  <q-card-section class="col">
                      <div class="row">
                        <div class="col-4 text-subtitle2"> {{ order.orderTime }} </div>
                        <div class="col-4 text-subtitle2"> 订单号：{{ order.orderID }} </div>
                        <div class="col-4 text-subtitle2"> 店铺：{{ order.shop }} </div>
                      </div>
                      <q-separator class="q-pt-none"></q-separator>
                      <div class="row q-pt-sm">
                        <div class="col-9">
                          <q-markup-table :separator="separator" flat bordered>
                            <thead>
                              <tr>
                                <th class="text-left" style="width:70%">商品信息</th>
                                <th class="text-left" style="width:10%">单价</th>
                                <th class="text-left" style="width:10%">数量</th>
                                <th class="text-left" style="width:10%">金额</th>
                              </tr>
                            </thead>
                            <tbody v-for="item in order.item" v-bind:key="item.itemID">
                              <tr>
                                <td class="text-left">
                                  <q-avatar size="80px" square><img :src="item.img"></q-avatar>
                                  {{ item.itemName }}
                                </td>
                                <td class="text-left"> {{ item.price }}</td>
                                <td class="text-left"> {{ item.amount }} </td>
                                <td class="text-left"> {{ item.price * item.amount }} </td>
                              </tr>
                            </tbody>
                            </q-markup-table>
                        </div>
                        <div class="col-2 text-center">
                          <q-list class="q-mx-sm" bordered separator>
                            <q-item v-ripple>
                              <q-item-section class="text-body2">实付款</q-item-section>
                            </q-item>
                            <q-item v-ripple>
                              <q-item-section>
                                <q-item-label class="text-h6 "> ¥{{ order.paid }} </q-item-label>
                              </q-item-section>
                            </q-item>
                          </q-list>
                          <q-list class="q-mx-sm q-mt-sm" bordered separator>
                            <q-item v-ripple>
                              <q-item-section class="text-body2"> {{ order.state }} </q-item-section>
                            </q-item>
                          </q-list>
                        </div>
                        <div class="col-1 text-subtitle2 text-right">
                          <q-btn class="q-my-xs" v-if="objData.type == 'buy' && order.status == 0" @click="pay(order.orderID)" label="付款"  color="primary"/><br/>
                          <q-btn class="q-my-xs" v-if="objData.type == 'buy' && order.status == 0" @click="order_cancel(order.orderID)" label="取消订单"  color="primary"/><br/>
                          <q-btn class="q-my-xs" v-if="objData.type == 'buy' && order.status == 2" @click="order_receive(order.orderID)" label="确认收货"  color="primary"/><br/>
                          <q-btn class="q-my-xs" v-if="objData.type == 'buy' && order.status == 3" @click="objData.comment.orderID = order.orderID;objData.comment.dialog = true;" label="买家评价"  color="primary"/><br/>
                          <q-btn class="q-my-xs" v-if="objData.type == 'sell' && order.status == 1" @click="objData.order_send.orderID = order.orderID;objData.order_send.dialog = true;" label="发货"  color="primary"/>
                        </div>
                      </div>
                      <q-separator class="q-mt-sm"></q-separator>
                      <div class="row">
                        <div class="col-5 text-subtitle2"> 收货地址：{{ order.username }} {{ order.address }} </div>
                        <div class="col-5 text-subtitle2 text-center"> 物流单号：{{ order.trackingNumber }} </div>
                        <div class="col-2 text-subtitle2 text-right"> 运费：¥{{ order.freight }} </div>
                      </div>
                    </q-card-section>
                </div>
              </q-card>
            </div>

            <div class="q-pa-lg flex flex-center">
              <q-pagination
                v-model="objData.current_page"
                :max="objData.total_page"
                :max-pages="6"
                direction-links
              />
            </div>

          </div>
      </div>
      
    </div>

    <!-- 发货 -->
    <q-dialog v-model="objData.order_send.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">发货</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input dense v-model="objData.order_send.trackingNumber" filled 
          :type="text" label="物流单号">
          </q-input>

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消" v-close-popup />
          <q-btn flat @click="order_send" label="确认发货" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 卖家评价 -->
    <q-dialog v-model="objData.comment.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">评价</div>
        </q-card-section>

        <q-card-section class="q-pt-none"
         v-for="item in onComment" v-bind:key="item.itemID">

          <span class="q-ml-sm"> 商品：{{ item.itemName }} </span>
          <!-- v-model="objData.comment.text"  -->
          <q-input dense filled v-on:input="objData.comment.all_comment[itemID]['text'] = $event.target.value; 
          objData.comment.all_comment[itemID]['score'] = objData.comment.all_comment[itemID]['score'] || 5"
          v-bind:value="showComment(item.itemID)['text']" :type="text" label="评价内容">  </q-input>

          <q-badge color="secondary">
            打分: {{ showComment(item.itemID)['score'] }} 
          </q-badge>
          <q-slider
            v-on:input="objData.comment.all_comment[itemID]['score'] = $event.target.value"
            color="green"
            :min="1"
            :step="1"
            :max="5"
          />

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat @click="comment.all_comment={}" label="取消" v-close-popup />
          <q-btn flat @click="comment" label="确认评价" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import { reactive, computed, onMounted } from 'vue' 
import { api, backend_port, testurl } from "../boot/axios";
import { useRouter, useRoute } from "vue-router";

export default {
  setup() {
    const router = useRouter()
    const route = useRoute()
    const states = ["待支付","待发货","待收货","待评价","已完成","已取消"]

    let objData = reactive({
      searchText: "",
      user_id: '',
      password: '',
      type: '',
      current_page: 1,
      total_page: 1,
      amount_per_page: 10,

      order_send: {
        dialog: false,
        orderID: '',
        trackingNumber: '',
      },

      comment: {
        dialog: false,
        orderID: '',
        text: '',
        all_comment: {},
      },

      data: [
        {
          orderTime: '2021-12-02',
          orderID: 123456789,
          state: '已完成',
          status: 0,
          paid: 73,
          username: '用户名',
          userphone: 1919810,
          address: '上海市 普陀区 中山北路3663号',
          trackingNumber: 987654321,
          freight: 0,
          current: 1,
          shop: '书店A',

          item: [
            {
              itemID: '1234512345',
              img: 'https://cdn.quasar.dev/img/mountains.jpg',
              itemName: '《他改变了中国》',
              amount: 1,
              price: 48,
              subtotal: 0,
            },
            {
              itemID: '12345123456',
              img: 'https://cdn.quasar.dev/img/mountains.jpg',
              itemName: '《数据科学与工程专业人才培养方案与核心课程体系》',
              amount: 1,
              price: 20,
              subtotal: 0,
            }
          ]
        },
        {
          orderTime: '2021-12-08',
          orderID: 134567890,
          state: '已完成',
          paid: 96,
          username: '用户名',
          userphone: 1919810,
          address: '上海市 普陀区 中山北路3663号',
          trackingNumber: 987654321,
          freight: 5,
          current: 1,
          shop: '书店B',

          item: [
            {
              itemID: '1234512345',
              img: 'https://cdn.quasar.dev/img/mountains.jpg',
              itemName: '《他改变了中国》',
              amount: 2,
              price: 48,
              subtotal: 0,
            },
          ]
        }
      ]

    });

    let onComment = computed(() => {
      return objData.data.find(shop => shop.orderID == objData.comment.orderID)
    })

    function showComment(itemID) {
      return {
        text: objData.comment.all_comment[itemID]['text'] || '',
        score: objData.comment.all_comment[itemID]['score'] || 1,
      }
    }

    function get_order(page) {
      objData.data = []
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + 
        "/" + (objData.type == 'sell' ? 'seller' : 'buyer') + "/order/" + objData.user_id + "/-1/" + page + "/" + objData.amount_per_page
      api.get(url).then((response) => {
        if(response.status == 200){
          response.data.orders.forEach(element => {
            var date=new Date(parseInt(element.time*1000));
            var year=date.getFullYear();
            var mon = date.getMonth()+1;
            var day = date.getDate();
            // alert(objData.type == 'buy' && (element.status == 0))
            objData.data.push({
              
              orderTime: year+'/'+mon+'/'+day,
              //orderTime: element.status,
              orderID: element.uuid,
              state: states[element.status],
              status: element.status,
              paid: element.price / 100,
              username: element.buyer_id,
              userphone: element.userphone || "收货手机号缺失",
              address: element.to_address,
              trackingNumber: element.logistic_id,

              freight: 0,
              shop: element.store_id,
              item: []
            })
            element.items.forEach(ele => {
              objData.data[objData.data.length-1].item.push({
                itemID: ele.book_id,
                img: 'data:image/png;base64,' + ele.book_info.picture,
                itemName: ele.book_info.title,
                amount: ele.count,
                price: ele.single_price / 100,
                subtotal: ele.single_price * ele.count,
              })
            })
          });
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function pay(order_id) {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/payment";
      let body = {
        "user_id": objData.user_id,
        "order_id": order_id,
        "password": objData.password,
      };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("支付成功！")
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function order_cancel(order_id) {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/order_cancel";
      let body = {
        "buyer_id": objData.user_id,
        "uuid": order_id,
      };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("取消成功！")
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function order_send() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/order_send";
      let body = {
        uuid: objData.order_send.orderID, // string 订单号
        seller_id: objData.user_id,  // string 卖家用户名
        logistic_id: objData.order_send.trackingNumber, // string 物流单号
      };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("发货成功！")
          location.reload()
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function order_receive(orderID) {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/order_receive";
      let body = {
        uuid: orderID, // string 订单号
        buyer_id: objData.user_id,  // string 卖家用户名
      };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("收货成功！")
          location.reload()
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function comment() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/comment";
      let body = {
          user_id: objData.user_id, // 用户名
          uuid: objData.comment.orderID, // string 订单号
          books: []
      };
      for (c in objData.comment.all_comment) {
        body.books.push({
          book_id: c,
          comment:{
            star: objData.comment.all_comment[c]['score'],
            text: objData.comment.all_comment[c]['text'],
          }
        })
      }
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("评价成功！")
          location.reload()
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    onMounted(() => {
      objData.user_id = window.localStorage.getItem('user_id');
      objData.password = window.localStorage.getItem('password')
      objData.type = route.query.type
      get_order(1)
    })
  
    return {
      objData,
      onComment,
      showComment,
      get_order,
      pay,
      order_send,
      order_receive,
      comment,
      order_cancel,
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
