package com.project.urlshortener.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.time.Instant;
import java.util.Objects;

@Entity
@Table(
        name = "click_events",
        indexes = {
                @Index(name = "idx_click_events_clicked_at", columnList = "clicked_at"),
                @Index(name = "idx_click_events_short_url_id", columnList = "short_url_id"),
                @Index(name = "idx_click_events_created_by_id", columnList = "created_by_id"),
                @Index(name = "idx_click_events_country", columnList = "country"),
                @Index(name = "idx_click_events_browser", columnList = "browser"),
                @Index(name = "idx_click_events_device", columnList = "device")
        }
)
public class ClickEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "short_url_id", nullable = false)
    private ShortUrl shortUrl;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "created_by_id", nullable = false)
    private User createdBy;

    @Column(name = "ip_address", nullable = false, length = 64)
    private String ipAddress;

    @Column(name = "country", length = 100)
    private String country;

    @Column(name = "city", length = 100)
    private String city;

    @Column(name = "browser", length = 100)
    private String browser;

    @Column(name = "operating_system", length = 100)
    private String operatingSystem;

    @Column(name = "device", length = 100)
    private String device;

    @Column(name = "referral", length = 512)
    private String referral;

    @Column(name = "clicked_at", nullable = false)
    private Instant clickedAt;

    public ClickEvent() {
    }

    public ClickEvent(Long id, ShortUrl shortUrl, User createdBy, String ipAddress, String country, String city, String browser, String operatingSystem, String device, String referral, Instant clickedAt) {
        this.id = id;
        this.shortUrl = shortUrl;
        this.createdBy = createdBy;
        this.ipAddress = ipAddress;
        this.country = country;
        this.city = city;
        this.browser = browser;
        this.operatingSystem = operatingSystem;
        this.device = device;
        this.referral = referral;
        this.clickedAt = clickedAt;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ShortUrl getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(ShortUrl shortUrl) {
        this.shortUrl = shortUrl;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public void setIpAddress(String ipAddress) {
        this.ipAddress = ipAddress;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getBrowser() {
        return browser;
    }

    public void setBrowser(String browser) {
        this.browser = browser;
    }

    public String getOperatingSystem() {
        return operatingSystem;
    }

    public void setOperatingSystem(String operatingSystem) {
        this.operatingSystem = operatingSystem;
    }

    public String getDevice() {
        return device;
    }

    public void setDevice(String device) {
        this.device = device;
    }

    public String getReferral() {
        return referral;
    }

    public void setReferral(String referral) {
        this.referral = referral;
    }

    public Instant getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Instant clickedAt) {
        this.clickedAt = clickedAt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ClickEvent that = (ClickEvent) o;
        return Objects.equals(id, that.id)
                && Objects.equals(shortUrl, that.shortUrl)
                && Objects.equals(createdBy, that.createdBy)
                && Objects.equals(ipAddress, that.ipAddress)
                && Objects.equals(country, that.country)
                && Objects.equals(city, that.city)
                && Objects.equals(browser, that.browser)
                && Objects.equals(operatingSystem, that.operatingSystem)
                && Objects.equals(device, that.device)
                && Objects.equals(referral, that.referral)
                && Objects.equals(clickedAt, that.clickedAt);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, shortUrl, createdBy, ipAddress, country, city, browser, operatingSystem, device, referral, clickedAt);
    }

    @Override
    public String toString() {
        return "ClickEvent{" +
                "id=" + id +
                ", shortUrlId=" + (shortUrl != null ? shortUrl.getId() : null) +
                ", createdById=" + (createdBy != null ? createdBy.getId() : null) +
                ", ipAddress='" + ipAddress + '\'' +
                ", country='" + country + '\'' +
                ", city='" + city + '\'' +
                ", browser='" + browser + '\'' +
                ", operatingSystem='" + operatingSystem + '\'' +
                ", device='" + device + '\'' +
                ", referral='" + referral + '\'' +
                ", clickedAt=" + clickedAt +
                '}';
    }
}
