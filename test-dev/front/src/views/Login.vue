<template>
  <div class="login-container">
    <div class="login-card">
      <div class="login-header">
        <h2>在线学习平台</h2>
        <p>欢迎回来，请登录您的账号</p>
      </div>
        <div class="form-group">
          <label for="username">用户名</label>
          <input
            type="text"
            id="username"
            v-model="username"
            required
            placeholder="请输入用户名"
          >
        </div>
        <div class="form-group">
          <label for="password">密码</label>
          <input
            type="password"
            id="password"
            v-model="password"
            required
            placeholder="请输入密码"
          >
        </div>
        <div class="form-group">
          <label>身份</label>
          <select v-model="userType" class="form-control">
            <option value="STUDENT">学生</option>
            <option value="TEACHER">老师</option>
            <option value="ADMIN">管理员</option>
          </select>
        </div>
        <button @click="handleLogin" class="login-button">登录</button>
       <div class="register-prompt">
          还没有账号? <button class="register-button" @click="register">去注册</button>
        </div>
    </div>
  </div>
</template>

<script>
import { mapActions, mapGetters } from 'vuex'; // 引入 mapActions 和 mapGetters
export default {
  data() {
    return {
      username: '',
      password: '',
      userType: 'STUDENT'
    };
  },
  computed: {
    ...mapGetters(['isLoggedIn', 'currentUser'])
  },
  methods: {
    ...mapActions(['login']), // 映射 Vuex 的 login 方法
    register() {
      this.$router.push('/register');
    },
    async handleLogin() {
      try {
        const user = await this.login({
          username: this.username,
          password: this.password,
          role: this.userType
        })

        // 登录成功后，判断角色跳转页面
        const role = user.role
        if (role === 'TEACHER') {
          this.$router.push('/dashboard')
        } else if (role === 'ADMIN') {
          this.$router.push('/admin')
        } else {
          this.$router.push('/dashboard')
        }

      } catch (err) {
        if (err.response && err.response.data && err.response.data.message) {
          alert('登录失败: ' + err.response.data.message)
        } else {
          alert('登录失败，请稍后再试')
        }
      }
    }
  }
}

</script>

<style scoped>
.title{
  color: #1e5e9f;
}
.register-button{
  position: relative;
  top:0.3px;
  font-size: 15px;
 color:#409eff;
 background-color: white;
 border: none;
 outline: none;
}
.register-button:hover{
  cursor: pointer;
}
.login-container {
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  background: linear-gradient(135deg, #131e9b, #9d1fc0);
  background-size: 400% 400%;
  animation: gradientBG 4s ease infinite;
}
@keyframes gradientBG {
  0% {background-position: 0% 50%;}
  50% {background-position: 100% 50%;}
  100% {background-position: 0% 50%;}
}

.login-card {

  height: 620px;
  width: 650px;
  padding: 30px;
  background-color: white;
  border-radius: 10px;
  box-shadow: 0 2px 15px rgba(0, 0, 0, 0.1);
}

.login-header {

  text-align: center;
  margin-bottom: 60px;
}

.login-header h2 {
  
  font-size:40px;
  color: #0a4297;
  margin-bottom: 10px;
}

.login-header p {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.form-group {
  margin: 40px;
}


.form-group label {
  text-align: center;
  position: relative;
  top:-15px;
  margin-bottom: 5px;
  display: block;
  margin-bottom: 2px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  display: block;
  margin:0 auto;
  width: 90%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
}

.form-control{
  display: block;
  margin:0 auto;
  top:-5  px;
  width:76%;
  margin:0px;
  position: relative;
  left:30px;
  width: 90%;
}

.login-button {
  display: block;
  margin:0 auto;
  width: 80%;
  padding: 12px;
  background-color: #3498db; 
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin-bottom: 20px; /* 调整按钮与输入框之间的距离 */
  box-shadow: 4px 4px 5px rgba(0, 0, 0, 0.5); /* 添加阴影效果 */;
}

.login-button:hover {
  background-color: #337ecc;
}

.register-prompt {
  text-align: center;
  margin-top: 0px;
  color: #666;
  font-size: 14px;
}

.register-prompt a {
  color: #409eff;
  text-decoration: none;
}
</style>

<style>
/* 添加选择框样式 */
.form-control {
  width: 100%;
  padding: 10px;
  margin-bottom: 15px;
  border: 1px solid #ddd;
  border-radius: 4px;
}
</style>

