package ru.practicum.shareit.item;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.util.DefaultUriBuilderFactory;
import ru.practicum.shareit.client.BaseClient;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.dto.ItemDto;

import java.util.Map;

@Service
public class ItemClient extends BaseClient {
    private static final String API_PREFIX = "/items";

    @Autowired
    public ItemClient(@Value("${shareit-server.url}") String serverUrl, RestTemplateBuilder builder) {
        super(
                builder
                        .uriTemplateHandler(new DefaultUriBuilderFactory(serverUrl + API_PREFIX))
                        .requestFactory(HttpComponentsClientHttpRequestFactory::new)
                        .build()
        );
    }

    public ResponseEntity<Object> getItemsOfUser(Integer userId, Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "from", from,
                "size", size
        );
        return get("?from={from}&size={size}", userId, parameters);
    }

    public ResponseEntity<Object> getItemById(Integer itemId, Integer userId) {
        return get("/" + itemId, userId);
    }

    public ResponseEntity<Object> getItems(String text,Integer from, Integer size) {
        Map<String, Object> parameters = Map.of(
                "text", text,
                "from", from,
                "size", size
        );
        return get("/search?text={text}", parameters);
    }

    public ResponseEntity<Object> updateItem(ItemDto itemDto, Integer itemId, Integer userId) {
        return patch("/" + itemId, userId, itemDto);
    }

    public ResponseEntity<Object> addItem(ItemDto itemDto, Integer userId) {
        return post("", userId, itemDto);
    }

    public ResponseEntity<Object> addComment(Integer itemId, Integer userId, CommentDto commentDto) {
        return post("/" + itemId + "/comment", userId, commentDto);
    }

    public ResponseEntity<Object> deleteItem(Integer itemId) {
        return delete("/" + itemId);
    }
}
