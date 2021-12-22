<template>
  <q-page>
    <div class="row q-col-gutter-sm q-ma-xs">
      <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
        <q-card class="my-card"  style="height:100%" flat bordered>
          <q-card-section horizontal>
            <q-card-section class="col-5 flex flex-center">
              <q-img
                class="rounded-borders"
                src="https://cdn.quasar.dev/img/boy-avatar.png"
              />
            </q-card-section>

            <q-card-section class="q-pt-xs">
              <div class="text-overline">超级会员</div>
              <div class="text-h5 q-mt-sm q-mb-xs"> {{ user.name }} </div>
              <div class="text-caption text-grey">
                用户ID：{{ user.user_id }}
              </div>
            </q-card-section>
          </q-card-section>
        </q-card>
      </div>
      <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
        <q-card  style="height:100%" flat bordered>
          <q-card-section>
            <q-form
              class="q-gutter-md"
            >
              <q-input
                filled
                v-model="user.name"
                label="收货人姓名"
                lazy-rules
              />

              <q-input
                filled
                v-model="user.phone"
                label="手机号"
                lazy-rules

              />

              <q-input
                filled
                v-model="user.address"
                label="收货地址"
                lazy-rules

              />

              <div>
                <q-btn label="更新" @click="auth" color="primary"/>
              </div>
            </q-form>
          </q-card-section>
        </q-card>
      </div>
    </div>
    
    <div class="row q-col-gutter-sm q-ma-xs">
      <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">

        <div class="q-pa-md row items-start q-gutter-md">
          <q-card flat bordered>
            <q-card-section class="bg-primary text-white">
              <div class="text-h6">订单管理</div>
            </q-card-section>
            <q-separator />
            <q-card-actions align="center">
              <q-btn flat @click="$router.push({ path: '/order', query: { type: 'buy' } })">购买订单</q-btn>
              <q-btn flat @click="$router.push({ path: '/order', query: { type: 'sell' } })">销售订单</q-btn>
            </q-card-actions>
          </q-card>

          <q-card flat bordered>
            <q-card-section class="bg-primary text-white">
              <div class="text-h6">账号管理</div>
            </q-card-section>
            <q-separator />
            <q-card-actions align="center">
              <q-btn @click="user.dialog.change = true" flat>修改密码</q-btn>
              <q-btn @click="user.dialog.unregister = true" flat>注销账号</q-btn>
            </q-card-actions>
          </q-card>
        </div>

      </div>
      <div class="col-lg-6 col-md-6 col-sm-12 col-xs-12">
        <q-card flat bordered>
          <q-card-section>
            <q-form
              class="q-gutter-md"
            >
              <q-input
                filled
                v-model="user.balance"
                label="账户余额 ￥"
                lazy-rules
                readonly
              />

              <q-input
                filled
                v-model="user.recharge"
                label="充值/提现金额 ￥"
                lazy-rules
              />

              <q-btn-group>
                <q-btn label="充值" @click="add_funds" color="primary"/>
                <q-btn label="提现" @click="cash" color="primary"/>
              </q-btn-group>
            </q-form>
          </q-card-section>
        </q-card>
      </div>

    </div>

    <!-- 修改密码 -->
    <q-dialog v-model="user.dialog.change" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">修改密码</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input dense v-model="user.change.username" filled 
            :type="text" label="用户名">
          </q-input>

          <q-input dense v-model="user.dialog.oldpassword" filled 
          :type="user.change.vopw ? 'password' : 'text'" label="旧密码">
            <template v-slot:append>
              <q-icon
                :name="user.change.vopw ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="user.change.vopw = !user.change.vopw"
              />
            </template>
          </q-input>

          <q-input class="q-py-sm" dense v-model="user.dialog.newpassword" filled 
          :type="user.change.vnpw ? 'password' : 'text'" label="新密码">
            <template v-slot:append>
              <q-icon
                :name="user.change.vnpw ? 'visibility_off' : 'visibility'"
                class="cursor-pointer"
                @click="user.change.vnpw = !user.change.vnpw"
              />
            </template>
          </q-input>

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消修改" v-close-popup />
          <q-btn flat label="确认修改" @click="changePW" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

    <!-- 确认注销 -->
    <q-dialog v-model="user.dialog.unregister" persistent>
      <q-card>
        <q-card-section class="row items-center">
          <q-avatar icon="location_disabled" color="primary" text-color="white" />
          <span class="q-ml-sm">您确认注销账号吗？该过程不可逆，您的余额将会丢失。<br/>为了保证安全，请输入您的用户名和密码</span>

            <q-input dense v-model="user.unregister.username" filled 
            :type="text" label="用户名">
            </q-input>

            <br/>

            <q-input class="q-py-sm" dense v-model="user.unregister.password" filled 
            :type="user.unregister.vpw ? 'password' : 'text'" label="新密码">
              <template v-slot:append>
                <q-icon
                  :name="user.unregister.vpw ? 'visibility_off' : 'visibility'"
                  class="cursor-pointer"
                  @click="user.unregister.vpw = !user.unregister.vpw"
                />
              </template>
            </q-input>

        </q-card-section>

        <q-card-actions align="right">
          <q-btn flat label="取消" color="primary" v-close-popup />
          <q-btn flat label="确认注销" color="primary" @click="unregister" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>

  </q-page>
