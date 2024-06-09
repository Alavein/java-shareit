package ru.practicum.shareit.booking;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.booking.dto.BookingDto;
import ru.practicum.shareit.booking.dto.BookingDtoAdd;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.ItemRepository;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class BookingServiceImpl implements BookingService {
    private final BookingRepository bookingRepository;
    private final UserRepository userRepository;
    private final ItemRepository itemRepository;
    private final Sort sort = Sort.by(Sort.Direction.DESC, "start");

    @Override
    public BookingDto getBooking(Integer bookingId, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найдено."));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Бронирование не найдено."));

        if (!booking.getBooker().getId().equals(userId) && !booking.getItem().getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Ошибка. Нет доступа к информации о бронировании.");
        }

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    public List<BookingDto> getBookings(String state, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка.Пользователь не найден"));
        List<Booking> booking;
        BookingStatusPresentation stateOfBooking = BookingStatusPresentation.valueOf(state);

        switch (stateOfBooking) {
            case ALL:
                booking = bookingRepository.findAllByBooker(user, sort);
                break;
            case CURRENT:
                booking = bookingRepository.findAllByBookerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                booking = bookingRepository.findAllByBookerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                booking = bookingRepository.findAllByBookerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                booking = bookingRepository.findAllByBookerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                booking = bookingRepository.findAllByBookerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return booking.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    public List<BookingDto> getBookingFromOwner(String state, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
        List<Booking> booking;
        BookingStatusPresentation stateOfBooking = BookingStatusPresentation.valueOf(state);

        switch (stateOfBooking) {
            case ALL:
                booking = bookingRepository.findAllByItemOwner(user, sort);
                break;
            case CURRENT:
                booking = bookingRepository.findAllByItemOwnerAndStartBeforeAndEndAfter(user, LocalDateTime.now(),
                        LocalDateTime.now(), sort);
                break;
            case PAST:
                booking = bookingRepository.findAllByItemOwnerAndEndBefore(user, LocalDateTime.now(), sort);
                break;
            case FUTURE:
                booking = bookingRepository.findAllByItemOwnerAndStartAfter(user, LocalDateTime.now(), sort);
                break;
            case WAITING:
                booking = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.WAITING, sort);
                break;
            case REJECTED:
                booking = bookingRepository.findAllByItemOwnerAndStatusEquals(user, Status.REJECTED, sort);
                break;
            default:
                throw new BadRequestException("Unknown state: UNSUPPORTED_STATUS");
        }

        return booking.stream().map(BookingMapper::toBookingDto).collect(Collectors.toList());
    }

    @Override
    @Transactional
    public BookingDto updateBooking(Integer bookingId, Integer userId, Boolean approved) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Бронирование не найдено."));

        if (!booking.getItem().getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Ошибка. Нет доступа к информации о бронировании.");
        }

        if (!booking.getStatus().equals(Status.WAITING)) {
            throw new BadRequestException("Ошибка. Невозможно изменить статус");
        }

        if (approved) {
            booking.setStatus(Status.APPROVED);
        } else {
            booking.setStatus(Status.REJECTED);
        }

        booking = bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

    @Override
    @Transactional
    public BookingDto createBooking(BookingDtoAdd bookingDto, Integer userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь не найден."));
        Item item = itemRepository.findById(bookingDto.getItemId())
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Предмет не найден."));

        if (bookingDto.getEnd().isBefore(bookingDto.getStart()) ||
                bookingDto.getStart().isAfter(bookingDto.getEnd()) ||
                bookingDto.getStart().equals(bookingDto.getEnd())) {
            throw new BadRequestException("Ошибка. Неправильный формат дат.");
        }

        if (!item.getAvailable()) {
            throw new BadRequestException("Ошибка. Предмет недоступен.");
        }

        if (item.getOwner().getId().equals(userId)) {
            throw new DataNotFoundException("Ошибка. Невозможно забронировать свою вещь.");
        }

        Booking booking = BookingMapper.toBookingFromAdd(bookingDto);
        booking.setBooker(user);
        booking.setItem(item);
        booking.setStatus(Status.WAITING);
        bookingRepository.save(booking);

        return BookingMapper.toBookingDto(booking);
    }

}
