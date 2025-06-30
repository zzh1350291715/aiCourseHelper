<template>
  <div class="profile-container">
    <el-row :gutter="20">
      <!-- 个人信息卡片 -->
      <el-col :span="8">
        <el-card class="profile-card">
          <div class="profile-header">
            <el-avatar :size="80" :src="userInfo.avatar" class="profile-avatar">
              <i class="el-icon-user-solid"></i>
            </el-avatar>
            <h3 class="username">{{ userInfo.username }}</h3>
            <el-tag :type="getRoleColor(userInfo.role)" class="role-tag">
              {{ getRoleText(userInfo.role) }}
            </el-tag>
          </div>
          
          <div class="profile-info">
            <div class="info-item">
              <span class="label">邮箱：</span>
              <span class="value">{{ userInfo.email }}</span>
            </div>
            <div class="info-item">
              <span class="label">注册时间：</span>
              <span class="value">{{ formatDate(userInfo.createdAt) }}</span>
            </div>
            <div class="info-item">
              <span class="label">最后登录：</span>
              <span class="value">{{ formatDate(userInfo.lastLoginAt) }}</span>
            </div>
          </div>
          
          <el-button type="primary" @click="showEditDialog = true" class="edit-btn">
            编辑资料
          </el-button>
        </el-card>
      </el-col>

      <!-- 统计信息 -->
      <el-col :span="16">
        <el-card class="stats-card" v-if="userInfo.role === 'INSTRUCTOR'">
          <div slot="header">
            <span>教学统计</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.totalCourses }}</div>
                <div class="stat-label">创建课程</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.totalStudents }}</div>
                <div class="stat-label">学生总数</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.totalMaterials }}</div>
                <div class="stat-label">上传材料</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.totalQuizzes }}</div>
                <div class="stat-label">创建测验</div>
              </div>
            </el-col>
          </el-row>
        </el-card>
        
        <el-card class="stats-card" v-else>
          <div slot="header">
            <span>学习统计</span>
          </div>
          <el-row :gutter="20">
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.enrolledCourses }}</div>
                <div class="stat-label">报名课程</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.completedCourses }}</div>
                <div class="stat-label">完成课程</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.studyHours }}</div>
                <div class="stat-label">学习时长(小时)</div>
              </div>
            </el-col>
            <el-col :span="6">
              <div class="stat-item">
                <div class="stat-number">{{ stats.completedQuizzes }}</div>
                <div class="stat-label">完成测验</div>
              </div>
            </el-col>
          </el-row>
        </el-card>

        <!-- 最近活动 -->
        <el-card class="activity-card">
          <div slot="header">
            <span>最近活动</span>
          </div>
          <el-timeline>
            <el-timeline-item 
              v-for="activity in recentActivities" 
              :key="activity.id"
              :timestamp="formatDate(activity.timestamp)"
            >
              {{ activity.description }}
            </el-timeline-item>
          </el-timeline>
        </el-card>
      </el-col>
    </el-row>

    <!-- 编辑资料对话框 -->
    <el-dialog title="编辑个人资料" :visible.sync="showEditDialog">
      <el-form :model="editForm" ref="editForm" :rules="editRules">
        <el-form-item label="用户名" prop="username">
          <el-input v-model="editForm.username" />
        </el-form-item>
        <el-form-item label="邮箱" prop="email">
          <el-input v-model="editForm.email" />
        </el-form-item>
        <el-form-item label="头像">
          <el-upload
            class="avatar-uploader"
            :action="uploadUrl"
            :headers="uploadHeaders"
            :show-file-list="false"
            :on-success="handleAvatarSuccess"
            :before-upload="beforeAvatarUpload"
          >
            <img v-if="editForm.avatar" :src="editForm.avatar" class="avatar">
            <i v-else class="el-icon-plus avatar-uploader-icon"></i>
          </el-upload>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showEditDialog = false">取消</el-button>
        <el-button type="primary" @click="saveProfile" :loading="saving">保存</el-button>
      </div>
    </el-dialog>

    <!-- 修改密码对话框 -->
    <el-dialog title="修改密码" :visible.sync="showPasswordDialog">
      <el-form :model="passwordForm" ref="passwordForm" :rules="passwordRules">
        <el-form-item label="当前密码" prop="currentPassword">
          <el-input type="password" v-model="passwordForm.currentPassword" />
        </el-form-item>
        <el-form-item label="新密码" prop="newPassword">
          <el-input type="password" v-model="passwordForm.newPassword" />
        </el-form-item>
        <el-form-item label="确认密码" prop="confirmPassword">
          <el-input type="password" v-model="passwordForm.confirmPassword" />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="showPasswordDialog = false">取消</el-button>
        <el-button type="primary" @click="changePassword" :loading="changingPassword">修改</el-button>
      </div>
    </el-dialog>

    <!-- 修改密码按钮 -->
    <el-button 
      type="warning" 
      @click="showPasswordDialog = true" 
      class="password-btn"
    >
      修改密码
    </el-button>
  </div>
</template>

<script>
import request from '@/utils/request'

