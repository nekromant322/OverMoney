package com.override.orchestrator_service.repository;

import com.override.dto.AnalyticsDataDTO;
import com.override.dto.constants.Type;
import com.override.orchestrator_service.model.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {
    @Query("SELECT c FROM Category c WHERE c.account.id = :id")
    List<Category> findAllByUserId(@Param("id") String accountId);

    @Query("SELECT c FROM Category c WHERE c.account.id = :id AND c.type = :type")
    List<Category> findAllByTypeAndAccId(@Param("id") Long accountId, @Param("type") Type type);

    @Query("SELECT new com.override.dto.AnalyticsDataDTO(c.id, c.name, (SUM(t.amount)) / " +
            "(EXTRACT(MONTH FROM MAX(t.date)) - EXTRACT(MONTH FROM MIN(t.date)) + " +
            "(((EXTRACT(YEAR FROM MAX(t.date)) - EXTRACT(YEAR FROM MIN(t.date)))) * 12) + 1))\n" +
            "FROM Category c \n" +
            "LEFT JOIN Transaction t ON t.category.id = c.id\n" +
            "WHERE c.account.id = :accId\n" +
            "AND c.type = :type\n" +
            "GROUP BY c.id")
    List<AnalyticsDataDTO> findMediumAmountOfAllCategoriesByAccIdAndType(@Param("accId") Long accId, @Param("type") Type type);

    @Modifying
    @Query("UPDATE Category c SET c.account.id = :newAccountId WHERE c.account.id = :oldAccountId")
    void updateAccountId(@Param("oldAccountId") Long oldAccountId, @Param("newAccountId") Long newAccountId);

    @Query("SELECT c FROM Category c WHERE c.account.id = :id AND c.name = :name")
    Category findCategoryByNameAndAccountId(@Param("id") Long accountId, @Param("name") String name);
}
