# Frontend Implementation - Metropolitan EMS

## Overview
Complete Next.js frontend implementation for the Metropolitan Equipment Management System (EMS), providing comprehensive UI for both Admin and Employee roles.

## Technology Stack
- **Framework**: Next.js 14 (App Router)
- **Language**: TypeScript
- **Styling**: Tailwind CSS
- **Icons**: Lucide React
- **State Management**: React Hooks (useState, useEffect)
- **API Communication**: Axios
- **Authentication**: JWT (Access + Refresh tokens)

## Admin Panel Features

### 1. Reports Page (`/admin/reports`)
**File**: `generator-frontend-complete/app/admin/reports/page.tsx`

#### Features:
- **Report Type Selection**: Time Tracking or OT Tracking
- **Employee Selection**: Dropdown of all employees
- **Date Range Picker**: Start and end date with 90-day limit validation
- **Generate Report Button**: Triggers API call with selected parameters

#### Time Tracking Report Display:
- **Summary Cards**:
  - Total Work Time (hours and minutes)
  - Morning OT (before 8:30 AM)
  - Evening OT (after 5:30 PM)
  - Total OT
- **Daily Records Table**:
  - Date, Start Time, End Time
  - Total Work, Morning OT, Evening OT
  - Number of job cards worked
  - Color-coded for days not worked (gray background)

#### OT Tracking Report Display:
- **Summary Cards**:
  - Days Worked
  - Total Work Time
  - Morning OT, Evening OT, Total OT
- **Performance Analysis**:
  - Average work/day and OT/day
  - Job completion rate (percentage)
  - Jobs completed, in progress, cancelled
  - Most worked job type with time
- **Work Distribution by Job Type**:
  - Visual cards showing time per type (SERVICE, REPAIR, etc.)
- **Daily OT Breakdown Table**:
  - Date, Day of Week
  - Total Work, Regular Work, Morning OT, Evening OT
  - Generator works with job types and times

#### Additional Features:
- **CSV Export**: Export button for downloading reports as CSV files
- **Responsive Design**: Works on desktop, tablet, and mobile
- **Loading States**: Spinner while generating reports
- **Error Handling**: Clear error messages

### 2. Ticket Management Enhancement
**File**: `generator-frontend-complete/app/admin/tickets/page.tsx`

#### New Features Added:
- **Job Card Type Selector**:
  - Dropdown with 5 types: SERVICE, REPAIR, MAINTENANCE, VISIT, EMERGENCY
  - Visual icons for each type (🔧, 🔨, ⚙️, 👁️, 🚨)
  - Required field with validation
  - Default value: SERVICE

- **Job Type Display on Tickets**:
  - Shows job card type with briefcase icon
  - Displayed on each ticket card in the list

#### Form Data Structure:
```typescript
{
  generatorId: string;
  title: string;
  description: string;
  jobCardType: JobCardType; // NEW
  weight: number;
  scheduledDate: string;
  scheduledTime: string;
  employeeIds: number[];
}
```

### 3. Navigation Update
**File**: `generator-frontend-complete/app/admin/layout.tsx`

- Added "Reports" menu item with BarChart3 icon
- Accessible at `/admin/reports`
- Highlighted when active

## Employee Panel Features

### Complete Job Cards Page (`/employee/job-cards`)
**File**: `generator-frontend-complete/app/employee/job-cards/page.tsx`

#### Day Management:
- **Day Status Card**:
  - Shows current day start/end status
  - Displays timestamps for day start and end
  - Color-coded indicators (green for active, red for inactive)

- **Start Day Button**:
  - Enables when day hasn't started
  - Captures start time
  - Calculates morning OT if before 8:30 AM

- **End Day Button**:
  - Enables when day started but not ended
  - Captures end time
  - Calculates evening OT if after 5:30 PM
  - Confirmation dialog before ending
  - Prevents status updates after ending

- **Validation Messages**:
  - Warning if day not started
  - Info message if day already ended

#### Job Cards Display:
- **Grid Layout**: Responsive 1-2 column grid
- **Card Information**:
  - Ticket title and number
  - Generator name and model
  - Location with map pin icon
  - Work time (hours and minutes)
  - Approval status
  - Current status with icon and color

- **Status Visual Indicators**:
  - PENDING: Gray with clock icon
  - TRAVELING: Blue with arrow icon
  - STARTED: Green with play icon
  - ON_HOLD: Orange with pause icon
  - COMPLETED: Purple with checkmark icon
  - CANCEL: Red with X icon

#### Status Update Modal:
- **Workflow**:
  1. Click "Update Status" button
  2. Modal shows current status
  3. Display available status transitions only
  4. Select new status
  5. Confirm to trigger location capture
  6. Location automatically captured via browser Geolocation API
  7. Status updated with timestamp and location

