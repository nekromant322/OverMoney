<script setup lang="ts">
import AppIcon from '@/components/AppIcon.vue';
import ClearIcon from '@/assets/images/Clear.svg';
import { useTemplateRef } from 'vue';

const props = defineProps<{
  modelValue: string,
  placeholder: string
}>();

const emit = defineEmits(['update:modelValue']);

const clear = () => {
  emit('update:modelValue', '')
}

const handleInput = (event: Event) => {
  const target = event.target as HTMLInputElement
  emit('update:modelValue', target.value)
}
</script>

<template>
  <div class="wrapper">
    <div class="icon-wrapper">
      <slot></slot>
    </div>
    <input 
      ref="category-search" 
      name="category-search" 
      class="input" 
      type="text" 
      :value="props.modelValue" 
      :placeholder="props.placeholder" 
      @input="handleInput" />
    <button
      name="clear-button"
      type="reset"
      :class="['clear-button', { 'hidden': !modelValue }]"
      @click="clear">
      <AppIcon :src="ClearIcon" alt="clear" />
    </button>
  </div>
</template>

<style scoped>
.wrapper {
  border: 1px solid #30363D;
  border-radius: 6px;
  padding: 8px 12px;
  min-width: 200px;
  display: flex;
  align-items: center;
  box-sizing: border-box;
  position: relative;
}

.wrapper:focus-within {
  border: 2px solid #4493F8;
  padding: 7px 11px;
}

.icon-wrapper {
  width: 16px;
  height: 16px;
}

.input {
  width: calc(100% - 40px);
  margin-left: 8px;
  outline: none;
  background: transparent;
  border: none;
  color: #C9D1D9;
  font-size: 14px;
  font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
}

.input::placeholder {
  color: #6E7681;
}

.clear-button {
  width: 16px;
  height: 16px;
  padding: 0;
  position: absolute;
  right: 8px;
}

.wrapper:focus-within .clear-button {
  right: 7px;
}

.clear-button.hidden {
  visibility: hidden;
}
</style>