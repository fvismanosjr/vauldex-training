import { Client } from '@stomp/stompjs';
import { useUserStore } from '@/stores/user';
import type { IMessage, StompSubscription } from '@stomp/stompjs';

// sidebar
export interface ChatType {
    id: number,
    name: string,
}

// for new chat
export interface ChatFormType {
    id: number,
    name?: string,
    members?: number[],
    message: ChatFormMessageType,
}

export interface ChatFormMessageType {
    content: string,
    type: number,
}
// end for new chat

export interface ChatMessageType {
    id: number,
    body: string,
    user: string,
    messageType: string,
    messageStatus: string,
    createdAt: string,
}

export interface ChatMessage {
    user: number,
    message: ChatFormMessageType,
    status: number,
}

type MessageCallback = (msg: ChatMessageType) => void;

class ChatSocketService {
    private client: Client | null = null;
    private connected = false;
    private subscriptions: Map<string, StompSubscription> = new Map();

    connect(onConnect?: () => void): void {
        if (this.connected) return;

        this.client = new Client({
            brokerURL: "ws://localhost:8080/ws-chat", // Your backend
            reconnectDelay: 0, // set >0 to auto-reconnect
            debug: (msg: string) => console.log("[STOMP]", msg),
        });

        this.client.onConnect = () => {
            this.connected = true;
            console.log("✅ STOMP connected");

            if (onConnect) onConnect();
        };

        this.client.onStompError = (frame) => {
            console.error("❌ STOMP Frame Error:", frame.headers['message']);
            console.error("Details:", frame.body);
        };

        this.client.onWebSocketClose = () => {
            this.connected = false;
            console.log("🔌 WebSocket closed");
        };

        this.client.activate(); // ⬅️ Open WebSocket
    }

    subscribe(channel: string, callback: MessageCallback): void {
        if (!this.client || !this.connected) {
            console.warn("Trying to subscribe before connection.");
            return;
        }

        if (this.subscriptions.has(channel)) {
            console.warn(`Already subscribed to ${channel}`);
            return;
        }

        const destination = `/topic/channel.${channel}`;

        const sub = this.client.subscribe(
            destination,
            (message: IMessage) => {
                try {
                    const data = JSON.parse(message.body) as ChatMessageType;
                    callback(data);
                } catch (e) {
                    console.error("Failed to parse message", e);
                }
            }
        );

        this.subscriptions.set(channel, sub);

        console.log(`📡 Subscribed to ${destination}`);
    }

    sendMessage(channel: string | number, msg: ChatMessage): void {
        if (!this.client || !this.connected) {
            console.warn("Cannot send, STOMP not connected");
            return;
        }

        this.client.publish({
            destination: `/app/chat/${channel}`,
            body: JSON.stringify(msg),
            headers: { 'content-type': 'application/json' }
        });
    }

    unsubscribe(channel: string | number): void {
        const sub = this.subscriptions.get(channel.toString());
        if (!sub) return;

        sub.unsubscribe();
        this.subscriptions.delete(channel.toString());

        console.log(`🗑️ Unsubscribed from channel ${channel}`);
    }

    disconnect(): void {
        if (!this.client || !this.connected) return;

        this.client.deactivate();
        this.connected = false;
        this.subscriptions.clear();

        console.log("🔌 STOMP disconnected");
    }
}

export default new ChatSocketService();

const CHAT_API_URL = "http://localhost:8080/chats";

export const getChats = async () => {
    const user = useUserStore();

    return await fetch(`${CHAT_API_URL}/users/${user.user.id}`, {
        method: "GET",
        credentials: "include",
    });
}

export const findChat = async (id: number) => {
    return await fetch(`${CHAT_API_URL}/${id}`, {
        method: "GET",
        credentials: "include",
    });
}

export const addChat = async (chat: object) => {
    return await fetch(CHAT_API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(chat),
        credentials: "include",
    });
}
