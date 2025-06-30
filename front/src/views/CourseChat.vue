<template>
  <div class="course-chat">
    <!-- 页面头部 -->
    <div class="chat-header">
      <div class="header-info">
        <div class="course-info">
          <h1>
            <i class="el-icon-chat-line-round"></i>
            {{ course.title || 'AI智能问答' }}
          </h1>
          <p class="course-desc">与AI助手讨论课程内容，获得个性化学习指导</p>
        </div>
        <div class="header-stats">
          <div class="stat-item">
            <i class="el-icon-chat-dot-round"></i>
            <span>{{ history.length }} 条对话</span>
          </div>
          <div class="stat-item">
            <i class="el-icon-user"></i>
            <span>{{ onlineCount }} 人在线</span>
          </div>
        </div>
      </div>
      <div class="header-actions">
        <el-button @click="exportChat" size="small" plain>
          <i class="el-icon-download"></i>
          导出对话
        </el-button>
        <el-button @click="clearHistory" size="small" type="danger" plain>
          <i class="el-icon-delete"></i>
          清空历史
        </el-button>
        <el-button @click="$router.back()" size="small">
          <i class="el-icon-arrow-left"></i>
          返回课程
        </el-button>
      </div>
    </div>

    <!-- 聊天容器 -->
    <div class="chat-container">
      <!-- 智能建议（首次进入时显示） -->
      <div v-if="history.length === 0 && !isTyping" class="welcome-section">
        <div class="ai-avatar large">
          <i class="el-icon-cpu"></i>
        </div>
        <h3 class="welcome-title">👋 欢迎来到AI智能问答</h3>
        <p class="welcome-desc">我是您的AI学习助手，可以帮助您：</p>
        <div class="welcome-features">
          <div class="feature-item">
            <i class="el-icon-search"></i>
            <span>回答课程相关问题</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-edit-outline"></i>
            <span>解释复杂概念</span>
          </div>
          <div class="feature-item">
            <i class="el-icon-medal"></i>
            <span>提供学习建议</span>
          </div>
        </div>
        
        <!-- 快捷问题模板 -->
        <div class="quick-questions">
          <h4>💡 试试这些问题：</h4>
          <div class="question-chips">
            <el-tag 
              v-for="(q, index) in quickQuestions" 
              :key="index"
              @click="selectQuickQuestion(q)"
              class="question-chip"
              type="info"
            >
              {{ q }}
            </el-tag>
          </div>
        </div>
      </div>

      <!-- 聊天历史 -->
      <div class="chat-history" ref="chatHistory" v-if="history.length > 0 || isTyping">
        <div v-for="(item, idx) in history" :key="idx" class="chat-message">
          <!-- 用户消息 -->
          <div class="message user-message">
            <div class="message-content">
              <div class="message-bubble">
                <div class="message-text">{{ item.question }}</div>
                <div class="message-time">{{ formatTime(item.createdAt) }}</div>
              </div>
            </div>
            <div class="message-avatar">
              <i class="el-icon-user"></i>
            </div>
          </div>

          <!-- AI回答 -->
          <div class="message ai-message">
            <div class="message-avatar ai-avatar">
              <i class="el-icon-cpu"></i>
            </div>
            <div class="message-content">
              <div class="message-bubble">
                <div class="message-text" v-html="formatAnswer(item.answer)"></div>
                <div class="message-actions">
                  <div class="message-time">{{ formatTime(item.createdAt) }}</div>
                  <div class="action-buttons">
                    <el-button size="mini" type="text" @click="copyAnswer(item.answer)">
                      <i class="el-icon-document-copy"></i>
                    </el-button>
                    <el-button size="mini" type="text" @click="rateAnswer(item, 'up')">
                      <i class="el-icon-top" :class="{ active: item.rating === 'up' }"></i>
                    </el-button>
                    <el-button size="mini" type="text" @click="rateAnswer(item, 'down')">
                      <i class="el-icon-bottom" :class="{ active: item.rating === 'down' }"></i>
                    </el-button>
                  </div>
                </div>
              </div>
            </div>
          </div>
        </div>

        <!-- 打字效果 -->
        <div v-if="isTyping" class="message ai-message typing">
          <div class="message-avatar ai-avatar">
            <i class="el-icon-cpu"></i>
          </div>
          <div class="message-content">
            <div class="message-bubble">
              <div class="typing-indicator">
                <div class="typing-dots">
                  <span></span>
                  <span></span>
                  <span></span>
                </div>
                <div class="typing-text">AI正在思考中...</div>
              </div>
            </div>
          </div>
        </div>
      </div>

      <!-- 输入区域 -->
      <div class="chat-input-container">
        <!-- 智能建议 -->
        <div v-if="suggestions.length > 0" class="suggestions-bar">
          <div class="suggestions-title">💡 相关建议：</div>
          <div class="suggestions-list">
            <el-tag 
              v-for="(suggestion, index) in suggestions" 
              :key="index"
              @click="selectSuggestion(suggestion)"
              class="suggestion-chip"
              size="small"
              type="info"
            >
              {{ suggestion }}
            </el-tag>
          </div>
        </div>

        <!-- 输入框 -->
        <div class="input-wrapper">
          <div class="input-container">
            <el-input 
              v-model="question" 
              type="textarea"
              :autosize="{ minRows: 1, maxRows: 4 }"
              placeholder="输入您的问题，按Enter发送，Shift+Enter换行..."
              :maxlength="1000"
              show-word-limit
              @keydown.native="handleKeyDown"
              @input="handleInput"
              :disabled="isTyping"
              ref="questionInput"
              class="question-input"
            />
            <div class="input-actions">
              <el-button 
                type="primary" 
                @click="ask" 
                :loading="isTyping"
                :disabled="!question.trim()"
                class="send-button"
                size="small"
              >
                <i v-if="!isTyping" class="el-icon-s-promotion"></i>
                {{ isTyping ? '思考中...' : '发送' }}
              </el-button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 侧边栏工具 -->
    <div class="chat-sidebar" v-if="showSidebar">
      <div class="sidebar-header">
        <h4>💬 对话工具</h4>
        <el-button size="mini" type="text" @click="showSidebar = false">
          <i class="el-icon-close"></i>
        </el-button>
      </div>
      
      <!-- 历史搜索 -->
      <div class="sidebar-section">
        <h5>🔍 搜索历史</h5>
        <el-input 
          v-model="searchKeyword" 
          placeholder="搜索对话内容..."
          size="small"
          @input="searchHistory"
        />
        <div class="search-results" v-if="searchResults.length > 0">
          <div 
            v-for="(result, index) in searchResults" 
            :key="index"
            class="search-result-item"
            @click="scrollToMessage(result)"
          >
            <div class="result-question">{{ result.question }}</div>
            <div class="result-time">{{ formatTime(result.createdAt) }}</div>
          </div>
        </div>
      </div>

      <!-- 会话统计 -->
      <div class="sidebar-section">
        <h5>📊 会话统计</h5>
        <div class="stats-grid">
          <div class="stat-box">
            <div class="stat-number">{{ history.length }}</div>
            <div class="stat-label">总对话</div>
          </div>
          <div class="stat-box">
            <div class="stat-number">{{ todayCount }}</div>
            <div class="stat-label">今日对话</div>
          </div>
          <div class="stat-box">
            <div class="stat-number">{{ avgResponseTime }}s</div>
            <div class="stat-label">平均响应</div>
          </div>
        </div>
      </div>
    </div>

    <!-- 浮动工具栏 -->
    <div class="floating-toolbar">
      <el-button 
        circle 
        size="small" 
        @click="showSidebar = !showSidebar"
        :type="showSidebar ? 'primary' : 'info'"
      >
        <i class="el-icon-s-tools"></i>
      </el-button>
      <el-button circle size="small" @click="scrollToBottom">
        <i class="el-icon-bottom"></i>
      </el-button>
    </div>
  </div>
