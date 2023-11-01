<template>
  <div class="app-container">
    <!-- 搜索工作栏 -->
    <el-form
      v-show="showSearch"
      ref="queryForm"
      :model="queryParams"
      size="small"
      :inline="true"
      label-width="75px"
    >
      <el-form-item label="名称" prop="name">
        <el-input
          v-model="queryParams.name"
          placeholder="请输入名称"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item label="接口名" prop="code">
        <el-input
          v-model="queryParams.code"
          placeholder="请输入接口名"
          clearable
          @keyup.enter.native="handleQuery"
        />
      </el-form-item>
      <el-form-item>
        <el-button type="primary" icon="el-icon-search" @click="handleQuery">
          搜索
        </el-button>
        <el-button icon="el-icon-refresh" @click="resetQuery">重置</el-button>
      </el-form-item>
    </el-form>

    <el-row>
      <el-col :span="4">
        <interface-group
          :group-list.sync="groupList"
          :group-map.sync="groupMap"
          :current-group.sync="currentGroup"
        />
      </el-col>
      <el-col :span="20" class="list">
        <!-- 操作工具栏 -->
        <el-row :gutter="10" class="mb8">
          <el-col :span="1.5">
            <el-button
              v-hasPermi="['system:interfaceitem:add']"
              type="primary"
              plain
              icon="el-icon-plus"
              size="mini"
              @click="handleAdd"
            >
              新增接口
            </el-button>
          </el-col>

          <right-toolbar :show-search.sync="showSearch" @queryTable="getList" />
        </el-row>

        <!-- 列表 -->
        <el-table v-loading="loading" :data="list">
          <el-table-column label="名称" align="center" prop="name" />
          <el-table-column label="分组" align="center" prop="groupId">
            <template slot-scope="scope">
              {{ getGroupName(scope.row.groupId) }}
            </template>
          </el-table-column>
          <el-table-column label="接口代号" align="center" prop="code" />
          <el-table-column label="类型" align="center" prop="type">
            <template slot-scope="scope">
              {{ scope.row.type === 1 ? 'SQL' : 'JavaScript' }}
            </template>
          </el-table-column>
          <el-table-column label="描述" prop="description" align="center">
            <template slot-scope="scope">
              <el-tooltip v-if="scope.row.description" placement="top">
                <div slot="content" style="white-space: pre">
                  <!--
                -->{{ scope.row.description }}
                </div>
                <div
                  style="
                    white-space: pre;
                    text-overflow: ellipsis;
                    overflow: hidden;
                  "
                >
                  <!--
                -->{{ scope.row.description }}
                </div>
              </el-tooltip>
            </template>
          </el-table-column>
          <el-table-column label="是否启用" align="center" prop="isEnable">
            <template slot-scope="scope">
              {{ scope.row.isEnable === 1 ? '是' : '否' }}
            </template>
          </el-table-column>
          <el-table-column label="权限标识" align="center" prop="permission">
            <template slot-scope="scope">
              {{ scope.row.permission || '--' }}
            </template>
          </el-table-column>
          <!--
          <el-table-column label="日志记录" align="center" prop="isLog">
            <template slot-scope="scope">
              {{ scope.row.isLog === 1 ? '是' : '否' }}
            </template>
          </el-table-column>
          -->
          <el-table-column
            label="更新时间"
            align="center"
            prop="updateTime"
            width="180"
          >
            <template slot-scope="scope">
              <span>{{ parseTime(scope.row.updateTime) }}</span>
            </template>
          </el-table-column>
          <el-table-column
            label="操作"
            align="center"
            class-name="small-padding fixed-width"
          >
            <template slot-scope="scope">
              <el-button
                v-hasPermi="['system:interfaceitem:edit']"
                size="mini"
                type="text"
                icon="el-icon-edit"
                @click="handleUpdate(scope.row)"
              >
                修改
              </el-button>
              <el-button
                v-hasPermi="['system:interfaceitem:remove']"
                size="mini"
                type="text"
                icon="el-icon-delete"
                @click="handleDelete(scope.row)"
              >
                删除
              </el-button>
            </template>
          </el-table-column>
        </el-table>
        <!-- 分页组件 -->
        <pagination
          v-show="total > 0"
          :total="total"
          :page.sync="queryParams.pageNo"
          :limit.sync="queryParams.pageSize"
          @pagination="getList"
        />
      </el-col>
    </el-row>

    <!-- 对话框(添加 / 修改) -->
    <code-dialog
      ref="codeDialog"
      :group-list="groupList"
      :group-map="groupMap"
      @submit="submitForm"
    />
  </div>
