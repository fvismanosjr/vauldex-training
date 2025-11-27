import type { LucideProps } from "lucide-vue-next";
import type { FunctionalComponent } from "vue";

export interface NavType {
    title: string,
    to: object,
    icon: FunctionalComponent<LucideProps>,
    group: string[],
}