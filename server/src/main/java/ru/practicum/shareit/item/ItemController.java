package ru.practicum.shareit.item;

import org.springframework.web.bind.annotation.*;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemDtoComments;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    @GetMapping
    public List<ItemDtoComments> getItemsOfUser(@RequestHeader("X-Sharer-User-Id") Integer userId,
                                                @RequestParam(defaultValue = "0") Integer from,
                                                @RequestParam(defaultValue = "30") Integer size) {
        return itemService.getItemsOfUser(userId, from, size);
    }

    @GetMapping("/{itemId}")
    public ItemDtoComments getItemById(@PathVariable Integer itemId,
                                       @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.getItemById(itemId, userId);
    }

    @GetMapping("/search")
    public List<ItemDto> getItems(@RequestParam(value = "text", required = false) String text,
                                  @RequestParam(defaultValue = "0") Integer from,
                                  @RequestParam(defaultValue = "30") Integer size) {
        return itemService.getItems(text, from, size);
    }

    @PatchMapping("/{itemId}")
    public ItemDto updateItem(@Valid @RequestBody ItemDto itemDto,
                              @PathVariable Integer itemId,
                              @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.updateItem(itemDto, itemId, userId);
    }

    @PostMapping
    public ItemDto addItem(@RequestBody ItemDto itemDto,
                           @RequestHeader("X-Sharer-User-Id") Integer userId) {
        return itemService.addItem(itemDto, userId);
    }

    @PostMapping("/{itemId}/comment")
    public CommentDto addComment(@PathVariable Integer itemId,
                                 @RequestHeader("X-Sharer-User-Id") Integer userId,
                                 @RequestBody CommentDto commentDto) {
        return itemService.addComment(itemId, userId, commentDto);
    }

    @DeleteMapping("/{itemId}")
    public void deleteItem(@PathVariable Integer itemId) {
        itemService.deleteItem(itemId);
    }
}
