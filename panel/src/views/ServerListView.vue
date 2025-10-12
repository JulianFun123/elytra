<script setup lang="ts">
import DataTable from 'primevue/datatable'
import Column from 'primevue/column'
</script>

<template>
  <div>
    <h2 class="text-2xl">Servers</h2>

    <DataTable :value="[
      {
        id: 'baseraid',
        proxy: 'proxy-1',
        node: 'master',
        info: {
          pingPlayerCount: 3,
          pingMaxPlayerCount: 20
        }
      }
    ]">
      <Column field="id" header="ID"  />
      <Column field="proxy" header="Proxy"  />
      <Column field="node" header="Node"  />
      <Column :field="(s) => `${s.info.pingPlayerCount}/${s.info.pingMaxPlayerCount}`" header="Players"  />
    </DataTable>
  </div>
</template>

<style scoped>

</style>
