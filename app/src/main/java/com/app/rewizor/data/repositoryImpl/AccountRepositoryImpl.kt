package com.app.rewizor.data.repositoryImpl

import com.app.rewizor.data.RewizorResult
import com.app.rewizor.data.model.Account
import com.app.rewizor.data.model.Region
import com.app.rewizor.data.repository.AccountRepository
import com.app.rewizor.preferences.PreferencesCache
import com.app.rewizor.remote.RestClient

class AccountRepositoryImpl(
    private val prefs: PreferencesCache,
    private val apiClient: RestClient
) : AccountRepository {

    override val isAuthorized: Boolean
        get() = prefs.sessionToken.let {
            it != ANON_TOKEN && it != null
        }

    override var account: Account = Account.DEFAULT
        set(value) {
            updateAccountSettings(value)
            field = value
        }

    override var systemRegions: List<Region> = listOf()

    private fun updateAccountSettings(account: Account) {
        account.region?.id?.let { prefs.savedRegionId = it }
        prefs.sessionToken = account.accessToken
    }


    override suspend fun getAccount(): RewizorResult<Account> {
        val accountResult = apiClient.run { callApi { api.getProfile() } }
        return accountResult.map().also {
            if (!it.isError) account = it.model!!
        }
    }

    override suspend fun updateAccount(account: Account): RewizorResult<Account> {
        val updateResult = apiClient.run {
            callApi {
                api.updateProfile(
                    account.lastName ?: "",
                    account.firstName ?: "",
                    account.middleName ?: "",
                    account.email,
                    account.phone,
                    account.region?.id
                )
            }
        }
        return updateResult.map()
            .also { if (!it.isError) this.account = it.model ?: Account.DEFAULT }

    }

    override suspend fun updateCity(region: Region): RewizorResult<Account> {
        val accountResult: RewizorResult<Account>
        if (!isAuthorized) {
            prefs.savedRegionId = region.id
            accountResult = RewizorResult(account)
        } else {
            val updateResult = apiClient.run {
                callApi {
                    api.updateProfile(
                        account.lastName ?: "",
                        account.firstName ?: "",
                        account.middleName ?: "",
                        account.email,
                        account.phone,
                        region.id
                    )
                }
            }
            accountResult = updateResult.map(Account.DEFAULT)
            if (!updateResult.isError) this.account = accountResult.model ?: Account.DEFAULT
        }
        return accountResult
    }

    override val region: Region
        get() =
            if (!isAuthorized) {
                systemRegions.first { it.id == prefs.savedRegionId }
            } else {
                account.region ?: Region.DEFAULT
            }

    override fun logout() {
        account = Account.DEFAULT
        prefs.sessionToken = null
    }

    companion object {
        const val ANON_TOKEN = "a0e6f6497e2c492dbd09e119a7340bd3"
    }
}