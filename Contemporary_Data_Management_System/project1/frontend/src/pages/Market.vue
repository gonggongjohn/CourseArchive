<template>
  <div class="row q-pa-md">
    <q-card class="col-10 q-pa-md">
      <q-card-section>
          <h4>市场</h4>
      </q-card-section>

      <q-card-section>
        <div class="row q-gutter-md">
          <template v-for="(item, index) in state.item_list" :key="index">
            <q-card class="q-pa-md" style="width: 200px">
              <p>装备：{{ item.treasure_name }}</p>
              <p>类别：{{ item.treasure_type === 1 ? "工具" : "配饰" }}</p>
              <p>卖家：{{ item.seller }}</p>
              <p>定价：{{ item.price }}</p>

              <q-btn v-if="item.self_belonging === 0" outline class="q-px-lg" color="primary" label="购买" size="lg" @click="onPurchase(index)" />
            </q-card>
          </template>
        </div>
      </q-card-section>
    </q-card>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";

export default defineComponent({
  name: 'PageMarket',
  setup(){
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      coin: 0,
      item_list: [
        /*
        {
          'id': 0,
          'treasure_id': 0,
          'treasure_name': "",
          'treasure_type': 0,
          'seller': "",
          'self_belonging': 0,
          'price': 0
        }
        */
      ]
    });

    onMounted(() => {
      getMarketItem();
    })

    function getHostUrl() {
      let full_path = window.document.location.href;
      let protocol_index = full_path.indexOf("://");
      let protocol_str = full_path.substring(0, protocol_index);
      let full_path_stripped = full_path.substring(protocol_index + 3);
      let router_path = route.path;
      let host_index = full_path_stripped.indexOf(router_path);
      let full_host = full_path_stripped.substring(0, host_index);
      let pred_index = full_host.lastIndexOf(":");
      let pure_host = full_host.substring(0, pred_index);
      return protocol_str + "://" + pure_host;
    }

    function getMarketItem(){
      let url = getHostUrl() + ":5000/market/list"
      api.get(url).then((response) => {
        if(response.data.status == 1){
          let item_list_raw = response.data.items;
          state.item_list = item_list_raw;
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function onPurchase(item_index){
      let url = getHostUrl() + ":5000/market/purchase"; 
      let target_item = state.item_list[item_index];
      let body = { 'id': target_item.id };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          alert("购买成功！");
          router.push('/market');
        }
        else if(response.data.status == 2){
          alert("该物品不在挂牌状态！");
        }
        else{
          alert("购买时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    return {
      state,
      onPurchase
    }
  }
})
</script>
