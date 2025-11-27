const loginForm = document.getElementById("login-form");

loginForm.addEventListener("submit", async (event) => {
    event.preventDefault();

    const usernameInput = document.getElementsByName("username")[0].value;
    const passwordInput = document.getElementsByName("password")[0].value;

    const auth = {
        username: usernameInput,
        password: passwordInput,
    }

    const response = await fetch("http://localhost:8080/auth/login", {
        method: "POST",
        headers: {
            "Content-Type": "application/json",
        },
        credentials: "include",
        body: JSON.stringify(auth),
    })

    if (response.ok) {
        window.location.href = "/students";
    }
})