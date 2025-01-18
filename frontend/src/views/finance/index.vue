<template>
  <div class="finance-container">
    <router-view v-slot="{ Component }">
      <transition name="fade" mode="out-in">
        <component :is="Component" />
      </transition>
    </router-view>
  </div>
</template>

<script setup>
import { onMounted } from 'vue'
import { useRoute, useRouter } from 'vue-router'

const route = useRoute()
const router = useRouter()

onMounted(() => {
  // 如果访问 /finance 路径，重定向到第一个子路由
  if (route.path.endsWith('/finance')) {
    router.push('/dashboard/finance/income')
  }
})
</script>

<style scoped>
.finance-container {
  padding: 20px;
  height: 100%;
}

.fade-enter-active,
.fade-leave-active {
  transition: opacity 0.3s ease;
}

.fade-enter-from,
.fade-leave-to {
  opacity: 0;
}
</style> 