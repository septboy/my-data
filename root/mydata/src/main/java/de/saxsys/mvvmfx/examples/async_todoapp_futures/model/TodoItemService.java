package de.saxsys.mvvmfx.examples.async_todoapp_futures.model;


import java.util.List;

import jakarta.inject.Singleton;

@Singleton
public interface TodoItemService {

    void createItem(TodoItem item);

    void deleteItem(TodoItem item);

    TodoItem getItemById(String id);

    List<TodoItem> getAllItems();

}