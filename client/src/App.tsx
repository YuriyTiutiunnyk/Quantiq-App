import { Switch, Route } from "wouter";
import { queryClient } from "./lib/queryClient";
import { QueryClientProvider } from "@tanstack/react-query";
import { Toaster } from "@/components/ui/toaster";
import Layout from "@/components/layout";
import Home from "@/pages/home";
import Details from "@/pages/details";
import SettingsPage from "@/pages/settings";
import WidgetPreview from "@/pages/widget-preview";
import NotFound from "@/pages/not-found";
import { useStore } from "./lib/store";
import { useEffect } from "react";

function Router() {
  return (
    <Layout>
      <Switch>
        <Route path="/" component={Home} />
        <Route path="/details/:id" component={Details} />
        <Route path="/settings" component={SettingsPage} />
        <Route path="/widget" component={WidgetPreview} />
        <Route component={NotFound} />
      </Switch>
    </Layout>
  );
}

function App() {
  const { theme } = useStore();

  useEffect(() => {
    document.documentElement.classList.toggle('dark', theme === 'dark');
  }, [theme]);

  return (
    <QueryClientProvider client={queryClient}>
      <Toaster />
      <Router />
    </QueryClientProvider>
  );
}

export default App;
