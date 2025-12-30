export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  last: boolean;
  first: boolean;
}

export interface ApiResponse<T> {
  success: boolean;
  message: string;
  data: T;
}
