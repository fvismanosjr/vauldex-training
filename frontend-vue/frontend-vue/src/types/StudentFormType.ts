import type { DateValue } from '@internationalized/date'

export interface StudentFormType {
    name: string,
    email: string,
    birthdate: DateValue,
    degree: string,
}