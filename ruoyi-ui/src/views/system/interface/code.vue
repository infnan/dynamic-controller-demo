<template>
  <!-- 对话框(添加 / 修改) -->
  <el-dialog
    :title="title"
    :visible.sync="open"
    width="1200px"
    append-to-body
    :close-on-click-modal="false"
  >
    <el-row>
      <el-col :span="16" class="form">
        <el-form ref="form" :model="form" :rules="rules" label-width="80px">
          <el-row>
            <el-col :span="12">
              <el-form-item label="接口名称" prop="name">
                <el-input v-model="form.name" placeholder="请输入接口名称" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="接口分组" prop="groupId">
                <el-cascader
                  v-model="form.groupId"
                  placeholder="请选择接口分组"
                  :options="groupList"
                  :props="{
                    checkStrictly: true,
                    label: 'name',
                    value: 'id',
                    emitPath: false,
                  }"
                  clearable
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="接口代号" prop="code">
                <el-input v-model="form.code" placeholder="请输入接口代号" />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item label="接口URL">
            {{ getGroupUrl(form.groupId) }}{{ form.code }}
          </el-form-item>
          <el-row>
            <el-col :span="12">
              <el-form-item label="类型" prop="type">
                <el-select v-model="form.type" placeholder="请选择类型">
                  <el-option label="SQL查询" :value="1" />
                  <el-option label="JavaScript（ES5）" :value="2" />
                </el-select>
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="请求方式" prop="method">
                <el-select v-model="form.method" placeholder="请选择请求方式">
                  <el-option label="任意" value="any" />
                  <el-option label="GET" value="GET" />
                  <el-option
                    label="POST (x-www-form-urlencoded)"
                    value="POST+form"
                  />
                  <el-option
                    label="POST (application/json)"
                    value="POST+json"
                  />
                  <!-- <el-option label="POST (Raw)" value="post+raw" /> -->
                </el-select>
              </el-form-item>
            </el-col>
          </el-row>
          <el-row>
            <el-col :span="12">
              <el-form-item label="启用" prop="isEnable">
                <el-checkbox
                  v-model="form.isEnable"
                  :true-label="1"
                  :false-label="0"
                >
                  启用接口
                </el-checkbox>
              </el-form-item>
            </el-col>
            <!--
            <el-col :span="12">
              <el-form-item label="日志" prop="isLog">
                <el-checkbox
                  v-model="form.isLog"
                  :true-label="1"
                  :false-label="0"
                >
                  记录调用日志
                </el-checkbox>
              </el-form-item>
            </el-col>
            -->
          </el-row>
          <el-form-item label="接口描述">
            <el-input
              v-model="form.description"
              type="textarea"
              :rows="5"
              placeholder="请输入接口描述"
            />
          </el-form-item>
          <el-row>
            <el-col :span="12">
              <el-form-item label="输入参数" prop="param">
                <el-input
                  v-model="form.param"
                  placeholder="输入参数字段名称，英文逗号分割"
                />
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="权限标识" prop="permission">
                <el-input
                  v-model="form.permission"
                  placeholder="请输入权限标识，“*”表示免登录认证"
                />
              </el-form-item>
            </el-col>
          </el-row>
          <el-form-item
            v-show="form.type === 1"
            label="数据源"
            prop="datasource"
          >
            <el-input
              v-model="form.datasource"
              placeholder="请输入数据源，默认master"
            />
          </el-form-item>
          <el-form-item v-if="form.type === 1" label="SQL" prop="program">
            <el-input
              v-model="form.program"
              type="textarea"
              :rows="10"
              placeholder="请输入SQL语句"
            />
            提示：<br />（1）支持MyBatis的参数绑定和动态SQL（需用&lt;script&gt;包围）；<br />（2）返回字段根据SQL自动识别；<br />（3）接口返回格式：<code
              >{"code" : 0, "msg": "", "data": [{"X": 0, "Y": 1}, {"X":1,
              "Y":2}, ...]}</code
            ><br />（4）SQL限制：仅支持查询，一次性最多100000条记录，最长执行时间30秒。
          </el-form-item>
          <el-form-item v-if="form.type === 2" label="代码" prop="program">
            <el-input
              v-model="form.program"
              type="textarea"
              :rows="10"
              placeholder="请输入JS代码"
            />
            提示：<br />（1）通过<code>paramMap["名称"]</code>获取输入参数；<br />（2）使用<code>return</code>返回结果；<br />（3）使用<code>AppUtil.executeQuery</code>执行SQL查询并获取结果，executeSql执行修改类操作；<br />（4）使用<code>AppUtil.getBean</code>获取Spring
            Bean；<br />（5）通过<code>importPackage(com.xxxx)</code>引入Java包；<br />（6）通过修改源代码的InterfaceUtil类来丰富AppUtil功能；<br />（7）不支持ES6语法。
          </el-form-item>
          <el-row v-if="form.id">
            <el-col :span="12">
              <el-form-item label="创建时间" prop="createTime">
                {{ parseTime(form.createTime) }}
              </el-form-item>
            </el-col>
            <el-col :span="12">
              <el-form-item label="更新时间" prop="updateTime">
                {{ parseTime(form.updateTime) }}
              </el-form-item>
            </el-col>
          </el-row>
        </el-form>
      </el-col>
      <el-col :span="8" class="test">
        <strong>测试区</strong>
        <div style="margin: 10px 0">
          接口URL：{{ getGroupUrl(form.groupId) }}{{ form.code }}
        </div>

        <el-form ref="form2" :model="debugField" label-width="80px">
          <el-form-item
            v-for="item in debugFields"
            :key="'f' + item"
            :label="item"
            prop="item"
          >
            <el-col :span="16">
              <el-input
                v-model="debugField[item]"
                :placeholder="'请输入' + item"
              />
            </el-col>
            <el-col :span="8">
              <el-select v-model="debugFieldType[item]" placeholder="数据类型">
                <el-option label="字符串" value="String" />
                <el-option label="数值型" value="Number" />
                <el-option label="布尔型" value="Boolean" />
              </el-select>
            </el-col>
          </el-form-item>
          <el-form-item>
            <el-button v-loading="debugLoading" type="primary" @click="debug">
              提交
            </el-button>
          </el-form-item>
        </el-form>
        <el-row>
          <strong>测试结果</strong>
          <el-input v-model="debugResult" type="textarea" :rows="20" />
        </el-row>
      </el-col>
    </el-row>
    <div slot="footer" class="dialog-footer">
      <el-button type="primary" @click="submitForm">确定</el-button>
      <el-button @click="cancel">取消</el-button>
    </div>
  </el-dialog>
