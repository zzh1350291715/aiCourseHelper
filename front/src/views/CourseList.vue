<template>
  <div class="course-list">
    <!-- 搜索和筛选栏 -->
    <el-card class="filter-card">
      <el-row :gutter="20">
        <el-col :span="8">
          <el-input
            v-model="searchKeyword"
            placeholder="搜索课程名称或讲师"
            prefix-icon="el-icon-search"
            @input="handleSearch"
            clearable
          />
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedCategory" placeholder="选择分类" @change="handleFilter">
            <el-option label="全部分类" value="" />
            <el-option label="编程技术" value="PROGRAMMING" />
            <el-option label="设计创意" value="DESIGN" />
            <el-option label="商业管理" value="BUSINESS" />
            <el-option label="语言学习" value="LANGUAGE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-col>
        <el-col :span="6">
          <el-select v-model="selectedStatus" placeholder="课程状态" @change="handleFilter">
            <el-option label="全部状态" value="" />
            <el-option label="进行中" value="ACTIVE" />
            <el-option label="即将开始" value="UPCOMING" />
            <el-option label="已结束" value="COMPLETED" />
          </el-select>
        </el-col>
        <el-col :span="4">
          <el-button type="primary" @click="resetFilters">重置</el-button>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程统计 -->
    <el-card class="stats-card">
      <el-row :gutter="20">
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ totalCourses }}</div>
            <div class="stat-label">总课程数</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ activeCourses }}</div>
            <div class="stat-label">进行中</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ enrolledCourses }}</div>
            <div class="stat-label">我的课程</div>
          </div>
        </el-col>
        <el-col :span="6">
          <div class="stat-item">
            <div class="stat-number">{{ completedCourses }}</div>
            <div class="stat-label">已完成</div>
          </div>
        </el-col>
      </el-row>
    </el-card>

    <!-- 课程卡片网格 -->
    <div class="courses-grid" v-loading="loading">
      <el-row :gutter="20" v-if="filteredCourses.length > 0">
        <el-col :span="8" v-for="course in paginatedCourses" :key="course.id">
          <course-card
            :course="course"
            :is-enrolled="isEnrolled(course.id)"
            @view-detail="viewDetail"
            @enroll="enrollCourse"
          />
        </el-col>
      </el-row>
      
      <!-- 空状态 -->
      <el-empty v-else description="暂无课程数据" />
    </div>

    <!-- 分页 -->
    <div class="pagination-wrapper" v-if="filteredCourses.length > pageSize">
      <el-pagination
        @current-change="handleCurrentChange"
        :current-page="currentPage"
        :page-size="pageSize"
        :total="filteredCourses.length"
        layout="total, prev, pager, next, jumper"
      />
    </div>
  </div>
</template>
  
<script>
import request from '@/utils/request'
import CourseCard from '@/components/CourseCard.vue'

export default {
  name: 'CourseList',
  components: {
    CourseCard
  },
  data() {
    return {
      courses: [],
      enrolledCourseIds: [],
      loading: false,
      searchKeyword: '',
      selectedCategory: '',
      selectedStatus: '',
      currentPage: 1,
      pageSize: 9
    }
  },
  computed: {
    filteredCourses() {
      let filtered = this.courses
      
      // 按关键词搜索
      if (this.searchKeyword) {
        const keyword = this.searchKeyword.toLowerCase()
        filtered = filtered.filter(course => 
          course.title.toLowerCase().includes(keyword) ||
          course.description?.toLowerCase().includes(keyword) ||
          course.instructor?.username?.toLowerCase().includes(keyword)
        )
      }
      
      // 按分类筛选
      if (this.selectedCategory) {
        filtered = filtered.filter(course => course.category === this.selectedCategory)
      }
      
      // 按状态筛选
      if (this.selectedStatus) {
        filtered = filtered.filter(course => course.status === this.selectedStatus)
      }
      
      return filtered
    },
    
    paginatedCourses() {
      const start = (this.currentPage - 1) * this.pageSize
      const end = start + this.pageSize
      return this.filteredCourses.slice(start, end)
    },
    
    totalCourses() {
      return this.courses.length
    },
    
    activeCourses() {
      return this.courses.filter(course => course.status === 'ACTIVE').length
    },
    
    enrolledCourses() {
      return this.enrolledCourseIds.length
    },
    
    completedCourses() {
      return this.courses.filter(course => 
        this.isEnrolled(course.id) && course.status === 'COMPLETED'
      ).length
    }
  },
  created() {
    this.fetchCourses()
    this.fetchEnrolledCourses()
  },
  methods: {
    async fetchCourses() {
      this.loading = true
      try {
        const data = await request.get('/api/courses')
        this.courses = data || []
      } catch (error) {
        this.$message.error('获取课程列表失败')
        this.courses = []
      } finally {
        this.loading = false
      }
    },
    
    async fetchEnrolledCourses() {
      try {
        const data = await request.get('/api/student-progress/enrolled')
        this.enrolledCourseIds = data.map(item => item.courseId) || []
      } catch (error) {
        console.error('获取已报名课程失败:', error)
        this.enrolledCourseIds = []
      }
    },
    
    handleSearch() {
      this.currentPage = 1
    },
    
    handleFilter() {
      this.currentPage = 1
    },
    
    resetFilters() {
      this.searchKeyword = ''
      this.selectedCategory = ''
      this.selectedStatus = ''
      this.currentPage = 1
    },
    
    handleCurrentChange(page) {
      this.currentPage = page
    },
    
    isEnrolled(courseId) {
      return this.enrolledCourseIds.includes(courseId)
    },
    
    viewDetail(course) {
      this.$router.push({ name: 'CourseDetail', params: { id: course.id } })
    },
    
    async enrollCourse(course) {
      try {
        await request.post(`/api/student-progress/enroll/${course.id}`)
        this.$message.success(`成功报名课程：${course.title}`)
        await this.fetchEnrolledCourses()
      } catch (error) {
        this.$message.error('报名失败，请重试')
      }
    }
  }
}
</script>
  
<style scoped>
.course-list {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.filter-card {
  margin-bottom: 20px;
}

.stats-card {
  margin-bottom: 30px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 28px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 8px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.courses-grid {
  min-height: 400px;
}

.pagination-wrapper {
  display: flex;
  justify-content: center;
  margin-top: 30px;
}

/* 响应式设计 */
@media (max-width: 992px) {
  .course-list {
    padding: 15px;
  }
  
  .filter-card .el-col {
    margin-bottom: 10px;
  }
  
  .stat-item {
    padding: 15px;
  }
  
  .stat-number {
    font-size: 24px;
  }
}

@media (max-width: 768px) {
  .course-list {
    padding: 10px;
  }
  
  .courses-grid .el-col {
    margin-bottom: 20px;
  }
  
  .stat-item {
    padding: 10px;
  }
  
  .stat-number {
    font-size: 20px;
  }
}
</style>