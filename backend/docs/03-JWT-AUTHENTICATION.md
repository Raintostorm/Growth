# JWT Authentication - Xác thực và phân quyền

## 🎯 Mục tiêu

Hiểu và thực hiện JWT (JSON Web Token) authentication trong Spring Boot application.

## 🔐 JWT là gì?

JWT (JSON Web Token) là một chuẩn mở (RFC 7519) để truyền thông tin an toàn giữa các bên dưới dạng JSON object. JWT được ký số để có thể xác minh và tin cậy.

### Cấu trúc JWT

```
eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiIxMjM0NTY3ODkwIiwibmFtZSI6IkpvaG4gRG9lIiwiaWF0IjoxNTE2MjM5MDIyfQ.SflKxwRJSMeKKF2QT4fwpMeJf36POk6yJV_adQssw5c
```

**3 phần được phân cách bởi dấu chấm (.)**:

1. **Header**: Thuật toán mã hóa và loại token
2. **Payload**: Thông tin (claims) được lưu trữ
3. **Signature**: Chữ ký để xác minh tính toàn vẹn

## 🏗️ JWT Implementation trong Spring Boot

### 1. Dependencies

```xml
<!-- JWT Dependencies -->
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-api</artifactId>
    <version>0.11.5</version>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-impl</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
<dependency>
    <groupId>io.jsonwebtoken</groupId>
    <artifactId>jjwt-jackson</artifactId>
    <version>0.11.5</version>
    <scope>runtime</scope>
</dependency>
```

### 2. JWT Configuration

```java
@Component
public class JwtTokenProvider {
    
    private static final String SECRET_KEY = "mySecretKey";
    private static final long EXPIRATION_TIME = 86400000; // 24 hours
    
    public String generateToken(UserDetails userDetails) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("username", userDetails.getUsername());
        claims.put("authorities", userDetails.getAuthorities());
        
        return createToken(claims, userDetails.getUsername());
    }
    
    private String createToken(Map<String, Object> claims, String subject) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + EXPIRATION_TIME))
                .signWith(SignatureAlgorithm.HS256, SECRET_KEY)
                .compact();
    }
    
    public Boolean validateToken(String token, UserDetails userDetails) {
        final String username = getUsernameFromToken(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }
    
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }
    
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }
    
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }
    
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(SECRET_KEY)
                .parseClaimsJws(token)
                .getBody();
    }
    
    private Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }
}
```

### 3. JWT Authentication Filter

```java
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserDetailsService userDetailsService;
    
    @Override
    protected void doFilterInternal(HttpServletRequest request, 
                                  HttpServletResponse response, 
                                  FilterChain filterChain) throws ServletException, IOException {
        
        try {
            String jwt = getJwtFromRequest(request);
            
            if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
                String username = jwtTokenProvider.getUsernameFromToken(jwt);
                
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);
                UsernamePasswordAuthenticationToken authentication = 
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception ex) {
            logger.error("Could not set user authentication in security context", ex);
        }
        
        filterChain.doFilter(request, response);
    }
    
    private String getJwtFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}
```

## 🔒 Spring Security Configuration

### 1. Security Configuration

```java
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class SecurityConfig {
    
    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
    
    @Autowired
    private JwtAuthenticationFilter jwtAuthenticationFilter;
    
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
    
    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }
    
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.cors().and().csrf().disable()
            .exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint).and()
            .sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS).and()
            .authorizeHttpRequests(authz -> authz
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers("/api/public/**").permitAll()
                .requestMatchers("/h2-console/**").permitAll()
                .requestMatchers("/actuator/**").permitAll()
                .anyRequest().authenticated()
            );
        
        http.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        
        return http.build();
    }
    
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("*"));
        configuration.setAllowCredentials(true);
        
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
```

### 2. Authentication Entry Point

```java
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {
    
    @Override
    public void commence(HttpServletRequest request, 
                        HttpServletResponse response,
                        AuthenticationException authException) throws IOException, ServletException {
        
        response.setContentType("application/json");
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        
        Map<String, Object> body = new HashMap<>();
        body.put("timestamp", new Date());
        body.put("status", HttpServletResponse.SC_UNAUTHORIZED);
        body.put("error", "Unauthorized");
        body.put("message", authException.getMessage());
        body.put("path", request.getServletPath());
        
        ObjectMapper mapper = new ObjectMapper();
        mapper.writeValue(response.getOutputStream(), body);
    }
}
```

