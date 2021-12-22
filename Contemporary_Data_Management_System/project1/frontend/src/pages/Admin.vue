<template>
  <div class="row q-pa-md">
    <q-card class="col-8 q-pa-md">
      <q-card-section>
          <h4>添加宝物</h4>
      </q-card-section>

      <q-card-section>
          <q-input v-model="state.treasure_name" label="宝物名称" />
          <q-select v-model="state.treasure_type" :options="state.type_options" label="宝物类型" />
          <q-input v-model.number="state.treasure_gain" type="number" label="宝物加成" />
      </q-card-section>

      <q-btn outline class="q-px-lg" color="primary" label="添加" size="lg" @click="onAddTreasure" />
    </q-card>
  </div>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";

export default defineComponent({
  name: 'PageAdmin',
  setup(){
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      treasure_name: "",
      treasure_type: null,
      treasure_gain: 0,
      type_options: [
        {
            label: "工具",
            value: 1
        },
        {
            label: "配饰",
            value: 2
        }
      ]
    });

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

    function onAddTreasure(){
      if(state.treasure_name != "" && state.treasure_type != null && state.treasure_gain > 0){
        let url = getHostUrl() + ":5000/admin/add_treasure";
        console.log(url);
        let body = {'name': state.treasure_name, 'type': state.treasure_type.value, 'gain': state.treasure_gain};
        api.post(url, body).then((response) => {
          if(response.data.status == 1){
            alert("添加物品成功！");
          }
          else if(response.data.status == 2){
            alert("无权添加物品！");
          }
          else{
            alert("添加物品时发生错误！");
          }
        })
        .catch((error) => {
          console.log(error);
        });
      }
    }

    return {
      state,
      onAddTreasure
    }
  }
})
</script>
