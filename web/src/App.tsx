import { FormEvent, useState } from "react";
import * as api from "./api";

function JsonBlock({ data }: { data: unknown }) {
  return (
    <pre className="result">
      {data === undefined ? "" : JSON.stringify(data, null, 2)}
    </pre>
  );
}

export function App() {
  const [out, setOut] = useState<unknown>(null);

  return (
    <div className="page">
      <header className="hero">
        <h1>Payments lab</h1>
        <p>
          Demo UI against TPAP (<code>{import.meta.env.VITE_TPAP_URL ?? "http://localhost:8080"}</code>).
          Amounts in rupees below are converted to paise. Seeded VPAs:{" "}
          <code>nivedita@banka</code>, <code>rohan@banka</code>, <code>rohan@bankb</code>.
        </p>
      </header>

      <section className="card">
        <h2>Pay (push)</h2>
        <form
          onSubmit={async (e: FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            const fd = new FormData(e.currentTarget);
            const rupees = Number(fd.get("rupees"));
            const r = await api.pay(
              String(fd.get("payerVpa")),
              String(fd.get("payeeVpa")),
              Math.round(rupees * 100),
            );
            setOut(r);
          }}
        >
          <label>
            Payer VPA
            <input name="payerVpa" defaultValue="nivedita@banka" required />
          </label>
          <label>
            Payee VPA
            <input name="payeeVpa" defaultValue="rohan@bankb" required />
          </label>
          <label>
            Amount (₹)
            <input name="rupees" type="number" step="0.01" min="0.01" defaultValue="1" required />
          </label>
          <button type="submit">Pay</button>
        </form>
      </section>

      <section className="card">
        <h2>Balance</h2>
        <form
          onSubmit={async (e: FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            const fd = new FormData(e.currentTarget);
            const r = await api.balance(String(fd.get("vpa")));
            setOut(r);
          }}
        >
          <label>
            VPA
            <input name="vpa" defaultValue="nivedita@banka" required />
          </label>
          <button type="submit">Query</button>
        </form>
      </section>

      <section className="card">
        <h2>Collect (create)</h2>
        <form
          onSubmit={async (e: FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            const fd = new FormData(e.currentTarget);
            const rupees = Number(fd.get("rupees"));
            const r = await api.collectCreate(
              String(fd.get("payerVpa")),
              String(fd.get("payeeVpa")),
              Math.round(rupees * 100),
              String(fd.get("note") ?? ""),
            );
            setOut(r);
          }}
        >
          <label>
            Payer VPA (payer will approve)
            <input name="payerVpa" defaultValue="nivedita@banka" required />
          </label>
          <label>
            Payee / merchant VPA
            <input name="payeeVpa" defaultValue="rohan@bankb" required />
          </label>
          <label>
            Amount (₹)
            <input name="rupees" type="number" step="0.01" min="0.01" defaultValue="1" required />
          </label>
          <label>
            Note
            <input name="note" placeholder="optional" />
          </label>
          <button type="submit">Create collect</button>
        </form>
      </section>

      <section className="card">
        <h2>Collect (approve)</h2>
        <form
          onSubmit={async (e: FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            const fd = new FormData(e.currentTarget);
            const r = await api.collectApprove(String(fd.get("collectId")), String(fd.get("pin")));
            setOut(r);
          }}
        >
          <label>
            Collect ID (from create response)
            <input name="collectId" placeholder="uuid" required />
          </label>
          <label>
            Simulated PIN
            <input name="pin" defaultValue="1234" required />
          </label>
          <button type="submit">Approve</button>
        </form>
      </section>

      <section className="card">
        <h2>Register VPA (directory)</h2>
        <p className="hint">
          Requires an existing ledger account at the bank. Demo IDs: Bank A user{" "}
          <code>aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa2</code> (Nivedita),{" "}
          <code>aaaaaaaa-aaaa-aaaa-aaaa-aaaaaaaaaaa3</code> (Rohan A); Bank B{" "}
          <code>bbbbbbbb-bbbb-bbbb-bbbb-bbbbbbbbbbb2</code> (Rohan B).
        </p>
        <form
          onSubmit={async (e: FormEvent<HTMLFormElement>) => {
            e.preventDefault();
            const fd = new FormData(e.currentTarget);
            const r = await api.registerVpa(
              String(fd.get("vpa")),
              String(fd.get("displayName")),
              String(fd.get("bankCode")),
              String(fd.get("accountId")),
            );
            setOut(r);
          }}
        >
          <label>
            VPA
            <input name="vpa" placeholder="you@banka" required />
          </label>
          <label>
            Display name
            <input name="displayName" required />
          </label>
          <label>
            Bank code
            <input name="bankCode" defaultValue="A" required />
          </label>
          <label>
            Account ID (UUID)
            <input name="accountId" required />
          </label>
          <button type="submit">Register</button>
        </form>
      </section>

      <section className="card">
        <h2>Device bind (stub)</h2>
        <button
          type="button"
          onClick={async () => {
            const r = await api.deviceBind();
            setOut(r);
          }}
        >
          Bind demo device
        </button>
      </section>

      <section className="card">
        <h2>Last response</h2>
        <JsonBlock data={out} />
      </section>
    </div>
  );
}
