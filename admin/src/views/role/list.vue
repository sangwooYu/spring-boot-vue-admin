<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item>
          <el-button
            type="success"
            size="mini"
            icon="el-icon-refresh"
            v-if="hasPermission('role:list')"
            @click.native.prevent="getRoleList"
          >새로 고침</el-button>
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-plus"
            v-if="hasPermission('role:add')"
            @click.native.prevent="showAddRoleDialog"
          >역할 추가하기</el-button>
        </el-form-item>
      </el-form>
    </div>
    <el-table
      :data="roleList"
      v-loading.body="listLoading"
      element-loading-text="loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="#" align="center" width="80">
        <template slot-scope="scope">
          <span v-text="getTableIndex(scope.$index)"></span>
        </template>
      </el-table-column>
      <el-table-column label="캐릭터 이름" align="center" prop="name" />
      <el-table-column label="생성 시간" align="center" prop="createTime">
        <template slot-scope="scope">{{ unix2CurrentTime(scope.row.createTime) }}</template>
      </el-table-column>
      <el-table-column label="시간 수정" align="center" prop="updateTime">
        <template slot-scope="scope">{{ unix2CurrentTime(scope.row.updateTime) }}</template>
      </el-table-column>
      <el-table-column
        label="관리"
        align="center"
        v-if="hasPermission('role:detail') || hasPermission('role:update') || hasPermission('role:delete')"
      >
        <template slot-scope="scope">
          <el-button
            type="info"
            size="mini"
            v-if="hasPermission('role:detail')"
            @click.native.prevent="showRoleDialog(scope.$index)"
          >보기</el-button>
          <el-button
            type="warning"
            size="mini"
            v-if="hasPermission('role:update') && scope.row.name !== '슈퍼 관리자'"
            @click.native.prevent="showUpdateRoleDialog(scope.$index)"
          >수정 사항</el-button>
          <el-button
            type="danger"
            size="mini"
            v-if="hasPermission('role:delete') && scope.row.name !== '슈퍼 관리자'"
            @click.native.prevent="removeRole(scope.$index)"
          >삭제</el-button>
        </template>
      </el-table-column>
    </el-table>
    <el-pagination
      @size-change="handleSizeChange"
      @current-change="handleCurrentChange"
      :current-page="listQuery.page"
      :page-size="listQuery.size"
      :total="total"
      :page-sizes="[9, 18, 36, 72]"
      layout="total, sizes, prev, pager, next, jumper"
    ></el-pagination>

    <el-dialog :title="textMap[dialogStatus]" :visible.sync="dialogFormVisible">
      <el-form
        status-icon
        class="small-space"
        label-position="left"
        label-width="100px"
        style="width: 500px; margin-left:50px;"
        :model="tempRole"
        :rules="createRules"
        ref="tempRole"
      >
        <el-form-item label="캐릭터 이름" prop="name" required>
          <el-input
            :disabled="dialogStatus === 'show'"
            type="text"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            v-model="tempRole.name"
          ></el-input>
        </el-form-item>
        <el-form-item label="권한" required>
          <div v-for="(permission, index) in permissionList" :key="index">
            <el-button
              :disabled="dialogStatus === 'show'"
              size="mini"
              :type="isMenuNone(index) ? '' : (isMenuAll(index) ? 'success' : 'primary')"
              @click.native.prevent="checkAll(index)"
            >{{ permission.resource }}</el-button>
            <!-- https://element.eleme.cn/#/zh-CN/component/checkbox#indeterminate-zhuang-tai -->
            <el-checkbox-group v-model="tempRole.permissionIdList">
              <el-checkbox
                :disabled="dialogStatus === 'show'"
                v-for="item in permission.handleList"
                :key="item.id"
                :label="item.id"
                @change="handleChecked(item, _index)"
              >
                <span>{{ item.handle }}</span>
              </el-checkbox>
            </el-checkbox-group>
          </div>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native.prevent="dialogFormVisible = false">취소</el-button>
        <el-button
          v-if="dialogStatus === 'add'"
          type="success"
          :loading="btnLoading"
          @click.native.prevent="addRole"
        >추가</el-button>
        <el-button
          v-if="dialogStatus === 'update'"
          type="primary"
          :loading="btnLoading"
          @click.native.prevent="updateRole"
        >업데이트</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import {
  listRoleWithPermission,
  listResourcePermission,
  add as addRole,
  update as updateRole,
  remove
} from '@/api/role'
import { unix2CurrentTime } from '@/utils'
import { mapGetters } from 'vuex'

