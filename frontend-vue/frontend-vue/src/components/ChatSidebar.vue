<script setup lang="ts">
import {
    Card,
    CardContent,
    CardDescription,
    CardFooter,
    CardHeader,
    CardTitle,
} from '@/components/ui/card'

import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

import {
    Item,
    ItemActions,
    ItemContent,
    ItemDescription,
    ItemMedia,
    ItemTitle,
    ItemSeparator,
} from '@/components/ui/item'

import {
    InputGroup,
    InputGroupAddon,
    InputGroupButton,
    InputGroupInput
} from "@/components/ui/input-group"

import {
    Empty,
    EmptyContent,
    EmptyDescription,
    EmptyHeader,
    EmptyMedia,
    EmptyTitle,
} from '@/components/ui/empty'

import {
    Plus,
    UserRoundSearch,
    FolderOpen,
    BotMessageSquare,
} from 'lucide-vue-next';

import { Button } from '@/components/ui/button'
import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import { ScrollArea } from '@/components/ui/scroll-area'
import { initials, chatName } from '@/helpers/StringHelper'
import { getChats } from '@/services/chat'
import type { ChatType } from '@/services/chat'
import { useUserStore } from '@/stores/user'
import { ref } from 'vue'

const emit = defineEmits<{
    (e: "select:chat", value: number): void
}>();

const user = useUserStore();
const chats = ref<ChatType[]>([]);

getChats().then(async (response) => {
    return await response.json();
}).then((result) => {
    chats.value = result;
});

const selectChat = (id: number) => {
    emit("select:chat", id);
}
</script>

<template>
    <Card class="pb-0 col-span-2">
        <CardHeader class="gap-4">
            <div class="flex flex-wrap items-center justify-between">
                <div>
                    <CardTitle class="text-xl font-semibold">Chat</CardTitle>
                    <CardDescription>You can message me here</CardDescription>
                </div>
                <div>
                    <DropdownMenu>
                        <DropdownMenuTrigger class="p-2 rounded-full border border-gray-500/50 cursor-pointer">
                            <Plus :size="20"></Plus>
                        </DropdownMenuTrigger>
                        <DropdownMenuContent align="end">
                            <DropdownMenuItem @click="selectChat(0)">New Chat</DropdownMenuItem>
                            <DropdownMenuItem @click="selectChat(0)">Create Group</DropdownMenuItem>
                        </DropdownMenuContent>
                    </DropdownMenu>
                </div>
            </div>
            <InputGroup>
                <InputGroupInput placeholder="Type to search..." />
                <InputGroupAddon>
                    <UserRoundSearch />
                </InputGroupAddon>
                <InputGroupAddon align="inline-end">
                    <InputGroupButton variant="secondary" type="submit">
                        Search
                    </InputGroupButton>
                </InputGroupAddon>
            </InputGroup>
        </CardHeader>
        <CardContent class="px-0">
            <ScrollArea class="h-[calc(100vh-260px)]">
                <Empty v-if="!chats.length">
                    <EmptyHeader>
                        <EmptyMedia variant="icon">
                            <BotMessageSquare />
                        </EmptyMedia>
                        <EmptyTitle>No Messages Yet</EmptyTitle>
                        <EmptyDescription>
                            You haven't created any conversations yet. Get started by creating your first conversation.
                        </EmptyDescription>
                    </EmptyHeader>
                    <EmptyContent>
                        <div class="flex gap-2">
                            <Button>New Chat</Button>
                            <Button variant="outline">
                                Create Group
                            </Button>
                        </div>
                    </EmptyContent>
                </Empty>
                <template v-else>
                    <template v-for="(chat, index) in chats" :key="`chat-${chat.id}`">
                        <Item 
                            class="rounded-none border-none align-middle hover:bg-muted cursor-pointer" 
                            @click.prevent="selectChat(chat.id)"
                        >
                            <ItemMedia class="bg-transparent border-none self-center size-7" variant="icon">
                                <Avatar class="size-7">
                                    <AvatarFallback class="text-xs">{{ initials(chatName(chat.name)) }}</AvatarFallback>
                                </Avatar>
                            </ItemMedia>
                            <ItemContent class="gap-0 min-w-0">
                                <ItemTitle>
                                    <span class="truncate text-sm font-medium">{{ chatName(chat.name) }}</span>
                                    <!-- <span class="text-muted-foreground text-xs">
                                        Yesterday
                                    </span> -->
                                </ItemTitle>
                                <div class="flex items-center gap-2">
                                    <!-- <span class="text-muted-foreground truncate text-start text-sm">
                                        last message should be here
                                    </span> -->
                                </div>
                            </ItemContent>
                        </Item>
                        <ItemSeparator v-if="index < (chats.length - 1)" />
                    </template>
                </template>
            </ScrollArea>
        </CardContent>
    </Card>
</template>