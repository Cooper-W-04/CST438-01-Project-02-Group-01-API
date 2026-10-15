package com.example

import org.junit.jupiter.api.Assertions.assertDoesNotThrow
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import org.mockito.Mockito.mock
import org.mockito.Mockito.verifyNoInteractions
import org.springframework.jdbc.core.JdbcTemplate

class PutRouteServiceTest {

    private val jdbcTemplate = mock(JdbcTemplate::class.java)
    private val service = PutRouteService(jdbcTemplate)

    @ParameterizedTest
    fun `replaceUserAvailability returns false for incorrectly formatted times`(
    ) {
        var times = ""
        var result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
        
        times = "1234567"
        result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
        
        times = "12345"
        result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
        
        times = "90809"
        result = 
                service.replaceUserAvailability(
                    groupId = 1L,
                    userId = 2L,
                    times = times
            )
        

        assertFalse(result)
        verifyNoInteractions(jdbcTemplate)
    }
}