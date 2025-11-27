<script setup lang="ts">
import { findDegreeWithStudents } from '@/services/degree';
import { ref } from 'vue';
import { useRoute } from 'vue-router';

const route = useRoute();

findDegreeWithStudents(Number(route.params.id)).then(async (response) => {
    return await response.json()
}).then((result) => {
    document.title = `No Brand - ${result.name}`;
    (route.meta.breadcrumb as Array<String>).push(result.name);
})
</script>
<template>
    I am here
</template>