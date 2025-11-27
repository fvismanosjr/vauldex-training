<script lang="ts" setup>
import {
    Card,
    CardContent,
    CardDescription,
    CardHeader,
    CardTitle,
} from "@/components/ui/card"

import {
    Popover,
    PopoverContent,
    PopoverTrigger,
} from '@/components/ui/popover'

import { Button } from "@/components/ui/button"
import { Calendar } from '@/components/ui/calendar'
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { Atom, CalendarIcon } from "lucide-vue-next"
import { cn } from "@/lib/utils"
import { register } from "@/services/auth";
import type { RegisterAuthType } from "@/types/RegisterAuthType";
import type { DateValue } from '@internationalized/date'
import { DateFormatter, getLocalTimeZone, parseDate, today } from '@internationalized/date'
import { ref, watch, type Ref } from 'vue';
import { useRouter } from 'vue-router';

const router = useRouter();
const dateFormatter = new DateFormatter("en-US", {
    dateStyle: 'long',
});

const auth = ref<RegisterAuthType>({
    name: "",
    email: "",
    birthDate: today(getLocalTimeZone()),
    password: "",
    confirmPassword: "",
});

const registerUser = async () => {
    const response = await register({
        ...auth.value,
        birthDate: auth.value.birthDate.toString()
    });

    if (response.ok) {
        router.push({
            name: "login",
        });
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
                        Create an account
                    </CardTitle>
                    <CardDescription>
                        Enter your information below to create your account
                    </CardDescription>
                </CardHeader>
                <CardContent>
                    <form>
                        <div class="grid gap-6">
                            <div class="grid gap-6">
                                <div class="grid gap-2">
                                    <Label html-for="fullname">Fullname</Label>
                                    <Input v-model="auth.name" id="fullname" type="text" placeholder="Jane Doe"
                                        required />
                                </div>
                                <div class="grid gap-2">
                                    <Label html-for="email">Email</Label>
                                    <Input v-model="auth.email" id="email" type="email" placeholder="m@example.com"
                                        required />
                                </div>
                                <div class="grid gap-2">
                                    <Label html-for="birthdate">Birthdate</Label>
                                    <div>
                                        <Popover v-slot="{ close }">
                                            <PopoverTrigger as-child>
                                                <Button variant="outline"
                                                    :class="cn('size-full justify-start text-left font-normal', !auth.birthDate && 'text-muted-foreground')">
                                                    <CalendarIcon />
                                                    {{ auth.birthDate ? dateFormatter.format(auth.birthDate.toDate(getLocalTimeZone())) : "Pick a date" }}
                                                </Button>
                                            </PopoverTrigger>
                                            <PopoverContent class="w-auto p-0" align="start">
                                                <Calendar v-model="auth.birthDate as DateValue" layout="month-and-year" initial-focus
                                                    @update:model-value="close" />
                                            </PopoverContent>
                                        </Popover>
                                    </div>
                                </div>
                                <div class="grid gap-2">
                                    <Label html-for="password">Password</Label>
                                    <Input v-model="auth.password" id="password" type="password" placeholder="********"
                                        required />
                                </div>
                                <div class="grid gap-2">
                                    <Label html-for="confirmPassword">Confirm Password</Label>
                                    <Input v-model="auth.confirmPassword" id="confirmPassword" type="password"
                                        placeholder="********" required />
                                </div>
                                <Button type="submit" class="w-full" @click.prevent="registerUser">
                                    Create Account
                                </Button>
                            </div>
                            <div class="text-center text-sm">
                                Already have an account?
                                <a href="/login" class="underline underline-offset-4">
                                    Sign in
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