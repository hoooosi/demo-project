# Easy Image Hosting UI

🖼️ 基于 Vue 3 + TypeScript + Ant Design Vue 的现代化图床系统前端

## Task

- [x] 优化样式

## 🚀 项目简介

Easy Image Hosting UI 是一个功能完整的企业级图床系统前端应用，提供直观易用的用户界面，支持多空间管理、图片上传与处理、权限控制等功能。项目采用现代化前端技术栈，具有高性能、响应式、用户体验优秀的特点。

### 主要特性

- ✅ **现代化技术栈**：基于 Vue 3 Composition API + TypeScript 构建
- ✅ **组件化开发**：使用 Ant Design Vue 4.x 组件库
- ✅ **多空间管理**：支持创建、加入、管理多个图片空间
- ✅ **图片管理**：支持拖拽上传、批量上传、格式转换、图片编辑
- ✅ **权限控制**：细粒度的空间权限管理（公共/成员权限）
- ✅ **响应式设计**：适配桌面端和移动端
- ✅ **状态管理**：基于 Pinia 的轻量级状态管理
- ✅ **路由管理**：基于 Vue Router 的前端路由
- ✅ **类型安全**：完整的 TypeScript 类型定义
- ✅ **API 集成**：自动生成的 API 接口类型定义

## 🛠 技术栈

### 核心框架

- **Vue 3.5.13** - 渐进式 JavaScript 框架
- **TypeScript 5.6.3** - JavaScript 的超集，提供类型安全
- **Vite 6.0.1** - 下一代前端构建工具

### UI 组件库

- **Ant Design Vue 4.x** - 企业级 UI 组件库
- **@ant-design/icons-vue 7.0.1** - Ant Design 图标库

### 状态管理与路由

- **Pinia 2.3.0** - Vue 官方状态管理库
- **Vue Router 4.5.0** - Vue 官方路由管理器

### 工具库

- **Axios 1.9.0** - HTTP 客户端
- **Spark-MD5 3.0.2** - MD5 文件哈希计算
- **QS 6.14.0** - 查询字符串解析和序列化

### 开发工具

- **@umijs/openapi 1.13.0** - OpenAPI 转 TypeScript 工具
- **Sass-embedded 1.83.0** - CSS 预处理器
- **Vue TSC 2.1.10** - Vue 3 TypeScript 类型检查

## 🏗 项目结构

```
demo-image-hosting-ui/
├── src/
│   ├── api/                        # API 接口
│   │   ├── index.ts               # API 导出
│   │   ├── req.d.ts               # 请求类型定义
│   │   ├── typings.d.ts           # 通用类型定义
│   │   └── vo.d.ts                # 视图对象类型定义
│   │
│   ├── assets/                     # 静态资源
│   │   └── style.scss             # 全局样式
│   │
│   ├── components/                 # 组件目录
│   │   ├── CustomSwitch.vue       # 自定义开关组件
│   │   ├── FormatConvertModal.vue # 格式转换弹窗
│   │   ├── GlobalHeader.vue       # 全局头部
│   │   ├── GridCom.vue            # 网格布局组件
│   │   ├── ImageCard.vue          # 图片卡片
│   │   ├── ImageDetailModal.vue   # 图片详情弹窗
│   │   ├── ImageEditForm.vue      # 图片编辑表单
│   │   ├── ImageFullscreen.vue    # 图片全屏查看
│   │   ├── ImageInfoDisplay.vue   # 图片信息展示
│   │   ├── ImageList.vue          # 图片列表
│   │   ├── NoData.vue             # 无数据占位
│   │   ├── Pagination.vue         # 分页组件
│   │   ├── Sort.vue               # 排序组件
│   │   ├── SpaceCard.vue          # 空间卡片
│   │   └── modal/                 # 弹窗组件
│   │       ├── ApplyModal.vue     # 申请加入空间
│   │       ├── EditSpaceModal.vue # 编辑空间
│   │       ├── JoinModal.vue      # 加入空间
│   │       ├── MemberMangeModal.vue # 成员管理
│   │       ├── NewUploadModal.vue # 新版上传
│   │       ├── PermissionModal.vue # 权限设置
│   │       ├── SpaceListModal.vue # 空间列表
│   │       └── UploadModal.vue    # 上传弹窗
│   │
│   ├── router/                     # 路由配置
│   │   ├── index.ts               # 路由实例
│   │   └── meta.ts                # 路由元信息
│   │
│   ├── stores/                     # 状态管理
│   │   └── dataStore.ts           # 数据状态
│   │
│   ├── utils/                      # 工具函数
│   │   └── index.ts               # 工具函数集合
│   │
│   ├── views/                      # 页面视图
│   │   ├── About.vue              # 关于页面
│   │   ├── Images.vue             # 图片浏览页
│   │   ├── Login.vue              # 登录页面
│   │   ├── Space.vue              # 空间详情页
│   │   ├── Spaces.vue             # 空间列表页
│   │   ├── Test.vue               # 测试页面
│   │   └── common/
│   │       └── BasicLayout.vue    # 基础布局
│   │
│   ├── App.vue                     # 根组件
│   ├── const.ts                    # 常量定义
│   └── main.ts                     # 应用入口
│
├── index.html                      # HTML 模板
├── vite.config.ts                  # Vite 配置
├── tsconfig.json                   # TypeScript 配置
├── package.json                    # 项目依赖
├── bun.lock                        # Bun 锁文件
└── README.md                       # 项目文档
```

## 📦 安装与使用

### 环境要求

- **Node.js**: 18+ 或 **Bun**: 1.0+
- **npm**: 9+ 或 **pnpm**: 8+ 或 **yarn**: 1.22+

### 安装依赖

使用 npm:

```powershell
npm install
```

使用 Bun（推荐，更快）:

```powershell
bun install
```

### 配置环境变量

在项目根目录创建 `.env` 文件：

```env
# 后端 API 地址
VITE_APP_API_URL=http://localhost:80
```

### 启动开发服务器

使用 npm:

```powershell
npm run dev
```

访问地址：http://localhost:3030
