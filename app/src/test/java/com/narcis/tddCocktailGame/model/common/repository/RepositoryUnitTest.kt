package com.narcis.tddCocktailGame.model.common.repository

import android.content.SharedPreferences
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.eq
import org.mockito.kotlin.inOrder
import org.mockito.kotlin.mock
import org.mockito.kotlin.whenever

class RepositoryUnitTest {
    @Test
    fun `save score should save to sharedPreferences`(){
        val api: CocktailsApi = mock()
        val sharedPreferencesEditor: SharedPreferences.Editor = mock()
        val sharedPreferences: SharedPreferences = mock()
        whenever(sharedPreferences.edit()).thenReturn(sharedPreferencesEditor)

        val repository = CocktailRepositoryImpl(api, sharedPreferences)
        val score = 100
        repository.saveHighScore(score)

        inOrder(sharedPreferencesEditor) {
            verify(sharedPreferencesEditor).putInt(any(), eq(score))
            verify(sharedPreferencesEditor).apply()
        }
    }
}