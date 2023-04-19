<template>
  <div class="app-container">
    <el-form
      v-loading.body="loading"
      :model="tmpAccount"
      :rules="updateDetailRules"
      ref="tmpAccount"
      label-width="115px"
    >
      <el-row :gutter="18">
        <el-col :span="9">
          <el-form-item label="계정 이름" prop="name">
            <el-input v-if="toUpdate" v-model="tmpAccount.name" />
            <span v-else>{{ tmpAccount.name }}</span>
          </el-form-item>
        </el-col>
        <el-col :span="9">
          <el-form-item label="사서함" prop="email">
            <el-input v-if="toUpdate" v-model="tmpAccount.email" />
            <span v-else>{{ tmpAccount.email }}</span>
          </el-form-item>
        </el-col>
      </el-row>
      <el-row :gutter="18">
        <el-col :span="9">
          <el-form-item label="등록 시간"><span>{{ unix2CurrentTime(account.registerTime) }}</span></el-form-item>
        </el-col>
        <el-col :span="9">
          <el-form-item label="마지막 로그인 시간"><span>{{ unix2CurrentTime(account.loginTime) }}</span></el-form-item>
        </el-col>
      </el-row>
      <el-form-item>
        <el-row :gutter="18">
          <el-col :span="6">
            <el-button type="success" :loading="btnLoading" @click.native.prevent="regainAccountDetail">정보 검색</el-button>
          </el-col>

          <el-col :span="6" v-if="!toUpdate">
            <el-button type="primary" :loading="btnLoading" @click.native.prevent="toUpdate = !toUpdate">정보 수정</el-button>
          </el-col>
          <el-col :span="6" v-else>
            <el-button type="primary" :loading="btnLoading" @click.native.prevent="updateDetail">변경 사항 확인</el-button>
            <el-button type="warning" @click.native.prevent="toUpdate = !toUpdate">수정 취소</el-button>
          </el-col>

          <el-col :span="6">
            <el-button type="danger" @click.native.prevent="showUpdatePasswordDialog">비밀번호 변경</el-button>
          </el-col>
        </el-row>
      </el-form-item>
    </el-form>

    <el-dialog title="비밀번호 변경" :visible.sync="dialogFormVisible">
      <el-form
        status-icon
        class="small-space"
        label-position="left"
        label-width="115px"
        style="width: 400px; margin-left:50px;"
        :model="tmpPassword"
        :rules="updatePasswordRules"
        ref="tmpPassword"
      >
        <el-form-item label="이전 비밀번호" prop="oldPassword" required>
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            placeholder="기존 비밀번호를 입력하세요."
            v-model="tmpPassword.oldPassword"
          />
        </el-form-item>
        <el-form-item label="새 비밀번호" prop="newPassword" required>
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            placeholder="새 비밀번호를 입력하세요."
            v-model="tmpPassword.newPassword"
          />
        </el-form-item>
        <el-form-item label="새 비밀번호" prop="newPassword2" required>
          <el-input
            type="password"
            prefix-icon="el-icon-edit"
            auto-complete="off"
            placeholder="새 비밀번호를 다시 입력하세요."
            v-model="tmpPassword.newPassword2"
          />
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click.native.prevent="dialogFormVisible = false">취소</el-button>
        <el-button type="danger" @click.native.prevent="$refs['tmpPassword'].resetFields()">초기화</el-button>
        <el-button type="primary" :loading="btnLoading" @click.native.prevent="updatePassword">업데이트</el-button>
      </div>
    </el-dialog>
  </div>
</template>

<script>
import store from '@/store'
import { update as updateAccount, validatePassword } from '@/api/account'
import { unix2CurrentTime } from '@/utils'
import { isValidateEmail } from '@/utils/validate'
import { setToken } from '@/utils/token'
import { mapState } from 'vuex'

