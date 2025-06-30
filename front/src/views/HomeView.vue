<template>
  <div class="home">
    <!-- Hero Section -->
    <section class="hero">
      <div class="hero-content">
        <h1 class="hero-title">欢迎来到智能在线学习平台</h1>
        <p class="hero-subtitle">AI赋能的现代化学习体验，让知识触手可及</p>
        <div class="hero-actions">
          <el-button type="primary" size="large" @click="handleGetStarted">
            开始学习
          </el-button>
          <el-button size="large" @click="scrollToFeatures">
            了解更多
          </el-button>
        </div>
      </div>
      <div class="hero-image">
        <i class="el-icon-reading hero-icon"></i>
      </div>
    </section>

    <!-- Features Section -->
    <section class="features" ref="features">
      <div class="container">
        <h2 class="section-title">平台特色</h2>
        <el-row :gutter="30">
          <el-col :span="8">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-cpu"></i>
              </div>
              <h3>AI智能助手</h3>
              <p>24/7在线AI助手，实时解答学习疑问，提供个性化学习建议</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-data-analysis"></i>
              </div>
              <h3>学习进度跟踪</h3>
              <p>智能跟踪学习进度，数据可视化展示，帮助制定高效学习计划</p>
            </div>
          </el-col>
          <el-col :span="8">
            <div class="feature-card">
              <div class="feature-icon">
                <i class="el-icon-document"></i>
              </div>
              <h3>丰富学习资源</h3>
              <p>多媒体课程材料，支持视频、文档、互动练习等多种学习形式</p>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- Stats Section -->
    <section class="stats">
      <div class="container">
        <el-row :gutter="20">
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">1000+</div>
              <div class="stat-label">优质课程</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">50000+</div>
              <div class="stat-label">学习用户</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">100+</div>
              <div class="stat-label">专业讲师</div>
            </div>
          </el-col>
          <el-col :span="6">
            <div class="stat-item">
              <div class="stat-number">95%</div>
              <div class="stat-label">学员满意度</div>
            </div>
          </el-col>
        </el-row>
      </div>
    </section>

    <!-- CTA Section -->
    <section class="cta">
      <div class="container">
        <h2>开启您的学习之旅</h2>
        <p>立即注册，体验AI赋能的智能学习</p>
        <div class="cta-actions">
          <el-button type="primary" size="large" @click="handleRegister">
            免费注册
          </el-button>
          <el-button type="text" size="large" @click="handleLogin">
            已有账号？立即登录
          </el-button>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'

export default {
  name: 'HomeView',
  computed: {
    ...mapGetters(['isLoggedIn', 'currentUser'])
  },
  methods: {
    handleGetStarted() {
      if (this.isLoggedIn) {
        if (this.currentUser.role === 'INSTRUCTOR') {
          this.$router.push('/instructor/courses')
        } else {
          this.$router.push('/courses')
        }
      } else {
        this.$router.push('/login')
      }
    },
    
    handleRegister() {
      this.$router.push('/register')
    },
    
    handleLogin() {
      this.$router.push('/login')
    },
    
    scrollToFeatures() {
      this.$refs.features.scrollIntoView({ behavior: 'smooth' })
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 100vh;
}

/* Hero Section */
.hero {
  display: flex;
  align-items: center;
  min-height: 80vh;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0 5%;
}

.hero-content {
  flex: 1;
  max-width: 600px;
}

.hero-title {
  font-size: 48px;
  font-weight: 300;
  margin-bottom: 20px;
  line-height: 1.2;
}

.hero-subtitle {
  font-size: 18px;
  margin-bottom: 40px;
  opacity: 0.9;
  line-height: 1.6;
}

.hero-actions {
  display: flex;
  gap: 20px;
}

.hero-image {
  flex: 1;
  display: flex;
  justify-content: center;
  align-items: center;
}

.hero-icon {
  font-size: 200px;
  opacity: 0.8;
}

/* Features Section */
.features {
  padding: 80px 0;
  background: #f8f9fa;
}

.container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 0 20px;
}

.section-title {
  text-align: center;
  font-size: 36px;
  margin-bottom: 60px;
  color: #2c3e50;
}

.feature-card {
  text-align: center;
  padding: 40px 20px;
  background: white;
  border-radius: 8px;
  box-shadow: 0 4px 12px rgba(0, 0, 0, 0.1);
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 48px;
  color: #409EFF;
  margin-bottom: 20px;
}

.feature-card h3 {
  font-size: 24px;
  margin-bottom: 15px;
  color: #2c3e50;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.6;
}

/* Stats Section */
.stats {
  padding: 60px 0;
  background: #2c3e50;
  color: white;
}

.stat-item {
  text-align: center;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #3498db;
  margin-bottom: 10px;
}

.stat-label {
  font-size: 16px;
  opacity: 0.8;
}

/* CTA Section */
.cta {
  padding: 80px 0;
  background: white;
  text-align: center;
}

.cta h2 {
  font-size: 36px;
  margin-bottom: 20px;
  color: #2c3e50;
}

.cta p {
  font-size: 18px;
  margin-bottom: 40px;
  color: #7f8c8d;
}

.cta-actions {
  display: flex;
  justify-content: center;
  gap: 20px;
  flex-wrap: wrap;
}

/* Responsive */
@media (max-width: 768px) {
  .hero {
    flex-direction: column;
    text-align: center;
    padding: 40px 20px;
  }
  
  .hero-title {
    font-size: 32px;
  }
  
  .hero-icon {
    font-size: 120px;
    margin-top: 40px;
  }
  
  .features {
    padding: 60px 0;
  }
  
  .section-title {
    font-size: 28px;
  }
  
  .hero-actions,
  .cta-actions {
    flex-direction: column;
    align-items: center;
  }
}
</style>
