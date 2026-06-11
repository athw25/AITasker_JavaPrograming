package mapper; // Đảm bảo trùng với cấu trúc thư mục của bạn hiện tại

import java.util.List;
import java.util.stream.Collectors;

/**
 * Giao diện cấu trúc (Interface) tổng quát cho việc chuyển đổi dữ liệu giữa Entity và DTO.
 * * @param <E> Đại diện cho lớp Entity (Database object)
 * @param <D> Đại diện cho lớp DTO (Data Transfer Object)
 */
public interface GenericMapper<E, D> {

    /**
     * Chuyển đổi từ một đối tượng Entity sang DTO để gửi về cho Client/Frontend.
     */
    D toDto(E entity);

    /**
     * Chuyển đổi ngược từ dữ liệu DTO người dùng gửi lên thành đối tượng Entity để lưu vào Database.
     */
    E toEntity(D dto);

    /**
     * Chuyển đổi nhanh cả danh sách (List) từ Entity sang DTO.
     */
    default List<D> toDtoList(List<E> entityList) {
        if (entityList == null) {
            return null;
        }
        return entityList.stream()
                         .map(this::toDto)
                         .collect(Collectors.toList());
    }

    /**
     * Chuyển đổi nhanh cả danh sách (List) từ DTO sang Entity.
     */
    default List<E> toEntityList(List<D> dtoList) {
        if (dtoList == null) {
            return null;
        }
        return dtoList.stream()
                         .map(this::toEntity)
                         .collect(Collectors.toList());
    }
}