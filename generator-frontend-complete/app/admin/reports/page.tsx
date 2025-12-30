'use client';
import { useState, useEffect } from 'react';
import { api } from '@/lib/api';
import { TimeTrackingReport, OTTrackingReport, ReportParams } from '@/types/report';
import { User } from '@/types/user';
import { Calendar, Clock, TrendingUp, Users, Download, FileText } from 'lucide-react';
import LoadingSpinner from '@/components/LoadingSpinner';
import ErrorMessage from '@/components/ErrorMessage';

export default function ReportsPage() {
  const [reportType, setReportType] = useState<'time' | 'ot'>('time');
  const [employees, setEmployees] = useState<User[]>([]);
  const [selectedEmployee, setSelectedEmployee] = useState<number | null>(null);
  const [startDate, setStartDate] = useState('');
  const [endDate, setEndDate] = useState('');
  const [timeReport, setTimeReport] = useState<TimeTrackingReport | null>(null);
  const [otReport, setOTReport] = useState<OTTrackingReport | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    loadEmployees();
    // Set default dates (last 30 days)
    const end = new Date();
    const start = new Date();
    start.setDate(start.getDate() - 30);
    setEndDate(end.toISOString().split('T')[0]);
    setStartDate(start.toISOString().split('T')[0]);
  }, []);

  const loadEmployees = async () => {
    try {
      const response = await api.get<{ content: User[] }>('/admin/users?size=100');
      const employeeList = response.content.filter(u => u.role === 'EMPLOYEE');
      setEmployees(employeeList);
    } catch (err: any) {
      setError(err.message || 'Failed to load employees');
    }
  };

  const generateReport = async () => {
    if (!selectedEmployee || !startDate || !endDate) {
      setError('Please select employee and date range');
      return;
    }

    setLoading(true);
    setError('');
    setTimeReport(null);
    setOTReport(null);

    try {
      const params = new URLSearchParams({
        employeeId: selectedEmployee.toString(),
        startDate,
        endDate
      });

      if (reportType === 'time') {
        const report = await api.get<TimeTrackingReport>(
          `/admin/reports/time-tracking?${params}`
        );
        setTimeReport(report);
      } else {
        const report = await api.get<OTTrackingReport>(
          `/admin/reports/ot-tracking?${params}`
        );
        setOTReport(report);
      }
    } catch (err: any) {
      setError(err.message || 'Failed to generate report');
    } finally {
      setLoading(false);
    }
  };

  const formatMinutes = (minutes: number) => {
    const hours = Math.floor(minutes / 60);
    const mins = minutes % 60;
    return `${hours}h ${mins}m`;
  };

  const exportToCSV = () => {
    // Simple CSV export functionality
    if (reportType === 'time' && timeReport) {
      let csv = 'Date,Day Started,Day Ended,Total Work,Morning OT,Evening OT,Total OT\n';
      timeReport.dailyRecords.forEach(record => {
        csv += `${record.date},${record.dayStarted ? 'Yes' : 'No'},${record.dayEnded ? 'Yes' : 'No'},${formatMinutes(record.totalWorkMinutes)},${formatMinutes(record.morningOtMinutes)},${formatMinutes(record.eveningOtMinutes)},${formatMinutes(record.morningOtMinutes + record.eveningOtMinutes)}\n`;
      });

      const blob = new Blob([csv], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `time-report-${timeReport.employeeName}-${startDate}-${endDate}.csv`;
      a.click();
    } else if (reportType === 'ot' && otReport) {
      let csv = 'Date,Day of Week,Total Work,Morning OT,Evening OT,Regular Work\n';
      otReport.dailyOTRecords.forEach(record => {
        csv += `${record.date},${record.dayOfWeek},${formatMinutes(record.totalWorkMinutes)},${formatMinutes(record.morningOtMinutes)},${formatMinutes(record.eveningOtMinutes)},${formatMinutes(record.regularWorkMinutes)}\n`;
      });

      const blob = new Blob([csv], { type: 'text/csv' });
      const url = window.URL.createObjectURL(blob);
      const a = document.createElement('a');
      a.href = url;
      a.download = `ot-report-${otReport.employeeName}-${startDate}-${endDate}.csv`;
      a.click();
    }
  };

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-800 flex items-center">
          <FileText className="mr-3 text-blue-600" />
          Employee Reports
        </h1>
      </div>

      {/* Report Configuration */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-xl font-semibold mb-4">Generate Report</h2>

        <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-4 gap-4">
          {/* Report Type */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Report Type
            </label>
            <select
              value={reportType}
              onChange={(e) => setReportType(e.target.value as 'time' | 'ot')}
              className="input w-full"
            >
              <option value="time">Time Tracking Report</option>
              <option value="ot">OT Tracking Report</option>
            </select>
          </div>

          {/* Employee Selection */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Employee
            </label>
            <select
              value={selectedEmployee || ''}
              onChange={(e) => setSelectedEmployee(Number(e.target.value))}
              className="input w-full"
            >
              <option value="">Select Employee</option>
              {employees.map(emp => (
                <option key={emp.id} value={emp.id}>
                  {emp.fullName} ({emp.email})
                </option>
              ))}
            </select>
          </div>

          {/* Start Date */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              Start Date
            </label>
            <input
              type="date"
              value={startDate}
              onChange={(e) => setStartDate(e.target.value)}
              className="input w-full"
            />
          </div>

          {/* End Date */}
          <div>
            <label className="block text-sm font-medium text-gray-700 mb-2">
              End Date
            </label>
            <input
              type="date"
              value={endDate}
              onChange={(e) => setEndDate(e.target.value)}
              className="input w-full"
            />
          </div>
        </div>

        <div className="flex gap-3 mt-4">
          <button
            onClick={generateReport}
            disabled={loading || !selectedEmployee}
            className="btn-primary flex items-center"
          >
            <TrendingUp className="w-4 h-4 mr-2" />
            {loading ? 'Generating...' : 'Generate Report'}
          </button>

          {(timeReport || otReport) && (
            <button
              onClick={exportToCSV}
              className="btn-secondary flex items-center"
            >
              <Download className="w-4 h-4 mr-2" />
              Export CSV
            </button>
          )}
        </div>

        {error && <ErrorMessage message={error} />}
      </div>

      {/* Loading State */}
      {loading && (
        <div className="flex justify-center py-12">
          <LoadingSpinner />
        </div>
      )}

      {/* Time Tracking Report Display */}
      {!loading && timeReport && (
        <div className="space-y-6">
          {/* Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-4 gap-4">
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-blue-600 font-medium">Total Work Time</p>
                  <p className="text-2xl font-bold text-blue-800">
                    {formatMinutes(timeReport.totalWorkMinutes)}
                  </p>
                </div>
                <Clock className="text-blue-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-orange-50 border border-orange-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-orange-600 font-medium">Morning OT</p>
                  <p className="text-2xl font-bold text-orange-800">
                    {formatMinutes(timeReport.totalMorningOtMinutes)}
                  </p>
                </div>
                <Calendar className="text-orange-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-purple-600 font-medium">Evening OT</p>
                  <p className="text-2xl font-bold text-purple-800">
                    {formatMinutes(timeReport.totalEveningOtMinutes)}
                  </p>
                </div>
                <Calendar className="text-purple-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-green-600 font-medium">Total OT</p>
                  <p className="text-2xl font-bold text-green-800">
                    {formatMinutes(timeReport.totalOtMinutes)}
                  </p>
                </div>
                <TrendingUp className="text-green-600 w-8 h-8" />
              </div>
            </div>
          </div>

          {/* Daily Records Table */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold">Daily Time Records</h3>
              <p className="text-sm text-gray-600">
                {timeReport.employeeName} ({timeReport.employeeEmail})
              </p>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Start Time</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">End Time</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total Work</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Morning OT</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Evening OT</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Job Cards</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {timeReport.dailyRecords.map((record, index) => (
                    <tr key={index} className={!record.dayStarted ? 'bg-gray-50' : ''}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{record.date}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">
                        {record.dayStartTime ? new Date(record.dayStartTime).toLocaleTimeString() : '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">
                        {record.dayEndTime ? new Date(record.dayEndTime).toLocaleTimeString() : '-'}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        {formatMinutes(record.totalWorkMinutes)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-orange-600">
                        {formatMinutes(record.morningOtMinutes)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-purple-600">
                        {formatMinutes(record.eveningOtMinutes)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">
                        {record.jobCards.length} cards
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* OT Tracking Report Display */}
      {!loading && otReport && (
        <div className="space-y-6">
          {/* Summary Cards */}
          <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-5 gap-4">
            <div className="bg-blue-50 border border-blue-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-blue-600 font-medium">Days Worked</p>
                  <p className="text-2xl font-bold text-blue-800">{otReport.totalDaysWorked}</p>
                </div>
                <Calendar className="text-blue-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-green-50 border border-green-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-green-600 font-medium">Total Work</p>
                  <p className="text-xl font-bold text-green-800">
                    {formatMinutes(otReport.totalWorkMinutes)}
                  </p>
                </div>
                <Clock className="text-green-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-orange-50 border border-orange-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-orange-600 font-medium">Morning OT</p>
                  <p className="text-xl font-bold text-orange-800">
                    {formatMinutes(otReport.totalMorningOtMinutes)}
                  </p>
                </div>
                <TrendingUp className="text-orange-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-purple-50 border border-purple-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-purple-600 font-medium">Evening OT</p>
                  <p className="text-xl font-bold text-purple-800">
                    {formatMinutes(otReport.totalEveningOtMinutes)}
                  </p>
                </div>
                <TrendingUp className="text-purple-600 w-8 h-8" />
              </div>
            </div>

            <div className="bg-indigo-50 border border-indigo-200 rounded-lg p-4">
              <div className="flex items-center justify-between">
                <div>
                  <p className="text-sm text-indigo-600 font-medium">Total OT</p>
                  <p className="text-xl font-bold text-indigo-800">
                    {formatMinutes(otReport.totalOtMinutes)}
                  </p>
                </div>
                <TrendingUp className="text-indigo-600 w-8 h-8" />
              </div>
            </div>
          </div>

          {/* Performance Analysis */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold mb-4 flex items-center">
              <Users className="w-5 h-5 mr-2 text-blue-600" />
              Performance Analysis
            </h3>
            <div className="grid grid-cols-1 md:grid-cols-2 lg:grid-cols-3 gap-4">
              <div className="border rounded-lg p-4">
                <p className="text-sm text-gray-600">Avg Work/Day</p>
                <p className="text-xl font-bold text-gray-800">
                  {formatMinutes(Math.round(otReport.performanceAnalysis.averageWorkMinutesPerDay))}
                </p>
              </div>
              <div className="border rounded-lg p-4">
                <p className="text-sm text-gray-600">Avg OT/Day</p>
                <p className="text-xl font-bold text-gray-800">
                  {formatMinutes(Math.round(otReport.performanceAnalysis.averageOTMinutesPerDay))}
                </p>
              </div>
              <div className="border rounded-lg p-4">
                <p className="text-sm text-gray-600">Completion Rate</p>
                <p className="text-xl font-bold text-green-600">
                  {otReport.performanceAnalysis.jobCompletionRate.toFixed(1)}%
                </p>
              </div>
              <div className="border rounded-lg p-4">
                <p className="text-sm text-gray-600">Jobs Completed</p>
                <p className="text-xl font-bold text-blue-600">
                  {otReport.performanceAnalysis.totalJobsCompleted}
                </p>
              </div>
              <div className="border rounded-lg p-4">
                <p className="text-sm text-gray-600">Jobs In Progress</p>
                <p className="text-xl font-bold text-orange-600">
                  {otReport.performanceAnalysis.totalJobsInProgress}
                </p>
              </div>
              <div className="border rounded-lg p-4">
                <p className="text-sm text-gray-600">Most Worked Type</p>
                <p className="text-lg font-bold text-purple-600">
                  {otReport.performanceAnalysis.mostWorkedJobType}
                </p>
                <p className="text-sm text-gray-500">
                  {formatMinutes(otReport.performanceAnalysis.mostWorkedJobTypeMinutes)}
                </p>
              </div>
            </div>
          </div>

          {/* Work by Job Type */}
          <div className="bg-white rounded-lg shadow-md p-6">
            <h3 className="text-lg font-semibold mb-4">Work Distribution by Job Type</h3>
            <div className="grid grid-cols-1 md:grid-cols-5 gap-4">
              {Object.entries(otReport.workMinutesByJobType).map(([type, minutes]) => (
                <div key={type} className="border rounded-lg p-4 text-center">
                  <p className="text-sm font-medium text-gray-600">{type}</p>
                  <p className="text-xl font-bold text-blue-600">{formatMinutes(minutes)}</p>
                </div>
              ))}
            </div>
          </div>

          {/* Daily OT Records */}
          <div className="bg-white rounded-lg shadow-md overflow-hidden">
            <div className="px-6 py-4 border-b border-gray-200">
              <h3 className="text-lg font-semibold">Daily OT Breakdown</h3>
              <p className="text-sm text-gray-600">
                {otReport.employeeName} ({otReport.employeeEmail})
              </p>
            </div>
            <div className="overflow-x-auto">
              <table className="min-w-full divide-y divide-gray-200">
                <thead className="bg-gray-50">
                  <tr>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Date</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Day</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Total Work</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Regular</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Morning OT</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Evening OT</th>
                    <th className="px-6 py-3 text-left text-xs font-medium text-gray-500 uppercase">Generators</th>
                  </tr>
                </thead>
                <tbody className="bg-white divide-y divide-gray-200">
                  {otReport.dailyOTRecords.map((record, index) => (
                    <tr key={index}>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{record.date}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">{record.dayOfWeek}</td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm font-medium">
                        {formatMinutes(record.totalWorkMinutes)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm">
                        {formatMinutes(record.regularWorkMinutes)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-orange-600">
                        {formatMinutes(record.morningOtMinutes)}
                      </td>
                      <td className="px-6 py-4 whitespace-nowrap text-sm text-purple-600">
                        {formatMinutes(record.eveningOtMinutes)}
                      </td>
                      <td className="px-6 py-4 text-sm">
                        {record.generatorWorks.map((gw, i) => (
                          <div key={i} className="text-xs">
                            {gw.generatorName} ({gw.jobCardType}): {formatMinutes(gw.workMinutes)}
                          </div>
                        ))}
                      </td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
        </div>
      )}

      {/* No Report Generated Yet */}
      {!loading && !timeReport && !otReport && (
        <div className="bg-white rounded-lg shadow-md p-12 text-center">
          <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <h3 className="text-lg font-medium text-gray-700 mb-2">No Report Generated</h3>
          <p className="text-gray-500">Select an employee and date range, then click Generate Report</p>
        </div>
      )}
    </div>
  );
}
