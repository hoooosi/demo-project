# ImageCard 组件重构说明

## 组件结构

原来的 `ImageCard.vue`（800+ 行）已被拆分为以下 6 个组件：

### 1. **ImageCard.vue** (主组件)
- 负责图片卡片的展示和基础交互
- 处理卡片点击、删除操作
- 管理子组件的显示状态
- **约 150 行代码**

### 2. **ImageDetailModal.vue** (详情弹窗)
- 管理图片详情弹窗的整体逻辑
- 处理格式切换
- 协调编辑表单和信息显示组件
- 加载标签数据
- **约 150 行代码**

### 3. **ImageEditForm.vue** (编辑表单)
- 图片信息编辑表单（名称、描述、标签）
- 使用 `a-select` 的 tags 模式支持多标签选择
- 表单验证和提交逻辑
- **约 80 行代码**

### 4. **ImageInfoDisplay.vue** (信息展示)
- 只读模式下的图片信息展示
- 包含所有详细信息：尺寸、格式、URL、创建者等
- URL 复制功能
- 自定义滚动条样式
- **约 180 行代码**

### 5. **FormatConvertModal.vue** (格式转换)
- 图片格式转换功能
- 支持 JPEG、PNG、WEBP 格式
- 转换确认和进度提示
- **约 120 行代码**

### 6. **ImageFullscreen.vue** (全屏预览)
- 全屏图片预览功能
- 简单的关闭控制
- **约 60 行代码**

## 技术要点

### 使用 Vue 3.4+ defineModel
所有组件间的双向绑定都使用 `defineModel`：

```typescript
// 子组件
const open = defineModel<boolean>("open", { default: false });

// 父组件
<ImageDetailModal v-model:open="showDetailModal" ... />
```

### 类型安全
- 所有组件都使用 TypeScript 严格类型
- Props 和 Emits 都有完整的类型定义
- 使用 VO 命名空间的类型定义

### 组件通信
- **Props**: 父传子的数据
- **Emits**: 子传父的事件（reload、fullscreen）
- **defineModel**: 双向绑定状态（open、selected）

### 样式隔离
- 每个组件都使用 `scoped` 样式
- 使用 SCSS 嵌套语法
- 自定义滚动条样式

## 数据流

```
ImageCard (主组件)
  ├─> ImageDetailModal (详情弹窗)
  │     ├─> ImageEditForm (编辑表单)
  │     ├─> ImageInfoDisplay (信息展示)
  │     └─> FormatConvertModal (格式转换)
  └─> ImageFullscreen (全屏预览)
```

## API 调用
- `ImageApi.listTags()` - 获取所有标签
- `ImageApi.edit()` - 保存编辑
- `ImageApi.convert()` - 格式转换
- `ImageApi.delete()` - 删除图片

## 优势

1. **可维护性**: 每个组件职责单一，易于理解和修改
2. **可复用性**: 子组件可以在其他地方独立使用
3. **可测试性**: 小组件更容易编写单元测试
4. **性能**: 按需加载，减少初始加载体积
5. **代码清晰**: 从 800+ 行拆分为 6 个 60-180 行的组件

## 备份

原始文件已备份为 `ImageCard.old.vue`，如需回滚可以使用。