</template>

<script>
import {
  getInterfaceItem,
  getInterfaceItemPage,
  updateInterfaceItem,
  createInterfaceItem,
  deleteInterfaceItem,
} from '@/api/system/interface/interfaceItem';
import CodeDialog from './code';
import InterfaceGroup from './group';

export default {
  name: 'InterfaceItem',
  components: {
    CodeDialog,
    InterfaceGroup,
  },
  data() {
    return {
      // 遮罩层
      groupLoading: false,
      loading: true,
      // 导出遮罩层
      exportLoading: false,
      // 显示搜索条件
      showSearch: true,
      // 总条数
      total: 0,
      // 接口设置列表
      list: [],
      dateRangeCreateTime: [],
      // 分组
      currentGroup: null,
      groupList: [],
      groupMap: {},
      // 查询参数
      queryParams: {
        pageNo: 1,
        pageSize: 10,
        name: null,
        code: null,
        type: null,
        groupCode: null,
        permission: null,
      },
    };
  },
  watch: {
    currentGroup(newVal, oldVal) {
      if (newVal !== oldVal) {
        this.getList();
      }
    },
  },
  created() {
    this.getList();
  },
  methods: {
    /** 查询列表 */
    getList() {
      this.loading = true;
      // 处理查询参数
      const params = {
        ...this.queryParams,
      };
      if (this.currentGroup) {
        params.groupId = this.currentGroup;
      }
      // 执行查询
      getInterfaceItemPage(params).then((response) => {
        this.list = response.rows;
        this.total = response.total;
        this.loading = false;
      });
    },

    /** 搜索按钮操作 */
    handleQuery() {
      this.queryParams.pageNo = 1;
      this.getList();
    },
    /** 重置按钮操作 */
    resetQuery() {
      this.dateRangeCreateTime = [];
      this.resetForm('queryForm');
      this.handleQuery();
    },
    /** 新增按钮操作 */
    handleAdd() {
      this.$refs.codeDialog.openDialog({
        title: '添加接口设置',
        groupId: this.currentGroup,
      });
    },
    /** 修改按钮操作 */
    handleUpdate(row) {
      const id = row.id;
      getInterfaceItem(id).then((response) => {
        this.$refs.codeDialog.openDialog({
          title: '修改接口设置',
          form: response.data,
        });
      });
    },
    /** 提交按钮 */
    submitForm(form) {
      // 修改的提交
      if (form.id != null) {
        updateInterfaceItem(form).then((response) => {
          this.$refs.codeDialog.closeDialog();
          this.$notify({ title: '提示', message: '修改成功', type: 'success' });
          this.getList();
        });
        return;
      }
      // 添加的提交
      createInterfaceItem(form).then((response) => {
        this.$refs.codeDialog.closeDialog();
        this.$notify({ title: '提示', message: '新增成功', type: 'success' });
        this.getList();
      });
    },
    /** 删除按钮操作 */
    handleDelete(row) {
      const id = row.id;
      this.$modal
        .confirm('是否确认删除接口设置编号为"' + id + '"的数据项?')
        .then(function () {
          return deleteInterfaceItem(id);
        })
        .then(() => {
          this.getList();
          this.$notify({ title: '提示', message: '删除成功', type: 'success' });
        })
        .catch(() => {});
    },
    testInterface(row) {},
    getGroupName(id) {
      if (id === null) {
        return '--';
      }
      return this.groupMap[id]?.name ?? '--';
    },
  },
};
</script>
<style lang="scss" scoped>
.list {
  padding-left: 10px;
}
</style>
