import baseUrl from './svc/baseUrl';
const { createProxyMiddleware } = require('http-proxy-middleware');

// '/api'로 시작하는 모든 요청에 대해 프록시 설정 적용/CORS 우회
module.exports = function(app) {
  app.use(
    '/api',
    createProxyMiddleware({
      target: baseUrl,
      changeOrigin: true,
    })
  );
};