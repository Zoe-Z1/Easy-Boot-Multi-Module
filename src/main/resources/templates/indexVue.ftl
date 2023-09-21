<template>
  <div class="top">
    <common-form
      ref="commonForm"
      :disabled="disabled"
      :options="options"
      @handlerClick="handlerClick"
      @advancedSearch="getList"
    >
      <template slot="advanced-content">
        <el-form ref="advancedForm" style="margin-top: 20px;" :model="queryForm" label-width="80px">
          <#list columns as column>
            <#if column.isAdvancedSearch == 0>
          <el-form-item label="${column.columnRemarks!}">
            <el-input v-model="queryForm.${column.javaName}" clearable />
          </el-form-item>
            </#if>
          </#list>
        </el-form>
      </template>
    </common-form>
    <common-table
      :table-data="data"
      :columns="columns"
      :height="height"
      :current-page="queryForm.pageNum"
      :page-size="queryForm.pageSize"
      :total="totalPage"
      :loading="loading"
      @handleSizeChange="handleSizeChange"
      @handleCurrentChange="handleCurrentChange"
      @handleSelectionChange="handleSelectionChange"
    >
      <!-- 列表序号 -->
      <template #serialNumber="scope">
        <span>{{
          Number(scope.index + 1) + (queryForm.pageNum - 1) * queryForm.pageSize
        }}</span>
      </template>
      <#list columns as column>
      <#if column.dictDomainCode??>
      <!-- ${column.columnRemarks!} -->
      <template #${column.javaName}="scope">
        <template v-for="(item, index) in ${column.javaName}List">
          <el-tag
            v-if="scope.row.${column.javaName} == item.code"
            :key="index"
          >{{ item.label }}</el-tag>
        </template>
      </template>
      </#if>
      </#list>
      <!-- 列表操作按钮 -->
      <template #operation="scope">
        <el-button
          v-throttle
          v-permission="'${permission}:edit'"
          :size="size"
          type="text"
          icon="el-icon-edit-outline"
          @click.stop="handlerSave(scope.row)"
        >编辑</el-button>
        <el-button
          v-throttle
          v-permission="'${permission}:del'"
          :size="size"
          type="text"
          icon="el-icon-delete"
          style="color: red"
          @click.stop="handlerDel(funs['del'], scope.row)"
        >删除</el-button>
      </template>
    </common-table>
    <!-- 新增or编辑组件 -->
    <add-or-update ref="addOrUpdate" <#if hasDict>:dict="{ <#list columns as column><#if column.dictDomainCode??>${column.javaName}List, </#if></#list> }"</#if> @ok="getList" />
    <!-- 全局导入组件 -->
    <to-channel ref="toChannel" title="${remarks!}导入" @change="getList" />
  </div>
</template>
<script>
  <#--import { edit } from '@/api<#if uiModuleName??>/${uiModuleName}</#if>/${jsName}' // 后台数据接口-->
  import { mixin } from '@/views/pages/mixin'
  import AddOrUpdate from './components/save'
  export default {
    name: '${className}',
    components: {
      AddOrUpdate
    },
    mixins: [mixin],
    data() {
      return {
        <#list columns as column>
        <#if column.dictDomainCode??>
        ${column.javaName}List: [], // ${column.columnRemarks!}数组
        </#if>
        </#list>
        // 表头数组
        columns: [
          {
            type: 'selection',
            width: '60'
          },
          {
            prop: 'number',
            label: '序号',
            width: '70',
            type: 'slot',
            slotType: 'serialNumber'
          },
          <#list columns as column>
          <#if column.listShow == 0>
          {
            prop: '${column.javaName}',
            label: '${column.columnRemarks!}',
            align: 'center'<#if column.dictDomainCode??>,</#if>
            <#if column.dictDomainCode??>
            type: 'slot',
            slotType: '${column.javaName}'
            </#if>
          },
          </#if>
          </#list>
          {
            type: 'slot',
            prop: 'operation',
            label: '操作',
            width: '168',
            slotType: 'operation',
            align: 'center',
            fixed: 'right'
          }
        ]
      }
    },
    async created() {
      <#if hasDict>
      // 通过全局方法取数据字典
      </#if>
      <#list columns as column>
      <#if column.dictDomainCode??>
      this.${column.javaName}List = await this.getDictInfo('${column.dictDomainCode}')
      </#if>
      </#list>
    },
    methods: {

    }
  }
</script>
