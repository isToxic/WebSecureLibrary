package is.toxic.dao;

import is.toxic.entity.AppUser;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserDAO extends CrudRepository<AppUser, Long> {

    @Query("Select e from AppUser e Where e.userName = :userName ")
    AppUser findUserAccount(@Param("userName") String userName);
}
