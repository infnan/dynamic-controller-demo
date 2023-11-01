<template>
  <div v-loading="loading">
    <div class="tool">
      <el-button
        v-hasPermi="['system:interfacegroup:add']"
        type="primary"
        plain
        icon="el-icon-plus"
        size="mini"
        @click="handleAdd(null)"
      >
        新增分组
      </el-button>
    </div>
    <el-tree
      :data="groupList"
      :props="defaultProps"
      default-expand-all
      :expand-on-click-node="false"
      :render-content="renderContent"
      @node-click="handleNodeClick"
    />
    <!-- 对话框(添加 / 修改) -->
    <el-dialog
      :title="title"
      :visible.sync="open"
      width="600px"
      append-to-body
      :close-on-click-modal="false"
    >
      <el-form ref="form" :model="form" :rules="rules" label-width="80px">
        <el-form-item label="上级分组">
          {{ parentName }}
        </el-form-item>
        <el-form-item label="分组名称" prop="name">
          <el-input v-model="form.name" placeholder="请输入分组名称" />
        </el-form-item>
        <el-form-item label="URL" prop="code">
          <el-input
            v-model="form.code"
            :disabled="form.id !== null"
            placeholder="请输入分组URL"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button type="primary" @click="submitForm">确定</el-button>
        <el-button @click="cancel">取消</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  getInterfaceGroupList,
  updateInterfaceGroup,
  createInterfaceGroup,
  deleteInterfaceGroup,
} from '@/api/system/interface/interfaceGroup';
export default {
  data() {
    return {
      loading: false,
      rawList: [],
      groupList: [],
      groupMap: {},
      currentGroup: null,
      defaultProps: {
        children: 'children',
        label: 'name',
      },

      title: '',
      open: false,
      form: {},
      parentName: '',
      // 表单校验
      rules: {
        name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
        code: [{ required: true, message: 'URL不能为空', trigger: 'blur' }],
      },
    };
  },
  watch: {
    groupList(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit('update:group-list', newVal);
      }
    },
    groupMap(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit('update:group-map', newVal);
      }
    },
    currentGroup(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.$emit('update:current-group', newVal);
      }
    },
  },
  created() {
    this.getGroup();
  },
  mounted() {},
  methods: {
    getGroup() {
      this.loading = true;
      getInterfaceGroupList().then((response) => {
        let rawList = response.data;
        if (!rawList.find((item) => item.code === '')) {
          rawList = [
            {
              id: null,
              name: '未分组',
              code: '',
              parentId: null,
            },
            ...rawList,
          ];
        }

        this.rawList = rawList;

        const groupList = [];
        const groupMap = {};
        for (const item of rawList) {
          if (item.parentId === null) {
            const obj = {
              ...item,
              url: item.code,
              children: [],
            };
            groupMap[item.id] = obj;
            groupList.push(obj);
          }
        }
        for (const item of rawList) {
          if (item.parentId !== null) {
            const parent = groupMap[item.parentId];
            if (parent) {
              const obj = {
                ...item,
                url: parent.url + '/' + item.code,
              };
              groupMap[item.id] = obj;
              parent.children.push(obj);
            }
          }
        }

        this.groupList = groupList;
        this.groupMap = groupMap;
        this.loading = false;
      });
    },
    handleNodeClick(data) {
      this.currentGroup = data.id;
    },
    handleAdd(parent) {
      let form;
      if (parent === null) {
        form = {
          id: null,
        };
      } else {
        form = {
          id: null,
          parentId: parent.id,
        };
        this.parentName = parent.name;
      }

      this.form = form;
      this.title = '新增分组';
      this.open = true;
    },
    renderContent(h, { node, data, store }) {
      let add = null;
      if (data.id === null) {
        return (
          <span class="custom-tree-node interface-group">{data.name}</span>
        );
      }

      if (data.parentId == null) {
        add = (
          <el-button
            size="mini"
            type="text"
            on-click={(e) => {
              e.stopPropagation();
              this.handleAdd(data);
            }}
          >
            +
          </el-button>
        );
      }

      let del = null;
      if (data.children?.length > 0) {
        del = (
          <el-button
            size="mini"
            type="text"
            on-click={(e) => {
              e.stopPropagation();
              this.$notify({
                title: '提示',
                message: '请先删除子分类及其接口',
                type: 'warning',
              });
            }}
          >
            删除
          </el-button>
        );
      } else {
        del = (
          <el-button
            size="mini"
            type="text"
            on-click={(e) => {
              e.stopPropagation();
              this.handleDelete(data);
            }}
          >
            删除
          </el-button>
        );
      }

      return (
        <span class="custom-tree-node interface-group">
          <span>{data.name}</span>
          <span>
            {add}
            <el-button
              size="mini"
              type="text"
              on-click={(e) => {
                e.stopPropagation();
                this.handleUpdate(data);
              }}
            >
              修改
            </el-button>
            {del}
          </span>
        </span>
      );
    },
    handleUpdate(item) {
      this.form = { ...item };
      if (item.parentId) {
        this.parentName = this.rawList.find(
          (x) => x.id === item.parentId
        )?.name;
      } else {
        this.parentName = '';
      }
      this.title = '修改分组';
      this.open = true;
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal
        .confirm('是否确认删除接口设置编号为"' + id + '"的数据项?')
        .then(function () {
          return deleteInterfaceGroup(id);
        })
        .then(() => {
          this.getGroup();
          this.$notify({ title: '提示', message: '删除成功', type: 'success' });
        })
        .catch(() => {});
    },
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (!valid) {
          return;
        }
        if (this.form.id) {
          updateInterfaceGroup(this.form).then((response) => {
            this.$notify({
              title: '提示',
              message: '修改成功',
              type: 'success',
            });
            this.open = false;
            this.getGroup();
          });
        } else {
          const form = { ...this.form };
          delete form.id;
          createInterfaceGroup(form).then((response) => {
            this.$notify({
              title: '提示',
              message: '新增成功',
              type: 'success',
            });
            this.open = false;
            this.getGroup();
          });
        }
      });
    },
    cancel() {
      this.open = false;
    },
  },
};
</script>
<style>
.custom-tree-node.interface-group {
  flex: 1;
  display: flex;
  align-items: center;
  justify-content: space-between;
  font-size: 14px;
  padding-right: 8px;
}
</style>
<style lang="scss" scoped>
.tool {
  margin-bottom: 10px;
}
</style>
