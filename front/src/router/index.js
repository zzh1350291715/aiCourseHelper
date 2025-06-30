import Vue from 'vue'
import VueRouter from 'vue-router'
import HomeView from '../views/HomeView.vue'
import store from '../store'

Vue.use(VueRouter)

const routes = [
  {
    path: '/',
    name: 'home',
    component: HomeView
  },
  {
    path: '/login',
    name: 'Login',
    component: () => import('@/views/Login.vue')
  },
  {
    path: '/register',
    name: 'Register',
    component: () => import('@/views/Register.vue')
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: () => import('@/views/Dashboard.vue')
  },
  {
    path: '/courses',
    name: 'CourseList',
    component: () => import('@/views/CourseList.vue')
  },
  {
    path: '/courses/:id',
    name: 'CourseDetail',
    component: () => import('@/views/CourseDetail.vue')
  },
  {
    path: '/courses/:courseId/materials',
    name: 'CourseMaterials',
    component: () => import('@/views/CourseMaterials.vue')
  },
  {
    path: '/courses/:courseId/chat',
    name: 'CourseChat',
    component: () => import('@/views/CourseChat.vue')
  },
  {
    path: '/courses/:courseId/progress',
    name: 'CourseProgress',
    component: () => import('@/views/CourseProgress.vue')
  },
  // 讲师专用路由
  {
    path: '/instructor/courses',
    name: 'InstructorCourses',
    component: () => import('@/views/instructor/MyCourses.vue')
  },
  {
    path: '/instructor/create-course',
    name: 'CreateCourse',
    component: () => import('@/views/instructor/CreateCourse.vue')
  },
  {
    path: '/instructor/courses/:courseId/materials',
    name: 'ManageCourseMaterials',
    component: () => import('@/views/instructor/ManageCourseMaterials.vue')
  },
  {
    path: '/instructor/knowledge-base',
    name: 'KnowledgeBaseManagement',
    component: () => import('@/views/instructor/KnowledgeBase.vue')
  },
  {
    path: '/instructor/courses/:courseId/knowledge-base',
    name: 'CourseKnowledgeBase',
    component: () => import('@/views/instructor/CourseKnowledgeBase.vue')
  },
  {
    path: '/instructor/ai-generation',
    name: 'AIGeneration',
    component: () => import('@/views/instructor/AIGeneration.vue')
  },
  // 考试系统路由 - 讲师
  {
    path: '/instructor/question-bank',
    name: 'QuestionBank',
    component: () => import('@/views/instructor/QuestionBank.vue')
  },
  {
    path: '/instructor/quiz-list',
    name: 'QuizList',
    component: () => import('@/views/instructor/QuizList.vue')
  },
  // 学生专用路由
  {
    path: '/student/progress',
    name: 'StudentProgress',
    component: () => import('@/views/student/MyProgress.vue')
  },
  {
    path: '/student/enrolled',
    name: 'EnrolledCourses',
    component: () => import('@/views/student/EnrolledCourses.vue')
  },
  // 考试系统路由 - 学生
  {
    path: '/student/quiz-list',
    name: 'StudentQuizList',
    component: () => import('@/views/student/QuizList.vue')
  },
  // 测验路由
  {
    path: '/materials/:materialId/quiz',
    name: 'Quiz',
    component: () => import('@/views/student/Quiz.vue')
  },
  {
    path: '/quiz/:quizId',
    name: 'Quiz',
    component: () => import('@/views/student/Quiz.vue')
  },
  {
    path: '/quiz/:quizId/result',
    name: 'QuizResult',
    component: () => import('@/views/student/QuizResult.vue')
  },
  // 个人中心
  {
    path: '/profile',
    name: 'Profile',
    component: () => import('@/views/Profile.vue')
  },
  {
    path: '/about',
    name: 'about',
    component: () => import('@/views/AboutView.vue')
  }
]

const router = new VueRouter({
  mode: 'history', // 加上这一行
  routes
})

// 全局路由守卫，防止重复跳转
router.beforeEach((to, from, next) => {
  // 需要登录的页面
  const requiresAuth = !['/', '/login', '/register'].includes(to.path)
  const isLoggedIn = store.getters.isLoggedIn

  if (requiresAuth && !isLoggedIn) {
    // 未登录访问受限页，跳转到登录
    if (to.path !== '/login') {
      next('/login')
    } else {
      next()
    }
  } else if (to.path === '/login' && isLoggedIn) {
    // 已登录访问登录页，跳转到dashboard
    next('/dashboard')
  } else {
    next()
  }
})

export default router
