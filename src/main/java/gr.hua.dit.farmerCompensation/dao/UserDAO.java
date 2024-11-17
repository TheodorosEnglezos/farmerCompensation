package gr.hua.dit.farmerCompensation.dao;

import gr.hua.dit.farmerCompensation.entity.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserDAO {
    //get user via user id
    public User getUserProfile(Integer user_id);

    //get user id via username
    Integer getUserId(String username);

    //save user
    public void saveUser(User user);
}
