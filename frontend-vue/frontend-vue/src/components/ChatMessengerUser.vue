<script setup lang="ts">
import { ListboxContent, ListboxFilter, ListboxItem, ListboxItemIndicator, ListboxRoot, useFilter, type AcceptableInputValue, type AcceptableValue } from 'reka-ui'
import { Button } from '@/components/ui/button'
import { Popover, PopoverAnchor, PopoverContent, PopoverTrigger } from '@/components/ui/popover'
import { TagsInput, TagsInputInput, TagsInputItem, TagsInputItemDelete, TagsInputItemText } from '@/components/ui/tags-input'
import { CheckIcon, ChevronDown } from 'lucide-vue-next'
import type { ChatUserType } from '@/types/ChatUserType'
import { getStudentUsers } from '@/services/student'
import type { StudentType } from '@/types/StudentType'
import { useUserStore } from '@/stores/user'
import { ref, watch, computed } from 'vue'

const emit = defineEmits<{
    (e: "update:model-value", value: AcceptableValue): void,
    (e: "update:chat-name", value: string): void,
}>();

const { contains } = useFilter({ sensitivity: 'base' })
const user = useUserStore();
const searchUser = ref("");
const isPopOverOpen = ref(false);
const userCollection = ref<ChatUserType[]>([]);
const selectedUsers = ref<ChatUserType[]>([]);
const selectedUserIds = ref<number[]>([]);

const filteredUsers = computed(() => {
    return searchUser.value === ''
        ? userCollection.value
        : userCollection.value.filter(option => contains(option.label, searchUser.value))
});

watch(searchUser, (f) => {
    if (f) {
        isPopOverOpen.value = true
    }
})

getStudentUsers().then(async (response) => {
    return await response.json();
}).then((result) => {
    userCollection.value = result._embedded.studentResponseList.filter((student: StudentType) => {
        return student.userId && student.userId != user.user.id;
    }).map((student: StudentType) => {
        return <ChatUserType>{
            id: student.userId,
            label: student.name,
        }
    });
});

const emitUpdateModelValue = (val: AcceptableValue) => {
    selectedUsers.value = userCollection.value.filter(option => {
        return Array.isArray(val) && val.includes(option.id);
    });

    emit("update:model-value", val);
    emit("update:chat-name", selectedUsers.value.map(i => i.label).join(", "))
}

const removeItem = (userId: number) => {
    selectedUserIds.value = selectedUserIds.value.filter(id => id !== userId);
    selectedUsers.value = selectedUsers.value.filter(user => user.id !== userId);

    emit("update:model-value", selectedUserIds.value);
}
</script>

<template>
    <Popover v-model:open="isPopOverOpen">
        <ListboxRoot 
            class="w-full" 
            v-model="selectedUserIds" 
            highlight-on-hover 
            multiple
            @update:model-value="emitUpdateModelValue"
        >
            <PopoverAnchor class="inline-flex w-full">
                <TagsInput v-slot="{ modelValue: tags }" class="w-full">
                    <TagsInputItem v-for="item in selectedUsers" :key="`tag-input-item-${item.id}`" :value="item.label">
                        <TagsInputItemText />
                        <!-- <TagsInputItemDelete @click.prevent="removeItem(item.id)"/> -->
                    </TagsInputItem>

                    <ListboxFilter v-model="searchUser" as-child>
                        <TagsInputInput 
                            placeholder="Users..." 
                            @keydown.enter.prevent
                            @keydown.down="isPopOverOpen = true" 
                        />
                    </ListboxFilter>

                    <PopoverTrigger as-child>
                        <Button size="icon-sm" variant="ghost" class="order-last self-start ml-auto">
                            <ChevronDown class="size-3.5" />
                        </Button>
                    </PopoverTrigger>
                </TagsInput>
            </PopoverAnchor>

            <PopoverContent class="p-1" @open-auto-focus.prevent>
                <ListboxContent
                    class="max-h-[300px] scroll-py-1 overflow-x-hidden overflow-y-auto empty:after:content-['No_options'] empty:p-1 empty:after:block empty:after:text-sm"
                    tabindex="0">
                    <ListboxItem 
                        v-for="item in filteredUsers" 
                        :key="`list-box-item-${item.id}`"
                        class="data-highlighted:bg-accent data-highlighted:text-accent-foreground [&_svg:not([class*=\'text-\'])]:text-muted-foreground relative flex cursor-default items-center gap-2 rounded-sm px-2 py-1.5 text-sm outline-hidden select-none data-disabled:pointer-events-none data-disabled:opacity-50 [&_svg]:pointer-events-none [&_svg]:shrink-0 [&_svg:not([class*=\'size-\'])]:size-4"
                        :value="item.id" 
                        @select="() => {
                            searchUser = ''
                        }"
                    >
                        <span>{{ item.label }}</span>
                        <ListboxItemIndicator class="ml-auto inline-flex items-center justify-center">
                            <CheckIcon />
                        </ListboxItemIndicator>
                    </ListboxItem>
                </ListboxContent>
            </PopoverContent>
        </ListboxRoot>
    </Popover>
</template>
