import React from 'react';

interface RootLayoutProps {
  children: React.ReactNode
}

export default function RootLayout(props: RootLayoutProps) {
  return (
    <html lang="ru">
      <body>{props.children}</body>
    </html>
  )
}