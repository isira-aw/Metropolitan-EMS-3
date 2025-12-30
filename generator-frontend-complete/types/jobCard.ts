import { Generator } from './generator';

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

export interface UpdateStatusRequest {
  newStatus: JobStatus;
  latitude: number;
  longitude: number;
}

export interface DayStatus {
  dayStarted: boolean;
  dayEnded: boolean;
  dayStartTime?: string;
  dayEndTime?: string;
}
