import { Generator } from './generator';
import { User } from './user';

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
  createdByName: string;
  createdAt: string;
  assignedEmployees?: User[];
}

export interface CreateTicketRequest {
  generatorId: number;
  title: string;
  description?: string;
  weight?: string;
  scheduledDate: string;
  scheduledTime: string;
}
