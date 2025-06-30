<template>
  <div class="admin-container">
    <div class="sidebar">
      <h2>管理员面板</h2>
      <ul>
        <li @click="activeTab = 'users'">用户管理</li>
        <li @click="activeTab = 'courses'">课程管理</li>
        <li @click="activeTab = 'stats'">数据统计</li>
      </ul>
    </div>
    
    <div class="main-content">
    <button class="loginoutbutton" @click="loginout">退出登录</button>
      <div v-show="activeTab === 'users'">
        <h2>用户管理</h2>
        <table>
          <thead>
            <tr>
              <th style="width: 10px;">ID</th>
              <th style="width: 10px;">用户名</th>
              <th style="width: 20px;">密码</th>
              <th style="width: 10px;">身份</th>
              <th>操作</th> 
            </tr>
          </thead>
          <tbody>
            <tr v-for="user in users" :key="user.user_id">
              <td>{{ user.id }}</td>
              <td>{{ user.username }}</td>
              <td >
                <span v-if="!visiblePasswords[user.user_id]">******</span>
                <span v-else>{{ user.password }}</span>
              </td>
              <td>{{ user.role }}</td>
              <td>
                <button @click="editUser(user)">编辑</button>
                <button @click="deleteUser(user.id)">删除</button>
                <button @click="checkPassword(user.id)">查看密码</button>
              </td>
            </tr>
          </tbody>
        </table>
      </div>
      
      <!-- 课程管理 -->
      <div v-show="activeTab === 'courses'">
        <h3>课程管理</h3>
        <!-- 课程列表和操作 -->
      </div>
      
      <!-- 数据统计 -->
      <div v-show="activeTab === 'stats'">
        <h3>数据统计</h3>
        <!-- 统计图表 -->
      </div>
    </div>
    <div v-if="showEditWindow" class="overlay">
    <div class="editcotainer">
        <h2>编辑用户</h2>
        <label>修改密码:</label>
        <input type="password" placeholder="请输入新密码">
        <div class="selectcontainer">
        <label >修改身份:</label>
        <select v-model="newRole">
            <option value="student">学生</option>
            <option value="teacher">教师</option>
            <option value="admin">管理员</option>
        </select>
    </div>
    <button>确认修改</button>
    <button @click="showEditWindow=false">取消修改</button>
    </div>
    </div>
  </div>
  
</template>

<script>
import axios from 'axios';

export default {
  data() {
    return {
      activeTab: 'users',
      users: [],
      courses: [],
      showEditWindow: false,
      visiblePasswords: {} 
    }
  },
  created() {
    // 初始化获取数据
    this.fetchUsers();
    this.fetchCourses();
  },
  methods: {
    loginout(){
      this.$router.push('/')
    },
    checkPassword(user_id) {
        this.$set(this.visiblePasswords, user_id, !this.visiblePasswords[user_id]);
    },
    fetchUsers() {
    axios.get(`http://localhost:8081/api/user/getalluser`)
    .then(res=>{
      this.users=res.data;
    }).catch(err=>{
      console.log(err);
    })
    },
    fetchCourses() {
      // 调用API获取课程列表
    },
    editUser() {
        this.showEditWindow=!this.showEditWindow;
    },
    deleteUser(userId) {
    axios.post(`http://localhost:8081/api/user/deleteuserbyid`,{
      user_id:userId,
    })
    .then(res=>{
      this.fetchUsers();
      console.log(res);
    })
    .catch(err=>{
      console.log(err);

    })
    }
  }
}
</script>

<style scoped>
.loginoutbutton{    
  position: relative;
  text-align: right;
  background-color: white;
  border: 2px solid #000000;
  left:423px;
  border-radius: 5px;
  width: 70px;
  height: 35px !important;
  margin:0 7px;
}
table {
  border:2px solid black;
  box-shadow: 0 2px 8px rgba(0,0,0,0.1);
  table-layout: fixed; /* 固定表格布局 */;
  width: 200px;
 }

tr:hover {
  background-color: #f0f0f0; /* 鼠标悬停高亮 */
}
th,td{
width: 20px;
border: 2px solid #000000;
padding: 8px;
text-align: left;
}

.selectcontainer label, .selectcontainer select {
  position: relative;
  right: 85px;
  top:30px;
}
.overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background-color: rgba(0, 0, 0, 0.4); /* 黑色半透明 */
  display: flex;
  justify-content: center;
  align-items: center;
  z-index: 1000; /* 保证在最上层 */
}
.editcotainer{
  position: fixed;
  border-radius: 10px;
  width: 500px;
  height: 400px;
  top: 20%;
  left: 35%;
  background-color: white;
  z-index: 1001; /* 确保在 overlay 之上 */;
}
.editcotainer button{
  position: relative;
  top: 40px;
  text-align: center;
  width: 120px;
  height: 30px;
  border-radius: 5px;
  background-color: white;
  display: block;
  margin: 30px auto;
}
.editcotainer button:hover{
  background-color: #000000;
  color: white;
  cursor: pointer;
}
.editcotainer h2{
  margin-top: 50px;
}   
.editcotainer input{
  margin-top: 20px;
  width: 50%;
  height: 30px;
  border-radius: 5px;
  border: 2px solid #000000;
}
.editcotainer select {
  padding: 6px 10px;
  border-radius: 5px;
  border: 2px solid #000000;
  font-size: 14px;
}
.admin-container {
  background-color: #f0f0f0;
  display: flex;
  min-height: 100vh;
}
.sidebar {
  width: 180px;
  background: #2c3e50;
  color: white;
  padding: 20px;
  border-radius: 8px;
}
.sidebar h2 {
  position: relative;
  right:10px;
}
.sidebar li {
  position: relative;
  right:60px;
  cursor: pointer;
  margin-top: 25px;
  list-style-type: none;
}
.main-content {
  flex: 1;
  padding: 20px;
}
.main-content h2 {
  position: relative;
  right:8%;
}

.main-content button  {

  background-color: white;
  border: 2px solid #000000;
  border-radius: 5px;
  width: 70px;
  margin:0 7px;
  height: 25px;
}
.main-content button:hover {
  background-color: #000000;
  color: white;
  cursor: pointer;
}
table {
  position: relative;
  right:8%;
  width:80%;
  margin:auto;
  border-collapse: collapse;
}

</style>