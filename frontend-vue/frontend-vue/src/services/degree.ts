const DEGREE_API_URL = "http://localhost:8080/degrees";

export const getDegrees = async (
    search: string = "",
    sortBy: string = "name",
    sortOrder: string = "asc",
    size: number = 5, 
    page: number = 0,
) => {
    let filter = `?size=${size}&page=${page}&sort=${sortBy},${sortOrder}`;

    if (search.length) {
        filter += `&filter=name like ${search.toLowerCase()}`;
    }

    return await fetch(`${DEGREE_API_URL}${filter}`, {
        method: "GET",
        credentials: "include",
    });
}

export const findDegree = async (id: number) => {
    return await fetch(`${DEGREE_API_URL}/${id}`, {
        method: "GET",
        credentials: "include",
    });
}

export const findDegreeWithStudents = async (id: number) => {
    return await fetch(`${DEGREE_API_URL}/${id}/students`, {
        method: "GET",
        credentials: "include",
    });
}

export const addDegree = async (student: object) => {
    return await fetch(DEGREE_API_URL, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(student),
        credentials: "include",
    });
}

export const updateDegree = async (student: object, id: number) => {
    return await fetch(`${DEGREE_API_URL}/${id}`, {
        method: "PUT",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(student),
        credentials: "include",
    });
}

export const deleteDegree = async (id: number) => {
    return await fetch(`${DEGREE_API_URL}/${id}`, {
        method: "DELETE",
        credentials: "include",
    });
}