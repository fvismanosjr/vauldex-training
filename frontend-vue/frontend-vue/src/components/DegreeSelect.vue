<script setup lang="ts">
import {
    Select,
    SelectContent,
    SelectGroup,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select"

import { getDegrees } from "@/services/degree"
import type { DegreeType } from "@/types/DegreeType";
import { ref } from 'vue';

let degrees = ref<DegreeType[]>([]);

getDegrees("", "name", "asc", -1).then(async (response) => {
    return await response.json();
}).then((result) => {
    degrees.value = result._embedded.degreeResponseList;
});

</script>

<template>
    <Select>
        <SelectTrigger class="w-full">
            <SelectValue placeholder="Select a degree" />
        </SelectTrigger>
        <SelectContent>
            <SelectGroup>
                <SelectItem
                    v-for="degree in degrees"
                    :key="degree.abbreviation"
                    :value="degree.abbreviation"
                >
                    {{ degree.name }}
                </SelectItem>
            </SelectGroup>
        </SelectContent>
    </Select>
</template>
