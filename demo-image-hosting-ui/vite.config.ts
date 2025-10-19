import { defineConfig, loadEnv } from 'vite'
import vue from '@vitejs/plugin-vue'
import { fileURLToPath, URL } from 'node:url'

export default ({ mode }: { mode: string }) => {
    const env = loadEnv(mode, process.cwd());

    return defineConfig({
        server: {
            port: 3030,
            proxy: {
                '/api': {
                    target: env.VITE_APP_API_URL,
                    changeOrigin: true,
                    rewrite: (path) => path.replace(/^\/api/, ''),
                }
            }
        },
        resolve: {
            alias: {
                '@': fileURLToPath(new URL('./src', import.meta.url))
            },
        },
        plugins: [vue()],
    })
}
