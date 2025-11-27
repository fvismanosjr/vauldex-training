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
    Search,
    Plus,
    X,
    Pencil,
    ChevronsUpDown,
    ChevronUp,
    ChevronDown,
    GraduationCap,
} from 'lucide-vue-next'

import DegreeDialog from '@/components/DegreeDialog.vue'
import { getDegrees } from '@/services/degree'
import type { DegreeType } from '@/types/DegreeType'
import type { PaginationType } from '@/types/PaginationType'
import type { SortType } from '@/types/SortType'
import type { DialogType } from '@/types/DialogType'
import { onBeforeMount, ref } from 'vue';

let degreeDialogKey = ref(0);
let paginationKey = ref(0);
let isLoading = ref(true);
let degrees = ref<DegreeType[]>([]);
let degreePagination = ref<PaginationType>({
    size: 5,
    totalElements: 0,
    totalPages: 0,
    number: 0,
});

let degreeSort = ref<SortType>({
    by: "name",
    order: "asc"
});

let searchInput = ref("");
let degreeDialogProps = ref<DialogType>({
    id: 0,
    isOpen: false,
    action: "add"
});

onBeforeMount(async () => {
    searchInput.value = "";
    await fetchDegrees();
});

const fetchDegrees = async (page: number = 0) => {
    isLoading.value = true;

    await getDegrees(
        searchInput.value,
        degreeSort.value.by,
        degreeSort.value.order,
        degreePagination.value.size,
        (page > 0 ? page - 1 : 0)
    ).then(async (response) => {
        return await response.json();
    }).then((result) => {
        isLoading.value = false;

        degrees.value = [];
        degreePagination.value = {
            size: 10,
            totalElements: 0,
            totalPages: 0,
            number: 0,
        };

        if (result.page.totalElements > 0) {
            degrees.value = result._embedded.degreeResponseList;
            degreePagination.value = result.page;
        }
    });
}

const triggerDegreeDialogOpen = (action: string = "add", studentId: number = 0) => {
    degreeDialogKey.value++;
    degreeDialogProps.value.id = studentId;
    degreeDialogProps.value.isOpen = true;
    degreeDialogProps.value.action = action;
}

const triggerSort = async (by: string, order: string) => {
    degreeSort.value.by = by;
    degreeSort.value.order = order;

    await refreshTable();
}

const refreshTable = async () => {
    await fetchDegrees();
    paginationKey.value++;
}
</script>

<template>
    <div>
        <div class="flex items-center justify-between mb-3">
            <div class="w-auto">
                <form @submit.prevent="refreshTable">
                    <InputGroup>
                        <InputGroupInput v-model="searchInput" placeholder="Type to search..." />
                        <InputGroupAddon>
                            <Search />
                        </InputGroupAddon>
                        <InputGroupAddon align="inline-end">
                            <InputGroupButton variant="secondary" type="submit">
                                Search
                            </InputGroupButton>
                        </InputGroupAddon>
                    </InputGroup>
                </form>
            </div>
            <Button variant="secondary" @click="triggerDegreeDialogOpen()">
                <Plus />
                Add Degree
            </Button>
        </div>
        <Table>
            <TableHeader>
                <TableRow>
                    <TableHead class="w-1/2">
                        <DropdownMenu>
                            <DropdownMenuTrigger as-child>
                                <Button variant="ghost">
                                    Name
                                    <ChevronUp v-if="degreeSort.by == 'name' && degreeSort.order == 'asc'"></ChevronUp>
                                    <ChevronDown v-else-if="degreeSort.by == 'name' && degreeSort.order == 'desc'">
                                    </ChevronDown>
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
                    <TableHead class="w-[100px]">Abbr</TableHead>
                    <TableHead>
                        <DropdownMenu>
                            <DropdownMenuTrigger as-child>
                                <Button variant="ghost">
                                    No. of Students
                                    <ChevronUp v-if="degreeSort.by == 'noOfStudents' && degreeSort.order == 'asc'">
                                    </ChevronUp>
                                    <ChevronDown
                                        v-else-if="degreeSort.by == 'noOfStudents' && degreeSort.order == 'desc'">
                                    </ChevronDown>
                                    <ChevronsUpDown v-else></ChevronsUpDown>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="start">
                                <DropdownMenuGroup>
                                    <DropdownMenuItem @click.prevent="triggerSort('noOfStudents', 'asc')">
                                        <ChevronUp></ChevronUp>
                                        <span>Ascending</span>
                                    </DropdownMenuItem>
                                    <DropdownMenuItem @click.prevent="triggerSort('noOfStudents', 'desc')">
                                        <ChevronDown></ChevronDown>
                                        <span>Descending</span>
                                    </DropdownMenuItem>
                                </DropdownMenuGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </TableHead>
                    <TableHead class="text-right w-[100px]"></TableHead>
                </TableRow>
            </TableHeader>
            <TableBody v-if="!isLoading">
                <TableRow v-for="degree in degrees" :key="`student-item-${degree.id}`">
                    <TableCell class="font-medium px-5">
                        <RouterLink 
                            :to="{ name: 'degree-profile', params: { id: degree.id}}"
                            class="inline-block"
                        >
                            {{ degree.name }}
                        </RouterLink>
                    </TableCell>
                    <TableCell>
                        <Badge>{{ degree.abbreviation }}</Badge>
                    </TableCell>
                    <TableCell class="px-5">{{ degree.noOfStudents }}</TableCell>
                    <TableCell class="text-right">
                        <DropdownMenu>
                            <DropdownMenuTrigger as-child>
                                <Button variant="outline" size="icon">
                                    <Ellipsis />
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                                <DropdownMenuGroup>
                                    <DropdownMenuItem @click.prevent="triggerDegreeDialogOpen('edit', degree.id)">
                                        <Pencil />
                                        Update Degree
                                    </DropdownMenuItem>
                                    <DropdownMenuSeparator />
                                    <DropdownMenuItem class="text-red-500"
                                        @click.prevent="triggerDegreeDialogOpen('delete', degree.id)">
                                        <X />
                                        Delete Student
                                    </DropdownMenuItem>
                                </DropdownMenuGroup>
                            </DropdownMenuContent>
                        </DropdownMenu>
                    </TableCell>
                </TableRow>
                <TableRow v-if="!degrees.length">
                    <TableCell class="text-center" colspan="4">
                        No result found
                    </TableCell>
                </TableRow>
            </TableBody>
        </Table>
        <Pagination class="justify-end mt-4" :items-per-page="degreePagination.size"
            :total="degreePagination.totalElements" :default-page="1" :key="paginationKey" v-slot="{ page }">
            <PaginationContent v-slot="{ items }">
                <PaginationPrevious @click.prevent="fetchDegrees(page - 1)" />

                <template v-for="(item, index) in items" :key="index">
                    <PaginationItem v-if="item.type === 'page'" :value="item.value" :is-active="item.value === page"
                        @click.prevent="fetchDegrees(item.value)">
                        {{ item.value }}
                    </PaginationItem>
                </template>

                <PaginationEllipsis v-if="degreePagination.totalPages > 5" :index="4" />

                <PaginationNext @click.prevent="fetchDegrees(page + 1)" />
            </PaginationContent>
        </Pagination>

        <DegreeDialog 
            :id="degreeDialogProps.id" 
            :action="degreeDialogProps.action" 
            :is-open="degreeDialogProps.isOpen"
            :key="degreeDialogKey"
            v-model:open="degreeDialogProps.isOpen" 
            @reload-table="refreshTable()" 
        />
    </div>
</template>