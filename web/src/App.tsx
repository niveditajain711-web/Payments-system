import { FormEvent, useState } from "react";
import * as api from "./api";

/* ── Icons (inline SVG) ─────────────────────────────── */
const Icon = {
  send: (
    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <line x1="22" y1="2" x2="11" y2="13" /><polygon points="22 2 15 22 11 13 2 9 22 2" />
    </svg>
  ),
  wallet: (
    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <rect x="2" y="5" width="20" height="14" rx="2" /><path d="M16 13h.01" strokeWidth="2.5" strokeLinecap="round" />
    </svg>
  ),
  collect: (
    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <circle cx="12" cy="12" r="10" /><path d="M12 8v4l3 3" strokeLinecap="round" />
    </svg>
  ),
  check: (
    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <polyline points="20 6 9 17 4 12" strokeLinecap="round" strokeLinejoin="round" />
    </svg>
  ),
  user: (
    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <path d="M20 21v-2a4 4 0 0 0-4-4H8a4 4 0 0 0-4 4v2" /><circle cx="12" cy="7" r="4" />
    </svg>
  ),
  device: (
    <svg width="16" height="16" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <rect x="5" y="2" width="14" height="20" rx="2" /><line x1="12" y1="18" x2="12" y2="18" strokeWidth="2.5" strokeLinecap="round" />
    </svg>
  ),
  logo: (
    <svg width="22" height="22" fill="none" stroke="currentColor" strokeWidth="2" viewBox="0 0 24 24">
      <path d="M12 2L2 7l10 5 10-5-10-5z" /><path d="M2 17l10 5 10-5" /><path d="M2 12l10 5 10-5" />
    </svg>
  ),
};

type Section = "pay" | "balance" | "collect-create" | "collect-approve" | "register-vpa" | "device";

interface Result { ok: boolean; status: number; body: unknown }

type NoticeKind = "success" | "error" | "loading";
type Notice = { kind: NoticeKind; title: string; detail?: string };

function NoticeBar({ notice }: { notice: Notice | null }) {
  if (!notice) return null;
  const cls = notice.kind === "success" ? "notice notice-success"
    : notice.kind === "error" ? "notice notice-error"
      : "notice notice-loading";
  return (
    <div className={cls}>
      <div className="notice-title">{notice.title}</div>
      {notice.detail && <div className="notice-detail">{notice.detail}</div>}
    </div>
  );
}

function noticeFromResult(r: Result): Notice {
  const b: any = r.body;
  const msg =
    b && typeof b === "object"
      ? (typeof b.message === "string" ? b.message
        : typeof b.error === "string" ? b.error
          : null)
      : null;
  if (r.ok) return { kind: "success", title: `Success (${r.status})`, detail: msg ?? undefined };
  return { kind: "error", title: `Request failed (${r.status})`, detail: msg ?? "Check server logs / DevTools for details." };
}

/* ── Field helper ───────────────────────────────────── */
function Field({ label, name, type = "text", placeholder, defaultValue, required }: {
  label: string; name: string; type?: string; placeholder?: string; defaultValue?: string | number; required?: boolean;
}) {
  return (
    <div className="field">
      <label className="field-label" htmlFor={name}>{label}</label>
      <input id={name} name={name} type={type} placeholder={placeholder} defaultValue={defaultValue} required={required} />
    </div>
  );
}

/* ── Nav button ─────────────────────────────────────── */
function NavBtn({ id, label, icon, active, onClick }: { id: Section; label: string; icon: React.ReactNode; active: boolean; onClick: (id: Section) => void }) {
  return (
    <button className={`nav-btn${active ? " active" : ""}`} onClick={() => onClick(id)}>
      {icon} {label}
    </button>
  );
}

/* ── Section views ──────────────────────────────────── */
function PayView({ onResult }: { onResult: (r: Result, loading: boolean) => void }) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-icon">{Icon.send}</span>
        <h2>Send Money</h2>
      </div>
      <p className="card-desc">Push payment from payer VPA to payee VPA. Amount is entered in ₹ and converted to paise.</p>
      <form className="form-grid" onSubmit={async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const fd = new FormData(e.currentTarget);
        onResult({ ok: false, status: 0, body: null }, true);
        const r = await api.pay(String(fd.get("payerVpa")), String(fd.get("payeeVpa")), Math.round(Number(fd.get("rupees")) * 100));
        onResult(r, false);
      }}>
        <Field label="Payer VPA" name="payerVpa" defaultValue="nivedita@banka" required />
        <Field label="Payee VPA" name="payeeVpa" defaultValue="rohan@bankb" required />
        <Field label="Amount (₹)" name="rupees" type="number" defaultValue="1" required />
        <div className="btn-row">
          <button className="btn btn-primary" type="submit">{Icon.send} Send Payment</button>
        </div>
      </form>
    </div>
  );
}

