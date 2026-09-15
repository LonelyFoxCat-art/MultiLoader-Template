// 根项目不放任何模组构建逻辑：所有共享配置都在 BuildManager 里。
// 这里只应用根项目约定插件（汇总任务 + 环境自检任务）。
plugins {
    id("multiloader.root")
}
