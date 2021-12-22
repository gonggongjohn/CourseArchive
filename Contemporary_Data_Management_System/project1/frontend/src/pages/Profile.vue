<template>
  <div class="row q-pa-md">
    <q-card class="col-8 q-pa-md">
      <q-card-section horizontal>
        <q-card-section class="col-3">
          <q-img class="q-mb-md" :src="state.avatar_url" style="max-width: 100px" />
          <q-file class="hidden" ref="avatar_selector" v-model="state.avatar_file" @change="onAvatarChosen" />
          <q-btn color="secondary" @click="onChangeAvatar" label="修改头像" />
        </q-card-section>

        <q-card-section>
          <div class="row">
            <p class="q-mr-lg">昵称：</p>
            <div class="cursor-pointer">
              <div v-if="state.nickname != ''">{{ state.nickname }}</div>
              <div v-else>No nickname</div>
              <q-popup-edit ref="nickname_editor" v-model="state.nickname" @before-show="onShowNicknameEditor" >
                <q-input v-model="state.nickname_modify" dense autofocus counter />
                <q-btn color="secondary" @click="onChangeNickname" label="确定" />
              </q-popup-edit>
            </div>
          </div>
          <p>注册时间：{{ state.register_time }}</p>
          <p>用户组：{{ state.user_permission === 1 ? "玩家" : "管理员" }}</p>
          <p>金币：{{ state.coin }}</p>
          <p>能力值：{{ state.power }}</p>
          <p>运气值：{{ state.luck }}</p>
        </q-card-section>
      </q-card-section>

      <q-card-section>
        <p>工具</p>

        <div class="row">
          <template v-for="(equipment_item, equipment_index) in state.equipment_list" :key="equipment_index">
            <q-card class="col q-ma-md q-pa-sm" v-if="equipment_item.type === 1">
              <p>装备：{{ equipment_item.name }}</p>
              <p>加成：{{ equipment_item.gain }}</p>
            </q-card>
          </template>
        </div>
      </q-card-section>

      <q-card-section>
        <p>配饰</p>

        <div class="row">
          <template v-for="(equipment_item, equipment_index) in state.equipment_list" :key="equipment_index">
            <q-card class="col q-ma-md q-pa-sm" v-if="equipment_item.type === 2">
              <p>装备：{{ equipment_item.name }}</p>
              <p>加成：{{ equipment_item.gain }}</p>
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
  name: 'PageProfile',
  setup(){
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      nickname: "",
      nickname_modify: "",
      avatar_url: "../assets/avatar_default.png",
      avatar_file: "",
      register_time: "",
      user_permission: 1,
      coin: 0,
      power: 0,
      luck: 0,
      equipment_list: [
        /*
        // Tools
        { id: 0, name: "", type: 1, gain: 0 },
        { id: 0, name: "", type: 1, gain: 0 },
        { id: 0, name: "", type: 1, gain: 0 },
        { id: 0, name: "", type: 1, gain: 0 },
        // Accessory
        { id: 0, name: "", type: 2, gain: 0 },
        { id: 0, name: "", type: 2, gain: 0 },
        { id: 0, name: "", type: 2, gain: 0 },
        { id: 0, name: "", type: 2, gain: 0 }
        */
      ]
    });
    const avatar_selector = ref(null);
    const nickname_editor = ref(null);

    onMounted(() => {
      getUserInfo();
      state.avatar_url = getHostUrl() + ":5000/user/get_avatar";
      getEquipment();
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

    function formatTimestamp(timestamp){
      var date = new Date(timestamp);
      var y = date.getFullYear();
      var m = date.getMonth() + 1;
      var d = date.getDate();
      var h = date.getHours();
      var min = date.getMinutes();
      var s = date.getSeconds();
      if(m < 10) { m = '0' + m; }
      if(d < 10) { d = '0' + d; }
      if(h < 10) { h = '0' + h; }
      if(min < 10) { min = '0' + min; }
      if(s < 10) { s = '0' + s; }
      var t = y + '-' + m + '-' + d + ' ' + h + ':' + min + ':' + s;
      return t;
    }

    function getUserInfo(){
      let url = getHostUrl() + ":5000/user/info"
      api.get(url).then((response) => {
        if(response.data.status == 1){
          console.log(response.data);
          let info_dict = response.data.info;
          state.nickname = info_dict.nickname;
          console.log(info_dict.regtime);
          state.register_time = formatTimestamp(parseInt(info_dict.regtime * 1000));
          state.user_permission = info_dict.permission;
          state.coin = info_dict.coin;
          state.power = info_dict.power;
          state.luck = info_dict.luck;
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function getEquipment(){
      let url = getHostUrl() + ":5000/user/equipment"
      api.get(url).then((response) => {
        if(response.data.status == 1){
          let equipment_list_raw = response.data.equipment;
          state.equipment_list = equipment_list_raw;
          var tool_cnt = 0;
          var accessory_cnt = 0;
          state.equipment_list.forEach(equipment => {
            if(equipment.type == 1) tool_cnt += 1;
            else if(equipment.type == 2) accessory_cnt += 1;
          });
          for(var i = tool_cnt; i < 4; i++){
            state.equipment_list.push({ id: 0, name: "", type: 1, gain: 0 })
          }
          for(var i = accessory_cnt; i < 4; i++){
            state.equipment_list.push({ id: 0, name: "", type: 2, gain: 0 })
          }
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function onChangeAvatar(){
      avatar_selector.value.pickFiles();
    }

    function onAvatarChosen(){
      let url = getHostUrl() + ":5000/user/set_avatar";
      let file_form = new FormData();
      file_form.append('avatar', state.avatar_file);
      api.post(url, file_form, {
        headers: {
          'Content-Type': 'multipart/form-data'
        }
      }).then((response) => {
        console.log(response.data);
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function onShowNicknameEditor(){
      state.nickname_modify = state.nickname
    }

    function onChangeNickname(){
      let url = getHostUrl() + ":5000/user/set_nickname";
      let body = {nickname: state.nickname_modify};
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          nickname_editor.value.set();
        }
        else{
          alert("修改用户名时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    return {
      state,
      avatar_selector,
      nickname_editor,
      onChangeAvatar,
      onAvatarChosen,
      onShowNicknameEditor,
      onChangeNickname
    }
  }
})
</script>