interface BalanceData { vpa: string; balancePaise: number; balanceRupees?: string; currency: string }

function BalanceView({ onResult }: { onResult: (r: Result, loading: boolean) => void }) {
  const [balanceData, setBalanceData] = useState<BalanceData | null>(null);
  const [querying, setQuerying] = useState(false);

  return (
    <div className="card">
      <div className="card-header">
        <span className="card-icon">{Icon.wallet}</span>
        <h2>Check Balance</h2>
      </div>
      <p className="card-desc">Query the account balance for any registered VPA.</p>
      <form className="form-grid" onSubmit={async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const fd = new FormData(e.currentTarget);
        setQuerying(true);
        setBalanceData(null);
        onResult({ ok: false, status: 0, body: null }, true);
        const r = await api.balance(String(fd.get("vpa")));
        setQuerying(false);
        onResult(r, false);
        if (r.ok && r.body) setBalanceData(r.body as BalanceData);
      }}>
        <Field label="VPA" name="vpa" defaultValue="nivedita@banka" required />
        <div className="btn-row">
          <button className="btn btn-primary" type="submit" disabled={querying}>
            {querying ? <span className="spinner" /> : Icon.wallet} Check Balance
          </button>
        </div>
      </form>

      {querying && (
        <div className="balance-result balance-loading">Fetching balance…</div>
      )}

      {!querying && balanceData && (
        <div className="balance-result">
          <div className="balance-vpa">{balanceData.vpa}</div>
          <div className="balance-amount">
            ₹{balanceData.balanceRupees ?? (balanceData.balancePaise / 100).toFixed(2)}
          </div>
          <div className="balance-meta">{balanceData.currency}</div>
        </div>
      )}
    </div>
  );
}

function CollectCreateView({ onResult }: { onResult: (r: Result, loading: boolean) => void }) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-icon">{Icon.collect}</span>
        <h2>Create Collect Request</h2>
      </div>
      <p className="card-desc">Merchant / payee initiates a collect. Payer receives the request and must approve it separately.</p>
      <form className="form-grid" onSubmit={async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const fd = new FormData(e.currentTarget);
        onResult({ ok: false, status: 0, body: null }, true);
        const r = await api.collectCreate(String(fd.get("payerVpa")), String(fd.get("payeeVpa")), Math.round(Number(fd.get("rupees")) * 100), String(fd.get("note") ?? ""));
        onResult(r, false);
      }}>
        <Field label="Payer VPA (who will approve)" name="payerVpa" defaultValue="nivedita@banka" required />
        <Field label="Payee / Merchant VPA" name="payeeVpa" defaultValue="rohan@bankb" required />
        <Field label="Amount (₹)" name="rupees" type="number" defaultValue="1" required />
        <Field label="Note (optional)" name="note" placeholder="e.g. Invoice #42" />
        <div className="btn-row">
          <button className="btn btn-primary" type="submit">{Icon.collect} Create Request</button>
        </div>
      </form>
    </div>
  );
}

function CollectApproveView({ onResult }: { onResult: (r: Result, loading: boolean) => void }) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-icon">{Icon.check}</span>
        <h2>Approve Collect Request</h2>
      </div>
      <p className="card-desc">Payer approves a pending collect request using the ID returned from the create step.</p>
      <form className="form-grid" onSubmit={async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const fd = new FormData(e.currentTarget);
        onResult({ ok: false, status: 0, body: null }, true);
        const r = await api.collectApprove(String(fd.get("collectId")), String(fd.get("pin")));
        onResult(r, false);
      }}>
        <Field label="Collect ID" name="collectId" placeholder="UUID from create response" required />
        <Field label="Simulated PIN" name="pin" defaultValue="1234" required />
        <div className="btn-row">
          <button className="btn btn-primary" type="submit">{Icon.check} Approve</button>
        </div>
      </form>
    </div>
  );
}

