export interface User {
  id: number;
  username: string;
  fullName: string;
  role: 'ADMIN' | 'EMPLOYEE';
  phone?: string;
  email?: string;
  active: boolean;
  createdAt: string;
}

export interface CreateUserRequest {
  username: string;
  password: string;
  fullName: string;
  role: 'ADMIN' | 'EMPLOYEE';
  phone?: string;
  email?: string;
}
