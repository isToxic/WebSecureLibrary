package is.toxic.dao;

import is.toxic.entity.AppRole;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AppRoleDAO extends CrudRepository<AppRole, Long> {

    @Query("Select ur.appRole.roleName from UserRole ur where ur.appUser.userId = :userId")
    List<String> getRoleNames(@Param("userId") Long userId);
}

