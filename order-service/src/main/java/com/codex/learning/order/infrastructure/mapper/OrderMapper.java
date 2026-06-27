package com.codex.learning.order.infrastructure.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.codex.learning.order.domain.OrderEntity;
import com.codex.learning.order.domain.OrderStatus;
import java.time.LocalDateTime;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Update;

public interface OrderMapper extends BaseMapper<OrderEntity> {

    @Update("""
            UPDATE orders
            SET status = #{targetStatus},
                updated_at = #{updatedAt}
            WHERE id = #{orderId}
              AND status = #{currentStatus}
            """)
    int updateStatusByIdAndStatus(@Param("orderId") Long orderId,
                                  @Param("currentStatus") OrderStatus currentStatus,
                                  @Param("targetStatus") OrderStatus targetStatus,
                                  @Param("updatedAt") LocalDateTime updatedAt);
}
