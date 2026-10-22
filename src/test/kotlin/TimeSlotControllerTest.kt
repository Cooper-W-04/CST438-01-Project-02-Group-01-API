package com.scheduler

import com.scheduler.config.SecurityConfig
import com.scheduler.controller.TimeSlotController
import com.scheduler.model.TimeSlot
import com.scheduler.repository.TimeSlotRepository
import org.junit.jupiter.api.Test
import org.mockito.Mockito.`when`
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.context.bean.override.mockito.MockitoBean
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(TimeSlotController::class)
// Importing security config is necessary for it to recognize that getting the user list
// should be public
@Import(SecurityConfig::class)
class TimeSlotControllerTest {

    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockitoBean
    private lateinit var timeSlotRepository: TimeSlotRepository

    // Mock data test just adds two timeslots and ensures that list works properly; the proper
    // 168 in the database would work according to the same logic.
    @Test
    fun `timeslot controller properly returns a list of all timeslots`() {
        val mockTimeslot = TimeSlot(1L, 1, 1, 2)
        val mockTimeslot2 = TimeSlot(2L, 2, 2, 3)

        `when`(timeSlotRepository.findAll())
            .thenReturn(listOf(mockTimeslot, mockTimeslot2))

        mockMvc.perform(
            get("/api/v1/time-slots")
                .accept(MediaType.APPLICATION_JSON)
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.length()").value(2))
            .andExpect(jsonPath("$[0].id").value(1))
            .andExpect(jsonPath("$[1].id").value(2))
    }
}