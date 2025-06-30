<template>
  <div class="quiz">
    <el-card>
      <div slot="header" class="clearfix">
        <span>课程测验</span>
        <el-button style="float: right;" @click="$router.back()">返回</el-button>
      </div>
      <div v-if="loading" style="text-align:center"><el-spinner /></div>
      <div v-else-if="questions.length">
        <el-form :model="answers">
          <div v-for="(q, idx) in questions" :key="q.id" style="margin-bottom: 24px;">
            <div><b>Q{{ idx + 1 }}：</b>{{ q.questionText }}</div>
            <div v-if="q.questionType === 'MULTIPLE_CHOICE'">
              <el-radio-group v-model="answers[q.id]">
                <el-radio v-for="(opt, key) in q.options" :key="key" :label="opt">{{ opt }}</el-radio>
              </el-radio-group>
            </div>
            <div v-else-if="q.questionType === 'TRUE_FALSE'">
              <el-radio-group v-model="answers[q.id]">
                <el-radio label="true">正确</el-radio>
                <el-radio label="false">错误</el-radio>
              </el-radio-group>
            </div>
            <div v-else>
              <el-input v-model="answers[q.id]" placeholder="请输入答案" />
            </div>
          </div>
        </el-form>
        <el-button type="primary" @click="submitQuiz">提交</el-button>
      </div>
      <el-empty v-else description="暂无测验题目" />
    </el-card>
  </div>
</template>

<script>
import request from '@/utils/request'
export default {
  name: 'Quiz',
  data() {
    return {
      questions: [],
      answers: {},
      loading: true
    }
  },
  created() {
    this.fetchQuiz()
  },
  methods: {
    fetchQuiz() {
      const materialId = this.$route.params.materialId
      request.get(`/api/quizzes/material/${materialId}`)
        .then(data => {
          this.questions = data.questions || []
          this.loading = false
        })
        .catch(() => { this.loading = false })
    },
    submitQuiz() {
      // 这里仅做演示，实际应提交到后端
      this.$message.success('答题已提交！')
    }
  }
}
</script>

<style scoped>
.quiz { max-width: 700px; margin: 40px auto; }
</style> 