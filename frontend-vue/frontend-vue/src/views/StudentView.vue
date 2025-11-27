<script lang="ts" setup>
import {
    Table,
    TableBody,
    TableCell,
    TableHead,
    TableHeader,
    TableRow,
} from '@/components/ui/table'

import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuGroup,
    DropdownMenuItem,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu"

import {
    InputGroup,
    InputGroupAddon,
    InputGroupButton,
    InputGroupInput
} from "@/components/ui/input-group"

import {
    Pagination,
    PaginationContent,
    PaginationEllipsis,
    PaginationItem,
    PaginationNext,
    PaginationPrevious,
} from '@/components/ui/pagination'

import { Badge } from '@/components/ui/badge'
import { Button } from "@/components/ui/button"

import { 
    Ellipsis, 
    UserRoundPen, 
    UserRoundX, 
    UserRoundPlus, 
    UserRoundSearch,
    ChevronsUpDown,
    ChevronUp,
    ChevronDown,
} from 'lucide-vue-next'

import StudentDialog from '@/components/StudentDialog.vue'
import type { StudentType } from '@/types/StudentType'
import type { PaginationType } from '@/types/PaginationType'
import type { DialogType } from '@/types/DialogType'
import type { SortType } from '@/types/SortType'
import { ref } from 'vue';
import { getStudents } from '@/services/student'

let studentDialogKey = ref(0);
let paginationKey = ref(0);
let isLoading = ref(true);
let students = ref<StudentType[]>([]);
let studentPagination = ref<PaginationType>({
    size: 5,
    totalElements: 0,
    totalPages: 0,
    number: 0,
});

let searchInput = ref("");
let studentDialogProps = ref<DialogType>({
    id: 0,
    isOpen: false,
    action: "add"
});

let studentSort = ref<SortType>({
    by: "name",
    order: "asc"
});

const fetchStudent = async (page: number = 0) => {
    isLoading.value = true;

    await getStudents(
        searchInput.value,
        studentSort.value.by,
        studentSort.value.order,
        studentPagination.value.size,
        (page > 0 ? page - 1 : 0)
    ).then(async (response) => {
        return await response.json();
    }).then((result) => {
        isLoading.value = false;

        students.value = [];
        studentPagination.value = {
            size: 5,
            totalElements: 0,
            totalPages: 0,
            number: 0,
        };

        if (result.page.totalElements > 0) {
            students.value = result._embedded.studentResponseList;
            studentPagination.value = result.page;
        }
    });
}

const triggerStudentDialogOpen = (action: string = "add", studentId: number = 0) => {
    studentDialogKey.value++;
    studentDialogProps.value.id = studentId;
    studentDialogProps.value.isOpen = true;
    studentDialogProps.value.action = action;
}

const triggerSort = async (by: string, order: string) => {
    studentSort.value.by = by;
    studentSort.value.order = order;

    await refreshTable();
}

const refreshTable = async () => {
    await fetchStudent();
    paginationKey.value++;
}

fetchStudent();
</script>

<template>
    <div>
        <div class="flex items-center justify-between mb-3">
            <div class="w-auto">
                <form @submit.prevent="refreshTable">
                    <InputGroup>
                        <InputGroupInput v-model="searchInput" placeholder="Type to search..." />
                        <InputGroupAddon>
                            <UserRoundSearch />
                        </InputGroupAddon>
                        <InputGroupAddon align="inline-end">
                            <InputGroupButton variant="secondary" type="submit">
                                Search
                            </InputGroupButton>
                        </InputGroupAddon>
                    </InputGroup>
                </form>
            </div>
            <Button variant="secondary" @click="triggerStudentDialogOpen()">
                <UserRoundPlus />
                Add Student
            </Button>
        </div>
        <Table>
            <TableHeader>
                <TableRow>
                    <TableHead class="w-1/2 p-1">
                        <DropdownMenu>
                            <DropdownMenuTrigger as-child>
                                <Button variant="ghost">
                                    Name
                                    <ChevronUp v-if="studentSort.by == 'name' && studentSort.order == 'asc'"></ChevronUp>
                                    <ChevronDown v-else-if="studentSort.by == 'name' && studentSort.order == 'desc'"></ChevronDown>
                                    <ChevronsUpDown v-else></ChevronsUpDown>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="start">
                                <DropdownMenuGroup>
                                    <DropdownMenuItem @click.prevent="triggerSort('name', 'asc')">
                                        <ChevronUp></ChevronUp>
                                        <span>Ascending</span>
                                    </DropdownMenuItem>
                                    <DropdownMenuItem @click.prevent="triggerSort('name', 'desc')">
                                        <ChevronDown></ChevronDown>
                                        <span>Descending</span>
                                    </DropdownMenuItem>
                                </DropdownMenuGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </TableHead>
                    <TableHead>Degree</TableHead>
                    <TableHead class="text-right w-[100px]"></TableHead>
                </TableRow>
            </TableHeader>
            <TableBody v-if="!isLoading">
                <TableRow v-for="student in students" :key="`student-item-${student.id}`">
                    <TableCell class="font-medium px-5">
                        <RouterLink 
                            :to="{ name: 'student-profile', params: { id: student.id}}"
                            class="inline-block"
                        >
                            <p>{{ student.name }}</p>
                            <p class="text-sm font-normal">{{ student.email }}</p>
                        </RouterLink>
                    </TableCell>
                    <TableCell>
                        <Badge v-if="student.degree">{{ student.degree }}</Badge>
                    </TableCell>
                    <TableCell class="text-right">
                        <DropdownMenu>
                            <DropdownMenuTrigger as-child>
                                <Button variant="outline" size="icon">
                                    <Ellipsis />
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                                <DropdownMenuGroup>
                                    <DropdownMenuItem @click.prevent="triggerStudentDialogOpen('edit', student.id)">
                                        <UserRoundPen />
                                        Update Student
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem class="text-red-500"
                                        @click.prevent="triggerStudentDialogOpen('delete', student.id)">
                                        <UserRoundX />
                                        Delete Student
                                    </DropdownMenuItem>
                                </DropdownMenuGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </TableCell>
                </TableRow>
                <TableRow v-if="!students.length">
                    <TableCell class="text-center" colspan="4">
                        No result found
                    </TableCell>
                </TableRow>
            </TableBody>
        </Table>
        <Pagination 
            class="justify-end mt-4" 
            :items-per-page="studentPagination.size"
            :total="studentPagination.totalElements" 
            :default-page="1"
            :key="paginationKey" 
            v-slot="{ page }"
        >
            <PaginationContent v-slot="{ items }">
                <PaginationPrevious @click.prevent="fetchStudent(page - 1)" />

                <template v-for="(item, index) in items" :key="index">
                    <PaginationItem v-if="item.type === 'page'" :value="item.value" :is-active="item.value === page"
                        @click.prevent="fetchStudent(item.value)">
                        {{ item.value }}
                    </PaginationItem>
                </template>

                <PaginationEllipsis v-if="studentPagination.totalPages > 5" :index="4" />

                <PaginationNext @click.prevent="fetchStudent(page + 1)" />
            </PaginationContent>
        </Pagination>

        <StudentDialog 
            :id="studentDialogProps.id" 
            :action="studentDialogProps.action"
            :is-open="studentDialogProps.isOpen"
            :key="`student-dialog-${studentDialogKey}`" 
            v-model:open="studentDialogProps.isOpen"
            @reload-table="refreshTable()" 
        />
    </div>
</template>