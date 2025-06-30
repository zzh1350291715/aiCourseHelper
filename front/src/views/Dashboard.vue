<template>
  <div class="dashboard">
    <el-row :gutter="20">
      <!-- 欢迎信息 -->
      <el-col :span="24">
        <el-card class="welcome-card">
          <h2>欢迎回来，{{ currentUser.username }}！</h2>
          <p>您的身份：{{ currentUser.role === 'TEACHER' ? '讲师' : '学生' }}</p>
        </el-card>
      </el-col>
    </el-row>

    <!-- 讲师仪表板 -->
    <template v-if="isInstructor">
      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 统计卡片 -->
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ instructorStats.courseCount }}</div>
              <div class="stat-label">我的课程</div>
            </div>
            <i class="el-icon-reading stat-icon"></i>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ instructorStats.materialCount }}</div>
              <div class="stat-label">课程材料</div>
            </div>
            <i class="el-icon-document stat-icon"></i>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ instructorStats.studentCount }}</div>
              <div class="stat-label">学生总数</div>
            </div>
            <i class="el-icon-user stat-icon"></i>
          </el-card>
        </el-col>
      </el-row>

      <!-- 快速操作 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <div slot="header">
              <span>快速操作</span>
            </div>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-button type="primary" icon="el-icon-plus" @click="$router.push('/instructor/create-course')">
                  创建课程
                </el-button>
              </el-col>
              <el-col :span="6">
                <el-button type="success" icon="el-icon-folder-add" @click="$router.push('/instructor/knowledge-base')">
                  管理知识库
                </el-button>
              </el-col>
              <el-col :span="6">
                <el-button type="info" icon="el-icon-view" @click="$router.push('/instructor/courses')">
                  查看我的课程
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>

      <!-- 最近课程 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <div slot="header">
              <span>最近的课程</span>
            </div>
            <el-table :data="recentCourses" style="width: 100%">
              <el-table-column prop="title" label="课程名称"></el-table-column>
              <el-table-column prop="createdAt" label="创建时间"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button size="mini" @click="viewCourse(scope.row)">查看</el-button>
                  <el-button size="mini" type="primary" @click="manageMaterials(scope.row)">管理材料</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </template>

    <!-- 学生仪表板 -->
    <template v-if="isStudent">
      <el-row :gutter="20" style="margin-top: 20px;">
        <!-- 学习统计 -->
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ studentStats.enrolledCourses }}</div>
              <div class="stat-label">已报名课程</div>
            </div>
            <i class="el-icon-reading stat-icon"></i>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ studentStats.completedMaterials }}</div>
              <div class="stat-label">完成材料</div>
            </div>
            <i class="el-icon-check stat-icon"></i>
          </el-card>
        </el-col>
        
        <el-col :span="8">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ studentStats.studyHours }}</div>
              <div class="stat-label">学习时长(小时)</div>
            </div>
            <i class="el-icon-time stat-icon"></i>
          </el-card>
        </el-col>
      </el-row>

      <!-- 快速入口 -->
      <el-row :gutter="20" style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <div slot="header">
              <span>快速入口</span>
            </div>
            <el-row :gutter="20">
              <el-col :span="6">
                <el-button type="primary" icon="el-icon-search" @click="$router.push('/courses')">
                  浏览课程
                </el-button>
              </el-col>
              <el-col :span="6">
                <el-button type="success" icon="el-icon-star-on" @click="$router.push('/student/enrolled')">
                  我的课程
                </el-button>
              </el-col>
              <el-col :span="6">
                <el-button type="info" icon="el-icon-trophy" @click="$router.push('/student/progress')">
                  学习进度
                </el-button>
              </el-col>
            </el-row>
          </el-card>
        </el-col>
      </el-row>

      <!-- 最近学习 -->
      <el-row style="margin-top: 20px;">
        <el-col :span="24">
          <el-card>
            <div slot="header">
              <span>最近学习</span>
            </div>
            <el-table :data="recentStudy" style="width: 100%">
              <el-table-column prop="courseName" label="课程名称"></el-table-column>
              <el-table-column prop="materialName" label="学习材料"></el-table-column>
              <el-table-column prop="lastAccessed" label="最后访问"></el-table-column>
              <el-table-column label="操作">
                <template slot-scope="scope">
                  <el-button size="mini" @click="continueLearning(scope.row)">继续学习</el-button>
                </template>
              </el-table-column>
            </el-table>
          </el-card>
        </el-col>
      </el-row>
    </template>
  </div>
