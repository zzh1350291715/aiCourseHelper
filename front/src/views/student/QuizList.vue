<template>
  <div class="student-quiz-list">
    <el-card>
      <div slot="header" class="clearfix">
        <span>我的测验</span>
      </div>

      <!-- 可参加的测验 -->
      <el-card class="quiz-section">
        <div slot="header">
          <span>可参加的测验</span>
        </div>
        <el-table :data="availableQuizzes" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="title" label="测验标题"></el-table-column>
          <el-table-column prop="courseMaterial.course.title" label="所属课程"></el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button size="mini" type="primary" @click="startQuiz(scope.row)">
                开始测验
              </el-button>
              <el-button size="mini" @click="viewQuiz(scope.row)">
                查看
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>

      <!-- 已完成的测验 -->
      <el-card class="quiz-section">
        <div slot="header">
          <span>已完成的测验</span>
        </div>
        <el-table :data="completedQuizzes" style="width: 100%">
          <el-table-column prop="id" label="ID" width="80"></el-table-column>
          <el-table-column prop="title" label="测验标题"></el-table-column>
          <el-table-column prop="courseMaterial.course.title" label="所属课程"></el-table-column>
          <el-table-column prop="createdAt" label="创建时间" width="180">
            <template slot-scope="scope">
              {{ formatDate(scope.row.createdAt) }}
            </template>
          </el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button size="mini" @click="viewResult(scope.row)">
                查看报告
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </el-card>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'StudentQuizList',
  data() {
    return {
      availableQuizzes: [],
      completedQuizzes: [],
      studentId: null
    }
  },
  mounted() {
    this.studentId = this.$store.state.user.id
    this.loadQuizzes()
  },
  methods: {
    async loadQuizzes() {
      try {
        // 加载可参加的测验
        const availableResponse = await request.get(`/api/quizzes/available?studentId=${this.studentId}`)
        this.availableQuizzes = availableResponse
        // 加载已完成的测验
        const completedResponse = await request.get(`/api/quizzes/completed?studentId=${this.studentId}`)
        this.completedQuizzes = completedResponse
      } catch (error) {
        this.$message.error('加载测验列表失败')
        console.error(error)
      }
    },
    
    startQuiz(quiz) {
      this.$router.push({
        name: 'Quiz',
        params: { quizId: quiz.id },
        query: { title: quiz.title }
      })
    },
    
    viewResult(quiz) {
      this.$router.push({
        name: 'QuizResult',
        params: { quizId: quiz.id },
        query: { title: quiz.title }
      })
    },
    
    viewQuiz(quiz) {
      this.$router.push({
        name: 'Quiz',
        params: { quizId: quiz.id },
        query: { title: quiz.title, preview: true }
      })
    },
    
    formatDate(dateString) {
      return new Date(dateString).toLocaleString()
    }
  }
}
</script>

<style scoped>
.student-quiz-list {
  padding: 20px;
}

.quiz-section {
  margin-bottom: 20px;
}

.quiz-section:last-child {
  margin-bottom: 0;
}
</style> 