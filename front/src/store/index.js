import Vue from 'vue'
import Vuex from 'vuex'
import request from '@/utils/request'

Vue.use(Vuex)

export default new Vuex.Store({
  state: {
    user: null,
    token: localStorage.getItem('token') || null,
    courses: [],
    currentCourse: null
  },
  getters: {
    isLoggedIn: state => !!state.user && !!state.token,
    currentUser: state => state.user,
    token: state => state.token,
    isInstructor: state => state.user && state.user.role === 'TEACHER',
    isStudent: state => state.user && state.user.role === 'STUDENT'
  },
  mutations: {
    SET_USER(state, user) {
      state.user = user
    },
    SET_TOKEN(state, token) {
      state.token = token
      if (token) {
        localStorage.setItem('token', token)
      } else {
        localStorage.removeItem('token')
      }
    },
    SET_COURSES(state, courses) {
      state.courses = courses
    },
    SET_CURRENT_COURSE(state, course) {
      state.currentCourse = course
    },
    LOGOUT(state) {
      state.user = null
      state.token = null
      state.currentCourse = null
      state.courses = []
      localStorage.removeItem('token')
      localStorage.removeItem('user')
    }
    
  },
  actions: {
    async login({ commit }, loginData) {
      try {
        const response = await request.post('/api/user/login', loginData)
    
        // 解构后端返回的结构
        const { sessionId, username, role, userId } = response
    
        const user = {
          username,
          role,
          id: userId
        }
    
        // 保存 sessionId 作为 token
        commit('SET_TOKEN', sessionId)
        commit('SET_USER', user)
    
        // 本地缓存
        localStorage.setItem('user', JSON.stringify(user))
        localStorage.setItem('token', sessionId)
    
        return user
      } catch (error) {
        throw error
      }
    },
    
    
    async register({ commit }, registrationData) {
      try {
        const response = await request.post('/api/user/register', registrationData)
        return response
      } catch (error) {
        throw error
      }
    },
    
    logout({ commit }) {
      commit('LOGOUT')
    },
    
    // 初始化用户状态（从本地存储恢复）
    initAuth({ commit }) {
      const user = localStorage.getItem('user')
      const token = localStorage.getItem('token')
      
      if (user && token) {
        commit('SET_USER', JSON.parse(user))
        commit('SET_TOKEN', token)
      }
    },
    
    async fetchCourses({ commit }) {
      try {
        const courses = await request.get('/api/courses')
        commit('SET_COURSES', courses)
        return courses
      } catch (error) {
        throw error
      }
    },
    
    async fetchCourseById({ commit }, courseId) {
      try {
        const course = await request.get(`/api/courses/${courseId}`)
        commit('SET_CURRENT_COURSE', course)
        return course
      } catch (error) {
        throw error
      }
    }
  }
})
