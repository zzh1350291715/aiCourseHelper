<template>
  <div class="my-courses">
    <div class="page-header">
      <h2>我的课程</h2>
      <el-button type="primary" icon="el-icon-plus" @click="$router.push('/instructor/create-course')">
        创建新课程
      </el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="24">
        <el-card>
          <el-table :data="courses" style="width: 100%" v-loading="loading">
            <el-table-column prop="id" label="ID" width="80" />
            <el-table-column prop="title" label="课程名称" min-width="150" />
            <el-table-column prop="description" label="课程描述" min-width="200" show-overflow-tooltip />
            <el-table-column prop="createdAt" label="创建时间" width="180">
              <template slot-scope="scope">
                {{ formatDate(scope.row.createdAt) }}
              </template>
            </el-table-column>
            <el-table-column label="状态" width="100">
              <template slot-scope="scope">
                <el-tag :type="scope.row.published ? 'success' : 'info'">
                  {{ scope.row.published ? '已发布' : '草稿' }}
                </el-tag>
              </template>
            </el-table-column>
            <el-table-column label="操作" width="300">
              <template slot-scope="scope">
                <el-button size="mini" @click="viewCourse(scope.row)">查看</el-button>
                <el-button size="mini" type="primary" @click="manageMaterials(scope.row)">管理材料</el-button>
                <el-button size="mini" type="success" @click="manageKnowledgeBase(scope.row)">知识库</el-button>
                <el-dropdown @command="(command) => handleCommand(command, scope.row)" style="margin-left: 10px;">
                  <el-button size="mini">
                    更多<i class="el-icon-arrow-down el-icon--right"></i>
                  </el-button>
                  <el-dropdown-menu slot="dropdown">
                    <el-dropdown-item command="edit">编辑课程</el-dropdown-item>
                    <el-dropdown-item command="publish" v-if="!scope.row.published">发布课程</el-dropdown-item>
                    <el-dropdown-item command="unpublish" v-if="scope.row.published">取消发布</el-dropdown-item>
                    <el-dropdown-item command="stats">查看统计</el-dropdown-item>
                    <el-dropdown-item command="delete" divided>删除课程</el-dropdown-item>
                  </el-dropdown-menu>
                </el-dropdown>
              </template>
            </el-table-column>
          </el-table>
        </el-card>
      </el-col>
    </el-row>

    <!-- 统计信息 -->
    <el-row :gutter="20" style="margin-top: 20px;">
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ courses.length }}</div>
            <div class="stat-label">总课程数</div>
          </div>
          <i class="el-icon-reading stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ publishedCourses }}</div>
            <div class="stat-label">已发布课程</div>
          </div>
          <i class="el-icon-check stat-icon"></i>
        </el-card>
      </el-col>
      <el-col :span="8">
        <el-card class="stat-card">
          <div class="stat-content">
            <div class="stat-number">{{ totalStudents }}</div>
            <div class="stat-label">总学生数</div>
          </div>
          <i class="el-icon-user stat-icon"></i>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import { mapGetters } from 'vuex'


export default {
  name: 'MyCourses',
  data() {
    return {
      courses: [],
      loading: false,
      totalStudents: 0
    }
  },
  computed: {
    ...mapGetters(['currentUser', 'isLoggedIn']),
    publishedCourses() {
      return this.courses.filter(course => course.published).length
    }
  },
  created() {
    this.fetchCourses()
  },
  methods: {
    async fetchCourses() {
      this.loading = true
      
      try {
        // 优先从Vuex store获取用户信息
        let userId = null
        if (this.currentUser && this.currentUser.id) {
          userId = this.currentUser.id
        } else {
          // 如果store中没有，尝试从localStorage获取
          const userStr = localStorage.getItem('user')
          if (userStr) {
            const user = JSON.parse(userStr)
            userId = user.id
          }
        }
        
        if (!userId) {
          this.$message.error('用户未登录或 userId 缺失，请重新登录')
          this.$router.push('/login')
          return
        }

        const data = await request.get(`/api/courses/instructor/${userId}`)
        this.courses = data || []
      } catch (error) {
        console.error('获取课程列表失败:', error)
        this.$message.error('获取课程列表失败')
      } finally {
        this.loading = false
      }
    },
    
    viewCourse(course) {
      this.$router.push({ name: 'CourseDetail', params: { id: course.id } })
    },
    
    manageMaterials(course) {
      this.$router.push({ name: 'ManageCourseMaterials', params: { courseId: course.id } })
    },
    
    manageKnowledgeBase(course) {
      this.$router.push({ name: 'CourseKnowledgeBase', params: { courseId: course.id } })
    },
    
    handleCommand(command, course) {
      switch (command) {
        case 'edit':
          this.editCourse(course)
          break
        case 'publish':
          this.publishCourse(course)
          break
        case 'unpublish':
          this.unpublishCourse(course)
          break
        case 'stats':
          this.viewStats(course)
          break
        case 'delete':
          this.deleteCourse(course)
          break
      }
    },
    
    editCourse(course) {
      // 这里可以打开编辑对话框或跳转到编辑页面
      this.$message.info('编辑功能开发中')
    },
    
    publishCourse(course) {
      this.$message.info('发布功能开发中')
    },
    
    unpublishCourse(course) {
      this.$message.info('取消发布功能开发中')
    },
    
    viewStats(course) {
      this.$message.info('统计功能开发中')
    },
    
    deleteCourse(course) {
      this.$confirm('确定要删除这门课程吗？此操作不可恢复！', '警告', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        this.$message.info('删除功能开发中')
      }).catch(() => {
        // 用户取消删除
      })
    },
    
    formatDate(dateString) {
      if (!dateString) return ''
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN')
    }
  }
}
</script>

<style scoped>
.my-courses {
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
</style> 