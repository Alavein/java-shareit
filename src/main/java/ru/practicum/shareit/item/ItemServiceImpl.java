package ru.practicum.shareit.item;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.booking.BookingMapper;
import ru.practicum.shareit.booking.BookingRepository;
import ru.practicum.shareit.booking.dto.BookingDtoForItem;
import ru.practicum.shareit.exceptions.BadRequestException;
import ru.practicum.shareit.exceptions.DataNotFoundException;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoBooking;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.UserRepository;
import ru.practicum.shareit.user.model.User;


import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ItemServiceImpl implements ItemService {
    private final ItemRepository itemRepository;
    private final UserRepository userRepository;
    private final BookingRepository bookingRepository;
    private final CommentRepository commentRepository;

    @Override
    public ItemDtoBooking getItem(long itemId, long userId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Предмет не найден."));
        return setBookingsAndComments(userId, List.of(item)).get(0);
    }

    @Override
    public List<ItemDtoBooking> getItemsByUser(long userId) {
        List<Item> userItems = itemRepository.findAllByOwnerIdOrderByIdAsc(userId);
        return setBookingsAndComments(userId, userItems);
    }

    @Override
    @Transactional
    public ItemDto createItem(long userId, ItemDto itemDto) {
        User user = userRepository.findById(userId).orElseThrow(() -> new DataNotFoundException("Ошибка. Пользователь" +
                " не найден."));
        Item item = ItemMapper.fromItemDto(itemDto, user, null);
        return ItemMapper.toItemDto(itemRepository.save(item));
    }

    @Override
    @Transactional
    public ItemDto updateItem(long id, ItemDto itemDto, long userId) {
        return itemRepository.findById(id)
                .map(i -> {
                    if (i.getOwner().getId() != userId) {
                        throw new DataNotFoundException("Ошибка. Пользователь не найден.");
                    }
                    if (itemDto.getName() != null)
                        i.setName(itemDto.getName());
                    if (itemDto.getDescription() != null)
                        i.setDescription(itemDto.getDescription());
                    if (itemDto.getAvailable() != null)
                        i.setAvailable(itemDto.getAvailable());
                    return ItemMapper.toItemDto(itemRepository.save(i));
                }).orElseThrow(() -> new DataNotFoundException("Ошибка. Предмет не найден."));
    }

    @Override
    public List<ItemDto> searchItems(String text) {
        if (text.isEmpty())
            return new ArrayList<>();
        return ItemMapper.mapToItemDto(itemRepository.searchItems(text));
    }

    @Override
    @Transactional
    public CommentDto createComment(long itemId, CommentDto commentDto, long authorId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new DataNotFoundException("Ошибка. Предмет с id = " + itemId + " не найден."));
        User user = userRepository.findById(authorId)
                .orElseThrow(() -> new DataNotFoundException("Пользователь с id = " + authorId + " не найден."));

        Comment comment = CommentMapper.toComment(user, item, commentDto);

        if (!bookingRepository.findByBookerIdAndItemIdAndEndBefore(
                authorId,
                itemId,
                LocalDateTime.now(),
                Sort.by(Sort.Direction.DESC, "start")).isEmpty()) {

            comment.setItem(item);
            comment.setAuthor(user);
            comment.setCreated(LocalDateTime.now());
            commentRepository.save(comment);
            return CommentMapper.toCommentDto(comment);
        } else
            throw new BadRequestException("Ошибка. Этот пользователь не может оставить комментарий.");
    }

    private List<ItemDtoBooking> setBookingsAndComments(long userId, List<Item> items) {
        LocalDateTime now = LocalDateTime.now();

        List<Long> ids = items.stream()
                .map(Item::getId)
                .collect(Collectors.toList());
        Map<Long, BookingDtoForItem> last = bookingRepository
                .findBookingsLast(ids, now, userId, Sort.by(Sort.Direction.DESC, "start")).stream()
                .map(BookingMapper::toBookingDtoForItem)
                .collect(Collectors.toMap(BookingDtoForItem::getItemId, item -> item, (a, b) -> a));
        Map<Long, BookingDtoForItem> next = bookingRepository
                .findBookingsNext(ids, now, userId, Sort.by(Sort.Direction.DESC, "start")).stream()
                .map(BookingMapper::toBookingDtoForItem)
                .collect(Collectors.toMap(BookingDtoForItem::getItemId, item -> item, (a, b) -> b));
        Map<Long, List<Comment>> comments = commentRepository.findByItemId_IdIn(ids).stream()
                .collect(Collectors.groupingBy(comment -> comment.getItem().getId()));

        List<ItemDtoBooking> result = items.stream()
                .map(ItemMapper::toItemDtoBooking)
                .collect(Collectors.toList());
        for (ItemDtoBooking item : result) {
            item.setLastBooking(last.get(item.getId()));
            item.setNextBooking(next.get(item.getId()));
            item.getComments().addAll(comments.getOrDefault(item.getId(), List.of()).stream()
                    .map(CommentMapper::toCommentDto).collect(Collectors.toList()));
        }

        return result;
    }
}