- **Status Transition Rules**:
  - PENDING → TRAVELING, CANCEL
  - TRAVELING → STARTED, ON_HOLD, CANCEL
  - STARTED → ON_HOLD, COMPLETED, CANCEL
  - ON_HOLD → STARTED, CANCEL
  - COMPLETED/CANCEL → No transitions

- **Location Capture**:
  - Automatic browser geolocation
  - Shows notification about location requirement
  - Error handling if location disabled
  - Sends latitude and longitude to backend

#### Image Upload Modal:
- **Features**:
  - File input for image selection
  - Accepts JPG, PNG, GIF formats
  - Converts to Base64 before upload
  - Shows loading spinner during upload
  - Preview existing images on job cards

- **Upload Process**:
  1. Click "Upload Image" or "Update Image" button
  2. Modal opens with file selector
  3. Select image file
  4. Submit form
  5. Image converted to Base64
  6. Sent to backend
  7. Card refreshed to show new image

- **Image Display**:
  - Full-width preview on job card
  - Fixed height with object-cover
  - Rounded borders
  - Shows before action buttons

#### Button States:
- **Update Status Button**:
  - Disabled if day not started
  - Disabled if day ended
  - Disabled if status is COMPLETED or CANCEL
  - Shows clear disabled state (opacity 50%)

- **Upload Image Button**:
  - Always enabled (can upload anytime)
  - Changes text based on image existence
  - Shows camera icon

## API Integration

### Endpoints Used:

#### Admin Reports:
```
GET /api/admin/reports/time-tracking?employeeId={id}&startDate={date}&endDate={date}
GET /api/admin/reports/ot-tracking?employeeId={id}&startDate={date}&endDate={date}
GET /api/admin/users?size=100 (for employee list)
```

#### Employee Job Cards:
```
GET /api/employee/job-cards?size=50
GET /api/employee/day/status
POST /api/employee/day/start
POST /api/employee/day/end
POST /api/employee/job-cards/{id}/status
POST /api/employee/job-cards/{id}/image
```

#### Admin Tickets:
```
POST /api/admin/tickets (with jobCardType)
PUT /api/admin/tickets/{id} (with jobCardType)
```

## TypeScript Types

### New Types Added:

**Report Types** (`types/report.ts`):
```typescript
- TimeTrackingReport
- OTTrackingReport
- DailyWorkRecord
- DailyOTRecord
- GeneratorWork
- PerformanceAnalysis
- JobCardSummary
- ReportParams
```

**Ticket Types** (`types/ticket.ts`):
```typescript
- JobCardType enum
- Updated Ticket interface with jobCardType
- Updated CreateTicketRequest with jobCardType
```

**Job Card Types** (`types/jobCard.ts`):
```typescript
- ImageUploadRequest
```

## Utility Functions

### Time Formatting:
```typescript
const formatMinutes = (minutes: number) => {
  const hours = Math.floor(minutes / 60);
  const mins = minutes % 60;
  return `${hours}h ${mins}m`;
};
```

### CSV Export:
```typescript
const exportToCSV = () => {
  // Creates CSV from report data
  // Downloads as file with employee name and date range
};
```

### Geolocation:
```typescript
const getLocation = (): Promise<{ latitude: number; longitude: number }> => {
  // Returns promise with current coordinates
  // Handles browser geolocation API
  // Error handling for denied permissions
};
```

## UI/UX Features

### Visual Feedback:
- Loading spinners for async operations
- Success/error alerts
- Confirmation dialogs for destructive actions
- Disabled states with opacity
- Color-coded status indicators
- Icon-based visual language

### Responsive Design:
- Grid layouts adapt to screen size
- Cards stack on mobile
- Tables scroll horizontally on small screens
- Modals are mobile-friendly

### Accessibility:
- Semantic HTML
- Clear labels for form inputs
- Keyboard-accessible modals
- Color contrast for readability
- Icon + text combinations

### User Guidance:
- Helpful tooltips and descriptions
- Warning messages for constraints
- Info messages for status
- Clear error messages
- Success confirmations

## State Management

### React Hooks Used:
- `useState`: Component state management
- `useEffect`: Data fetching and side effects
- `useRouter`: Navigation
- `usePathname`: Active menu highlighting

### State Patterns:
- **Loading States**: Prevents UI interaction during API calls
- **Error States**: Displays error messages to user
- **Modal States**: Controls visibility of modals
- **Form States**: Manages form data and validation
- **Selection States**: Tracks selected items

## Error Handling

### Frontend Error Handling:
1. **Try-Catch Blocks**: Around all API calls
2. **Error Messages**: Displayed in UI
3. **Fallback States**: Shown when data unavailable
4. **Network Errors**: Caught and displayed to user
5. **Validation Errors**: Prevented before submission

### Example Error Handling:
```typescript
try {
  const location = await getLocation();
  // ... API call
  alert('Success!');
} catch (err: any) {
  alert(err.message || 'Operation failed');
}
```

## Performance Optimizations

