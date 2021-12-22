<template>
  <q-page class="q-mt-sm">
    <div class="text-grey-9 text-weight-bold">

      <!--头图-->
      <div class="bg-white row">
        <div class="row q-col-gutter-sm col-sm-10 col-md-8">
          <div class="col-12 q-ma-lg">
            <div class="text-h5 q-mt-sm q-mb-xs">我的店铺</div>
            <q-separator class="q-my-md"/>
            <q-carousel
              :class="$q.platform.is.desktop ? 'q-ml-sm' : ''"
              arrows
              animated
              v-model="objData.slide"
              height="400px"
            >
              <q-carousel-slide :name="s.slide" img-src="https://cdn.quasar.dev/img/mountains.jpg"
              @click="$router.push('/shop?id='+s.shopID)" class="cursor-pointer"
              v-for="s in objData.shop" v-bind:key="s.shopID">
                <div class="absolute-bottom custom-caption">
                  <div class="text-subtitle1"> {{ s.shopID }} </div>
                </div>
              </q-carousel-slide>
            </q-carousel>
          </div>
        </div>
        <div class="col-sm-2 col-md-4">
          <div class="col-12 q-ma-lg">
            <q-btn class="q-my-xs" label="创建店铺" @click="objData.create.dialog = true" color="primary"/>
          </div>
        </div>
      </div>
    </div>

    <!-- 创建商店 -->
    <q-dialog v-model="objData.create.dialog" persistent>
      <q-card style="min-width: 500px">
        <q-card-section>
          <div class="text-h6">创建商店</div>
        </q-card-section>

        <q-card-section class="q-pt-none">

          <q-input dense v-model="objData.create.name" filled 
          :type="text" label="商店ID">
          </q-input>

        </q-card-section>

        <q-card-actions align="right" class="text-primary">
          <q-btn flat label="取消创建" v-close-popup />
          <q-btn flat @click="create_store" label="确认创建" v-close-popup />
        </q-card-actions>
      </q-card>
    </q-dialog>
  </q-page>
</template>

<script>
import { reactive, onMounted } from 'vue' 
import { api, backend_port, testurl } from "../boot/axios";

export default {
  setup () {
    let objData = reactive({
      user_id: "",
      create: {
        dialog: false,
        name: "",
      },
      slide: 0,
      shop: [
        {
          slide: 0,
          shopID: "12345",
        },
        {
          slide: 1,
          shopID: "67890",
        },
        {
          slide: 2,
          shopID: "13579",
        },
        {
          slide: 3,
          shopID: "98765",
        },
      ],
    });
    
    function create_store() {
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/create_store";
      let body = { user_id: objData.user_id, store_id: objData.create.name };
      api.post(url, body).then((response) => {
        if(response.status == 200){
          alert("创建成功！");
          location.reload();
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }

    onMounted(() => {
      objData.user_id = window.localStorage.getItem('user_id')

      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/seller/store/" + objData.user_id;
      api.get(url).then((response) => {
        if(response.status == 200){
          objData.shop = response.data.stores;
          for (let i = 0; i < objData.shop.length; i++) {
            objData.shop[i]['shopID'] = objData.shop[i]['store_id']
            objData.shop[i]['slide'] = i
          }
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    })
    
    return {
      objData,
      create_store,
    }
  },
};
</script>

<style lang="sass" scoped>
.custom-caption
  text-align: center
  padding: 8px
  color: white
  background-color: rgba(0, 0, 0, .3)
</style>
