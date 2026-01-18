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
      // Logic handled by showing locked item in list or alert
      // Ideally redirect to settings
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
    <div className="p-4 space-y-4">
      <header className="py-4">
        <h1 className="text-4xl font-normal text-foreground tracking-tight ml-2">Quantiq</h1>
      </header>

      <div className="space-y-2 pb-24">
        <AnimatePresence mode="popLayout">
          {counters.map((counter) => (
            <motion.div
              key={counter.id}
              initial={{ opacity: 0, x: -20 }}
              animate={{ opacity: 1, x: 0 }}
              exit={{ opacity: 0, height: 0 }}
              layoutId={counter.id}
            >
              <Link href={`/details/${counter.id}`} className="block group cursor-pointer">
                  <div className="bg-surface-container-low hover:bg-surface-container transition-colors rounded-xl p-4 flex items-center justify-between shadow-sm border border-transparent hover:border-outline-variant">
                      <div className="flex flex-col">
                        <span className="text-lg font-medium text-on-surface">{counter.title}</span>
                        <span className="text-sm text-on-surface-variant">Step: {counter.step}</span>
                      </div>
                      <div className="flex items-center gap-4">
                        <span className="text-3xl font-normal text-primary tabular-nums">
                          {counter.value}
                        </span>
                        <ChevronRight className="text-on-surface-variant opacity-50" size={24} />
                      </div>
                  </div>
              </Link>
            </motion.div>
          ))}
        </AnimatePresence>

        {/* Free Limit Placeholder - M3 Style */}
        {!isPro && counters.length >= 3 && (
          <motion.div 
            initial={{ opacity: 0 }}
            animate={{ opacity: 1 }}
          >
             <Link href="/settings" className="block cursor-pointer">
              <div className="border border-dashed border-outline rounded-xl p-6 flex flex-col items-center justify-center text-center gap-2 opacity-50 hover:opacity-80 transition-opacity">
                <Lock className="text-on-surface-variant" size={24} />
                <p className="text-sm text-on-surface-variant font-medium">Free limit reached (3/3)</p>
                <span className="text-primary text-xs font-bold uppercase tracking-wide">Unlock PRO</span>
              </div>
            </Link>
          </motion.div>
        )}
      </div>

      <motion.button
        whileHover={{ scale: 1.05 }}
        whileTap={{ scale: 0.95 }}
        onClick={handleAdd}
        disabled={!isPro && counters.length >= 3}
        className={`fixed bottom-24 right-6 w-14 h-14 rounded-2xl shadow-xl flex items-center justify-center z-40 transition-colors
          ${(!isPro && counters.length >= 3) ? 'bg-surface-container-high text-on-surface-variant opacity-50' : 'bg-primary-container text-on-primary-container hover:shadow-2xl'}
        `}
      >
        {(!isPro && counters.length >= 3) ? <Lock size={24} /> : <Plus size={24} />}
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
              placeholder="e.g. Coffee"
              autoFocus
            />
          </div>
          <DialogFooter>
             <Button variant="ghost" onClick={() => setIsDialogOpen(false)}>Cancel</Button>
             <Button onClick={confirmAdd}>Create</Button>
          </DialogFooter>
        </DialogContent>
      </Dialog>
    </div>
  );
}
