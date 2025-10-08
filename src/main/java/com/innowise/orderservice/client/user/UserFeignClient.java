package com.innowise.orderservice.client.user;

import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "user-service", url = "${user.service.url}")
public interface UserFeignClient {

    @GetMapping("/api/v1/users/{userId}")
    UserResponse getUserById(@PathVariable UUID userId);

}