export default {
  created() {
    this.setDetail()
  },
  data() {
    const validateOldPassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('비밀번호 길이는 6자 이상이어야 합니다.'))
      }
      // promise异步查询后端密码
      this.validateOldPassword(value).then(isValidate => {
        if (!isValidate) {
          callback(new Error('이전 비밀번호가 올바르지 않습니다.'))
        } else {
          callback()
        }
      })
    }
    const validateNewPassword = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('비밀번호 길이는 6자 이상이어야 합니다.'))
      } else if (this.isOldNewPasswordSame()) {
        callback(new Error('이전 비밀번호와 새 비밀번호는 동일할 수 없습니다.'))
      } else {
        callback()
      }
    }
    const validateNewPassword2 = (rule, value, callback) => {
      if (value.length < 6) {
        callback(new Error('비밀번호 길이는 6자 이상이어야 합니다.'))
      } else if (!this.isNewPasswordSame()) {
        callback(new Error('두 번 입력한 비밀번호가 일치하지 않습니다.'))
      } else {
        callback()
      }
    }
    const validateName = (rule, value, callback) => {
      if (value.length < 3) {
        callback(new Error('계정 이름은 3자 이상이어야 합니다.'))
      } else {
        callback()
      }
    }
    const validateEmail = (rule, value, callback) => {
      if (!isValidateEmail(value)) {
        callback(new Error('잘못된 이메일 형식'))
      } else {
        callback()
      }
    }
    return {
      loading: false,
      btnLoading: false,
      dialogFormVisible: false,
      toUpdate: false,
      tmpPassword: {
        oldPassword: '',
        newPassword: '',
        newPassword2: ''
      },
      updatePasswordRules: {
        oldPassword: [
          { required: true, trigger: 'blur', validator: validateOldPassword }
        ],
        newPassword: [
          { required: true, trigger: 'blur', validator: validateNewPassword }
        ],
        newPassword2: [
          { required: true, trigger: 'blur', validator: validateNewPassword2 }
        ]
      },
      tmpAccount: {
        id: '',
        name: '',
        email: ''
      },
      updateDetailRules: {
        name: [{ required: true, trigger: 'blur', validator: validateName }],
        email: [{ required: true, trigger: 'blur', validator: validateEmail }]
      }
    }
  },
  computed: {
    ...mapState({
      account: state => state.account
    })
  },
  methods: {
    unix2CurrentTime,
    /**
     * 사용자 프로필 설정
     */
    setDetail() {
      this.tmpAccount.name = this.account.name
      this.tmpAccount.email = this.account.email
    },
    /**
     * 이전 비밀번호 확인
     * @param oldPassword 이전 비밀번호
     */
    validateOldPassword(oldPassword) {
      const account = {
        id: this.account.accountId,
        password: oldPassword
      }
      return validatePassword(account).then(response => response.data)
    },
    /**
     * 이전 비밀번호와 새 비밀번호가 같은가요?
     */
    isOldNewPasswordSame() {
      return this.tmpPassword.oldPassword === this.tmpPassword.newPassword
    },
    /**
     * 새 비밀번호 1과 2가 같은가요?
     */
    isNewPasswordSame() {
      return this.tmpPassword.newPassword === this.tmpPassword.newPassword2
    },
    /**
     * 토큰 재설정
     */
    resetToken(token) {
      setToken(token)
      this.account.token = token
    },
    /**
     * 사용자 정보 검색
     */
    regainAccountDetail() {
      this.loading = true
      this.btnLoading = true
      store.dispatch('Detail').then(() => {
        this.loading = false
        this.btnLoading = false
      })
    },
    /**
     * 사용자 업데이트
     * @param account 사용자
     */
    updateAccount(account) {
      this.btnLoading = true
      updateAccount(account).then(response => {
        this.$message.success('업데이트 성공')
        this.resetToken(response.data)
        this.regainAccountDetail()
        this.btnLoading = false
      }).catch(res => {
        this.$message.error('업데이트 실패')
      })
    },
    /**
     * 사용자 정보 업데이트
     */
    updateDetail() {
      this.$refs.tmpAccount.validate(valid => {
        if (valid) {
          this.tmpAccount.id = this.account.accountId
          this.updateAccount(this.tmpAccount)
        }
      })
    },
    /**
     * 비밀번호 업데이트 대화 상자 표시
     */
    showUpdatePasswordDialog() {
      this.dialogFormVisible = true
      this.tmpPassword.oldPassword = ''
      this.tmpPassword.newPassword = ''
      this.tmpPassword.newPassword2 = ''
    },
    /**
     * 비밀번호 업데이트
     */
    updatePassword() {
      this.$refs.tmpPassword.validate(valid => {
        if (valid) {
          const account = {}
          account.id = this.account.accountId
          account.password = this.tmpPassword.newPassword
          this.updateAccount(account)
          this.dialogFormVisible = false
        }
      })
    }
  }
}
</script>
