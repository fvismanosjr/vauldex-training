const STUDENT_API_URL = "http://localhost:8080/students";

async function loadStudents() {
    const response = await fetch(STUDENT_API_URL, {
        method: "GET",
        credentials: "include",
    });

    if ([401, 403].includes(response.status)) {
        window.location.href = "/login";
    }
}

loadStudents();

