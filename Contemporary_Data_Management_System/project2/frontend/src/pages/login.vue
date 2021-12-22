<template>
  <q-layout>
    <q-page-container>
      <q-page class="flex flex-center" style="font-family: Lato;">
<!--        <div id="particles-js"></div>-->
        <q-card :style="$q.platform.is.desktop ? 'width:55%;' : ''" class="row my-card items-center q-pa-none q-ma-none shadow-24">
          <q-card-section v-if="$q.platform.is.desktop" class="col-md-4 col-lg-4 col-sm-12 sol-xs-12 items-center float-left" style="background-color: #1f509e"
                          :style="{'height':(win_height-270)+'px'}" horizontal>
            <div class="text-center full-width">
              <div><img src="images/logo.png" style="width: 33%"/></div>
              <div class="text-weight-bolder text-white text-h6">网上书店</div>
              <div class="text-caption text-white">一站式在线图书交易方案</div>
            </div>
          </q-card-section>
          <q-card-section class="col-md-8 col-lg-8 col-sm-12 sol-xs-12 float-left">
            <q-card-section class="items-center">
              <div>
                <div v-if="!$q.platform.is.desktop" class="text-weight-bolder text-center q-mb-md text-primary text-h6">网上书店</div>
                <q-form :style="$q.platform.is.desktop ? 'width:55%;margin: auto;' : 'margin: auto;'" class="q-gutter-md">
                  <span class="text-subtitle1 text-weight-bold text-grey-7">欢迎登录</span>
                  <q-input
                    dense
                    outlined
                    v-model="objData.username"
                    label="用户名"
                    lazy-rules
                  />

                  <q-input
                    dense
                    type="密码"
                    outlined
                    v-model="objData.password"
                    label="Password"
                    lazy-rules
                  />
                  <q-checkbox class="text-grey-8" dense v-model="objData.remember_me" label="记住我"/>
                  <div>
                    <q-btn class="text-capitalize" size="sm" style="width:75px" dense label="登录" @click="login" type="button"
                           color="primary"/>
                    <q-btn class="float-right text-blue-9 text-capitalize" @click="register" size="sm" style="width:75px;border: 1px solid #36669e;" dense label="注册"
                           type="button"/>
                  </div>
                </q-form>

              </div>
            </q-card-section>
          </q-card-section>

        </q-card>
      </q-page>
    </q-page-container>
  </q-layout>
</template>

<script>
  import { reactive, computed, onMounted } from 'vue' 
  import { useRouter } from "vue-router";
  import { api, backend_port, testurl } from "../boot/axios";

  export default {
    setup () {
      const router = useRouter()

      let objData = reactive({
        username: 'mayur',
        password: 'mayur@qu',
        remember_me: false
      });

      function register() {
        // let url = location.protocol + "//" + location.hostname + ":" + backend_port + "/auth/register";
        let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth/register";
        let body = { user_id: objData.username, password: objData.password };
        api.post(url, body).then((response) => {
          if(response.status == 200){
            alert("注册成功，请登录！")
          }
        })
        .catch((error) => {
          alert(error.response.data.message)
        });
      }

      function login() {
        let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth/login";
        let body = { user_id: objData.username, password: objData.password, terminal: Math.floor(Math.random()*(99999-10000+1)+10000) };
        api.post(url, body).then((response) => {
          if(response.status == 200){
            window.localStorage.setItem("token", response.data.token)
            window.localStorage.setItem("user_id", body.user_id)
            window.localStorage.setItem("password", body.password)
            alert("登陆成功！欢迎：" + body.user_id )
            router.push({ path: '/home'});
          }
        })
        .catch((error) => {
          alert(error.response.data.message)
        });
      }

      return {
        objData,
        register,
        login,
      }
    }
  }
</script>
<style>
  .my-card {
    /*width: 55%;*/
    height: 30%;
  }

  #particles-js {
    position: absolute;
    width: 100%;
    height: 100%;
    /*background: linear-gradient(145deg, #abbaab 15%, #ffffff 70%);*/
    /*background: linear-gradient(145deg,#f7f8f8 11%, #627e79 75%);*/
    background-repeat: no-repeat;
    background-size: cover;
    background-position: 50% 50%;
  }

  .login-form {
    position: absolute;
  }
</style>
