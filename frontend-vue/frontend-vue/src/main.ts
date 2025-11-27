import './assets/main.css'

import { createApp } from 'vue'
import { createPinia } from 'pinia'
import { persistToLocalStorage } from '@/plugins/persistToLocalStorage'

import App from './App.vue'
import router from './router'

const app = createApp(App)
const pinia = createPinia()

pinia.use(persistToLocalStorage(['user']))
app.use(pinia)
app.use(router)

app.mount('#app')
