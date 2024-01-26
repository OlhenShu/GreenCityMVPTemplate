package greencity.service;

import greencity.entity.Habit;
import greencity.entity.ShoppingListItem;
import greencity.repository.HabitRepo;
import greencity.repository.ShoppingListItemRepo;
import greencity.service.HabitShoppingListItemServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.mockito.internal.verification.VerificationModeFactory.times;

@ExtendWith(MockitoExtension.class)
class HabitShoppingListItemServiceImplTest {


    @Mock
    private ShoppingListItemRepo shoppingListItemRepo;

    @Mock
    private HabitRepo habitRepo;

    @InjectMocks
    private HabitShoppingListItemServiceImpl habitShoppingListItemService;

    @Test
    void testUnlinkShoppingListItems() {
        // Arrange
        Long habitId = 1L;
        List<Long> shopIds = Arrays.asList(2L, 3L);

        Habit habit = new Habit();
        habit.setId(habitId);

        ShoppingListItem shoppingListItem1 = new ShoppingListItem();
        shoppingListItem1.setId(2L);

        ShoppingListItem shoppingListItem2 = new ShoppingListItem();
        shoppingListItem2.setId(3L);

        habit.setShoppingListItems(new HashSet<>(Arrays.asList(shoppingListItem1, shoppingListItem2)));

        when(habitRepo.findById(habitId)).thenReturn(Optional.of(habit));
        when(shoppingListItemRepo.findById(2L)).thenReturn(Optional.of(shoppingListItem1));
        when(shoppingListItemRepo.findById(3L)).thenReturn(Optional.of(shoppingListItem2));
        // Act
        habitShoppingListItemService.unlinkShoppingListItems(shopIds, habitId);
        // Assert
        verify(habitRepo, times(1)).findById(habitId);
        verify(shoppingListItemRepo, times(2)).findById(anyLong());
        verify(habitRepo, times(1)).save(habit);

        assert !habit.getShoppingListItems().contains(shoppingListItem1);
        assert !habit.getShoppingListItems().contains(shoppingListItem2);
    }
}