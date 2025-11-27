import type { DateValue } from '@internationalized/date'

export interface RegisterAuthType {
    name: string,
    email: string,
    birthDate: DateValue,
    password: string,
    confirmPassword: string,
}