</template>

<script>
import { mapGetters } from 'vuex'
import request from '@/utils/request'

export default {
  name: 'Dashboard',
  data() {
    return {
      instructorStats: {
        courseCount: 0,
        materialCount: 0,
        studentCount: 0
      },
      studentStats: {
        enrolledCourses: 0,
        completedMaterials: 0,
        studyHours: 0
      },
      recentCourses: [],
      recentStudy: []
    }
  },
  computed: {
    ...mapGetters(['currentUser', 'isInstructor', 'isStudent'])
  },
  created() {
    this.loadDashboardData()
  },
  methods: {
    async loadDashboardData() {
      if (this.isInstructor) {
        await this.loadInstructorData()
      } else if (this.isStudent) {
        await this.loadStudentData()
      }
    },
    
    async loadInstructorData() {
      try {
        // 获取用户ID
        let userId = null
        if (this.currentUser && this.currentUser.id) {
          userId = this.currentUser.id
        } else {
          const userStr = localStorage.getItem('user')
          if (userStr) {
            const user = JSON.parse(userStr)
            userId = user.id
          }
        }
        
        if (!userId) {
          console.error('无法获取用户ID')
          return
        }
        
        // 获取讲师的课程列表
        const courses = await request.get(`/api/courses/instructor/${userId}`)
        this.recentCourses = courses.slice(0, 5) // 显示最近5个课程
        this.instructorStats.courseCount = courses.length
        
        // 获取材料统计等其他数据...
        // 这里可以添加更多API调用来获取统计数据
      } catch (error) {
        console.error('加载讲师数据失败:', error)
      }
    },
    
    async loadStudentData() {
      try {
        // 获取用户ID
        let userId = null
        if (this.currentUser && this.currentUser.id) {
          userId = this.currentUser.id
        } else {
          const userStr = localStorage.getItem('user')
          if (userStr) {
            const user = JSON.parse(userStr)
            userId = user.id
          }
        }
        
        if (!userId) {
          console.error('无法获取用户ID')
          return
        }
        
        // 获取学生的学习进度
        const progress = await request.get(`/api/student-progress/student/${userId}/courses`)
        this.studentStats.enrolledCourses = progress.length
        
        // 这里可以添加更多API调用来获取学习统计数据
      } catch (error) {
        console.error('加载学生数据失败:', error)
      }
    },
    
    viewCourse(course) {
      this.$router.push({ name: 'CourseDetail', params: { id: course.id } })
    },
    
    manageMaterials(course) {
      this.$router.push({ name: 'ManageCourseMaterials', params: { courseId: course.id } })
    },
    
    continueLearning(study) {
      this.$router.push({ name: 'CourseMaterials', params: { courseId: study.courseId } })
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 20px;
}

.welcome-card {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.welcome-card h2 {
  margin: 0 0 10px 0;
  font-size: 28px;
  font-weight: 300;
}

.welcome-card p {
  margin: 0;
  font-size: 16px;
  opacity: 0.9;
}

.stat-card {
  position: relative;
  overflow: hidden;
}

.stat-content {
  position: relative;
  z-index: 2;
}

.stat-number {
  font-size: 36px;
  font-weight: bold;
  color: #409EFF;
  line-height: 1;
}

.stat-label {
  font-size: 14px;
  color: #909399;
  margin-top: 8px;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 20px;
  font-size: 48px;
  color: #E4E7ED;
  z-index: 1;
}

.el-button {
  width: 100%;
}
</style> 