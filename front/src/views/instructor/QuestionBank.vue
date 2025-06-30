<template>
  <div class="question-bank">
    <el-card>
      <div slot="header" class="clearfix">
        <span>题库管理</span>
        <el-button style="float: right; padding: 3px 0" type="text" @click="showAddDialog = true">
          添加题目
        </el-button>
      </div>

      <!-- 筛选条件 -->
      <el-form :inline="true" class="filter-form">
        <el-form-item label="题目类型">
          <el-select v-model="filterType" placeholder="选择题目类型" clearable>
            <el-option label="选择题" value="MULTIPLE_CHOICE"></el-option>
            <el-option label="判断题" value="TRUE_FALSE"></el-option>
            <el-option label="简答题" value="SHORT_ANSWER"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="难度">
          <el-select v-model="filterDifficulty" placeholder="选择难度" clearable>
            <el-option label="简单" value="EASY"></el-option>
            <el-option label="中等" value="MEDIUM"></el-option>
            <el-option label="困难" value="HARD"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item label="知识库">
          <el-select v-model="filterKnowledgeBaseId" placeholder="选择知识库" clearable>
            <el-option v-for="kb in knowledgeBases" :key="kb.id" :label="kb.name" :value="kb.id"></el-option>
          </el-select>
        </el-form-item>
        <el-form-item>
          <el-button type="primary" @click="loadQuestions">筛选</el-button>
          <el-button @click="resetFilter">重置</el-button>
        </el-form-item>
      </el-form>

      <!-- 题目列表 -->
      <el-table :data="questions" style="width: 100%">
        <el-table-column prop="id" label="ID" width="80"></el-table-column>
        <el-table-column prop="questionText" label="题目内容" show-overflow-tooltip></el-table-column>
        <el-table-column prop="questionType" label="类型" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.questionType === 'MULTIPLE_CHOICE'" type="primary">选择题</el-tag>
            <el-tag v-else-if="scope.row.questionType === 'TRUE_FALSE'" type="success">判断题</el-tag>
            <el-tag v-else-if="scope.row.questionType === 'SHORT_ANSWER'" type="warning">简答题</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="difficulty" label="难度" width="100">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.difficulty === 'EASY'" type="success">简单</el-tag>
            <el-tag v-else-if="scope.row.difficulty === 'MEDIUM'" type="warning">中等</el-tag>
            <el-tag v-else-if="scope.row.difficulty === 'HARD'" type="danger">困难</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="知识库" width="150">
          <template slot-scope="scope">
            <span>{{ scope.row.knowledgeBase ? scope.row.knowledgeBase.name : '-' }}</span>
          </template>
        </el-table-column>
        <el-table-column label="标签" width="180">
          <template slot-scope="scope">
            <el-tag
              v-for="tag in scope.row.tags"
              :key="tag"
              type="info"
              style="margin-right: 4px"
            >{{ tag }}</el-tag>
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button size="mini" @click="editQuestion(scope.row)">编辑</el-button>
            <el-button size="mini" type="danger" @click="deleteQuestion(scope.row.id)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 添加/编辑题目对话框 -->
    <el-dialog :title="dialogTitle" :visible.sync="showAddDialog" width="60%">
      <el-form :model="questionForm" :rules="rules" ref="questionForm" label-width="100px">
        <el-form-item label="题目内容" prop="questionText">
          <el-input type="textarea" v-model="questionForm.questionText" :rows="3"></el-input>
        </el-form-item>
        
        <el-form-item label="题目类型" prop="questionType">
          <el-select v-model="questionForm.questionType" placeholder="选择题目类型">
            <el-option label="选择题" value="MULTIPLE_CHOICE"></el-option>
            <el-option label="判断题" value="TRUE_FALSE"></el-option>
            <el-option label="简答题" value="SHORT_ANSWER"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="难度" prop="difficulty">
          <el-select v-model="questionForm.difficulty" placeholder="选择难度">
            <el-option label="简单" value="EASY"></el-option>
            <el-option label="中等" value="MEDIUM"></el-option>
            <el-option label="困难" value="HARD"></el-option>
          </el-select>
        </el-form-item>

        <!-- 选择题选项 -->
        <el-form-item v-if="questionForm.questionType === 'MULTIPLE_CHOICE'" label="选项">
          <div v-for="(option, index) in questionForm.options" :key="index" style="margin-bottom: 10px;">
            <el-input v-model="questionForm.options[index]" placeholder="选项内容">
              <template slot="prepend">{{ String.fromCharCode(65 + index) }}</template>
            </el-input>
          </div>
          <el-button @click="addOption" size="small">添加选项</el-button>
          <el-button @click="removeOption" size="small" type="danger">删除选项</el-button>
        </el-form-item>

        <el-form-item label="正确答案" prop="correctAnswer">
          <el-input v-model="questionForm.correctAnswer" placeholder="请输入正确答案"></el-input>
        </el-form-item>

        <el-form-item label="标签">
          <el-select v-model="questionForm.tags" multiple filterable allow-create placeholder="请选择或输入标签">
            <el-option v-for="tag in commonTags" :key="tag" :label="tag" :value="tag"></el-option>
          </el-select>
        </el-form-item>

        <el-form-item label="所属知识库" prop="knowledgeBaseId" required>
          <el-select v-model="questionForm.knowledgeBaseId" placeholder="请选择知识库">
            <el-option v-for="kb in knowledgeBases" :key="kb.id" :label="kb.name" :value="kb.id"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      
      <div slot="footer" class="dialog-footer">
        <el-button @click="showAddDialog = false">取 消</el-button>
        <el-button type="primary" @click="saveQuestion">确 定</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'QuestionBank',
  data() {
    return {
      questions: [],
      showAddDialog: false,
      isEdit: false,
      filterType: '',
      filterDifficulty: '',
      filterKnowledgeBaseId: '',
      questionForm: {
        questionText: '',
        questionType: 'MULTIPLE_CHOICE',
        difficulty: 'MEDIUM',
        options: ['', '', '', ''],
        correctAnswer: '',
        tags: [],
        knowledgeBaseId: ''
      },
      rules: {
        questionText: [
          { required: true, message: '请输入题目内容', trigger: 'blur' }
        ],
        questionType: [
          { required: true, message: '请选择题目类型', trigger: 'change' }
        ],
        correctAnswer: [
          { required: true, message: '请输入正确答案', trigger: 'blur' }
        ],
        knowledgeBaseId: [
          { required: true, message: '请选择所属知识库', trigger: 'change' }
        ]
      },
      commonTags: ['Java', 'Spring', '数据库', '算法', '前端', '后端', '基础', '进阶'],
      knowledgeBases: []
    }
  },
  computed: {
    dialogTitle() {
      return this.isEdit ? '编辑题目' : '添加题目'
    }
  },
  mounted() {
    this.loadQuestions()
    this.loadKnowledgeBases()
  },
  methods: {
    async loadQuestions() {
      try {
        let params = {};
        if (this.filterType) params.questionType = this.filterType;
        if (this.filterDifficulty) params.difficulty = this.filterDifficulty;
        if (this.filterKnowledgeBaseId) params.knowledgeBaseId = this.filterKnowledgeBaseId;
        const response = await request.get('/api/questions', { params });
        this.questions = response;
      } catch (error) {
        this.$message.error('加载题目失败');
        console.error(error);
      }
    },
    
    resetFilter() {
      this.filterType = ''
      this.filterDifficulty = ''
      this.filterKnowledgeBaseId = ''
      this.loadQuestions()
    },
    
    async loadKnowledgeBases() {
      try {
        const response = await request.get('/api/knowledge-base')
        this.knowledgeBases = response
      } catch (error) {
        this.$message.error('加载知识库失败')
      }
    },
    
    editQuestion(question) {
      this.isEdit = true
      this.questionForm = {
        id: question.id,
        questionText: question.questionText,
        questionType: question.questionType,
        difficulty: question.difficulty || 'MEDIUM',
        options: question.options ? Object.values(question.options) : ['', '', '', ''],
        correctAnswer: question.correctAnswer,
        tags: question.tags || [],
        knowledgeBaseId: question.knowledgeBase ? question.knowledgeBase.id : ''
      }
      this.showAddDialog = true
    },
    
    async deleteQuestion(id) {
      try {
        await this.$confirm('确定要删除这个题目吗？', '提示', {
          confirmButtonText: '确定',
          cancelButtonText: '取消',
          type: 'warning'
        })
        
        await request.delete(`/api/questions/${id}`)
        this.$message.success('删除成功')
        this.loadQuestions()
      } catch (error) {
        if (error !== 'cancel') {
          this.$message.error('删除失败')
          console.error(error)
        }
      }
    },
    
    addOption() {
      this.questionForm.options.push('')
    },
    
    removeOption() {
      if (this.questionForm.options.length > 2) {
        this.questionForm.options.pop()
      }
    },
    
    async saveQuestion() {
      try {
        await this.$refs.questionForm.validate()
        
        const formData = {
          ...this.questionForm,
          knowledgeBase: this.questionForm.knowledgeBaseId ? { id: this.questionForm.knowledgeBaseId } : null,
          options: this.questionForm.questionType === 'MULTIPLE_CHOICE' 
            ? this.questionForm.options.reduce((acc, option, index) => {
                acc[String.fromCharCode(65 + index)] = option
                return acc
              }, {})
            : null
        }
        
        if (this.isEdit) {
          await request.put(`/api/questions/${formData.id}`, formData)
          this.$message.success('更新成功')
        } else {
          await request.post('/api/questions', formData)
          this.$message.success('添加成功')
        }
        
        this.showAddDialog = false
        this.resetForm()
        this.loadQuestions()
      } catch (error) {
        this.$message.error('保存失败')
        console.error(error)
      }
    },
    
    resetForm() {
      this.isEdit = false
      this.questionForm = {
        questionText: '',
        questionType: 'MULTIPLE_CHOICE',
        difficulty: 'MEDIUM',
        options: ['', '', '', ''],
        correctAnswer: '',
        tags: [],
        knowledgeBaseId: ''
      }
      this.$refs.questionForm && this.$refs.questionForm.resetFields()
    }
  }
}
</script>

<style scoped>
.question-bank {
  padding: 20px;
}

.filter-form {
  margin-bottom: 20px;
}

.el-table {
  margin-top: 20px;
}
</style> 