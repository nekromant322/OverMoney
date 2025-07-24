<script setup lang="ts">
</script>

<template>
    <div :class="$style.overlay"></div>
</template>

<style module>
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100vw;
  height: 100vh;
  background-color: #212830;
  opacity: 0.4;
  z-index: 1;
}
</style>