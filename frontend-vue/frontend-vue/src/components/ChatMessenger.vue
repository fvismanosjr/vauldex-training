<script setup lang="ts">
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
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuLabel,
    DropdownMenuSeparator,
    DropdownMenuTrigger,
} from '@/components/ui/dropdown-menu'

import {
    Empty,
    EmptyContent,
    EmptyDescription,
    EmptyHeader,
    EmptyMedia,
    EmptyTitle,
} from '@/components/ui/empty'

import { ScrollArea } from '@/components/ui/scroll-area'

import {
    Paperclip,
    Ellipsis,
    Brain,
} from 'lucide-vue-next';

import { Avatar, AvatarFallback, AvatarImage } from '@/components/ui/avatar'
import ChatMessengerUser from '@/components/ChatMessengerUser.vue'
import type { AcceptableInputValue, AcceptableValue } from 'reka-ui'
import type { ChatUserType } from '@/types/ChatUserType'
import ChatSocketService, { addChat, findChat, type ChatType, type ChatMessageType, type ChatFormType, type ChatFormMessageType } from '@/services/chat.ts'
import { chatName, initials } from '@/helpers/StringHelper'
import { formatDateToLong } from '@/helpers/DateHelper'
import { useUserStore } from '@/stores/user'
import { onMounted, onUnmounted, ref } from 'vue'

const chatProps = defineProps({
    chatId: {
        type: Number,
        default: null,
        required: false,
    }
});

const emit = defineEmits<{
    (e: "new:chat", value: number): void
}>();

const user = useUserStore();

// this is used when creating a new chat
const chatForm = ref<ChatFormType>({
    id: 0,
    name: "",
    members: [],
    message: <ChatFormMessageType>{
        content: "",
        type: 1
    }
});

const messages = ref<ChatMessageType[]>([]);

if (chatProps.chatId > 0) {
    findChat(chatProps.chatId).then(async (response) => {
        return await response.json();
    }).then((result) => {
        chatForm.value.name = result.name;
        messages.value = result.messages;
        // connect here and subscribe
        ChatSocketService.connect(() => {
            ChatSocketService.subscribe(chatProps.chatId.toString(), (msg) => {
                messages.value.push(msg);
            });
        });
    });
}

const selectedUserIds = (value: AcceptableValue) => {
    chatForm.value.members = Array.isArray(value) ? value : [];
}

const submitChatMessage = async () => {
    if (chatProps.chatId == 0) {
        // add current user to the list of members only if this is a new chat
        chatForm.value.members?.push(user.user.id);
        chatForm.value.name += `, ${user.user.name}`;

        await addChat(chatForm.value).then(async (response) => {
            return await response.json();
        }).then((result) => {
            emit("new:chat", result.id);
        });

        return;
    } else {
        ChatSocketService.sendMessage(chatProps.chatId, {
            user: user.user.id,
            message: <ChatFormMessageType>{
                content: chatForm.value.message.content,
                type: 1
            },
            status: 1
        });

        chatForm.value.message = <ChatFormMessageType>{
            content: "",
            type: 1,
        }
    }
}

onUnmounted(() => {
    // disconnect
    ChatSocketService.disconnect();
})
</script>

<template>
    <Empty v-if="chatId === null" class="h-full">
        <EmptyHeader>
            <EmptyMedia variant="icon">
                <Brain/>
            </EmptyMedia>
            <EmptyDescription>
                There is no one who loves pain itself, who seeks after it and wants to have it, simply because it is pain.
            </EmptyDescription>
        </EmptyHeader>
    </Empty>
    <div v-else>
        <div class="flex px-3">
            <ChatMessengerUser 
                v-if="chatId === 0" 
                @update:model-value="selectedUserIds"
                @update:chat-name="(val) => { chatForm.name = val }"
            ></ChatMessengerUser>
            <template v-else>
                <Item class="rounded-none border-none align-middle p-0">
                    <ItemMedia class="bg-transparent border-none self-center size-10" variant="icon">
                        <Avatar class="size-10">
                            <AvatarFallback>
                                {{ initials(chatName(chatForm.name)) }}
                            </AvatarFallback>
                        </Avatar>
                    </ItemMedia>
                    <ItemContent class="gap-0 min-w-0">
                        <ItemTitle>
                            <span class="truncate text-sm font-medium">
                                {{ chatName(chatForm.name) }}
                            </span>
                        </ItemTitle>
                        <ItemDescription>-</ItemDescription>
                    </ItemContent>
                </Item>
            </template>
        </div>
        <ScrollArea class="h-[calc(100vh-240px)] px-3">
            <div class="flex flex-col items-start space-y-6 py-8">
                <template v-if="messages.length > 0">
                    <template v-for="message in messages" :key="`message-${message.id}`">
                        <Item class="rounded-none border-none align-middle p-0">
                            <ItemMedia class="bg-transparent border-none self-center size-10" variant="icon">
                                <Avatar class="size-10">
                                    <AvatarFallback>{{ initials(message.user) }}</AvatarFallback>
                                </Avatar>
                            </ItemMedia>
                            <ItemContent class="gap-0">
                                <ItemTitle class="justify-start gap-2">
                                    <span class="truncate text-sm font-medium">{{ message.user }}</span>
                                    <span class="text-muted-foreground text-xs">
                                        {{ formatDateToLong(message.createdAt) }}
                                    </span>
                                </ItemTitle>
                                <ItemDescription>{{ message.body }}</ItemDescription>
                            </ItemContent>
                        </Item>
                    </template>
                </template>
            </div>
        </ScrollArea>
        <div>
            <InputGroup>
                <textarea 
                    data-slot="input-group-control"
                    class="flex field-sizing-content h-10 w-full resize-none rounded-md bg-transparent px-3 py-2.5 text-base transition-[color,box-shadow] outline-none md:text-sm"
                    placeholder="Type your message here..." 
                    v-model="chatForm.message.content"
                />
                <InputGroupAddon align="block-end">
                    <InputGroupButton variant="outline" class="rounded-full" size="icon-sm">
                        <Paperclip class="size-4" />
                    </InputGroupButton>
                    <InputGroupButton class="ml-auto" size="sm" variant="default" @click.prevent="submitChatMessage">Send</InputGroupButton>
                </InputGroupAddon>
            </InputGroup>
        </div>
    </div>
</template>