</template>

<script>
import request from '@/utils/request'
import { mapGetters } from 'vuex'

export default {
  name: 'CourseChat',
  data() {
    return {
      course: {},
      question: '',
      history: [],
      isTyping: false,
      onlineCount: 1,
      showSidebar: false,
      searchKeyword: '',
      searchResults: [],
      suggestions: [],
      
      // 快捷问题模板
      quickQuestions: [
        '这门课程的核心内容是什么？',
        '有哪些重要的概念需要掌握？',
        '如何制定学习计划？',
        '有什么学习资源推荐？'
      ],
      
      // 统计数据
      todayCount: 0,
      avgResponseTime: 2.5,
      
      // 打字效果
      typingText: '',
      typingInterval: null
    }
  },
  computed: {
    ...mapGetters(['user', 'userRole']),
    courseId() {
      return this.$route.params.courseId
    }
  },
  created() {
    this.fetchCourseInfo()
    this.fetchHistory()
    this.generateSuggestions()
  },
  mounted() {
    // 自动聚焦输入框
    this.$nextTick(() => {
      if (this.$refs.questionInput) {
        this.$refs.questionInput.focus()
      }
    })
  },
  beforeDestroy() {
    if (this.typingInterval) {
      clearInterval(this.typingInterval)
    }
  },
  methods: {
    async fetchCourseInfo() {
      try {
        const course = await request.get(`/api/courses/${this.courseId}`)
        this.course = course
      } catch (error) {
        console.error('获取课程信息失败:', error)
      }
    },
    
    async fetchHistory() {
      try {
        const data = await request.get(`/api/ai-chat/history/student/${this.user.id}/course/${this.courseId}`)
        this.history = (data || []).map(item => ({
          ...item,
          rating: null // 初始化评分
        }))
        
        // 计算今日对话数
        const today = new Date().toDateString()
        this.todayCount = this.history.filter(item => 
          new Date(item.createdAt).toDateString() === today
        ).length
        
        this.$nextTick(() => {
          this.scrollToBottom()
        })
      } catch (error) {
        console.error('获取聊天记录失败:', error)
        this.history = []
      }
    },
    
    async ask() {
      if (!this.question.trim() || this.isTyping) return
      
      const questionText = this.question.trim()
      this.question = ''
      this.isTyping = true
      
      // 添加用户消息到历史记录
      const userMessage = {
        question: questionText,
        answer: '',
        createdAt: new Date().toISOString(),
        rating: null
      }
      
      try {
        const startTime = Date.now()
        const response = await request.post('/api/ai-chat/ask', {
          studentId: this.user.id,
          courseId: parseInt(this.courseId),
          question: questionText
        })
        
        const responseTime = (Date.now() - startTime) / 1000
        this.avgResponseTime = ((this.avgResponseTime * this.history.length + responseTime) / (this.history.length + 1)).toFixed(1)
        
        // 模拟打字效果
        userMessage.answer = response.answer
        this.history.push(userMessage)
        
        // 滚动到底部
        this.$nextTick(() => {
          this.scrollToBottom()
        })
        
        // 生成相关建议
        this.generateSuggestions(response.answer)
        
        // 更新统计
        this.updateStats()
        
      } catch (error) {
        console.error('提问失败:', error)
        this.$message.error('提问失败，请稍后重试')
      } finally {
        this.isTyping = false
      }
    },
    
    selectQuickQuestion(question) {
      this.question = question
      this.ask()
    },
    
    selectSuggestion(suggestion) {
      this.question = suggestion
      this.$refs.questionInput.focus()
    },
    
    handleKeyDown(event) {
      if (event.key === 'Enter' && !event.shiftKey) {
        event.preventDefault()
        this.ask()
      }
    },
    
    handleInput() {
      // 实时生成建议（防抖）
      clearTimeout(this.suggestionTimeout)
      this.suggestionTimeout = setTimeout(() => {
        if (this.question.length > 5) {
          this.generateContextualSuggestions()
        }
      }, 500)
    },
    
    async generateSuggestions(context = '') {
      // 基于课程内容和上下文生成建议问题
      const baseSuggestions = [
        '请解释这个概念',
        '有什么实际应用？',
        '如何更好地理解？',
        '有什么学习技巧？'
      ]
      
      this.suggestions = baseSuggestions.slice(0, 3)
    },
    
    generateContextualSuggestions() {
      // 基于当前输入生成上下文建议
      const keywords = this.question.toLowerCase()
      const contextualSuggestions = []
      
      if (keywords.includes('什么')) {
        contextualSuggestions.push('这是什么意思？')
      }
      if (keywords.includes('如何')) {
        contextualSuggestions.push('具体怎么做？')
      }
      if (keywords.includes('为什么')) {
        contextualSuggestions.push('原因是什么？')
      }
      
      this.suggestions = contextualSuggestions.slice(0, 3)
    },
    
    searchHistory() {
      if (!this.searchKeyword.trim()) {
        this.searchResults = []
        return
      }
      
      const keyword = this.searchKeyword.toLowerCase()
      this.searchResults = this.history.filter(item => 
        item.question.toLowerCase().includes(keyword) ||
        item.answer.toLowerCase().includes(keyword)
      ).slice(0, 10)
    },
    
    scrollToMessage(message) {
      // 滚动到指定消息
      const index = this.history.findIndex(item => item === message)
      if (index !== -1) {
        const element = this.$refs.chatHistory.children[index]
        if (element) {
          element.scrollIntoView({ behavior: 'smooth' })
        }
      }
    },
    
    scrollToBottom() {
      this.$nextTick(() => {
        if (this.$refs.chatHistory) {
          this.$refs.chatHistory.scrollTop = this.$refs.chatHistory.scrollHeight
        }
      })
    },
    
    formatTime(timeString) {
      if (!timeString) return ''
      const date = new Date(timeString)
      const now = new Date()
      const diff = now - date
      
      if (diff < 60000) { // 1分钟内
        return '刚刚'
      } else if (diff < 3600000) { // 1小时内
        return `${Math.floor(diff / 60000)}分钟前`
      } else if (date.toDateString() === now.toDateString()) { // 今天
        return date.toLocaleTimeString('zh-CN', { hour: '2-digit', minute: '2-digit' })
      } else {
        return date.toLocaleDateString('zh-CN', { month: 'short', day: 'numeric', hour: '2-digit', minute: '2-digit' })
      }
    },
    
    formatAnswer(answer) {
      // 格式化答案，支持markdown语法
      return answer
        .replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')
        .replace(/\*(.*?)\*/g, '<em>$1</em>')
        .replace(/\n/g, '<br>')
    },
    
    async copyAnswer(answer) {
      try {
        await navigator.clipboard.writeText(answer)
        this.$message.success('已复制到剪贴板')
      } catch (error) {
        this.$message.error('复制失败')
      }
    },
    
    async rateAnswer(item, rating) {
      try {
        item.rating = item.rating === rating ? null : rating
        await request.post('/api/ai-chat/rate', {
          chatId: item.id,
          rating: item.rating
        })
        this.$message.success(rating === 'up' ? '感谢您的反馈！' : '我们会继续改进')
      } catch (error) {
        console.error('评分失败:', error)
      }
    },
    
    exportChat() {
      const content = this.history.map(item => 
        `问：${item.question}\n答：${item.answer}\n时间：${this.formatTime(item.createdAt)}\n\n`
      ).join('')
      
      const blob = new Blob([content], { type: 'text/plain;charset=utf-8' })
      const url = URL.createObjectURL(blob)
      const link = document.createElement('a')
      link.href = url
      link.download = `${this.course.title}_AI对话记录.txt`
      link.click()
      URL.revokeObjectURL(url)
    },
    
    clearHistory() {
      this.$confirm('确定要清空所有对话记录吗？此操作不可撤销。', '确认清空', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(async () => {
        try {
          await request.delete(`/api/ai-chat/history/student/${this.user.id}/course/${this.courseId}`)
          this.history = []
          this.todayCount = 0
          this.$message.success('历史记录已清空')
        } catch (error) {
          console.error('清空失败:', error)
          this.$message.error('清空失败')
        }
      }).catch(() => {
        // 用户取消
      })
    },
    
    updateStats() {
      const today = new Date().toDateString()
      this.todayCount = this.history.filter(item => 
        new Date(item.createdAt).toDateString() === today
      ).length
    }
  }
}
</script>

<style scoped>
.course-chat {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
  position: relative;
}

.chat-header {
  display: flex;
  justify-content: space-between;
  align-items: flex-start;
  margin-bottom: 20px;
  padding: 20px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
}

.header-info {
  flex: 1;
}

.course-info h1 {
  margin: 0 0 8px 0;
  font-size: 24px;
  color: #303133;
  display: flex;
  align-items: center;
}

.course-info h1 i {
  margin-right: 8px;
  color: #409eff;
}

.course-desc {
  margin: 0 0 15px 0;
  color: #606266;
  font-size: 14px;
}

.header-stats {
  display: flex;
  gap: 20px;
}

.stat-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #909399;
}

