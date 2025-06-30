<template>
  <el-dialog :visible.sync="visible" title="测验详情" width="80%">
    <div>
      <h2>{{ quiz.title }}</h2>
      <p>课程：{{ quiz.courseTitle }}</p>
      <p>创建时间：{{ quiz.createdAt }}</p>
      <p>参与学生</p>
      <el-table :data="quiz.students || []" style="width:100%">
        <el-table-column prop="studentId" label="学生ID" width="100"/>
        <el-table-column prop="username" label="用户名" width="150"/>
        <el-table-column label="操作" width="150">
          <template slot-scope="scope">
            <el-button size="mini" @click="viewStudentQuiz(scope.row)">查看答题</el-button>
          </template>
        </el-table-column>
      </el-table>
    </div>
  </el-dialog>
</template>

<script>
export default {
  props: {
    visible: Boolean,
    quiz: Object
  },
  methods: {
    viewStudentQuiz(student) {
      this.$router.push({
        name: 'StudentQuizDetail',
        params: { quizId: this.quiz.id, studentId: student.studentId }
      })
    }
  }
}
</script> 