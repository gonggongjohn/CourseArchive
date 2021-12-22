<template>
  <q-page class="q-mt-sm">
    <div class="text-grey-9 text-weight-bold">

      <div class="row items-center q-mx-sm">
        <div class="col-12 bg-white q-mt-sm">
          <div class="q-pl-md q-pt-sm ">
            <span class="text-grey-9 text-h6 text-weight-bold">购物车</span>
          </div>

          <div class="row">
            <div class="col-sm-12">
              <div v-for="shop in objData.data" v-bind:key="shop.store_id">
                <q-card class="order center" style="min-height:200px;width:100%">
                  <div class="row">
                    <q-card-section class="col">
                        <div class="row">
                          <div class="col-4 text-subtitle2"> 店铺：{{ shop.store_id }} </div>
                        </div>
                        <q-separator class="q-pt-none"></q-separator>
                        <div class="row q-pt-sm">
                          <div class="col-12">
                            <q-markup-table :separator="separator" flat bordered>
                              <thead>
                                <tr>
                                  <th class="text-left" style="width:5%">选中</th>
                                  <th class="text-left" style="width:55%">商品信息</th>
                                  <th class="text-left" style="width:10%">单价</th>
                                  <th class="text-left" style="width:10%">数量</th>
                                  <th class="text-left" style="width:10%">金额</th>
                                  <th class="text-right" style="width:10%"></th>
                                </tr>
                              </thead>
                              <tbody v-for="item in shop.item" v-bind:key="item.itemID">
                                <tr>
                                  <td class="text-left">
                                    <q-checkbox v-model="item.selected" />
                                  </td>
                                  <td class="text-left">
                                    <q-avatar size="80px" square><img :src="item.img"></q-avatar>
                                    {{ item.itemName }}
                                  </td>
                                  <td class="text-left"> {{ item.price }}</td>
                                  <td class="text-left">
                                    <q-input
                                      v-model.number="item.amount"
                                      mask="####"
                                      type="number"
                                      filled
                                      style="max-width: 100px"
                                      lazy-rules
                                      :rules="[ val => val >= 1 && val % 1 === 0 || '数量必须为正整数']"
                                      @blur="() => {
                                        item.amount = item.amount % 1 === 0 ? item.amount : Math.floor(item.amount);
                                        item.amount = item.amount > 0 ? item.amount : 1;
                                        changeAmount(item.itemID, item.amount);
                                        }"
                                    /> 
                                  </td>
                                  <td class="text-left"> {{ item.price * item.amount }} </td>
                                  <td class="text-left"><q-btn class="q-my-xs" label="删除" color="primary"/><br/></td>
                                </tr>
                              </tbody>
                              </q-markup-table>
                          </div>
                        </div>
                        <q-separator class="q-mt-sm"></q-separator>
                      </q-card-section>
                  </div>
                </q-card>
              </div>
            </div>
          </div>
          
        </div>
      </div>
    </div>

    <q-footer elevated>
      <div class="bg-blue-1 row">
        <div class="col-sm-0 col-md-3"></div>

        <q-toolbar class="col-sm-12 col-md-6 justify-between" height="min-height:200px">
          <div>
            <span class="text-grey-9">已选商品</span> 
            <span class="text-h5 text-red" color="red">&nbsp; {{ count }} &nbsp;</span> 
            <span class="text-grey-9">件</span>
          </div>
          <div>
            <span class="text-grey-9">&nbsp; 合计（不含运费）：</span> 
            <span class="text-red" color="red">&nbsp; ￥ </span>
            <span class="text-h5 text-red" color="red">{{ total }} &nbsp;</span> 
          </div>
          <div>
            <q-btn outline @click="make_order" color="grey-9" label="结算" />
          </div>
        </q-toolbar>

        <div class="col-sm-0 col-md-3"></div>
      </div>
    </q-footer>
  </q-page>
</template>

<script>
import { reactive, computed, onMounted } from 'vue' 
import { api, backend_port, testurl } from "../boot/axios";

export default {
  setup() {
    let objData = reactive({
      user_id: '',
      data: [
        {
          store_id: '111111',
          item: [
            {
              id: '',
              itemID: '12321',
              img: 'https://cdn.quasar.dev/img/mountains.jpg',
              itemName: '《他改变了中国》',
              selected: false,
              amount: 1,
              price: 48,
            },
            {
              id: '',
              itemID: '23456',
              img: 'https://cdn.quasar.dev/img/mountains.jpg',
              itemName: '《数据科学与工程专业人才培养方案与核心课程体系》',
              selected: false,
              amount: 1,
              price: 20,
            }
          ],
        },
        {
          store_id: '111111',
          item: [
            {
              id: '',
              itemID: '188887',
              img: 'https://cdn.quasar.dev/img/mountains.jpg',
              itemName: '《他改变了中国》',
              selected: false,
              amount: 1,
              price: 42,
            },
          ],
        },
      ],
    });

    let count = computed(() => {
      var data = objData.data;
      var total = 0;
      data.forEach(shop => {
        shop.item.forEach(element => {
          total += element.selected == true ? 1 : 0;
        });
      });
      return total;
    })

    let total = computed(() => {
      var data = objData.data;
      var total = 0;
      data.forEach(item => {
        item.item.forEach(element => {
          if (element.selected) {
            total += element.amount * element.price
          }
        });
      });
      return total;
    });

    function changeAmount(itemID, amount) {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/cart";
      let body = { buyer_id: objData.user_id, book_id: itemID, count: amount }
      api.patch(url, body).then((response) => {
        if(response.status == 200) {
          //
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    // function delete_bought() {

    // }

    function cart_order(order_store_id) {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/cart_order";
      let items = objData.data.find(shop => shop.store_id == order_store_id).item;
      let books = []
      items.forEach(element => {
        if (element.selected) {
          books.push({id: element.id, count: element.amount});
        }
      });
      let body = {
        "user_id": objData.user_id,
        "store_id": order_store_id,
        "books": books
      }
      api.post(url, body).then((response) => {
        if(response.status == 200) {
          alert('商店 ' + order_store_id + ' 下单成功')
          //delete_bought(order_store_id);
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function make_order() {
      objData.data.forEach(shop => {
        let sign = false;
        shop.item.forEach(element => {
          sign = sign || element.selected
        })
        if (sign) {
          cart_order(shop.store_id)
        }
      })
    }
    
    onMounted(() => {
      objData.user_id = window.localStorage.getItem("user_id");
      objData.data = []
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/cart/" + objData.user_id;
      api.get(url).then((response) => {
        if(response.status == 200){
          response.data.items.forEach(element => {
            for (let i=0; i < objData.data.length; i++) {
              if (objData.data[i].store_id == element.store_id) {
                objData.data[i].item.push({
                    id: element.book_info.id,
                    img: 'data:image/png;base64,' + element.book_info.picture,
                    itemName: element.book_info.title,
                    selected: false,
                    amount: element.count,
                    price: element.single_price / 100,
                });
                return;
              }
            }
            objData.data.push({
              store_id: element.store_id,
              item: [{
                id: element.info_id,
                itemID: element.book_id,
                img: 'data:image/png;base64,' + element.book_info.picture,
                itemName: element.book_info.title,
                selected: false,
                amount: element.count,
                price: element.single_price / 100,
              }]
            })
          })
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    });

    return {
      objData, 
      count, 
      total,
      changeAmount,
      cart_order,
      make_order,
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
