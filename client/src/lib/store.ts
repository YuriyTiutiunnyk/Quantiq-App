import { create } from 'zustand';
import { persist } from 'zustand/middleware';
import { Counter } from './types';
import { v4 as uuidv4 } from 'uuid';

interface AppState {
  counters: Counter[];
  isPro: boolean;
  theme: 'light' | 'dark';
  
  // Actions
  addCounter: (title: string, step?: number) => void;
  removeCounter: (id: string) => void;
  updateCounter: (id: string, updates: Partial<Counter>) => void;
  increment: (id: string) => void;
  decrement: (id: string) => void;
  reset: (id: string) => void;
  togglePro: () => void;
  toggleTheme: () => void;
}

const COLORS = [
  'bg-red-200 text-red-900',
  'bg-blue-200 text-blue-900',
  'bg-green-200 text-green-900',
  'bg-yellow-200 text-yellow-900',
  'bg-purple-200 text-purple-900',
  'bg-pink-200 text-pink-900',
  'bg-indigo-200 text-indigo-900',
  'bg-orange-200 text-orange-900',
];

export const useStore = create<AppState>()(
  persist(
    (set) => ({
      counters: [
        { id: '1', title: 'Water Cups', value: 0, step: 1, color: COLORS[1] },
        { id: '2', title: 'Pushups', value: 0, step: 5, color: COLORS[0] }
      ],
      isPro: false,
      theme: 'light',

      addCounter: (title, step = 1) => set((state) => {
        const randomColor = COLORS[Math.floor(Math.random() * COLORS.length)];
        return {
          counters: [
            ...state.counters,
            { 
              id: uuidv4(), 
              title: title || 'New Counter', 
              value: 0, 
              step,
              color: randomColor
            }
          ]
        };
      }),

      removeCounter: (id) => set((state) => ({
        counters: state.counters.filter((c) => c.id !== id)
      })),

      updateCounter: (id, updates) => set((state) => ({
        counters: state.counters.map((c) => 
          c.id === id ? { ...c, ...updates } : c
        )
      })),

      increment: (id) => set((state) => ({
        counters: state.counters.map((c) => 
          c.id === id ? { ...c, value: c.value + c.step } : c
        )
      })),

      decrement: (id) => set((state) => ({
        counters: state.counters.map((c) => 
          c.id === id ? { ...c, value: c.value - c.step } : c
        )
      })),

      reset: (id) => set((state) => ({
        counters: state.counters.map((c) => 
          c.id === id ? { ...c, value: 0 } : c
        )
      })),

      togglePro: () => set((state) => ({ isPro: !state.isPro })),
      
      toggleTheme: () => set((state) => {
        const newTheme = state.theme === 'light' ? 'dark' : 'light';
        if (typeof window !== 'undefined') {
          document.documentElement.classList.toggle('dark', newTheme === 'dark');
        }
        return { theme: newTheme };
      }),
    }),
    {
      name: 'quantiq-storage',
    }
  )
);
