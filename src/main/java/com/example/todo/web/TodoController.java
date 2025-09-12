package com.example.todo.web;

import com.example.todo.domain.Todo;
import com.example.todo.domain.User;
import com.example.todo.repository.TodoRepository;
import com.example.todo.repository.UserRepository;
import com.example.todo.web.TodoDtos.TodoRequest;

import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.example.todo.web.TodoDtos.*;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoRepository todoRepository;
    private final UserRepository userRepository;

    public TodoController(TodoRepository todoRepository, UserRepository userRepository) {
        this.todoRepository = todoRepository;
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<Todo> list(@AuthenticationPrincipal UserDetails principal) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return todoRepository.findByUser(u);
    }

    @PostMapping
    public ResponseEntity<Todo> create(@AuthenticationPrincipal UserDetails principal,
                                       @Valid @RequestBody TodoRequest req) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        Todo t = new Todo(req.title(), req.description(), u);
        t.setCompleted(req.completed());
        Todo saved = todoRepository.save(t);
        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Todo> update(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable Long id,
                                    @Valid @RequestBody TodoRequest req) {
        var u = userRepository.findByUsername(principal.getUsername()).orElseThrow();

        return todoRepository.findById(id)
            .filter(t -> t.getUser().getId().equals(u.getId()))
            .map(t -> {
                t.setTitle(req.title());
                t.setDescription(req.description());
                t.setCompleted(req.completed());
                var saved = todoRepository.save(t);
                return ResponseEntity.ok(saved);                      // ResponseEntity<Todo>
            })
            .orElseGet(() -> ResponseEntity.status(404).build());     // ResponseEntity<Todo> (no body)
    }


    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@AuthenticationPrincipal UserDetails principal,
                                    @PathVariable Long id) {
        User u = userRepository.findByUsername(principal.getUsername()).orElseThrow();
        return todoRepository.findById(id)
                .filter(t -> t.getUser().getId().equals(u.getId()))
                .map(t -> {
                    todoRepository.delete(t);
                    return ResponseEntity.noContent().build();
                })
                .orElse(ResponseEntity.status(404).body("Todo not found or not owned by user"));
    }
}
