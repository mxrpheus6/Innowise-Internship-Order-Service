package com.innowise.orderservice.client.user;

import com.innowise.orderservice.configuration.FeignClientConfiguration;
import java.util.List;
import java.util.UUID;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(
        name = "user-service",
        url = "${user.service.url}",
        configuration = FeignClientConfiguration.class)
public interface UserFeignClient {

    @GetMapping(value = "/api/v1/users/{userId}")
    UserResponse getUserById(@PathVariable UUID userId);

    @GetMapping(value = "/api/v1/users/batch")
    List<UserResponse> getUsersByIds(@RequestParam List<UUID> ids);

}
