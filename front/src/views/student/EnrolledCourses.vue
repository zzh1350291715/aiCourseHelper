<template>
  <div class="enrolled-courses">
    <div class="page-header">
      <h2>我的课程</h2>
      <el-button type="primary" @click="$router.push('/courses')">
        浏览更多课程
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card v-if="courses.length === 0" class="empty-state">
          <el-empty description="您还没有报名任何课程">
            <el-button type="primary" @click="$router.push('/courses')">
              去逛逛
            </el-button>
          </el-empty>
        </el-card>
        
        <el-row :gutter="20" v-else>
          <el-col :span="8" v-for="course in courses" :key="course.id">
            <el-card class="course-card" shadow="hover">
              <div class="course-cover">
                <img v-if="course.coverUrl" :src="course.coverUrl" :alt="course.title" />
                <div v-else class="default-cover">
                  <i class="el-icon-reading"></i>
                </div>
                <div class="course-progress-overlay">
                  <el-progress 
                    type="circle" 
                    :percentage="course.progress || 0" 
                    :width="60"
                    :color="getProgressColor(course.progress || 0)">
                  </el-progress>
                </div>
              </div>
              
              <div class="course-info">
                <h3 class="course-title">{{ course.title }}</h3>
                <p class="course-instructor">讲师：{{ course.instructor?.username || '未知' }}</p>
                <p class="course-description">{{ course.description }}</p>
                
                <div class="course-stats">
                  <span class="stat-item">
                    <i class="el-icon-document"></i>
                    {{ course.materialCount || 0 }} 个材料
                  </span>
                  <span class="stat-item">
                    <i class="el-icon-time"></i>
                    {{ course.studyTime || 0 }} 小时
                  </span>
                </div>
                
                <div class="course-actions">
                  <el-button type="primary" size="small" @click="continueLearning(course)">
                    {{ course.progress > 0 ? '继续学习' : '开始学习' }}
                  </el-button>
                  <el-button size="small" @click="viewMaterials(course)">
                    查看材料
                  </el-button>
                  <el-button size="small" @click="openChat(course)">
                    AI问答
                  </el-button>
                </div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import { mapGetters } from 'vuex'

export default {
  name: 'EnrolledCourses',
  data() {
    return {
      courses: [],
      loading: false
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  created() {
    this.fetchEnrolledCourses()
  },
  methods: {
    async fetchEnrolledCourses() {
      this.loading = true
      try {
        // 这里应该调用获取学生已报名课程的API
        // 目前后端可能还没有这个接口，所以使用模拟数据
        this.courses = [
          {
            id: 1,
            title: 'JavaScript入门教程',
            description: '从零开始学习JavaScript编程语言',
            instructor: { username: '张老师' },
            progress: 75,
            materialCount: 8,
            studyTime: 12,
            coverUrl: null
          },
          {
            id: 2,
            title: 'Vue.js实战开发',
            description: '使用Vue.js构建现代Web应用',
            instructor: { username: '李老师' },
            progress: 40,
            materialCount: 10,
            studyTime: 8,
            coverUrl: null
          }
        ]
      } catch (error) {
        console.error('获取已报名课程失败:', error)
        this.$message.error('获取已报名课程失败')
      } finally {
        this.loading = false
      }
    },
    
    continueLearning(course) {
      this.$router.push({ name: 'CourseMaterials', params: { courseId: course.id } })
    },
    
    viewMaterials(course) {
      this.$router.push({ name: 'CourseMaterials', params: { courseId: course.id } })
    },
    
    openChat(course) {
      this.$router.push({ name: 'CourseChat', params: { courseId: course.id } })
    },
    
    getProgressColor(percentage) {
      if (percentage < 30) return '#f56c6c'
      if (percentage < 70) return '#e6a23c'
      return '#67c23a'
    }
  }
}
</script>

<style scoped>
.enrolled-courses {
  padding: 20px;
}

.page-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
}

.empty-state {
  text-align: center;
  padding: 60px 20px;
}

.course-card {
  margin-bottom: 20px;
  transition: transform 0.3s ease;
}

.course-card:hover {
  transform: translateY(-5px);
}

.course-cover {
  position: relative;
  height: 160px;
  overflow: hidden;
  border-radius: 4px;
  margin-bottom: 15px;
}

.course-cover img {
  width: 100%;
  height: 100%;
  object-fit: cover;
}

.default-cover {
  width: 100%;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 48px;
}

.course-progress-overlay {
  position: absolute;
  top: 10px;
  right: 10px;
  background: rgba(255, 255, 255, 0.9);
  border-radius: 50%;
  padding: 5px;
}

.course-info {
  padding: 10px 0;
}

.course-title {
  font-size: 18px;
  margin: 0 0 8px 0;
  color: #303133;
  font-weight: 500;
}

.course-instructor {
  font-size: 14px;
  color: #909399;
  margin: 0 0 8px 0;
}

.course-description {
  font-size: 14px;
  color: #606266;
  margin: 0 0 15px 0;
  line-height: 1.5;
  display: -webkit-box;
  -webkit-line-clamp: 2;
  -webkit-box-orient: vertical;
  overflow: hidden;
}

.course-stats {
  display: flex;
  gap: 15px;
  margin-bottom: 15px;
  font-size: 12px;
  color: #909399;
}

.stat-item i {
  margin-right: 4px;
}

.course-actions {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.course-actions .el-button {
  flex: 1;
  min-width: 0;
}
</style> 