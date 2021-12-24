package com.melkonian.datasource

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.ServiceTestRule
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.Mockito.verify
import java.util.concurrent.TimeoutException

@RunWith(AndroidJUnit4::class)
class SpeedValuesServiceTest {
    @get:Rule
    var serviceRule: ServiceTestRule? = ServiceTestRule()

    var serviceMock: SpeedValuesService? = null

    @Before
    fun setUp() {
        serviceMock = mock(SpeedValuesService::class.java)
    }

    @Test
    @Throws(TimeoutException::class)
    fun test_start_boundService() {
        serviceRule?.let {
            serviceMock?.let { mock -> verify(mock).generateSpeed() }
        }
    }

    @After
    fun tearDown() {
        serviceRule = null
        serviceMock = null
    }
}