export interface LoginResponse {
  accessToken: string;
  refreshToken: string;
  tokenType: string;
  userId: number;
  username: string;
  fullName: string;
  role: 'ADMIN' | 'EMPLOYEE';
}

export const isAuthenticated = (): boolean => {
  if (typeof window === 'undefined') return false;
  return !!localStorage.getItem('accessToken');
};

export const getRole = (): 'ADMIN' | 'EMPLOYEE' | null => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('role') as 'ADMIN' | 'EMPLOYEE' | null;
};

export const getUserId = (): number | null => {
  if (typeof window === 'undefined') return null;
  const userId = localStorage.getItem('userId');
  return userId ? parseInt(userId) : null;
};

export const getUsername = (): string | null => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('username');
};

export const getFullName = (): string | null => {
  if (typeof window === 'undefined') return null;
  return localStorage.getItem('fullName');
};

export const saveAuthData = (data: LoginResponse) => {
  if (typeof window === 'undefined') return;
  localStorage.setItem('accessToken', data.accessToken);
  localStorage.setItem('refreshToken', data.refreshToken);
  localStorage.setItem('role', data.role);
  localStorage.setItem('userId', data.userId.toString());
  localStorage.setItem('username', data.username);
  localStorage.setItem('fullName', data.fullName);
};

export const clearAuthData = () => {
  if (typeof window === 'undefined') return;
  localStorage.clear();
};
