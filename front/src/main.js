import Vue from 'vue'
import App from './App.vue'
import router from './router'
import store from './store'

// 导入Element UI
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'

Vue.use(ElementUI)

Vue.config.productionTip = false

store.dispatch('initAuth')

new Vue({
  router,
  store,
  render: h => h(App)
}).$mount('#app')
