import axios from 'axios';

// 백엔드 API 기본 URL
const API_BASE_URL = 'http://localhost:8080';

// axios 인스턴스 생성
const api = axios.create({
  baseURL: API_BASE_URL,
  timeout: 10000,
  headers: {
    'Content-Type': 'application/json',
  },
});

// API 요청/응답 인터셉터
api.interceptors.request.use(
  (config) => {
    console.log('API 요청:', config.method?.toUpperCase(), config.url);
    return config;
  },
  (error) => {
    console.error('API 요청 에러:', error);
    return Promise.reject(error);
  }
);

api.interceptors.response.use(
  (response) => {
    console.log('API 응답:', response.status, response.config.url);
    return response;
  },
  (error) => {
    console.error('API 응답 에러:', error.response?.status, error.config?.url);
    return Promise.reject(error);
  }
);

// 주차장 관련 API 함수들
export const parkingAPI = {
  // 전체 주차장 정보 조회
  getAllParkingInfo: () => api.get('/parking'),
  
  // 주차장 목록만 조회
  getParkingList: () => api.get('/parking/list'),
  
  // 주차장 통계 정보 조회
  getParkingStats: () => api.get('/parking/stats'),
};

// 기본 export
export default api;
