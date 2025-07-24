<script setup lang="ts">
defineProps({
    title: {
        type: String,
    },
    selected: {
        type: Boolean,
        default: false,
    },
});
</script>

<template>
    <button :class="[$style.tabButton, {[$style.selected]: selected }]">
        <span :class="$style.title">{{ title }}</span>
    </button>
</template>

<style module>
.tabButton {
    padding: 5px 8px;
    border-radius: 6px;
    color: #C9D1D9;
    transition: all 150ms ease-in-out;
}

.tabButton.selected {
    background: #21262D;
}

.title {
    font-family: 'Noto Sans', Arial, Helvetica, sans-serif;
    font-size: 14px;
    color: #C9D1D9;
    font-weight: 600;
}
</style>