import ch.salt.generation.model.PaymentDto;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * This class returns a list of records.
 */
public class PageAnswer<T> {
    private final List<T> records;
    private final Integer offset;
    private final Integer total;
    private       String  status = "success";

    public PageAnswer(final List<T> data, final Integer offset, final Integer totalCount) {
        this.records = data;
        this.offset  = offset;
        this.total   = totalCount;
    }

    public static <T> PageAnswer<T> of(final List<T> records) {
        return new PageAnswer<T>(records, 0, records == null ? 0 : records.size());
    }

    public static <T> PageAnswer<T> of(final List<T> records, final Integer offset, final Integer totalCount) {
        return new PageAnswer<T>(records, offset, totalCount);
    }

    public static <T> PageAnswer<T> of(final Page<T> records, final Integer offset, final Integer totalCount) {
        return new PageAnswer<T>(records.getContent(), offset, totalCount);
    }

    public static <T> PageAnswer<T> of(Page<T> page) {
        return of(page.getContent(), page.getNumber() * page.getSize(), (int) page.getTotalElements());
    }

    public String getStatus() {
        return this.status;
    }

    public void setStatus(final String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "PageAnswer{" +
                "records=" + this.records +
                ", offset=" + this.offset +
                ", total=" + this.total +
                ", status='" + this.status + '\'' +
                '}';
    }

    public List<T> getRecords() {
        return this.records;
    }

    public Integer getOffset() {
        return this.offset;
    }

    public Integer getTotal() {
        return this.total;
    }
}
