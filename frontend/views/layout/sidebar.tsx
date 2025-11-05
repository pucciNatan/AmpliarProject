"use client"

import { cn } from "@/lib/utils"
import { Button } from "@/components/ui/button"
import { LayoutDashboard, Users, Calendar, DollarSign, User, Settings, Brain } from "lucide-react"
import type { Page } from "./dashboard-layout"
import type { User as AuthUser } from "@/models/auth" // Renomeado para evitar conflito

interface SidebarProps {
  currentPage: Page
  onPageChange: (page: Page) => void
  isOpen: boolean
  onToggle: () => void
  user: AuthUser | null
}

const menuItems = [
  { id: "dashboard" as Page, label: "Dashboard", icon: LayoutDashboard },
  { id: "patients" as Page, label: "Pacientes", icon: Users },
  { id: "appointments" as Page, label: "Agendamentos", icon: Calendar },
  { id: "finance" as Page, label: "Financeiro", icon: DollarSign },
  { id: "profile" as Page, label: "Perfil", icon: User },
  { id: "settings" as Page, label: "Configurações", icon: Settings },
]

export function Sidebar({ currentPage, onPageChange, isOpen, user }: SidebarProps) {

  const getInitials = () => {
    if (!user?.name) return "U"
    return user.name
      .split(" ")
      .map((n) => n[0])
      .join("")
      .slice(0, 2)
      .toUpperCase()
  }

  return (
    <div
      className={cn(
        "bg-white dark:bg-gray-800 border-r border-gray-200 dark:border-gray-700 transition-all duration-300 flex flex-col",
        isOpen ? "w-64" : "w-16",
      )}
    >
      <div className="p-4 border-b border-gray-200 dark:border-gray-700">
        <div className="flex items-center gap-3">
          <div className="flex h-8 w-8 items-center justify-center rounded-lg bg-blue-600 dark:bg-blue-500">
            <Brain className="h-5 w-5 text-white" />
          </div>
          {isOpen && (
            <div>
              <h1 className="font-semibold text-gray-900 dark:text-gray-100">PsySchedule</h1>
              <p className="text-xs text-gray-500 dark:text-gray-400">Sistema Psicológico</p>
            </div>
          )}
        </div>
      </div>

      <nav className="flex-1 p-4">
        <ul className="space-y-2">
          {menuItems.map((item) => {
            const Icon = item.icon
            const isActive = currentPage === item.id

            return (
              <li key={item.id}>
                <Button
                  variant={isActive ? "default" : "ghost"}
                  className={cn(
                    "w-full justify-start gap-3 h-10",
                    isActive
                      ? "bg-blue-600 text-white hover:bg-blue-700 dark:bg-blue-500 dark:hover:bg-blue-600"
                      : "text-gray-700 hover:bg-gray-100 dark:text-gray-300 dark:hover:bg-gray-700",
                    !isOpen && "justify-center px-2",
                  )}
                  onClick={() => onPageChange(item.id)}
                >
                  <Icon className="h-4 w-4 flex-shrink-0" />
                  {isOpen && <span className="truncate">{item.label}</span>}
                </Button>
              </li>
            )
          })}
        </ul>
      </nav>

      {isOpen && (
        <div className="p-4 border-t border-gray-200 dark:border-gray-700">
          <div className="flex items-center gap-3">
            <div className="h-8 w-8 rounded-full bg-green-100 dark:bg-green-900 flex items-center justify-center">
              <span className="text-sm font-medium text-green-700 dark:text-green-300">
                {getInitials()}
              </span>
            </div>
            <div className="flex-1 min-w-0">
              <p className="text-sm font-medium text-gray-900 dark:text-gray-100 truncate">
                {user?.name || "..."}
              </p>
              <p className="text-xs text-gray-500 dark:text-gray-400 truncate">
                {user?.email || "..."}
              </p>
            </div>
          </div>
        </div>
      )}
    </div>
  )
}
