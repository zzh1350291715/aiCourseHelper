<template>
  <div class="course-knowledge-base">
    <el-card>
      <div slot="header" class="clearfix">
        <span>课程知识库</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text"
          @click="showUploadDialog = true"
        >
          上传文档
        </el-button>
      </div>

      <!-- 知识库统计 -->
      <el-row :gutter="20" class="stats-row">
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ totalDocuments }}</div>
              <div class="stat-label">总文档数</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ processedDocuments }}</div>
              <div class="stat-label">已处理</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ generatedOutlines }}</div>
              <div class="stat-label">已生成大纲</div>
            </div>
          </el-card>
        </el-col>
        <el-col :span="6">
          <el-card class="stat-card">
            <div class="stat-content">
              <div class="stat-number">{{ generatedQuizzes }}</div>
              <div class="stat-label">已生成测验</div>
            </div>
          </el-card>
        </el-col>
      </el-row>

      <!-- 文档列表 -->
      <el-table :data="documents" style="width: 100%" class="document-table">
        <el-table-column prop="title" label="文档标题" />
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag :type="getStatusColor(scope.row.status)">
              {{ getStatusText(scope.row.status) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间">
          <template slot-scope="scope">
            {{ formatDate(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="300">
          <template slot-scope="scope">
            <el-button 
              type="text" 
              @click="generateOutline(scope.row)"
              :disabled="scope.row.status !== 'PROCESSED'"
            >
              生成大纲
            </el-button>
            <el-button 
              type="text" 
              @click="generatePPT(scope.row)"
              :disabled="scope.row.status !== 'PROCESSED'"
            >
              生成PPT
            </el-button>
            <el-button 
              type="text" 
              @click="generateQuiz(scope.row)"
              :disabled="scope.row.status !== 'PROCESSED'"
            >
              生成测验
            </el-button>
            <el-button 
              type="text" 
              style="color: #f56c6c" 
              @click="deleteDocument(scope.row)"
            >
              删除
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传文档对话框 -->
    <el-dialog title="上传知识库文档" :visible.sync="showUploadDialog">
      <el-form :model="uploadForm" ref="uploadForm" :rules="uploadRules">
        <el-form-item label="文档标题" prop="title">
          <el-input v-model="uploadForm.title" />
        </el-form-item>
        <el-form-item label="文档描述" prop="description">
          <el-input type="textarea" v-model="uploadForm.description" />
        </el-form-item>
        <el-form-item label="上传文件" prop="file">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :auto-upload="false"
            accept=".pdf,.doc,.docx,.txt"
          >
            <el-button slot="trigger" size="small" type="primary">选择文件</el-button>
            <div slot="tip" class="el-upload__tip">支持PDF、Word文档、文本文件，单个文件不超过50MB</div>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showUploadDialog = false">取消</el-button>
        <el-button type="primary" @click="submitUpload" :loading="uploading">上传</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'CourseKnowledgeBase',
  data() {
    return {
      documents: [],
      showUploadDialog: false,
      uploading: false,
      uploadForm: {
        title: '',
        description: ''
      },
      uploadRules: {
        title: [{ required: true, message: '请输入文档标题', trigger: 'blur' }]
      },
      fileList: [],
      uploadUrl: process.env.VUE_APP_API_BASE_URL + '/api/knowledge-base/documents/upload',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    }
  },
  computed: {
    courseId() {
      return this.$route.params.courseId
    },
    totalDocuments() {
      return this.documents.length
    },
    processedDocuments() {
      return this.documents.filter(doc => doc.status === 'PROCESSED').length
    },
    generatedOutlines() {
      return this.documents.filter(doc => doc.hasOutline).length
    },
    generatedQuizzes() {
      return this.documents.filter(doc => doc.hasQuiz).length
    }
  },
  created() {
    this.fetchDocuments()
  },
  methods: {
    fetchDocuments() {
      request.get(`/api/knowledge-base/documents/course/${this.courseId}`)
        .then(data => {
          this.documents = data
        })
        .catch(error => {
          this.$message.error('获取文档列表失败')
        })
    },
    
    getStatusColor(status) {
      const colors = {
        UPLOADED: 'info',
        PROCESSING: 'warning',
        PROCESSED: 'success',
        ERROR: 'danger'
      }
      return colors[status] || ''
    },
    
    getStatusText(status) {
      const texts = {
        UPLOADED: '已上传',
        PROCESSING: '处理中',
        PROCESSED: '已处理',
        ERROR: '处理失败'
      }
      return texts[status] || '未知状态'
    },
    
    formatDate(dateString) {
      if (!dateString) return '未知时间'
      return new Date(dateString).toLocaleString('zh-CN')
    },
    
    generateOutline(document) {
      this.$confirm('确定要为此文档生成教学大纲吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        request.post(`/api/knowledge-base/documents/${document.id}/generate-outline`)
          .then(() => {
            this.$message.success('大纲生成中，请稍候查看结果')
            this.fetchDocuments()
          })
          .catch(() => {
            this.$message.error('生成大纲失败')
          })
      })
    },
    
    generatePPT(document) {
      this.$confirm('确定要为此文档生成PPT吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        request.post(`/api/knowledge-base/documents/${document.id}/generate-ppt`)
          .then(() => {
            this.$message.success('PPT生成中，请稍候查看结果')
            this.fetchDocuments()
          })
          .catch(() => {
            this.$message.error('生成PPT失败')
          })
      })
    },
    
    generateQuiz(document) {
      this.$confirm('确定要为此文档生成测验吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'info'
      }).then(() => {
        request.post(`/api/knowledge-base/documents/${document.id}/generate-quiz`)
          .then(() => {
            this.$message.success('测验生成中，请稍候查看结果')
            this.fetchDocuments()
          })
          .catch(() => {
            this.$message.error('生成测验失败')
          })
      })
    },
    
    deleteDocument(document) {
      this.$confirm('确定要删除这个文档吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/api/knowledge-base/documents/${document.id}`)
          .then(() => {
            this.$message.success('删除成功')
            this.fetchDocuments()
          })
          .catch(() => {
            this.$message.error('删除失败')
          })
      })
    },
    
    beforeUpload(file) {
      const isValidType = ['application/pdf', 'application/msword', 'application/vnd.openxmlformats-officedocument.wordprocessingml.document', 'text/plain'].includes(file.type)
      const isLt50M = file.size / 1024 / 1024 < 50
      
      if (!isValidType) {
        this.$message.error('只能上传PDF、Word文档或文本文件!')
      }
      if (!isLt50M) {
        this.$message.error('上传文件大小不能超过 50MB!')
      }
      return isValidType && isLt50M
    },
    
    submitUpload() {
      this.$refs.uploadForm.validate(valid => {
        if (valid && this.fileList.length > 0) {
          this.uploading = true
          this.$refs.upload.submit()
        } else {
          this.$message.error('请完整填写表单并选择文件')
        }
      })
    },
    
    handleUploadSuccess(response, file) {
      this.uploading = false
      this.showUploadDialog = false
      this.$message.success('上传成功，文档正在处理中...')
      this.resetUploadForm()
      this.fetchDocuments()
    },
    
    handleUploadError(error) {
      this.uploading = false
      this.$message.error('上传失败')
    },
    
    resetUploadForm() {
      this.uploadForm = {
        title: '',
        description: ''
      }
      this.fileList = []
      this.$refs.uploadForm.resetFields()
    }
  }
}
</script>

<style scoped>
.course-knowledge-base {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.stats-row {
  margin-bottom: 20px;
}

.stat-card {
  text-align: center;
}

.stat-content {
  padding: 10px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
  margin-bottom: 5px;
}

.stat-label {
  font-size: 14px;
  color: #606266;
}

.document-table {
  margin-top: 20px;
}

.dialog-footer {
  text-align: right;
}
</style> 