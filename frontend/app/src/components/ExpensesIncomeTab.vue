<script setup lang="ts">
</script>

<template>
  <div :class="$style.tab">
    <ul :class="$style.list">
      <li v-for="index in 100" :key="index" :class="$style.item">
        <div :class="$style.category">Бизнес</div>
        <div :class="$style.price">- 120 840</div>
      </li>
    </ul>
  </div>
</template>

<style module>
.tab {
  overflow: auto;
  margin-top: 16px;
  padding-right: 24px;
  scrollbar-color: #424242 #686868;
  box-sizing: border-box;
  height: Calc(100% - 80px);
}

.list {
  padding: 16px 0;
}

.item {
  display: flex;
  align-items: center;
  justify-content: space-between;
}

.category {
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 12px;
  color: #C9D1D9;
  line-height: 40px;
}

.price {
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 12px;
  color: #FF918B;
  line-height: 40px;
}
</style>