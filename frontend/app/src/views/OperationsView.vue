<script setup lang="ts">
import { ref, onMounted, Fragment } from 'vue';
import ViewWrapper from '@/components/ViewWrapper.vue';
import ExampleAvatar from '@/assets/images/example-avatar.jpg';

const data = ref(null);

// TODO Get data from an endpoint
const avatar = '';
// TODO Get data from an endpoint
const username = 'nekromant322';

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
  <ViewWrapper :avatar="ExampleAvatar" :username="username">
    Operations
  </ViewWrapper>
</template>
