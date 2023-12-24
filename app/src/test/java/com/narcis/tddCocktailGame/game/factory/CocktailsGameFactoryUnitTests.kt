package com.narcis.tddCocktailGame.game.factory

import com.narcis.tddCocktailGame.common.network.Cocktail
import com.narcis.tddCocktailGame.common.repository.CocktailsRepository
import com.narcis.tddCocktailGame.common.repository.RepositoryCallback
import com.narcis.tddCocktailGame.game.model.Game
import com.narcis.tddCocktailGame.game.model.Question
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.any
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify
import org.mockito.kotlin.whenever

class CocktailsGameFactoryUnitTests {
    private lateinit var repository: CocktailsRepository
    private lateinit var factory: CocktailsGameFactory

    private val cocktails = listOf(
        Cocktail("1", "Drink1", "image1"),
        Cocktail("2", "Drink2", "image2"),
        Cocktail("3", "Drink3", "image3"),
        Cocktail("4", "Drink4", "image4"),
    )

    @Before
    fun setUp() {
        repository = mock()
        factory = CocktailsGameFactoryImpl(repository)
    }

    @Test
    fun `build game should get cocktails from repo`() {
        factory.buildGame(mock())
        verify(repository).getAlcoholic(any())
    }

    @Test
    fun `buildGame should call onSuccess`() {
        val callback = mock<CocktailsGameFactory.Callback>()
        setUpRepositoryWithCocktails(repository)
        factory.buildGame(callback)
        verify(callback).onSuccess(any())
    }

    @Test
    fun `buildGame should call onError`() {
        val callback = mock<CocktailsGameFactory.Callback>()
        setUpRepositoryWithError(repository)
        factory.buildGame(callback)
        verify(callback).onError()
    }

    @Test
    fun `buildGame should get highScore from repo`() {
        setUpRepositoryWithCocktails(repository)
        factory.buildGame(mock())
        verify(repository).getHighScore()
    }

    @Test
    fun `buildGame should build game with highScore`() {
        setUpRepositoryWithCocktails(repository)
        val highScore = 100
        whenever(repository.getHighScore()).thenReturn(highScore)
        factory.buildGame(object : CocktailsGameFactory.Callback {
            override fun onSuccess(game: Game) {
                return assertEquals(highScore, game.score.highest)
            }

            override fun onError() {
                return fail()
            }
        })
    }

    @Test
    fun `buildGame should build game with questions`(){
        setUpRepositoryWithCocktails(repository)

        factory.buildGame(object : CocktailsGameFactory.Callback{
            override fun onSuccess(game: Game) {
                cocktails.forEach {
                    assertQuestion(game.nextQuestion(),
                        it.idDrink,
                        it.strDrinkThumb
                        )
                }
            }

            override fun onError() {
                return fail()
            }

        })
    }
    private fun setUpRepositoryWithCocktails(
        repository: CocktailsRepository,
    ) {
        doAnswer {
            val callback: RepositoryCallback<List<Cocktail>, String> = it.getArgument(0)
            callback.onSuccess(cocktails)
        }.whenever(repository).getAlcoholic(any())
    }
    private fun setUpRepositoryWithError(
        repository: CocktailsRepository,
    ) {
        doAnswer {
            val callback: RepositoryCallback<List<Cocktail>, String> = it.getArgument(0)
            callback.onError("Error")
        }.whenever(repository).getAlcoholic(any())
    }
    private fun assertQuestion(question: Question?, correctOption: String, imageUrl: String?) {
        assertNotNull(question)
        assertEquals(imageUrl, question?.imageUrl)
        assertEquals(correctOption, question?.correctOption)
        assertNotEquals(correctOption, question?.incorrectOption)
    }
}
