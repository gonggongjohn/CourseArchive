<template>
  <div>
    <div class="row bg-white q-mt-sm q-mx-lg">
      <div class="col-lg-12 col-md-12 col-sm-12 col-xs-12">

        <div class="q-px-lg q-pt-sm q-pb-none row items-start q-gutter-md">
          <div v-for="tag in tags" v-bind:key="tag">
            <div class="q-py-md">
              <q-btn outline color="primary" :label="tag" icon="clear" v-on:click="objData.deleteTag(tag);"/>
            </div>
          </div>
        </div>

        <div class="q-px-lg q-pt-none q-pb-none row items-start q-gutter-md justify-between">
            <div class="q-pa-md">
              <q-btn-toggle
                v-model="objData.default.orderType"
                toggle-color="primary"
                :options="[
                  {label: '新品↓', value: 0},
                  {label: '销量↓', value: 1},
                  {label: '价格↑', value: 2},
                  {label: '价格↓', value: 3},
                ]"
              />
            </div>
        </div>

        <div class="q-px-lg q-pt-sm q-pb-lg flex flex-center">
          <q-pagination
            v-model="objData.default.current_page"
            :max="5"
            :max-pages="6"
            direction-links
          />
        </div>

        <div class="q-px-lg q-pt-sm q-pb-none row items-start q-gutter-md row">
          <q-card @click="$router.push({path:'/details', query:{itemID:i.itemID}})" class="q-ma-none q-pa-xs cursor-pointer col-3" :class="class_val"
           style="min-height:400px"
           v-for="i in objData.item" v-bind:key="i.itemID">

            <q-img :src="i.img"></q-img>

            <q-card-section class="q-pb-xs q-pt-md">

              <div class="row no-wrap items-center">
                <div class="col text-subtitle2 ellipsis-2-lines text-grey-10">
                  {{ i.itemName }}
                </div>
                <div class="col-auto text-grey text-caption q-pt-md row no-wrap items-center">
                  <q-icon name="place"></q-icon>
                    {{ i.address }}
                </div>
              </div>
            </q-card-section>

            <q-card-section class="q-py-sm">
              <div>
                <div class="text-caption text-green-8 text-weight-bolder">特价好货</div>
                <span class="text-h6">￥{{ i.price }}</span>
              </div>
            </q-card-section>

            <q-separator></q-separator>

            <q-card-actions>
              <q-btn flat class="text-weight-bold text-capitalize" dense color="primary">
                查看详情
              </q-btn>
            </q-card-actions>
            
          </q-card>
        </div>

      </div>
    </div>
  </div>
</template>

<script>
import { reactive, onMounted, computed } from 'vue'
import { api, backend_port, testurl } from "../boot/axios";
import { useRouter, useRoute } from "vue-router";

export default {
  setup () {
    const router = useRouter()
    const route = useRoute()

    let objData = reactive({
      searchText: "",
      default: {
        stars: 4,
        current_page: 1,
        orderType: 0,
        test: 0,
      },
      tags: ['A', 'B', 'C'],
      item_count: 20,
      num_per_page: 10,
      item: [
        {
          itemID: '1234512345',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《他改变了中国》',
          address: '上海 闵行',
          author: 'ABC',
          price: 48,
        },
        {
          itemID: '1234512345',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《他改变了中国》',
          address: '上海 闵行',
          author: 'ABC',
          price: 48,
        },
        {
          itemID: '12345123456',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《数据科学与工程专业人才培养方案与核心课程体系》',
          address: '上海 闵行',
          author: 'ABC',
          price: 20,
        },
        {
          itemID: '12345123456',
          img: 'https://cdn.quasar.dev/img/mountains.jpg',
          itemName: '《数据科学与工程专业人才培养方案与核心课程体系》',
          address: '上海 闵行',
          author: 'ABC',
          price: 20,
        }
      ]
    });
    
    function deleteTag(tag) {
        objData.tags = objData.tags.filter(function(item) {
            return item != tag;
        });
    }

    function search(page, num_per_page, strategy) {
      objData.item = [];
      let url = location.protocol + "//" + ( testurl || location.hostname ) + ":" + backend_port + "/search/book/0/" 
      + page + "/" + num_per_page + "/" + strategy + '?keyword=' + (objData.searchText || '')
      api.get(url).then((response) => {
        if(response.status == 200) {
          objData.item_count = response.data.count;
          response.data.books.forEach(element => {
            objData.item.push({
              itemID: element.id,
              img: 'data:image/png;base64,' + element.book_info.picture,
              itemName: element.book_info.title,
              author: element.book_info.author,
              price: element.price / 100,
              address: "",
            });
          });
        }
      })
      .catch((error) => {
        alert(error.response.data.message);
      });
    }    

    onMounted(() => {
      objData.searchText = route.query.searchText;
      // objData.tags = objData.searchText.trim().split(/\s+/);
      search(1, objData.num_per_page, objData.default.orderType);
    })
    
    return {
      objData,
      deleteTag,
    }
  },
}
</script>

<style scoped>
  .my-card {
    width: 100%;
    max-width: 300px;
  }
</style>