## 🎯 Authentication Controller

### 1. Login Request/Response DTOs

```java
// LoginRequest.java
public class LoginRequest {
    @NotBlank
    private String username;
    
    @NotBlank
    private String password;
    
    // Constructors, getters, setters
}

// AuthResponse.java
public class AuthResponse {
    private String accessToken;
    private String tokenType = "Bearer";
    private Long expiresIn;
    private UserDto user;
    
    // Constructors, getters, setters
}

// UserDto.java
public class UserDto {
    private Long id;
    private String username;
    private String email;
    private String displayName;
    private Role role;
    
    // Constructors, getters, setters
}
```

### 2. Authentication Controller

```java
@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {
    
    @Autowired
    private AuthenticationManager authenticationManager;
    
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    
    @Autowired
    private UserService userService;
    
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = jwtTokenProvider.generateToken((UserDetails) authentication.getPrincipal());
        
        User user = userService.findByUsername(loginRequest.getUsername());
        UserDto userDto = convertToDto(user);
        
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(jwt);
        authResponse.setExpiresIn(86400000L); // 24 hours
        authResponse.setUser(userDto);
        
        return ResponseEntity.ok(authResponse);
    }
    
    @PostMapping("/register")
    public ResponseEntity<UserDto> register(@Valid @RequestBody RegisterRequest registerRequest) {
        
        if (userService.existsByUsername(registerRequest.getUsername())) {
            throw new UsernameAlreadyExistsException("Username is already taken!");
        }
        
        if (userService.existsByEmail(registerRequest.getEmail())) {
            throw new EmailAlreadyExistsException("Email is already in use!");
        }
        
        User user = new User();
        user.setUsername(registerRequest.getUsername());
        user.setEmail(registerRequest.getEmail());
        user.setPassword(registerRequest.getPassword());
        user.setDisplayName(registerRequest.getDisplayName());
        
        User savedUser = userService.save(user);
        UserDto userDto = convertToDto(savedUser);
        
        return ResponseEntity.status(HttpStatus.CREATED).body(userDto);
    }
    
    @GetMapping("/me")
    @PreAuthorize("hasRole('USER')")
    public ResponseEntity<UserDto> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();
        
        User user = userService.findByUsername(username);
        UserDto userDto = convertToDto(user);
        
        return ResponseEntity.ok(userDto);
    }
    
    private UserDto convertToDto(User user) {
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setUsername(user.getUsername());
        userDto.setEmail(user.getEmail());
        userDto.setDisplayName(user.getDisplayName());
        userDto.setRole(user.getRole());
        return userDto;
    }
}
```

## 🔐 User Details Service

```java
@Service
public class CustomUserDetailsService implements UserDetailsService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
            .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        
        return UserPrincipal.create(user);
    }
    
    public UserDetails loadUserById(Long id) {
        User user = userRepository.findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("User", "id", id));
        
        return UserPrincipal.create(user);
    }
}
```

### User Principal

```java
public class UserPrincipal implements UserDetails {
    
    private Long id;
    private String username;
    private String email;
    private String password;
    private Collection<? extends GrantedAuthority> authorities;
    
    public UserPrincipal(Long id, String username, String email, String password, 
                        Collection<? extends GrantedAuthority> authorities) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
        this.authorities = authorities;
    }
    
    public static UserPrincipal create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
            new SimpleGrantedAuthority("ROLE_" + user.getRole().name())
        );
        
        return new UserPrincipal(
            user.getId(),
            user.getUsername(),
            user.getEmail(),
            user.getPassword(),
            authorities
        );
    }
    
    // UserDetails implementation
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
    
    @Override
    public String getPassword() {
        return password;
    }
    
    @Override
    public String getUsername() {
        return username;
    }
    
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }
    
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }
    
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
    
    @Override
    public boolean isEnabled() {
        return true;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getEmail() {
        return email;
    }
}
```

