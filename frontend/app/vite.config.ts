import { defineConfig, type ProxyOptions } from 'vite';
import react from '@vitejs/plugin-react';

const PROD_BACKEND = 'https://overmoney.tech';

const apiProxy: ProxyOptions = {
  target: PROD_BACKEND,
  changeOrigin: true,
  secure: false,
  configure: (proxy) => {
    proxy.on('proxyReq', (proxyReq) => {
      proxyReq.removeHeader('origin');
      const referer = proxyReq.getHeader('referer');
      if (typeof referer === 'string') {
        proxyReq.setHeader(
          'referer',
          referer.replace(/^https?:\/\/(127\.0\.0\.1|localhost)(:\d+)?/, PROD_BACKEND),
        );
      }
    });
    proxy.on('proxyRes', (proxyRes) => {
      const cookies = proxyRes.headers['set-cookie'] as string[] | undefined;
      if (cookies) {
        proxyRes.headers['set-cookie'] = cookies.map((c: string) =>
          c.replace(/;\s*Domain=[^;]+/gi, '').replace(/;\s*Secure/gi, ''),
        );
      }
    });
  },
};

const apiPrefixes = [
  '/auth',
  '/transaction',
  '/transactions',
  '/analytics',
  '/account',
  '/users',
  '/settings',
  '/admin',
  '/payments',
  '/properties',
  '/history',
  '/bugreport',
];

const proxy: Record<string, ProxyOptions> = Object.fromEntries(
  apiPrefixes.map((p) => [p, apiProxy]),
);
// Use regex so the SPA /categories route falls through; only /categories/* hits backend.
proxy['^/categories/'] = apiProxy;
proxy['^/login/.+'] = apiProxy;

export default defineConfig(({ command }) => ({
  plugins: [react()],
  base: command === 'build' ? '/front/' : '/',
  server: {
    host: '127.0.0.1',
    port: 5173,
    strictPort: true,
    proxy,
  },
}));
