//package com.example.projectmdp.ui.module.UserDashboard
//
//import androidx.arch.core.executor.testing.InstantTaskExecutorRule
//import androidx.lifecycle.Observer
//import com.example.projectmdp.data.repository.ProductRepository
//import com.example.projectmdp.data.source.dataclass.Product
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.ExperimentalCoroutinesApi
//import kotlinx.coroutines.flow.flowOf
//import kotlinx.coroutines.test.*
//import org.junit.After
//import org.junit.Before
//import org.junit.Rule
//import org.junit.Test
//import org.junit.runner.RunWith
//import org.mockito.Mock
//import org.mockito.Mockito.*
//import org.mockito.junit.MockitoJUnitRunner
//import org.mockito.kotlin.whenever
//
//@ExperimentalCoroutinesApi
//@RunWith(MockitoJUnitRunner::class)
//class UserDashboardViewModelTest {
//
//    @get:Rule
//    val instantTaskExecutorRule = InstantTaskExecutorRule()
//
//    @Mock
//    private lateinit var productRepository: ProductRepository
//
//    @Mock
//    private lateinit var productsObserver: Observer<List<Product>>
//
//    @Mock
//    private lateinit var loadingObserver: Observer<Boolean>
//
//    @Mock
//    private lateinit var errorObserver: Observer<String?>
//
//    private lateinit var viewModel: UserDashboardViewModel
//
//    private val testDispatcher = UnconfinedTestDispatcher()
//
//    // Sample test data
//    private val sampleProducts = listOf(
//        Product(
//            product_id = "1",
//            name = "Product 1",
//            price = 100.0,
//            description = "Description 1",
//            category = "Electronics",
//            image = "image1.jpg",
//            user_id = "user1",
//            created_at = "2024-01-01",
//            deleted_at = null
//        ),
//        Product(
//            product_id = "2",
//            name = "Product 2",
//            price = 200.0,
//            description = "Description 2",
//            category = "Books",
//            image = "image2.jpg",
//            user_id = "user1",
//            created_at = "2024-01-02",
//            deleted_at = null
//        )
//    )
//
//    @Before
//    fun setup() {
//        Dispatchers.setMain(testDispatcher)
//        viewModel = UserDashboardViewModel(productRepository)
//
//        // Setup observers
//        viewModel.products.observeForever(productsObserver)
//        viewModel.loading.observeForever(loadingObserver)
//        viewModel.error.observeForever(errorObserver)
//    }
//
//    @After
//    fun tearDown() {
//        Dispatchers.resetMain()
//        viewModel.products.removeObserver(productsObserver)
//        viewModel.loading.removeObserver(loadingObserver)
//        viewModel.error.removeObserver(errorObserver)
//    }
//
//    @Test
//    fun `loadProducts should emit loading states and products successfully`() = runTest {
//        // Arrange
//        val successResult = Result.success(
//            ProductRepository.ProductsWithPagination(
//                products = sampleProducts,
//                pagination = ProductRepository.PaginationInfo(
//                    currentPage = 1,
//                    totalPages = 1,
//                    totalProducts = 2,
//                    hasNext = false,
//                    hasPrev = false,
//                    limit = 10
//                )
//            )
//        )
//        whenever(productRepository.getAllProducts()).thenReturn(flowOf(successResult))
//
//        // Act
//        viewModel.loadProducts()
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(loadingObserver).onChanged(true)  // Loading started
//        verify(loadingObserver).onChanged(false) // Loading finished
//        verify(productsObserver).onChanged(sampleProducts)
//        verify(errorObserver).onChanged(null) // Clear any previous errors
//        verifyNoMoreInteractions(errorObserver)
//    }
//
//    @Test
//    fun `loadProducts should emit error when repository fails`() = runTest {
//        // Arrange
//        val errorMessage = "Network error"
//        val failureResult = Result.failure<ProductRepository.ProductsWithPagination>(
//            Exception(errorMessage)
//        )
//        whenever(productRepository.getAllProducts()).thenReturn(flowOf(failureResult))
//
//        // Act
//        viewModel.loadProducts()
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(loadingObserver).onChanged(true)  // Loading started
//        verify(loadingObserver).onChanged(false) // Loading finished
//        verify(errorObserver).onChanged(errorMessage)
//        verify(productsObserver, never()).onChanged(any()) // Products not updated on error
//    }
//
//    @Test
//    fun `refreshProducts should force refresh and emit new data`() = runTest {
//        // Arrange
//        val refreshedProducts = listOf(sampleProducts[0]) // Only one product after refresh
//        val successResult = Result.success(
//            ProductRepository.ProductsWithPagination(
//                products = refreshedProducts,
//                pagination = ProductRepository.PaginationInfo(
//                    currentPage = 1,
//                    totalPages = 1,
//                    totalProducts = 1,
//                    hasNext = false,
//                    hasPrev = false,
//                    limit = 10
//                )
//            )
//        )
//        whenever(productRepository.getAllProducts(forceRefresh = true))
//            .thenReturn(flowOf(successResult))
//
//        // Act
//        viewModel.refreshProducts()
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(productRepository).getAllProducts(forceRefresh = true)
//        verify(loadingObserver).onChanged(true)
//        verify(loadingObserver).onChanged(false)
//        verify(productsObserver).onChanged(refreshedProducts)
//    }
//
//    @Test
//    fun `searchProducts should filter products by name`() = runTest {
//        // Arrange
//        val searchQuery = "Product 1"
//        val searchResults = listOf(sampleProducts[0]) // Only first product matches
//        val successResult = Result.success(searchResults)
//        whenever(productRepository.searchProducts(searchQuery))
//            .thenReturn(flowOf(successResult))
//
//        // Act
//        viewModel.searchProducts(searchQuery)
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(productRepository).searchProducts(searchQuery)
//        verify(loadingObserver).onChanged(true)
//        verify(loadingObserver).onChanged(false)
//        verify(productsObserver).onChanged(searchResults)
//    }
//
//    @Test
//    fun `searchProducts with empty query should load all products`() = runTest {
//        // Arrange
//        val successResult = Result.success(
//            ProductRepository.ProductsWithPagination(
//                products = sampleProducts,
//                pagination = ProductRepository.PaginationInfo(
//                    currentPage = 1,
//                    totalPages = 1,
//                    totalProducts = 2,
//                    hasNext = false,
//                    hasPrev = false,
//                    limit = 10
//                )
//            )
//        )
//        whenever(productRepository.getAllProducts()).thenReturn(flowOf(successResult))
//
//        // Act
//        viewModel.searchProducts("") // Empty query
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(productRepository).getAllProducts() // Should call getAllProducts instead of search
//        verify(productsObserver).onChanged(sampleProducts)
//    }
//
//    @Test
//    fun `loadProductsByCategory should filter products by category`() = runTest {
//        // Arrange
//        val category = "Electronics"
//        val filteredProducts = listOf(sampleProducts[0]) // Only electronics product
//        whenever(productRepository.getProductsByCategory(category))
//            .thenReturn(filteredProducts)
//
//        // Act
//        viewModel.loadProductsByCategory(category)
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(productRepository).getProductsByCategory(category)
//        verify(loadingObserver).onChanged(true)
//        verify(loadingObserver).onChanged(false)
//        verify(productsObserver).onChanged(filteredProducts)
//    }
//
//    @Test
//    fun `loadProductsByCategory should handle empty results`() = runTest {
//        // Arrange
//        val category = "NonExistentCategory"
//        whenever(productRepository.getProductsByCategory(category))
//            .thenReturn(emptyList())
//
//        // Act
//        viewModel.loadProductsByCategory(category)
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(productRepository).getProductsByCategory(category)
//        verify(productsObserver).onChanged(emptyList())
//        verify(errorObserver, never()).onChanged(any()) // No error for empty results
//    }
//
//    @Test
//    fun `multiple loadProducts calls should cancel previous operations`() = runTest {
//        // Arrange
//        val firstResult = Result.success(
//            ProductRepository.ProductsWithPagination(
//                products = listOf(sampleProducts[0]),
//                pagination = ProductRepository.PaginationInfo(1, 1, 1, false, false, 10)
//            )
//        )
//        val secondResult = Result.success(
//            ProductRepository.ProductsWithPagination(
//                products = sampleProducts,
//                pagination = ProductRepository.PaginationInfo(1, 1, 2, false, false, 10)
//            )
//        )
//
//        whenever(productRepository.getAllProducts())
//            .thenReturn(flowOf(firstResult))
//            .thenReturn(flowOf(secondResult))
//
//        // Act
//        viewModel.loadProducts() // First call
//        viewModel.loadProducts() // Second call should cancel first
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(productRepository, times(2)).getAllProducts()
//        verify(productsObserver).onChanged(sampleProducts) // Should have latest result
//    }
//
//    @Test
//    fun `error state should be cleared on successful load`() = runTest {
//        // Arrange - First fail, then succeed
//        val errorResult = Result.failure<ProductRepository.ProductsWithPagination>(
//            Exception("Network error")
//        )
//        val successResult = Result.success(
//            ProductRepository.ProductsWithPagination(
//                products = sampleProducts,
//                pagination = ProductRepository.PaginationInfo(1, 1, 2, false, false, 10)
//            )
//        )
//
//        whenever(productRepository.getAllProducts())
//            .thenReturn(flowOf(errorResult))
//            .thenReturn(flowOf(successResult))
//
//        // Act
//        viewModel.loadProducts() // First call - should fail
//        testScheduler.advanceUntilIdle()
//
//        viewModel.loadProducts() // Second call - should succeed
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(errorObserver).onChanged("Network error") // Error from first call
//        verify(errorObserver).onChanged(null) // Error cleared on second call
//        verify(productsObserver).onChanged(sampleProducts) // Success data from second call
//    }
//
//    @Test
//    fun `ViewModel should handle repository exceptions gracefully`() = runTest {
//        // Arrange
//        whenever(productRepository.getAllProducts())
//            .thenThrow(RuntimeException("Unexpected error"))
//
//        // Act
//        viewModel.loadProducts()
//
//        // Wait for coroutines to complete
//        testScheduler.advanceUntilIdle()
//
//        // Assert
//        verify(loadingObserver).onChanged(true)
//        verify(loadingObserver).onChanged(false)
//        verify(errorObserver).onChanged("Unexpected error")
//        verify(productsObserver, never()).onChanged(any())
//    }
//
//    @Test
//    fun `loading state should be false initially`() {
//        // Assert
//        verify(loadingObserver).onChanged(false)
//    }
//
//    @Test
//    fun `products should be empty list initially`() {
//        // Assert
//        verify(productsObserver).onChanged(emptyList())
//    }
//
//    @Test
//    fun `error should be null initially`() {
//        // Assert
//        verify(errorObserver).onChanged(null)
//    }
//}