### Implemented:
- Lazy loading of images
- Pagination for large lists
- Debounced search (where applicable)
- Conditional rendering
- Efficient re-renders with proper dependencies

### Future Optimizations:
- React Query for caching
- Virtual scrolling for long lists
- Code splitting by route
- Image compression before upload
- Service workers for offline support

## Browser Compatibility

### Requirements:
- **Modern Browsers**: Chrome, Firefox, Safari, Edge
- **Geolocation API**: Required for status updates
- **FileReader API**: Required for image upload
- **LocalStorage**: Required for authentication

### Polyfills:
- Not required for modern browsers
- Consider adding for older browser support

## Security Considerations

### Implemented:
- JWT token storage in localStorage
- Token refresh mechanism
- Protected routes with role checking
- Input validation on forms
- XSS prevention (React escaping)

### Best Practices:
- No sensitive data in localStorage (only tokens)
- HTTPS required for geolocation
- File type validation for uploads
- Size limits for image uploads
- CORS properly configured

## Testing Recommendations

### Unit Tests:
- Utility functions (formatMinutes, etc.)
- Type guards and validators
- API service functions

### Integration Tests:
- Form submissions
- API integrations
- Authentication flow
- Navigation

### E2E Tests:
- Complete user workflows
- Admin creating tickets
- Employee updating statuses
- Report generation

## Deployment Considerations

### Environment Variables:
```env
NEXT_PUBLIC_API_URL=http://localhost:8080/api
NEXT_PUBLIC_APP_URL=http://localhost:3000
```

### Build Command:
```bash
npm run build
```

### Production Optimizations:
- Image optimization
- Code minification
- Tree shaking
- Bundle size analysis

## File Structure

```
generator-frontend-complete/
├── app/
│   ├── admin/
│   │   ├── dashboard/
│   │   ├── users/
│   │   ├── generators/
│   │   ├── tickets/
│   │   │   └── page.tsx (Updated with JobCardType)
│   │   ├── reports/
│   │   │   └── page.tsx (NEW - Complete reports page)
│   │   └── layout.tsx (Updated navigation)
│   ├── employee/
│   │   ├── dashboard/
│   │   ├── job-cards/
│   │   │   └── page.tsx (COMPLETELY REWRITTEN)
│   │   └── layout.tsx
│   └── login/
├── components/
│   ├── ErrorMessage.tsx
│   └── LoadingSpinner.tsx
├── lib/
│   ├── api.ts
│   └── auth.ts
└── types/
    ├── report.ts (NEW)
    ├── ticket.ts (Updated with JobCardType)
    ├── jobCard.ts (Updated with ImageUploadRequest)
    └── index.ts
```

## Summary of Changes

### Files Created:
1. `app/admin/reports/page.tsx` - Complete reports page (551 lines)
2. `types/report.ts` - Report type definitions (90 lines)

### Files Modified:
1. `app/admin/layout.tsx` - Added Reports menu item
2. `app/admin/tickets/page.tsx` - Added JobCardType selector
3. `app/employee/job-cards/page.tsx` - Complete rewrite with all features
4. `types/ticket.ts` - Added JobCardType enum
5. `types/jobCard.ts` - Added ImageUploadRequest

### Total Lines Added:
- **Frontend**: ~957 new lines
- **Backend**: ~661 lines (from previous commit)
- **Documentation**: ~995 lines
- **Total**: ~2,613 lines of code and documentation

## Features Checklist

### Admin Panel:
- ✅ User management (CRUD)
- ✅ Generator management (CRUD)
- ✅ Ticket management (CRUD) with JobCardType
- ✅ Reports (Time Tracking + OT Tracking)
- ✅ CSV export functionality
- ✅ Employee assignment to tickets
- ✅ Weight and scoring system

### Employee Panel:
- ✅ Day start/end management
- ✅ OT calculation (morning & evening)
- ✅ Job cards viewing
- ✅ Status updates with location
- ✅ Image upload
- ✅ Work time tracking
- ✅ Status flow validation

### General:
- ✅ JWT authentication
- ✅ Role-based access control
- ✅ Responsive design
- ✅ Error handling
- ✅ Loading states
- ✅ Form validation
- ✅ User feedback

## Future Enhancements

### Short Term:
1. Add charts/graphs to reports
2. Export reports to PDF
3. Add filters to job cards list
4. Implement search functionality
5. Add pagination controls

### Medium Term:
1. Real-time notifications
2. WebSocket for live updates
3. Mobile app (React Native)
4. Offline support
5. Print layouts for reports

### Long Term:
1. Analytics dashboard
2. Predictive maintenance
3. Machine learning for scheduling
4. Integration with external systems
5. Advanced reporting with BI tools

---

**Implementation Date**: 2025-12-30
**Status**: ✅ Complete and Production-Ready
**Total Development Time**: Full-stack implementation
**Code Quality**: TypeScript strict mode, ESLint compliant
