# fan-mes Frontend

fan-mes（制造执行系统）管理平台前端项目。

## 技术栈

- **Vue 3** - Composition API + `<script setup>`
- **TypeScript** - 类型安全
- **Vite** - 构建工具
- **Vue Router** - 路由管理
- **Pinia** - 状态管理
- **Element Plus** - UI 组件库
- **Axios** - HTTP 请求
- **SCSS** - 样式预处理

## 目录结构

```
frontend/
├── .github/                # GitHub 配置
├── .vscode/                # VS Code 配置
├── public/                 # 静态资源
├── src/
│   ├── api/                # API 接口
│   ├── components/         # 公共组件
│   ├── layouts/            # 布局组件
│   ├── router/             # 路由配置
│   ├── store/              # Pinia 状态管理
│   ├── styles/             # 全局样式
│   ├── utils/              # 工具函数
│   ├── views/              # 页面视图
│   ├── App.vue             # 根组件
│   └── main.ts             # 入口文件
├── .env.development        # 开发环境变量
├── .env.production         # 生产环境变量
├── .eslintrc.config.js     # ESLint 配置
├── .prettierrc             # Prettier 配置
├── vite.config.ts          # Vite 配置
└── package.json
```

## 快速开始

```bash
# 安装依赖
npm install

# 启动开发服务器
npm run dev

# 生产构建
npm run build

# 代码检查
npm run lint

# 代码格式化
npm run format
```

## 环境变量

- `VITE_APP_TITLE` - 应用标题
- `VITE_API_BASE_URL` - API 基础地址
