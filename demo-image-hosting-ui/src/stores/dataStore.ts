import { defineStore } from 'pinia'
import { ref } from 'vue'

const useDataStore = defineStore('data-store', () => {
  const user = ref<VO.UserVO>()
  const gridMaxSize = ref<number>(10)
  const gridRows = ref<number>(10)

  return {
    user,
    gridMaxSize,
    gridRows,
  }
})

export default useDataStore
