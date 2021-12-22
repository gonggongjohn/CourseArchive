<template>
  <q-layout view="lHh Lpr lFf">
    <q-page-container>
      <div class="row justify-center">
        <q-img src="../assets/game_icon.png" style="max-width: 800px" />
      </div>
      <router-view />
    </q-page-container>
  </q-layout>
</template>

<script>
import { defineComponent } from 'vue'

export default defineComponent({
  name: 'SignLayout',

  components: {

  },

  setup () {

    return {
    }
  }
})
</script>
