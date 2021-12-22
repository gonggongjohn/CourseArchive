<template>
  <div class="row justify-center q-pa-md">
    <q-card class="col-8">
      <q-card-section class="flex flex-center">
        <h3>登录游戏</h3>
      </q-card-section>

      <q-card-section>
        <q-input class="q-mb-lg" v-model="state.username" label="用户名" />
        <q-input v-model="state.password" type="password" label="密码" />
      </q-card-section>

      <q-card-section class="row justify-center">
        <q-btn
          outline
          class="q-px-lg q-mr-lg"
          color="primary"
          label="登录"
          size="lg"
          @click="onLogin"
        />
        <q-btn
          outline
          class="q-px-lg q-ml-lg"
          color="secondary"
          label="注册"
          size="lg"
          @click="toRegister"
        />
      </q-card-section>
    </q-card>
  </div>
</template>

<script>
import { defineComponent, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";

export default defineComponent({
  name: "PageLogin",

  setup() {
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      username: "",
      password: "",
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

    function onLogin() {
      let url = getHostUrl() + ":5000/user/login";
      let body = { username: state.username, password: state.password };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          router.push("/board");
        }
        else if(response.data.status == 2){
          alert("密码错误！");
        }
        else if(response.data.status == 3){
          alert("用户名不存在！");
        }
        else{
          alert("登录时发生错！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function toRegister() {
      router.push("/register");
    }

    return {
      state,
      onLogin,
      toRegister,
    };
  },
});
</script>
