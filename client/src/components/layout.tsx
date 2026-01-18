import { Link, useLocation } from "wouter";
import { useStore } from "@/lib/store";
import { cn } from "@/lib/utils";
import { LayoutGrid, Settings, Smartphone } from "lucide-react";

export default function Layout({ children }: { children: React.ReactNode }) {
  const [location] = useLocation();

  return (
    <div className="min-h-screen bg-background text-foreground flex justify-center bg-gray-100 dark:bg-neutral-900">
      {/* Mobile Container Simulator */}
      <div className="w-full max-w-md bg-background min-h-screen shadow-2xl overflow-hidden relative flex flex-col">
        
        {/* Status Bar Shim */}
        <div className="h-8 bg-surface-container/50 w-full shrink-0" />

        {/* Main Content Area */}
        <main className="flex-1 overflow-y-auto pb-20 scrollbar-hide">
          {children}
        </main>

        {/* Bottom Navigation */}
        <nav className="h-20 bg-surface-container border-t border-outline-variant flex items-center justify-around px-4 pb-2 shrink-0 z-50">
          <Link href="/" className={cn(
              "flex flex-col items-center justify-center w-16 h-full gap-1 transition-colors cursor-pointer",
              location === "/" ? "text-primary" : "text-outline"
            )}>
              <div className={cn(
                "px-5 py-1 rounded-full transition-colors",
                location === "/" ? "bg-primary/20" : "bg-transparent"
              )}>
                <LayoutGrid size={24} strokeWidth={location === "/" ? 2.5 : 2} />
              </div>
              <span className="text-xs font-medium">Counters</span>
          </Link>

          <Link href="/widget" className={cn(
              "flex flex-col items-center justify-center w-16 h-full gap-1 transition-colors cursor-pointer",
              location === "/widget" ? "text-primary" : "text-outline"
            )}>
              <div className={cn(
                "px-5 py-1 rounded-full transition-colors",
                location === "/widget" ? "bg-primary/20" : "bg-transparent"
              )}>
                <Smartphone size={24} strokeWidth={location === "/widget" ? 2.5 : 2} />
              </div>
              <span className="text-xs font-medium">Widget</span>
          </Link>

          <Link href="/settings" className={cn(
              "flex flex-col items-center justify-center w-16 h-full gap-1 transition-colors cursor-pointer",
              location === "/settings" ? "text-primary" : "text-outline"
            )}>
              <div className={cn(
                "px-5 py-1 rounded-full transition-colors",
                location === "/settings" ? "bg-primary/20" : "bg-transparent"
              )}>
                <Settings size={24} strokeWidth={location === "/settings" ? 2.5 : 2} />
              </div>
              <span className="text-xs font-medium">Settings</span>
          </Link>
        </nav>
      </div>
    </div>
  );
}