</template>

<script>
import { reactive, onMounted } from 'vue'
import { useRouter } from "vue-router";
import { api, backend_port, testurl } from "../boot/axios";


export default {
  setup () {
    const router = useRouter();

    let user = reactive({

      dialog: {
        change: false,
        unregister: false,
      },
      unregister: {
        username: "",
        password: "",
        vpw: false,
      },
      change: {
        username: "",
        oldpassword: '',
        vopw: true,
        newpassword: '',
        vnpw: true,
      },
      user_id: 'id',
      password: '',
      name: '小李',
      phone: 1234567890,
      address: '上海市 普陀区 中山北路3663号',
      balance: 999.90,
      recharge: 0.00
    });

    function unregister() {
      // let url = location.protocol + "//" + location.hostname + ":" + backend_port + "/auth/unregister";
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth/unregister";
      let body = { user_id: user.unregister.username, password: user.unregister.password };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("注销成功！")
          router.push("/login");
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    };

    function changePW() {
      // let url = location.protocol + "//" + location.hostname + ":" + backend_port + "/auth/unregister";
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth/password";
      let body = { user_id: user.change.username, oldPassword: user.change.oldpassword, newPassword: user.change.newpassword };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("修改成功！请重新登陆")
          router.push("/login");
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    };

    function add_funds() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/add_funds";
      let recharge = Math.floor(user.recharge * 100)
      let body = { user_id: user.user_id, password: user.password, add_value: recharge };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("充值成功！")
          location.reload()
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function cash() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/buyer/cash";
      let out = Math.floor(user.recharge * 100)
      let body = {
        user_id: user.user_id, // string 用户名
        cash: out
      }

      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("提现成功！")
          location.reload()
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    function auth() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth";
      let body = { user_id: user.user_id, nickname: user.name, phone: user.phone, address: user.address };
      api.patch(url, body).then((response) => {
        if(response.status == 200){
          alert("更新成功！")
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    onMounted(() => {
      user.user_id = window.localStorage.getItem("user_id");
      user.password = window.localStorage.getItem("password");
      
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth/" + user.user_id;
      api.get(url).then((response) => {
        if(response.status == 200){
          user.name = response.data.user.nickname;
          user.phone = response.data.user.phone;
          user.balance = response.data.user.money / 100;
          user.address = response.data.user.address;
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    });
    
    return {
      user,
      unregister,
      changePW,
      add_funds,
      cash,
      auth,
    }
  }
}
</script>

<style scoped>

</style>
