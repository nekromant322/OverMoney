<script setup lang="ts">
import AppIcon from '@/components/AppIcon.vue';
import AppInput from '@/components/AppInput.vue';
import SearchIcon from '@/assets/images/Search.svg';
import { ref } from 'vue';

const category = ref('');
</script>

<template>
  <div class="tab">
    <aside class="categories">
      <AppInput v-model="category" placeholder="Найти категорию...">
        <AppIcon :src="SearchIcon" alt="search" />
      </AppInput>
    </aside>
    <main class="operations">
      Operations
    </main>
  </div>
</template>

<style scoped>
.tab {
  display: flex;
  color: #fff;
  padding: 0 32px;
  height: calc(100vh - 110px);
}

.categories {
  padding: 24px 24px 24px 0;
  border-right: 1px solid #30363D;
}

.operations {
  padding: 24px 0 24px 24px;
}
</style>