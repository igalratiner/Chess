package service

import client.AccountsClient
import com.google.inject.Inject
import dao.TextAccountRoleDao
import dao.TextRoleProvisionDao
import dao.TextsDao
import io.ktor.features.NotFoundException
import io.ktor.util.KtorExperimentalAPI
import mu.KLogging
import pojo.TextDetails
import pojo.TextRole

class TextsService @Inject constructor(private val textsDao: TextsDao,
                                       private val textAccountRoleDao: TextAccountRoleDao,
                                       private val textRoleProvisionDao: TextRoleProvisionDao) {
    companion object : KLogging()

    private val accountsClient = AccountsClient()

    fun createText(textName: String, accountId: Int): TextDetails {
        val textDetails = textsDao.createText(textName)
        textAccountRoleDao.createTextToAccount(textDetails.hash, accountId)
        return textDetails
    }

    fun getTextDetails(textHash: String): TextDetails {
        return textsDao.getText(textHash)!!
    }

    fun updateText() {

    }

    fun deleteText(textHash: String) {
        textRoleProvisionDao.deleteTextProvisions(textHash)
        textAccountRoleDao.deleteTextPermissions(textHash)
        textsDao.deleteText(textHash)
    }

    fun shareTextWithAccount(textHash: String, role: TextRole, usernameToShareWith: String) {
        val accountToShareWith = accountsClient.getAccount(usernameToShareWith)
        textAccountRoleDao.updateTextToAccount(textHash, role, accountToShareWith.id)
        shareText(textHash, role)
    }

    fun shareText(textHash: String, role: TextRole): String {
        if (role == TextRole.OWNER) {
            throw RuntimeException("Only single owner for text")
        }
        return textRoleProvisionDao.addProvisionToText(textHash, role)
    }

    fun getTexts(accountId: Int): List<TextDetails> {
        return textAccountRoleDao.getTexts(accountId).mapNotNull { textsDao.getText(it) }
    }

    @KtorExperimentalAPI
    fun getTextAuthorization(accountId: Int, textHash: String): String {
        val accountRole = textAccountRoleDao.getTextRole(accountId, textHash) ?: throw NotFoundException()
        val textDetails = textsDao.getText(textHash) ?: throw NotFoundException()
        return TextJwtConfig.makeToken(textDetails, accountRole)
    }

    @KtorExperimentalAPI
    fun getTextAuthorization(textProvision: String): String {
        val textAccess = textRoleProvisionDao.getTextAccess(textProvision) ?: throw NotFoundException()
        val textDetails = textsDao.getText(textAccess.textHash) ?: throw NotFoundException()
        return TextJwtConfig.makeToken(textDetails, textAccess.textRole)
    }
}