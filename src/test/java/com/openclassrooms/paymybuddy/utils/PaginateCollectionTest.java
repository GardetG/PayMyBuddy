package com.openclassrooms.paymybuddy.utils;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@SpringBootTest
class PaginateCollectionTest {

  private Collection<String> collection;

  @BeforeEach
  void setUp() {
    collection = List.of("a","b","c","d","e","f","g");
  }

  @DisplayName("Paginate a collection should return correct element of the requested page")
  @Test
  void paginateTest() {
    Pageable pageable = PageRequest.of(1,3);

    Page<String> page = PaginateCollection.paginate(collection, pageable);

    assertThat(page.getContent()).containsExactly("d","e","f");
    assertThat(page.getTotalPages()).isEqualTo(3);
  }

  @DisplayName("Paginate a collection should return correct element of the last incomplete page")
  @Test
  void paginateWithLastPageNotCompleteTest() {
    Pageable pageable = PageRequest.of(2,3);

    Page<String> page = PaginateCollection.paginate(collection, pageable);

    assertThat(page.getContent()).containsExactly("g");
    assertThat(page.getTotalPages()).isEqualTo(3);
  }

  @DisplayName("Paginate a collection With unpaged should return a page with all the elements")
  @Test
  void paginateWithUnpagedTest() {
    Pageable pageable = Pageable.unpaged();

    Page<String> page = PaginateCollection.paginate(collection, pageable);

    assertThat(page.getContent()).containsExactlyElementsOf(collection);
    assertThat(page.getTotalPages()).isEqualTo(1);
  }

  @DisplayName("Paginate a collection with requested page above last page should return an empty page")
  @Test
  void paginateWithPageAboveMaxPageTest() {
    Pageable pageable = PageRequest.of(9,2);

    Page<String> page = PaginateCollection.paginate(collection, pageable);

    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalPages()).isEqualTo(4);
  }

  @DisplayName("Paginate an empty collection should return an empty page")
  @Test
  void paginateEmptyCollectionTest() {
    Pageable pageable = PageRequest.of(1,3);

    Page<String> page = PaginateCollection.paginate(Collections.emptyList(), pageable);

    assertThat(page.getContent()).isEmpty();
    assertThat(page.getTotalPages()).isZero();
  }

}
