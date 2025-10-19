import { createApp } from 'vue'
import router from './router'
import { createPinia } from 'pinia';
import Antd from 'ant-design-vue'
import './assets/style.scss'
import App from './App.vue'

const app = createApp(App)
    .use(router)
    .use(Antd)
    .use(createPinia())
    .mount('#app')
