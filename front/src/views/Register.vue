<template>
    <div class="login-container">
      <div class="login-card">
        <div class="login-header">
          <h1>在线学习平台</h1>
          <p>请输入您要注册的用户名跟密码</p>
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
            <label>角色</label>
            <select v-model="userType" class="form-control">
              <option value="STUDENT">学生</option>
              <option value="TEACHER">老师</option>
            </select>
          </div>
          <button @click="handleregister" class="register-button">注册</button>
          <button class="register-button" @click="returnlogin">返回登录</button>
      </div>
    </div>
  </template>
  
  <script>
  import aoxis from 'axios'
  export default {
    data() {
      return {
        username: '',
        password: '',
        userTyppe: 'STUDENT'
      };
    },
    methods: {
      returnlogin(){
          this.$router.push({path:'/login'})
      },
      handleregister() {
         aoxis.post(`http://localhost:8081/api/user/register`,{
          username:this.username,
          password:this.password,
          role:this.userType,
        })
      .then(res=>{
          alert('注册成功',res.data)
          this.$router.push({path:'/login'})
      }
      )
        .catch(err=>{
          if(err.response){
            console.log("注册失败,"+err.response.data.message)
            alert("注册失败,"+err.response.data.message) // 输出错误信息
          }else{
                console.log("网络错误",err.message)
          }
        })
      }
    }
  }
  </script>
  
  <style scoped>
.login-container {
  border-radius: 8px;
  display: flex;
  justify-content: center;
  align-items: center;
  min-height: 100vh;
  background-color: #f5f7fa;
  background: linear-gradient(135deg, #1b2276, #820ca2);
}

.login-card {
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

.login-header h1 {
  font-size:40px;
  color: #0a4297;
  margin-top: 20px;
  margin-bottom: 20px;
}

.login-header p {
  margin-bottom: 20px;
  color: #666;
  font-size: 14px;
}

.form-group {
  margin: 40px;
}
.formcontrol {
  width: 100%;
}

.form-group label {
  text-align: center;
  position: relative;
  top:-15px;
  margin-bottom: 10px;
  display: block;
  margin-bottom: 8px;
  color: #333;
  font-weight: 500;
}

.form-group input {
  display: block;
  margin:0 auto;
  width: 80%;
  padding: 12px;
  border: 1px solid #ddd;
  border-radius: 5px;
  font-size: 14px;
  margin:auto;
}
.form-control{
  display: block;
  margin:0 auto;
  width:85%;
}

.register-button {
  
  width: 75%;
  padding: 12px;
  background-color: #3498db;
  color: white;
  border: none;
  border-radius: 5px;
  font-size: 16px;
  cursor: pointer;
  transition: background-color 0.3s;
  margin: 20px auto ; /* 调整按钮与输入框之间的距离 */
  display:block;
  box-shadow: 4px 4px 5px rgba(0, 0, 0, 0.5); /* 添加阴影效果 */;
}

.register-button:hover {
  background-color: #337ecc;
}

.register-prompt {
  text-align: center;
  margin-top: 20px;
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
  
  