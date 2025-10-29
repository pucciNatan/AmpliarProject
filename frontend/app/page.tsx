"use client"

import { useState } from "react"
import { ThemeProvider } from "@/providers/theme-provider"
import { AuthView } from "@/views/auth/auth-view"
import { DashboardLayout } from "@/views/layout/dashboard-layout"

export default function Home() {
  const [isAuthenticated, setIsAuthenticated] = useState(false)

  return (
    <ThemeProvider defaultTheme="light" storageKey="psy-schedule-theme">
      {!isAuthenticated ? <AuthView onLogin={() => setIsAuthenticated(true)} /> : <DashboardLayout />}
    </ThemeProvider>
  )
}
