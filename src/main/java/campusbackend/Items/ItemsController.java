package campusbackend.Items;

import campusbackend.auth.Users;
import campusbackend.dto.CreateItemRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/items")
public class ItemsController {

    private final ItemsService itemsService;
    private final AuthService authService;

    public ItemsController(ItemsService itemsService, AuthService authService) {
        this.itemsService = itemsService;
        this.authService = authService;
    }


    @PostMapping("/create")
    public ResponseEntity<Items> createItem(@RequestBody CreateItemRequest createItemRequest, @RequestHeader("Authorization") String token) {
        Users currentUser = authService.getCurrentUser(token);

        Items item = itemsService.addItems(
                createItemRequest.getIdNumber(),
                createItemRequest.getStudentName(),
                currentUser
        );

        return ResponseEntity.ok(item);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteItem(
            @PathVariable Long id,
            @RequestHeader("Authorization") String token
    ) {
        Users currentUser = authService.getCurrentUser(token);
        itemsService.deleteItem(id, currentUser);
        return ResponseEntity.ok("Item deleted");
    }

    @GetMapping("/my-items")
    public ResponseEntity<List<Items>> viewMyItems(@RequestHeader("Authorization") String token) {
        Users currentUser = authService.getCurrentUser(token);
        List<Items> myItems = itemsService.viewMyItems(currentUser);
        return ResponseEntity.ok(myItems);
    }
    @GetMapping("view-all")
    public ResponseEntity<List<Items>> viewAll(){
        List<Items> items = itemsService.viewPosts();
        return ResponseEntity.ok(items);
    }
    @GetMapping("/search")
    public ResponseEntity<List<Items>> search(
            @RequestParam String studentName,
            @RequestParam String studentId) {

        List<Items> results = itemsService.searchItems(studentName, studentId);
        return ResponseEntity.ok(results);
    }




}



