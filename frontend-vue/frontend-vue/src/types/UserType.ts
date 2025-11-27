import type { RoleType } from "./RoleType";

export interface UserType {
    id: number,
    name: string,
    email: string,
    avatar: string,
    role: RoleType,
}