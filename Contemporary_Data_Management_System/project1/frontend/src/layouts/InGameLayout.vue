<template>
  <q-layout view="lHh Lpr lFf">
    <q-header elevated>
      <q-toolbar>
        <q-btn
          flat
          dense
          round
          icon="menu"
          aria-label="Menu"
          @click="toggleLeftDrawer"
        />

        <q-toolbar-title>
          Treasure Hunting
        </q-toolbar-title>

      </q-toolbar>
    </q-header>

    <q-drawer v-model="state.leftDrawer_flag" show-if-above bordered>
      <q-scroll-area class="fit">
        <q-list>
          <q-item-label header>Game Section</q-item-label>

          <template v-for="(menu_item, menu_index) in state.menu_list" :key="menu_index">
            <q-item clickable v-if="state.user_permission >= menu_item.permission" @click="onMenuClick(menu_index)" :active="menu_index === state.menu_pos" v-ripple>
              <q-item-section avatar>
                <q-icon :name="menu_item.icon" />
              </q-item-section>
              <q-item-section>
                {{ menu_item.label }}
              </q-item-section>
            </q-item>
            <q-separator :key="'sep' + menu_index"  v-if="menu_item.separator" />
          </template>
        </q-list>
      </q-scroll-area>
    </q-drawer>

    <q-page-container>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent, ref, reactive, onMounted } from 'vue';
import { useRoute, useRouter } from "vue-router";
import { api } from "boot/axios";

export default defineComponent({
  name: 'InGameLayout',

  setup () {
    const route = useRoute();
    const router = useRouter();
    const state = reactive({
      leftDrawer_flag: true,
      user_permission: 1,
      menu_list: [
        {
          icon: 'accessibility_new',
          label: '玩家',
          permission: 1,
          separator: true
        },
        {
          icon: 'sports_esports',
          label: '游戏',
          permission: 1,
          separator: true
        },
        {
          icon: 'inventory_2',
          label: '储物箱',
          permission: 1,
          separator: true
        },
        {
          icon: 'storefront',
          label: '市场',
          permission: 1,
          separator: true
        },
        {
          icon: 'settings',
          label: '管理员',
          permission: 2,
          separator: true
        }
      ],
      menu_pos: 0
    });

    onMounted(() => {
      getUserInfo();
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

    function getUserInfo(){
      let url = getHostUrl() + ":5000/user/info"
      api.get(url).then((response) => {
        if(response.data.status == 1){
          let info_dict = response.data.info;
          state.user_permission = info_dict.permission;
        }
      })
      .catch((error) => {
        console.log(error);
      });
    }

    function toggleLeftDrawer () {
      state.leftDrawer_flag = !state.leftDrawer_flag
    }
    
    function onMenuClick(menu_index){
      state.menu_pos = menu_index;
      if(menu_index == 0){
        router.push("/board");
      }
      else if(menu_index == 1){
        router.push('/game');
      }
      else if(menu_index == 2){
        router.push('/container');
      }
      else if(menu_index == 3){
        router.push('/market');
      }
      else if(menu_index == 4){
        router.push('/admin');
      }
    }

    return {
      state,
      onMenuClick,
      toggleLeftDrawer
    }
  }
})
</script>
