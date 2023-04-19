<template>
  <div class="app-container">
    <div class="filter-container">
      <el-form :inline="true">
        <el-form-item>
          <el-button
            type="success"
            size="mini"
            icon="el-icon-refresh"
            v-if="hasPermission('account:list')"
            @click.native.prevent="getAccountList"
          >새로 고침</el-button>
          <el-button
            type="primary"
            size="mini"
            icon="el-icon-plus"
            v-if="hasPermission('account:add')"
            @click.native.prevent="showAddAccountDialog"
          >계정 추가</el-button>
        </el-form-item>

        <span v-if="hasPermission('account:search')">
          <el-form-item>
            <el-input v-model="search.accountName" placeholder="계정 이름"></el-input>
          </el-form-item>
          <el-form-item>
            <el-select v-model="search.roleName" placeholder="역할">
              <el-option label="선택하세요" value />
              <div v-for="(role, index) in roleList" :key="index">
                <el-option :label="role.name" :value="role.name"/>
              </div>
            </el-select>
          </el-form-item>
          <el-form-item>
            <el-button type="primary" @click="searchBy" :loading="btnLoading">문의</el-button>
          </el-form-item>
        </span>
      </el-form>
    </div>
    <el-table
      :data="accountList"
      v-loading.body="listLoading"
      element-loading-text="loading"
      border
      fit
      highlight-current-row
    >
      <el-table-column label="#" align="center" width="80">
        <template slot-scope="scope">
          <span v-text="getIndex(scope.$index)"></span>
        </template>
      </el-table-column>
      <el-table-column label="계정 이름" align="center" prop="name" width="180" />
      <el-table-column label="사서함" align="center" prop="email" width="200" />
      <el-table-column label="등록 시간" align="center" prop="registerTime" width="160">
        <template slot-scope="scope">{{ unix2CurrentTime(scope.row.registerTime) }}</template>
      </el-table-column>
      <el-table-column label="마지막 로그인 시간" align="center" prop="loginTime" width="160">
        <template slot-scope="scope">{{ scope.row.loginTime ? unix2CurrentTime(scope.row.loginTime) : '로그인하지 않음' }}</template>
      </el-table-column>
      <el-table-column label="역할" align="center" prop="roleName" width="120" />
      <el-table-column label="관리" align="center"
        v-if="hasPermission('role:update') || hasPermission('account:update') || hasPermission('account:delete')">
        <template slot-scope="scope">
          <el-button
            type="warning"
            size="mini"
            v-if="hasPermission('role:update') && scope.row.id !== accountId"
            @click.native.prevent="showUpdateAccountDialog(scope.$index)"
          >계정</el-button>
          <el-button
            type="warning"
            size="mini"
            v-if="hasPermission('account:update') && scope.row.id !== accountId"
            @click.native.prevent="showUpdateAccountRoleDialog(scope.$index)"
          >역할</el-button>
          <el-button
            type="danger"
            size="mini"
            v-if="hasPermission('account:delete') && scope.row.id !== accountId"
            @click.native.prevent="removeAccount(scope.$index)"
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
        label-width="75px"
        style="width: 300px; margin-left:50px;"
        :model="tmpAccount"
        :rules="createRules"
        ref="tmpAccount"
      >
        <el-form-item label="계정 이름" prop="name" required>
          <el-input
            type="text"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            :disabled="dialogStatus === 'updateRole'"
            v-model="tmpAccount.name"
          />
        </el-form-item>
        <el-form-item label="사서함" prop="email">
          <el-input
            type="text"
            prefix-icon="el-icon-message"
            auto-complete="off"
            :disabled="dialogStatus === 'updateRole'"
            v-model="tmpAccount.email"
          />
        </el-form-item>
        <el-form-item label="비밀번호" prop="password" required
        v-if="dialogStatus !== 'updateRole'">
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            v-model="tmpAccount.password"
            v-if="dialogStatus !== 'updateRole'"
          />
        </el-form-item>
        <el-form-item label="역할"
          v-if="dialogStatus === 'updateRole'">
          <el-select placeholder="선택하세요" v-model="tmpAccount.roleId">
            <el-option v-for="item in roleList" :key="item.id" :label="item.name" :value="item.id" />
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native.prevent="dialogFormVisible = false">취소</el-button>
        <el-button
          type="danger"
          v-if="dialogStatus === 'add'"
          @click.native.prevent="$refs['tmpAccount'].resetFields()"
        >초기화</el-button>
        <el-button
          type="success"
          v-if="dialogStatus === 'add'"
          :loading="btnLoading"
          @click.native.prevent="addAccount"
        >추가</el-button>
        <el-button
          type="primary"
          v-if="dialogStatus === 'update'"
          :loading="btnLoading"
          @click.native.prevent="updateAccount"
        >정보 업데이트</el-button>
        <el-button
          type="primary"
          v-if="dialogStatus === 'updateRole'"
          :loading="btnLoading"
          @click.native.prevent="updateAccountRole"
        >역할 업데이트</el-button>
      </div>
    </el-dialog>
  </div>
