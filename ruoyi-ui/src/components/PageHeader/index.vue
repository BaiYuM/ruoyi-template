<template>
  <div class="page-header mb-4 flex items-center justify-between">
    <div>
      <h2 class="page-title">{{ title }}</h2>
    </div>
    <div class="header-actions">
      <slot name="actions" />
    </div>
  </div>
</template>

<script setup>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import menuTitles from '@/views/exposure/menuTitles'

const route = useRoute()
const title = computed(() => {
  if (route.meta && route.meta.title) return route.meta.title
  const seg = route.path ? route.path.split('/').filter(Boolean).pop() : ''
  if (!seg) return '页面'
  if (menuTitles[seg]) return menuTitles[seg]
  return seg.replace(/[-_]/g, ' ')
})
</script>

<style scoped>
.page-header{display:flex;align-items:center;gap:12px}
.page-title{font-size:20px;font-weight:600;margin:0}
.header-actions{display:flex;align-items:center}
</style>
