package com.example.grampet.viewmodel

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.example.grampet.remote.model.PostResponse
import com.example.grampet.repository.PostRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test

/**
 * PostsViewModelTest - 5 Tests Unitarios
 *
 * Usa MockK para mockear PostRepository
 * Valida la lógica de negocio del ViewModel
 */
@ExperimentalCoroutinesApi
class PostsViewModelTest {

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    private lateinit var viewModel: PostsViewModel
    private lateinit var mockRepository: PostRepository
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockRepository = mockk()
    }

    @After
    fun tearDown() {
        // No necesitas hacer nada aquí
    }

    /**
     * TEST 1: Carga exitosa de publicaciones desde API
     *
     * Valida que cuando la API devuelve posts,
     * el ViewModel los carga correctamente
     */
    @Test
    fun testLoadPostsSuccess() = runTest {
        // ARRANGE - Mock de datos exitosos
        val mockPosts = listOf(
            PostResponse(1, "baltazar_dev", "https://example.com/img1.jpg", "¡Mi primera foto!"),
            PostResponse(2, "michi_aventurero", "https://example.com/img2.jpg", "Explorando"),
            PostResponse(3, "firulais_viajero", "https://example.com/img3.jpg", "El mejor día")
        )

        coEvery { mockRepository.getPosts() } returns mockPosts
        viewModel = PostsViewModel(mockRepository)

        // ACT - Esperar a que se carguen
        val posts = viewModel.posts.first { it.isNotEmpty() }

        // ASSERT
        assertEquals("Debe cargar 3 posts", 3, posts.size)
        assertTrue("Todos deben tener ID > 0", posts.all { it.id > 0 })
        assertTrue("Todos deben tener username", posts.all { it.username.isNotEmpty() })
        assertEquals("Primer post de baltazar_dev", "baltazar_dev", posts[0].username)
    }

    /**
     * TEST 2: Fallback cuando API falla
     *
     * Valida que cuando la API lanza error,
     * el ViewModel carga datos de prueba
     */
    @Test
    fun testLoadPostsErrorFallback() = runTest {
        // ARRANGE - Mock que lanza excepción
        coEvery { mockRepository.getPosts() } throws RuntimeException("Error de red")
        viewModel = PostsViewModel(mockRepository)

        // ACT - Esperar a que se carguen datos de prueba
        val posts = viewModel.posts.first { it.isNotEmpty() }

        // ASSERT - Debe tener datos de fallback
        assertTrue("Debe haber posts de fallback", posts.isNotEmpty())
        assertTrue("Debe cargar al menos 1 post", posts.size >= 1)
        assertEquals("Primer post debe ser de baltazar_dev", "baltazar_dev", posts[0].username)
    }

    /**
     * TEST 3: Estructura válida de posts
     *
     * Valida que cada post tiene todos los campos
     * necesarios para renderizarse
     */
    @Test
    fun testPostStructureIsValid() = runTest {
        // ARRANGE
        val mockPosts = listOf(
            PostResponse(1, "user1", "https://example.com/img.jpg", "Descripción 1"),
            PostResponse(2, "user2", "https://example.com/img2.jpg", "Descripción 2")
        )

        coEvery { mockRepository.getPosts() } returns mockPosts
        viewModel = PostsViewModel(mockRepository)

        // ACT
        val posts = viewModel.posts.first { it.isNotEmpty() }

        // ASSERT - Validar estructura
        posts.forEach { post ->
            assertTrue("ID debe ser > 0", post.id > 0)
            assertTrue("Username debe tener contenido", post.username.isNotEmpty())
            assertTrue("Description debe tener contenido", post.description.isNotEmpty())
            assertTrue("Model no debe ser null", post.model != null)
        }
    }

    /**
     * TEST 4: IDs únicos en posts
     *
     * Valida que no hay IDs duplicados
     * en la lista de posts
     */
    @Test
    fun testAllPostsHaveUniqueIds() = runTest {
        // ARRANGE
        val mockPosts = listOf(
            PostResponse(1, "user1", "https://example.com/img1.jpg", "Post 1"),
            PostResponse(2, "user2", "https://example.com/img2.jpg", "Post 2"),
            PostResponse(3, "user3", "https://example.com/img3.jpg", "Post 3"),
            PostResponse(4, "user4", "https://example.com/img4.jpg", "Post 4"),
            PostResponse(5, "user5", "https://example.com/img5.jpg", "Post 5")
        )

        coEvery { mockRepository.getPosts() } returns mockPosts
        viewModel = PostsViewModel(mockRepository)

        // ACT
        val posts = viewModel.posts.first { it.isNotEmpty() }
        val ids = posts.map { it.id }
        val uniqueIds = ids.toSet()

        // ASSERT
        assertEquals("Todos los IDs deben ser únicos", uniqueIds.size, ids.size)
        assertTrue("No debe haber duplicados", ids.size == uniqueIds.size)
    }

    /**
     * TEST 5: Estado de carga se completa
     *
     * Valida que isLoading cambia a false
     * cuando termina la carga
     */
    @Test
    fun testLoadingStateCompletesSuccessfully() = runTest {
        // ARRANGE
        val mockPosts = listOf(
            PostResponse(1, "user1", "https://example.com/img.jpg", "Test")
        )

        coEvery { mockRepository.getPosts() } returns mockPosts
        viewModel = PostsViewModel(mockRepository)

        // ACT - Esperar a que loading sea false
        val finalLoadingState = viewModel.isLoading.first { !it }

        // ASSERT
        assertEquals("Loading debe ser false al completar", false, finalLoadingState)

        // Validar que hay datos
        val posts = viewModel.posts.first { it.isNotEmpty() }
        assertTrue("Debe haber posts cuando loading es false", posts.isNotEmpty())
    }
}
