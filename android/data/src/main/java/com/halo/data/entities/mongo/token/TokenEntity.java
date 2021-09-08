/*
 * Copyright 10/10/2018 Hahalolo. All rights reserved.
 *
 * https://help.hahalolo.com/commercial_terms/
 */

package com.halo.data.entities.mongo.token;

import android.os.Parcel;
import android.os.Parcelable;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.room.TypeConverters;

import com.google.common.base.Strings;
import com.google.gson.annotations.SerializedName;
import com.halo.constant.HaloConfig;

import java.util.UUID;

/**
 * @author ndn
 * Created by ndn
 * Created on 6/11/18.
 * <p>
 * Use {@link TypeConverters} convert long to Date
 */
public class TokenEntity implements Parcelable {

    @SerializedName("pn100")
    @NonNull
    public String userId = UUID.randomUUID().toString();

    @SerializedName("access_token")
    @NonNull
    public String accessToken = UUID.randomUUID().toString();

    @SerializedName("refresh_token")
    public String refreshToken;

    @SerializedName("nexttoken")
    public String nexttoken;

    @SerializedName("token_type")
    public String tokenType;

    @SerializedName("token_date")
    public String tokenDate;

    /*
     *  Expires in for token on server
     *  When token expires, re login with email or password
     */
    @SerializedName("expires_in")
    public long expiresIn;

    @SerializedName("scope")
    public String scope;

    @SerializedName("nv101")
    public String code;

    @SerializedName("nv102")
    public String username;

    @SerializedName("nv103")
    public String firstname;

    @SerializedName("nv104")
    public String lastname;

    @SerializedName("nv105")
    public String fullname;

    @SerializedName("nv107")
    public String gender;

    @SerializedName("nv108")
    public String mail;

    @SerializedName("nv111")
    public String countryCode;

    @SerializedName("nv120")
    public String avatar;

    @SerializedName("nn121")
    public int businessExist;

    @SerializedName("nn122")
    public int walletExist;

    @SerializedName("nn165")
    public int liked;

    @SerializedName("nd106")
    public long nd106;

    @SerializedName("dl148")
    public String dl148;

    @SerializedName("nsms")
    public int nsms = 0;

    @SerializedName("nn130")
    public int nn130 = 0;

    @SerializedName("nn132")
    public int nn132 = 0;

    @SerializedName("nn141")
    public int nn141 = 0;

    @SerializedName("lang")
    public String lang;

    @SerializedName("currency")
    public String currency;

    @SerializedName("ipAddress")
    public String ipAddress;

    @SerializedName("requestHeader")
    public String requestHeader;

    @SerializedName("deviceName")
    public String deviceName;

    @SerializedName("browserName")
    public String browserName;

    @SerializedName("clientOsName")
    public String clientOsName;

    @SerializedName("toolName")
    public String toolName;

    /**
     * --- Cập nhật phiên bản đăng ký v2. Nâng cấp phiên bản database 10->11 ---
     */

    // Số điện thoại mà người dùng nhập vào form
    @SerializedName("nv150")
    public String nv150;

    // Số điện thoại hoàn chỉnh sau khi verify thành công
    @SerializedName("nv152")
    public String nv152;

    // Mã quốc gia của số điện thoại cần xác thực
    @SerializedName("nv113")
    public String nv113;

    public TokenEntity() {
    }

    public void updateExpiresIn() {
        this.expiresIn = (this.expiresIn * 1000) + System.currentTimeMillis();
    }

    public String getUserId() {
        return Strings.nullToEmpty(userId);
    }

    public boolean isUnactive() {
        return TextUtils.equals(accessToken, HaloConfig.TOKEN_N_ACTIVE);
    }

    public boolean isActiveSignupV2() {
        return nn130 == 1;
    }

    public void updateTokenVerifyProfile(TokenEntity tokenEntity) {
        if (tokenEntity != null) {
            userId = tokenEntity.userId;
            nexttoken = tokenEntity.nexttoken;
            firstname = tokenEntity.firstname;
            lastname = tokenEntity.lastname;
            fullname = tokenEntity.fullname;
            gender = tokenEntity.gender;
            countryCode = tokenEntity.countryCode;
            nd106 = tokenEntity.nd106;
            nsms = tokenEntity.nsms;
            nn130 = tokenEntity.nn130;
            nn141 = tokenEntity.nn141;
            nv150 = tokenEntity.nv150;
            nv152 = tokenEntity.nv152;
            nv113 = tokenEntity.nv113;
        }
    }

    public void setUserId(@NonNull String userId) {
        this.userId = userId;
    }

