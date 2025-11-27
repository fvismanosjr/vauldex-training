<script setup lang="ts">
import {
    Dialog,
    DialogContent,
    DialogDescription,
    DialogFooter,
    DialogHeader,
    DialogTitle,
} from "@/components/ui/dialog"
import { Button } from "@/components/ui/button"
import { Input } from "@/components/ui/input"
import { Label } from "@/components/ui/label"
import { UserRound } from "lucide-vue-next"
import { ucwords } from "@/helpers/StringHelper"
import { findDegree, addDegree, updateDegree, deleteDegree } from "@/services/degree"
import type { DegreeFormType } from "@/types/DegreeFormType"
import { computed, ref, watch } from "vue"
import { initials } from "@/helpers/StringHelper"

const degreeForm = ref<DegreeFormType>({
    name: "",
    abbreviation: "",
});

const degreeFormProps = defineProps({
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

const title = computed(() => {
    return ucwords(degreeFormProps.action.trim());
});

const isInputDisabled = computed(() => {
    return degreeFormProps.action.trim() == 'delete';
});

const submitButtonVariant = computed(() => {
    return degreeFormProps.action.trim() == 'delete' ? "destructive" : "default";
});

const clearForm = () => {
    degreeForm.value = {
        name: "",
        abbreviation: "",
    }
}

if (degreeFormProps.id) {
    findDegree(degreeFormProps.id).then(async (response) => {
        return await response.json();
    }).then((result) => {
        if (result.id) {
            degreeForm.value.name = result.name;
            degreeForm.value.abbreviation = result.abbreviation;
        }
    })
} else {
    clearForm();
}

const submitDegreeForm = async () => {
    switch (degreeFormProps.action) {
        case "add":
            await addDegree(degreeForm.value);
            break;
    
        case "edit":
            await updateDegree(degreeForm.value, degreeFormProps.id);
            break;

        case "delete":
            await deleteDegree(degreeFormProps.id);
            break;
    }

    emit("reload-table", true);
    emit("update:open", false);
    clearForm();
}

watch(() => degreeForm.value.name, (newName) => {
    if (degreeFormProps.action == "add") {
        degreeForm.value.abbreviation = initials(newName);
    }
});
</script>

<template>
    <Dialog :open="isOpen" @update:open="(val) => emit('update:open', val)">
        <DialogContent class="sm:max-w-[425px]">
            <DialogHeader>
                <DialogTitle>{{ title }} Degree</DialogTitle>
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
                        v-model="degreeForm.name"
                        :disabled="isInputDisabled" 
                    />
                </div>
                <div class="grid grid-cols-4 items-center gap-4">
                    <Label for="abbr" class="text-right">
                        Abbreviation
                    </Label>
                    <Input 
                        type="text" 
                        id="abbr" 
                        placeholder="YOLO"
                        class="col-span-3" 
                        v-model="degreeForm.abbreviation"
                        :disabled="isInputDisabled" 
                    />
                </div>
            </div>
            <DialogFooter>
                <Button :variant="submitButtonVariant" type="button" @click.prevent="submitDegreeForm">
                    <UserRound />
                    {{ title }} Degree
                </Button>
            </DialogFooter>
        </DialogContent>
    </Dialog>
</template>