function RegisterVpaView({ onResult }: { onResult: (r: Result, loading: boolean) => void }) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-icon">{Icon.user}</span>
        <h2>Register VPA</h2>
      </div>
      <div className="hint">
        Requires an existing ledger account at the bank. Demo account IDs:
        <br />
        <span className="chip">aaaaaaaa-…-aaa2</span> Nivedita (Bank A) &nbsp;
        <span className="chip">aaaaaaaa-…-aaa3</span> Rohan (Bank A) &nbsp;
        <span className="chip">bbbbbbbb-…-bbb2</span> Rohan (Bank B)
      </div>
      <form className="form-grid" onSubmit={async (e: FormEvent<HTMLFormElement>) => {
        e.preventDefault();
        const fd = new FormData(e.currentTarget);
        onResult({ ok: false, status: 0, body: null }, true);
        const r = await api.registerVpa(String(fd.get("vpa")), String(fd.get("displayName")), String(fd.get("bankCode")), String(fd.get("accountId")));
        onResult(r, false);
      }}>
        <Field label="VPA" name="vpa" placeholder="you@banka" required />
        <Field label="Display Name" name="displayName" placeholder="e.g. Alice" required />
        <Field label="Bank Code" name="bankCode" defaultValue="A" required />
        <Field label="Account ID (UUID)" name="accountId" placeholder="aaaaaaaa-aaaa-…" required />
        <div className="btn-row">
          <button className="btn btn-primary" type="submit">{Icon.user} Register</button>
        </div>
      </form>
    </div>
  );
}

function DeviceView({ onResult }: { onResult: (r: Result, loading: boolean) => void }) {
  return (
    <div className="card">
      <div className="card-header">
        <span className="card-icon">{Icon.device}</span>
        <h2>Device Bind (stub)</h2>
      </div>
      <p className="card-desc">Simulates binding a demo device to the TPAP. This is a stub endpoint — no real device provisioning occurs.</p>
      <div className="btn-row">
        <button className="btn btn-primary" type="button" onClick={async () => {
          onResult({ ok: false, status: 0, body: null }, true);
          const r = await api.deviceBind();
          onResult(r, false);
        }}>{Icon.device} Bind Demo Device</button>
      </div>
    </div>
  );
}

/* ── App shell ──────────────────────────────────────── */
const NAV: { id: Section; label: string; icon: React.ReactNode }[] = [
  { id: "pay",             label: "Send Money",     icon: Icon.send    },
  { id: "balance",         label: "Balance",        icon: Icon.wallet  },
  { id: "collect-create",  label: "Collect",        icon: Icon.collect },
  { id: "collect-approve", label: "Approve",        icon: Icon.check   },
  { id: "register-vpa",    label: "Register VPA",   icon: Icon.user    },
  { id: "device",          label: "Device Bind",    icon: Icon.device  },
];

export function App() {
  const [section, setSection] = useState<Section>("pay");
  const [notice, setNotice] = useState<Notice | null>(null);

  function onResult(r: Result, isLoading: boolean) {
    if (isLoading) {
      setNotice({ kind: "loading", title: "Working…", detail: "Please wait." });
      return;
    }
    setNotice(noticeFromResult(r));
  }

  return (
    <div className="shell">
      {/* Top bar */}
      <header className="topbar">
        <span className="topbar-logo">
          {Icon.logo} Payments Lab
        </span>
        <span className="topbar-sub">UPI simulation · TPAP @ {import.meta.env.VITE_TPAP_URL ?? "localhost:8080"}</span>
        <div className="topbar-vpas">
          {["nivedita@banka", "rohan@banka", "rohan@bankb"].map(v => (
            <span key={v} className="chip">{v}</span>
          ))}
        </div>
      </header>

      {/* Sidebar */}
      <nav className="sidebar">
        <span className="sidebar-label">Actions</span>
        {NAV.map(n => (
          <NavBtn key={n.id} id={n.id} label={n.label} icon={n.icon} active={section === n.id} onClick={setSection} />
        ))}
      </nav>

      {/* Main */}
      <main className="main">
        <NoticeBar notice={notice} />
        {section === "pay"             && <PayView             onResult={onResult} />}
        {section === "balance"         && <BalanceView         onResult={onResult} />}
        {section === "collect-create"  && <CollectCreateView   onResult={onResult} />}
        {section === "collect-approve" && <CollectApproveView  onResult={onResult} />}
        {section === "register-vpa"    && <RegisterVpaView     onResult={onResult} />}
        {section === "device"          && <DeviceView          onResult={onResult} />}
      </main>
    </div>
  );
}
