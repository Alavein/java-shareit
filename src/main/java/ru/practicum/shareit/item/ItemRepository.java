package ru.practicum.shareit.item;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Repository
public interface ItemRepository extends JpaRepository<Item, Long> {

    List<Item> findAllByOwnerIdOrderByIdAsc(long userId);

    @Query("select it " +
            "from Item as it " +
            "join it.owner as u " +
            "where u.id = ?1")
    List<Item> findItemsByUserId(long userId);

    @Query("select it " +
            "from Item as it " +
            "where it.available = true " +
            "and (lower(it.description) like lower(concat('%', ?1,'%'))" +
            "or lower(it.name) like lower(concat('%', ?1,'%')) )")
    List<Item> searchItems(String text);
}
