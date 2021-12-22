<template>
  <q-layout view="lHh LpR lFf" style="font-family: Lato;"><!-- 
    <q-header reveal elevated style="background-color: #1f509e;"> -->

    <q-header reveal elevated style="background-color: #1f509e;">
      <q-toolbar v-if="!$q.platform.is.desktop" class="q-py-sm">
        <div class="row">
          <div class="col-sm-12 col-xs-12">
            <q-toolbar-title>
              <img @click="$router.push('/home')" class="cursor-pointer float-left" src="images/logo.png" style="width: 12%"/>
              <span class="float-left q-mt-xs q-ml-md text-h6 text-weight-bold" style="font-size: 17px;">网上书店</span>
            </q-toolbar-title>
          </div>
          <div class="col-sm-12 col-xs-12 q-mt-md">
            <q-input class="float-left q-mx-md full-width" square bg-color="white" dense outlined
                     v-model="menu.text"
                     label="商品搜索"/>
          </div>
          <div class="col-sm-12 col-xs-12 q-mt-md">
            <div>
              <q-btn class="q-mr-md" dense round flat icon="shopping_cart">
                <q-badge color="red" class="text-bold" floating transparent>
                  4
                </q-badge>
              </q-btn>
              <q-btn flat round dense icon="settings" class="q-mr-md"/>
              <q-btn flat round dense icon="fas fa-sign-out-alt" to="/"/>
            </div>
          </div>
        </div>
        <!--          <q-btn @click="left = !left" flat round dense icon="menu" class="q-mr-sm" />-->


        <!--<q-btn flat dense icon="shopping_cart" class="text-capitalize q-mr-md text-bold" label="Cart"/>-->

      </q-toolbar>
      <q-toolbar v-if="$q.platform.is.desktop" class="q-py-sm">
        <!--          <q-btn @click="left = !left" flat round dense icon="menu" class="q-mr-sm" />-->
        <img @click="$router.push('/home')" class="cursor-pointer" src="images/logo.png" style="width: 3%"/>
        <q-toolbar-title>
          <span class="float-left q-mt-xs text-h6 text-weight-bold" style="font-size: 17px;">网上书店</span>
          <q-input class="float-left q-ml-xl" style="max-width: 650px;" square bg-color="white" dense outlined
                   v-model="menu.text"
                   label="商品搜索（标签请用空格隔开）">
            <template v-slot:after>
              <q-btn class="bg-white" round dense flat icon="search" @click="$router.push({path:'/newsearch', query:{searchText: menu.text}});"/>
            </template>
          </q-input>
        </q-toolbar-title>

        <!--<q-btn flat dense icon="shopping_cart" class="text-capitalize q-mr-md text-bold" label="Cart"/>-->
        <q-btn flat round dense icon="assignment" class="q-mr-md" @click="$router.push({ path: '/order', query: { type: 'buy' } })"/>
        <q-btn class="q-mr-md" dense round flat icon="shopping_cart" @click="$router.push('/cart')">
          <q-badge color="red" class="text-bold" floating transparent>
            4
          </q-badge>
        </q-btn>
        <q-btn flat round dense icon="store" class="q-mr-md" @click="$router.push('/my_shop')"/>
        <q-btn flat round dense icon="settings" class="q-mr-md" @click="$router.push('/my_profile')"/>
        <q-btn flat round dense icon="fas fa-sign-out-alt" @click="logout" />
      </q-toolbar>
      <div class="bg-white text-grey-9 text-weight-bold shadow-transition">
        <div class="row text-center items-center" :style="$q.platform.is.desktop ? 'height: 38px' : ''">
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 cursor-pointer hover-blue" @mouseover="menu.menu_cat_life=true">
            生活
            <q-menu
              fit
              @mouseleave="menu.menu_cat_life=false"
              v-model="menu.menu_cat_life"
              transition-show="flip-right"
              transition-hide="flip-left"
            >
              <q-list dense class="text-grey-9 text-caption">
                <q-item @click="$router.push({path:'/search', query:{searchText: '旅游'}})" clickable>
                  <q-item-section>旅游</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '美食'}})" clickable>
                  <q-item-section>美食</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '养生'}})" clickable>
                  <q-item-section>养生</q-item-section>
                </q-item>
                <!--<q-separator/>-->
                <q-item @click="$router.push({path:'/search', query:{searchText: '时尚'}})" clickable>
                  <q-item-section>时尚</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
            <q-icon size="sm" class="q-ml-xs text-grey-5" name="keyboard_arrow_down"></q-icon>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 cursor-pointer hover-blue" @mouseover="menu.menu_cat_tech=true">
            科技
            <q-menu
              fit
              @mouseleave="menu.menu_cat_tech=false"
              v-model="menu.menu_cat_tech"
              transition-show="flip-right"
              transition-hide="flip-left"
            >
              <q-list dense class="text-grey-9 text-caption">
                <q-item @click="$router.push({path:'/search', query:{searchText: '科普'}})" clickable>
                  <q-item-section>科普</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '计算机'}})" clickable>
                  <q-item-section>计算机</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '医学'}})" clickable>
                  <q-item-section>医学</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
            <q-icon size="sm" class="q-ml-xs text-grey-5" name="keyboard_arrow_down"></q-icon>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 cursor-pointer hover-blue" @mouseover="menu.menu_cat_edu=true">
            教育考试
            <q-menu
              fit
              @mouseleave="menu.menu_cat_edu=false"
              v-model="menu.menu_cat_edu"
              transition-show="flip-right"
              transition-hide="flip-left"
            >
              <q-list dense class="text-grey-9 text-caption">
                <q-item @click="$router.push({path:'/search', query:{searchText: '教材'}})" clickable>
                  <q-item-section>教材</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '教辅'}})" clickable>
                  <q-item-section>教辅</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '考研'}})" clickable>
                  <q-item-section>考研</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
            <q-icon size="sm" class="q-ml-xs text-grey-5" name="keyboard_arrow_down"></q-icon>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 cursor-pointer hover-blue" @mouseover="menu.menu_cat_ss=true">
            人文社科
            <q-menu
              fit
              @mouseleave="menu.menu_cat_ss=false"
              v-model="menu.menu_cat_ss"
              transition-show="flip-right"
              transition-hide="flip-left"
            >
              <q-list dense class="text-grey-9 text-caption">
                <q-item @click="$router.push({path:'/search', query:{searchText: '历史'}})" clickable>
                  <q-item-section>历史</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '哲学'}})" clickable>
                  <q-item-section>哲学</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '传记'}})" clickable>
                  <q-item-section>传记</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
            <q-icon size="sm" class="q-ml-xs text-grey-5" name="keyboard_arrow_down"></q-icon>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 cursor-pointer hover-blue" @mouseover="menu.menu_cat_man=true">
            经管
            <q-menu
              fit
              @mouseleave="menu.menu_cat_man=false"
              v-model="menu.menu_cat_man"
              transition-show="flip-right"
              transition-hide="flip-left"
            >
              <q-list dense class="text-grey-9 text-caption">
                <q-item @click="$router.push({path:'/search', query:{searchText: '管理'}})" clickable>
                  <q-item-section>管理</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '经济'}})" clickable>
                  <q-item-section>经济</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '投资'}})" clickable>
                  <q-item-section>投资</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
            <q-icon size="sm" class="q-ml-xs text-grey-5" name="keyboard_arrow_down"></q-icon>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 cursor-pointer hover-blue" @mouseover="menu.menu_cat_lit=true">
            文艺
            <q-menu
              fit
              @mouseleave="menu.menu_cat_lit=false"
              v-model="menu.menu_cat_lit"
              transition-show="flip-right"
              transition-hide="flip-left"
            >
              <q-list dense class="text-grey-9 text-caption">
                <q-item @click="$router.push({path:'/search', query:{searchText: '文学'}})" clickable>
                  <q-item-section>文学</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '艺术'}})" clickable>
                  <q-item-section>艺术</q-item-section>
                </q-item>
                <q-item @click="$router.push({path:'/search', query:{searchText: '漫画'}})" clickable>
                  <q-item-section>漫画</q-item-section>
                </q-item>
              </q-list>
            </q-menu>
          </div>
        </div>
      </div>
    </q-header>


    <q-page-container style="background-color:#f1f2f6">
      <router-view/>
      <div class="q-mt-sm">
        <!--
        <div class="row q-pa-md bg-primary">
          <div class="col-lg-1 col-md-1 col-sm-12 col-xs-12"></div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 text-white">
            <div class="text-subtitle1 text-weight-bold">About</div>
            <div class="text-caption hover_underline_white q-mt-sm">Contact us</div>
            <div class="text-caption hover_underline_white">About Us</div>
            <div class="text-caption hover_underline_white">Careers</div>
            <div class="text-caption hover_underline_white">Our Stories</div>
            <div class="text-caption hover_underline_white">Press</div>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 text-white">
            <div class="text-subtitle1 text-weight-bold">Connect with Us</div>
            <div class="text-caption hover_underline_white q-mt-sm">Facebook</div>
            <div class="text-caption hover_underline_white">Instagram</div>
            <div class="text-caption hover_underline_white">Twitter</div>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 text-white">
            <div class="text-subtitle1 text-weight-bold">Policy</div>
            <div class="text-caption hover_underline_white q-mt-sm">Return Policy</div>
            <div class="text-caption hover_underline_white">Terms Of Use</div>
            <div class="text-caption hover_underline_white">Security</div>
            <div class="text-caption hover_underline_white">Privacy</div>
            <div class="text-caption hover_underline_white">Sitemap</div>
          </div>
          <div class="col-lg-2 col-md-2 col-sm-12 col-xs-12 text-white">
            <div class="text-subtitle1 text-weight-bold">Help</div>
            <div class="text-caption hover_underline_white q-mt-sm">Payments</div>
            <div class="text-caption hover_underline_white">Shipping</div>
            <div class="text-caption hover_underline_white">Cancellation & Returns</div>
            <div class="text-caption hover_underline_white">FAQ</div>
          </div>
          <div class="col-lg-3 col-md-3 col-sm-12 col-xs-12  text-white"
               :class="$q.platform.is.desktop ? 'q-pl-xl' : ''"
               :style="$q.platform.is.desktop ? 'border-left: 1px solid grey;' : ''">
            <div class="text-subtitle1 text-weight-bold">Registered Office Address:</div>
            <div class="text-caption q-mt-sm">335, Gokhale Wadi, Grant Road</div>
            <div class="text-caption">Mumbai, Maharashtra</div>
            <div class="text-caption">412207</div>
            <div class="text-caption">India</div>
          </div>
        </div>-->
        <div style="background-color: #163758;">
          <div class="q-mr-md text-right q-py-xs text-weight-bold text-grey-6" style="">
            Made with
            <span style="color: #e25555;font-size: 16px">&#9829;</span> using
            <a target="_blank" class="text-blue-1 hover_underline_white" style="text-decoration: none"
               href="https://quasar-framework.org">&nbsp;Quasar&nbsp;</a>
          </div>
        </div>
      </div>
    </q-page-container>


  </q-layout>
