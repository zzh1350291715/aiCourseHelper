<template>
  <div class="course-detail">
    <!-- 课程头部信息 -->
    <el-card class="course-header" shadow="never">
      <div class="course-header-content">
        <div class="course-info">
          <h1 class="course-title">{{ course.title || '加载中...' }}</h1>
          <p class="course-description">{{ course.description || '暂无描述' }}</p>
          
          <div class="course-meta">
            <el-tag size="medium" type="primary">
              <i class="el-icon-user"></i>
              {{ course.instructor && course.instructor.username || '未知讲师' }}
            </el-tag>
            <el-tag size="medium" type="success">
              <i class="el-icon-calendar"></i>
              创建时间：{{ formatDate(course.createdAt) }}
            </el-tag>
            <el-tag size="medium" type="info">
              <i class="el-icon-collection"></i>
              课程材料：{{ materialCount }}个
            </el-tag>
          </div>
        </div>

        <div class="course-actions">
          <template v-if="!isEnrolled && userRole === 'STUDENT'">
            <el-button 
              type="primary" 
              size="large" 
              @click="enrollCourse"
              :loading="enrollLoading"
            >
              <i class="el-icon-plus"></i>
              报名课程
            </el-button>
          </template>

          <template v-else-if="isEnrolled && userRole === 'STUDENT'">
            <el-button 
              type="success" 
              size="large"
              disabled
            >
              <i class="el-icon-check"></i>
              已报名
            </el-button>
            <el-button 
              type="danger" 
              size="large" 
              plain
              @click="unenrollCourse"
              :loading="unenrollLoading"
            >
              退课
            </el-button>
          </template>

          <template v-if="userRole === 'INSTRUCTOR' && isOwner">
            <el-button 
              type="warning" 
              size="large"
              @click="$router.push('/instructor/manage-materials/' + course.id)"
            >
              <i class="el-icon-edit"></i>
              管理课程
            </el-button>
          </template>
        </div>
      </div>
    </el-card>

    <!-- 课程统计信息 -->
    <el-row :gutter="20" class="stats-section">
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.studentCount }}</div>
            <div class="stat-label">已报名学生</div>
          </div>
          <i class="el-icon-user-solid stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.materialCount }}</div>
            <div class="stat-label">课程材料</div>
          </div>
          <i class="el-icon-document stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :xs="24" :sm="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ stats.avgProgress }}%</div>
            <div class="stat-label">平均进度</div>
          </div>
          <i class="el-icon-pie-chart stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 学习进度（仅学生可见） -->
    <el-card v-if="userRole === 'STUDENT' && isEnrolled" class="progress-section">
      <div slot="header">
        <i class="el-icon-progress"></i>
        我的学习进度
      </div>
      <div class="progress-content">
        <div class="progress-info">
          <h3>总体进度：{{ myProgress }}%</h3>
          <el-progress 
            :percentage="myProgress" 
            :color="progressColor"
            :stroke-width="8"
            text-inside
          ></el-progress>
        </div>
        <div class="progress-details">
          <p>已完成材料：{{ completedMaterials }}/{{ totalMaterials }}</p>
          <p>最后学习时间：{{ formatDate(lastStudyTime) }}</p>
        </div>
      </div>
    </el-card>

    <!-- 课程导航 -->
    <el-card class="navigation-section">
      <div slot="header">
        <i class="el-icon-menu"></i>
        课程导航
      </div>
      <el-row :gutter="20">
        <el-col :xs="24" :sm="8">
          <div class="nav-item" @click="goToMaterials">
            <i class="el-icon-folder-opened"></i>
            <h4>课程资料</h4>
            <p>查看课程相关文档和材料</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="nav-item" @click="goToChat">
            <i class="el-icon-chat-line-round"></i>
            <h4>AI智能问答</h4>
            <p>与AI助手讨论课程内容</p>
          </div>
        </el-col>
        <el-col :xs="24" :sm="8">
          <div class="nav-item" @click="goToProgress">
            <i class="el-icon-data-analysis"></i>
            <h4>学习进度</h4>
            <p>查看详细的学习统计</p>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程材料预览 -->
    <el-card class="materials-preview">
      <div slot="header">
        <i class="el-icon-document"></i>
        课程材料预览
        <el-button 
          type="text" 
          style="float: right; padding: 3px 0"
          @click="goToMaterials"
        >
          查看全部
        </el-button>
      </div>
      <el-table 
        :data="previewMaterials" 
        style="width: 100%"
        v-loading="materialsLoading"
      >
        <el-table-column prop="title" label="资料名称" min-width="200">
          <template slot-scope="scope">
            <i :class="getMaterialIcon(scope.row.type)"></i>
            {{ scope.row.title }}
          </template>
        </el-table-column>
        <el-table-column prop="type" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag size="mini" :type="getMaterialTagType(scope.row.type)">
              {{ getMaterialTypeName(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间" width="150">
          <template slot-scope="scope">
            {{ formatDate(scope.row.uploadTime) }}
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading-container">
      <el-skeleton :rows="5" animated />
    </div>

    <!-- 错误状态 -->
    <el-empty v-if="!loading && !course" description="未找到课程信息">
      <el-button type="primary" @click="$router.back()">返回</el-button>
    </el-empty>
  </div>
</template>

<script>
import request from '@/utils/request'
import { mapGetters } from 'vuex'

export default {
  name: 'CourseDetail',
  data() {
    return {
      course: null,
      loading: true,
      materialsLoading: false,
      enrollLoading: false,
      unenrollLoading: false,
      isEnrolled: false,
      materialCount: 0,
      previewMaterials: [],
      stats: {
        studentCount: 0,
        materialCount: 0,
        avgProgress: 0
      },
      myProgress: 0,
      completedMaterials: 0,
      totalMaterials: 0,
      lastStudyTime: null
    }
  },
  computed: {
    ...mapGetters(['user', 'userRole']),
    isOwner() {
      return this.course && this.user && this.course.instructorId === this.user.id
    },
    progressColor() {
      if (this.myProgress < 30) return '#f56c6c'
      if (this.myProgress < 70) return '#e6a23c'
      return '#67c23a'
    }
  },
  created() {
    this.fetchCourseData()
  },
  methods: {
    async fetchCourseData() {
      const id = this.$route.params.id
      try {
        const [courseData, materialsData] = await Promise.all([
          request.get(`/api/courses/${id}`),
          request.get(`/api/course-materials/course/${id}`)
        ])
        
        this.course = courseData
        this.previewMaterials = materialsData.slice(0, 5) // 只显示前5个
        this.materialCount = materialsData.length
        this.stats.materialCount = materialsData.length
        
        // 如果是学生，检查报名状态和学习进度
        if (this.userType === 'STUDENT') {
          await this.checkEnrollmentStatus()
          await this.fetchStudentProgress()
        }
        
        // 获取课程统计信息
        await this.fetchCourseStats()
        
      } catch (error) {
        console.error('获取课程数据失败:', error)
        this.$message.error('获取课程信息失败')
      } finally {
        this.loading = false
      }
    },

    async checkEnrollmentStatus() {
      try {
        const response = await request.get(`/api/student-progress/enrollment-status/${this.course.id}`)
        this.isEnrolled = response.enrolled
      } catch (error) {
        console.error('检查报名状态失败:', error)
      }
    },

    async fetchStudentProgress() {
      if (!this.isEnrolled) return
      
      try {
        const progress = await request.get(`/api/student-progress/course/${this.course.id}`)
        this.myProgress = Math.round(progress.overallProgress || 0)
        this.completedMaterials = progress.completedMaterials || 0
        this.totalMaterials = progress.totalMaterials || 0
        this.lastStudyTime = progress.lastStudyTime
      } catch (error) {
        console.error('获取学习进度失败:', error)
      }
    },

    async fetchCourseStats() {
      try {
        const stats = await request.get(`/api/courses/${this.course.id}/stats`)
        this.stats = {
          studentCount: stats.studentCount || 0,
          materialCount: this.materialCount,
          avgProgress: Math.round(stats.avgProgress || 0)
        }
      } catch (error) {
        console.error('获取课程统计失败:', error)
      }
    },

    async enrollCourse() {
      this.enrollLoading = true
      try {
        await request.post(`/api/student-progress/enroll/${this.course.id}`)
        this.isEnrolled = true
        this.$message.success('报名成功！')
        await this.fetchStudentProgress()
        await this.fetchCourseStats()
      } catch (error) {
        console.error('报名失败:', error)
        this.$message.error('报名失败，请重试')
      } finally {
        this.enrollLoading = false
      }
    },

    async unenrollCourse() {
      this.$confirm('确定要退课吗？退课后将失去所有学习进度。', '确认退课', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        this.unenrollLoading = true
        try {
          await request.delete(`/api/student-progress/unenroll/${this.course.id}`)
          this.isEnrolled = false
          this.myProgress = 0
          this.$message.success('退课成功')
          await this.fetchCourseStats()
        } catch (error) {
          console.error('退课失败:', error)
          this.$message.error('退课失败，请重试')
        } finally {
          this.unenrollLoading = false
        }
      }).catch(() => {
        // 用户取消
      })
    },

    goToMaterials() {
      this.$router.push({ name: 'CourseMaterials', params: { courseId: this.course.id } })
    },
    
    goToChat() {
      this.$router.push({ name: 'CourseChat', params: { courseId: this.course.id } })
    },
    
    goToProgress() {
      this.$router.push({ name: 'CourseProgress', params: { courseId: this.course.id } })
    },

    formatDate(date) {
      if (!date) return '未知'
      return new Date(date).toLocaleDateString('zh-CN')
    },

    getMaterialIcon(type) {
      const icons = {
        'PDF': 'el-icon-document',
        'WORD': 'el-icon-edit-outline',
        'POWERPOINT': 'el-icon-present',
        'VIDEO': 'el-icon-video-play',
        'AUDIO': 'el-icon-microphone',
        'OTHER': 'el-icon-paperclip'
      }
      return icons[type] || 'el-icon-document'
    },

    getMaterialTagType(type) {
      const types = {
        'PDF': 'danger',
        'WORD': 'primary',
        'POWERPOINT': 'warning',
        'VIDEO': 'success',
        'AUDIO': 'info',
        'OTHER': ''
      }
      return types[type] || ''
    },

    getMaterialTypeName(type) {
      const names = {
        'PDF': 'PDF',
        'WORD': 'Word',
        'POWERPOINT': 'PPT',
        'VIDEO': '视频',
        'AUDIO': '音频',
        'OTHER': '其他'
      }
      return names[type] || '未知'
    }
  }
}
</script>

<style scoped>
.course-detail {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.course-header {
  margin-bottom: 20px;
}

.course-header-content {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  flex-wrap: wrap;
}

.course-info {
  flex: 1;
  min-width: 300px;
}

.course-title {
  margin: 0 0 10px 0;
  font-size: 28px;
  color: #303133;
  font-weight: 600;
}

.course-description {
  margin: 0 0 20px 0;
  font-size: 16px;
  color: #606266;
  line-height: 1.6;
}

.course-meta {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
}

.course-meta .el-tag {
  padding: 8px 12px;
}

.course-actions {
  display: flex;
  flex-direction: column;
  gap: 10px;
  align-items: flex-end;
}

.stats-section {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
  position: relative;
  overflow: hidden;
}

.stat-content {
  position: relative;
  z-index: 2;
}

.stat-number {
  font-size: 32px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #909399;
}

.stat-icon {
  position: absolute;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  font-size: 48px;
  color: #f0f0f0;
  z-index: 1;
}

.progress-section,
.navigation-section,
.materials-preview {
  margin-bottom: 20px;
}

.progress-content {
  display: flex;
  justify-content: space-between;
  align-items: center;
  flex-wrap: wrap;
}

.progress-info {
  flex: 1;
  min-width: 300px;
}

.progress-info h3 {
  margin: 0 0 10px 0;
  color: #303133;
}

.progress-details {
  margin-left: 20px;
}

.progress-details p {
  margin: 5px 0;
  color: #606266;
}

.nav-item {
  text-align: center;
  padding: 20px;
  border: 2px solid #f0f0f0;
  border-radius: 8px;
  cursor: pointer;
  transition: all 0.3s;
  margin-bottom: 10px;
}

.nav-item:hover {
  border-color: #409eff;
  background-color: #f0f8ff;
}

.nav-item i {
  font-size: 32px;
  color: #409eff;
  margin-bottom: 10px;
}

.nav-item h4 {
  margin: 10px 0 5px 0;
  color: #303133;
}

.nav-item p {
  margin: 0;
  font-size: 14px;
  color: #909399;
}

.loading-container {
  padding: 20px;
}

@media (max-width: 768px) {
  .course-header-content {
    flex-direction: column;
  }
  
  .course-actions {
    width: 100%;
    margin-top: 20px;
    align-items: stretch;
  }
  
  .progress-content {
    flex-direction: column;
    align-items: stretch;
  }
  
  .progress-details {
    margin-left: 0;
    margin-top: 15px;
  }
}
</style> 