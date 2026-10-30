import React from 'react'
import ReactDOM from 'react-dom/client'
import { BrowserRouter, Navigate, Route, Routes } from 'react-router-dom'
import { AuthProvider } from './auth/AuthContext'
import ProtectedRoute from './components/ProtectedRoute'
import Layout from './components/Layout'
import AuthPage from './pages/AuthPage'
import ProductsPage from './pages/ProductsPage'
import CategoriesPage from './pages/CategoriesPage'
import UsersPage from './pages/UsersPage'
import ProfilePage from './pages/ProfilePage'
import './styles.css'

ReactDOM.createRoot(document.getElementById('root')).render(
  <React.StrictMode>
    <BrowserRouter>
      <AuthProvider>
        <Routes>
          <Route path="/login" element={<AuthPage mode="login" />} />
          <Route path="/register" element={<AuthPage mode="register" />} />
          <Route element={<ProtectedRoute />}>
            <Route element={<Layout />}>
              <Route index element={<ProductsPage />} />
              <Route path="/categories" element={<CategoriesPage />} />
              <Route path="/profile" element={<ProfilePage />} />
            </Route>
          </Route>
          <Route element={<ProtectedRoute admin />}>
            <Route element={<Layout />}>
              <Route path="/users" element={<UsersPage />} />
            </Route>
          </Route>
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </AuthProvider>
    </BrowserRouter>
  </React.StrictMode>,
)
