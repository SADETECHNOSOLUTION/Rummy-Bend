package com.sadetech.kyc_verification.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "kyc_verification")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Kyc {

    @Id
    private String id;
    private boolean kycStatus;
    private String panNumber;
    private String panImage;
    private String playerId;

}
