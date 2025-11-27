<script lang="ts" setup>
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"

import {
    Alert,
    AlertDescription,
    AlertTitle,
} from '@/components/ui/alert'

import { Atom, Bug, User } from "lucide-vue-next"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { login } from "@/services/auth";
import { useUserStore } from "@/stores/user";
import type { UserType } from "@/types/UserType";
import type { LoginAuthType } from "@/types/LoginAuthType";
import { ref } from 'vue';
import { useRouter } from 'vue-router';

const user = useUserStore();
const router = useRouter();
const auth = ref<LoginAuthType>({
    username: "",
    password: "",
});

const hasError = ref(false);

const logInUser = async () => {
    const response = await login(auth.value)

    if (response.ok) {
        const result = await response.json();
        
        const payload: UserType = {
            id: result.id,
            name: result.name,
            email: result.email,
            avatar: "",
            role: result.role,
        };

        user.save(payload);

        switch (result.role) {
            case "ROLE_STUDENT":
                router.push({
                    name: "student-profile",
                    params: {
                        id: result.id
                    }
                });
                break;
            default:
                router.push({
                    name: "dashboard",
                });
                break;
        }
    } else {
        hasError.value = true;
    }
}
</script>

<template>
    <div class="flex w-full max-w-sm flex-col gap-6">
        <a href="#" class="flex items-center gap-2 self-center font-medium">
            <div class="flex h-6 w-6 items-center justify-center rounded-md bg-primary text-primary-foreground">
                <Atom class="size-4" />
            </div>
            No Brand
        </a>
        <div class="flex flex-col gap-6">
            <Card>
                <CardHeader class="text-center">
                    <CardTitle class="text-xl">
                        Welcome back
                    </CardTitle>
                    <CardDescription>
                        Enter your email below to login to your account
                    </CardDescription>
                    <Alert v-if="hasError" class="mt-3" variant="destructive">
                        <Bug />
                        <AlertTitle>That didn’t match our records. Try again</AlertTitle>
                    </Alert>
                </CardHeader>
                <CardContent>
                    <form>
                        <div class="grid gap-6">
                            <div class="grid gap-6">
                                <div class="grid gap-2">
                                    <Label html-for="email">Email</Label>
                                    <Input v-model="auth.username" id="email" type="email" placeholder="m@example.com"
                                        required />
                                </div>
                                <div class="grid gap-2">
                                    <div class="flex items-center">
                                        <Label html-for="password">Password</Label>
                                        <a href="/forgot-password"
                                            class="ml-auto text-sm underline-offset-4 hover:underline" tabindex="-1">
                                            Forgot your password?
                                        </a>
                                    </div>
                                    <Input v-model="auth.password" id="password" type="password" required />
                                </div>
                                <Button type="submit" class="w-full" @click.prevent="logInUser">
                                    Login
                                </Button>
                            </div>
                            <div class="text-center text-sm">
                                Don't have an account?
                                <a href="/register" class="underline underline-offset-4">
                                    Sign up
                                </a>
                            </div>
                        </div>
                    </form>
                </CardContent>
            </Card>
            <div
                class="text-balance text-center text-xs text-muted-foreground [&_a]:underline [&_a]:underline-offset-4 [&_a]:hover:text-primary">
                By clicking continue, you agree to our <a href="#">Terms of Service</a>
                and <a href="#">Privacy Policy</a>.
            </div>
        </div>
    </div>
</template>