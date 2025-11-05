"use client"

import * as React from "react"

type Theme = "dark" | "light" | "system"

type ThemeProviderProps = {
  children: React.ReactNode
  defaultTheme?: Theme
  storageKey?: string
}

type ThemeProviderState = {
  theme: Theme
  setTheme: (theme: Theme) => void
}

const initialState: ThemeProviderState = {
  theme: "system",
  setTheme: () => null,
}

const ThemeProviderContext = React.createContext<ThemeProviderState>(initialState)

export function ThemeProvider({
  children,
  defaultTheme = "system",
  storageKey = "vite-ui-theme",
  ...props
}: ThemeProviderProps) {
  // 1. CORREÇÃO: Inicialize o estado apenas com o defaultTheme (seguro para o servidor)
  const [theme, setTheme] = React.useState<Theme>(defaultTheme)

  // 2. CORREÇÃO: Este useEffect SÓ roda no cliente (navegador)
  React.useEffect(() => {
    // 2a. Verifique o que está salvo no localStorage
    const storedTheme = (localStorage.getItem(storageKey) as Theme) || defaultTheme
    setTheme(storedTheme)
  }, [storageKey, defaultTheme])


  React.useEffect(() => {
    const root = window.document.documentElement
    root.classList.remove("light", "dark")

    let effectiveTheme = theme
    if (theme === "system") {
      effectiveTheme = window.matchMedia("(prefers-color-scheme: dark)").matches ? "dark" : "light"
    }

    root.classList.add(effectiveTheme)

    // 3. CORREÇÃO: Salve no localStorage apenas quando o tema mudar
    localStorage.setItem(storageKey, theme)

  }, [theme, storageKey])

  const value = {
    theme,
    setTheme, // A função setTheme já atualiza o estado, disparando o useEffect acima
  }

  return (
    <ThemeProviderContext.Provider {...props} value={value}>
      {children}
    </ThemeProviderContext.Provider>
  )
}

export const useTheme = () => {
  const context = React.useContext(ThemeProviderContext)

  if (context === undefined) throw new Error("useTheme must be used within a ThemeProvider")

  return context
}
