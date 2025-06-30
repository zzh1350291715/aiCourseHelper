<template>
  <div class="my-progress">
    <div class="page-header">
      <h2>我的学习进度</h2>
    </div>

    <!-- 总体进度统计 -->
    <el-row :gutter="20" style="margin-bottom: 20px;">
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ totalCourses }}</div>
            <div class="stat-label">已报名课程</div>
          </div>
          <i class="el-icon-reading stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ completedMaterials }}</div>
            <div class="stat-label">完成材料</div>
          </div>
          <i class="el-icon-check stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ totalStudyTime }}</div>
            <div class="stat-label">学习时长(小时)</div>
          </div>
          <i class="el-icon-time stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="6">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ overallProgress }}%</div>
            <div class="stat-label">总体进度</div>
          </div>
          <i class="el-icon-trophy stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>

    <!-- 课程进度详情 -->
    <el-card>
      <div slot="header">
        <span>课程进度详情</span>
      </div>
      
      <el-table :data="courseProgress" style="width: 100%" v-loading="loading">
        <el-table-column prop="courseName" label="课程名称" min-width="150" />
        <el-table-column label="进度" width="200">
          <template slot-scope="scope">
            <el-progress 
              :percentage="scope.row.progressPercentage" 
              :color="getProgressColor(scope.row.progressPercentage)">
            </el-progress>
          </template>
        </el-table-column>
        <el-table-column prop="completedMaterials" label="完成材料" width="120">
          <template slot-scope="scope">
            {{ scope.row.completedMaterials }}/{{ scope.row.totalMaterials }}
          </template>
        </el-table-column>
        <el-table-column prop="lastStudyTime" label="最后学习时间" width="180">
          <template slot-scope="scope">
            {{ formatDate(scope.row.lastStudyTime) }}
          </template>
        </el-table-column>
        <el-table-column prop="studyTimeHours" label="学习时长" width="120">
          <template slot-scope="scope">
            {{ scope.row.studyTimeHours }}小时
          </template>
        </el-table-column>
        <el-table-column label="操作" width="120">
          <template slot-scope="scope">
            <el-button size="mini" type="primary" @click="continueLearning(scope.row)">
              继续学习
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 学习建议 -->
    <el-card style="margin-top: 20px;">
      <div slot="header">
        <span>学习建议</span>
      </div>
      <div class="suggestions">
        <el-alert
          v-for="suggestion in suggestions"
          :key="suggestion.id"
          :title="suggestion.title"
          :description="suggestion.description"
          :type="suggestion.type"
          show-icon
          style="margin-bottom: 10px;">
        </el-alert>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
import { mapGetters } from 'vuex'

export default {
  name: 'MyProgress',
  data() {
    return {
      courseProgress: [],
      loading: false,
      suggestions: [
        {
          id: 1,
          title: '保持学习节奏',
          description: '建议每天至少学习30分钟，保持良好的学习习惯',
          type: 'info'
        },
        {
          id: 2,
          title: '复习已学内容',
          description: '定期复习之前学过的内容，巩固知识点',
          type: 'success'
        }
      ]
    }
  },
  computed: {
    ...mapGetters(['currentUser']),
    totalCourses() {
      return this.courseProgress.length
    },
    completedMaterials() {
      return this.courseProgress.reduce((total, course) => total + course.completedMaterials, 0)
    },
    totalStudyTime() {
      return this.courseProgress.reduce((total, course) => total + course.studyTimeHours, 0)
    },
    overallProgress() {
      if (this.courseProgress.length === 0) return 0
      const totalProgress = this.courseProgress.reduce((total, course) => total + course.progressPercentage, 0)
      return Math.round(totalProgress / this.courseProgress.length)
    }
  },
  created() {
    this.fetchProgress()
  },
  methods: {
    async fetchProgress() {
      this.loading = true
      try {
        const data = await request.get(`/api/student-progress/student/${this.currentUser.id}/courses`)
        this.courseProgress = data || []
      } catch (error) {
        console.error('获取学习进度失败:', error)
        this.$message.error('获取学习进度失败')
        // 使用模拟数据
        this.courseProgress = [
          {
            courseName: 'JavaScript入门',
            progressPercentage: 75,
            completedMaterials: 6,
            totalMaterials: 8,
            lastStudyTime: new Date().toISOString(),
            studyTimeHours: 12
          },
          {
            courseName: 'Vue.js实战',
            progressPercentage: 40,
            completedMaterials: 3,
            totalMaterials: 10,
            lastStudyTime: new Date(Date.now() - 86400000).toISOString(),
            studyTimeHours: 8
          }
        ]
      } finally {
        this.loading = false
      }
    },
    
    continueLearning(course) {
      // 这里应该跳转到具体的课程学习页面
      this.$router.push({ name: 'CourseMaterials', params: { courseId: course.courseId } })
    },
    
    getProgressColor(percentage) {
      if (percentage < 30) return '#f56c6c'
      if (percentage < 70) return '#e6a23c'
      return '#67c23a'
    },
    
    formatDate(dateString) {
      if (!dateString) return '未开始'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style scoped>
.my-progress {
  padding: 20px;
}

.page-header {
  margin-bottom: 20px;
}

.page-header h2 {
  margin: 0;
  color: #303133;
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
  font-size: 32px;
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
  font-size: 40px;
  color: #E4E7ED;
  z-index: 1;
}

.suggestions {
  max-height: 200px;
  overflow-y: auto;
}
</style> 