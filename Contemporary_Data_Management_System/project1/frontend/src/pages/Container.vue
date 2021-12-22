<template>
  <div class="row q-pa-md">
    <q-card class="col-10 q-pa-md">
      <q-card-section>
          <h4>我的储物箱</h4>
      </q-card-section>

      <q-card-section>
        <div class="row q-gutter-md">
          <template v-for="(item, index) in state.item_list" :key="index">
            <q-card class="q-pa-md" style="width: 200px">
              <p>装备：{{ item.treasure_name }}</p>
              <p>类别：{{ item.treasure_type === 1 ? "工具" : "配饰" }}</p>
              <p>加成：{{ item.treasure_gain }}</p>
              <p>状态：{{ item.status === 1 ? "佩戴中" : (item.status === 2 ? "存储中" : "出售中") }}</p>

              <div class="column">
              <q-btn outline v-if="item.status === 2" class="q-px-lg q-mb-md" color="primary" label="佩戴" size="lg" @click="onPutOn(index)" />
              <q-btn outline v-if="item.status === 2" class="p-px-lg" color="primary" label="出售" size="lg" @click="showSellDialog(index)" />
              <q-btn outline v-if="item.status === 1" class="p-px-lg" color="primary" label="取下" size="lg" @click="onTakeOff(index)" />
              <q-btn outline v-if="item.status === 3" class="p-px-lg" color="primary" label="收回" size="lg" @click="onAbortSell(index)" />
              </div>
            </q-card>
          </template>
        </div>
      </q-card-section>
    </q-card>

    <q-dialog v-model="state.sell_dialog_flag">
        <q-card>
          <q-card-section>
            <div class="text-h6">出售物品</div>
          </q-card-section>
          <q-card-section class="q-pt-none">
              <p>宝物名称：{{ state.item_list[state.sell_index].treasure_name }}</p>
              <q-input v-model.number="state.sell_price" type="number" label="定价" />
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="确定" color="primary" @click="onSell" v-close-popup />
          </q-card-actions>
      </q-card>
      </q-dialog>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";

export default defineComponent({
  name: 'PageContainer',
  setup(){
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      item_list: [
        /*
        {
          'id': 0,
          'treasure_id': 0,
          'treasure_name': "",
          'treasure_type': 0,
          'treasure_gain': 0,
          'status': 1
        },
        {
          'id': 0,
          'treasure_id': 0,
          'treasure_name': "",
          'treasure_type': 0,
          'status': 2
        },
        {
          'id': 0,
          'treasure_id': 0,
          'treasure_name': "",
          'treasure_type': 0,
          'status': 3
        }
        */
      ],
      sell_dialog_flag: false,
      sell_index: 0,
      sell_price: 0
    });

    onMounted(() => {
      getContainerItems();
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

    function getContainerItems(){
      let url = getHostUrl() + ":5000/container/list";
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

    function onPutOn(item_index){
      let url = getHostUrl() + ":5000/container/wear"; 
      let target_item = state.item_list[item_index];
      let body = { 'id': target_item.id };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          alert("穿戴成功！")
          state.item_list[item_index].status = 1;
        }
        else if(response.data.status == 2){
          alert("该种类物品穿戴数已达到上限！");
        }
        else{
          alert("穿戴时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function onTakeOff(item_index){
      let url = getHostUrl() + ":5000/container/takeoff"; 
      let target_item = state.item_list[item_index];
      let body = { 'id': target_item.id };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          alert("取下成功！")
          state.item_list[item_index].status = 2;
        }
        else if(response.data.status == 2){
          alert("未装备该物品！");
        }
        else{
          alert("取下物品时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function showSellDialog(item_index){
      state.sell_index = item_index;
      state.sell_dialog_flag = true;
    }

    function onSell(){
      let url = getHostUrl() + ":5000/container/sell"; 
      let target_item = state.item_list[state.sell_index];
      let body = { 'id': target_item.id, 'price': state.sell_price };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          alert("挂牌成功！")
          state.item_list[item_index].status = 3;
        }
        else if(response.data.status == 2){
          alert("该物品无法被挂牌！");
        }
        else{
          alert("挂牌时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function onAbortSell(item_index){
      let url = getHostUrl() + ":5000/container/abortsell"; 
      let target_item = state.item_list[item_index];
      let body = { 'id': target_item.id };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          alert("取消挂牌成功！")
          state.item_list[item_index].status = 2;
        }
        else if(response.data.status == 2){
          alert("该物品不在挂牌状态！");
        }
        else{
          alert("挂牌时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    return {
      state,
      onPutOn,
      onTakeOff,
      onSell,
      onAbortSell,
      showSellDialog
    }
  }
})
</script>
