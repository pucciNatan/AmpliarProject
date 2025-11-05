import { AuthController } from "@/controllers/auth-controller";

const BASE_URL = "http://localhost:8080";

type ApiOptions = Omit<RequestInit, 'body'> & {
    body?: object;
};

export const api = async (endpoint: string, options: ApiOptions = {}) => {
    const authController = AuthController.getInstance();
    const { token } = authController.getAuthState();

    const headers: Record<string, string> = {
        ...options.headers as Record<string, string>,
        "Content-Type": "application/json",
    };

    if (token) {
        headers["Authorization"] = `Bearer ${token}`;
    }

    const { body, ...restOptions } = options;

    const config: RequestInit = {
        ...restOptions,
        headers,
    };

    if (body) {
        config.body = JSON.stringify(body);
    }

    const response = await fetch(`${BASE_URL}${endpoint}`, config);

    if (!response.ok) {
        let errorMessage = `Erro ${response.status} ao acessar ${endpoint}`;

        try {
            const errorData = await response.json();

            if (errorData.errors && Array.isArray(errorData.errors)) {
                const messages = errorData.errors.map((err: any) => err.defaultMessage || err.code).join(", ");
                errorMessage = messages;
            }
            else if (errorData.fieldErrors && Array.isArray(errorData.fieldErrors)) {
                errorMessage = errorData.fieldErrors
                    .map((err: any) => err.defaultMessage)
                    .join(", ");
            }
            else if (errorData.error || errorData.message) {
                errorMessage = errorData.error || errorData.message;
            }
            else {
                errorMessage = `Erro ${response.status}: Resposta inesperada do servidor.`;
            }

        } catch (jsonError) {
            try {
                const errorText = await response.text();
                if (errorText) {
                    errorMessage = errorText;
                } else {
                    errorMessage = `Erro ${response.status}: A resposta do servidor estava vazia.`;
                }
            } catch (textError) {
                errorMessage = `Erro ${response.status}: Não foi possível ler a resposta do servidor.`;
            }
        }
        throw new Error(errorMessage);
    }

    const contentType = response.headers.get("content-type");
    if (contentType && contentType.includes("application/json")) {
        return response.json();
    }
    return response;
};
