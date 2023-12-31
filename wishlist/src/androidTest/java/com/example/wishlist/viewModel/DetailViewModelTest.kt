package com.example.wishlist.viewModel

import android.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.wishlist.dataPersistance.RepositoryImpl
import com.example.wishlist.dataPersistance.WishlistDao
import com.example.wishlist.dataPersistance.WishlistDaoImpl
import com.example.wishlist.dataPersistance.WishlistDatabase
import com.example.wishlist.model.Wishlist
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.runTest
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import org.mockito.kotlin.any
import org.mockito.kotlin.mock
import org.mockito.kotlin.verify

@RunWith(MockitoJUnitRunner::class)
class DetailViewModelTest {
    @get:Rule
    val taskExecutorRule = InstantTaskExecutorRule()

    private val wishlistDao: WishlistDao = Mockito.spy(WishlistDaoImpl())
    private val viewModel = DetailViewModel(RepositoryImpl(wishlistDao))

//    private val wishlistDao: WishlistDao = Mockito.spy(
//        Room.inMemoryDatabaseBuilder(
//            ApplicationProvider.getApplicationContext(),
//            WishlistDatabase::class.java,
//        )
//            .allowMainThreadQueries()
//            .build()
//            .wishListDao(),
//    )
    @Test
    fun saveNewItemCallsDatabase() {
        val wishItem = Wishlist("Victoria", listOf("RW Android Apprentice book", "AndroidPhone"), 1)
        viewModel.saveNewItem(wishItem, "smart watch")
        verify(wishlistDao).save(any())
    }

    @Test
    fun saveNewItemSavesData() = runTest {
        val wishItem = Wishlist("Victoria", listOf("RW Android Apprentice book", "AndroidPhone"), 1)
        val name = "smart watch"
        viewModel.saveNewItem(wishItem, name)
        val mockObserver = mock<Observer<Wishlist>>()
        runBlocking(Dispatchers.Main) {
            wishlistDao.findById(wishItem.id)
                .observeForever(mockObserver)
            verify(mockObserver).onChanged(wishItem.copy(wishes = wishItem.wishes + name))
        }
    }

    @Test
    fun getWishListCallsDatabase() {
        viewModel.getWishlist(1)
        verify(wishlistDao).findById(any())
    }

    @Test
    fun getWishListReturnsCorrectData() = runTest {
        val wishItem = Wishlist("Victoria", listOf("RW Android Apprentice book", "AndroidPhone"), 1)
        val name = "smart watch"
        wishlistDao.save(wishItem)
        runBlocking(Dispatchers.Main) {
            val mockObserver = mock<Observer<Wishlist>>()
            viewModel.getWishlist(1).observeForever(mockObserver)

            verify(mockObserver).onChanged(wishItem)
        }
    }
}