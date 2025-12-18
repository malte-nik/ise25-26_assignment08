package de.seuhd.campuscoffee.domain.implementation;

import de.seuhd.campuscoffee.domain.exceptions.NotFoundException;
import de.seuhd.campuscoffee.domain.model.objects.Review;
import de.seuhd.campuscoffee.domain.ports.data.CrudDataService;
import de.seuhd.campuscoffee.domain.tests.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

class CrudServiceTest {

    @Mock
    private CrudDataService<Review, Long> dataService;

    private CrudServiceImpl<Review, Long> crudService;

    private static class TestCrudService extends CrudServiceImpl<Review, Long> {
        private final CrudDataService<Review, Long> dataService;

        TestCrudService(CrudDataService<Review, Long> dataService) {
            super(Review.class);
            this.dataService = dataService;
        }

        @Override
        protected CrudDataService<Review, Long> dataService() {
            return dataService;
        }
    }

    @BeforeEach
    void beforeEach() {
        MockitoAnnotations.openMocks(this);
        crudService = new TestCrudService(dataService);
    }

    @Test
    void clearDelegatesToDataService() {
        // when
        crudService.clear();

        // then
        verify(dataService).clear();
    }

    @Test
    void getAllReturnsEntities() {
        // given
        List<Review> reviews = TestFixtures.getReviewFixtures();
        when(dataService.getAll()).thenReturn(reviews);

        // when
        List<Review> result = crudService.getAll();

        // then
        verify(dataService).getAll();
        assertThat(result).isEqualTo(reviews);
    }

    @Test
    void getAllReturnsEmptyList() {
        // given
        when(dataService.getAll()).thenReturn(Collections.emptyList());

        // when
        List<Review> result = crudService.getAll();

        // then
        verify(dataService).getAll();
        assertThat(result).isEmpty();
    }

    @Test
    void getByIdReturnsEntity() {
        // given
        Review review = TestFixtures.getReviewFixtures().getFirst();
        when(dataService.getById(review.getId())).thenReturn(review);

        // when
        Review result = crudService.getById(review.getId());

        // then
        verify(dataService).getById(review.getId());
        assertThat(result).isEqualTo(review);
    }

    @Test
    void getByIdThrowsIfNotFound() {
        // given
        Long id = 999L;
        when(dataService.getById(id))
                .thenThrow(new NotFoundException(Review.class, id));

        // when, then
        assertThrows(NotFoundException.class, () -> crudService.getById(id));
        verify(dataService).getById(id);
    }


    @Test
    void upsertDelegatesToDataService() {
        // given
        Review review = TestFixtures.getReviewFixtures().getFirst();
        when(dataService.upsert(review)).thenReturn(review);

        // when
        Review result = crudService.upsert(review);

        // then
        verify(dataService).upsert(review);
        assertThat(result).isEqualTo(review);
    }

    @Test
    void upsertThrowsIfEntityIsNull() {
        // when, then
        assertThrows(NullPointerException.class, () -> crudService.upsert(null));
    }

    @Test
    void deleteDelegatesToDataService() {
        // given
        Long id = 1L;
        doNothing().when(dataService).delete(id);

        // when
        crudService.delete(id);

        // then
        verify(dataService).delete(id);
    }

    @Test
    void deleteThrowsIfNotFound() {
        // given
        Long id = 42L;
        doThrow(new NotFoundException(Review.class, id)).when(dataService).delete(id);

        // when, then
        assertThrows(NotFoundException.class, () -> crudService.delete(id));
        verify(dataService).delete(id);
    }
}