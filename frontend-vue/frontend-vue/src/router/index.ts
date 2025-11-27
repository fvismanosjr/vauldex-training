import { createRouter, createWebHistory } from 'vue-router'
import { checkIfAuthenticated } from '@/services/auth';
import LoginView from '@/views/LoginView.vue'
import AuthLayout from '@/views/layouts/AuthLayout.vue'
import DefaultLayout from '@/views/layouts/DefaultLayout.vue';
import DashboardView from '@/views/DashboardView.vue';
import StudentView from '@/views/StudentView.vue';
import StudentProfileView from '@/views/StudentProfileView.vue';
import DegreeView from '@/views/DegreeView.vue';
import DegreeProfileView from '@/views/DegreeProfileView.vue';
import HomeView from '@/views/HomeView.vue';
import PageNotFoundView from '@/views/PageNotFoundView.vue';
import RegisterView from '@/views/RegisterView.vue';
import { useUserStore } from '@/stores/user';
import type { RoleType } from '@/types/RoleType';
import ChatView from '@/views/ChatView.vue';

const router = createRouter({
    history: createWebHistory(import.meta.env.BASE_URL),
    routes: [
        {
            path: '/',
            name: 'home',
            component: HomeView,
            meta: {
                guestOnly: true,
                title: "Home",
                layout: AuthLayout,
            }
        },
        {
            path: '/login',
            name: 'login',
            component: LoginView,
            meta: {
                title: "Login",
                guestOnly: true,
                layout: AuthLayout
            }
        },
        {
            path: '/register',
            name: 'register',
            component: RegisterView,
            meta: {
                title: "Create an account",
                guestOnly: true,
                layout: AuthLayout
            }
        },
        {
            path: "/dashboard",
            name: "dashboard",
            component: DashboardView,
            meta: {
                title: "Dashboard",
                description: "Anything you want is right here",
                layout: DefaultLayout,
                roles: ["ROLE_ADMIN"],
                breadcrumb: [
                    "Dashboard",
                ],
            }
        },
        {
            path: "/students",
            name: "students",
            component: StudentView,
            meta: {
                title: "Students",
                description: "Show all student records",
                layout: DefaultLayout,
                roles: ["ROLE_ADMIN"],
                breadcrumb: [
                    "Students",
                ],
            },
        },
        {
            path: "/students/:id/profile",
            name: "student-profile",
            component: StudentProfileView,
            meta: {
                title: "Student Profile",
                description: "Show all specific student details",
                layout: DefaultLayout,
                breadcrumb: [
                    "Students",
                ],
            }
        },
        {
            path: "/degrees",
            name: "degrees",
            component: DegreeView,
            meta: {
                title: "Degrees",
                description: "Show all degree records",
                layout: DefaultLayout,
                roles: ["ROLE_ADMIN"],
                breadcrumb: [
                    "Degrees",
                ],
            }
        },
        {
            path: "/degrees/:id/profile",
            name: "degree-profile",
            component: DegreeProfileView,
            meta: {
                title: "Degree Profile",
                description: "Show all specific degree details",
                layout: DefaultLayout,
                roles: ["ROLE_ADMIN"],
                breadcrumb: [
                    "Degrees",
                ]
            }
        },
        {
            path: "/users",
            name: "users",
            component: DegreeView,
            meta: {
                title: "Users",
                description: "Show all users",
                layout: DefaultLayout,
                roles: ["ROLE_SUDO"],
                breadcrumb: [
                    "Users"
                ]
            }
        },
        {
            path: "/chats",
            name: "chats",
            component: ChatView,
            meta: {
                title: "Chat",
                description: "You can message me here",
                layout: DefaultLayout,
            }
        },
        { 
            path: '/:pathMatch(.*)*', 
            name: 'notfound', 
            component: PageNotFoundView,
            meta: {
                guestOnly: true,
                layout: AuthLayout,
            }
        },
    ],
});

router.beforeEach(async (to) => {
    const authResult = await checkIfAuthenticated();
    const user = useUserStore();

    if (authResult === "network-error" && to.name !== "home") {
        return { name: "home" };
    }

    if (to.meta.guestOnly) {
        if (to.name === "login" && authResult) {
            return { name: "dashboard" };
        } else return true;
    }

    if (!authResult) {
        return { name: "login" };
    }

    // checking of roles
    if (user.isSuper) {
        return true;
    }

    if (to.meta.roles && !(to.meta.roles as Array<RoleType>).includes(user.user.role)) {
        return { name: "notfound" };
    }
});

router.afterEach((to) => {
    document.title = `No Brand - ${to.meta.title}`;
})

export default router
