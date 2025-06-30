<template>
  <div class="manage-materials">
    <el-card>
      <div slot="header" class="clearfix">
        <span>管理课程材料</span>
        <el-button 
          style="float: right; padding: 3px 0" 
          type="text"
          @click="showUploadDialog = true"
        >
          上传材料
        </el-button>
      </div>

      <!-- 材料列表 -->
      <el-table :data="materials" style="width: 100%">
        <el-table-column prop="title" label="材料标题" />
        <el-table-column prop="type" label="类型">
          <template slot-scope="scope">
            <el-tag :type="getTypeColor(scope.row.type)">
              {{ getTypeText(scope.row.type) }}
            </el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="fileSize" label="文件大小">
          <template slot-scope="scope">
            {{ formatFileSize(scope.row.fileSize) }}
          </template>
        </el-table-column>
        <el-table-column prop="uploadTime" label="上传时间">
          <template slot-scope="scope">
            {{ formatDate(scope.row.uploadTime) }}
          </template>
        </el-table-column>
        <el-table-column label="操作" width="200">
          <template slot-scope="scope">
            <el-button type="text" @click="editMaterial(scope.row)">编辑</el-button>
            <el-button type="text" @click="downloadMaterial(scope.row)">下载</el-button>
            <el-button type="text" style="color: #f56c6c" @click="deleteMaterial(scope.row)">删除</el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-card>

    <!-- 上传材料对话框 -->
    <el-dialog title="上传课程材料" :visible.sync="showUploadDialog">
      <el-form :model="uploadForm" ref="uploadForm" :rules="uploadRules">
        <el-form-item label="材料标题" prop="title">
          <el-input v-model="uploadForm.title" />
        </el-form-item>
        <el-form-item label="材料类型" prop="type">
          <el-select v-model="uploadForm.type" placeholder="请选择材料类型">
            <el-option label="视频" value="VIDEO" />
            <el-option label="文档" value="DOCUMENT" />
            <el-option label="音频" value="AUDIO" />
            <el-option label="图片" value="IMAGE" />
            <el-option label="其他" value="OTHER" />
          </el-select>
        </el-form-item>
        <el-form-item label="材料描述" prop="description">
          <el-input type="textarea" v-model="uploadForm.description" />
        </el-form-item>
        <el-form-item label="上传文件" prop="file">
          <el-upload
            ref="upload"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :data="uploadData"
            :file-list="fileList"
            :on-success="handleUploadSuccess"
            :on-error="handleUploadError"
            :before-upload="beforeUpload"
            :auto-upload="false"
            :limit="1"
            :on-exceed="handleExceed"
          >
            <el-button slot="trigger" size="small" type="primary">选择文件</el-button>
            <div slot="tip" class="el-upload__tip">支持各种格式文件，单个文件不超过100MB</div>
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
import { mapGetters } from 'vuex'

export default {
  name: 'ManageCourseMaterials',
  data() {
    return {
      materials: [],
      showUploadDialog: false,
      uploading: false,
      uploadForm: {
        title: '',
        type: '',
        description: '',
        file: null
      },
      uploadRules: {
        title: [{ required: true, message: '请输入材料标题', trigger: 'blur' }],
        type: [{ required: true, message: '请选择材料类型', trigger: 'change' }]
      },
      fileList: [],
      uploadUrl: 'http://localhost:8081/api/course-materials/upload'
    }
  },
  computed: {
    ...mapGetters(['token', 'isLoggedIn']),
    courseId() {
      return this.$route.params.courseId
    },
    uploadHeaders() {
      return {
        'sessionId': this.token || ''
      }
    },
    uploadData() {
      return {
        courseId: this.courseId,
        title: this.uploadForm.title,
        type: this.uploadForm.type,
        description: this.uploadForm.description
      }
    }
  },
  created() {
    this.fetchMaterials()
  },
  methods: {
    fetchMaterials() {
      request.get(`/api/course-materials/course/${this.courseId}`)
        .then(data => {
          this.materials = data
        })
        .catch(error => {
          this.$message.error('获取材料列表失败')
        })
    },
    
    getTypeColor(type) {
      const colors = {
        VIDEO: 'primary',
        DOCUMENT: 'success',
        AUDIO: 'warning',
        IMAGE: 'info',
        OTHER: ''
      }
      return colors[type] || ''
    },
    
    getTypeText(type) {
      const texts = {
        VIDEO: '视频',
        DOCUMENT: '文档',
        AUDIO: '音频',
        IMAGE: '图片',
        OTHER: '其他'
      }
      return texts[type] || '未知'
    },
    
    formatFileSize(size) {
      if (!size) return '未知'
      const units = ['B', 'KB', 'MB', 'GB']
      let index = 0
      while (size >= 1024 && index < units.length - 1) {
        size /= 1024
        index++
      }
      return `${size.toFixed(1)} ${units[index]}`
    },
    
    formatDate(dateString) {
      if (!dateString) return '未知时间'
      return new Date(dateString).toLocaleString('zh-CN')
    },
    
    editMaterial(material) {
      this.$message.info('编辑功能开发中...')
    },
    
    downloadMaterial(material) {
      window.open(material.fileUrl)
    },
    
    deleteMaterial(material) {
      this.$confirm('确定要删除这个材料吗？', '提示', {
        confirmButtonText: '确定',
        cancelButtonText: '取消',
        type: 'warning'
      }).then(() => {
        request.delete(`/api/course-materials/${material.id}`)
          .then(() => {
            this.$message.success('删除成功')
            this.fetchMaterials()
          })
          .catch(() => {
            this.$message.error('删除失败')
          })
      })
    },
    
    beforeUpload(file) {
      // 验证文件大小
      const isLt100M = file.size / 1024 / 1024 < 100
      if (!isLt100M) {
        this.$message.error('上传文件大小不能超过 100MB!')
        return false
      }
      
      // 验证表单
      if (!this.uploadForm.title) {
        this.$message.error('请输入材料标题')
        return false
      }
      
      if (!this.uploadForm.type) {
        this.$message.error('请选择材料类型')
        return false
      }
      
      return true
    },
    
    submitUpload() {
      this.$refs.uploadForm.validate((valid) => {
        if (valid) {
          this.uploading = true
          this.$refs.upload.submit()
        }
      })
    },
    
    handleUploadSuccess(response, file) {
      this.uploading = false
      this.$message.success('上传成功！')
      this.showUploadDialog = false
      this.resetUploadForm()
      this.fetchMaterials()
    },
    
    handleUploadError(error) {
      this.uploading = false
      console.error('上传失败:', error)
      this.$message.error('上传失败，请重试')
    },
    
    handleExceed() {
      this.$message.warning('只能上传一个文件')
    },
    
    resetUploadForm() {
      this.uploadForm = {
        title: '',
        type: '',
        description: '',
        file: null
      }
      this.fileList = []
      this.$refs.uploadForm.resetFields()
    }
  }
}
</script>

<style scoped>
.manage-materials {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.dialog-footer {
  text-align: right;
}
</style> 