package io.github.liruohrh.apiplatform.model.resp;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.github.liruohrh.apiplatform.apicommon.error.Resp;
import java.io.Serializable;
import java.util.Collections;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class PageResp<T> implements Serializable {

  private List<T> data;
  private long total;
  private long current;
  private long pages;
  private long size;
  private static final long serialVersionUID = 1L;

  public static <E> PageResp<E> one(E data) {
    return new PageResp<>(Collections.singletonList(data), 1, 1, 1, 1);
  }

  public static <E> Resp<PageResp<E>> empty() {
    return Resp.ok(new PageResp<>(
            Collections.emptyList(),
            0,
            1,
            0,
            0
        )
    );
  }

  public static <E> Resp<PageResp<E>> empty(Integer current, Integer size) {
    return Resp.ok(new PageResp<>(
        Collections.emptyList(),
        0,
        current,
        0,
        size
        )
    );
  }

  public static <E> Resp<PageResp<E>> empty(Page<?> page) {
    return Resp.ok(new PageResp<>(
        Collections.emptyList(),
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize())
    );
  }

  public static <E> Resp<PageResp<E>> of(List<E> records, Page<?> page) {
    return Resp.ok(new PageResp<>(
        records,
        page.getTotal(),
        page.getCurrent(),
        page.getPages(),
        page.getSize())
    );
  }
}
