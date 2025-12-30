import axios from 'axios';

const api = axios.create({
  baseURL: process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api',
  headers: { 'Content-Type': 'application/json' },
});

api.interceptors.request.use(
  (config) => {
    if (typeof window !== 'undefined') {
      const token = localStorage.getItem('accessToken');
      if (token) config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => Promise.reject(error)
);

api.interceptors.response.use(
  (response) => response,
  async (error) => {
    const originalRequest = error.config as any;
    if (error.response?.status === 401 && !originalRequest._retry) {
      originalRequest._retry = true;
      if (typeof window !== 'undefined') {
        try {
          const refreshToken = localStorage.getItem('refreshToken');
          if (refreshToken) {
            const response = await axios.post(
              `${process.env.NEXT_PUBLIC_API_URL || 'http://localhost:8080/api'}/auth/refresh`,
              { refreshToken }
            );
            const { accessToken } = response.data;
            localStorage.setItem('accessToken', accessToken);
            originalRequest.headers.Authorization = `Bearer ${accessToken}`;
            return api(originalRequest);
          }
        } catch (refreshError) {
          localStorage.clear();
          window.location.href = '/login';
          return Promise.reject(refreshError);
        }
      }
    }
    return Promise.reject(error);
  }
);

export default api;

// Auth API
export const authAPI = {
  login: (username: string, password: string) => 
    api.post('/auth/login', { username, password }),
  logout: (userId: number) => 
    api.post(`/auth/logout?userId=${userId}`),
  refresh: (refreshToken: string) => 
    api.post('/auth/refresh', { refreshToken }),
};

// Admin User API
export const adminUserAPI = {
  getAll: (page = 0, size = 10, role?: string) => 
    api.get(`/admin/users?page=${page}&size=${size}${role ? `&role=${role}` : ''}`),
  getById: (id: number) => 
    api.get(`/admin/users/${id}`),
  create: (data: any) => 
    api.post('/admin/users', data),
  update: (id: number, data: any) => 
    api.put(`/admin/users/${id}`, data),
  delete: (id: number) => 
    api.delete(`/admin/users/${id}`),
};

// Admin Generator API
export const adminGeneratorAPI = {
  getAll: (page = 0, size = 10) => 
    api.get(`/admin/generators?page=${page}&size=${size}`),
  getById: (id: number) => 
    api.get(`/admin/generators/${id}`),
  create: (data: any) => 
    api.post('/admin/generators', data),
  update: (id: number, data: any) => 
    api.put(`/admin/generators/${id}`, data),
  delete: (id: number) => 
    api.delete(`/admin/generators/${id}`),
};

// Admin Ticket API
export const adminTicketAPI = {
  getAll: (page = 0, size = 10, status?: string) => 
    api.get(`/admin/tickets?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  getById: (id: number) => 
    api.get(`/admin/tickets/${id}`),
  create: (data: any) => 
    api.post('/admin/tickets', data),
  update: (id: number, data: any) => 
    api.put(`/admin/tickets/${id}`, data),
  delete: (id: number) => 
    api.delete(`/admin/tickets/${id}`),
  assign: (id: number, employeeIds: number[]) => 
    api.post(`/admin/tickets/${id}/assign`, { employeeIds }),
};

// Employee Day API
export const employeeDayAPI = {
  getStatus: () => 
    api.get('/employee/day/status'),
  start: () => 
    api.post('/employee/day/start'),
  end: () => 
    api.post('/employee/day/end'),
};

// Employee Job Card API
export const employeeJobCardAPI = {
  getAll: (page = 0, size = 10, status?: string) => 
    api.get(`/employee/job-cards?page=${page}&size=${size}${status ? `&status=${status}` : ''}`),
  getById: (id: number) => 
    api.get(`/employee/job-cards/${id}`),
  updateStatus: (id: number, data: any) => 
    api.post(`/employee/job-cards/${id}/status`, data),
};
