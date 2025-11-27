<script setup lang='ts'>
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"

import {
    SquareLibrary, 
    GraduationCap 
} from "lucide-vue-next";

import { getStudents } from "@/services/student";
import { ref } from 'vue';
import { getDegrees } from "@/services/degree";

let totalStudents = ref(0);
let totalDegrees = ref(0);

getStudents().then(async (response) => {
    return await response.json();
}).then((result) => {
    totalStudents.value = result.page.totalElements;
});

getDegrees().then(async (response) => {
    return await response.json();
}).then((result) => {
    totalDegrees.value = result.page.totalElements;
});
</script>

<template>
    <div class="grid grid-cols-1 gap-5 md:grid-cols-4">
        <Card class="gap-4">
            <CardHeader>
                <CardTitle class="font-semibold flex items-center justify-between">
                    Students
                    <SquareLibrary class="text-muted-foreground"></SquareLibrary>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <p class="text-4xl font-semibold mb-2">{{ totalStudents }}</p>
                <CardDescription>Total no. of students</CardDescription>
            </CardContent>
        </Card>
        <Card class="gap-4">
            <CardHeader>
                <CardTitle class="font-semibold flex items-center justify-between">
                    Degrees
                    <GraduationCap class="text-muted-foreground"></GraduationCap>
                </CardTitle>
            </CardHeader>
            <CardContent>
                <p class="text-4xl font-semibold mb-2">{{ totalDegrees }}</p>
                <CardDescription>Total no. of degrees</CardDescription>
            </CardContent>
        </Card>
    </div>
</template>