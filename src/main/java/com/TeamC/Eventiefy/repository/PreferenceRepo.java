package com.TeamC.Eventiefy.repository;

import com.TeamC.Eventiefy.entity.Preference;
import com.TeamC.Eventiefy.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PreferenceRepo extends JpaRepository<Preference, Long> {
    List<Preference> findByUser(User user);
}
