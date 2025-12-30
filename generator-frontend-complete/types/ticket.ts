import { Generator } from './generator';
import { User } from './user';

export interface Ticket {
  id: number;
  ticketNumber: string;
  generatorId: number;
  generatorName: string;
  generatorModel: string;
  title: string;
  description?: string;
  weight?: string;
  status: 'CREATED' | 'ASSIGNED' | 'CLOSED';
  scheduledDate: string;
  scheduledTime: string;
  createdById: number;
  createdByName: string;
  createdAt: string;
  assignedEmployees?: User[];
}

export interface CreateTicketRequest {
  ticketNumber: string;
  generatorId: number;
  title: string;
  description?: string;
  weight?: string;
  scheduledDate: string;
  scheduledTime: string;
}
