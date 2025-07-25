<script setup lang="ts">
import AppIcon from '@/components/AppIcon.vue';
import ClearIcon from '@/assets/images/Clear.svg';

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
  <div :class="$style.wrapper">
    <div :class="$style.iconWrapper">
      <slot></slot>
    </div>
    <input 
      ref="category-search" 
      name="category-search" 
      :class="$style.input" 
      type="text" 
      :value="props.modelValue" 
      :placeholder="props.placeholder" 
      @input="handleInput" />
    <button
      name="clear-button"
      type="reset"
      :class="[$style.clearButton, { [$style.hidden]: !modelValue }]"
      @click="clear">
      <AppIcon :src="ClearIcon" alt="clear" />
    </button>
  </div>
</template>

<style module>
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

.wrapper:hover {
  border: 1px solid #3D444D;
}

.wrapper:focus-within {
  border: 2px solid #4493F8;
  padding: 7px 11px;
}

.iconWrapper {
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

.clearButton {
  width: 16px;
  height: 16px;
  padding: 0;
  position: absolute;
  right: 8px;
}

.wrapper:focus-within .clear-button {
  right: 7px;
}

.hidden {
  visibility: hidden;
}
</style>