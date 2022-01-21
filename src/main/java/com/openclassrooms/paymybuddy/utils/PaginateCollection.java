package com.openclassrooms.paymybuddy.utils;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

/**
 * Utility class to paginate a collection.
 */
public class PaginateCollection {

  private PaginateCollection() {
    throw new IllegalStateException("Utility class");
  }

  /**
   * Paginate a collection according to the Pageable provided. This method doesn't handle the
   * sorting of the page.
   *
   * @param collection to paginate
   * @param pageable of the requested page
   * @param <T> Type of the collection to paginate
   * @return Page of the collection
   */
  public static <T> Page<T> paginate(Collection<T> collection, Pageable pageable) {
    Stream<T> pageStream = collection.stream();

    if (pageable.isPaged()) {
      int pageSize = pageable.getPageSize();
      int skipCount = pageable.getPageNumber() * pageSize;
      pageStream = pageStream
          .skip(skipCount)
          .limit(pageSize);
    }

    List<T> pageList = pageStream.collect(Collectors.toList());
    return new PageImpl<>(pageList, pageable, collection.size());
  }

}
