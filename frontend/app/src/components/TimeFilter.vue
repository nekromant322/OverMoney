<script setup lang="ts">
defineProps<{
  modelValue: TimePeriod
}>();

const emit = defineEmits(['update:modelValue']);
</script>

<script lang="ts">
enum TimePeriod {
  'D' = 'Д',
  'M' = 'М',
  'Y' = 'Г'
}

export { TimePeriod }
</script>

<template>
  <div :class="$style.filter">
    <button :class="[$style.button, { [$style.selectedButton]: modelValue === TimePeriod.D }]" @click="emit('update:modelValue', TimePeriod.D)">Д</button>
    <button :class="[$style.button, { [$style.selectedButton]: modelValue === TimePeriod.M }]" @click="emit('update:modelValue', TimePeriod.M)">M</button>
    <button :class="[$style.button, { [$style.selectedButton]: modelValue === TimePeriod.Y }]" @click="emit('update:modelValue', TimePeriod.Y)">Г</button>
  </div>
</template>

<style module>
.filter {
  width: fit-content;
  border: 1px solid #30363D;
  border-radius: 6px;
}

.button {
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
  font-size: 14px;
  color: #C9D1D9;
  background: transparent;
  transition: all 150ms ease-in-out;
  line-height: 32px;
  padding: 0 8px;
}

.selectedButton {
  background: #30363D;
}
</style>
