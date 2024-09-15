import baseUrl from './env';
const { createProxyMiddleware } = require('http-proxy-middleware');

module.exports = function(app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: baseUrl,
      changeOrigin: true,
    })
  );
};