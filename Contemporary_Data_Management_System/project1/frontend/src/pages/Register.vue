<template>
  <div class="row justify-center q-pa-md">
    <q-card class="col-8">
      <q-card-section class="flex flex-center">
        <h3>注册账户</h3>
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
          label="注册"
          size="lg"
          @click="onRegister"
        />
        <q-btn
          outline
          class="q-px-lg q-ml-lg"
          color="secondary"
          label="返回登录"
          size="lg"
          @click="toLogin"
        />
      </q-card-section>
    </q-card>
  </div>
</template>

<script>
import { defineComponent, reactive } from "vue";
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";
//api.defaults.withCredentials = true;

export default defineComponent({
  name: "PageRegister",

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

    function onRegister() {
      let url = getHostUrl() + ":5000/user/register";
      let body = { username: state.username, password: state.password };
      api.post(url, body).then((response) => {
        if(response.data.status == 1){
          alert("注册成功！");
        }
        else if(response.data.status == 2){
          alert("用户名已存在！");
        }
        else if(response.data.status == 3){
          alert("用户信息不允许为空");
        }
        else{
          alert("注册时发生错误！");
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function toLogin() {
      router.push("/");
    }

    return {
      state,
      onRegister,
      toLogin,
    };
  },
});
</script>
