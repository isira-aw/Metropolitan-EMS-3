import { JobCardType } from './ticket';

export interface JobCardSummary {
  jobCardId: number;
  ticketNumber: string;
  generatorName: string;
  status: string;
  startTime?: string;
  endTime?: string;
  workMinutes: number;
}

export interface DailyWorkRecord {
  date: string;
  dayStartTime?: string;
  dayEndTime?: string;
  totalWorkMinutes: number;
  morningOtMinutes: number;
  eveningOtMinutes: number;
  dayStarted: boolean;
  dayEnded: boolean;
  jobCards: JobCardSummary[];
}

export interface TimeTrackingReport {
  employeeId: number;
  employeeName: string;
  employeeEmail: string;
  startDate: string;
  endDate: string;
  dailyRecords: DailyWorkRecord[];
  totalWorkMinutes: number;
  totalMorningOtMinutes: number;
  totalEveningOtMinutes: number;
  totalOtMinutes: number;
}

export interface GeneratorWork {
  generatorName: string;
  generatorModel: string;
  jobCardType: JobCardType;
  workMinutes: number;
  status: string;
}

export interface DailyOTRecord {
  date: string;
  dayOfWeek: string;
  totalWorkMinutes: number;
  morningOtMinutes: number;
  eveningOtMinutes: number;
  regularWorkMinutes: number;
  generatorWorks: GeneratorWork[];
}

export interface PerformanceAnalysis {
  averageWorkMinutesPerDay: number;
  averageOTMinutesPerDay: number;
  totalJobsCompleted: number;
  totalJobsInProgress: number;
  totalJobsCancelled: number;
  jobCompletionRate: number;
  mostWorkedJobType: string;
  mostWorkedJobTypeMinutes: number;
}

export interface OTTrackingReport {
  employeeId: number;
  employeeName: string;
  employeeEmail: string;
  startDate: string;
  endDate: string;
  totalDaysWorked: number;
  totalWorkMinutes: number;
  totalMorningOtMinutes: number;
  totalEveningOtMinutes: number;
  totalOtMinutes: number;
  dailyOTRecords: DailyOTRecord[];
  workMinutesByJobType: Record<JobCardType, number>;
  performanceAnalysis: PerformanceAnalysis;
}

export interface ReportParams {
  employeeId: number;
  startDate: string;
  endDate: string;
}
