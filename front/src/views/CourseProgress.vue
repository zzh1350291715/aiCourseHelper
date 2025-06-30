<template>
  <div class="course-progress">
    <el-card>
      <div slot="header" class="clearfix">
        <span>学习进度</span>
        <el-button style="float: right;" @click="$router.back()">返回</el-button>
      </div>
      <el-table :data="progressList" style="width: 100%">
        <el-table-column prop="material.title" label="资料标题" />
        <el-table-column prop="status" label="状态">
          <template slot-scope="scope">
            <el-tag v-if="scope.row.status === 'COMPLETED'" type="success">已完成</el-tag>
            <el-tag v-else-if="scope.row.status === 'IN_PROGRESS'" type="warning">学习中</el-tag>
            <el-tag v-else>未开始</el-tag>
          </template>
        </el-table-column>
        <el-table-column prop="lastAccessedAt" label="最后访问时间" />
      </el-table>
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'CourseProgress',
  data() {
    return {
      progressList: []
    }
  },
  created() {
    this.fetchProgress()
  },
  methods: {
    fetchProgress() {
      const courseId = this.$route.params.courseId
      const studentId = 1 // 实际应从登录用户信息获取
      request.get(`/api/student-progress/student/${studentId}/course/${courseId}`)
        .then(data => { this.progressList = data })
    }
  }
}
</script>

<style scoped>
.course-progress { max-width: 900px; margin: 40px auto; }
</style> 