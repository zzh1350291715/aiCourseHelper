<template>
  <div class="create-course">
    <div class="page-header">
      <h2>创建新课程</h2>
      <el-button @click="$router.back()">返回</el-button>
    </div>

    <el-row :gutter="20">
      <el-col :span="16">
        <el-card>
          <el-form :model="courseForm" :rules="rules" ref="courseForm" label-width="120px">
            <el-form-item label="课程名称" prop="title">
              <el-input v-model="courseForm.title" placeholder="请输入课程名称" />
            </el-form-item>
            
            <el-form-item label="课程描述" prop="description">
              <el-input 
                v-model="courseForm.description" 
                type="textarea" 
                :rows="4"
                placeholder="请输入课程描述" />
            </el-form-item>
            
            <el-form-item>
              <el-button type="primary" @click="createCourse" :loading="creating">创建课程</el-button>
              <el-button @click="resetForm">重置</el-button>
            </el-form-item>
          </el-form>
        </el-card>
      </el-col>
    </el-row>
  </div>
</template>

<script>
import request from '@/utils/request'
import { mapGetters } from 'vuex'

export default {
  name: 'CreateCourse',
  data() {
    return {
      courseForm: {
        title: '',
        description: ''
      },
      rules: {
        title: [
          { required: true, message: '请输入课程名称', trigger: 'blur' }
        ],
        description: [
          { required: true, message: '请输入课程描述', trigger: 'blur' }
        ]
      },
      creating: false
    }
  },
  computed: {
    ...mapGetters(['currentUser'])
  },
  methods: {
    async createCourse() {
      this.$refs.courseForm.validate(async (valid) => {
        if (!valid) return
        
        this.creating = true
        try {
          const courseData = {  
            ...this.courseForm,
            instructorId: parseInt(localStorage.getItem('userId'))
          }
          
          const course = await request.post('/api/courses', courseData)
          this.$message.success('课程创建成功！')
          this.$router.push({ name: 'InstructorCourses' })
        } catch (error) {
          console.error('创建课程失败:', error)
          this.$message.error('创建课程失败，请重试')
        } finally {
          this.creating = false
        }
      })
    },
    
    resetForm() {
      this.$refs.courseForm.resetFields()
    }
  }
}
</script>

<style scoped>
.create-course {
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
</style> 