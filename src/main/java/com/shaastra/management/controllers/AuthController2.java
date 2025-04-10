//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.BadCredentialsException;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.core.userdetails.UserDetailsService;
//import org.springframework.web.bind.annotation.PostMapping;
//import org.springframework.web.bind.annotation.RequestBody;
//import org.springframework.web.bind.annotation.RequestMapping;
//import org.springframework.web.bind.annotation.RestController;
//
//import com.shaastra.management.config.JwtUtil;
//import com.shaastra.management.resource_representation.AuthRequest;
//import com.shaastra.management.resource_representation.AuthResponse;
//
//@RestController
//@RequestMapping("/auth")
//public class AuthController2 {
//
//    @Autowired
//    private AuthenticationManager authenticationManager;
//
//    @Autowired
//    private UserDetailsService userDetailsService;
//
//    @Autowired
//    private JwtUtil jwtUtil;
//
//    @PostMapping("/admin/login")
//    public ResponseEntity<AuthResponse> adminLogin(@RequestBody AuthRequest authRequest) {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new AuthResponse(null, "Incorrect username or password"));
//        }
//        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
//        // Ensure the user has admin authority (this check is still good to have)
//        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_ADMIN"))) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new AuthResponse(null, "Access denied: not an admin"));
//        }
//        // Generate token with username and authorities
//        String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
//        return ResponseEntity.ok(new AuthResponse(jwt, "Successful login"));
//    }
//
//    // Contest Participant (Student) login endpoint
//    @PostMapping("/participant/login")
//    public ResponseEntity<AuthResponse> participantLogin(@RequestBody AuthRequest authRequest) {
//        try {
//            authenticationManager.authenticate(
//                    new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
//        } catch (BadCredentialsException e) {
//            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
//                    .body(new AuthResponse(null, "Incorrect username or password"));
//        }
//        UserDetails userDetails = userDetailsService.loadUserByUsername(authRequest.getUsername());
//        // Ensure the user has participant authority (this check is still good to have)
//        if (userDetails.getAuthorities().stream().noneMatch(a -> a.getAuthority().equals("ROLE_PARTICIPANT"))) {
//            return ResponseEntity.status(HttpStatus.FORBIDDEN)
//                    .body(new AuthResponse(null, "Access denied: not a contest participant"));
//        }
//        // Generate token with username and authorities
//        String jwt = jwtUtil.generateToken(userDetails.getUsername(), userDetails.getAuthorities());
//        return ResponseEntity.ok(new AuthResponse(jwt, "Successful login"));
//    }
//}