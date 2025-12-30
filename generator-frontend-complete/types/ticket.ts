import { Generator } from './generator';
import { User } from './user';

export type JobCardType =
  | 'SERVICE'
  | 'REPAIR'
  | 'MAINTENANCE'
  | 'VISIT'
  | 'EMERGENCY';

export type TicketStatus =
  | 'CREATED'
  | 'ASSIGNED'
  | 'IN_PROGRESS'
  | 'COMPLETED'
  | 'PENDING_APPROVAL'
  | 'APPROVED'
  | 'REJECTED'
  | 'CLOSED';

export interface SubTicket {
  id: number;
  ticketNumber: string;
  mainTicketId: number;
  mainTicketNumber: string;
  mainTicketWeight: number;
  employeeId: number;
  employeeName: string;
  employeeEmail?: string;
  status: TicketStatus;
  notes?: string;
  completionFactor?: number;
  qualityFactor?: number;
  score?: number;
  approved: boolean;
  approvedById?: number;
  approvedByName?: string;
  approvedAt?: string;
  adminReviewNotes?: string;
  createdAt: string;
  updatedAt: string;
  completedAt?: string;
}

export interface Ticket {
  id: number;
  ticketNumber: string;
  generatorId: number;
  generatorName: string;
  generatorModel: string;
  title: string;
  description?: string;
  jobCardType: JobCardType;
  weight: number; // 1-5 stars
  weightDisplay: string; // "*****"
  status: TicketStatus;
  scheduledDate: string;
  scheduledTime: string;
  createdById: number;
  createdByName: string;
  createdAt: string;
  subTickets?: SubTicket[];
  totalAssignments?: number;
  completedAssignments?: number;
}

export interface CreateTicketRequest {
  generatorId: number;
  title: string;
  description?: string;
  jobCardType: JobCardType;
  weight: number; // 1-5
  scheduledDate: string;
  scheduledTime: string;
  employeeIds: number[]; // 1-5 employees required
}

export interface ApprovalRequest {
  approved: boolean;
  completionFactor?: number; // 0.0 to 1.0
  qualityFactor?: number; // 0.0 to 1.0
  adminReviewNotes?: string;
}