## 🧪 Testing JWT Authentication

### 1. Unit Tests

```java
@ExtendWith(MockitoExtension.class)
class JwtTokenProviderTest {
    
    @InjectMocks
    private JwtTokenProvider jwtTokenProvider;
    
    @Test
    void shouldGenerateToken() {
        // Given
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        
        // When
        String token = jwtTokenProvider.generateToken(userDetails);
        
        // Then
        assertThat(token).isNotNull();
        assertThat(jwtTokenProvider.getUsernameFromToken(token)).isEqualTo("testuser");
    }
    
    @Test
    void shouldValidateToken() {
        // Given
        UserDetails userDetails = mock(UserDetails.class);
        when(userDetails.getUsername()).thenReturn("testuser");
        
        String token = jwtTokenProvider.generateToken(userDetails);
        
        // When
        Boolean isValid = jwtTokenProvider.validateToken(token, userDetails);
        
        // Then
        assertThat(isValid).isTrue();
    }
}
```

### 2. Integration Tests

```java
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureTestDatabase
class AuthControllerIntegrationTest {
    
    @Autowired
    private TestRestTemplate restTemplate;
    
    @Autowired
    private UserRepository userRepository;
    
    @Test
    void shouldLoginSuccessfully() {
        // Given
        User user = new User("testuser", "test@example.com", "password");
        userRepository.save(user);
        
        LoginRequest loginRequest = new LoginRequest("testuser", "password");
        
        // When
        ResponseEntity<AuthResponse> response = restTemplate.postForEntity(
            "/api/auth/login", loginRequest, AuthResponse.class);
        
        // Then
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getAccessToken()).isNotNull();
        assertThat(response.getBody().getUser().getUsername()).isEqualTo("testuser");
    }
}
```

## 🔧 Configuration Properties

```yaml
# application.yml
jwt:
  secret: ${JWT_SECRET:mySecretKey}
  expiration: 86400000 # 24 hours in milliseconds
  header: Authorization
  prefix: "Bearer "
```

```java
@ConfigurationProperties(prefix = "jwt")
@Component
public class JwtProperties {
    private String secret;
    private long expiration;
    private String header;
    private String prefix;
    
    // Getters and setters
}
```

## 🎯 Best Practices

### 1. Token Security

```java
// Sử dụng strong secret key
private static final String SECRET_KEY = "myVeryLongAndSecureSecretKeyThatIsAtLeast256BitsLong";

// Set appropriate expiration time
private static final long EXPIRATION_TIME = 86400000; // 24 hours
```

### 2. Error Handling

```java
@ControllerAdvice
public class AuthExceptionHandler {
    
    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUsernameNotFound(UsernameNotFoundException ex) {
        ErrorResponse error = new ErrorResponse("USER_NOT_FOUND", ex.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
    
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(BadCredentialsException ex) {
        ErrorResponse error = new ErrorResponse("INVALID_CREDENTIALS", "Invalid username or password");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }
}
```

### 3. Token Refresh

```java
@PostMapping("/refresh")
public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String authHeader) {
    String token = authHeader.substring(7); // Remove "Bearer "
    
    if (jwtTokenProvider.canTokenBeRefreshed(token)) {
        String refreshedToken = jwtTokenProvider.refreshToken(token);
        
        AuthResponse authResponse = new AuthResponse();
        authResponse.setAccessToken(refreshedToken);
        authResponse.setExpiresIn(86400000L);
        
        return ResponseEntity.ok(authResponse);
    }
    
    return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
}
```

## 📚 Tài liệu tham khảo

- [JWT.io](https://jwt.io/)
- [Spring Security JWT](https://spring.io/guides/topicals/spring-security-architecture/)
- [RFC 7519 - JSON Web Token](https://tools.ietf.org/html/rfc7519)

## 🎯 Bài tập thực hành

1. **Tạo JWT token** với custom claims
2. **Implement token refresh** mechanism
3. **Add role-based authorization** với `@PreAuthorize`
4. **Test JWT authentication** với Postman
5. **Implement logout** với token blacklisting

---

**Lưu ý**: JWT tokens không thể bị revoke trước khi expire, cần implement token blacklisting cho logout functionality!
