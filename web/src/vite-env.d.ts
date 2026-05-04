/// <reference types="vite/client" />

interface ImportMetaEnv {
  readonly VITE_TPAP_URL: string;
}

interface ImportMeta {
  readonly env: ImportMetaEnv;
}
