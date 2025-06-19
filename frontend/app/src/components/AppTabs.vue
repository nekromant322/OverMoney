<template>
  <div class="container">
    <ul class="list">
      <slot></slot>
    </ul>
  </div>
</template>

<style scoped>
.list {
  display: flex;
  justify-content: center;
  border-top: 1px solid #30363D;
  border-bottom: 1px solid #30363D;
  padding: 0 32px;
  container-type: inline-size;
}
</style>