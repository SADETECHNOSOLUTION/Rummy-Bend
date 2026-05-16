package com.sadetech.user_info.controller;

import com.sadetech.user_info.dto.*;
import com.sadetech.user_info.model.*;
import com.sadetech.user_info.repository.AvatarRepository;
import com.sadetech.user_info.service.*;
import jakarta.servlet.http.HttpServletRequest;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.core.io.Resource;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/user")
public class UserManagementController {

    @Autowired
    private UsersManagementService usersManagementService;

    @Autowired
    private OurUserDetailsService ourUserDetailsService;

    @Autowired
    private JWTUtils jwtUtils;

    // @Autowired
    // private UserRepository userRepository;

    @Autowired
    private AvatarService avatarService;

    @Autowired
    private AvatarRepository avatarRepository;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private GoogleAuthService googleAuthService;

    @Autowired
    private WalletService walletService;

    @PostMapping("/google-register")
    public ResponseEntity<?> registerWithGoogle(@RequestBody OAuthRequest request) {
        ReqRes user = usersManagementService.checkUserAndRegisterWithOAuth(
                request.getIdToken());
        return ResponseEntity.ok(user);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> userLogin(@RequestParam(required = false) String email, @RequestParam(required = false) String phoneNumber, @RequestParam String password, HttpServletRequest request) {
        LoginResponse response = usersManagementService.loginWithPhoneNumberOrEmail(email, phoneNumber, password,request);
        return ResponseEntity.ok(response);
    }

@PostMapping("/refresh")
public ResponseEntity<ReqRes> refreshToken(@RequestBody ReqRes req) {
    // Basic null check to prevent NullPointerExceptions in the service
    if (req.getRefreshToken() == null || req.getRefreshToken().isEmpty()) {
        ReqRes errorResponse = new ReqRes();
        errorResponse.setStatusCode(400);
        errorResponse.setMessage("Refresh token is required.");
        return ResponseEntity.badRequest().body(errorResponse);
    }
    
    return ResponseEntity.ok(usersManagementService.refreshToken(req));
}
    @PostMapping("/forgot-password")
    public ResponseEntity<?> requestPasswordReset(@RequestBody Map<String, String> request) {
        String input = request.get("input"); // can be email or phone number
        String response = usersManagementService.requestPasswordReset(input);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(response));
    }

@PostMapping("/verify-otp")
public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request, HttpServletRequest request1) {
    String input = request.get("input");
    String otp = request.get("otp");

    // Change 'String response' to 'LoginResponse response'
    LoginResponse response = usersManagementService.verifyOtp(input, otp, request1);
    
    return ResponseEntity.ok(response);
}
    @PostMapping("/reset-password")
    public ResponseEntity<?> resetPassword(@RequestBody Map<String, String> request) {
        String input = request.get("input");
        String newPassword = request.get("newPassword");
        String confirmPassword = request.get("confirmPassword");

        String response = usersManagementService.resetPassword(input, newPassword, confirmPassword);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(response));
    }


    @GetMapping("/get-user/{playerId}")
    public ResponseEntity<?> getDetails(@PathVariable String playerId) {
        User user = usersManagementService.getUserById(playerId);
        return ResponseEntity.ok(user);
    }

    @PostMapping("/update-chips/{playerId}")
    public ResponseEntity<?> updateChips(@PathVariable String playerId, @RequestParam double chips) {

        User user = usersManagementService.updateChips(playerId, chips);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @PostMapping("/update-money/{playerId}")
    public ResponseEntity<?> updateInGameMoney(@PathVariable String playerId, @RequestParam double inGameMoney) {
        User user = usersManagementService.updateInGameMoney(playerId, inGameMoney);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @PutMapping("/update-cash-game-wallet/{playerId}")
    public ResponseEntity<?> updateCashGameWalletAndLoyaltyPoint(@PathVariable String playerId, @RequestParam double cashGameWallet) {
        User user = usersManagementService.updateCashGameWalletAndLoyaltyPoint(playerId, cashGameWallet);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse("Cash game wallet and Loyalty Point updated successfully for the player id " + user.getPlayerId()));
    }

    @GetMapping("/player-exist/{playerId}")
    public boolean findUserExistOrNot(@PathVariable String playerId) {
        return usersManagementService.existById(playerId);
    }

    @PostMapping("/update-winning-money/{playerId}")
    public ResponseEntity<?> updateWinningMoney(@PathVariable String playerId, @RequestParam double money) {
        User user = usersManagementService.updateWinningWallet(playerId, money);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @PatchMapping("/update-image/{playerId}")
    public ResponseEntity<?> updateImagePath(@PathVariable String playerId, @RequestParam String imagePath, @RequestHeader("Authorization") String authHeader) {
        User user = usersManagementService.updateImagePath(playerId, imagePath, authHeader);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadImage(@RequestParam("file") MultipartFile file, @RequestParam String name) throws IOException {
        Avatar avatar = avatarService.addAvatarToDB(name, file);
        return ResponseEntity.ok(avatar);
    }

    private String getFullUrl(String imagePath) {
        if (imagePath == null || imagePath.isEmpty()) {
            return null;
        }
        return "http://localhost:7070/api/user" + imagePath;
    }

    @GetMapping("/uploads/{fileName:.+}")
    public ResponseEntity<Resource> serveFile(@PathVariable String fileName) throws MalformedURLException {
        Path filePath = Paths.get("static/uploads/").resolve(fileName).normalize();
        Resource resource = new UrlResource(filePath.toUri());

        if (resource.exists()) {
            String contentType = determineContentType(fileName);

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(contentType))
                    .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + resource.getFilename() + "\"")
                    .body(resource);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    private String determineContentType(String fileName) {
        if (fileName.toLowerCase().endsWith(".mp4")) {
            return "video/mp4";
        } else if (fileName.toLowerCase().endsWith(".jpg") || fileName.toLowerCase().endsWith(".jpeg") || fileName.toLowerCase().endsWith(".webp")) {
            return "image/jpeg";
        } else if (fileName.toLowerCase().endsWith(".png")) {
            return "image/png";
        } else {
            return "application/octet-stream";
        }
    }

    @GetMapping("/get-all-avatar")
    public ResponseEntity<List<Avatar>> getAllAvatar() {
        List<Avatar> avatarList = avatarRepository.findAll();
        return ResponseEntity.status(HttpStatus.OK).body(avatarList);
    }

    @GetMapping("/get-avatar/{id}")
    public ResponseEntity<?> getAvatar(@PathVariable String id) {
        Avatar avatar = avatarService.getAvatar(id);
        return ResponseEntity.status(HttpStatus.OK).body(avatar);
    }

    @PostMapping("/request-money")
    public ResponseEntity<MoneyRequest> requestAmount(
            @RequestParam String requestPlayerId,
            @RequestParam String senderPlayerId,
            @RequestParam double amount,
            @RequestHeader("Authorization") String authHeader) {
        MoneyRequest moneyRequest = usersManagementService.requestAmountFromPlayer(requestPlayerId, senderPlayerId, amount, authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(moneyRequest);
    }

    @GetMapping("/request-details/{id}")
    public ResponseEntity<MoneyRequest> getMoneyRequestById(@PathVariable String id) {
        MoneyRequest moneyRequest = usersManagementService.getDetailsById(id);
        return ResponseEntity.ok(moneyRequest);
    }

    @PatchMapping("/{id}/update-status/money-request")
    public ResponseEntity<MoneyRequest> updateRequestStatus(
            @PathVariable String id,
            @RequestParam String status,
            @RequestHeader("Authorization") String authHeader) {
        MoneyRequest updatedRequest = usersManagementService.updateRequestStatus(id, status,authHeader);
        return ResponseEntity.ok(updatedRequest);
    }

    @PostMapping("/{id}/send-money/on-request")
    public ResponseEntity<ApiResponse> sendMoney(@PathVariable String id, @RequestHeader("Authorization") String authHeader) {
        String response = usersManagementService.sendMoneyForRequestedPlayer(id,authHeader);
        return ResponseEntity.ok(new ApiResponse(response));
    }

    @PostMapping("/send-money")
    public ResponseEntity<MoneyRequest> sendMoneyForPlayer(
            @RequestParam String playerId,
            @RequestParam String senderPlayerId,
            @RequestParam double amount,
            @RequestHeader("Authorization") String authHeader) {
        MoneyRequest moneyRequest = usersManagementService.sendMoneyForPlayer(playerId, senderPlayerId, amount, authHeader);
        return new ResponseEntity<>(moneyRequest, HttpStatus.OK);
    }

    @GetMapping("/get-request-details/{senderPlayerId}")
    public ResponseEntity<List<MoneyRequest>> getDetailsForPlayerId(@PathVariable String senderPlayerId, @RequestParam String requestSummaryStatus) {
        List<MoneyRequest> moneyRequests = usersManagementService.getRequestDetailsByPlayerId(senderPlayerId, requestSummaryStatus);
        return ResponseEntity.status(HttpStatus.OK).body(moneyRequests);
    }

    @PostMapping("/send-otp-mobile")
    public ResponseEntity<?> sendOtp(@RequestParam String phoneNumber) {
        Otp otp = usersManagementService.sendOtpToPhoneNumber(phoneNumber);
        OtpResponse otpResponse = modelMapper.map(otp,OtpResponse.class);
        return new ResponseEntity<>(otpResponse, HttpStatus.CREATED);
    }

    @PostMapping("/login/otp")
    public ResponseEntity<Map<String,String>> loginWithOtp(@RequestBody ReqRes loginRequest, HttpServletRequest request) {
        Map<String,String> response = usersManagementService.loginWithMobileOtp(loginRequest,request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/get-all-user")
    public ResponseEntity<List<User>> getAllUserDetails() {
        List<User> user = usersManagementService.getAllUser();
        return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PutMapping("/update-mission-wallet/{playerId}")
    public ResponseEntity<User> addMoneyFromMissionWalletToInGameWallet(@PathVariable String playerId, @RequestParam String missionId) {
        User user = usersManagementService.addMoneyFromMissionToDepositWallet(playerId, missionId);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(user);
    }

    @GetMapping("/generate-whatsapp-link")
    public ResponseEntity<Map<String,String>> generateWhatsAppReferral(@RequestParam String playerId) {
        Map<String,String> whatsappLink = usersManagementService.generateWhatsAppReferralLink(playerId);
        return ResponseEntity.ok(whatsappLink);
    }

    @PostMapping("/verify-otp-register")
    public ResponseEntity<LoginResponse> verifyOtpAndRegisterForPhoneNumber(
            @RequestBody ReqRes verificationRequest) {
        LoginResponse response = usersManagementService.verifyOtpAndRegisterForPhoneNumber(
                verificationRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/verify-otp-register-with-referral")
    public ResponseEntity<LoginResponse> verifyOtpAndRegisterForPhoneNumberWithReferral(
            @RequestBody ReqRes verificationRequest,
            @RequestParam(required = false, defaultValue = "false") boolean isReferral,
            @RequestParam(required = false) String referrerId) {
        LoginResponse response = usersManagementService.verifyOtpAndRegisterForPhoneNumberWithReferral(
                verificationRequest, isReferral, referrerId);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/register-mobile")
    public ResponseEntity<?> registerWithMobileNumber(@RequestBody MobileRegisterRequest request) {
        Otp response = usersManagementService.registerWithMobileNumber(
                request.getPhoneNumber());

        OtpResponse otpResponse = modelMapper.map(response, OtpResponse.class);
        return ResponseEntity.status(HttpStatus.CREATED).body(otpResponse);
    }

    @PutMapping("/{playerId}/update-profile")
    public ResponseEntity<ApiResponse> updateUserProfile(
            @PathVariable String playerId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String language,
            @RequestParam(required = false) String dateOfBirth,
            @RequestHeader("Authorization") String authHeader
    ) {

        String response = usersManagementService.updateProfile(playerId, name, address, gender, language,dateOfBirth,authHeader);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(response));
    }

    @PutMapping("/{playerId}/update-password")
    public ResponseEntity<ApiResponse> updateUserPassword(
            @PathVariable String playerId,
            @RequestParam(required = false) String oldPassword,
            @RequestParam String newPassword,
            @RequestParam String confirmPassword,
            @RequestHeader("Authorization") String authHeader) {

        User user = usersManagementService.updatePassword(playerId, oldPassword, newPassword, confirmPassword,authHeader);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse("Password updated successfully for the player id " + user.getPlayerId()));
    }

    @GetMapping("/referral-list/{referrerId}")
    public ResponseEntity<Map<String, Object>> getListOfReferredUser(@PathVariable String referrerId){
        Map<String, Object> userList = usersManagementService.getListOfUserByReferralId(referrerId);
        return ResponseEntity.status(HttpStatus.OK).body(userList);
    }

    @PutMapping("/update-referral")
    public ResponseEntity<ApiResponse> updateReferrerId(@RequestParam String playerId, @RequestParam String referredId, @RequestHeader("Authorization") String authHeader){
        String user = usersManagementService.updateReferredId(playerId,referredId,authHeader);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(user));
    }

    @GetMapping("/search-and-get-user")
    public ResponseEntity<User> getUserDetails(@RequestParam String idOrEmailOrPhone){
            User user = usersManagementService.getUserDetails(idOrEmailOrPhone);
            return ResponseEntity.status(HttpStatus.OK).body(user);
    }

    @PostMapping("/add-transaction")
    public ResponseEntity<ApiResponse> wallet (@RequestBody Wallet wallet){
        String response = walletService.settleAmount(wallet);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(response));
    }

    @GetMapping("/get-transaction")
    public ResponseEntity<List<Wallet>> getTransaction(@RequestParam String playerId){
        List<Wallet> walletList = walletService.getWalletDetails(playerId);
        return ResponseEntity.status(HttpStatus.OK).body(walletList);
    }

    @PatchMapping("/update-location")
    public ResponseEntity<ApiResponse> updateLocationStatus(@RequestParam String playerId, @RequestParam boolean locationStatus, @RequestHeader("Authorization") String authHeader){
        String response = usersManagementService.updateLocationStatus(playerId,locationStatus,authHeader);
        return ResponseEntity.status(HttpStatus.CREATED).body(new ApiResponse(response));
    }

    @PutMapping("/update-email")
    public ResponseEntity<ApiResponse> updateEmail (
            @RequestParam String playerId,
            @RequestParam String email,
            @RequestHeader("Authorization") String authHeader){
        String response = usersManagementService.updateEmail(playerId,email,authHeader);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(new ApiResponse(response));
    }

}