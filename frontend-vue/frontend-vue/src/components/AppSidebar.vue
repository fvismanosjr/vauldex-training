<script setup lang="ts">
import { 
    Atom,
    LayoutDashboard, 
    SquareLibrary, 
    GraduationCap,
    UserRound,
    MessageSquareText,
    BotMessageSquare,
} from "lucide-vue-next";

import type { SidebarProps } from "@/components/ui/sidebar";

import {
    Sidebar,
    SidebarContent,
    SidebarGroup,
    SidebarHeader,
    SidebarMenu,
    SidebarMenuButton,
    SidebarMenuItem,
    SidebarFooter,
    SidebarRail,
} from "@/components/ui/sidebar";

import { RouterLink, useRoute } from "vue-router";
import UserNav from "@/components/UserNav.vue";
import type { NavType } from "@/types/NavType";
import { useUserStore } from "@/stores/user";
import { ref } from "vue";

const props = defineProps<SidebarProps>();
const route = useRoute();
const user = useUserStore();
const navs = ref<NavType[]>([]);

switch (user.user.role) {
    case "ROLE_SUDO":
        navs.value = [
            {
                title: "Dashboard",
                to: { name: "dashboard" },
                icon: LayoutDashboard,
                group: [],
            },
            {
                title: "Student",
                to: { name: "students" },
                icon: SquareLibrary,
                group: ["students", "student-profile"],
            },
            {
                title: "Degree",
                to: { name: "degrees" },
                icon: GraduationCap,
                group: ["degrees", "degree-profile"],
            },
        ]
        break

    case "ROLE_ADMIN":
        navs.value = [
            {
                title: "Dashboard",
                to: { name: "dashboard" },
                icon: LayoutDashboard,
                group: [],
            },
            {
                title: "Student",
                to: { name: "students" },
                icon: SquareLibrary,
                group: ["students", "student-profile"],
            },
            {
                title: "Degree",
                to: { name: "degrees" },
                icon: GraduationCap,
                group: ["degrees", "degree-profile"],
            },
        ]
        break;

    case "ROLE_STUDENT":
        navs.value = [
            {
                title: "Student",
                to: { name: "student-profile", params: { id: user.user.id }},
                icon: SquareLibrary,
                group: ["student-profile"],
            },
        ]
        break

    default:
        navs.value = []
        break;
}

navs.value.push({
    title: "Chat",
    to: { name: "chats" },
    icon: BotMessageSquare,
    group: ["chat"]
});

const isItemActiveSidebar = (routeNames: string[]) => {
    return routeNames.includes(route.name as string)
}
</script>

<template>
    <Sidebar v-bind="props">
        <SidebarHeader>
            <SidebarMenu>
                <SidebarMenuItem>
                    <SidebarMenuButton size="lg" as-child>
                        <a href="#">
                            <div class="flex aspect-square size-8 items-center justify-center rounded-lg bg-sidebar-primary text-sidebar-primary-foreground">
                                <Atom class="size-4" />
                            </div>
                            <div class="flex flex-col gap-0.5 leading-none">
                                <span class="font-semibold">NoBrand</span>
                                <span class="">v1.0.0</span>
                            </div>
                        </a>
                    </SidebarMenuButton>
                </SidebarMenuItem>
            </SidebarMenu>
        </SidebarHeader>
        <SidebarContent>
            <SidebarGroup>
                <SidebarMenu>
                    <SidebarMenuItem v-for="item in navs" :key="item.title">
                        <SidebarMenuButton as-child>
                            <RouterLink 
                                :to="item.to"
                                :class="{ 'router-link-active': isItemActiveSidebar(item.group) }"
                            >
                                <component :is="item.icon" />
                                <span>{{ item.title }}</span>
                            </RouterLink>
                        </SidebarMenuButton>
                    </SidebarMenuItem>
                </SidebarMenu>
            </SidebarGroup>
        </SidebarContent>
        <SidebarFooter>
            <UserNav />
        </SidebarFooter>
        <SidebarRail />
    </Sidebar>
</template>

<style scoped>
.router-link-active {
    font-weight: 600;
}
</style>
