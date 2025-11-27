<script setup lang="ts">
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"

import {
  Popover,
  PopoverContent,
  PopoverTrigger,
} from '@/components/ui/popover'

import { Calendar } from '@/components/ui/calendar'
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { UserRound, CalendarIcon } from "lucide-vue-next"
import { cn } from "@/lib/utils"
import { ucwords } from "@/helpers/StringHelper"
import DegreeSelect from "@/components/DegreeSelect.vue"
import type { DateValue } from '@internationalized/date'
import type { StudentFormType } from "@/types/StudentFormType"
import { DateFormatter, getLocalTimeZone, parseDate, today } from '@internationalized/date'
import { findStudent, addStudent, updateStudent, deleteStudent } from "@/services/student"
import { computed, ref, watch, type Ref } from "vue"

const studentFormProps = defineProps({
    id: {
        type: Number,
        default: 0,
        required: false,
    },
    action: {
        type: String,
        default: "add",
        validator(value: string) {
            return ["add", "edit", "delete"].includes(value);
        }
    },
    isOpen: {
        type: Boolean,
        default: false,
    }
});

const emit = defineEmits<{
    (e: "update:open", value: boolean): void,
    (e: "reload-table", value: boolean): void,
}>();

const studentForm = ref<StudentFormType>({
    name: "",
    email: "",
    birthdate: today(getLocalTimeZone()),
    degree: "",
});

const dateFormatter = new DateFormatter("en-US", {
    dateStyle: 'long',
});

const clearForm = () => {
    studentForm.value = {
        name: "",
        email: "",
        birthdate: today(getLocalTimeZone()),
        degree: "",
    }
}

const title = computed(() => {
    return ucwords(studentFormProps.action.trim());
});

const isInputDisabled = computed(() => {
    return studentFormProps.action.trim() == 'delete';
});

const submitButtonVariant = computed(() => {
    return studentFormProps.action.trim() == 'delete' ? "destructive" : "default";
});

if (studentFormProps.id) {
    findStudent(studentFormProps.id).then(async (response) => {
        return await response.json();
    }).then((result) => {
        if (result.id) {
            studentForm.value.name = result.name;
            studentForm.value.email = result.email;
            studentForm.value.birthdate = parseDate(result.birthdate);
            studentForm.value.degree = result.degree;
        }
    });
} else {
    clearForm();
}

const submitStudentForm = async () => {
    switch (studentFormProps.action) {
        case "add":
            await addStudent({
                ...studentForm.value,
                birthdate: studentForm.value.birthdate.toString()
            });
            break;
    
        case "edit":
            await updateStudent({
                ...studentForm.value,
                birthdate: studentForm.value.birthdate.toString()
            }, studentFormProps.id);
            break;

        case "delete":
            await deleteStudent(studentFormProps.id);
            break;
    }

    emit("reload-table", true);
    emit("update:open", false);
    clearForm();
}
</script>

<template>
    <Dialog :open="isOpen" @update:open="(val) => emit('update:open', val)">
        <DialogContent class="sm:max-w-[425px]">
            <DialogHeader>
                <DialogTitle>{{ title }} Student</DialogTitle>
                <DialogDescription>
                    Neque porro quisquam est qui dolorem ipsum quia dolor sit amet, consectetur, adipisci velit
                </DialogDescription>
            </DialogHeader>
            <div class="grid gap-4 py-4">
                <div class="grid grid-cols-4 items-center gap-4">
                    <Label for="name" class="text-right">
                        Name
                    </Label>
                    <Input 
                        type="text" 
                        id="name" 
                        placeholder="John Doe" 
                        class="col-span-3" 
                        v-model="studentForm.name"
                        :disabled="isInputDisabled" 
                    />
                </div>
                <div class="grid grid-cols-4 items-center gap-4">
                    <Label for="email" class="text-right">
                        Email
                    </Label>
                    <Input 
                        type="email" 
                        id="email" 
                        placeholder="johndoe@company.com"
                        class="col-span-3" 
                        v-model="studentForm.email"
                        :disabled="isInputDisabled" 
                    />
                </div>
                <div class="grid grid-cols-4 items-center gap-4">
                    <Label>Birthdate</Label>
                    <div class="col-span-3">
                        <Popover v-slot="{ close }">
                            <PopoverTrigger as-child>
                            <Button 
                                variant="outline" 
                                :class="cn('size-full justify-start text-left font-normal', !studentForm.birthdate && 'text-muted-foreground')"
                                :disabled="isInputDisabled" 
                            >
                                <CalendarIcon />
                                {{ studentForm.birthdate ? dateFormatter.format(studentForm.birthdate.toDate(getLocalTimeZone())) : "Pick a date" }}
                            </Button>
                            </PopoverTrigger>
                            <PopoverContent class="w-auto p-0" align="start">
                            <Calendar
                                v-model="studentForm.birthdate as DateValue"
                                layout="month-and-year"
                                initial-focus
                                :disabled="isInputDisabled" 
                                @update:model-value="close"
                            />
                            </PopoverContent>
                        </Popover>
                    </div>
                </div>
                <div class="grid grid-cols-4 items-center gap-4">
                    <Label for="degree" class="text-right">
                        Degree
                    </Label>
                    <div class="col-span-3">
                        <DegreeSelect v-model:model-value="studentForm.degree" :disabled="isInputDisabled" />
                    </div>
                </div>
            </div>
            <DialogFooter>
                <Button :variant="submitButtonVariant" type="button" @click.prevent="submitStudentForm">
                    <UserRound />
                    {{ title }} Student
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>