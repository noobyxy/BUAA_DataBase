package com.yxy.market1.mapper;

import com.yxy.market1.entity.Favorite;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FavoriteMapper extends JpaRepository<Favorite, Integer> {
//    @Query(value = "select p from favorite p where p.userid=?1 and p.productid=?2")
    List<Favorite> findFavoriteByUserIdAndProductId(Integer userId, Integer productId);

    @Query(value = "select f.productId from Favorite f where f.userId=:userid")
    List<Integer> findProductIdByUserId(@Param("userid") Integer userId);
}
