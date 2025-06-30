<template>
  <div class="quiz-list">
    <el-card>
      <div slot="header" class="clearfix">
        <span>测验管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="showGenerateDialog = true">
          生成测验
        </el-button>
      </div>

      <!-- 测验列表 -->
      <el-table :data="quizzes || []" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="title" label="测验标题"></el-table-column>
        <el-table-column prop="courseTitle" label="所属课程"></el-table-column>
        <el-table-column prop="studentCount" label="参与学生" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.hasStudent" type="success">{{ scope.row.studentCount }}</el-tag>
            <el-tag v-else type="info">0</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="250">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewQuizDetail(scope.row)">查看详情</el-button>
            <el-button size="mini" type="danger" @click="deleteQuiz(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 生成测验对话框 -->
    <el-dialog title="生成测验" :visible.sync="showGenerateDialog" width="50%">
      <el-form :model="generateForm" :rules="generateRules" ref="generateForm" label-width="120px">
        <el-form-item label="测验标题" prop="title">
          <el-input v-model="generateForm.title" placeholder="请输入测验标题"></el-input>
        </el-form-item>
        
        <el-form-item label="选择课程" prop="courseId">
          <el-select v-model="generateForm.courseId" placeholder="选择课程" @change="loadKnowledgeBases">
            <el-option v-for="course in courses" :key="course.id" :label="course.title" :value="course.id"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="选择知识库" prop="knowledgeBaseId">
          <el-select v-model="generateForm.knowledgeBaseId" placeholder="选择知识库" :disabled="!generateForm.courseId">
            <el-option v-for="kb in knowledgeBases" :key="kb.id" :label="kb.name" :value="kb.id"></el-option>
          </el-select>
        </el-form-item>
        
        <el-form-item label="题目数量" prop="questionCount">
          <el-input-number v-model="generateForm.questionCount" :min="1" :max="50"></el-input-number>
        </el-form-item>
        
        <el-form-item label="题目类型">
          <el-checkbox-group v-model="generateForm.questionTypes">
            <el-checkbox label="MULTIPLE_CHOICE">选择题</el-checkbox>
            <el-checkbox label="TRUE_FALSE">判断题</el-checkbox>
            <el-checkbox label="SHORT_ANSWER">简答题</el-checkbox>
          </el-checkbox-group>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showGenerateDialog = false">取 消</el-button>
        <el-button type="primary" @click="generateQuiz">生 成</el-button>
      </div>
    </el-dialog>

    <!-- 测验详情对话框 -->
    <el-dialog title="测验详情" :visible.sync="showDetailDialog" width="70%" @close="onDetailDialogClose">
      <div v-if="quizDetail">
        <h3>{{ quizDetail.title }}</h3>
        <p><strong>课程：</strong>{{ quizDetail.courseTitle }}</p>
        <p><strong>创建时间：</strong>{{ formatDate(quizDetail.createdAt) }}</p>
        
        <h4>参与学生</h4>
        <el-table :data="quizDetail.students || []" style="width: 100%">
          <el-table-column prop="studentId" label="学生ID" width="100"></el-table-column>
          <el-table-column prop="username" label="用户名"></el-table-column>
          <el-table-column label="操作" width="150">
            <template slot-scope="scope">
              <el-button size="mini" @click="viewStudentDetail(quizDetail.id, scope.row.studentId)">
                查看答题
              </el-button>
            </template>
          </el-table-column>
        </el-table>
      </div>
    </el-dialog>

    <!-- 学生答题详情对话框 -->
    <el-dialog title="学生答题详情" :visible.sync="showStudentDetailDialog" width="80%" @close="onStudentDetailDialogClose">
      <div v-if="Array.isArray(studentAnswers) && studentAnswers.length > 0">
        <div v-for="(answer, index) in studentAnswers" :key="index" class="answer-item">
          <h4>第{{ index + 1 }}题</h4>
          <p><strong>题目：</strong>{{ answer.questionText }}</p>
          <p><strong>学生答案：</strong>{{ answer.studentAnswer }}</p>
          <p><strong>正确答案：</strong>{{ answer.correctAnswer }}</p>
          <p><strong>是否正确：</strong>
            <el-tag v-if="answer.isCorrect" type="success">正确</el-tag>
            <el-tag v-else type="danger">错误</el-tag>
          </p>
          <p><strong>得分：</strong>{{ answer.score }}</p>
          <hr>
        </div>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'QuizList',
  data() {
    return {
      quizzes: [],
      showGenerateDialog: false,
      showDetailDialog: false,
      showStudentDetailDialog: false,
      courses: [],
      knowledgeBases: [],
      quizDetail: { students: [] },
      studentAnswers: [],
      generateForm: {
        title: '',
        courseId: '',
        knowledgeBaseId: '',
        questionCount: 10,
        questionTypes: ['MULTIPLE_CHOICE', 'TRUE_FALSE']
      },
      generateRules: {
        title: [
          { required: true, message: '请输入测验标题', trigger: 'blur' }
        ],
        courseId: [
          { required: true, message: '请选择课程', trigger: 'change' }
        ],
        knowledgeBaseId: [
          { required: true, message: '请选择知识库', trigger: 'change' }
        ]
      }
    }
  },
  mounted() {
    this.loadQuizList()
    this.loadCourses()
  },
  methods: {
    async loadQuizList() {
      try {
        const response = await request.get('/api/quizzes/instructor/list')
        this.quizzes = response
      } catch (error) {
        this.$message.error('加载测验列表失败')
        console.error(error)
      }
    },
    
    async loadCourses() {
      try {
        const response = await request.get('/api/courses')
        this.courses = response
      } catch (error) {
        this.$message.error('加载课程列表失败')
        console.error(error)
      }
    },
    
    async loadKnowledgeBases() {
      if (!this.generateForm.courseId) return
      
      try {
        const response = await request.get(`/api/knowledge-base/course/${this.generateForm.courseId}`)
        this.knowledgeBases = response
      } catch (error) {
        this.$message.error('加载知识库失败')
        console.error(error)
      }
    },
    
    async generateQuiz() {
      try {
        await this.$refs.generateForm.validate()
        
        const response = await request.post('/api/quizzes/generate', this.generateForm)
        this.$message.success('测验生成成功')
        this.showGenerateDialog = false
        this.loadQuizList()
      } catch (error) {
        this.$message.error('生成测验失败')
        console.error(error)
      }
    },
    
    async viewQuizDetail(quiz) {
      try {
        const response = await request.get(`/api/quizzes/instructor/${quiz.id}/detail`)
        this.quizDetail = response
        this.showDetailDialog = true
      } catch (error) {
        this.$message.error('加载测验详情失败')
        console.error(error)
      }
    },
    
    async viewStudentDetail(quizId, studentId) {
      try {
        const response = await request.get(`/api/quizzes/instructor/${quizId}/student/${studentId}/detail`)
        this.studentAnswers = response.data || response || []
        this.showStudentDetailDialog = true
      } catch (error) {
        this.$message.error('加载学生答题详情失败')
        this.studentAnswers = []
        console.error(error)
      }
    },
    
    async deleteQuiz(id) {
      try {
        await this.$confirm('确定要删除这个测验吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request.delete(`/api/quizzes/${id}`)
        this.$message.success('删除成功')
        this.loadQuizList()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
          console.error(error)
        }
      }
    },
    
    formatDate(dateString) {
      return new Date(dateString).toLocaleString()
    },
    onDetailDialogClose() {
      this.quizDetail = { students: [] }
    },
    onStudentDetailDialogClose() {
      this.studentAnswers = []
    }
  }
}
</script>

<style scoped>
.quiz-list {
  padding: 20px;
}

.answer-item {
  margin-bottom: 20px;
  padding: 15px;
  border: 1px solid #eee;
  border-radius: 5px;
}

.answer-item h4 {
  margin-top: 0;
  color: #409EFF;
}

.answer-item p {
  margin: 5px 0;
}
</style> 