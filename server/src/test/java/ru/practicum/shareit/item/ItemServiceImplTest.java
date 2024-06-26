package ru.practicum.shareit.item;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.PageRequest;
import ru.practicum.shareit.booking.model.Booking;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.Status;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.request.ItemRequestRepository;
import ru.practicum.shareit.request.model.ItemRequest;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class ItemServiceImplTest {

    @InjectMocks
    private ItemServiceImpl itemService;
    @Mock
    private ItemRepository itemRepository;
    @Mock
    private UserRepository userRepository;
    @Mock
    private BookingRepository bookingRepository;
    @Mock
    private CommentRepository commentRepository;
    @Mock
    private ItemRequestRepository itemRequestRepository;

    private User user;
    private ItemDto itemDto;
    private Item item;
    private ItemDtoComments itemDtoComments;
    private Comment comment;
    private CommentDto commentDto;
    private Booking booking;
    private Booking booking2;
    private ItemRequest itemRequest;

    @BeforeEach
    void create() {
        user = new User(1, "Имя", "mail@yandex.ru");

        item = new Item(1, "Предмет", "Описание", true, user,
                null);

        itemDto = new ItemDto(1, "Предмет", "Описание", true, 1);

        itemDtoComments = new ItemDtoComments(1, "Имя", "Описание", true,
                null, null, null);

        itemRequest = new ItemRequest(1, "Описание", new User(), null);

        booking = new Booking(1, null, null, item, user, Status.WAITING);

        booking2 = new Booking(2, LocalDateTime.of(2020, 10, 20, 20, 20, 20),
                LocalDateTime.of(2020, 10, 22, 20, 20, 20), item, user,
                Status.APPROVED);

        comment = new Comment(1, "Комментарий", item, user, null);
        commentDto = new CommentDto(1, "Комментарий", user.getName(), null);

    }

    @Test
    void getItemsOfUser() {

        Integer userId = 1;

        Mockito.when(userRepository.findById(userId))
                .thenReturn(Optional.of(user));

        Mockito.when(itemRepository.findAllByOwnerId(user.getId(), PageRequest.of(0, 10)))
                .thenReturn(List.of(item));

        Mockito.when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEquals(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt())).thenReturn(List.of(comment));

        itemDtoComments = ItemMapper.toItemDtoComments(item);
        itemDtoComments.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDtoComments.setLastBooking(new ItemDtoComments.BookingDto(booking.getId(), booking.getBooker().getId()));
        itemDtoComments.setNextBooking(new ItemDtoComments.BookingDto(booking.getId(), booking.getBooker().getId()));

        List<ItemDtoComments> itemDtoComments1 = List.of(itemDtoComments);
        List<ItemDtoComments> itemDtoComments2 = itemService.getItemsOfUser(userId, 0, 10);

        Assertions.assertEquals(itemDtoComments1, itemDtoComments2);
    }

    @Test
    void getItem() {
        Integer itemId = 1;
        Integer userId = 1;

        Mockito.when(itemRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.findAllByItemId(Mockito.anyInt())).thenReturn(List.of(comment));
        Mockito.when(bookingRepository.findFirstByItemIdAndStartLessThanEqualAndStatus(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        Mockito.when(bookingRepository.findFirstByItemIdAndStartAfterAndStatusEquals(Mockito.any(),
                Mockito.any(), Mockito.any(), Mockito.any())).thenReturn(Optional.of(booking));

        itemDtoComments = ItemMapper.toItemDtoComments(item);
        itemDtoComments.setComments(List.of(CommentMapper.toCommentDto(comment)));
        itemDtoComments.setLastBooking(new ItemDtoComments.BookingDto(booking.getId(), booking.getBooker().getId()));
        itemDtoComments.setNextBooking(new ItemDtoComments.BookingDto(booking.getId(), booking.getBooker().getId()));

        assertThat(itemService.getItemById(itemId, userId)).isEqualTo(itemDtoComments);
    }

    @Test
    void getItemByIdObjectNotFoundException() {
        Integer itemId = 1000;
        Integer userId = 1;

        Mockito.when(itemRepository.findById(Mockito.anyInt()))
                .thenThrow(new DataNotFoundException("Ошибка. Предмет не найден."));

        assertThrows(DataNotFoundException.class, () -> itemService.getItemById(itemId, userId));
    }

    @Test
    void getItems() {
        Mockito.when(itemRepository
                        .findAllByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCaseAndAvailable("Текст",
                                "Текст", true, PageRequest.of(0, 10)))
                .thenReturn(List.of(item));

        List<ItemDto> itemDto1 = List.of(ItemMapper.toItemDto(item));
        List<ItemDto> itemDto2 = itemService.getItems("Текст", 0, 10);

        assertEquals(itemDto1, itemDto2);
    }

    @Test
    void addItemWithItemRequest() {
        Integer userId = 1;

        Mockito.when(userRepository.findById(Mockito.anyInt())).thenReturn(Optional.of(user));
        Mockito.when(itemRequestRepository.findById(Mockito.anyInt()))
                .thenReturn(Optional.of(itemRequest));

        ItemDto itemDto1 = new ItemDto(1, "Предмет", "Описание", true, 1);
        ItemDto itemDto2 = itemService.addItem(itemDto, userId);

        assertEquals(itemDto1, itemDto2);

        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void addItemOwnerNotFound() {
        Integer userId = 999;

        Mockito.when(userRepository.findById(userId))
                .thenThrow(new DataNotFoundException("Ошибка. Пользователь не найден."));

        assertThrows(DataNotFoundException.class, () -> itemService.addItem(itemDto, userId));

        Mockito.verify(itemRequestRepository, Mockito.never()).findById(Mockito.any());
        Mockito.verify(itemRepository, Mockito.never()).save(Mockito.any());
    }

    @Test
    void updateItem() {
        Integer itemId = 1;
        Integer userId = 1;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));


        ItemDto itemDto1 = new ItemDto(itemId, "Предметик", "Описание", true, null);

        itemService.updateItem(itemDto1, itemId, userId);

        Mockito.verify(itemRepository, Mockito.times(1)).save(Mockito.any());
    }

    @Test
    void updateItemObjectNotFoundException() {
        Integer itemId = 1;
        Integer userId = 3;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        ItemDto itemDto1 = new ItemDto(itemId, "Предметик", "Описание", true, null);

        assertThrows(DataNotFoundException.class, () -> itemService.updateItem(itemDto1, itemId, userId));
    }

    @Test
    void addComment() {
        Integer itemId = 1;
        Integer userId = 1;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        Mockito.when(commentRepository.save(Mockito.any())).thenReturn(comment);
        List<Booking> bookingList = List.of(booking2);
        Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyInt(), Mockito.any())).thenReturn(bookingList);

        CommentDto commentDto1 = itemService.addComment(itemId, userId, commentDto);

        assertEquals(1, commentDto1.getId());
        assertEquals("Комментарий", commentDto1.getText());
        assertEquals("Имя", commentDto1.getAuthorName());
    }

    @Test
    void addCommentBadRequestException() {
        Integer itemId = 1;
        Integer userId = 1;

        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));
        List<Booking> bookingList = List.of(booking);
        Mockito.when(bookingRepository.findAllByBookerId(Mockito.anyInt(), Mockito.any()))
                .thenReturn(bookingList);

        assertThrows(BadRequestException.class, () -> itemService.addComment(itemId, userId, commentDto));
    }

    @Test
    void deleteItem() {
        Integer itemId = 1;

        Mockito.when(itemRepository.findById(itemId)).thenReturn(Optional.of(item));

        itemService.deleteItem(itemId);

        Mockito.verify(itemRepository, Mockito.times(1)).deleteById(itemId);
    }
}
