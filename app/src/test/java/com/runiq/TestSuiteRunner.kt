package com.runiq

import kotlinx.coroutines.ExperimentalCoroutinesApi
import org.junit.runner.RunWith
import org.junit.runners.Suite

/**
 * Test suite runner for organizing and running all RunIQ tests.
 * This provides a convenient way to run all tests together.
 */
@ExperimentalCoroutinesApi
@RunWith(Suite::class)
@Suite.SuiteClasses(
    // Domain Model Tests
    com.runiq.domain.model.RunSessionTest::class,
    com.runiq.domain.model.GpsTrackPointTest::class,
    
    // Use Case Tests
    com.runiq.domain.usecase.StartRunUseCaseTest::class,
    
    // Repository Tests
    com.runiq.data.repository.RunRepositoryImplTest::class,
    
    // DAO Tests
    com.runiq.data.local.dao.RunSessionDaoTest::class,
    com.runiq.data.local.dao.GpsTrackDaoTest::class,
    
    // ViewModel Tests
    com.runiq.presentation.screens.run.RunViewModelTest::class,
    
    // Utility Tests
    com.runiq.util.TestDataFactoryTest::class,
    com.runiq.util.TestExtensionsTest::class,
    
    // Integration Tests
    com.runiq.integration.RunSessionIntegrationTest::class
)
class TestSuiteRunner