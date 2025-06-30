import axios from 'axios';
import { Message } from 'element-ui';

const service = axios.create({
  baseURL: 'http://localhost:8081', // 后端API地址，根据实际情况修改
  timeout: 10000
});

// 请求拦截器
service.interceptors.request.use(
  config => {
    // 添加token作为sessionId到请求头
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.sessionId = token;
    }
    return config;
  },
  error => Promise.reject(error)
);

// 响应拦截器
service.interceptors.response.use(
  response => response.data,
  error => {
    const message = error.response?.data?.message || error.response?.data || '请求出错';
    Message.error(message);
    
    // 如果是401错误，清除登录信息并跳转到登录页
    if (error.response?.status === 401) {
      localStorage.removeItem('token');
      localStorage.removeItem('user');
      // 重定向到登录页
      window.location.href = '/login';
    }
    
    return Promise.reject(error);
  }
);

export default service;