export default {
  name: 'Profile',
  data() {
    return {
      userInfo: {},
      stats: {},
      recentActivities: [],
      showEditDialog: false,
      showPasswordDialog: false,
      saving: false,
      changingPassword: false,
      editForm: {
        username: '',
        email: '',
        avatar: ''
      },
      editRules: {
        username: [{ required: true, message: '请输入用户名', trigger: 'blur' }],
        email: [
          { required: true, message: '请输入邮箱', trigger: 'blur' },
          { type: 'email', message: '请输入正确的邮箱格式', trigger: 'blur' }
        ]
      },
      passwordForm: {
        currentPassword: '',
        newPassword: '',
        confirmPassword: ''
      },
      passwordRules: {
        currentPassword: [{ required: true, message: '请输入当前密码', trigger: 'blur' }],
        newPassword: [
          { required: true, message: '请输入新密码', trigger: 'blur' },
          { min: 6, message: '密码长度不少于6位', trigger: 'blur' }
        ],
        confirmPassword: [
          { required: true, message: '请确认新密码', trigger: 'blur' },
          { validator: this.validateConfirmPassword, trigger: 'blur' }
        ]
      },
      uploadUrl: process.env.VUE_APP_API_BASE_URL + '/api/users/avatar',
      uploadHeaders: {
        'Authorization': 'Bearer ' + localStorage.getItem('token')
      }
    }
  },
  created() {
    this.fetchUserInfo()
    this.fetchStats()
    this.fetchRecentActivities()
  },
  methods: {
    fetchUserInfo() {
      request.get('/api/users/profile')
        .then(data => {
          this.userInfo = data
          this.editForm = {
            username: data.username,
            email: data.email,
            avatar: data.avatar
          }
        })
        .catch(() => {
          this.$message.error('获取用户信息失败')
        })
    },
    
    fetchStats() {
      request.get('/api/users/stats')
        .then(data => {
          this.stats = data
        })
        .catch(() => {
          this.$message.error('获取统计信息失败')
        })
    },
    
    fetchRecentActivities() {
      request.get('/api/users/activities')
        .then(data => {
          this.recentActivities = data
        })
        .catch(() => {
          this.$message.error('获取活动记录失败')
        })
    },
    
    getRoleColor(role) {
      return role === 'INSTRUCTOR' ? 'success' : 'primary'
    },
    
    getRoleText(role) {
      return role === 'INSTRUCTOR' ? '讲师' : '学生'
    },
    
    formatDate(dateString) {
      if (!dateString) return '未知'
      return new Date(dateString).toLocaleString('zh-CN')
    },
    
    saveProfile() {
      this.$refs.editForm.validate(valid => {
        if (valid) {
          this.saving = true
          request.put('/api/users/profile', this.editForm)
            .then(() => {
              this.$message.success('资料更新成功')
              this.showEditDialog = false
              this.fetchUserInfo()
              this.saving = false
            })
            .catch(() => {
              this.$message.error('资料更新失败')
              this.saving = false
            })
        }
      })
    },
    
    validateConfirmPassword(rule, value, callback) {
      if (value !== this.passwordForm.newPassword) {
        callback(new Error('两次输入密码不一致'))
      } else {
        callback()
      }
    },
    
    changePassword() {
      this.$refs.passwordForm.validate(valid => {
        if (valid) {
          this.changingPassword = true
          request.put('/api/users/password', this.passwordForm)
            .then(() => {
              this.$message.success('密码修改成功')
              this.showPasswordDialog = false
              this.passwordForm = {
                currentPassword: '',
                newPassword: '',
                confirmPassword: ''
              }
              this.changingPassword = false
            })
            .catch(() => {
              this.$message.error('密码修改失败')
              this.changingPassword = false
            })
        }
      })
    },
    
    beforeAvatarUpload(file) {
      const isJPG = file.type === 'image/jpeg' || file.type === 'image/png'
      const isLt2M = file.size / 1024 / 1024 < 2

      if (!isJPG) {
        this.$message.error('上传头像图片只能是 JPG/PNG 格式!')
      }
      if (!isLt2M) {
        this.$message.error('上传头像图片大小不能超过 2MB!')
      }
      return isJPG && isLt2M
    },
    
    handleAvatarSuccess(res, file) {
      this.editForm.avatar = URL.createObjectURL(file.raw)
    }
  }
}
</script>

<style scoped>
.profile-container {
  max-width: 1200px;
  margin: 0 auto;
  padding: 20px;
}

.profile-card {
  text-align: center;
}

.profile-header {
  padding: 20px 0;
}

.profile-avatar {
  margin-bottom: 15px;
}

.username {
  margin: 10px 0 5px 0;
  color: #303133;
}

.role-tag {
  margin-bottom: 20px;
}

.profile-info {
  text-align: left;
  margin-bottom: 20px;
}

.info-item {
  margin-bottom: 10px;
  display: flex;
  justify-content: space-between;
}

.label {
  color: #909399;
  font-weight: 500;
}

.value {
  color: #303133;
}

.edit-btn {
  width: 100%;
}

.stats-card {
  margin-bottom: 20px;
}

.stat-item {
  text-align: center;
  padding: 20px;
}

.stat-number {
  font-size: 24px;
  font-weight: bold;
  color: #409EFF;
}

.stat-label {
  font-size: 14px;
  color: #606266;
  margin-top: 5px;
}

.activity-card {
  margin-bottom: 20px;
}

.password-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
}

.dialog-footer {
  text-align: right;
}

.avatar-uploader .el-upload {
  border: 1px dashed #d9d9d9;
  border-radius: 6px;
  cursor: pointer;
  position: relative;
  overflow: hidden;
}

.avatar-uploader .el-upload:hover {
  border-color: #409EFF;
}

.avatar-uploader-icon {
  font-size: 28px;
  color: #8c939d;
  width: 178px;
  height: 178px;
  line-height: 178px;
  text-align: center;
}

.avatar {
  width: 178px;
  height: 178px;
  display: block;
}
</style> 