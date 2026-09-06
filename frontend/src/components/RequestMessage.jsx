function RequestMessage({ mensaje, error }) {
  if (mensaje) return <p className="success" role="status">{mensaje}</p>
  if (error) return <p className="error" role="alert">{error}</p>
  return null
}

export default RequestMessage
