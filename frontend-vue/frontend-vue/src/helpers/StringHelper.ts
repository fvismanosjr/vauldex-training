import { useUserStore } from "@/stores/user";

export const ucwords = (str: string) => {
    return String(str)
            .toLowerCase()
            .replace(/\b[a-z]/g, (letter) => letter.toUpperCase());
}

export const initials = (str: string, min: number = 3) => {
    return String(str)
            .trim()
            .split(/\s+/)
            .filter(word => word.length > min && /^[A-Za-z]/.test(word))
            .map(w => w[0]?.toUpperCase())
            .join("");
}

export const chatName = (name?: string) => {
    const user = useUserStore();
    return name ? name
            .split(", ")
            .filter(item => item !== user.user.name)
            .join(",") : "";
}