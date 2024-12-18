import baseUrl from './svc/baseUrl';
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