</template>

<script>
import { debugInterface } from '@/api/system/interface/interfaceItem';

export default {
  components: {},
  props: {
    groupList: {
      type: Array,
      default: () => [],
    },
    groupMap: {
      type: Object,
      default: () => ({}),
    },
  },
  data() {
    return {
      // 标题
      title: '',
      // 是否显示弹出层
      open: false,
      // 表单参数
      form: {},
      // 表单校验
      rules: {
        name: [{ required: true, message: '名称不能为空', trigger: 'blur' }],
        code: [{ required: true, message: '接口名不能为空', trigger: 'blur' }],
        type: [
          {
            required: true,
            message: '类型不能为空',
            trigger: 'change',
          },
        ],
      },

      debugField: {},
      debugFieldType: {},
      debugResult: '',
      debugLoading: false,
    };
  },
  computed: {
    debugFields() {
      let params = this.form.param;
      if (!params) {
        return [];
      }

      params = params.split(',');
      const result = [];
      const map = {};
      for (const s of params) {
        const t = s.trim();
        if (t && !map[t]) {
          map[t] = true;
          result.push(t);
        }
      }
      return result;
    },
  },
  created() {},
  methods: {
    openDialog({ title, form, groupId } = {}) {
      this.title = title;
      this.debugField = {};
      this.debugFieldType = {};
      this.debugResult = '';
      if (form) {
        this.form = form;
      } else {
        this.reset({ groupId });
      }
      this.open = true;
    },
    closeDialog() {
      this.open = false;
    },
    /** 取消按钮 */
    cancel() {
      this.open = false;
      this.reset();
    },
    /** 表单重置 */
    reset({ groupId = null } = {}) {
      this.form = {
        id: undefined,
        name: undefined,
        code: undefined,
        type: 1,
        isEnable: 1,
        method: 'any',
        description: undefined,
        groupId,
        groupCode: undefined,
        isLog: 0,
        permission: undefined,
        param: undefined,
        program: undefined,
        datasource: undefined,
      };
      this.resetForm('form');
    },
    submitForm() {
      this.$refs['form'].validate((valid) => {
        if (!valid) {
          return;
        }
        const form = { ...this.form };
        this.$emit('submit', form);
      });
    },
    debug() {
      const paramMap = {};
      for (const item of this.debugFields) {
        paramMap[item] = this.debugField[item];
        const type = this.debugFieldType[item];
        if (type === 'Number') {
          paramMap[item] = Number(paramMap[item]);
        } else if (type === 'Boolean') {
          if (
            !paramMap[item] ||
            (paramMap[item] + '').toLowerCase() === 'false'
          ) {
            paramMap[item] = false;
          } else {
            paramMap[item] = true;
          }
        }
      }

      this.debugLoading = true;
      debugInterface({
        type: this.form.type,
        code: this.form.program,
        inputParam: this.form.param,
        datasource: this.form.datasource,
        paramMap,
      }).then((response) => {
        this.debugLoading = false;
        if (typeof response === 'string') {
          this.debugResult = response;
        } else {
          this.debugResult = JSON.stringify(response, null, 2);
        }
      });
    },
    getGroupUrl(id) {
      let url = this.groupMap?.[id]?.url ?? '';
      if (url) {
        url += '/';
      }
      url = '/webapi/' + url;
      return url;
    },
  },
};
</script>
<style lang="scss" scoped>
.form {
  padding-right: 10px;
  border-right: 1px solid #eee;
}

.test {
  padding-left: 10px;
}
</style>
