package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDtoRequest;
import ru.practicum.shareit.booking.dto.BookingDtoResponse;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.BookingNotAvailableException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;

    @Override
    @Transactional
    public BookingDtoResponse createBooking(long userId, BookingDtoRequest bookingDtoRequest) {

        if (bookingDtoRequest.getStart().isEqual(bookingDtoRequest.getEnd())
                || bookingDtoRequest.getEnd().isBefore(bookingDtoRequest.getStart())) {
            throw new BadRequestException("Ошибка. Не получилось начать процесс.");
        }

        Item item = itemRepository.findById(bookingDtoRequest.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Предмет не найден."));
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
        Booking booking = BookingMapper.toBooking(bookingDtoRequest, item, user);

        if (Boolean.FALSE.equals(booking.getItem().getAvailable())) {
            throw new BookingNotAvailableException("Ошибка. Предмет не доступен.");
        }

        if (booking.getItem().getOwner().getId() == userId) {
            throw new DataNotFoundException("Ошиюбка. Нельзя забронировать свой же предмет.");
        }

        booking.setStatus(Status.WAITING);
        return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
    }

    @Override
    @Transactional
    public BookingDtoResponse updateBooking(long bookingId, long ownerId, boolean approved) {

        userRepository.findById(ownerId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Бронирование не найдено."));

        if (booking.getStatus().equals(Status.APPROVED)
                || booking.getStatus().equals(Status.REJECTED)) {
            throw new BadRequestException("Ошибка. Это бронирование не может сменить статус.");
        }

        if (ownerId == booking.getItem().getOwner().getId()) {
            if (approved) {
                booking.setStatus(Status.APPROVED);
            } else {
                booking.setStatus(Status.REJECTED);
            }
            return BookingMapper.toBookingDtoResponse(bookingRepository.save(booking));
        } else {
            throw new DataNotFoundException("Ошибка. Id владельца в запросе и владелец элемента не совпадают.");
        }
    }

    @Override
    public BookingDtoResponse getBooking(long bookingId, long userId) {

        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Бронирование не найдено."));

        if (booking.getItem().getOwner().getId() == userId || booking.getBooker().getId() == userId) {
            return BookingMapper.toBookingDtoResponse(booking);
        } else {
            throw new DataNotFoundException("Ошибка. Этот пользователь не может получить доступ к этой информации");
        }
    }

    @Override
    public List<BookingDtoResponse> getBookings(long ownerId, String state) {

        checkUserAndState(ownerId, state);

        List<Booking> bookings = new ArrayList<>();

        switch (BookingStatusPresentation.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findAllByBookerIdOrderByStartDesc(ownerId);
                break;
            case CURRENT:
                bookings = bookingRepository.findByBookerCurrent(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByBookerPast(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByBookerFuture(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByBookerAndStatus(
                        ownerId,
                        Status.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByBookerAndStatus(
                        ownerId,
                        Status.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
        }

        return BookingMapper.mapToBookingDtoResponse(bookings);
    }

    @Override
    public List<BookingDtoResponse> getBookingFromOwner(long ownerId, String state) {

        checkUserAndState(ownerId, state);

        List<Booking> bookings = new ArrayList<>();

        switch (BookingStatusPresentation.valueOf(state)) {
            case ALL:
                bookings = bookingRepository.findByItemOwnerId(
                        ownerId,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case CURRENT:
                bookings = bookingRepository.findByItemOwnerCurrent(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case PAST:
                bookings = bookingRepository.findByItemOwnerPast(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case FUTURE:
                bookings = bookingRepository.findByItemOwnerFuture(
                        ownerId,
                        LocalDateTime.now(),
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case WAITING:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        ownerId,
                        Status.WAITING,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
            case REJECTED:
                bookings = bookingRepository.findByItemOwnerAndStatus(
                        ownerId,
                        Status.REJECTED,
                        Sort.by(Sort.Direction.DESC, "start"));
                break;
        }

        return BookingMapper.mapToBookingDtoResponse(bookings);
    }

    private void checkUserAndState(long userId, String state) {
        BookingStatusPresentation[] states = BookingStatusPresentation.values();
        if (Arrays.stream(states).noneMatch(s -> s.name().equals(state))) {
            throw new BadRequestException("Ошибка: " + state);
        }
        userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
    }
}
