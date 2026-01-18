import { Link, useLocation } from "wouter";
import { useStore } from "@/lib/store";
import { cn } from "@/lib/utils";
import { LayoutGrid, Settings, Smartphone } from "lucide-react";

export default function Layout({ children }: { children: React.ReactNode }) {
  const [location] = useLocation();

  return (
    <div className="min-h-screen bg-neutral-100 dark:bg-neutral-900 flex justify-center text-foreground font-sans">
      {/* Mobile Container Simulator */}
      <div className="w-full max-w-md bg-background min-h-screen shadow-2xl overflow-hidden relative flex flex-col border-x border-outline-variant/20">
        
        {/* Status Bar Shim */}
        <div className="h-8 bg-surface w-full shrink-0 flex items-center justify-end px-4 gap-2">
            <div className="w-4 h-4 rounded-full bg-current opacity-20"></div>
            <div className="w-4 h-4 rounded-full bg-current opacity-20"></div>
        </div>

        {/* Main Content Area */}
        <main className="flex-1 overflow-y-auto pb-20 scrollbar-hide">
          {children}
        </main>

        {/* M3 Navigation Bar */}
        <nav className="h-20 bg-surface-container border-t border-outline-variant flex items-center justify-around px-4 pb-4 shrink-0 z-50">
          <Link href="/" className="group flex flex-col items-center gap-1 w-16 cursor-pointer">
              <div className={cn(
                "px-5 py-1 rounded-full transition-all duration-300",
                location === "/" ? "bg-primary text-primary-foreground" : "text-on-surface-variant group-hover:bg-surface-variant"
              )}>
                <LayoutGrid size={24} strokeWidth={location === "/" ? 2.5 : 2} />
              </div>
              <span className={cn("text-xs font-medium transition-colors", location === "/" ? "text-on-surface" : "text-on-surface-variant")}>Counters</span>
          </Link>

          <Link href="/widget" className="group flex flex-col items-center gap-1 w-16 cursor-pointer">
              <div className={cn(
                "px-5 py-1 rounded-full transition-all duration-300",
                location === "/widget" ? "bg-primary text-primary-foreground" : "text-on-surface-variant group-hover:bg-surface-variant"
              )}>
                <Smartphone size={24} strokeWidth={location === "/widget" ? 2.5 : 2} />
              </div>
              <span className={cn("text-xs font-medium transition-colors", location === "/widget" ? "text-on-surface" : "text-on-surface-variant")}>Widget</span>
          </Link>

          <Link href="/settings" className="group flex flex-col items-center gap-1 w-16 cursor-pointer">
              <div className={cn(
                "px-5 py-1 rounded-full transition-all duration-300",
                location === "/settings" ? "bg-primary text-primary-foreground" : "text-on-surface-variant group-hover:bg-surface-variant"
              )}>
                <Settings size={24} strokeWidth={location === "/settings" ? 2.5 : 2} />
              </div>
              <span className={cn("text-xs font-medium transition-colors", location === "/settings" ? "text-on-surface" : "text-on-surface-variant")}>Settings</span>
          </Link>
        </nav>
      </div>
    </div>
  );
}
