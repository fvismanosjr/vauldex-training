const ROUTE_API_URL = "http://localhost:8080/auth";

export const checkIfAuthenticated = async () => {
    try {
        const response = await fetch(`${ROUTE_API_URL}/me`, {
            method: "GET",
            credentials: "include",
        });

        if (response.status == 403) {
            return false;
        }

        return true;
    } catch (error) {
        
        if (error instanceof TypeError) {
            return "network-error";
        }

        return false;
    }
}

export const login = async (user: object) => {
    return await fetch(`${ROUTE_API_URL}/login`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(user),
    });

    // add pinia storage here to be used in usernav component
}

export const logout = async () => {
    return await fetch(`${ROUTE_API_URL}/logout`, {
        method: "POST",
        credentials: "include",
    });
}

export const register = async (user: object) => {
    return await fetch(`${ROUTE_API_URL}/register`, {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        body: JSON.stringify(user),
    });
}