</template>

<script>
import { reactive, onMounted } from 'vue'
import { useRouter } from "vue-router";
import { api, backend_port, testurl } from "../boot/axios";

  export default {
    setup() {
      const router = useRouter();

      let menu = reactive({
        user_id: "",

        menu_cat_life: false,
        menu_cat_tech: false,
        menu_cat_edu: false,
        menu_cat_ss: false,
        menu_cat_man: false,
        menu_cat_lit: false,
        text: ''
      });

      function logout() {
        // let url = location.protocol + "//" + location.hostname + ":" + backend_port + "/auth/unregister";
        let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/auth/logout";
        let body = { user_id: menu.user_id };
        api.post(url, body).then((response) => {
          if(response.status == 200){
            window.localStorage.removeItem("token")
            router.push("/");
          }
        })
        .catch((error) => {
          alert(error.response.data.message);
        });
      };

      onMounted(() => {
        menu.user_id = window.localStorage.getItem("user_id");
      });

      return {
        menu,
        logout,
      }
    }
  }
</script>

<style>
  .q-drawer {
    /*background-image: url(https://demos.creative-tim.com/vue-material-dashboard/img/sidebar-2.32103624.jpg) !important;*/
    background-image: url('/images/lake.jpg') !important;
    background-size: cover !important;
  }

  .q-drawer__content {
    background-color: rgba(1, 1, 1, 0.75);
  }

  .navigation-item {
    border-radius: 5px;
  }

  .tab-active {
    background-color: green;
  }

  .hover-blue:hover {
    color: #1f509e;
  }

  .hover_underline_white:hover {
    text-decoration: underline !important;
    cursor: pointer;
  }

  .hover_border_grey:hover {
    border: 1px solid lightgrey;
    cursor: pointer;
    border-radius: 3px;
  }
</style>
