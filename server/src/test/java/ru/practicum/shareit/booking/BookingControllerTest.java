package ru.practicum.shareit.booking;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = BookingController.class)
@AutoConfigureMockMvc
class BookingControllerTest {
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private BookingServiceImpl bookingServiceImpl;
    private final LocalDateTime start = LocalDateTime.now().plusDays(1);
    private final LocalDateTime end = start.plusDays(7);

    @SneakyThrows
    @Test
    void getBookingById() {
        Integer bookingId = 1;
        BookingDto bookingDto = new BookingDto(1, start, end, null, null, Status.WAITING);

        Mockito.when(bookingServiceImpl.getBooking(Mockito.anyInt(), Mockito.anyInt())).thenReturn(bookingDto);

        String result = mockMvc.perform(get("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getAllBookingOfOwner() {
        BookingDto bookingDto = new BookingDto(1, start, end, null, null, Status.WAITING);

        Mockito.when(bookingServiceImpl.getBookingFromOwner(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings/owner")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void getBookings() {
        BookingDto bookingDto = new BookingDto(1, start, end, null, null, Status.WAITING);

        Mockito.when(bookingServiceImpl.getBookings(Mockito.anyString(), Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyInt())).thenReturn(List.of(bookingDto));

        String result = mockMvc.perform(get("/bookings")
                        .header("X-Sharer-User-Id", 1)
                        .param("state", "ALL")
                        .param("from", String.valueOf(0))
                        .param("size", String.valueOf(10)))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(List.of(bookingDto)), result);
    }

    @SneakyThrows
    @Test
    void addBookingRequest() {
        Integer userId = 1;
        Integer itemId = 1;
        BookingDtoAdd bookingDtoAdd = new BookingDtoAdd(userId, start, end, itemId);
        BookingDto bookingDto = new BookingDto(1, start, end, null, null, null);

        Mockito.when(bookingServiceImpl.createBooking(Mockito.any(), Mockito.anyInt()))
                .thenReturn(bookingDto);

        String result = mockMvc.perform(post("/bookings")
                        .header("X-Sharer-User-Id", userId)
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(bookingDtoAdd)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }

    @SneakyThrows
    @Test
    void getConsentToBooking() {
        Integer bookingId = 1;
        BookingDto bookingDto = new BookingDto(1, start, end, null, null, Status.WAITING);

        Mockito.when(bookingServiceImpl.updateBooking(Mockito.anyInt(), Mockito.anyInt(),
                Mockito.anyBoolean())).thenReturn(bookingDto);

        String result = mockMvc.perform(patch("/bookings/{bookingId}", bookingId)
                        .header("X-Sharer-User-Id", 1)
                        .param("bookingId", "1L")
                        .param("approved", "true"))
                .andDo(print())
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString();

        assertEquals(objectMapper.writeValueAsString(bookingDto), result);
    }
}
