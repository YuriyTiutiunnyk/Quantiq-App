import { useStore } from "@/lib/store";
import { Plus, Minus } from "lucide-react";
import { useState } from "react";
import { Select, SelectContent, SelectItem, SelectTrigger, SelectValue } from "@/components/ui/select";

export default function WidgetPreview() {
  const { counters, increment, decrement } = useStore();
  const [selectedId, setSelectedId] = useState<string>(counters[0]?.id || "");

  const activeCounter = counters.find(c => c.id === selectedId);

  return (
    <div className="h-full flex flex-col p-6 bg-background">
       <div className="mb-6">
         <h1 className="text-2xl font-normal">Widget Preview</h1>
         <p className="text-muted-foreground text-sm mt-1">
           Simulating Android Home Screen Widget. 
           Add this to your home screen to control counters instantly.
         </p>
       </div>

       {/* Home Screen Simulator */}
       <div 
        className="flex-1 bg-cover bg-center rounded-[32px] overflow-hidden relative shadow-inner border-8 border-neutral-900"
        style={{ backgroundImage: 'url(https://images.unsplash.com/photo-1618005182384-a83a8bd57fbe?q=80&w=2564&auto=format&fit=crop)' }}
       >
         {/* Overlay to dim background slightly */}
         <div className="absolute inset-0 bg-black/20 backdrop-blur-[1px]" />

         {/* Grid Layout */}
         <div className="absolute top-12 left-6 right-6 grid grid-cols-4 gap-4">
           {/* App Icons (Fake) */}
           <div className="w-14 h-14 bg-white/20 rounded-2xl backdrop-blur-md" />
           <div className="w-14 h-14 bg-white/20 rounded-2xl backdrop-blur-md" />
           <div className="w-14 h-14 bg-white/20 rounded-2xl backdrop-blur-md" />
           <div className="w-14 h-14 bg-white/20 rounded-2xl backdrop-blur-md" />
         </div>

         {/* The Widget */}
         <div className="absolute top-36 left-1/2 -translate-x-1/2 w-64">
           <div className="bg-surface/90 dark:bg-[#1C1B1F]/90 backdrop-blur-md rounded-[28px] p-5 shadow-xl border border-white/10 text-foreground">
              {counters.length > 0 ? (
                <>
                  <div className="flex items-center justify-between mb-2">
                    <Select value={selectedId} onValueChange={setSelectedId}>
                      <SelectTrigger className="h-8 border-none bg-transparent hover:bg-black/5 dark:hover:bg-white/5 p-0 px-2 rounded-lg font-medium text-sm focus:ring-0 w-full justify-between">
                        <SelectValue placeholder="Select counter" />
                      </SelectTrigger>
                      <SelectContent>
                        {counters.map(c => (
                          <SelectItem key={c.id} value={c.id}>{c.title}</SelectItem>
                        ))}
                      </SelectContent>
                    </Select>
                  </div>

                  {activeCounter ? (
                    <div className="flex items-center justify-between">
                       <button 
                        onClick={() => decrement(activeCounter.id)}
                        className="w-10 h-10 rounded-xl bg-primary/10 hover:bg-primary/20 text-primary flex items-center justify-center transition-colors"
                       >
                         <Minus size={20} />
                       </button>
                       
                       <span className="text-3xl font-light tabular-nums">
                         {activeCounter.value}
                       </span>

                       <button 
                        onClick={() => increment(activeCounter.id)}
                        className="w-10 h-10 rounded-xl bg-primary text-primary-foreground hover:bg-primary/90 flex items-center justify-center transition-colors"
                       >
                         <Plus size={20} />
                       </button>
                    </div>
                  ) : (
                    <div className="text-center py-2 text-sm text-muted-foreground">Select a counter</div>
                  )}
                </>
              ) : (
                <div className="text-center py-4 text-sm">No counters created</div>
              )}
           </div>
           <p className="text-center text-white/80 text-xs mt-3 font-medium shadow-black drop-shadow-md">Quantiq Widget (4x2)</p>
         </div>
       </div>
    </div>
  );
}
