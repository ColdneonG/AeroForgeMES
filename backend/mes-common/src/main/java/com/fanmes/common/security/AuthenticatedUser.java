package com.fanmes.common.security;

import java.util.Set;

public record AuthenticatedUser(Long userId, String username, Set<String> roles, Set<String> permissions) {}
