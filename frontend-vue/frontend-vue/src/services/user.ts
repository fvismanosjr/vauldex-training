const USER_API_URL = "http://localhost:8080/users";

export const getUsers = async (
    sortBy: string = "name",
    sortOrder: string = "asc",
    size: number = 5, 
    page: number = 0,
) => {
    const filter = `?size=${size}&page=${page}&sort=${sortBy},${sortOrder}&filter=username<>superadmin`;

    return await fetch(`${USER_API_URL}${filter}`, {
        method: "GET",
        credentials: "include",
    });
}