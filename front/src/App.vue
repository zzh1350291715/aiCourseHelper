<template>
  <div id="app">
    <!-- 顶部导航栏始终存在 -->
    <el-header class="app-header">
      <div class="header-left">
        <router-link to="/" class="logo">
          <h2>在线学习平台</h2>
        </router-link>
      </div>
      <div class="header-right">
        <template v-if="!isLoggedIn">
          <el-button type="text" @click="$router.push('/login')">登录</el-button>
          <el-button type="primary" @click="$router.push('/register')">注册</el-button>
        </template>
        <template v-else>
          <el-dropdown @command="handleCommand">
            <span class="el-dropdown-link">
              {{ currentUser.username }} <i class="el-icon-arrow-down el-icon--right"></i>
            </span>
            <el-dropdown-menu slot="dropdown">
              <el-dropdown-item command="profile">个人中心</el-dropdown-item>
              <el-dropdown-item command="dashboard" v-if="currentUser.role === 'TEACHER'">管理面板</el-dropdown-item>
              <el-dropdown-item command="logout" divided>退出登录</el-dropdown-item>
            </el-dropdown-menu>
          </el-dropdown>
        </template>
      </div>
    </el-header>

    <!-- 如果当前是 admin 页面，不要侧边栏，直接显示内容 -->
    <div v-if="$route.path === '/admin'">
      <router-view />
    </div>

    <!-- 其余页面使用完整布局：侧边栏 + 内容 -->
    <el-container v-else>
      <el-aside width="200px" class="app-aside" v-if="isLoggedIn">
        <el-menu
          :default-active="$route.path"
          class="el-menu-vertical"
          router
          background-color="#545c64"
          text-color="#fff"
          active-text-color="#ffd04b"
        >
          <el-menu-item index="/dashboard">
            <i class="el-icon-s-home"></i>
            <span slot="title">首页</span>
          </el-menu-item>
          <el-menu-item index="/courses">
            <i class="el-icon-reading"></i>
            <span slot="title">课程列表</span>
          </el-menu-item>

          <template v-if="currentUser.role === 'TEACHER'">
            <el-submenu index="instructor">
              <template slot="title">
                <i class="el-icon-s-tools"></i>
                <span>讲师功能</span>
              </template>
              <el-menu-item index="/instructor/courses">我的课程</el-menu-item>
              <el-menu-item index="/instructor/create-course">创建课程</el-menu-item>
              <el-menu-item index="/instructor/knowledge-base">知识库管理</el-menu-item>
            </el-submenu>
          </template>

          <template v-if="currentUser.role === 'STUDENT'">
            <el-menu-item index="/student/progress">
              <i class="el-icon-trophy"></i>
              <span slot="title">学习进度</span>
            </el-menu-item>
            <el-menu-item index="/student/enrolled">
              <i class="el-icon-star-on"></i>
              <span slot="title">我的课程</span>
            </el-menu-item>
          </template>
        </el-menu>
      </el-aside>

      <el-main class="app-main">
        <router-view />
      </el-main>
    </el-container>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'App',
  computed: {
    ...mapGetters(['isLoggedIn', 'currentUser'])
  },
  created() {
    // 初始化用户状态
    this.$store.dispatch('initAuth')
  },
  methods: {
    handleCommand(command) {
      switch (command) {
        case 'profile':
          this.$router.push('/profile')
          break
        case 'dashboard':
          this.$router.push('/instructor/dashboard')
          break
        case 'logout':
          this.$store.dispatch('logout')
          this.$router.push('/login')
          break
      }
    }
  }
}
</script>

<style>
* {
  margin: 0;
  padding: 0;
  box-sizing: border-box;
}

#app {
  font-family: 'Helvetica Neue', Helvetica, 'PingFang SC', 'Hiragino Sans GB', Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  height: 100vh;
  width: 100vw;
  overflow: hidden; /* 可选，避免某些滚动条 */
}

.app-header {
  background: #fff;
  border-bottom: 1px solid #e6e6e6;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 20px;
}

.header-left .logo {
  text-decoration: none;
  color: #409EFF;
}

.header-left h2 {
  margin: 0;
}

.header-right .el-dropdown-link {
  cursor: pointer;
  color: #606266;
}

.app-aside {
  background-color: #545c64;
}

.app-main {
  background-color: #f5f5f5;
  padding: 0px;
}

.el-container {
  height: 100vh;
}
html,body {
  margin: 0px;
  padding: 0px;
  caret-color: transparent;
  user-select: none; 
}
</style>
