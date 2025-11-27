<script setup lang="ts">

import ChatSidebar from '@/components/ChatSidebar.vue'
import ChatMessenger from '@/components/ChatMessenger.vue';
import ChatSocketService from '@/services/chat.ts'
import { onMounted, ref } from 'vue';

const chatMessengerKey = ref(0);
const chatSidebarKey = ref(0);
let chatId = ref();

const selectChat = (selectedChatId: number) => {
    chatMessengerKey.value++;
    chatId.value = selectedChatId;
}

const reloadChat = (newChatId: number) => {
    chatSidebarKey.value++;
    selectChat(newChatId);
}
</script>

<template>
    <div class="grid grid-cols-1 md:grid-cols-6 gap-4">
        <ChatSidebar 
            @select:chat="selectChat"
            :key="chatSidebarKey"
        ></ChatSidebar>
        <div class="col-span-4">
            <ChatMessenger 
                :chatId="chatId" 
                :key="chatMessengerKey"
                @new:chat="reloadChat"
            ></ChatMessenger>
        </div>
    </div>
</template>