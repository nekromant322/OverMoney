<script setup lang="ts">
import AppIcon from '@/components/AppIcon.vue';
import AppInput from '@/components/AppInput.vue';
import SearchIcon from '@/assets/images/Search.svg';
import { defineAsyncComponent, ref } from 'vue';
import TextTabButton from './TextTabButton.vue';
import TimeFilter, { TimePeriod } from './TimeFilter.vue';

const ExpensesTab = defineAsyncComponent(() =>
  import('@/components/ExpensesTab.vue')
);

const IncomeTab = defineAsyncComponent(() =>
  import('@/components/IncomeTab.vue')
);

const category = ref('');
const period = ref(TimePeriod.M);
const activeTab = ref(0);
const tabs = [
  { id: 'expenses', title: 'Расходы', component: ExpensesTab },
  { id: 'income', title: 'Доходы', component: IncomeTab }
];
</script>

<template>
  <div :class="$style.tab">
    <aside :class="$style.categories">
      <AppInput v-model="category" placeholder="Найти категорию...">
        <AppIcon :src="SearchIcon" alt="search" />
      </AppInput>
      <div :class="$style.filters">
        <div>
          <TextTabButton 
            v-for="(tab, index) in tabs" 
            :key="tab.id" 
            :title="tab.title" 
            :class="$style.textTabButton"
            :selected="activeTab === index"
            @click="activeTab = index" />
        </div>
        <TimeFilter :class="$style.timeFilter" v-model="period" />
      </div>
      <KeepAlive>
        <component :is="tabs[activeTab].component" />
      </KeepAlive>
    </aside>
    <main :class="$style.operations">
      Operations
    </main>
  </div>
</template>

<style module>
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

.filters {
  margin-top: 24px;
  display: flex;
  align-items: center;
} 

.textTabButton {
  margin-left: 8px;
}

.textTabButton:first-child {
  margin-left: 0;
}

.timeFilter {
  margin-left: 16px;
}

.operations {
  padding: 24px 0 24px 24px;
}
</style>