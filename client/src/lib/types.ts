export interface Counter {
  id: string;
  title: string;
  value: number;
  step: number;
  color: string; // For visual distinction
}

export type ThemeMode = 'light' | 'dark' | 'system';
