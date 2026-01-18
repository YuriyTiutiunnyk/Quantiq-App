import { useStore } from "@/lib/store";
import { useRoute, useLocation } from "wouter";
import { ArrowLeft, Settings2, RotateCcw, Minus, Plus, Trash2 } from "lucide-react";
import { motion } from "framer-motion";
import { useState, useEffect } from "react";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {
  Drawer,
  DrawerClose,
  DrawerContent,
  DrawerFooter,
  DrawerHeader,
  DrawerTitle,
  DrawerTrigger,
} from "@/components/ui/drawer";

export default function Details() {
  const [, params] = useRoute("/details/:id");
  const [, setLocation] = useLocation();
  const { counters, increment, decrement, reset, updateCounter, removeCounter } = useStore();
  
  const counter = counters.find(c => c.id === params?.id);
  const [editTitle, setEditTitle] = useState("");
  const [editStep, setEditStep] = useState("1");

  useEffect(() => {
    if (counter) {
      setEditTitle(counter.title);
      setEditStep(counter.step.toString());
    }
  }, [counter]);

  if (!counter) return null;

  const handleSaveSettings = () => {
    updateCounter(counter.id, { 
      title: editTitle, 
      step: parseInt(editStep) || 1 
    });
  };

  const handleDelete = () => {
    removeCounter(counter.id);
    setLocation("/");
  };

  return (
    <div className="h-full flex flex-col bg-background">
      {/* Header */}
      <header className="p-4 flex items-center justify-between">
        <button onClick={() => setLocation("/")} className="p-2 -ml-2 rounded-full hover:bg-surface-variant text-foreground/80">
          <ArrowLeft size={24} />
        </button>
        
        <Drawer>
          <DrawerTrigger asChild>
            <button className="p-2 -mr-2 rounded-full hover:bg-surface-variant text-foreground/80">
              <Settings2 size={24} />
            </button>
          </DrawerTrigger>
          <DrawerContent>
            <div className="mx-auto w-full max-w-sm">
              <DrawerHeader>
                <DrawerTitle>Edit Counter</DrawerTitle>
              </DrawerHeader>
              <div className="p-4 space-y-4">
                <div className="space-y-2">
                  <Label>Name</Label>
                  <Input value={editTitle} onChange={(e) => setEditTitle(e.target.value)} />
                </div>
                <div className="space-y-2">
                  <Label>Step Size</Label>
                  <Input 
                    type="number" 
                    value={editStep} 
                    onChange={(e) => setEditStep(e.target.value)} 
                  />
                </div>
                
                <div className="pt-4 border-t border-outline-variant">
                   <Button variant="destructive" className="w-full" onClick={handleDelete}>
                    <Trash2 className="mr-2 h-4 w-4" /> Delete Counter
                   </Button>
                </div>
              </div>
              <DrawerFooter>
                <DrawerClose asChild>
                  <Button onClick={handleSaveSettings}>Save Changes</Button>
                </DrawerClose>
                <DrawerClose asChild>
                  <Button variant="outline">Cancel</Button>
                </DrawerClose>
              </DrawerFooter>
            </div>
          </DrawerContent>
        </Drawer>
      </header>

      {/* Main Display */}
      <div className="flex-1 flex flex-col items-center justify-center space-y-8 -mt-20">
        <div className="text-center space-y-2">
          <motion.h2 
            className="text-2xl font-medium text-muted-foreground"
            initial={{ opacity: 0, y: 10 }}
            animate={{ opacity: 1, y: 0 }}
          >
            {counter.title}
          </motion.h2>
          <motion.div 
            key={counter.value}
            initial={{ scale: 0.8, opacity: 0 }}
            animate={{ scale: 1, opacity: 1 }}
            className="text-9xl font-light text-foreground tracking-tighter tabular-nums"
          >
            {counter.value}
          </motion.div>
        </div>

        {/* Controls */}
        <div className="flex items-center gap-8">
          <motion.button
            whileTap={{ scale: 0.9 }}
            onClick={() => decrement(counter.id)}
            className="w-20 h-20 rounded-full bg-surface-variant hover:bg-surface-container flex items-center justify-center text-foreground shadow-sm transition-colors"
          >
            <Minus size={32} />
          </motion.button>

          <motion.button
            whileTap={{ scale: 0.9 }}
            onClick={() => reset(counter.id)}
            className="w-12 h-12 rounded-full bg-transparent hover:bg-surface-variant flex items-center justify-center text-muted-foreground transition-colors"
          >
            <RotateCcw size={20} />
          </motion.button>

          <motion.button
            whileTap={{ scale: 0.9 }}
            onClick={() => increment(counter.id)}
            className="w-24 h-24 rounded-[32px] bg-primary text-primary-foreground flex items-center justify-center shadow-lg shadow-primary/20"
          >
            <Plus size={40} />
          </motion.button>
        </div>
      </div>
      
      <div className="h-12 flex items-center justify-center pb-8 opacity-50 text-sm">
        Step: {counter.step}
      </div>
    </div>
  );
}
