import Logo from './Logo'

function AuthLayout({ title, subtitle, children }) {
  return (
    <main className="page">
      <section className="register-card">
        <Logo />
        <h1>{title}</h1>
        <p className="subtitle">{subtitle}</p>
        {children}
      </section>
    </main>
  )
}

export default AuthLayout
