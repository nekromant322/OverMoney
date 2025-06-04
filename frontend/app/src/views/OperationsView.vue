<script setup lang="ts">
  import { ref, onMounted } from 'vue';

  const data = ref(null);

  onMounted(async () => {
    try {
      const response = await fetch('/api/transactions', {
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
