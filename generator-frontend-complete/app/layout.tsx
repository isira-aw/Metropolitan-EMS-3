import type { Metadata } from 'next';
import './globals.css';

export const metadata: Metadata = {
  title: 'Generator Management System',
  description: 'Complete working system with all endpoints',
};

export default function RootLayout({ children }: { children: React.ReactNode }) {
  return (
    <html lang="en">
      <body>{children}</body>
    </html>
  );
}
