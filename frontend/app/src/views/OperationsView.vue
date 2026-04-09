<script setup lang="ts">
import {onMounted, ref} from 'vue';

const data = ref(null);

  onMounted(async () => {
    try {
      const response = await fetch('/transactions', {
        method: 'GET',
        headers: {
        'Content-Type': 'application/json',
      },
      });
      if (!response.ok) {
        throw new Error('Network response was not ok');
      }
      data.value = await response.json();
    } catch (error) {
      console.error('Error fetching data:', error);
    }
  });
</script>

<template>
  <main>
    Operations view
  </main>
</template>