.stat-item i {
  margin-right: 5px;
  color: #409eff;
}

.header-actions {
  display: flex;
  gap: 8px;
}

.chat-container {
  background: white;
  border-radius: 12px;
  box-shadow: 0 2px 12px rgba(0,0,0,0.1);
  overflow: hidden;
  position: relative;
}

.welcome-section {
  padding: 60px 40px;
  text-align: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}

.ai-avatar.large {
  width: 80px;
  height: 80px;
  border-radius: 50%;
  background: linear-gradient(135deg, #67c23a, #85ce61);
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 36px;
  margin: 0 auto 20px;
  box-shadow: 0 4px 20px rgba(103, 194, 58, 0.3);
}

.welcome-title {
  margin: 0 0 10px 0;
  font-size: 24px;
  color: #303133;
}

.welcome-desc {
  margin: 0 0 20px 0;
  color: #606266;
  font-size: 16px;
}

.welcome-features {
  display: flex;
  justify-content: center;
  gap: 30px;
  margin-bottom: 30px;
  flex-wrap: wrap;
}

.feature-item {
  display: flex;
  align-items: center;
  font-size: 14px;
  color: #606266;
}

.feature-item i {
  margin-right: 8px;
  color: #409eff;
  font-size: 16px;
}

.quick-questions h4 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 16px;
}

