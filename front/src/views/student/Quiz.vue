<template>
  <div class="quiz-page">
    <el-card>
      <div slot="header" class="clearfix">
        <span>{{ quizTitle }}</span>
        <div style="float: right;">
          <span>剩余时间: {{ formatTime(remainingTime) }}</span>
        </div>
      </div>

      <div v-if="currentQuestion" class="question-container">
        <div class="question-header">
          <h3>第{{ currentIndex + 1 }}题 / 共{{ questions.length }}题</h3>
        </div>

        <div class="question-content">
          <h4>{{ currentQuestion.questionText }}</h4>
          
          <!-- 选择题 -->
          <div v-if="currentQuestion.questionType === 'MULTIPLE_CHOICE'" class="options">
            <el-radio-group v-model="currentAnswer">
              <el-radio 
                v-for="(option, idx) in getCurrentOptions()" 
                :key="idx" 
                :label="getOptionLabel(idx)"
                class="option-item"
              >
                {{ getOptionLabel(idx) }}. {{ option }}
              </el-radio>
              <div v-if="getCurrentOptions().length === 0" style="color: #f56c6c; margin-top: 10px;">暂无选项</div>
            </el-radio-group>
          </div>
          
          <!-- 判断题 -->
          <div v-else-if="currentQuestion.questionType === 'TRUE_FALSE'" class="options">
            <el-radio-group v-model="currentAnswer">
              <el-radio label="true" class="option-item">正确</el-radio>
              <el-radio label="false" class="option-item">错误</el-radio>
            </el-radio-group>
          </div>
          
          <!-- 简答题 -->
          <div v-else-if="currentQuestion.questionType === 'SHORT_ANSWER'" class="short-answer">
            <el-input 
              type="textarea" 
              v-model="currentAnswer" 
              :rows="4"
              placeholder="请输入您的答案"
            ></el-input>
          </div>
        </div>

        <div class="question-actions">
          <el-button @click="previousQuestion" :disabled="currentIndex === 0">
            上一题
          </el-button>
          <el-button type="primary" @click="nextQuestion" v-if="currentIndex < questions.length - 1">
            下一题
          </el-button>
          <el-button type="success" @click="submitQuiz" v-if="currentIndex === questions.length - 1">
            提交测验
          </el-button>
        </div>
      </div>

      <!-- 题目导航 -->
      <div class="question-navigation">
        <h4>题目导航</h4>
        <div class="nav-buttons">
          <el-button 
            v-for="(question, index) in questions" 
            :key="index"
            :type="getQuestionButtonType(index)"
            size="mini"
            @click="goToQuestion(index)"
          >
            {{ index + 1 }}
          </el-button>
        </div>
      </div>
    </el-card>

    <!-- 提交确认对话框 -->
    <el-dialog title="确认提交" :visible.sync="showSubmitDialog" width="30%">
      <span>确定要提交测验吗？提交后将无法修改答案。</span>
      <span slot="footer" class="dialog-footer">
        <el-button @click="showSubmitDialog = false">取 消</el-button>
        <el-button type="primary" @click="confirmSubmit">确 定</el-button>
      </span>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Quiz',
  data() {
    return {
      quizId: null,
      quizTitle: '',
      questions: [],
      currentIndex: 0,
      answers: {},
      remainingTime: 3600, // 1小时
      timer: null,
      showSubmitDialog: false
    }
  },
  computed: {
    currentQuestion() {
      return this.questions[this.currentIndex] || null
    },
    currentAnswer: {
      get() {
        if (!this.currentQuestion) return ''
        if (this.currentQuestion.questionType === 'MULTIPLE_CHOICE') {
          return this.answers[this.currentQuestion.id]?.label || ''
        }
        return this.answers[this.currentQuestion.id]?.value || this.answers[this.currentQuestion.id] || ''
      },
      set(value) {
        if (this.currentQuestion) {
          if (this.currentQuestion.questionType === 'MULTIPLE_CHOICE') {
            // 存label和内容
            const idx = this.getOptionIndexByLabel(value)
            const optionContent = (this.currentQuestion.options && this.currentQuestion.options.options) ? this.currentQuestion.options.options[idx] : ''
            this.answers[this.currentQuestion.id] = { label: value, value: optionContent }
          } else {
            this.answers[this.currentQuestion.id] = value
          }
        }
      }
    }
  },
  mounted() {
    this.quizId = this.$route.params.quizId
    this.quizTitle = this.$route.query.title || '测验'
    if (!this.quizId && this.$route.params.materialId) {
      this.fetchQuizIdByMaterialId(this.$route.params.materialId)
    } else if (this.quizId) {
      this.loadQuiz()
      this.startTimer()
    } else {
      this.$message.error('未获取到测验ID或资料ID，无法加载测验')
    }
  },
  beforeDestroy() {
    if (this.timer) {
      clearInterval(this.timer)
    }
  },
  methods: {
    async fetchQuizIdByMaterialId(materialId) {
      try {
        const res = await request.get(`/api/quizzes/material/${materialId}`)
        if (!res || !res.id) {
          this.$message.error('未找到测验信息，请联系老师或重试')
          return
        }
        this.quizId = res.id
        this.quizTitle = res.title || this.quizTitle
        this.loadQuiz()
        this.startTimer()
      } catch (e) {
        this.$message.error('无法获取测验信息')
      }
    },
    async loadQuiz() {
      try {
        const response = await request.get(`/api/quizzes/${this.quizId}/questions`)
        if (!response || response.length === 0) {
          this.$message.error('未找到测验题目，请联系老师或重试')
          this.questions = []
          return
        }
        this.questions = response
      } catch (error) {
        this.$message.error('加载测验失败')
        this.questions = []
        console.error(error)
      }
    },
    
    startTimer() {
      this.timer = setInterval(() => {
        if (this.remainingTime > 0) {
          this.remainingTime--
        } else {
          this.submitQuiz()
        }
      }, 1000)
    },
    
    formatTime(seconds) {
      const hours = Math.floor(seconds / 3600)
      const minutes = Math.floor((seconds % 3600) / 60)
      const secs = seconds % 60
      return `${hours.toString().padStart(2, '0')}:${minutes.toString().padStart(2, '0')}:${secs.toString().padStart(2, '0')}`
    },
    
    previousQuestion() {
      if (this.currentIndex > 0) {
        this.currentIndex--
      }
    },
    
    nextQuestion() {
      if (this.currentIndex < this.questions.length - 1) {
        this.currentIndex++
      }
    },
    
    goToQuestion(index) {
      this.currentIndex = index
    },
    
    getQuestionButtonType(index) {
      if (index === this.currentIndex) {
        return 'primary'
      } else if (this.answers[this.questions[index]?.id]) {
        return 'success'
      } else {
        return 'info'
      }
    },
    
    submitQuiz() {
      this.showSubmitDialog = true
    },
    
    getOptionLabel(idx) {
      return String.fromCharCode(65 + idx)
    },
    getOptionIndexByLabel(label) {
      return label ? label.charCodeAt(0) - 65 : 0
    },
    
    getCurrentOptions() {
      if (!this.currentQuestion || !this.currentQuestion.options) return []
      if (Array.isArray(this.currentQuestion.options)) {
        return this.currentQuestion.options
      }
      if (Array.isArray(this.currentQuestion.options.options)) {
        return this.currentQuestion.options.options
      }
      return []
    },
    
    async confirmSubmit() {
      try {
        const studentId = this.$store.state.user.id
        const answers = Object.keys(this.answers).map(questionId => {
          const ans = this.answers[questionId]
          if (typeof ans === 'object' && ans.label && ans.value) {
            return { questionId: parseInt(questionId), answer: ans.value }
          } else {
            return { questionId: parseInt(questionId), answer: ans }
          }
        })
        
        const response = await request.post('/api/student-answers/batch-submit', {
          studentId,
          quizId: this.quizId,
          answers
        })
        
        this.$message.success('测验提交成功')
        this.showSubmitDialog = false
        
        // 跳转到结果页面
        this.$router.push({
          name: 'QuizResult',
          params: { quizId: this.quizId },
          query: { title: this.quizTitle }
        })
      } catch (error) {
        this.$message.error('提交失败')
        console.error(error)
      }
    }
  }
}
</script>

<style scoped>
.quiz-page {
  padding: 20px;
}

.question-container {
  margin-bottom: 30px;
}

.question-header {
  margin-bottom: 20px;
  padding-bottom: 10px;
  border-bottom: 1px solid #eee;
}

.question-content {
  margin-bottom: 30px;
}

.question-content h4 {
  margin-bottom: 20px;
  color: #333;
}

.options {
  margin-top: 20px;
}

.option-item {
  display: block;
  margin-bottom: 15px;
  padding: 10px;
  border: 1px solid #ddd;
  border-radius: 5px;
  cursor: pointer;
}

.option-item:hover {
  background-color: #f5f5f5;
}

.short-answer {
  margin-top: 20px;
}

.question-actions {
  text-align: center;
  margin-top: 30px;
}

.question-actions .el-button {
  margin: 0 10px;
}

.question-navigation {
  margin-top: 30px;
  padding-top: 20px;
  border-top: 1px solid #eee;
}

.nav-buttons {
  display: flex;
  flex-wrap: wrap;
  gap: 10px;
  margin-top: 15px;
}
</style> 