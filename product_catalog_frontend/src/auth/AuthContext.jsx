import { createContext, useContext, useMemo, useState } from 'react'

const AuthContext = createContext(null)

export function AuthProvider({ children }) {
  const [user, setUser] = useState(() => {
    const saved = localStorage.getItem('user')
    return saved ? JSON.parse(saved) : null
  })

  const value = useMemo(
    () => ({
      user,
      setSession(session) {
        localStorage.setItem('token', session.token)
        const profile = { email: session.email, fullName: session.fullName, role: session.role }
        localStorage.setItem('user', JSON.stringify(profile))
        setUser(profile)
      },
      logout() {
        localStorage.removeItem('token')
        localStorage.removeItem('user')
        setUser(null)
      },
    }),
    [user],
  )

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export const useAuth = () => useContext(AuthContext)
