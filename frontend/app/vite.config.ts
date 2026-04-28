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
      const location = proxyRes.headers['location'];
      if (typeof location === 'string' && location.startsWith(PROD_BACKEND)) {
        proxyRes.headers['location'] = location.replace(PROD_BACKEND, 'http://127.0.0.1:5173');
      }

      const cookies = proxyRes.headers['set-cookie'] as string[] | undefined;
      if (cookies) {
        proxyRes.headers['set-cookie'] = cookies.map((c: string) =>
          c.replace(/;\s*Domain=[^;]+/gi, '').replace(/;\s*Secure/gi, ''),
        );
      }

      proxyRes.headers['access-control-allow-origin'] = 'http://127.0.0.1:5173';
      proxyRes.headers['access-control-allow-credentials'] = 'true';
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
  base: command === 'build' ? '/finances/' : '/',
  server: {
    host: '127.0.0.1',
    port: 5173,
    strictPort: true,
    proxy,
  },
}));
