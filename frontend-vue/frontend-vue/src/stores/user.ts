// stores/user.js
import { defineStore } from 'pinia'
import { computed, ref } from 'vue'
import { initials } from '@/helpers/StringHelper'
import type { UserType } from '@/types/UserType'

export const useUserStore = defineStore('user', () => {
    const user = ref<UserType>({
        id: 0,
        name: "",
        email: "",
        avatar: "",
        role: "guest"
    });

    const displayName = computed(() => {
        return user.value.name || user.value.email || "Anonymous";
    });

    const displayNameFallback = computed(() => {
        return initials(user.value.name || user.value.email || "Anonymous", 2);
    });

    const isSuper = computed(() => user.value.role == "ROLE_SUDO");
    const isStudent = computed(() => user.value.role == "ROLE_STUDENT");
    const isAdmin = computed(() => user.value.role == "ROLE_ADMIN");

    const save = (payload: Partial<UserType>) => {
        Object.assign(user.value, payload);
    }

    const destroy = () => {
        user.value.name = "";
        user.value.email = "";
        user.value.avatar = "";
        user.value.role = "guest";
    }

    return {
        user,
        displayName,
        displayNameFallback,
        isSuper,
        isStudent,
        isAdmin,
        save,
        destroy
    }
});