</template>
<script>
import { list as getAccountList, search, register, remove, update as updateAccount } from '@/api/account'
import { list as getRoleList, updateAccountRole } from '@/api/role'
import { isValidateEmail } from '@/utils/validate'
import { unix2CurrentTime } from '@/utils'
import { mapGetters } from 'vuex'

export default {
  created() {
    if (this.hasPermission('role:update')) {
      this.getRoleList()
    }
    if (this.hasPermission('account:list')) {
      this.getAccountList()
    }
  },
  data() {
    const validateEmail = (rule, value, callback) => {
      if (!isValidateEmail(value)) {
        callback(new Error('잘못된 이메일 형식'))
      } else {
        callback()
      }
    }
    const validateName = (rule, value, callback) => {
      if (value.length < 3) {
        callback(new Error('계정 이름 길이는 3 이상이어야 합니다.'))
      } else {
        callback()
      }
    }
    const validatePassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('비밀번호 길이는 6 이상이어야 합니다.'))
      } else {
        callback()
      }
    }
    return {
      accountList: [], // 사용자 목록
      roleList: [], // 모든 역할
      filterRoleNameList: [], // 테이블 역할 필터링 목록 http://element-cn.eleme.io/#/zh-CN/component/table#shai-xuan
      listLoading: false, // 데이터 로딩 대기 애니메이션
      total: 0, // 총 데이터
      listQuery: {
        page: 1, // 페이지
        size: 9 // 페이지당 개수
      },
      dialogStatus: 'add',
      dialogFormVisible: false,
      textMap: {
        updateRole: '계정 역할 수정',
        update: '계정 번호 수정',
        add: '계정 추가'
      },
      btnLoading: false, // 버튼 대기 애니메이션
      tmpAccount: {
        accountId: '',
        email: '',
        name: '',
        password: '',
        roleId: 2 // 백엔드 데이터베이스 공통 사용자 역할 Id에 해당합니다.
      },
      search: {
        page: null,
        size: null,
        accountName: null,
        roleName: null
      },
      createRules: {
        email: [{ required: true, trigger: 'blur', validator: validateEmail }],
        name: [{ required: true, trigger: 'blur', validator: validateName }],
        password: [
          { required: true, trigger: 'blur', validator: validatePassword }
        ]
      }
    }
  },
  computed: {
    ...mapGetters(['accountId'])
  },
  methods: {
    unix2CurrentTime,
    /**
     * 모든 캐릭터 가져오기
     */
    getRoleList() {
      getRoleList().then(response => {
        this.roleList = response.data.list
      }).catch(res => {
        this.$message.error('역할을 로드하지 못했습니다.')
      })
    },
    /**
     * 사용자 목록 가져오기
     */
    getAccountList() {
      this.listLoading = true
      getAccountList(this.listQuery).then(response => {
        this.accountList = response.data.list
        this.total = response.data.total
        for (let i = 0; i < this.accountList.length; i++) {
          const filterObject = {}
          filterObject.text = this.accountList[i].roleName
          filterObject.value = this.accountList[i].roleName
          this.filterRoleNameList.push(filterObject)
        }
        this.listLoading = false
      }).catch(res => {
        this.$message.error('계정 목록을 로드하지 못했습니다.')
      })
    },
    searchBy() {
      this.btnLoading = true
      this.listLoading = true
      this.search.page = this.listQuery.page
      this.search.size = this.listQuery.size
      search(this.search).then(response => {
        this.accountList = response.data.list
        this.total = response.data.total
        this.listLoading = false
        this.btnLoading = false
      }).catch(res => {
        this.$message.error('검색 실패')
      })
    },
    /**
     * 페이지당 페이지 수 변경
     * @param size 페이지 크기
     */
    handleSizeChange(size) {
      this.listQuery.size = size
      this.listQuery.page = 1
      this.getAccountList()
    },
    /**
     * 페이지 번호 변경
     * @param page 페이지 번호
     */
    handleCurrentChange(page) {
      this.listQuery.page = page
      this.getAccountList()
    },
    /**
     * 양식 일련 번호
     * 사용자 지정 양식 일련번호 참조
     * http://element-cn.eleme.io/#/zh-CN/component/table#zi-ding-yi-suo-yin
     * @param index 데이터 구독
     * @returns 양식 일련 번호
     */
    getIndex(index) {
      return (this.listQuery.page - 1) * this.listQuery.size + index + 1
    },
    /**
     * 사용자 추가 대화 상자 표시
     */
    showAddAccountDialog() {
      // 새 대화 상자 표시
      this.dialogFormVisible = true
      this.dialogStatus = 'add'
      this.tmpAccount.email = ''
      this.tmpAccount.name = ''
      this.tmpAccount.password = ''
    },
    /**
     * 사용자 추가
     */
    addAccount() {
      this.$refs.tmpAccount.validate(valid => {
        if (valid && this.isUniqueDetail(this.tmpAccount)) {
          this.btnLoading = true
          register(this.tmpAccount).then(() => {
            this.$message.success('추가 성공')
            this.getAccountList()
            this.dialogFormVisible = false
            this.btnLoading = false
          }).catch(res => {
            this.$message.error('계정을 추가하지 못했습니다.')
            this.btnLoading = false
          })
        }
      })
    },
    /**
     * 사용자 수정 대화 상자 표시
     * @param index 사용자 구독
     */
    showUpdateAccountDialog(index) {
      this.dialogFormVisible = true
      this.dialogStatus = 'update'
      this.tmpAccount.accountId = this.accountList[index].id
      this.tmpAccount.email = this.accountList[index].email
      this.tmpAccount.name = this.accountList[index].name
      this.tmpAccount.password = ''
      this.tmpAccount.roleId = this.accountList[index].roleId
    },
    /**
     * 사용자 업데이트
     */
    updateAccount() {
      updateAccount(this.tmpAccount).then(() => {
        this.$message.success('업데이트 성공')
        this.getAccountList()
        this.dialogFormVisible = false
      }).catch(res => {
        this.$message.error('업데이트 실패')
      })
    },
    /**
     * 사용자 역할 수정 대화 상자 표시
     * @param index 사용자 구독
     */
    showUpdateAccountRoleDialog(index) {
      this.dialogFormVisible = true
      this.dialogStatus = 'updateRole'
      this.tmpAccount.accountId = this.accountList[index].id
      this.tmpAccount.email = this.accountList[index].email
      this.tmpAccount.name = this.accountList[index].name
      this.tmpAccount.password = ''
      this.tmpAccount.roleId = this.accountList[index].roleId
    },
    /**
     * 사용자 역할 업데이트
     */
    updateAccountRole() {
      updateAccountRole(this.tmpAccount).then(() => {
        this.$message.success('업데이트 성공')
        this.getAccountList()
        this.dialogFormVisible = false
      }).catch(res => {
        this.$message.error('업데이트 실패')
      })
    },
    /**
     * 사용자 프로필이 고유한 지 여부
     * @param account 사용자
     */
    isUniqueDetail(account) {
      for (let i = 0; i < this.accountList.length; i++) {
        if (this.accountList[i].name === account.name) {
          this.$message.error('계정 이름이 이미 존재합니다.')
          return false
        }
        if (this.accountList[i].email === account.email) {
          this.$message.error('사서함이 이미 존재합니다.')
          return false
        }
      }
      return true
    },
    /**
     * 사용자 삭제
     * @param index 사용자 구독
     */
    removeAccount(index) {
      this.$confirm('계정을 삭제하시겠습니까?', '경고', {
        confirmButtonText: '예',
        cancelButtonText: '아니오',
        type: 'warning'
      }).then(() => {
        const id = this.accountList[index].id
        remove(id).then(() => {
          this.$message.success('성공적으로 삭제됨')
          this.getAccountList()
        })
      }).catch(() => {
        this.$message.info('삭제됨')
      })
    }
  }
}
</script>
