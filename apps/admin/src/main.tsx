import React from "react";
import { createRoot } from "react-dom/client";
import "./styles.css";

function App() {
  return (
    <main className="shell">
      <section className="card">
        <span className="eyebrow">SUPERIPTV</span>
        <h1>Painel administrativo</h1>
        <p>A fundação do SuperIPTV 2.0 está pronta para receber usuários, dispositivos, planos, sessões e auditoria.</p>
        <div className="status">Pacote 1 · Fundação</div>
      </section>
    </main>
  );
}

createRoot(document.getElementById("root")!).render(<React.StrictMode><App /></React.StrictMode>);
