import { format } from 'date-fns';

export const formatDate = (date: string | Date): string => {
  try {
    return format(new Date(date), 'MMM dd, yyyy');
  } catch {
    return '-';
  }
};

export const formatDateTime = (date: string | Date): string => {
  try {
    return format(new Date(date), 'MMM dd, yyyy HH:mm');
  } catch {
    return '-';
  }
};

export const formatTime = (date: string | Date): string => {
  try {
    return format(new Date(date), 'HH:mm');
  } catch {
    return '-';
  }
};
