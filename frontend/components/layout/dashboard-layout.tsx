"use client"

import { useState } from "react"
import { Sidebar } from "@/components/layout/sidebar"
import { Header } from "@/components/layout/header"
import { Dashboard } from "@/components/pages/dashboard"
import { Patients } from "@/components/pages/patients"
import { Appointments } from "@/components/pages/appointments"
import { Finance } from "@/components/pages/finance"
import { Profile } from "@/components/pages/profile"
import { Settings } from "@/components/pages/settings"

export type Page = "dashboard" | "patients" | "appointments" | "finance" | "profile" | "settings"

export function DashboardLayout() {
  const [currentPage, setCurrentPage] = useState<Page>("dashboard")
  const [sidebarOpen, setSidebarOpen] = useState(true)

  const renderPage = () => {
    switch (currentPage) {
      case "dashboard":
        return <Dashboard />
      case "patients":
        return <Patients />
      case "appointments":
        return <Appointments />
      case "finance":
        return <Finance />
      case "profile":
        return <Profile />
      case "settings":
        return <Settings />
      default:
        return <Dashboard />
    }
  }

  return (
    <div className="flex h-screen bg-gray-50">
      <Sidebar
        currentPage={currentPage}
        onPageChange={setCurrentPage}
        isOpen={sidebarOpen}
        onToggle={() => setSidebarOpen(!sidebarOpen)}
      />

      <div className="flex-1 flex flex-col overflow-hidden">
        <Header onMenuClick={() => setSidebarOpen(!sidebarOpen)} sidebarOpen={sidebarOpen} />

        <main className="flex-1 overflow-auto p-6">{renderPage()}</main>
      </div>
    </div>
  )
}
