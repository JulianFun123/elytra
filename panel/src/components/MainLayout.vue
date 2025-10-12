<script lang="ts" setup>
import Menu from 'primevue/menu'
</script>
<template>
  <div class="grid grid-cols-[300px_auto] gap-10 h-full">
    <Menu
      :pt="{
      root: {
        class: 'border-0 border-r-1 rounded-none'
      }
      }"
      :model="[
        {
          label: 'Nodes',
          to: { name: 'nodes' }
        },
        {
          label: 'Servers',
          to: { name: 'servers' }
        },
        {
          label: 'Proxies',
          to: { name: 'proxies' }
        },
      ]"
    >
      <template #start>
        <div class="p-4 pb-2">
          <span class="text-xl">
            <span
              v-for="(c, i) in 'elytra'.split('')"
              :style="{
                animation: 'purple-color-effect 4s infinite linear',
                animationDelay: `${i * 100}ms`
              }"
            >
              {{c}}
            </span>
          </span>
        </div>
      </template>
    </Menu>
    <div class="p-10">
      <router-view />
    </div>
  </div>
</template>

<style>
@keyframes purple-color-effect {
  0% {
    color: #000;
  }
  6% {
    color: #cc20c9;
  }
  12% {
    color: #7e147b;
  }
  25% {
    color: #000;
  }
}
</style>
