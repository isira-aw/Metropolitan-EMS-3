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

export interface Generator {
  id: number;
  model: string;
  name: string;
  capacity?: string;
  locationName?: string;
  ownerEmail?: string;
  latitude?: number;
  longitude?: number;
  note?: string;
  createdAt: string;
}

export interface Ticket {
  id: number;
  ticketNumber: string;
  generator: Generator;
  title: string;
  description?: string;
  weight?: string;
  status: 'CREATED' | 'ASSIGNED' | 'CLOSED';
  scheduledDate: string;
  scheduledTime: string;
  createdAt: string;
}

export type JobStatus = 'PENDING' | 'TRAVELING' | 'STARTED' | 'ON_HOLD' | 'COMPLETED' | 'CANCEL';

export interface JobCard {
  id: number;
  mainTicketId: number;
  ticketNumber: string;
  ticketTitle: string;
  generator: Generator;
  status: JobStatus;
  startTime?: string;
  endTime?: string;
  workMinutes: number;
  approved: boolean;
  image?: string;
  createdAt: string;
}

export interface DayStatus {
  dayStarted: boolean;
  dayEnded: boolean;
  dayStartTime?: string;
  dayEndTime?: string;
}

export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
  first: boolean;
  last: boolean;
}
