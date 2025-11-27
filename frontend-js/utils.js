async function getCurrentLoggedUser()
{
    const response = await fetch("http://localhost:8080/auth/me", {
        credentials: "include"
    }).then((response) => {
        switch (response.status) {
            case 200:
                if (window.location.href == "http://localhost:5500/login/") {
                    window.location.href = "/students";
                }
                break;
        
            default:
                window.location.href = "/login";
                break;
        }
    })
}

getCurrentLoggedUser();

// const API_URL = "http://localhost:8080/students";
// const form = document.getElementById("studentForm");
// const tableBody = document.querySelector("#studentsTable tbody");
// var token = "";

// // async function login()
// // {
// //     const response = await fetch("http://localhost:8080/auth/login", {
// //         method: "POST",
// //         headers: {
// //             "Content-Type": "application/json",
// //         },
// //         body: JSON.stringify({ 
// //             username: "superadmin",
// //             password: "password",
// //         }),
// //     })

// //     const result = await response.json();

// //     token = result.token || "";
// // }

// // async function loadStudents() {
// //     const res = await fetch(API_URL, {
// //         method: "GET",
// //         headers: {
// //             "Content-Type": "application/json",
// //             "Authorization": `Bearer ${token}`,
// //         },
// //     });

// //     const students = await res.json();

// //     tableBody.innerHTML = "";

// //     students._embedded.studentResponseList.forEach((s) => {
// //     const row = document.createElement("tr");
// //         row.innerHTML = `
// //           <td>${s.id}</td>
// //           <td>${s.name}</td>
// //           <td>${s.email}</td>
// //           <td><button onclick="deleteStudent(${s.id})">Delete</button></td>
// //         `;
// //         tableBody.appendChild(row);
// //     });
// // }

// // form.addEventListener("submit", async (e) => {
// //     e.preventDefault();
// //     const student = {
// //         name: document.getElementById("name").value,
// //         email: document.getElementById("email").value,
// //     };
    
// //     await fetch(API_URL, {
// //         method: "POST",
// //         headers: {"Content-Type": "application/json"},
// //         body: JSON.stringify(student),
// //     });
// //     form.reset();
// //     loadStudents();
// // });

// // async function deleteStudent(id) {
// //     await fetch(`${API_URL}/${id}`, { method: "DELETE" });
// //     loadStudents();
// // }

// // login();

// // setTimeout(() => {
// //     loadStudents();
// // }, 5000)

