package com.narcis.tddCocktailGame.game.factory

import com.narcis.tddCocktailGame.common.network.Cocktail
import com.narcis.tddCocktailGame.common.repository.CocktailsRepository
import com.narcis.tddCocktailGame.common.repository.RepositoryCallback
import com.narcis.tddCocktailGame.game.model.Game
import com.narcis.tddCocktailGame.game.model.Question
import com.narcis.tddCocktailGame.game.model.Score

class CocktailsGameFactoryImpl(private val repository: CocktailsRepository) :
    CocktailsGameFactory {
    override fun buildGame(callback: CocktailsGameFactory.Callback) {
        repository.getAlcoholic(
            object : RepositoryCallback<List<Cocktail>, String> {
                override fun onSuccess(t: List<Cocktail>) {
                    val questions = buildQuestions(t)
                    val score = Score(repository.getHighScore())
                    val game = Game(score, questions)
                    callback.onSuccess(game)
                }

                override fun onError(e: String) {
                    callback.onError()
                }
            },
        )
    }

    private fun buildQuestions(cocktailList: List<Cocktail>) =
        cocktailList.map { cocktail ->
            val otherCocktail = cocktailList.shuffled().first { it != cocktail }
            Question(cocktail.strDrink, otherCocktail.strDrink, cocktail.strDrinkThumb)
        }
}
