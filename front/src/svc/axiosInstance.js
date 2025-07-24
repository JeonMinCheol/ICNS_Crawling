import axios from 'axios';

const token = localStorage.getItem('token');

const axiosInstance = axios.create({
  baseURL: 'http://your-api-url.com',
  headers: {
    // 요청 헤더에 Authorization 포함
    Authorization: `Bearer ${token}`,  // 기본 헤더에 토큰 설정
  }
});

export default axiosInstance;