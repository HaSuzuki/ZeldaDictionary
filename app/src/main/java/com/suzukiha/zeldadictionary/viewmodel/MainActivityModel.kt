package com.suzukiha.zeldadictionary.viewmodel

import android.app.Application
import androidx.annotation.StringDef
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import com.google.mlkit.common.model.DownloadConditions
import com.google.mlkit.common.model.RemoteModelManager
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.TranslateRemoteModel
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions


class MainActivityModel(
    application: Application
) : AndroidViewModel(application) {

    private val _hasLanguageModel = MutableLiveData<Boolean>(false)
    val hasLanguageModel = _hasLanguageModel

    private val _availableDownloadedModel = MutableLiveData<Boolean>(false)
    val availableDownloadedModel = _availableDownloadedModel

    private var isDownloadModelIfNeeded = false
    private var isAlreadyModelDownloaded = false
    var isShowToast = false

    fun downloadTranslateLanguageModelIfNeeded(@Language language: String) {
        if (isDownloadModelIfNeeded) {
            return
        }
        isDownloadModelIfNeeded = true
        val options = TranslatorOptions.Builder()
            .setSourceLanguage(TranslateLanguage.ENGLISH)
            .setTargetLanguage(language)
            .build()
        Translation.getClient(options).downloadModelIfNeeded(DownloadConditions.Builder().build())
            .addOnSuccessListener {
                isDownloadModelIfNeeded = false
                if (_availableDownloadedModel.value == false) {
                    _availableDownloadedModel.postValue(true)
                }
            }
            .addOnFailureListener { _ ->
                isDownloadModelIfNeeded = false
                _availableDownloadedModel.postValue(false)
            }
    }

    fun checkAlreadyTranslateLanguageModel(@Language language: String) {
        if (isAlreadyModelDownloaded) {
            return
        }
        isAlreadyModelDownloaded = true
        val modelManager = RemoteModelManager.getInstance()
        val downloadedModel = TranslateRemoteModel.Builder(language).build()
        modelManager.isModelDownloaded(downloadedModel)
            .addOnSuccessListener {
                isAlreadyModelDownloaded = false
                if (!it) {
                    isShowToast = true
                }
                _hasLanguageModel.postValue(it)
            }
    }

    @Language
    fun selectDownloadLanguage(language: String): String {
        return when (language) {
            ENGLISH -> ENGLISH
            JAPANESE -> JAPANESE
            CHINESE -> CHINESE
            SPANISH -> SPANISH
            else -> ENGLISH
        }
    }

    companion object {
        @Retention(AnnotationRetention.SOURCE)
        @StringDef(ENGLISH, JAPANESE, CHINESE, SPANISH)
        annotation class Language

        const val ENGLISH = TranslateLanguage.ENGLISH
        const val JAPANESE = TranslateLanguage.JAPANESE
        const val CHINESE = TranslateLanguage.CHINESE
        const val SPANISH = TranslateLanguage.SPANISH
    }
}