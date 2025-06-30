<template>
  <div class="quiz-result">
    <el-card>
      <div slot="header" class="clearfix">
        <span>{{ quizTitle }} - 评分报告</span>
        <el-button style="float: right;" @click="$router.back()">返回</el-button>
      </div>

      <div v-if="result" class="result-summary">
        <el-row :gutter="20">
          <el-col :span="8">
            <el-card class="summary-card">
              <div class="summary-item">
                <h3>总分</h3>
                <div class="score">{{ result.report?.score || 0 }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="summary-card">
              <div class="summary-item">
                <h3>正确题数</h3>
                <div class="score">{{ correctCount }}</div>
              </div>
            </el-card>
          </el-col>
          <el-col :span="8">
            <el-card class="summary-card">
              <div class="summary-item">
                <h3>总题数</h3>
                <div class="score">{{ result.answers?.length || 0 }}</div>
              </div>
            </el-card>
          </el-col>
        </el-row>
      </div>

      <div v-if="result && result.answers" class="answer-details">
        <h3>答题详情</h3>
        <div v-for="(answer, index) in result.answers" :key="index" class="answer-item">
          <div class="question-header">
            <h4>第{{ index + 1 }}题</h4>
            <el-tag v-if="answer.isCorrect" type="success">正确</el-tag>
            <el-tag v-else type="danger">错误</el-tag>
          </div>
          
          <div class="question-content">
            <p><strong>题目：</strong>{{ answer.questionText }}</p>
            <p><strong>您的答案：</strong>{{ formatStudentAnswer(answer) }}</p>
            <p><strong>正确答案：</strong>{{ answer.correctAnswer }}</p>
          </div>
        </div>
      </div>

    
      <div v-else class="loading">
        <el-spinner></el-spinner>
        <p>加载中...</p>
      </div>
      <div style="text-align: right; margin-top: 24px;">
        <el-button type="primary" @click="redoQuiz">重做</el-button>
      </div>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'QuizResult',
  data() {
    return {
      quizId: null,
      quizTitle: '',
      result: null,
      materialId: null
    }
  },
  computed: {
    correctCount() {
      if (!this.result || !this.result.answers) return 0
      return this.result.answers.filter(answer => answer.isCorrect).length
    }
  },
  async mounted() {
    this.quizId = this.$route.params.quizId
    this.quizTitle = this.$route.query.title || '测验'
    await this.loadResult()
    await this.loadMaterialId()
  },
  methods: {
    async loadResult() {
      try {
        const studentId = this.$store.state.user.id
        const response = await request.get(`/api/student-answers/student/${studentId}/quiz/${this.quizId}/result`)
        this.result = response
      } catch (error) {
        this.$message.error('加载结果失败')
        console.error(error)
      }
    },
    async loadMaterialId() {
      try {
        const quiz = await request.get(`/api/quizzes/${this.quizId}`)
        this.materialId = quiz.courseMaterial?.id
      } catch (e) {
        this.materialId = null
      }
    },
    redoQuiz() {
      if (this.materialId) {
        this.$router.push({ path: `/materials/${this.materialId}/quiz` })
      } else {
        this.$message.error('无法获取测验信息')
      }
    },
    formatStudentAnswer(answer) {
      if ((answer.questionType === 'MULTIPLE_CHOICE' || answer.questionType === 'SINGLE_CHOICE') && answer.options) {
        let label = ''
        let content = answer.studentAnswer
        if (Array.isArray(answer.options)) {
          const idx = answer.options.findIndex(opt => this.normalize(opt) === this.normalize(content))
          if (idx !== -1) label = String.fromCharCode(65 + idx)
        } else if (typeof answer.options === 'object') {
          for (const [key, val] of Object.entries(answer.options)) {
            if (this.normalize(val) === this.normalize(content)) {
              label = key
              break
            }
          }
        }
        return label ? `${label}: ${content}` : content
      }
      return answer.studentAnswer || '未作答'
    },
    normalize(s) {
      if (!s) return ''
      return String(s).trim().replace(/\s+/g, '').toLowerCase()
    }
  }
}
</script>

<style scoped>
.quiz-result {
  padding: 20px;
}

.result-summary {
  margin-bottom: 30px;
}

.summary-card {
  text-align: center;
}

.summary-item h3 {
  margin: 0;
  color: #666;
  font-size: 16px;
}

.summary-item .score {
  font-size: 32px;
  font-weight: bold;
  color: #409EFF;
  margin-top: 10px;
}

.answer-details {
  margin-top: 30px;
}

.answer-details h3 {
  margin-bottom: 20px;
  color: #333;
}

.answer-item {
  margin-bottom: 20px;
  padding: 20px;
  border: 1px solid #eee;
  border-radius: 5px;
  background-color: #fafafa;
}

.question-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 15px;
}

.question-header h4 {
  margin: 0;
  color: #409EFF;
}

.question-content p {
  margin: 8px 0;
  line-height: 1.6;
}

.loading {
  text-align: center;
  padding: 40px;
}

.loading p {
  margin-top: 20px;
  color: #666;
}

.score-report {
  margin-top: 30px;
}
</style> 