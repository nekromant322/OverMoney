package com.override.orchestrator_service.service;

import com.override.orchestrator_service.mapper.AccountMapper;
import com.override.orchestrator_service.model.Category;
import com.override.orchestrator_service.model.OverMoneyAccount;
import com.override.orchestrator_service.utils.TestFieldsUtil;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@ExtendWith(MockitoExtension.class)
public class AccountMapperTest {

    @InjectMocks
    private AccountMapper accountMapper;

    @Test
    public void accountMapperTest() {
        final Category category1 = TestFieldsUtil.generateTestCategory();
        final Category category2 = TestFieldsUtil.generateTestCategory();
        List<Category> testListCategory = List.of(category1, category2);
        System.out.println(Arrays.toString(testListCategory.toArray()));
        category2.setId(123L);
        category2.setName("транспорт");
        Set<Category> categorySet = new HashSet<>();
        categorySet.add(category1);
        categorySet.add(category2);
        final OverMoneyAccount account = TestFieldsUtil.generateTestAccount();
        account.setCategories(categorySet);
        Assertions.assertEquals(accountMapper.mapAccountToCategoryList(account).size(), testListCategory.size());
    }
}
