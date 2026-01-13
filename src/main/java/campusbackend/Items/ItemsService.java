package campusbackend.Items;

import campusbackend.auth.Users;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ItemsService {

    private final ItemsRepository itemsRepository;

    public ItemsService(ItemsRepository itemsRepository) {
        this.itemsRepository = itemsRepository;
    }

    public Items addItems(String studentId, String studentName, Users currentUser) {
        Items item = new Items(currentUser);
        item.setstudentName(studentName);
        item.setIdNumber(studentId);
        return itemsRepository.save(item);  // Add return
    }
    public void deleteItem(Long itemId,Users currentUser ){
        Items item = itemsRepository.findById(itemId).orElseThrow(()-> new RuntimeException("item not found"));
        if(!item.getUser().getId().equals(currentUser.getId())){
            throw new RuntimeException("You can only delete your own posts");
        }
        itemsRepository.delete(item);
    }
    public List<Items>viewMyItems(Users user){
      return itemsRepository.findByUser(user);

    }
    public List<Items> viewPosts(){
        return itemsRepository.findAllByOrderByCreatedAtDesc();
    }
    public List<Items> searchItem(String studentName,String studentId){
        return itemsRepository.findByStudentNameIgnoreCaseOrIdNumberIgnoreCase(studentName,studentId);

    }
}
