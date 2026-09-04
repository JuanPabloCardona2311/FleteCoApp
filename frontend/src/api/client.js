import axios from 'axios'

// En despliegue, VITE_API_URL debe contener la URL pública del backend.
const client = axios.create({
  baseURL: import.meta.env.VITE_API_URL || 'http://localhost:8080',
})

client.interceptors.request.use((config) => {
  const token = localStorage.getItem('fleteco_token')

  if (token) {
    config.headers.Authorization = `Bearer ${token}`
  }

  return config
})

export default client
