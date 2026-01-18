import { useStore } from "@/lib/store";
import { Link } from "wouter";
import { Plus, ChevronRight, Lock } from "lucide-react";
import { motion, AnimatePresence } from "framer-motion";
import { useState } from "react";
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogFooter } from "@/components/ui/dialog";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button";

export default function Home() {
  const { counters, addCounter, isPro } = useStore();
  const [isDialogOpen, setIsDialogOpen] = useState(false);
  const [newTitle, setNewTitle] = useState("");

  const handleAdd = () => {
    if (!isPro && counters.length >= 3) {
      // Show PRO wall dialog or toast? For now just return to prevent add.
      // Ideally trigger a "Go Pro" modal.
      alert("Free version limited to 3 counters. Upgrade to PRO for unlimited!");
      return;
    }
    setIsDialogOpen(true);
  };

  const confirmAdd = () => {
    addCounter(newTitle || "New Counter");
    setNewTitle("");
    setIsDialogOpen(false);
  };

  return (
    <div className="p-4 space-y-6">
      <header className="flex justify-between items-center py-2">
        <h1 className="text-3xl font-normal text-foreground tracking-tight">Quantiq</h1>
        <div className="w-8 h-8 rounded-full bg-primary/20 flex items-center justify-center text-primary font-bold text-sm">
          {counters.length}
        </div>
      </header>

      <div className="space-y-3 pb-20">
        <AnimatePresence mode="popLayout">
          {counters.map((counter) => (
            <motion.div
              key={counter.id}
              initial={{ opacity: 0, y: 20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, scale: 0.95 }}
              layoutId={counter.id}
            >
              <Link href={`/details/${counter.id}`} className="block group cursor-pointer">
                  <div className="bg-surface-container hover:bg-surface-variant transition-colors rounded-[20px] p-5 relative overflow-hidden elevation-1 active:scale-[0.98] duration-200">
                    <div className={`absolute top-0 right-0 w-24 h-24 bg-primary/10 rounded-full -translate-y-8 translate-x-8 blur-2xl group-hover:bg-primary/20 transition-all`} />
                    
                    <div className="flex justify-between items-center relative z-10">
                      <div>
                        <h2 className="text-lg font-medium text-foreground/90">{counter.title}</h2>
                        <p className="text-sm text-muted-foreground mt-1">Step: {counter.step}</p>
                      </div>
                      <div className="flex items-center gap-3">
                        <span className="text-4xl font-light text-primary tabular-nums tracking-tighter">
                          {counter.value}
                        </span>
                        <ChevronRight className="text-muted-foreground/50" size={20} />
                      </div>
                    </div>
                  </div>
              </Link>
            </motion.div>
          ))}
        </AnimatePresence>

        {/* Free Limit Placeholder */}
        {!isPro && counters.length >= 3 && (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
            className="border-2 border-dashed border-outline-variant rounded-[20px] p-6 flex flex-col items-center justify-center text-center gap-2 opacity-60"
          >
            <Lock className="text-muted-foreground" />
            <p className="text-sm text-muted-foreground">Free limit reached (3/3)</p>
            <Link href="/settings" className="text-primary font-medium text-sm hover:underline cursor-pointer">
              Upgrade to PRO
            </Link>
          </motion.div>
        )}
      </div>

      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.9 }}
        onClick={handleAdd}
        className="fixed bottom-24 right-6 w-14 h-14 bg-primary text-primary-foreground rounded-2xl shadow-lg shadow-primary/30 flex items-center justify-center z-40"
      >
        <Plus size={28} />
      </motion.button>

      <Dialog open={isDialogOpen} onOpenChange={setIsDialogOpen}>
        <DialogContent>
          <DialogHeader>
            <DialogTitle>New Counter</DialogTitle>
          </DialogHeader>
          <div className="py-4">
            <Label htmlFor="title" className="mb-2 block">Name</Label>
            <Input 
              id="title" 
              value={newTitle} 
              onChange={(e) => setNewTitle(e.target.value)} 
              placeholder="e.g. Coffee Cups"
              className="text-lg"
              autoFocus
            />
          </div>
          <DialogFooter>
             <Button variant="outline" onClick={() => setIsDialogOpen(false)}>Cancel</Button>
             <Button onClick={confirmAdd}>Create</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
