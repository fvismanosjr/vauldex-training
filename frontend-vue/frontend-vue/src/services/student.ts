import { useUserStore } from "@/stores/user";
const STUDENT_API_URL = "http://localhost:8080/students";

export const getStudents = async (
    search: string = "",
    sortBy: string = "name",
    sortOrder: string = "asc",
    size: number = 5, 
    page: number = 0,
) => {
    let filter = `?size=${size}&page=${page}&sort=${sortBy},${sortOrder}`;

    if (search.length) {
        filter += `&filter=name like ${search.toLowerCase()} or email like ${search.toLowerCase()}`;
    }

    return await fetch(`${STUDENT_API_URL}${filter}`, {
        method: "GET",
        credentials: "include",
    });
}

export const getStudentUsers = async () => {
    const user = useUserStore();
    return await fetch(`${STUDENT_API_URL}?size=-1&sort=name,asc&filter=email<>${user.user.email}`, {
        method: "GET",
        credentials: "include",
    });
}

export const findStudent = async (id: number) => {
    return await fetch(`${STUDENT_API_URL}/${id}`, {
        method: "GET",
        credentials: "include",
    });
}

export const addStudent = async (student: object) => {
    return await fetch(STUDENT_API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(student),
        credentials: "include",
    });
}

export const updateStudent = async (student: object, id: number) => {
    return await fetch(`${STUDENT_API_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(student),
        credentials: "include",
    });
}

export const deleteStudent = async (id: number) => {
    return await fetch(`${STUDENT_API_URL}/${id}`, {
        method: "DELETE",
        credentials: "include",
    });
}