    @NonNull
    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(@NonNull String accessToken) {
        this.accessToken = accessToken;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public String getNexttoken() {
        return nexttoken;
    }

    public void setNexttoken(String nexttoken) {
        this.nexttoken = nexttoken;
    }

    public String getTokenType() {
        return tokenType;
    }

    public void setTokenType(String tokenType) {
        this.tokenType = tokenType;
    }

    public String getTokenDate() {
        return tokenDate;
    }

    public void setTokenDate(String tokenDate) {
        this.tokenDate = tokenDate;
    }

    public long getExpiresIn() {
        return expiresIn;
    }

    public void setExpiresIn(long expiresIn) {
        this.expiresIn = expiresIn;
    }

    public String getScope() {
        return scope;
    }

    public void setScope(String scope) {
        this.scope = scope;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstname() {
        return Strings.nullToEmpty(firstname);
    }

    public void setFirstname(String firstname) {
        this.firstname = firstname;
    }

    public String getLastname() {
        return Strings.nullToEmpty(lastname);
    }

    public void setLastname(String lastname) {
        this.lastname = lastname;
    }

    public String getFullname() {
        return Strings.nullToEmpty(fullname);
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public int getBusinessExist() {
        return businessExist;
    }

    public void setBusinessExist(int businessExist) {
        this.businessExist = businessExist;
    }

    public int getWalletExist() {
        return walletExist;
    }

    public void setWalletExist(int walletExist) {
        this.walletExist = walletExist;
    }

    public int getLiked() {
        return liked;
    }

    public void setLiked(int liked) {
        this.liked = liked;
    }

    public long getNd106() {
        return nd106;
    }

    public void setNd106(long nd106) {
        this.nd106 = nd106;
    }

    public String getDl148() {
        return dl148;
    }

    public void setDl148(String dl148) {
        this.dl148 = dl148;
    }

    public int getNsms() {
        return nsms;
    }

    public void setNsms(int nsms) {
        this.nsms = nsms;
    }

    public int getNn130() {
        return nn130;
    }

    public void setNn130(int nn130) {
        this.nn130 = nn130;
    }

    public int getNn132() {
        return nn132;
    }

    public void setNn132(int nn132) {
        this.nn132 = nn132;
    }

    public int getNn141() {
        return nn141;
    }

    public void setNn141(int nn141) {
        this.nn141 = nn141;
    }

    public String getLang() {
        return lang;
    }

    public void setLang(String lang) {
        this.lang = lang;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getRequestHeader() {
        return requestHeader;
    }

    public void setRequestHeader(String requestHeader) {
        this.requestHeader = requestHeader;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getBrowserName() {
        return browserName;
    }

    public void setBrowserName(String browserName) {
        this.browserName = browserName;
    }

    public String getClientOsName() {
        return clientOsName;
    }

    public void setClientOsName(String clientOsName) {
        this.clientOsName = clientOsName;
    }

    public String getToolName() {
        return toolName;
    }

    public void setToolName(String toolName) {
        this.toolName = toolName;
    }

    public String getNv150() {
        return nv150;
    }

    public void setNv150(String nv150) {
        this.nv150 = nv150;
    }

    public String getNv152() {
        return nv152;
    }

    public void setNv152(String nv152) {
        this.nv152 = nv152;
    }

    public String getNv113() {
        return nv113;
    }

    public void setNv113(String nv113) {
        this.nv113 = nv113;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.userId);
        dest.writeString(this.accessToken);
        dest.writeString(this.refreshToken);
        dest.writeString(this.nexttoken);
        dest.writeString(this.tokenType);
        dest.writeString(this.tokenDate);
        dest.writeLong(this.expiresIn);
        dest.writeString(this.scope);
        dest.writeString(this.code);
        dest.writeString(this.username);
        dest.writeString(this.firstname);
        dest.writeString(this.lastname);
        dest.writeString(this.fullname);
        dest.writeString(this.gender);
        dest.writeString(this.mail);
        dest.writeString(this.countryCode);
        dest.writeString(this.avatar);
        dest.writeInt(this.businessExist);
        dest.writeInt(this.walletExist);
        dest.writeInt(this.liked);
        dest.writeLong(this.nd106);
        dest.writeString(this.dl148);
        dest.writeInt(this.nsms);
        dest.writeInt(this.nn130);
        dest.writeInt(this.nn132);
        dest.writeInt(this.nn141);
        dest.writeString(this.lang);
        dest.writeString(this.currency);
        dest.writeString(this.ipAddress);
        dest.writeString(this.requestHeader);
        dest.writeString(this.deviceName);
        dest.writeString(this.browserName);
        dest.writeString(this.clientOsName);
        dest.writeString(this.toolName);
    }

    protected TokenEntity(Parcel in) {
        this.userId = in.readString();
        this.accessToken = in.readString();
        this.refreshToken = in.readString();
        this.nexttoken = in.readString();
        this.tokenType = in.readString();
        this.tokenDate = in.readString();
        this.expiresIn = in.readLong();
        this.scope = in.readString();
        this.code = in.readString();
        this.username = in.readString();
        this.firstname = in.readString();
        this.lastname = in.readString();
        this.fullname = in.readString();
        this.gender = in.readString();
        this.mail = in.readString();
        this.countryCode = in.readString();
        this.avatar = in.readString();
        this.businessExist = in.readInt();
        this.walletExist = in.readInt();
        this.liked = in.readInt();
        this.nd106 = in.readLong();
        this.dl148 = in.readString();
        this.nsms = in.readInt();
        this.nn130 = in.readInt();
        this.nn132 = in.readInt();
        this.nn141 = in.readInt();
        this.lang = in.readString();
        this.currency = in.readString();
        this.ipAddress = in.readString();
        this.requestHeader = in.readString();
        this.deviceName = in.readString();
        this.browserName = in.readString();
        this.clientOsName = in.readString();
        this.toolName = in.readString();
    }

    public static final Creator<TokenEntity> CREATOR = new Creator<TokenEntity>() {
        @Override
        public TokenEntity createFromParcel(Parcel source) {
            return new TokenEntity(source);
        }

        @Override
        public TokenEntity[] newArray(int size) {
            return new TokenEntity[size];
        }
    };
}
