import { useStore } from "@/lib/store";
import { Crown, Moon, Sun, Check, FileJson, Upload, Download } from "lucide-react";
import { Switch } from "@/components/ui/switch";
import { Button } from "@/components/ui/button";

export default function SettingsPage() {
  const { isPro, togglePro, theme, toggleTheme } = useStore();

  const handleExport = () => {
    if (!isPro) {
        alert("Export is a PRO feature");
        return;
    }
    const data = JSON.stringify(localStorage.getItem('quantiq-storage'));
    const blob = new Blob([data], { type: "application/json" });
    const url = URL.createObjectURL(blob);
    const link = document.createElement("a");
    link.href = url;
    link.download = `quantiq-backup-${new Date().toISOString().split('T')[0]}.json`;
    link.click();
  };

  const handleImport = () => {
     if (!isPro) {
        alert("Import is a PRO feature");
        return;
    }
    document.getElementById('import-file')?.click();
  };

  return (
    <div className="p-6 space-y-8 pb-24">
      <div>
        <h1 className="text-3xl font-normal mb-6">Settings</h1>
        
        <div className="space-y-6">
          {/* Theme Section */}
          <section className="space-y-4">
            <h3 className="text-sm font-medium text-muted-foreground uppercase tracking-wider">Appearance</h3>
            <div className="bg-surface-container rounded-xl p-4 flex items-center justify-between">
              <div className="flex items-center gap-3">
                {theme === 'dark' ? <Moon size={20} /> : <Sun size={20} />}
                <span>Dark Mode</span>
              </div>
              <Switch checked={theme === 'dark'} onCheckedChange={toggleTheme} />
            </div>
          </section>

          {/* Data Management Section (New) */}
          <section className="space-y-4">
            <h3 className="text-sm font-medium text-muted-foreground uppercase tracking-wider">Data</h3>
            <div className="bg-surface-container rounded-xl p-2 space-y-1">
               <button 
                onClick={handleExport}
                className="w-full flex items-center gap-3 p-3 hover:bg-surface-variant rounded-lg transition-colors text-left disabled:opacity-50"
                disabled={!isPro}
               >
                 <Download size={20} className="text-primary" />
                 <div className="flex-1">
                   <div className="font-medium flex items-center gap-2">
                     Export Backup
                     {!isPro && <Crown size={12} className="text-muted-foreground" />}
                   </div>
                   <p className="text-xs text-muted-foreground">Save your counters to a JSON file</p>
                 </div>
               </button>

               <div className="h-px bg-outline-variant/50 mx-4" />

               <button 
                onClick={handleImport}
                className="w-full flex items-center gap-3 p-3 hover:bg-surface-variant rounded-lg transition-colors text-left disabled:opacity-50"
                disabled={!isPro}
               >
                 <Upload size={20} className="text-primary" />
                 <div className="flex-1">
                   <div className="font-medium flex items-center gap-2">
                     Import Backup
                     {!isPro && <Crown size={12} className="text-muted-foreground" />}
                   </div>
                   <p className="text-xs text-muted-foreground">Restore from a JSON file</p>
                 </div>
               </button>
               <input type="file" id="import-file" className="hidden" accept=".json" />
            </div>
          </section>

          {/* Pro Section */}
          <section className="space-y-4">
            <h3 className="text-sm font-medium text-muted-foreground uppercase tracking-wider">Membership</h3>
            
            <div className={`rounded-2xl p-6 ${isPro ? 'bg-gradient-to-br from-primary/20 to-primary/5 border border-primary/20' : 'bg-surface-container'}`}>
              <div className="flex justify-between items-start mb-4">
                <div>
                  <h2 className="text-xl font-bold flex items-center gap-2">
                    <Crown size={24} className={isPro ? "text-primary" : "text-muted-foreground"} fill={isPro ? "currentColor" : "none"} />
                    Quantiq PRO
                  </h2>
                  <p className="text-sm text-muted-foreground mt-1">
                    {isPro ? "Active Membership" : "Unlock the full potential"}
                  </p>
                </div>
                {isPro && <div className="bg-primary/20 text-primary text-xs font-bold px-3 py-1 rounded-full">ACTIVE</div>}
              </div>

              <div className="space-y-2 mb-6">
                <FeatureRow active={isPro} text="Unlimited Counters" />
                <FeatureRow active={isPro} text="JSON Import/Export" />
                <FeatureRow active={isPro} text="Multiple Widgets" />
                <FeatureRow active={isPro} text="Cloud Sync (Soon)" />
              </div>

              <Button 
                className={`w-full ${isPro ? 'bg-surface text-foreground hover:bg-surface-variant' : 'bg-primary text-primary-foreground'}`}
                onClick={togglePro}
              >
                {isPro ? "Cancel Membership" : "Upgrade - $4.99"}
              </Button>
              {!isPro && <p className="text-xs text-center mt-3 text-muted-foreground">Restore Purchases</p>}
            </div>
          </section>

          {/* About */}
          <section className="pt-4 text-center">
             <p className="text-xs text-muted-foreground">Version 1.0.0 (Build 42)</p>
             <p className="text-xs text-muted-foreground mt-1">Made with Replit</p>
          </section>
        </div>
      </div>
    </div>
  );
}

function FeatureRow({ active, text }: { active: boolean, text: string }) {
  return (
    <div className="flex items-center gap-3">
      <div className={`w-5 h-5 rounded-full flex items-center justify-center ${active ? 'bg-primary text-primary-foreground' : 'bg-muted text-muted-foreground'}`}>
        <Check size={12} strokeWidth={3} />
      </div>
      <span className={active ? 'text-foreground' : 'text-muted-foreground'}>{text}</span>
    </div>
  )
}
