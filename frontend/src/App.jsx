import { useEffect, useState } from 'react'
import axios from 'axios'
import './App.css'

const initialRegisterForm = {
  nombre: '',
  email: '',
  password: '',
  telefono: '',
  tipoDocumentoIdentidad: '',
  numeroDocumentoIdentidad: '',
  tipoUsuario: '',
}

const initialLoginForm = { email: '', password: '' }

// En despliegue, define VITE_API_URL con la URL pública del backend.
const apiUrl = import.meta.env.VITE_API_URL || 'http://localhost:8080'
const loginUrl = `${apiUrl}/api/auth/login`
const registerUrl = `${apiUrl}/api/auth/register`

function getPage() {
  return window.location.pathname === '/registro' ? 'register' : 'login'
}

function navigateTo(path) {
  window.history.pushState({}, '', path)
  window.dispatchEvent(new PopStateEvent('popstate'))
}

function RequestMessage({ mensaje, error }) {
  if (mensaje) return <p className="success" role="status">{mensaje}</p>
  if (error) return <p className="error" role="alert">{error}</p>
  return null
}

function LoginPage() {
  const [form, setForm] = useState(initialLoginForm)
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  const handleSubmit = async (event) => {
    event.preventDefault()
    setMensaje('')
    setError('')
    setCargando(true)

    try {
      const response = await axios.post(loginUrl, form)
      localStorage.setItem('fleteco_token', response.data.token)
      localStorage.setItem('fleteco_tipo_usuario', response.data.tipoUsuario)
      setMensaje(`Bienvenido. Rol: ${response.data.tipoUsuario.toLowerCase()}.`)
      setForm(initialLoginForm)
    } catch (requestError) {
      if (requestError.response?.status === 401) {
        setError('El correo o la contraseña no son válidos.')
      } else if (requestError.response?.data) {
        setError(typeof requestError.response.data === 'string' ? requestError.response.data : 'Revisa los datos ingresados.')
      } else {
        setError('No fue posible conectar con el servidor.')
      }
    } finally {
      setCargando(false)
    }
  }

  return (
    <AuthLayout title="Iniciar sesión" subtitle="Accede a tu espacio de trabajo en FleteCo.">
      <form onSubmit={handleSubmit}>
        <label>
          Correo electrónico
          <input type="email" name="email" value={form.email} onChange={(event) => setForm({ ...form, email: event.target.value })} required autoComplete="email" />
        </label>
        <label>
          Contraseña
          <input type="password" name="password" value={form.password} onChange={(event) => setForm({ ...form, password: event.target.value })} required autoComplete="current-password" />
        </label>
        <button type="submit" disabled={cargando}>{cargando ? 'Ingresando...' : 'Iniciar sesión'}</button>
        <RequestMessage mensaje={mensaje} error={error} />
      </form>
      <div className="form-footer">
        <span>¿Aún no tienes una cuenta?</span>
        <button type="button" className="link-button" onClick={() => navigateTo('/registro')}>Crear cuenta</button>
      </div>
    </AuthLayout>
  )
}

function RegisterPage() {
  const [form, setForm] = useState(initialRegisterForm)
  const [mensaje, setMensaje] = useState('')
  const [error, setError] = useState('')
  const [cargando, setCargando] = useState(false)

  const handleChange = (event) => setForm({ ...form, [event.target.name]: event.target.value })

  const handleSubmit = async (event) => {
    event.preventDefault()
    setMensaje('')
    setError('')
    setCargando(true)

    try {
      const response = await axios.post(registerUrl, form)
      setMensaje(response.data)
      setForm(initialRegisterForm)
    } catch (requestError) {
      if (requestError.response?.status === 409) setError('El email ya está registrado.')
      else if (requestError.response?.data) setError(typeof requestError.response.data === 'string' ? requestError.response.data : 'Revisa los datos ingresados.')
      else setError('No fue posible conectar con el servidor.')
    } finally {
      setCargando(false)
    }
  }

  return (
    <AuthLayout title="Crear cuenta" subtitle="Regístrate para usar la plataforma según tu rol.">
      <form onSubmit={handleSubmit}>
        <label>Nombre completo<input name="nombre" value={form.nombre} onChange={handleChange} required autoComplete="name" /></label>
        <label>Correo electrónico<input type="email" name="email" value={form.email} onChange={handleChange} required autoComplete="email" /></label>
        <label>Contraseña<input type="password" name="password" minLength="6" value={form.password} onChange={handleChange} required autoComplete="new-password" /></label>
        <label>Teléfono<input name="telefono" value={form.telefono} onChange={handleChange} autoComplete="tel" /></label>
        <div className="two-columns">
          <label>Tipo de documento<select name="tipoDocumentoIdentidad" value={form.tipoDocumentoIdentidad} onChange={handleChange} required><option value="">Selecciona</option><option value="CC">Cédula de ciudadanía</option><option value="CE">Cédula de extranjería</option><option value="PASAPORTE">Pasaporte</option></select></label>
          <label>Número de documento<input name="numeroDocumentoIdentidad" value={form.numeroDocumentoIdentidad} onChange={handleChange} required /></label>
        </div>
        <fieldset><legend>Tipo de usuario</legend><label className="role-option"><input type="radio" name="tipoUsuario" value="CONDUCTOR" checked={form.tipoUsuario === 'CONDUCTOR'} onChange={handleChange} required />Conductor</label><label className="role-option"><input type="radio" name="tipoUsuario" value="DESPACHADOR" checked={form.tipoUsuario === 'DESPACHADOR'} onChange={handleChange} />Despachador</label></fieldset>
        <button type="submit" disabled={cargando}>{cargando ? 'Registrando...' : 'Crear cuenta'}</button>
        <RequestMessage mensaje={mensaje} error={error} />
      </form>
      <div className="form-footer"><span>¿Ya tienes una cuenta?</span><button type="button" className="link-button" onClick={() => navigateTo('/')}>Iniciar sesión</button></div>
    </AuthLayout>
  )
}

function AuthLayout({ title, subtitle, children }) {
  return <main className="page"><section className="register-card"><p className="eyebrow">FleteCo</p><h1>{title}</h1><p className="subtitle">{subtitle}</p>{children}</section></main>
}

function App() {
  const [page, setPage] = useState(getPage)

  useEffect(() => {
    const handlePopState = () => setPage(getPage())
    window.addEventListener('popstate', handlePopState)
    return () => window.removeEventListener('popstate', handlePopState)
  })

  return page === 'register' ? <RegisterPage /> : <LoginPage />
}

export default App
