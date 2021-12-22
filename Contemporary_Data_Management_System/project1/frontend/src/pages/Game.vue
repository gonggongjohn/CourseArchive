<template>
  <div class="row q-pa-md">
    <q-card class="col-8 q-pa-md">
      <q-card-section>
          <h4>探索世界</h4>
      </q-card-section>

      <q-card-section>
          <p>世界时间：</p>
      </q-card-section>

      <q-btn outline class="q-px-lg q-mr-md" color="primary" label="寻宝" size="lg" @click="onSeekTreasure" />
      <q-btn outline class="q-px-lg q-ml-md" color="primary" label="劳动" size="lg" @click="onWork" />

      <q-dialog v-model="state.treasure_dialog_flag">
        <q-card>
          <q-card-section>
            <div class="text-h6">获得宝物！</div>
          </q-card-section>
          <q-card-section class="q-pt-none">
              <p>宝物名称：{{ state.treasure_name }}</p>
              <p>宝物类别：{{ state.treasure_type === 1 ? "工具" : "配饰" }}</p>
              <p>宝物加成：{{ state.treasure_gain }}</p>
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="确定" color="primary" v-close-popup />
          </q-card-actions>
      </q-card>
      </q-dialog>

      <q-dialog v-model="state.coin_dialog_flag">
        <q-card>
          <q-card-section>
            <div class="text-h6">工作收获！</div>
          </q-card-section>
          <q-card-section class="q-pt-none">
              <p>收益：{{ state.coin_gained }}</p>
          </q-card-section>
          <q-card-actions align="right">
            <q-btn flat label="确定" color="primary" v-close-popup />
          </q-card-actions>
      </q-card>
      </q-dialog>
    </q-card>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted, onUnmounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";
//import { socket } from "boot/socket";

export default defineComponent({
  name: 'PageGame',
  setup(){
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      web_socket: null,
      coin_gained: 0,
      treasure_dialog_flag: false,
      coin_dialog_flag: false,
      treasure_name: "",
      treasure_type: 1,
      treasure_gain: 0
    });

    onMounted(() => {
      var full_path = window.document.location.href;
      var route_path = route.path;
      var base_path = full_path.substring(7, full_path.indexOf(route_path));
      var base_path_stripped = base_path.substring(0, base_path.indexOf(':'));
      /*
      socket.subscribe('response', (data) => {
        console.log(data);
      })
      */
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

    function onSeekTreasure(){
      let url = getHostUrl() + ":5000/game/seek";
      api.get(url).then((response) => {
        if(response.data.status == 1){
          let treasure_info = response.data.treasure;
          state.treasure_name = treasure_info.name;
          state.treasure_type = treasure_info.type;
          state.treasure_gain = treasure_info.gain;
          state.treasure_dialog_flag = true;
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function onWork(){
      let url = getHostUrl() + ":5000/game/work";
      api.get(url).then((response) => {
        if(response.data.status == 1){
          let coin = response.data.coin;
          state.coin_gained = coin;
          state.coin_dialog_flag = true;
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    return {
      state,
      onSeekTreasure,
      onWork
    }
  }
})
</script>
