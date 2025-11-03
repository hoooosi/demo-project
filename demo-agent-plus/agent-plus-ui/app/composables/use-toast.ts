import { reactive } from 'vue'

export interface Toast {
    id: string
    title?: string
    description?: string
    variant?: 'default' | 'destructive'
    duration?: number
}

const toasts = reactive<Toast[]>([])

export const useToast = () => {
    const toast = (options: Omit<Toast, 'id'>) => {
        const id = Math.random().toString(36).substr(2, 9)
        const duration = options.duration || 3000

        toasts.push({
            ...options,
            id
        })

        if (duration > 0) {
            setTimeout(() => {
                dismiss(id)
            }, duration)
        }

        return id
    }

    const dismiss = (id: string) => {
        const index = toasts.findIndex(t => t.id === id)
        if (index > -1) {
            toasts.splice(index, 1)
        }
    }

    return {
        toast,
        toasts,
        dismiss
    }
}
