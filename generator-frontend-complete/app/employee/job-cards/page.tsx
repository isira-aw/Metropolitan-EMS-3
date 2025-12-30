'use client';
import { useEffect, useState } from 'react';
import { api } from '@/lib/api';
import { JobCard, JobStatus, UpdateStatusRequest, DayStatus, ImageUploadRequest } from '@/types/jobCard';
import { FileText, MapPin, Clock, Camera, CheckCircle, PlayCircle, Pause, XCircle, ArrowRight, Upload } from 'lucide-react';
import LoadingSpinner from '@/components/LoadingSpinner';
import ErrorMessage from '@/components/ErrorMessage';

export default function JobCardsPage() {
  const [jobCards, setJobCards] = useState<JobCard[]>([]);
  const [dayStatus, setDayStatus] = useState<DayStatus | null>(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [selectedJobCard, setSelectedJobCard] = useState<JobCard | null>(null);
  const [showStatusModal, setShowStatusModal] = useState(false);
  const [showImageModal, setShowImageModal] = useState(false);
  const [selectedStatus, setSelectedStatus] = useState<JobStatus | null>(null);
  const [uploadingImage, setUploadingImage] = useState(false);

  useEffect(() => {
    loadJobCards();
    loadDayStatus();
  }, []);

  const loadJobCards = async () => {
    try {
      const response = await api.get<{ content: JobCard[] }>('/employee/job-cards?size=50');
      setJobCards(response.content);
    } catch (err: any) {
      setError(err.message || 'Failed to load job cards');
    } finally {
      setLoading(false);
    }
  };

  const loadDayStatus = async () => {
    try {
      const status = await api.get<DayStatus>('/employee/day/status');
      setDayStatus(status);
    } catch (err: any) {
      console.error('Failed to load day status:', err);
    }
  };

  const handleStartDay = async () => {
    try {
      const status = await api.post<DayStatus>('/employee/day/start');
      setDayStatus(status);
      alert('Day started successfully!');
    } catch (err: any) {
      alert(err.message || 'Failed to start day');
    }
  };

  const handleEndDay = async () => {
    if (!confirm('Are you sure you want to end your day? You cannot update job statuses after ending the day.')) {
      return;
    }
    try {
      const status = await api.post<DayStatus>('/employee/day/end');
      setDayStatus(status);
      alert('Day ended successfully!');
    } catch (err: any) {
      alert(err.message || 'Failed to end day');
    }
  };

  const getLocation = (): Promise<{ latitude: number; longitude: number }> => {
    return new Promise((resolve, reject) => {
      if (!navigator.geolocation) {
        reject(new Error('Geolocation is not supported by your browser'));
        return;
      }

      navigator.geolocation.getCurrentPosition(
        (position) => {
          resolve({
            latitude: position.coords.latitude,
            longitude: position.coords.longitude
          });
        },
        (error) => {
          reject(new Error('Please enable location access to update status'));
        }
      );
    });
  };

  const handleUpdateStatus = async () => {
    if (!selectedJobCard || !selectedStatus) return;

    try {
      const location = await getLocation();

      const request: UpdateStatusRequest = {
        newStatus: selectedStatus,
        latitude: location.latitude,
        longitude: location.longitude
      };

      await api.post(`/employee/job-cards/${selectedJobCard.id}/status`, request);
      setShowStatusModal(false);
      setSelectedJobCard(null);
      setSelectedStatus(null);
      loadJobCards();
      alert('Status updated successfully!');
    } catch (err: any) {
      alert(err.message || 'Failed to update status');
    }
  };

  const handleImageUpload = async (e: React.FormEvent) => {
    e.preventDefault();
    if (!selectedJobCard) return;

    const fileInput = document.getElementById('imageFile') as HTMLInputElement;
    if (!fileInput.files || !fileInput.files[0]) {
      alert('Please select an image');
      return;
    }

    const file = fileInput.files[0];
    const reader = new FileReader();

    reader.onloadend = async () => {
      try {
        setUploadingImage(true);
        const base64 = reader.result as string;

        const request: ImageUploadRequest = {
          imageData: base64,
          fileName: file.name,
          contentType: file.type
        };

        await api.post(`/employee/job-cards/${selectedJobCard.id}/image`, request);
        setShowImageModal(false);
        setSelectedJobCard(null);
        loadJobCards();
        alert('Image uploaded successfully!');
      } catch (err: any) {
        alert(err.message || 'Failed to upload image');
      } finally {
        setUploadingImage(false);
      }
    };

    reader.readAsDataURL(file);
  };

  const getStatusIcon = (status: JobStatus) => {
    switch (status) {
      case 'PENDING': return <Clock className="w-4 h-4" />;
      case 'TRAVELING': return <ArrowRight className="w-4 h-4" />;
      case 'STARTED': return <PlayCircle className="w-4 h-4" />;
      case 'ON_HOLD': return <Pause className="w-4 h-4" />;
      case 'COMPLETED': return <CheckCircle className="w-4 h-4" />;
      case 'CANCEL': return <XCircle className="w-4 h-4" />;
    }
  };

  const getStatusColor = (status: JobStatus) => {
    switch (status) {
      case 'PENDING': return 'bg-gray-100 text-gray-800';
      case 'TRAVELING': return 'bg-blue-100 text-blue-800';
      case 'STARTED': return 'bg-green-100 text-green-800';
      case 'ON_HOLD': return 'bg-orange-100 text-orange-800';
      case 'COMPLETED': return 'bg-purple-100 text-purple-800';
      case 'CANCEL': return 'bg-red-100 text-red-800';
    }
  };

  const getAvailableStatusTransitions = (currentStatus: JobStatus): JobStatus[] => {
    switch (currentStatus) {
      case 'PENDING': return ['TRAVELING', 'CANCEL'];
      case 'TRAVELING': return ['STARTED', 'ON_HOLD', 'CANCEL'];
      case 'STARTED': return ['ON_HOLD', 'COMPLETED', 'CANCEL'];
      case 'ON_HOLD': return ['STARTED', 'CANCEL'];
      default: return [];
    }
  };

  if (loading) {
    return (
      <div className="flex justify-center py-12">
        <LoadingSpinner />
      </div>
    );
  }

  return (
    <div className="space-y-6">
      <div className="flex justify-between items-center">
        <h1 className="text-3xl font-bold text-gray-800 flex items-center">
          <FileText className="mr-3 text-blue-600" />
          My Job Cards
        </h1>
      </div>

      {error && <ErrorMessage message={error} />}

      {/* Day Status */}
      <div className="bg-white rounded-lg shadow-md p-6">
        <h2 className="text-lg font-semibold mb-4">Day Status</h2>
        {dayStatus ? (
          <div className="flex items-center justify-between">
            <div className="space-y-2">
              <p className="text-sm">
                <strong>Day Started:</strong>{' '}
                <span className={dayStatus.dayStarted ? 'text-green-600' : 'text-red-600'}>
                  {dayStatus.dayStarted ? `✓ ${dayStatus.dayStartTime ? new Date(dayStatus.dayStartTime).toLocaleString() : 'Yes'}` : '✗ No'}
                </span>
              </p>
              <p className="text-sm">
                <strong>Day Ended:</strong>{' '}
                <span className={dayStatus.dayEnded ? 'text-green-600' : 'text-gray-600'}>
                  {dayStatus.dayEnded ? `✓ ${dayStatus.dayEndTime ? new Date(dayStatus.dayEndTime).toLocaleString() : 'Yes'}` : '✗ No'}
                </span>
              </p>
            </div>
            <div className="flex gap-3">
              {!dayStatus.dayStarted && (
                <button onClick={handleStartDay} className="btn-primary">
                  Start Day
                </button>
              )}
              {dayStatus.dayStarted && !dayStatus.dayEnded && (
                <button onClick={handleEndDay} className="btn-danger">
                  End Day
                </button>
              )}
            </div>
          </div>
        ) : (
          <div className="text-center py-4">
            <LoadingSpinner />
          </div>
        )}

        {!dayStatus?.dayStarted && (
          <div className="mt-4 bg-yellow-50 border border-yellow-200 rounded p-3 text-sm text-yellow-800">
            ⚠️ You must start your day before updating job card statuses
          </div>
        )}

        {dayStatus?.dayEnded && (
          <div className="mt-4 bg-gray-50 border border-gray-200 rounded p-3 text-sm text-gray-800">
            ℹ️ Day has ended. You cannot update job statuses anymore
          </div>
        )}
      </div>

      {/* Job Cards List */}
      {jobCards.length === 0 ? (
        <div className="bg-white rounded-lg shadow-md text-center py-12">
          <FileText className="w-16 h-16 text-gray-400 mx-auto mb-4" />
          <p className="text-gray-500">No job cards assigned yet</p>
        </div>
      ) : (
        <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
          {jobCards.map((job) => (
            <div key={job.id} className="bg-white rounded-lg shadow-md overflow-hidden">
              <div className="p-6">
                <div className="flex justify-between items-start mb-4">
                  <div>
                    <h3 className="font-bold text-lg text-gray-800">{job.ticketTitle}</h3>
                    <p className="text-sm text-gray-600">#{job.ticketNumber}</p>
                  </div>
                  <span className={`px-3 py-1 rounded-full text-sm font-semibold flex items-center gap-2 ${getStatusColor(job.status)}`}>
                    {getStatusIcon(job.status)}
                    {job.status}
                  </span>
                </div>

                <div className="space-y-2 text-sm mb-4">
                  <p><strong>Generator:</strong> {job.generator.name} ({job.generator.model})</p>
                  {job.generator.locationName && (
                    <p className="flex items-center gap-2">
                      <MapPin className="w-4 h-4 text-gray-500" />
                      {job.generator.locationName}
                    </p>
                  )}
                  {job.workMinutes > 0 && (
                    <p className="flex items-center gap-2">
                      <Clock className="w-4 h-4 text-gray-500" />
                      Work Time: {Math.floor(job.workMinutes / 60)}h {job.workMinutes % 60}m
                    </p>
                  )}
                  {job.approved && (
                    <p className="text-green-600 font-medium">✓ Approved</p>
                  )}
                </div>

                {job.image && (
                  <div className="mb-4">
                    <img
                      src={job.image}
                      alt="Job card"
                      className="w-full h-48 object-cover rounded border"
                    />
                  </div>
                )}

                <div className="flex gap-2">
                  <button
                    onClick={() => {
                      setSelectedJobCard(job);
                      setShowStatusModal(true);
                    }}
                    disabled={!dayStatus?.dayStarted || dayStatus?.dayEnded || job.status === 'COMPLETED' || job.status === 'CANCEL'}
                    className="flex-1 btn-primary text-sm disabled:opacity-50 disabled:cursor-not-allowed"
                  >
                    Update Status
                  </button>
                  <button
                    onClick={() => {
                      setSelectedJobCard(job);
                      setShowImageModal(true);
                    }}
                    className="btn-secondary text-sm flex items-center gap-2"
                  >
                    <Camera className="w-4 h-4" />
                    {job.image ? 'Update' : 'Upload'} Image
                  </button>
                </div>
              </div>
            </div>
          ))}
        </div>
      )}

      {/* Status Update Modal */}
      {showStatusModal && selectedJobCard && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Update Status</h2>
            <p className="text-sm text-gray-600 mb-4">
              Current Status: <strong>{selectedJobCard.status}</strong>
            </p>

            <div className="space-y-3 mb-6">
              {getAvailableStatusTransitions(selectedJobCard.status).map(status => (
                <button
                  key={status}
                  onClick={() => setSelectedStatus(status)}
                  className={`w-full p-3 rounded border-2 text-left flex items-center gap-3 ${
                    selectedStatus === status
                      ? 'border-blue-600 bg-blue-50'
                      : 'border-gray-300 hover:border-blue-400'
                  }`}
                >
                  {getStatusIcon(status)}
                  <span className="font-medium">{status}</span>
                </button>
              ))}
            </div>

            <div className="bg-blue-50 border border-blue-200 rounded p-3 text-sm text-blue-800 mb-4">
              <MapPin className="w-4 h-4 inline mr-1" />
              Location will be automatically captured when you update status
            </div>

            <div className="flex gap-3">
              <button
                onClick={handleUpdateStatus}
                disabled={!selectedStatus}
                className="flex-1 btn-primary disabled:opacity-50"
              >
                Confirm Update
              </button>
              <button
                onClick={() => {
                  setShowStatusModal(false);
                  setSelectedJobCard(null);
                  setSelectedStatus(null);
                }}
                className="flex-1 btn-secondary"
              >
                Cancel
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Image Upload Modal */}
      {showImageModal && selectedJobCard && (
        <div className="fixed inset-0 bg-black bg-opacity-50 flex items-center justify-center z-50 p-4">
          <div className="bg-white rounded-lg p-6 w-full max-w-md">
            <h2 className="text-xl font-bold mb-4">Upload Image</h2>
            <p className="text-sm text-gray-600 mb-4">
              Job Card: <strong>{selectedJobCard.ticketTitle}</strong>
            </p>

            <form onSubmit={handleImageUpload} className="space-y-4">
              <div>
                <label className="block text-sm font-medium mb-2">Select Image</label>
                <input
                  type="file"
                  id="imageFile"
                  accept="image/*"
                  className="w-full border rounded p-2"
                  required
                />
                <p className="text-xs text-gray-500 mt-1">
                  Supported formats: JPG, PNG, GIF
                </p>
              </div>

              <div className="flex gap-3">
                <button
                  type="submit"
                  disabled={uploadingImage}
                  className="flex-1 btn-primary disabled:opacity-50 flex items-center justify-center gap-2"
                >
                  {uploadingImage ? (
                    <>
                      <LoadingSpinner />
                      Uploading...
                    </>
                  ) : (
                    <>
                      <Upload className="w-4 h-4" />
                      Upload
                    </>
                  )}
                </button>
                <button
                  type="button"
                  onClick={() => {
                    setShowImageModal(false);
                    setSelectedJobCard(null);
                  }}
                  className="flex-1 btn-secondary"
                  disabled={uploadingImage}
                >
                  Cancel
                </button>
              </div>
            </form>
          </div>
        </div>
      )}
    </div>
  );
}
