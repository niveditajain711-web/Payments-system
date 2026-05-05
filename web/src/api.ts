const TPAP = import.meta.env.VITE_TPAP_URL ?? "http://localhost:8080";

const CORRELATION = "X-Correlation-Id";
const IDEMPOTENCY = "Idempotency-Key";

function headers(idem?: string): HeadersInit {
  const h: Record<string, string> = {
    "Content-Type": "application/json",
    [CORRELATION]: crypto.randomUUID(),
  };
  if (idem) h[IDEMPOTENCY] = idem;
  return h;
}

export async function pay(payerVpa: string, payeeVpa: string, amountPaise: number) {
  const idem = crypto.randomUUID();
  const res = await fetch(`${TPAP}/api/v1/tpap/payments/pay`, {
    method: "POST",
    headers: headers(idem),
    body: JSON.stringify({ payerVpa, payeeVpa, amountPaise }),
  });
  const body = await res.json();
  return { ok: res.ok, status: res.status, body };
}

export async function balance(vpa: string) {
  const u = new URL(`${TPAP}/api/v1/tpap/balance`);
  u.searchParams.set("vpa", vpa);
  const res = await fetch(u.toString(), { headers: { [CORRELATION]: crypto.randomUUID() } });
  if (res.status === 404) return { ok: false, status: 404, body: null };
  const body = await res.json();
  return { ok: res.ok, status: res.status, body };
}

export async function collectCreate(payerVpa: string, payeeVpa: string, amountPaise: number, note?: string) {
  const idem = crypto.randomUUID();
  const res = await fetch(`${TPAP}/api/v1/tpap/collect-requests`, {
    method: "POST",
    headers: headers(idem),
    body: JSON.stringify({ payerVpa, payeeVpa, amountPaise, note: note ?? "" }),
  });
  const body = await res.json();
  return { ok: res.ok, status: res.status, body };
}

export async function collectApprove(collectId: string, upiPinEncrypted: string) {
  const idem = crypto.randomUUID();
  const res = await fetch(`${TPAP}/api/v1/tpap/collect-requests/${collectId}/approve`, {
    method: "POST",
    headers: headers(idem),
    body: JSON.stringify({ upiPinEncrypted }),
  });
  const body = await res.json();
  return { ok: res.ok, status: res.status, body };
}

export async function registerVpa(vpa: string, displayName: string, bankCode: string, accountId: string) {
  const res = await fetch(`${TPAP}/api/v1/tpap/register/vpa`, {
    method: "POST",
    headers: headers(),
    body: JSON.stringify({ vpa, displayName, bankCode, accountId }),
  });
  const body = await res.json();
  return { ok: res.ok, status: res.status, body };
}

export async function deviceBind() {
  const res = await fetch(`${TPAP}/api/v1/tpap/devices/bind`, {
    method: "POST",
    headers: headers(),
    body: JSON.stringify({ device: "demo" }),
  });
  const body = await res.json();
  return { ok: res.ok, status: res.status, body };
}