export default {
  created() {
    if (this.hasPermission('role:update')) {
      this.getPermissionList()
    }
    if (this.hasPermission('role:list')) {
      this.getRoleList()
    }
  },
  data() {
    /**
     * 역할 이름 확인
     * @param rule 규칙
     * @param value 캐릭터 이름
     * @param callback 콜백
     */
    const validateRoleName = (rule, value, callback) => {
      if (value === '') {
        callback(new Error('캐릭터 이름은 비워둘 수 없습니다.'))
      } else {
        callback()
      }
    }
    return {
      roleList: [],
      permissionList: [],
      listLoading: false,
      total: 0,
      listQuery: {
        page: 1,
        size: 9
      },
      dialogStatus: 'add',
      dialogFormVisible: false,
      textMap: {
        update: '캐릭터 수정',
        add: '캐릭터 수정'
      },
      btnLoading: false,
      tempRole: {
        id: '',
        name: '',
        permissionIdList: []
      },
      createRules: {
        name: [{ required: true, trigger: 'blur', validator: validateRoleName }]
      }
    }
  },
  computed: {
    ...mapGetters(['roleName'])
  },
  methods: {
    unix2CurrentTime,
    /**
     * 모든 역할에 액세스하기
     */
    getPermissionList() {
      listResourcePermission().then(response => {
        this.permissionList = response.data.list
      }).catch(res => {
        this.$message.error('권한 목록을 로드하지 못했습니다.')
      })
    },
    /**
     * 문자 목록 가져오기
     */
    getRoleList() {
      this.listLoading = true
      listRoleWithPermission(this.listQuery).then(response => {
        this.roleList = response.data.list
        this.total = response.data.total
        this.listLoading = false
      }).catch(res => {
        this.$message.error('역할 목록을 로드하지 못했습니다.')
      })
    },
    /**
     * 페이지당 페이지 수 변경
     * @param size 페이지 크기
     */
    handleSizeChange(size) {
      this.listQuery.page = 1
      this.listQuery.size = size
      this.getRoleList()
    },
    /**
     * 페이지 번호 변경
     * @param page 페이지 번호
     */
    handleCurrentChange(page) {
      this.listQuery.page = page
      this.getRoleList()
    },
    /**
     * 양식 일련 번호
     * @param index 데이터 구독
     * @returns 양식 일련 번호
     */
    getTableIndex(index) {
      return (this.listQuery.page - 1) * this.listQuery.size + index + 1
    },
    /**
     * 새 역할 표시 대화 상자
     */
    showAddRoleDialog() {
      this.dialogFormVisible = true
      this.dialogStatus = 'add'
      this.tempRole.name = ''
      this.tempRole.id = ''
      this.tempRole.permissionIdList = []
    },
    /**
     * 역할 업데이트를 위한 대화 상자 표시
     * @param index 문자 아래 첨자
     */
    showUpdateRoleDialog(index) {
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
      const role = this.roleList[index]
      this.tempRole.name = role.name
      this.tempRole.id = role.id
      this.tempRole.permissionIdList = []
      for (let i = 0; i < role.resourceList.length; i++) {
        const handleList = role.resourceList[i].handleList
        for (let j = 0; j < handleList.length; j++) {
          const handle = handleList[j]
          this.tempRole.permissionIdList.push(handle.id)
        }
      }
    },
    /**
     * 역할 권한을 표시하는 대화 상자
     * @param index 문자 아래 첨자
     */
    showRoleDialog(index) {
      this.dialogFormVisible = true
      this.dialogStatus = 'show'
      const role = this.roleList[index]
      this.tempRole.name = role.name
      this.tempRole.id = role.id
      this.tempRole.permissionIdList = []
      let resourceList = []
      if (role.name === '슈퍼 관리자') {
        resourceList = this.permissionList
      } else {
        resourceList = role.resourceList
      }
      for (let i = 0; i < resourceList.length; i++) {
        const handleList = resourceList[i].handleList
        for (let j = 0; j < handleList.length; j++) {
          const handle = handleList[j]
          this.tempRole.permissionIdList.push(handle.id)
        }
      }
    },
    /**
     * 새 캐릭터 추가
     */
    addRole() {
      this.$refs.tempRole.validate(valid => {
        if (
          valid &&
          this.isRoleNameUnique(this.tempRole.id, this.tempRole.name)
        ) {
          this.btnLoading = true
          addRole(this.tempRole).then(() => {
            this.$message.success('추가 성공')
            this.getRoleList()
            this.dialogFormVisible = false
            this.btnLoading = false
          }).catch(res => {
            this.$message.error('문자 추가에 실패했습니다.')
          })
        } else {
          console.log('양식 유효하지 않음')
        }
      })
    },
    /**
     * 캐릭터 수정
     */
    updateRole() {
      this.$refs.tempRole.validate(valid => {
        if (
          valid &&
          this.isRoleNameUnique(this.tempRole.id, this.tempRole.name)
        ) {
          this.btnLoading = true
          updateRole(this.tempRole).then(() => {
            this.$message.success('업데이트 성공')
            this.getRoleList()
            this.dialogFormVisible = false
            this.btnLoading = false
          }).catch(res => {
            this.$message.error('캐릭터 업데이트 실패')
          })
        } else {
          console.log('양식 유효하지 않음')
        }
      })
    },
    /**
     * 역할 이름이 이미 존재하는지 확인
     * @param id 캐릭터 ID
     * @param name 캐릭터 이름
     * @returns {boolean}
     */
    isRoleNameUnique(id, name) {
      for (let i = 0; i < this.roleList.length; i++) {
        if (this.roleList[i].id !== id && this.roleList[i].name === name) {
          this.$message.error('캐릭터 이름이 이미 존재합니다.')
          return false
        }
      }
      return true
    },
    /**
     * 문자 제거
     * @param index 문자 아래 첨자
     * @returns {boolean}
     */
    removeRole(index) {
      this.$confirm('역할을 삭제하시겠습니까?', '경고', {
        confirmButtonText: '예',
        cancelButtonText: '아니오',
        type: 'warning'
      }).then(() => {
        const roleId = this.roleList[index].id
        remove(roleId).then(() => {
          this.$message.success('성공적으로 삭제됨')
          this.getRoleList()
        }).catch(() => {
          this.$message.error('삭제하지 못함')
        })
      }).catch(() => {
        this.$message.info('삭제됨')
      })
    },
    /**
     * 역할 메뉴에서 선택된 권한이 없는지 확인합니다.
     * @param index 구독
     * @returns {boolean}
     */
    isMenuNone(index) {
      const handleList = this.permissionList[index].handleList
      for (let i = 0; i < handleList.length; i++) {
        if (this.tempRole.permissionIdList.indexOf(handleList[i].id) > -1) {
          return false
        }
      }
      return true
    },
    /**
     * 역할 메뉴의 모든 권한이 선택되어 있는지 확인합니다.
     * @param index 구독
     * @returns {boolean}
     */
    isMenuAll(index) {
      const handleList = this.permissionList[index].handleList
      for (let i = 0; i < handleList.length; i++) {
        if (this.tempRole.permissionIdList.indexOf(handleList[i].id) < 0) {
          return false
        }
      }
      return true
    },
    /**
     * 메뉴 상태에 따라 모든 확인란을 선택합니다.
     * @param index 구독
     */
    checkAll(index) {
      if (this.isMenuAll(index)) {
        // 모두 선택한 경우 모두 취소합니다.
        this.cancelAll(index)
      } else {
        // 아직 선택하지 않은 경우 모두 선택
        this.selectAll(index)
      }
    },
    /**
     * checkbox모두 선택
     * @param index 구독
     */
    selectAll(index) {
      const handleList = this.permissionList[index].handleList
      for (let i = 0; i < handleList.length; i++) {
        this.addUnique(handleList[i].id, this.tempRole.permissionIdList)
      }
    },
    /**
     * checkbox모두선택
     * @param index 구독
     */
    cancelAll(index) {
      const handleList = this.permissionList[index].handleList
      for (let i = 0; i < handleList.length; i++) {
        const idIndex = this.tempRole.permissionIdList.indexOf(handleList[i].id)
        if (idIndex > -1) {
          this.tempRole.permissionIdList.splice(idIndex, 1)
        }
      }
    },
    /**
     * 이 메서드는 틱 상태가 변경된 후 트리거됩니다.
     * @param item 옵션
     * @param index 해당 구독
     */
    handleChecked(item, index) {
      if (this.tempRole.permissionIdList.indexOf(item.id) > -1) {
        // 선택한 이벤트
        // 이 권한이 이전에 확인되지 않은 경우, 확인 시 이 ID가 임시 역할에 포함됩니다.
        // 그런 다음 필수 권한을 선택합니다.
        this.makePermissionChecked(index)
      } else {
        // 이벤트 선택 취소
        this.cancelAll(index)
      }
    },
    /**
     * 역할 메뉴의 필수 권한을 선택합니다(여기에는 필수 데이터베이스 필드가 없습니다).
     * @param index 권한은 구독에 해당합니다.
     */
    makePermissionChecked(index) {
      const handleList = this.permissionList[index].handleList
      for (let i = 0; i < handleList.length; i++) {
        this.addUnique(handleList[i].id, this.tempRole.permissionIdList)
      }
    },
    /**
     * 배열에 반복 방지 요소 추가하기
     * @param val 가치
     * @param arr 배열
     */
    addUnique(val, arr) {
      const _index = arr.indexOf(val)
      if (_index < 0) {
        arr.push(val)
      }
    }
  }
}
</script>
