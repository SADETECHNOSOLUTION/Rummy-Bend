package com.sadetech.user_info.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


@Data
@Document(collection = "user_authentication")
@AllArgsConstructor
@NoArgsConstructor
public class User implements UserDetails {

    @Id
    private String playerId;
    private String name;
    @Indexed(unique = true)
    private String phoneNumber;
    @Indexed(unique = true)
    private String email;
    private String address;
    private String password;
    private Set<String> role = new HashSet<>();
    private double chips;
    private double inGameWallet;
    private double winningWallet;
    private double missionWallet;
    private double dailyMissionWallet;
    private boolean isWithDraw;
    private boolean isWalletRecharge;
    private String dateOfBirth;
    private String gender;
    private String language;
    private String imagePath;
    private String referrerId;
    private boolean isReferral;
    private int referralRank;
    private String referralStatus;
    private int loyaltyPoint;
    private double cashGameWallet; // For each money game you play, the money will be added here from the deposit balance just to calculate the loyalty point
    private double totalDepositMoney;
    private double totalWithdrawMoney;
    private boolean locationStatus = false;
    @CreatedDate
    private String idCreatedAt;

    @Override
    public String getPassword(){return password;}

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role == null ? List.of() :
                role.stream().map(SimpleGrantedAuthority::new).toList();
    }

    @Override
    public String getUsername() {
        // Always return phone number if present, otherwise email
        return (phoneNumber != null && !phoneNumber.isEmpty()) ? phoneNumber : email;
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
}