.question-chips {
  display: flex;
  justify-content: center;
  gap: 10px;
  flex-wrap: wrap;
}

.question-chip {
  cursor: pointer;
  transition: all 0.3s;
  padding: 8px 16px;
  border-radius: 20px;
}

.question-chip:hover {
  background: #409eff;
  color: white;
  transform: translateY(-2px);
}

.chat-history {
  padding: 20px;
  max-height: 600px;
  overflow-y: auto;
  scroll-behavior: smooth;
}

.chat-message {
  margin-bottom: 30px;
}

.message {
  display: flex;
  align-items: flex-start;
  margin-bottom: 15px;
}

.user-message {
  justify-content: flex-end;
}

.ai-message {
  justify-content: flex-start;
}

.message-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  background: #409eff;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-size: 16px;
  flex-shrink: 0;
  box-shadow: 0 2px 8px rgba(64, 158, 255, 0.3);
}

.ai-avatar {
  background: linear-gradient(135deg, #67c23a, #85ce61);
  box-shadow: 0 2px 8px rgba(103, 194, 58, 0.3);
}

.message-content {
  max-width: 70%;
  margin: 0 15px;
}

.user-message .message-content {
  margin-right: 0;
}

.ai-message .message-content {
  margin-left: 0;
}

.message-bubble {
  background: #f0f9ff;
  border: 1px solid #e1f5fe;
  border-radius: 18px;
  padding: 12px 16px;
  position: relative;
}

.user-message .message-bubble {
  background: linear-gradient(135deg, #409eff, #66b1ff);
  color: white;
  border: none;
}

.message-text {
  line-height: 1.6;
  word-wrap: break-word;
  margin-bottom: 8px;
}

.message-actions {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-top: 8px;
}

.message-time {
  font-size: 12px;
  color: #c0c4cc;
}

.user-message .message-time {
  color: rgba(255, 255, 255, 0.8);
}

.action-buttons {
  display: flex;
  gap: 4px;
}

.action-buttons .el-button {
  padding: 2px 6px;
  color: #909399;
}

.action-buttons .el-button:hover {
  color: #409eff;
}

.action-buttons .el-button.active {
  color: #409eff;
}

.typing {
  animation: fadeIn 0.3s ease-in;
}

.typing-indicator {
  display: flex;
  align-items: center;
  gap: 10px;
}

.typing-dots {
  display: flex;
  gap: 4px;
}

.typing-dots span {
  width: 8px;
  height: 8px;
  border-radius: 50%;
  background: #67c23a;
  animation: typing 1.4s infinite;
}

.typing-dots span:nth-child(2) {
  animation-delay: 0.2s;
}

.typing-dots span:nth-child(3) {
  animation-delay: 0.4s;
}

.typing-text {
  font-size: 14px;
  color: #909399;
}

.chat-input-container {
  border-top: 1px solid #ebeef5;
  background: #fafafa;
}

.suggestions-bar {
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.suggestions-title {
  font-size: 14px;
  color: #606266;
  margin-bottom: 10px;
}

.suggestions-list {
  display: flex;
  gap: 8px;
  flex-wrap: wrap;
}

.suggestion-chip {
  cursor: pointer;
  transition: all 0.3s;
}

.suggestion-chip:hover {
  background: #409eff;
  color: white;
}

.input-wrapper {
  padding: 20px;
}

.input-container {
  display: flex;
  align-items: flex-end;
  gap: 10px;
}

.question-input {
  flex: 1;
}

.send-button {
  align-self: flex-end;
  height: 36px;
}

.chat-sidebar {
  position: fixed;
  right: 20px;
  top: 50%;
  transform: translateY(-50%);
  width: 300px;
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  z-index: 1000;
  max-height: 70vh;
  overflow-y: auto;
}

.sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px 20px;
  border-bottom: 1px solid #ebeef5;
}

.sidebar-header h4 {
  margin: 0;
  color: #303133;
  font-size: 16px;
}

.sidebar-section {
  padding: 20px;
  border-bottom: 1px solid #ebeef5;
}

.sidebar-section:last-child {
  border-bottom: none;
}

.sidebar-section h5 {
  margin: 0 0 15px 0;
  color: #303133;
  font-size: 14px;
}

.search-results {
  margin-top: 10px;
  max-height: 200px;
  overflow-y: auto;
}

.search-result-item {
  padding: 10px;
  border: 1px solid #ebeef5;
  border-radius: 4px;
  margin-bottom: 8px;
  cursor: pointer;
  transition: all 0.3s;
}

.search-result-item:hover {
  background: #f5f7fa;
  border-color: #409eff;
}

.result-question {
  font-size: 14px;
  color: #303133;
  margin-bottom: 4px;
}

.result-time {
  font-size: 12px;
  color: #909399;
}

.stats-grid {
  display: grid;
  grid-template-columns: repeat(2, 1fr);
  gap: 15px;
}

.stat-box {
  text-align: center;
  padding: 15px;
  background: #f5f7fa;
  border-radius: 8px;
}

.stat-number {
  font-size: 20px;
  font-weight: bold;
  color: #409eff;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 12px;
  color: #909399;
}

.floating-toolbar {
  position: fixed;
  right: 30px;
  bottom: 30px;
  display: flex;
  flex-direction: column;
  gap: 10px;
  z-index: 999;
}

@keyframes typing {
  0%, 60%, 100% {
    transform: translateY(0);
  }
  30% {
    transform: translateY(-10px);
  }
}

@keyframes fadeIn {
  from {
    opacity: 0;
    transform: translateY(10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

@media (max-width: 768px) {
  .course-chat {
    padding: 10px;
  }
  
  .chat-header {
    flex-direction: column;
    align-items: stretch;
  }
  
  .header-actions {
    margin-top: 15px;
    justify-content: center;
  }
  
  .welcome-features {
    flex-direction: column;
    gap: 15px;
  }
  
  .message-content {
    max-width: 85%;
  }
  
  .chat-sidebar {
    position: fixed;
    left: 0;
    top: 0;
    width: 100%;
    height: 100vh;
    transform: none;
    border-radius: 0;
  }
  
  .floating-toolbar {
    right: 15px;
    bottom: 15px;
  }
}
</style> 